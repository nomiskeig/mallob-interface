import { PastEventManager } from './PastEventManager';
import { TimeManager } from './TimeManager';
import { Event } from './Event';
import axios from 'axios';
import { AppError } from '../../../context/AppError';

jest.mock('./TimeManager.jsx');
jest.mock('axios');
const token = 'token1234';
const mockUserContext = {
	user: {
		token: token,
	},
};
let nextTime = new Date();
let pastEventManager;
const testSystemState = [
	{
		time: nextTime.toISOString(),
		jobID: 1,
		rank: 2,
		treeIndex: 3,
		load: 1,
	},
];

function setTimeManagerDefaults() {
	jest.spyOn(TimeManager.prototype, 'getNextTime').mockReturnValue(nextTime);
	jest.spyOn(TimeManager.prototype, 'getMultiplier').mockReturnValue(1);
    jest.spyOn(TimeManager.prototype, 'getDirection').mockReturnValue(1);
}
beforeEach(() => {
	TimeManager.mockClear();
	axios.mockClear();
	let timeManager = new TimeManager();
	pastEventManager = new PastEventManager(timeManager);
});
test('gets the systemState of the next time', async () => {
    setTimeManagerDefaults();
    jest.spyOn(pastEventManager, 'getNewEvents').mockImplementation(() =>{})

	axios.mockResolvedValue({
		data: testSystemState,
	});
	let state = pastEventManager.getSystemState(mockUserContext);
	state.then((data) => {
		expect(data[0].getRank()).toBe(testSystemState[0].rank);
		expect(data.length).toBe(1);
	});
	expect(axios).toBeCalledTimes(1);
});



