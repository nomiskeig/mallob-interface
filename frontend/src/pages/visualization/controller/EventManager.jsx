import {TYPE_UNRECOVERABLE} from '../../../context/InfoContextProvider'
export class EventManager {
    // have to be public so subclasses can access them
    events; 
    timeManager;
    constructor(timeManager) {
        this.events = [];
        this.timeManager = timeManager;
    }
    getNewEvents() {
        throw Error('the getNewEvents method can not be used on the parent class', TYPE_UNRECOVERABLE);
    }

    getSystemState(userContext) {
       throw Error('the getSystemState method can not be used on the parent class');
    }
    closeStream() {

    }

}
