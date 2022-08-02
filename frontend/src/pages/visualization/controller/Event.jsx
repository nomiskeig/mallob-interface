export class Event {
    #time;
    #rank; 
    #treeIndex;
    #jobID;
    #load;
    constructor(time, rank, treeIndex, jobID, load) {
        this.#time= time;
        this.#rank = rank;
        this.#treeIndex = treeIndex;
        this.#jobID = jobID;
        this.#load = load;
    }

    getTime() {
        return this.#time;
    }

    getRank() {
        return this.#rank;
    }

    getTreeIndex() {
        return this.#treeIndex;
    }
    getJobID() {
        return this.#jobID;
    }
    getLoad() {
        return this.#load;
    }
}
