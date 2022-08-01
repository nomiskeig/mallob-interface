import {EventManager} from './EventManager';
import {TimeManager} from './TimeManager';

jest.mock('./TimeManager.jsx');
test('is initialized correctly', () =>{ 
    let tm = new TimeManager();
    let eventManager = new EventManager(tm);
    expect(eventManager.events).toEqual([]);
    expect(eventManager.timeManager).toBe(tm);
})

test('throws error if getNewEvents method is called', ()=>{
    let eventManager = new EventManager();
    expect(() => eventManager.getNewEvents()).toThrowError();
})
test('throws error if getSystemState method is called', ()=>{
    let eventManager = new EventManager();
    expect(() => eventManager.getSystemState()).toThrowError();
})

test('does not throw error if the closeStream method is called', () =>{
    let eventManager = new EventManager();
    expect(() => {
        eventManager.closeStream()
    }).not.toThrowError();
})
