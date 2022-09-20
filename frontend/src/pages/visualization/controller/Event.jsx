/**
 * This class represents an event that is processed by the logic.
 * 
 * @author Simon Giek
 */
export class Event {
    #time;
    #rank; 
    #treeIndex;
    #jobID;
    #load;
    /**
     * The constructor.
     *
     * @param {Date} time - The point in time at which the event occured.
     * @param {int} rank - The rank which the event belongs to.
     * @param {int} treeIndex - The index of the binary tree which the event belongs to.
     * @param {int} jobID - The ID of the job the event belongs to.
     * @param {int} load - 1 for a load-event, 0 for a unload event.
     */
    constructor(time, rank, treeIndex, jobID, load) {
        this.#time= time;
        this.#rank = rank;
        this.#treeIndex = treeIndex;
        this.#jobID = jobID;
        this.#load = load;
    }

    /**
     * Getter for the time of the event.
     *
     * @returns {Date} The time of the event.
     */
    getTime() {
        return this.#time;
    }

    /**
     * Getter for the rank of the event.
     *
     * @returns {int} The rank of the event.
     */
    getRank() {
        return this.#rank;
    }

    /**
     * Getter for the tree index of the event.
     *
     * @returns {int} The tree index of the event.
     */
    getTreeIndex() {
        return this.#treeIndex;
    }
    /**
     * Getter for the ID of the job the event belongs to.
     *
     * @returns {int} The ID of the job.
     */
    getJobID() {
        return this.#jobID;
    }
    /**
     * Getter for the load of the event.
     *
     * @returns {int} The load of the event.
     */
    getLoad() {
        return this.#load;
    }
}
