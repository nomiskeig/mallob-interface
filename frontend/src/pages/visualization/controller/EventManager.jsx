
export class EventManager {
    // have to be public so subclasses can access them
    events; 
    timeManager;
    constructor(timeManager) {
        this.events = [];
        this.timeManager = timeManager;
    }
    getNewEvents() {
        throw 'the getNewEvents method can not be used on the parent class';
    }

    getSystemState(userContext) {
        throw 'the getSystemState method can not be used on the parent class';
    }
    closeStream() {

    }

}
