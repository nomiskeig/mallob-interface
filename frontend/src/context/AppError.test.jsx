import {AppError} from './AppError';

test("returns the message and type", () =>{
    let message = 'this is a test message';
    let type = 'this is a test type';
    let error = new AppError(message, type);
    expect(error.getMessage()).toEqual(message);
    expect(error.getType()).toEqual(type);
})
