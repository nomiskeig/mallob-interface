export class GlobalStats {
    #usedProcesses;
    #activeJobs;
    #processes;

    setActiveJobs(activeJobs) {
        this.#activeJobs = activeJobs;
    }
    setUsedProcesses(usedProcesses) {
        this.#usedProcesses = usedProcesses;
    }

    setProcesses(processes) {
        this.#processes = processes;
    }

    getActiveJobs() {
        return this.#activeJobs;
    }

    getUsedProcesses() {
        return this.#usedProcesses;
    }

    getProcesses() {
        return this.#processes;
    }
}
