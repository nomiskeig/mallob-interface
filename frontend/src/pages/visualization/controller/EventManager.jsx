
export class EventManager {
    // have to be public so subclasses can access them
    events; 
    timeManager;
    constructor(timeManager) {
        this.events = [];
        this.timeManager = timeManager;
    }
    getNewEvents() {
       console.log('not implemented')
    }

    getSystemState(userContext) {
        console.log('not implemented')
    }
    closeStream() {

    }

}
