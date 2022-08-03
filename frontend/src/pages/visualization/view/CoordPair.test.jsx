import {CoordPair} from './CoordPair';

test('gets the first parameters correctly', ()=> {
    let pair = new CoordPair(4,2);
    expect(pair.getX()).toBe(4);
    expect(pair.getY()).toBe(2);
})
