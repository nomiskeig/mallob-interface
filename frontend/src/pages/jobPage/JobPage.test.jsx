import { JobPage } from './JobPage';
import '@testing-library/jest-dom';
import { within } from '@testing-library/dom';
import React from 'react';
import {
	render,
	screen,
	act,
	waitFor,
	fireEvent,
} from '@testing-library/react';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { JobContext } from '../../context/JobContextProvider';
import { ROLE_ADMIN, UserContext } from '../../context/UserContextProvider';
import axios from 'axios';
jest.mock('axios');

let fakeJobProvider;
let fakeUserProvider;
const customRender = (ui, jobProvider, userProvider) => {
	return render(
		<UserContext.Provider
			value={userProvider ? userProvider : fakeUserProvider}
		>
			<JobContext.Provider value={jobProvider ? jobProvider : fakeJobProvider}>
				<BrowserRouter>
					<Routes>
						<Route path='/' element={ui} />
					</Routes>
				</BrowserRouter>
			</JobContext.Provider>
		</UserContext.Provider>
	);
};
global.URL.createObjectURL = jest.fn();

beforeEach(() => {
	jest.resetAllMocks();
	fakeJobProvider = {
		jobs: [
			{
				config: {
					name: 'job3',
				},
				jobID: 3,
			},
			{
				config: {
					name: 'job2',
					dependencies: [3],
				},
				jobID: 2,
			},
		],
		loadSingleJob: jest.fn(),
		cancelJob: jest.fn(),
	};
	fakeUserProvider = {
		user: {
			username: 'user1',
		},
	};
});
test('Displays the name of the job', () => {
	customRender(<JobPage jobID={3} />);
	expect(screen.getByText(/^job3/)).not.toBeNull();
});

test('Displays the name of the job if embedded', () =>{ 
    customRender(<JobPage embedded={true} jobID={3}/>);
    expect(screen.getByText(/^job3/)).not.toBeNull();

})

test('Tries to load dependendencies and display dependencies', () => {
	customRender(<JobPage jobID={2} />);
	expect(fakeJobProvider.loadSingleJob).toHaveBeenCalledTimes(2);
	expect(fakeJobProvider.loadSingleJob.mock.calls).toEqual([[2], [3]]);
	expect(screen.getByText(/^job3/)).not.toBeNull();
});

test('Does not crash if there is no job with the given jobID', () => {
	customRender(<JobPage />);
	expect(screen.getByText(/^dependencies/i)).not.toBeNull();
});

test('Displays the priority', () => {
	let privateJobProvider = { ...fakeJobProvider };
	privateJobProvider.jobs.push({
		jobID: 1,
		config: { name: 'Job1', priority: 1 },
	});
	customRender(<JobPage jobID={1} />, privateJobProvider);
	const element = screen.getByTestId('inputWithLabel-priority');
	expect(within(element).getByText(/^Priority/)).not.toBeNull();
	const input = within(element).getByRole('textbox');
	expect(input.value).toBe('1');
});

test('Displays additional config', () => {
	let privateJobProvider = { ...fakeJobProvider };
	privateJobProvider.jobs.push({
		jobID: 1,
		config: {
			name: 'job 2',
			additionalConfig: {
				color: 'red',
			},
		},
	});
	customRender(<JobPage jobID={1} />, privateJobProvider);
	const element = screen.getByTestId('inputWithLabel-color');
	expect(within(element).getByText(/^color/)).not.toBeNull();
	const input = within(element).getByRole('textbox');
	expect(input.value).toBe('red');
});

test('Displays the cancelled state', () => {
	let privateJobProvider = { ...fakeJobProvider };
	privateJobProvider.jobs.push({
		jobID: 1,
		status: 'CANCELLED',
		config: {},
	});
	customRender(<JobPage jobID={1} />);
	expect(screen.getByText('cancelled')).not.toBeNull();
});
test('Displays the cancelled state', () => {
	let privateJobProvider = { ...fakeJobProvider };
	privateJobProvider.jobs.push({
		jobID: 1,
		status: 'DONE',
		config: {},
	});
	customRender(<JobPage jobID={1} />);
	expect(screen.getByText('done')).not.toBeNull();
});
test('Displays the running state', () => {
	let privateJobProvider = { ...fakeJobProvider };
	privateJobProvider.jobs.push({
		jobID: 1,
		status: 'RUNNING',
		config: {},
	});
	customRender(<JobPage jobID={1} />);
	expect(screen.getByText('in progress')).not.toBeNull();
});

test('Displays the button to download the description', async () => {
	fakeJobProvider.jobs.push({
		jobID: 1,
		user: 'user1',
		config: {},
		status: 'DONE',
	});
	axios.mockResolvedValue({
		headers: {
			'content-type': 'application/zip',
		},
		data: 'TestData',
	});
	await waitFor(() => {
		customRender(<JobPage jobID={1} />);
	});
	await waitFor(() =>
		expect(screen.getByText('Download description')).not.toBeNull()
	);
});

test('Does not display the button to download the description if job admin is logged in and viewing job of other user.', async () => {
	fakeJobProvider.jobs.push({
		jobID: 1,
		user: 'user1',
		config: {},
		status: 'DONE',
	});
	fakeUserProvider.user = {
		username: 'admin',
		role: ROLE_ADMIN,
	};

	axios.mockResolvedValue({
		headers: {
			'content-type': 'application/zip',
		},
		data: 'testData',
	});
	global.URL.createObjectURL = jest.fn();

	await waitFor(() => {
		customRender(<JobPage jobID={1} />);
	});
	await waitFor(() =>
		expect(screen.queryByText('Download description')).toBeNull()
	);
});

test('Displays the input field wiht the description', async () => {
	fakeJobProvider.jobs.push({
		jobID: 1,
		config: {},
		user: 'user1',
	});
	axios.mockResolvedValue({
		headers: {
			'content-type': 'application/json',
		},
		data: new Blob([
			JSON.stringify({ description: ['testData', 'testData2'] }),
		]),
	});
	await waitFor(() => {
		customRender(<JobPage jobID={1} />);
	});
	await waitFor(() => {
		const element = screen.getByTestId('textFieldDescription');
		expect(within(element).queryAllByRole('button')).toHaveLength(2);
		expect(within(element).getByText('testData')).not.toBeNull();
		expect(within(element).queryByText('testData2')).toBeNull();
		const button = within(element).getByRole('button', { name: '2' });
		fireEvent.click(button);
		expect(within(element).getByText('testData2')).not.toBeNull();
		expect(within(element).queryByText('testData')).toBeNull();
	});
});

test('Displays the cancel button and can cancel jobs if user who owns the job is logged in', () => {
	fakeJobProvider.jobs.push({
		jobID: 1,
		config: {},
		user: 'user1',
		status: 'RUNNING',
	});
	axios.mockRejectedValue(new Error('test error'));
	customRender(<JobPage jobID={1} />);
	const button = screen.getByRole('button', { name: 'Cancel job' });
	expect(button).not.toBeNull();
	fireEvent.click(button);
	expect(fakeJobProvider.cancelJob).toHaveBeenCalledTimes(1);
});

test('Displays the cancel button if admin is logged in and job is not the own', () => {
	fakeJobProvider.jobs.push({
		jobID: 1,
		config: {},
		status: 'RUNNING',
	});
	customRender(<JobPage jobID={1} />);
	expect(screen.getByRole('button', { name: 'Cancel job' })).not.toBeNull();
});

test('Displays the download button if user who owns the job is logged in and can download result', () => {
	fakeJobProvider.jobs.push({
		jobID: 1,
		config: {},
		user: 'user1',
		status: 'DONE',
	});
	axios.mockRejectedValueOnce(new Error('test error'));
	customRender(<JobPage jobID={1} />);
	const button = screen.getByRole('button', { name: 'Download result' });
	expect(button).not.toBeNull();
    expect(axios).toHaveBeenCalledTimes(1);
	axios.mockResolvedValueOnce({
		headers: {
			'content-type': 'application/json',
		},
		data: new Blob([
			JSON.stringify({ result: 'result' }),
		]),
	});
	fireEvent.click(button);
    expect(axios).toHaveBeenCalledTimes(2);
});
