import {TYPE_UNRECOVERABLE} from '../../../context/InfoContextProvider'
/**
 * This class represents the base class for the more specific EventManagers.
 *
 * @author Simon Giek
 */
export class EventManager {
    // have to be public so subclasses can access them
    events; 
    timeManager;
    /**
     * The constructor.
     *
     * @param {TimeManager} timeManager - The timeManager the instance uses.
     */
    constructor(timeManager) {
        this.events = [];
        this.timeManager = timeManager;
    }
    /**
     * Returns the new events since the last point in time.
     *
     */
    getNewEvents() {
    }

    
    /**
     * Returns the events that make up the system state at the time which the timeManager currently has.
     *
     * @param {UserContext} userContext - The context with information about the logged in user.
     */
    getSystemState(userContext) {
    }
    /**
     * Closes the instance of a stream, if one is currently active.
     *
     */
    closeStream() {

    }

}
