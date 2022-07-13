import { TimeManager, TIME_BACKWARD, TIME_FORWARD } from './TimeManager';

test('calculates the forward directen correctly', () => {
	let tm = new TimeManager();
	expect(tm.getDirection()).toBe(TIME_FORWARD);
});

test('calculates the backward direction correctly', () => {
	let tm = new TimeManager();
	tm.setMultiplier(-1);
	expect(tm.getDirection()).toBe(TIME_BACKWARD);
});

test('has a next time after creation', () => {
	let tm = new TimeManager();
	expect(tm.getNextTime()).not.toBeUndefined();
});


