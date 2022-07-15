import { AppError } from '../../../global/errorHandler/AppError';
import {Job} from './Job'
import {JobTreeVertex} from './JobTreeVertex'
export class JobStorage {
	#jobUpdateListeners;
	#jobs;
	constructor() {
		this.#jobUpdateListeners = new Array();
		this.#jobs = new Array();
	}

	reset() {
		this.#jobs = new Array();
	}

	/**
	 * @param {Event} events - the events to add
	 *
	 */
	addEvents(events) {
		let wasEmpty = this.#jobs.length == 0 ? true : false;
		events.forEach((event) => {
			let jobIndex = this.#jobs.findIndex(
				(e) => e.getJobID() == event.getJobID()
			);
			let job = this.#jobs[jobIndex];
			let jobID = event.getJobID();
			let rank = event.getRank();
			let treeIndex = event.getTreeIndex();
			//unload events
			if (event.getLoad() == 0) {
				if (job == undefined) {
					throw new AppError('Can not stop working on a non-existent job');
				}
				job.remove(event.getTreeIndex());
				if (!wasEmpty) {
					this.#jobUpdateListeners.forEach((listener) =>
						listener.update(job, treeIndex)
					);
				}
				if (job.getSize() == 0) {
					this.#jobs[jobIndex] = undefined;
				}
			} else {
				//load event
				if (job == undefined) {
					let newJob = new Job(jobID);
					this.#jobs.push(newJob);
                    job = newJob;
				}
				let vertex = new JobTreeVertex(rank, treeIndex);
				job.addVertex(vertex);
                if (!wasEmpty) {
				this.#jobUpdateListeners.forEach((listener) =>
					listener.update(job, treeIndex)
				);
                }
			}
		});
        if (wasEmpty) {
            this.#jobUpdateListeners.forEach((listener) => {
                listener.totalUpdate(this.#jobs);
            })
        }
	}

	addJobUpdateListener(jul) {
		this.#jobUpdateListeners.push(jul);
	}

	removeJobUpdateLisener(jul) {
		let index = this.#jobUpdateListeners.findIndex((e) => e === jul);
		if (index) {
			this.#jobUpdateListeners[index] = undefined;
		}
	}

	getAllJobs() {
		return this.#jobs;
	}

	getJob(jobID) {
        let job = this.#jobs.find(e => e.getJobID() == jobID) 
        if (job == undefined) {
            return null;
        }
        return job
    }
}
