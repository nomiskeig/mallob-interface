import {Event} from './Event'


test('stores data from constructor', () => {
    let date = new Date();
    let event = new Event(date, 5, 3, 2, 1)
    expect(event.getRank()).toBe(5)
    expect(event.getTime()).toBe(date)
})
