import { TimeManager, TIME_BACKWARD, TIME_FORWARD } from './TimeManager';
import { waitFor } from '@testing-library/react';
import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import addMilliseconds from 'date-fns/addMilliseconds';
import addSeconds from 'date-fns/addSeconds'

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

    // set timemanager to a past time so the multiplier is not reset because it would surpass the current time
    let pastTime = addMilliseconds(new Date(), -2000);

    tm.setNextTime(pastTime);
    tm.updateTime();
	tm.setMultiplier(2);
	await new Promise((r) => setTimeout(r, TIMEOUT));
	let difference = differenceInMilliseconds(
		tm.getNextTime(),
        addMilliseconds(pastTime, 2 * TIMEOUT)
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

test('sets paused', () =>{
    let tm = new TimeManager();
    tm.setPaused(true);
    expect(tm.isPaused()).toBeTruthy();
}) 

test('sets jump', ()=>{
    let tm = new TimeManager();
    expect(tm.getJump()).toBeFalsy();
    tm.setJump();
    expect(tm.getJump()).toBeTruthy();
})

test('sets the multiplier', () => {
    let tm = new TimeManager();
    tm.setMultiplier(5);
    expect(tm.getMultiplier()).toBe(5);
})


test('returns the correct last date', ()=> {
    let tm = new TimeManager();
    tm.setMultiplier(200);
    let nextTime = tm.getNextTime();
    tm.updateTime();
    expect(tm.getLastTime()).toBe(nextTime);
})


test('sets to live if the next time is close enough to the current time', ()=> {
    let tm = new TimeManager();
    tm.setNextTime(addSeconds(new Date(), -200));
    tm.updateTime();
    tm.getNextTime();
    expect(tm.isLive()).toBeFalsy();
    tm.setNextTime(new Date());
    tm.getNextTime();
    expect(tm.isLive()).toBeTruthy();
})


test('does not update the time if is paused', () =>{
    let tm = new TimeManager();
    let time = addSeconds(new Date(), -200);
    tm.setNextTime(time);
    tm.updateTime();
    tm.setPaused(true);
    expect(tm.getNextTime()).toBe(time);
    

});

test('sets live to false if paused while live', () =>{
    let tm = new TimeManager();
    expect(tm.isLive()).toBeTruthy();
    tm.setPaused(true);
    tm.getNextTime();
    expect(tm.isLive()).toBeFalsy();

})

