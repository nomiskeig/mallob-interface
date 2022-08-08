import { AppError } from '../../../context/AppError';
import { JobStorage } from './JobStorage';
import { Event } from '../controller/Event';
import { JobUpdateListener } from './JobUpdateListener';
import { GlobalStats } from './GlobalStats';
import { Job } from './Job';
import { waitFor } from '@testing-library/react';
jest.mock('./JobUpdateListener');
jest.mock('./GlobalStats.jsx');
jest.mock('./Job.jsx');
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

beforeEach(() => {
	JobUpdateListener.mockClear();
	GlobalStats.mockClear();
	Job.mockClear();
});
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
afterEach(() => {
	jest.resetAllMocks();
});

test('adds a new job if job does not exist', () => {
	let event = new Event('a time', 2, 2, 2, 1);
	let jobStorage = new JobStorage(mockContext);
	jobStorage.addEvents([event]);
	expect(jobStorage.getAllJobs().length).toBe(1);
	expect(Job).toHaveBeenCalledTimes(1);
	//expect(jobStorage.getJob(event.getJobID()).getJobID()).toBe(event.getJobID());
});

test('jobname is not set if information can not be accessed', async () => {
	let spy = jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockRejectedValue();
	let jobStorage = new JobStorage(mockContext);
	let event = new Event('a time', 4, 3, 2, 1);
	jobStorage.addEvents([event]);
	await waitFor(() => {
		//expect(jobStorage.getJob(event.getJobID()).getJobName()).toBeNull();
		expect(Job.mock.instances[0].setJobName).not.toHaveBeenCalled();
		expect(spy).toHaveBeenCalledWith(event.getJobID());
	});
});

test('jobname is set if information can be accessed', async () => {
	let spy = jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockResolvedValue(testSingleInformation);
	let jobStorage = new JobStorage(mockContext);
	let event = new Event('a time', 4, 3, 2, 1);
	jobStorage.addEvents([event]);
	await waitFor(() => {
		//expect(jobStorage.getJob(event.getJobID()).getJobName()).toBe(testJobName)
		expect(Job.mock.instances[0].setJobName).toHaveBeenCalledTimes(1);
		expect(Job.mock.instances[0].setJobName).toHaveBeenCalledWith(testJobName);
	});
});

test('username and email are set if information can be accessed and the names differ', async () => {
	let spy = jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockResolvedValue(testSingleInformation);
	let jobStorage = new JobStorage(mockContext);
	let event = new Event('a time', 4, 3, 2, 1);
	jobStorage.addEvents([event]);
	await waitFor(() => {
		let instance = Job.mock.instances[0];
		expect(instance.setUsername).toHaveBeenCalledTimes(1);
		expect(instance.setUsername).toHaveBeenCalledWith(testUsername);
		expect(instance.setUserEmail).toHaveBeenCalledTimes(1);
		expect(instance.setUserEmail).toHaveBeenCalledWith(testUserEmail);
	});
});

test('username and email are not set if information can be accessed and the name is the same', async () => {
	let localMockContext = mockContext;
	localMockContext.userContext.user.username = testUsername;
	let spy = jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockResolvedValue(testSingleInformation);
	let jobStorage = new JobStorage(localMockContext);
	let event = new Event('a time', 4, 3, 2, 1);
	jobStorage.addEvents([event]);
	await waitFor(() => {
		let instance = Job.mock.instances[0];
		expect(instance.setJobName).toHaveBeenCalledTimes(1);
		expect(instance.setJobName).toHaveBeenCalledWith(testJobName);
		expect(instance.setUsername).not.toHaveBeenCalled();
		expect(instance.setUserEmail).not.toHaveBeenCalled();
	});
});

test('calls the update method after adding a job and is not reset before', () => {
	jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockResolvedValue(null);
	let event1 = new Event('a time', 2, 2, 2, 1);
	let event2 = new Event('a second time ', 2, 3, 4, 1);
	let jobStorage = new JobStorage(mockContext);
	let listener = new JobUpdateListener();
	jobStorage.addEvents([event1]);
	jobStorage.addJobUpdateListener(listener);
	jobStorage.addEvents([event2]);
	expect(listener.update).toHaveBeenCalledTimes(1);
	expect(listener.update).toHaveBeenCalledWith(
		//jobStorage.getJob(event2.getJobID()),
		Job.mock.instances[1],
		event2.getTreeIndex(),
		true,
        false
	);
});

test('calls the total update method in in beginning', () => {
	jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockRejectedValue(null);
	let event1 = new Event('a time', 2, 2, 2, 1);
	let jobStorage = new JobStorage(mockContext);
	let listener = new JobUpdateListener();
	jobStorage.addJobUpdateListener(listener);
	jobStorage.addEvents([event1]);
	expect(listener.totalUpdate).toHaveBeenCalledTimes(1);
	expect(listener.totalUpdate).toHaveBeenCalledWith([
		//jobStorage.getJob(event1.getJobID()),
		Job.mock.instances[0],
	]);
});


test('removes updateListeners', () =>{
	jest
		.spyOn(mockContext.jobContext, 'getSingleJobInfo')
		.mockRejectedValue(null);
    let jobStorage = new JobStorage(mockContext);
    let listener = new JobUpdateListener();
    let event1 = new Event('a time', 4,3,2,1);
    jobStorage.addJobUpdateListener(listener);
    jobStorage.addEvents([event1]);
    let event2 = new Event('a time', 5,4,2,1);
    jobStorage.addEvents([event2])
    expect(listener.update).toHaveBeenCalledTimes(1);
    jobStorage.removeJobUpdateListener(listener);
    let event3 = new Event('a time', 6,5,2,1);
    jobStorage.addEvents([event3]);
    expect(listener.update).toHaveBeenCalledTimes(1);
})

test('removing a non existing updateListener does not crash', () =>{
    let jobStorage = new JobStorage(mockContext);
    expect(() =>{jobStorage.removeJobUpdateListener(null)}).not.toThrowError();
    
})

test('resets the global stats', () => {
	let jobStorage = new JobStorage(mockContext);
	jobStorage.reset();
	let globalStatsInstance = GlobalStats.mock.instances[0];
	expect(globalStatsInstance.setUsedProcesses).toHaveBeenCalledWith(0);
	expect(globalStatsInstance.setActiveJobs).toHaveBeenCalledWith(0);
});

test('initializes the gloabal stats', () => {
	let jobStorage = new JobStorage(mockContext);
	expect(GlobalStats).toHaveBeenCalledTimes(1);
	let globalStatsInstance = GlobalStats.mock.instances[0];
	expect(globalStatsInstance.setProcesses).toHaveBeenCalledTimes(1);
	expect(globalStatsInstance.setProcesses).toHaveBeenCalledWith(
		amountProcesses
	);
	expect(globalStatsInstance.setUsedProcesses).toHaveBeenCalledTimes(1);
	expect(globalStatsInstance.setUsedProcesses).toHaveBeenCalledWith(0);
	expect(globalStatsInstance.setActiveJobs).toHaveBeenCalledTimes(1);
	expect(globalStatsInstance.setActiveJobs).toHaveBeenCalledWith(0);
});

test('does not crash if addEvents gets null or undefined as parameter', () => {
	let jobStorage = new JobStorage(mockContext);
	jobStorage.addEvents(null);
	expect(jobStorage.getAllJobs().length).toBe(0);
	jobStorage.addEvents(undefined);
	expect(jobStorage.getAllJobs().length).toBe(0);
});

test('ignores unload events if empty', () => {
	let jobStorage = new JobStorage(mockContext);
	let event = new Event('a time', 3, 2, 1, 0);
	expect(() => {
		jobStorage.addEvents([event]);
	}).not.toThrow(AppError);
});

test('returns the created global stats', ()=>{
     let jobStorage = new JobStorage(mockContext);
    expect(jobStorage.getGlobalStats()).toBe(GlobalStats.mock.instances[0])
})


