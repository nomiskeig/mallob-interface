import { JOBS_SUCCESSFULLY_CANCELED, JobTable } from './JobTable';
import {
	screen,
	render,
	fireEvent,
	within,
	waitFor,
} from '@testing-library/react';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { JobContext } from '../../context/JobContextProvider';
import { InfoContext, TYPE_INFO } from '../../context/InfoContextProvider';
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
		expect.objectContaining({ data: expect.objectContaining({
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

test('Can cancel jobs but only some are actually cancelled', () => {
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
    customRender(<JobTable jobs={fakeJobProvider.jobs}/>)
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
        expect( w)
    })


});
