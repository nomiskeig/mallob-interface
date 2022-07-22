import { AppError } from '../../../global/errorHandler/AppError';
import { Job } from './Job';
import { JobTreeVertex } from './JobTreeVertex';
import {GlobalStats} from './GlobalStats'
export class JobStorage {
	#jobUpdateListeners;
	#jobs;
	#context;
	#globalStats;
	constructor(context) {
		this.#jobUpdateListeners = new Array();
		this.#jobs = new Array();
		this.#context = context;
		this.#globalStats = new GlobalStats();
		this.#globalStats.setProcesses(
			context.settingsContext.settings.amountProcesses
		);
		this.#globalStats.setUsedProcesses(0);
		this.#globalStats.setActiveJobs(0);
	}

	updateContext(context) {
		this.#context = context;
	}

	reset() {
		this.#jobs = new Array();
	}

	/**
	 * @param {Event} events - the events to add
	 *
	 */
	addEvents(events) {
		function getRandomColor() {
			let color =
				'hsl(' +
				360 * Math.random() +
				',' +
				'100%,' +
				(45 + 10 * Math.random()) +
				'%)';
			return color;
		}
		if (!events) {
			return;
		}
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
                if (wasEmpty) {
                    return;
                }
                this.#globalStats.setUsedProcesses(this.#globalStats.getUsedProcesses() - 1)
				if (job == undefined) {
					throw new AppError('Can not stop working on a non-existent job');
				}
				if (!wasEmpty) {
					this.#jobUpdateListeners.forEach((listener) =>
						listener.update(job, treeIndex, false)
					);
				}
				job.removeVertex(event.getTreeIndex());
				// remove the job if there are no processes left working on it
				if (job.getSize() == 0) {
					delete this.#jobs[jobIndex];
					this.#globalStats.setActiveJobs(
						this.#globalStats.getActiveJobs() - 1
					);
				}
			} else {
				//load event
                this.#globalStats.setUsedProcesses(this.#globalStats.getUsedProcesses() + 1)
                // create now job if job does not exist
				if (job == undefined) {
					let newJob = new Job(jobID, getRandomColor());
					this.#jobs.push(newJob);
					job = newJob;
                    this.#globalStats.setActiveJobs(this.#globalStats.getActiveJobs() + 1);
				}
				/*
                // this is necessary to prevent "ghosts", it is not required if we can assume the structure of events given by mallob
				let oldVertex = job.getVertex(treeIndex);
                if (oldVertex) {
                    if (!wasEmpty) {
                        this.#jobUpdateListeners.forEach((listener)  => (
							listener.update(job, treeIndex, false)
                        ))
                    }
                    
                }
                */
				let vertex = new JobTreeVertex(rank, treeIndex);
				job.addVertex(vertex);

				if (!wasEmpty) {
					this.#jobUpdateListeners.forEach((listener) => {
						listener.update(job, treeIndex, true);
					});
				}
			}
		});
		if (wasEmpty) {
            console.log('updated all')
			this.#jobUpdateListeners.forEach((listener) => {
				listener.totalUpdate(this.#jobs);
			});
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
		let job = this.#jobs.find((e) => e.getJobID() == jobID);
		if (job == undefined) {
			return null;
		}
		return job;
	}

    getGlobalStats() {
        return this.#globalStats;
    }
}
