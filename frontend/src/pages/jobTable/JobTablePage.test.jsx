import { JobTablePage } from './JobTablePage';
import { screen, render, fireEvent } from '@testing-library/react';
import { Routes, Route, BrowserRouter } from 'react-router-dom';
import { JobContext } from '../../context/JobContextProvider';
import { ROLE_ADMIN, UserContext } from '../../context/UserContextProvider';

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
beforeEach(() => {
	jest.resetAllMocks();
	fakeJobProvider = {
		jobs: [],
		fetchMostJobsPossible: jest.fn(),
		loadAllJobOfUser: jest.fn(),
        loadSingleJob: jest.fn(),
        getSingleJobInfo: jest.fn()
	};
	fakeUserProvider = {
		user: {},
	};
    fakeJobProvider.getSingleJobInfo.mockResolvedValueOnce({})
});

test('loads the jobs from the api on load', () => {
	customRender(<JobTablePage />);
	expect(screen.getByTestId('jobTable')).not.toBeNull();
	expect(fakeJobProvider.fetchMostJobsPossible).toHaveBeenCalledTimes(1);
});

test('Displays the embedded jobPage', () => {
	fakeJobProvider.jobs.push({
		config: { name: 'job1' },
		status: 'RUNNING',
		jobID: 1,
	});
	customRender(<JobTablePage />);
    let element = screen.getByText("1");
    expect(screen.queryByTestId('jobPage')).toBeNull();
    fireEvent.click(element);
    expect(screen.getByTestId('jobPage')).not.toBeNull();
    
});
