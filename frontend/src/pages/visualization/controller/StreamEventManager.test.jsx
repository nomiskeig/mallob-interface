import { StreamEventManager } from './StreamEventManager';
import axios from 'axios';
import { TimeManager } from './TimeManager.jsx';
import addSeconds from 'date-fns/addSeconds';
import { Event } from './Event';

jest.mock('axios');
jest.mock('./TimeManager.jsx');
let streamEventManager;
let nextTime = new Date();
let mockUserContext = {
	user: {
		token: 'testToken',
	},
};
const testSystemState = [
	{
		time: nextTime.toISOString(),
		jobID: 1,
		rank: 2,
		treeIndex: 3,
		load: 1,
	},
];
beforeEach(() => {
	axios.mockClear();
	TimeManager.mockClear();
	let timeManager = new TimeManager();
	streamEventManager = new StreamEventManager(timeManager);
	jest.clearAllMocks();
});
afterEach(() => {
	jest.clearAllMocks();
    streamEventManager.closeStream();
});
function setTimeManagerDefaults() {
	jest.spyOn(TimeManager.prototype, 'getNextTime').mockReturnValue(nextTime);
	jest.spyOn(TimeManager.prototype, 'getMultiplier').mockReturnValue(1);
	jest.spyOn(TimeManager.prototype, 'getDirection').mockReturnValue(1);
}

test('gets the current system state', () => {
	setTimeManagerDefaults();

	axios.mockResolvedValue({
		data: {
            events: testSystemState,
        }
	});
	let state = streamEventManager.getSystemState(mockUserContext);
	state.then((data) => {
		expect(data[0].getRank()).toBe(testSystemState[0].rank);
		expect(data.length).toBe(1);
	});
	expect(axios).toBeCalledTimes(1);

});

test('opens the stream when getSystemState is called', () => {
	setTimeManagerDefaults();
	//let openFn = jest.spyOn(XMLHttpRequest.prototype, 'open');
	axios.mockResolvedValue({
		data: {
            events: testSystemState,
        }
	});
	streamEventManager.getSystemState(mockUserContext);
    streamEventManager.closeStream();
	//expect(openFn).toHaveBeenCalledTimes(1);
});
// something does not work with the spy in the above test, if you remove the spy this does work...
/*test('closes the stream', () => {
	setTimeManagerDefaults();
	//let abortFn = jest.spyOn(XMLHttpRequest.prototype, 'abort');
	axios.mockResolvedValue({ data: testSystemState });
	streamEventManager.getSystemState(mockUserContext);
	//streamEventManager.closeStream();
	//expect(abortFn).toHaveBeenCalledTimes(1)
});*/

test('get the new events that are in the past', () => {
	setTimeManagerDefaults();
	let event = new Event(addSeconds(nextTime, -1), 3, 2, 1, 1);
	streamEventManager.events = [event];
	expect(streamEventManager.getNewEvents()).toEqual([event]);
	expect(streamEventManager.events.length).toBe(0);
});

test('only gets the new events whihc are before the nexttime', () => {
	setTimeManagerDefaults();
	let event1 = new Event(addSeconds(nextTime, -1), 4, 3, 2, 1);
	let event2 = new Event(addSeconds(nextTime, 1), 4, 3, 2, 1);
	streamEventManager.events = [event1, event2];
	expect(streamEventManager.getNewEvents()).toEqual([event1]);
	expect(streamEventManager.events.length).toBe(1);
});
