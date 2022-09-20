import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import '@testing-library/jest-dom';
import { JobContextProvider } from './JobContextProvider';
import { JobContext } from './JobContextProvider';
import { InfoContext } from './InfoContextProvider';
import axios from 'axios';
import { ROLE_ADMIN, ROLE_USER, UserContext } from './UserContextProvider';
jest.mock('axios');
jest.mock('./UserContextProvider.jsx');

let fakeUseProvider = {
	user: {
		token: 'userToken',
		role: ROLE_ADMIN,
	},
};
let callback = jest.fn();
let jobToCancelID = 1;

let fakeInfoContext = {
	handleInformation: jest.fn(),
};
function DummyDisplay(props) {
	return (
		<InfoContext.Provider value={fakeInfoContext}>
			<UserContext.Provider value={fakeUseProvider}>
				<JobContextProvider>
					<JobContext.Consumer>
						{({
							jobs,
							fetchMostJobsPossible,
							loadSingleJob,
							getSingleJobInfo,
							loadAllJobsOfUser,
							cancelJob,
						}) => (
							<div>
								<button onClick={() => fetchMostJobsPossible(false, callback)}>
									FetchMostJobs
								</button>
								<button onClick={() => loadAllJobsOfUser()}>
									LoadJobsOfUser
								</button>
								<button onClick={() => cancelJob(jobToCancelID)}>
									CancelJob
								</button>
								<button onClick={() => getSingleJobInfo(jobToCancelID)}>
									GetSingleJobInfo
								</button>
                                        <button onClick={()=> loadSingleJob(jobToCancelID)}>LoadSingleJob</button>
								{jobs.map((item, index) => (
									<div key={index} data-testid={'dummyDisplay'}>
										{item.name}
									</div>
								))}
							</div>
						)}
					</JobContext.Consumer>
				</JobContextProvider>
			</UserContext.Provider>
		</InfoContext.Provider>
	);
}
beforeEach(() => {
	jest.resetAllMocks();
});

test('Context should have a default value', () => {
	render(<DummyDisplay />);
	const dummyDisplay = screen.queryByTestId('dummyDisplay');
	expect(dummyDisplay).not.toBeInTheDocument();
});
test('Context can fetch most jobs and call callback', async () => {
	render(<DummyDisplay />);
	const fakeJobs = [
		{
			name: 'testJob',
		},
	];

	axios.mockResolvedValue({ data: { information: fakeJobs } });
    await waitFor(() => {

	fireEvent.click(screen.getByRole('button', { name: 'FetchMostJobs' }));
    })
	await waitFor(() => {
		expect(screen.getByText(/testJob/)).not.toBeNull();
		expect(callback).toHaveBeenCalledTimes(1);
	});
});
test('Loads the jobs of the user', async () => {
	render(<DummyDisplay />);
	const fakeJobs = [
		{
			name: 'testJob',
		},
	];

	axios.mockResolvedValue({ data: { information: fakeJobs } });
	act(() => {
		fireEvent.click(screen.getByRole('button', { name: 'LoadJobsOfUser' }));
	});
    await waitFor(() =>{
		expect(screen.getByText(/testJob/)).not.toBeNull();
		expect(callback).toHaveBeenCalledTimes(0);
    }) 
});

test('Cancels a job', async () => {
	render(<DummyDisplay />);
	axios.mockResolvedValueOnce({}).mockResolvedValueOnce({});
	await waitFor(() => {
		fireEvent.click(screen.getByRole('button', { name: 'CancelJob' }));
	});
	await waitFor(() => {
		expect(axios).toHaveBeenCalledTimes(2);
		const axiosCallParameters = axios.mock.calls[0][0];
		expect(axiosCallParameters).toEqual(
			expect.objectContaining({
				url:
					process.env.REACT_APP_API_BASE_PATH +
					'/api/v1/jobs/cancel/single/' +
					jobToCancelID,
			})
		);
	});
});

test('Gets promise with single job info', async () => {
	render(<DummyDisplay />);
	axios.mockResolvedValueOnce({ data: { name: 'job1', jobID: 1 } });
	fireEvent.click(screen.getByRole('button', { name: 'GetSingleJobInfo' }));
	await waitFor(() => {
		expect(axios).toHaveBeenCalledTimes(1);
		expect(screen.getByText(/job1/)).not.toBeNull();
	});
});

test('returnes stored job info', async () => {
	render(<DummyDisplay />);
	axios.mockResolvedValueOnce({ data: { name: 'job1', jobID: 1 } });
	fireEvent.click(screen.getByRole('button', { name: 'GetSingleJobInfo' }));
    await waitFor(() => {
        expect(screen.getByText(/job1/)).not.toBeNull();
        expect(screen.queryByText(/job2/)).toBeNull();
    })
    axios.mockResolvedValueOnce({data: {name: 'job2', jobID: 1}});
	fireEvent.click(screen.getByRole('button', { name: 'GetSingleJobInfo' }));
    expect(axios).toHaveBeenCalledTimes(1);

});

test("loadsinglejob replaceses existing job", async () =>{
	render(<DummyDisplay />);
	axios.mockResolvedValueOnce({ data: { name: 'job1', jobID: 1 } });
	fireEvent.click(screen.getByRole('button', { name: 'LoadSingleJob' }));
    await waitFor(() => {
        expect(screen.getByText(/job1/)).not.toBeNull();
        expect(screen.queryByText(/job2/)).toBeNull();
    })
    axios.mockResolvedValueOnce({data: {name: 'job2', jobID: 1}});
	fireEvent.click(screen.getByRole('button', { name: 'LoadSingleJob' }));
    await waitFor(() => {
        expect(screen.getByText(/job2/)).not.toBeNull();
        expect(screen.queryByText(/job1/)).toBeNull();
    })
})
