import { Job } from './Job';
import { JobTreeVertex } from './JobTreeVertex';
import { GlobalStats } from './GlobalStats';
/**
 * This class is stores and mananges the jobs currenlty in the System. The context is used to set Name and Color of the job, as well as the username and the email of the user.
 * 
 * @author Simon Giek
 */
export class JobStorage {
	#jobUpdateListeners;
	#jobs;
	#context;
	#globalStats;
    /**
     * The constructor.
     *
     * @param {Context} context - The context.
     */
	constructor(context) {
		this.#jobUpdateListeners = [];
		this.#jobs = [];
		this.#context = context;
		this.#globalStats = new GlobalStats();
		this.#globalStats.setProcesses(
			context.settingsContext.settings.amountProcesses
		);
		this.#globalStats.setUsedProcesses(0);
		this.#globalStats.setActiveJobs(0);
	}

    /**
     * Updates the stored context.
     *
     * @param {Context} context - The new context.
     */
	updateContext(context) {
		this.#context = context;
	}

    /**
     * Deletes the stored jobs.
     *
     */
	reset() {
		this.#jobs = [];
		this.#globalStats.setUsedProcesses(0);
		this.#globalStats.setActiveJobs(0);
	}

    /**
     * Modifies the stored jobs based on the given events and notifies the the registered JobUpdateListeners about the changes.
     *
     * @param {Event[]} events - The events to process.
     */
	addEvents(events) {
        events.forEach((event) => console.log("added event", event, event.getTime().toISOString()))
		function getSeededRandom(a) {
			let t = (a += 0x6d2b79f5);
			t = Math.imul(t ^ (t >>> 15), t | 1);
			t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
			return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
		}
		function getRandomColor(jobID) {
			let color =
				'hsl(' +
				360 * getSeededRandom(jobID) +
				',' +
				'100%,' +
				(45 + 10 * getSeededRandom(getSeededRandom(jobID))) +
				'%)';
			return color;
		}
		function getRandomOuterColor(jobID) {
			let color =
				'hsl(' +
				360 * getSeededRandom(getSeededRandom(jobID) * 100) +
				',' +
				'100%,' +
				(45 +
					10 *
						getSeededRandom(
							10 * getSeededRandom(1000 * getSeededRandom(jobID))
						)) +
				'%)';
			return color;
		}
		function getRandomGrayColor(jobID) {
			// https://stackoverflow.com/questions/46893750/how-to-generate-random-grey-colors-in-javascript
			let v = ((getSeededRandom(jobID) * 128) | 0).toString(16);
			return '#' + v + v + v;
		}
		if (!events) {
			return;
		}
		let wasEmpty = this.#jobs.length === 0 ? true : false;
		events.forEach((event) => {
			let jobIndex = this.#jobs.findIndex(
				(e) => e.getJobID() === event.getJobID()
			);
			let job = this.#jobs[jobIndex];
			let jobID = event.getJobID();
			let rank = event.getRank();
			let treeIndex = event.getTreeIndex();
			//unload events
			if (event.getLoad() === 0) {
				// ignore the unloads while loading the state
				if (wasEmpty) {
					return;
				}
				if (job === undefined) {
					console.log('non existant job');
                    return;
				}
				if (!job.getVertex(event.getTreeIndex())) {
					console.log('trying to remove a vertex which is not part of the job');
					console.log('rank: ' + event.getRank());
					console.log('treeIndex: ' + event.getTreeIndex());
                    return;
				}
				this.#globalStats.setUsedProcesses(
					this.#globalStats.getUsedProcesses() - 1
				);
				if (!wasEmpty) {
					this.#jobUpdateListeners.forEach((listener) =>
						listener.update(job, treeIndex, false,false)
					);
				}
				job.removeVertex(event.getTreeIndex());
				// remove the job if there are no processes left working on it
				if (job.getSize() === 0) {
					//delete this.#jobs[jobIndex];
                    this.#jobs.splice(jobIndex, 1);
					this.#globalStats.setActiveJobs(
						this.#globalStats.getActiveJobs() - 1
					);
				}
			} else {
				//load event

				this.#globalStats.setUsedProcesses(
					this.#globalStats.getUsedProcesses() + 1
				);
				// create now job if job does not exist
				if (job === undefined) {
					let newJob = new Job(jobID, getRandomGrayColor(jobID));
					this.#jobs.push(newJob);
					job = newJob;
					this.#globalStats.setActiveJobs(
						this.#globalStats.getActiveJobs() + 1
					);
					this.#context.jobContext
						.getSingleJobInfo(jobID)
						.then((info) => {
							job.setColor(getRandomColor(jobID));
							job.setOuterColor(getRandomOuterColor(jobID));
							job.setJobName(info.config.name);
							if (info.user !== this.#context.userContext.user.username) {
								job.setUsername(info.user);
								job.setUserEmail(info.email);
							}

							this.#jobUpdateListeners.forEach((listener) => {
								job
									.getVertices()
									.forEach((vertex) =>
										listener.update(job, vertex.getTreeIndex(), true, true)
									);
							});
						})
						.catch(() => {});
				}
				if (job.getVertex(treeIndex)) {
					console.log('trying to add a vertex which is already existent');
					console.log('rank: ' + rank);
					console.log('treeIndex ' + treeIndex);
				}
				let vertex = new JobTreeVertex(rank, treeIndex);
				job.addVertex(vertex);

				if (!wasEmpty) {
					this.#jobUpdateListeners.forEach((listener) => {
						listener.update(job, treeIndex, true, false);
					});
				}
			}
		});
		if (wasEmpty) {
			this.#jobUpdateListeners.forEach((listener) => {
				listener.totalUpdate(this.#jobs);
			});
		}
	}

    /**
     * Registers the given JobUpdateListener.
     *
     * @param {JobUpdateListener} jul - The listener to register.
     */
	addJobUpdateListener(jul) {
		this.#jobUpdateListeners.push(jul);
	}

    /**
     * Removes the given JobUpdateListener.
     *
     * @param {JobUpdateListener} jul - The JobUpdateListener to remove.
     */
	removeJobUpdateListener(jul) {
		let index = this.#jobUpdateListeners.findIndex((e) => e === jul);
		if (index !== -1) {
			this.#jobUpdateListeners.splice(index, 1);
		}
	}

    /**
     * Returns all the currently stored jobs.
     *
     * @returns {Job[]} The currently stored jobs.
     */
	getAllJobs() {
		return this.#jobs;
	}

    /**
     * Returns the job with the given ID.
     *
     * @param {int} jobID - The ID of the job to return.
     * @returns {Job} The job with the given ID. Returns null if there is no job with the given ID.
     */
	getJob(jobID) {
		let job = this.#jobs.find((e) => e.getJobID() === jobID);
		if (job === undefined) {
			return null;
		}
		return job;
	}

    /**
     * Getter for the instance of the GlobalStats.
     *
     * @returns {GlobalStats} The stored instance of GlobalStats.
     */
	getGlobalStats() {
		return this.#globalStats;
	}
}
