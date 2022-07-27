import { AppError } from '../../../context/AppError';
import { JobStorage } from './JobStorage';
import { Event } from '../controller/Event';
import {JobUpdateListener} from './JobUpdateListener'
jest.mock("./JobUpdateListener");
let mockContext = {
    settingsContext: {
        settings: {
            amountProcesses: 5,
        }
    }
}

beforeEach(() => {
    JobUpdateListener.mockClear();
})
test('throws error when trying to apply an event with load 0 on a job which does not exist after the initial load', () => {

	let event = new Event('a time', 5, 4, 1, 1);
    let event2 = new Event('a second time', 6,7,1,0)
	let jobStorage = new JobStorage(mockContext);
    jobStorage.addEvents([event])
	expect(() => {
		jobStorage.addEvents([event2]);
	}).toThrow(AppError);
});

test('adds a new job if job does not exist', () => {
	let event = new Event('a time', 2, 2, 2, 1);
	let jobStorage = new JobStorage(mockContext);
	jobStorage.addEvents([event]);
	expect(jobStorage.getAllJobs().length).toBe(1);
	expect(jobStorage.getJob(event.getJobID()).getJobID()).toBe(event.getJobID());
});

test('calls the update method after adding a job and is not reset before', () => {
	let event1 = new Event('a time', 2, 2, 2, 1);
    let event2 = new Event('a second time ', 2, 3, 4, 1);
	let jobStorage = new JobStorage(mockContext);
    let listener = new JobUpdateListener();
    jobStorage.addEvents([event1]);
    jobStorage.addJobUpdateListener(listener);
	jobStorage.addEvents([event2]);
    expect(listener.update).toHaveBeenCalledTimes(1);
    expect(listener.update).toHaveBeenCalledWith(jobStorage.getJob(event2.getJobID()), event2.getTreeIndex(), true)

});


test('calls the total update method in in beginning', () => {
	let event1 = new Event('a time', 2, 2, 2, 1);
    let jobStorage = new JobStorage(mockContext);
    let listener = new JobUpdateListener();
    jobStorage.addJobUpdateListener(listener);
    jobStorage.addEvents([event1]);
    expect(listener.totalUpdate).toHaveBeenCalledTimes(1);
    expect(listener.totalUpdate).toHaveBeenCalledWith([jobStorage.getJob(event1.getJobID())])
})
