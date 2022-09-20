import { AppError } from '../../../context/AppError';
import { JobStorage } from './JobStorage';
import { Event } from '../controller/Event';
import { JobUpdateListener } from './JobUpdateListener';
import { GlobalStats } from './GlobalStats';
import { Job } from './Job';
import { waitFor } from '@testing-library/react';
// this file is a workaroundk for a limition of jest, where the mocked imports cannot be reset for a single test in the same file...
jest.mock('./JobUpdateListener.jsx');
jest.mock('./GlobalStats.jsx')
const amountProcesses = 5;
export const mockContext = {
	settingsContext: {
		settings: {
			amountProcesses: amountProcesses,
		},
	},
	jobContext: {
		getSingleJobInfo: function (jobID) {
			return new Promise(function (resolve, reject) {
				reject(null);
			});
		},
	},
	userContext: {
		user: {
			username: 'user',
		},
	},
};

const testJobName = 'testJobName';
const testUsername = 'testUsername';
const testUserEmail = 'testUserEmail';
const testSingleInformation = {
	config: {
		name: testJobName,
	},
	user: testUsername,
	email: testUserEmail,
};
beforeEach(() => {
	JobUpdateListener.mockClear();
    GlobalStats.mockClear();
});
afterEach(() => {
	jest.resetAllMocks();
    
});

test('removes a single job correctly', () => {
	let jobStorage = new JobStorage(mockContext);
	let event = new Event('a time', 3, 2, 1, 1);
	jobStorage.addEvents([event]);
	expect(jobStorage.getAllJobs().length).toBe(1);
	let event2 = new Event('a time', 3, 2, 1, 0);
	jobStorage.addEvents([event2]);
	expect(jobStorage.getAllJobs().length).toBe(0);
});



test('getjob returns a created job', () => {
	let jobStorage = new JobStorage(mockContext);
	let event = new Event('a time', 4, 3, 2, 1);
	jobStorage.addEvents([event]);
	expect(jobStorage.getJob(2)).not.toBeNull();
});

test('get job returns null if there is not job with that id', () => {
	let jobStorage = new JobStorage(mockContext);
	expect(jobStorage.getJob(0)).toBeNull();
});

test('informs the listeners about a removed vertex', () => {
	let jobStorage = new JobStorage(mockContext);
	let listener = new JobUpdateListener();
	let event1 = new Event('a time', 4, 3, 2, 1);
	let event2 = new Event('a time', 4, 3, 2, 0);
	jobStorage.addJobUpdateListener(listener);
    jobStorage.addEvents([event1]);
    expect(listener.update).not.toHaveBeenCalled();
    jobStorage.addEvents([event2]);
    expect(listener.update).toHaveBeenCalledTimes(1);

});
