import {
	JOBS_PARTLY_CANCELED_BEGIN,
	JOBS_SUCCESSFULLY_CANCELED,
	JobTable,
} from './JobTable';
import {
	screen,
	render,
	fireEvent,
	within,
	waitFor,
} from '@testing-library/react';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { JobContext } from '../../context/JobContextProvider';
import {
	InfoContext,
	TYPE_INFO,
	TYPE_WARNING,
} from '../../context/InfoContextProvider';
import {
	ROLE_ADMIN,
	ROLE_USER,
	UserContext,
} from '../../context/UserContextProvider';
import axios from 'axios';
jest.mock('axios');

let fakeJobProvider;
let fakeUserProvider;
let fakeInfoProvider = {
	handleInformation: jest.fn(),
};

const customRender = (ui, jobProvider, userProvider) => {
	return render(
		<InfoContext.Provider value={fakeInfoProvider}>
			<UserContext.Provider
				value={userProvider ? userProvider : fakeUserProvider}
			>
				<JobContext.Provider
					value={jobProvider ? jobProvider : fakeJobProvider}
				>
					<BrowserRouter>
						<Routes>
							<Route path='/' element={ui} />
						</Routes>
					</BrowserRouter>
				</JobContext.Provider>
			</UserContext.Provider>
		</InfoContext.Provider>
	);
};
beforeEach(() => {
	jest.resetAllMocks();
	fakeJobProvider = {
		jobs: [],
		fetchMostJobsPossible: jest.fn(),
		loadAllJobOfUser: jest.fn(),
		loadSingleJob: jest.fn(),
	};
	fakeUserProvider = {
		user: {
			role: ROLE_USER,
			token: 'token',
		},
	};
	fakeInfoProvider = {
		handleInformation: jest.fn(),
	};
});

test('Displays the name and the id of the job by default', () => {
	fakeJobProvider.jobs.push({
		config: { name: 'job1' },
		jobID: 1,
		user: 'user1',
	});
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	expect(screen.getByText('1')).not.toBeNull();
	expect(screen.getByText('job1')).not.toBeNull();
});

test('Can display the priority of a job', () => {
	fakeJobProvider.jobs.push({
		config: { name: 'job1', priority: 2 },
		jobID: 1,
		user: 'user1',
	});
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	let element = screen.getByText('Priority');
	expect(screen.queryByText('2')).toBeNull();
	fireEvent.click(element);
	expect(screen.getByText('2')).not.toBeNull();
});

test('Can filter jobs', () => {
	fakeJobProvider.jobs = [
		{
			config: { name: 'job1' },
			jobID: 1,
			user: 'user1',
		},
		{
			config: { name: 'job2' },
			jobID: 2,
			user: 'admin',
		},
	];
	fakeUserProvider = {
		user: { username: 'admin', role: ROLE_ADMIN },
	};
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	expect(screen.getAllByText(/^job[1,2]$/)).toHaveLength(2);
	const filterElement = screen.getByTestId('adminFilter');
	const checkbox = within(filterElement).getByRole('checkbox');
	fireEvent.click(checkbox);
	expect(screen.getAllByText(/^job[1,2]$/)).toHaveLength(1);
	expect(screen.getByText('job2')).not.toBeNull();
});

test('Can cancel jobs sucessfully', async () => {
	fakeJobProvider.jobs = [
		{
			config: { name: 'job1' },
			jobID: 1,
		},
		{
			config: { name: 'job2' },
			jobID: 2,
		},
	];
	axios.mockResolvedValueOnce({
		data: {
			cancelled: [1, 2],
		},
	});
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	expect(screen.getAllByText(/^job[1,2]$/)).toHaveLength(2);
	const checkboxJob1 = screen.getByTestId('rowCheckbox-1');
	fireEvent.click(checkboxJob1);
	const checkboxJob2 = screen.getByTestId('rowCheckbox-2');
	fireEvent.click(checkboxJob2);
	const cancelButton = screen.getByTestId(
		'dropdownOption-Cancel selected jobs'
	);
	fireEvent.click(cancelButton);
	expect(axios).toHaveBeenCalledTimes(1);
	const axiosCallParameters = axios.mock.calls[0][0];

	expect(axiosCallParameters).toEqual(
		expect.objectContaining({
			data: expect.objectContaining({
				jobs: expect.arrayContaining([1, 2]),
			}),
		})
	);
	await waitFor(() => {
		expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
		expect(fakeInfoProvider.handleInformation).toHaveBeenCalledWith(
			JOBS_SUCCESSFULLY_CANCELED,
			TYPE_INFO
		);
	});
});

test('Can cancel jobs but only some are actually cancelled', async () => {
	fakeJobProvider.jobs = [
		{
			config: { name: 'job1' },
			jobID: 1,
		},
		{
			config: { name: 'job2' },
			jobID: 2,
		},
	];
	axios.mockResolvedValueOnce({
		data: {
			cancelled: [1],
		},
	});
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	expect(screen.getAllByText(/^job[1,2]$/)).toHaveLength(2);
	const checkboxJob1 = screen.getByTestId('rowCheckbox-1');
	fireEvent.click(checkboxJob1);
	const checkboxJob2 = screen.getByTestId('rowCheckbox-2');
	fireEvent.click(checkboxJob2);
	const cancelButton = screen.getByTestId(
		'dropdownOption-Cancel selected jobs'
	);
	fireEvent.click(cancelButton);
	expect(axios).toHaveBeenCalledTimes(1);
	await waitFor(() => {
		expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
		expect(fakeInfoProvider.handleInformation).toHaveBeenCalledWith(
			JOBS_PARTLY_CANCELED_BEGIN + '2',
			TYPE_WARNING
		);
	});
});

test('Unselecting a job works and buttons are disabled if no job is selected', () => {
	fakeJobProvider.jobs.push({ config: { name: 'job1' }, jobID: 1 });
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	expect(screen.getAllByText('job1')).toHaveLength(1);
	const cancelButton = screen.getByTestId(
		'dropdownOption-Cancel selected jobs'
	);
	const downloadButton = screen.getByTestId(
		'dropdownOption-Download results of selected jobs'
	);
	expect(cancelButton).toBeDisabled();
	expect(downloadButton).toBeDisabled();
	const checkboxJob1 = screen.getByTestId('rowCheckbox-1');
	fireEvent.click(checkboxJob1);
	expect(cancelButton).not.toBeDisabled();
	expect(downloadButton).not.toBeDisabled();
	fireEvent.click(checkboxJob1);
	expect(cancelButton).toBeDisabled();
	expect(downloadButton).toBeDisabled();
});

test('Column can be removed again', async () => {
	fakeJobProvider.jobs.push({
		config: {
			name: 'job1',
		},
		jobID: 1,
	});

	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	fireEvent.click(screen.getByTestId('dropdownOption-Priority'));
	await waitFor(() => {
		const removeButton = screen.getByTestId('remove-priority');
		expect(removeButton).not.toBeNull();
		fireEvent.click(removeButton);
        const nextRemoveButton = screen.queryByTestId('remove-priority');
		expect(nextRemoveButton).toBeNull();
	});
});


test('Can download results', async () =>{
	fakeJobProvider.jobs = [
		{
			config: { name: 'job1' },
			jobID: 1,
		},
		{
			config: { name: 'job2' },
			jobID: 2,
		},
	];
	axios.mockResolvedValueOnce({
		data: new Blob([JSON.stringify('data')])
	});
    global.URL.createObjectURL = jest.fn();
	customRender(<JobTable jobs={fakeJobProvider.jobs} />);
	expect(screen.getAllByText(/^job[1,2]$/)).toHaveLength(2);
	const checkboxJob1 = screen.getByTestId('rowCheckbox-1');
	fireEvent.click(checkboxJob1);
	const checkboxJob2 = screen.getByTestId('rowCheckbox-2');
	fireEvent.click(checkboxJob2);
	const downloadButton = screen.getByTestId(
		'dropdownOption-Download results of selected jobs'
	);
	fireEvent.click(downloadButton);
	expect(axios).toHaveBeenCalledTimes(1);
	const axiosCallParameters = axios.mock.calls[0][0];
	expect(axiosCallParameters).toEqual(
		expect.objectContaining({
			data: expect.objectContaining({
				jobs: expect.arrayContaining([1, 2]),
			}),
		})
	);
});

