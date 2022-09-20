/**
 * This class contains the values for the global statistics. Is kept up to date be the {@link JobStorage} class.
 *
 * @author Simon Giek
 *
 */
export class GlobalStats {
    #usedProcesses;
    #activeJobs;
    #processes;

    /**
     * Setter for the amount of currently active jobs.
     *
     * @param {int} activeJobs - The new amount of active jobs.
     */
    setActiveJobs(activeJobs) {
        this.#activeJobs = activeJobs;
    }
    /**
     * Setter for the amount of currently used processes.
     *
     * @param {int} usedProcesses - The new amount of used processes.
     */
    setUsedProcesses(usedProcesses) {
        this.#usedProcesses = usedProcesses;
    }

    /**
     * Setter for the amount of the total processes the system uses.
     *
     * @param {int} processes - The amount of processes the system uses.
     */
    setProcesses(processes) {
        this.#processes = processes;
    }

    /**
     * Getter for the amount of currently active jobs.
     *
     * @returns {int} The amount of currently active jobs.
     */
    getActiveJobs() {
        return this.#activeJobs;
    }

    /**
     * Getter for the amount of currently used processes.
     *
     * @returns {int} The amount of currently used processes.
     */
    getUsedProcesses() {
        return this.#usedProcesses;
    }

    /**
     * Getter for the amount of processes.
     *
     * @returns {int} The amount of processes.
     */
    getProcesses() {
        return this.#processes;
    }
}
