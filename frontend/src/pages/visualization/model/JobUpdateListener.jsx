/**
 * This class defines the interface for the listeners which can be registered in the JobStorage-class.
 *
 * @author Simon Giek
 */
export class JobUpdateListener {

    /**
     * Informs the implementing class that the given job was updated at the given treeIndex.
     *
     * @param {int} job - The updated job.
     * @param {int} updatedTreeIndex - The index of the vertex which was updated.
     * @param {boolean} add - True if the update is for an event with load = 1, false if not
     * @param {boolean} justForColor - If true, only the color is updated.
     */
	update(job, updatedTreeIndex, add, justForColor) {}
    /**
     * Informs the implementing class that the current jobs have to be updated to a new set of jobs.
     *
     * @param {Job[]} jobs - The new set of jobs. 
     */
	totalUpdate(jobs) {}
}
