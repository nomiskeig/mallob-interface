import { TimeManager, TIME_BACKWARD, TIME_FORWARD } from './TimeManager';
import { waitFor } from '@testing-library/react';
import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import addMilliseconds from 'date-fns/addMilliseconds';

const TIMEOUT = 50;
const MAX_DIFFERENCE = 10
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
    expect(tm.getNextTime()).not.toBeNull();
});

test('returns the same next time when updateTime is not called in between', () => {
	let tm = new TimeManager();
	let nextTime = tm.getNextTime();
	expect(tm.getNextTime()).toBe(nextTime);
});

test('calculates the next time after updateTime has been called', async () => {
	let tm = new TimeManager();
    let time = tm.getNextTime();
    tm.updateTime();
    await new Promise((r) => setTimeout(r, TIMEOUT))
    let newTime = tm.getNextTime();
    expect(newTime).not.toEqual(time)

});


    

test('calculates the next time with default multiplier', async () => {
	let tm = new TimeManager();
	let initialTime = new Date();
	await new Promise((r) => setTimeout(r, TIMEOUT));
	let newTime = new Date();
	let difference = differenceInMilliseconds(newTime, tm.getNextTime());
	expect(Math.abs(difference)).toBeLessThanOrEqual(MAX_DIFFERENCE);
});

test('calculates the next time with other positive multiplier', async () => {
	let tm = new TimeManager();
	tm.setMultiplier(2);
	await new Promise((r) => setTimeout(r, TIMEOUT));
	let newTime = new Date();
	let difference = differenceInMilliseconds(
		tm.getNextTime(),
		addMilliseconds(newTime, TIMEOUT)
	);
	expect(Math.abs(difference)).toBeLessThanOrEqual(MAX_DIFFERENCE);
});

test('calculates the next time with negative multiplier', async () => {
	let tm = new TimeManager();
	tm.setMultiplier(-2);
	await new Promise((r) => setTimeout(r, TIMEOUT));
    let newTime = new Date();
    let difference = differenceInMilliseconds(tm.getNextTime(), addMilliseconds(newTime, - TIMEOUT * 3))
    expect(Math.abs(difference)).toBeLessThanOrEqual(MAX_DIFFERENCE);
});
