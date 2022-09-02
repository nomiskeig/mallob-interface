import { SubmitPage } from './SubmitPage';
import { within, prettyDOM } from '@testing-library/dom';

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
import { InfoContext } from '../../context/InfoContextProvider';
import axios from 'axios';
jest.mock('axios');

const mockedUseNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
	...jest.requireActual('react-router-dom'),
	useNavigate: () => mockedUseNavigate,
}));

const mockedFileSelector = jest.fn();
let mockPlainFiles = [];
let mockClear = jest.fn(() => (mockPlainFiles = []));
jest.mock('use-file-picker', () => ({
	...jest.requireActual('use-file-picker'),
	useFilePicker: () => [
		mockedFileSelector,
		{ plainFiles: mockPlainFiles, clear: mockClear },
	],
}));
jest.mock('form-data');

const fakeInfoProvider = {
	handleInformation: jest.fn(),
};
let fakeJobProvider;
let fakeUserProvider;
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

const jobName = 'job1';
const application = 'SAT';
const jobDescription = 'jobDescription';
const dependendencyID = 1;
const priority = 2.5;
const maxDemand = 10;
const wallclockLimit = '100s';
const cpuLimit = '200s';
const arrivalInput = '2022-09-02T14:47';
const arrival = '2022-09-02T12:47:00.000Z';
const precursor = 1;
const contentMode = 'raw';
const key1 = 'key1';
const key2 = 'key2';
const value1 = 'value1';
const value2 = 'value2';
beforeEach(() => {
	jest.resetAllMocks();

	fakeJobProvider = {
		jobs: [],
		jobToRestart: null,
		loadAllJobsOfUser: jest.fn(),
		setJobToRestart: jest.fn(),
	};
	fakeUserProvider = {
		user: {
			isVerified: true,
		},
	};
});
test('Can submit job without optional parameters', async () => {
	customRender(<SubmitPage />);
	fireEvent.click(screen.getByTestId('dropdownOption-' + application));
	fireEvent.change(screen.getByTestId('input-Name'), {
		target: { value: jobName },
	});
	fireEvent.change(screen.getByTestId('descriptionTextArea'), {
		target: { value: jobDescription },
	});
	axios.mockResolvedValueOnce({
		data: {
			jobID: 1,
		},
	});
	fireEvent.click(screen.getByRole('button', { name: 'Submit job' }));
	await waitFor(() => {
		expect(axios).toHaveBeenCalledTimes(1);
		const axiosCallParamters = axios.mock.calls[0][0];
		expect(axiosCallParamters).toEqual(
			expect.objectContaining({
				data: expect.objectContaining({
					name: jobName,
					application: application,
					description: expect.arrayContaining([jobDescription]),
				}),
			})
		);
	});
});

test('Can submit job with all required and optional parameters with inclusive description', async () => {
	fakeJobProvider.jobs.push({
		config: {
			name: 'dependendencyJob',
		},
		jobID: 1,
	});
	customRender(<SubmitPage />);
	fireEvent.change(screen.getByTestId('input-Name'), {
		target: { value: jobName },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-' + application));
	fireEvent.click(screen.getByTestId('dropdownOption-Priority'));
	fireEvent.change(screen.getByTestId('input-Priority'), {
		target: { value: priority },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Maximum Demand'));
	fireEvent.change(screen.getByTestId('input-Maximum Demand'), {
		target: { value: maxDemand },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Wallclock-Limit'));
	fireEvent.change(screen.getByTestId('input-Wallclock-Limit'), {
		target: { value: wallclockLimit },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-CPU-Limit'));
	fireEvent.change(screen.getByTestId('input-CPU-Limit'), {
		target: { value: cpuLimit },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Incremental'));
	fireEvent.click(screen.getByTestId('inputCheckbox-Incremental'));
	fireEvent.click(screen.getByTestId('dropdownOption-Precursor'));
	fireEvent.change(screen.getByTestId('input-Precursor'), {
		target: { value: precursor },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Arrival'));
	fireEvent.change(screen.getByTestId('input-Arrival'), {
		target: { value: arrivalInput },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Content-Mode'));
	fireEvent.click(screen.getByTestId('dropdownOption-' + contentMode));
	fireEvent.click(screen.getByTestId('dropdownOption-Key-Value-Parameter'));
	fireEvent.click(screen.getByTestId('dropdownOption-Key-Value-Parameter'));
	const additionalParam1 = screen.getByTestId('inputAdditionalParam-0');
	fireEvent.change(within(additionalParam1).getByTestId('input-Key'), {
		target: { value: key1 },
	});
	fireEvent.change(within(additionalParam1).getByTestId('input-Value'), {
		target: { value: value1 },
	});
	const additionalParam2 = screen.getByTestId('inputAdditionalParam-1');
	fireEvent.change(within(additionalParam2).getByTestId('input-Key'), {
		target: { value: key2 },
	});
	fireEvent.change(within(additionalParam2).getByTestId('input-Value'), {
		target: { value: value2 },
	});
	fireEvent.click(
		screen.getByTestId('dependencyTableCheckbox-' + dependendencyID)
	);
	fireEvent.change(screen.getByTestId('descriptionTextArea'), {
		target: { value: jobDescription },
	});
	axios.mockResolvedValueOnce({
		data: {
			jobID: 2,
		},
	});
	fireEvent.click(screen.getByRole('button', { name: 'Submit job' }));
	await waitFor(() => {
		expect(axios).toHaveBeenCalledTimes(1);
		const axiosCallParameters = axios.mock.calls[0][0];
		expect(axiosCallParameters).toEqual(
			expect.objectContaining({
				data: expect.objectContaining({
					name: jobName,
					application: application,
					description: expect.arrayContaining([jobDescription]),
					dependencies: expect.arrayContaining([dependendencyID]),
					priority: priority,
					maxDemand: maxDemand,
					wallclockLimit: wallclockLimit,
					cpuLimit: cpuLimit,
					precursor: precursor,
					contentMode: contentMode,
					incremental: true,
					arrival: arrival,
					additionalConfig: expect.objectContaining({
						key1: value1,
						key2: value2,
					}),
				}),
			})
		);
	});
});

test('Can submit job with all required and optional parameters with exclusive description', async () => {
    mockPlainFiles = [new File(['some other file data'], 'testFile2.cnf')]
	fakeJobProvider.jobs.push({
		config: {
			name: 'dependendencyJob',
		},
		jobID: 1,
	});
	customRender(<SubmitPage />);
	fireEvent.change(screen.getByTestId('input-Name'), {
		target: { value: jobName },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-' + application));
	fireEvent.click(screen.getByTestId('dropdownOption-Priority'));
	fireEvent.change(screen.getByTestId('input-Priority'), {
		target: { value: priority },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Maximum Demand'));
	fireEvent.change(screen.getByTestId('input-Maximum Demand'), {
		target: { value: maxDemand },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Wallclock-Limit'));
	fireEvent.change(screen.getByTestId('input-Wallclock-Limit'), {
		target: { value: wallclockLimit },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-CPU-Limit'));
	fireEvent.change(screen.getByTestId('input-CPU-Limit'), {
		target: { value: cpuLimit },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Incremental'));
	fireEvent.click(screen.getByTestId('inputCheckbox-Incremental'));
	fireEvent.click(screen.getByTestId('dropdownOption-Precursor'));
	fireEvent.change(screen.getByTestId('input-Precursor'), {
		target: { value: precursor },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Arrival'));
	fireEvent.change(screen.getByTestId('input-Arrival'), {
		target: { value: arrivalInput },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-Content-Mode'));
	fireEvent.click(screen.getByTestId('dropdownOption-' + contentMode));
	fireEvent.click(screen.getByTestId('dropdownOption-Key-Value-Parameter'));
	fireEvent.click(screen.getByTestId('dropdownOption-Key-Value-Parameter'));
	const additionalParam1 = screen.getByTestId('inputAdditionalParam-0');
	fireEvent.change(within(additionalParam1).getByTestId('input-Key'), {
		target: { value: key1 },
	});
	fireEvent.change(within(additionalParam1).getByTestId('input-Value'), {
		target: { value: value1 },
	});
	const additionalParam2 = screen.getByTestId('inputAdditionalParam-1');
	fireEvent.change(within(additionalParam2).getByTestId('input-Key'), {
		target: { value: key2 },
	});
	fireEvent.change(within(additionalParam2).getByTestId('input-Value'), {
		target: { value: value2 },
	});
	fireEvent.click(
		screen.getByTestId('dependencyTableCheckbox-' + dependendencyID)
	);
	fireEvent.click(screen.getByTestId('dropdownOption-File'));
	await waitFor(() =>
		fireEvent.click(screen.getByTestId('fileDescriptionTestButton'))
	);
	const descriptionID = 1;

	axios.mockImplementation(({ url }) => {
		if (url.endsWith('description')) {
			return Promise.resolve({
				data: {
					descriptionID: descriptionID,
				},
			});
		}
		if (url.endsWith('config')) {
			return Promise.resolve({
				data: {
					jobID: 2,
				},
			});
		}
	});

	fireEvent.click(screen.getByRole('button', { name: 'Submit job' }));
	await waitFor(() => {
		expect(axios).toHaveBeenCalledTimes(2);
		const axiosCallParameters = axios.mock.calls[1][0];
		expect(axiosCallParameters).toEqual(
			expect.objectContaining({
				data: expect.objectContaining({
					name: jobName,
					application: application,
					descriptionID: 1,
					dependencies: expect.arrayContaining([dependendencyID]),
					priority: priority,
					maxDemand: maxDemand,
					wallclockLimit: wallclockLimit,
					cpuLimit: cpuLimit,
					precursor: precursor,
					contentMode: contentMode,
					incremental: true,
					arrival: arrival,
					additionalConfig: expect.objectContaining({
						key1: value1,
						key2: value2,
					}),
				}),
			})
		);
	});
});
test('Displays config when job is restarted', async () => {
	fakeJobProvider.jobs.push({
		config: {
			name: jobName,
			application: application,
			descriptionID: 1,
			dependencies: [dependendencyID],
			priority: priority,
			maxDemand: maxDemand,
			wallclockLimit: wallclockLimit,
			cpuLimit: cpuLimit,
			precursor: precursor,
			contentMode: contentMode,
			incremental: true,
			arrival: arrival,
			additionalConfig: {
				key1: value1,
				key2: value2,
			},
		},
		jobID: 1,
	});
	fakeJobProvider.jobToRestart = 1;
	await waitFor(() => {
		customRender(<SubmitPage />);
	});
	expect(screen.getByTestId('input-Name').value).toBe(jobName);
	let applicationDropdownButton = screen.getByTestId(
		'dropdownSelected-Application'
	);
	let applicationDropdownButtonText =
		applicationDropdownButton.textContent ||
		applicationDropdownButton.innerText;
	expect(applicationDropdownButtonText).toBe(application);
	expect(screen.getByTestId('input-Priority').value).toBe('' + priority);
	expect(screen.getByTestId('input-Maximum Demand').value).toBe('' + maxDemand);
	expect(screen.getByTestId('input-Wallclock-Limit').value).toBe(
		wallclockLimit
	);
	expect(screen.getByTestId('input-CPU-Limit').value).toBe(cpuLimit);
	expect(screen.getByTestId('input-Precursor').value).toBe('' + precursor);
	let contentModeButton = screen.getByTestId('dropdownSelected-Content-Mode');
	let contentModeButtonText =
		contentModeButton.textContent || contentModeButton.innerText;
	expect(contentModeButtonText).toBe(contentMode);
	const additionalParam1 = screen.getByTestId('inputAdditionalParam-0');
	expect(within(additionalParam1).getByTestId('input-Key').value).toBe('key1');
	expect(within(additionalParam1).getByTestId('input-Value').value).toBe(
		'value1'
	);
	const additionalParam2 = screen.getByTestId('inputAdditionalParam-1');
	expect(within(additionalParam2).getByTestId('input-Key').value).toBe('key2');
	expect(within(additionalParam2).getByTestId('input-Value').value).toBe(
		'value2'
	);
	expect(screen.getByTestId('input-Arrival').value).toBe(arrivalInput);
	expect(screen.getByTestId('inputCheckbox-Incremental').checked).toBeTruthy();
	expect(screen.getByTestId('dependencyTableCheckbox-1').checked).toBeTruthy();
});

test('Displays warning if description is missing inclusive', () => {

	customRender(<SubmitPage />);
	fireEvent.change(screen.getByTestId('input-Name'), {
		target: { value: jobName },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-' + application));
    fireEvent.click(screen.getByRole('button', {name: 'Submit job'}));
    expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
});

test('Displays warning if description is missing exclusive', () =>{
    mockPlainFiles = [];
	customRender(<SubmitPage />);
	fireEvent.change(screen.getByTestId('input-Name'), {
		target: { value: jobName },
	});
	fireEvent.click(screen.getByTestId('dropdownOption-' + application));
    fireEvent.click(screen.getByTestId('dropdownOption-File'));
    fireEvent.click(screen.getByRole('button', {name: 'Submit job'}));
    expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);

})

test('Displays warning if name is missing', () =>{
	customRender(<SubmitPage />);
	fireEvent.click(screen.getByTestId('dropdownOption-' + application));
	fireEvent.change(screen.getByTestId('descriptionTextArea'), {
		target: { value: jobDescription },
	});
    fireEvent.click(screen.getByRole('button', {name: 'Submit job'}));
	const baseDiv = screen.getByTestId('baseDiv');
    
    expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);
})

test('Unverified user can not access the submit page', ()=>{
    fakeUserProvider = {
        user: {
            username: 'username',
            isVerified: false,
        }
    }
    customRender(<SubmitPage />);
    expect(mockedUseNavigate).toHaveBeenCalledTimes(1);
    expect(mockedUseNavigate).toHaveBeenCalledWith('/jobs');
    expect(fakeInfoProvider.handleInformation).toHaveBeenCalledTimes(1);

})
