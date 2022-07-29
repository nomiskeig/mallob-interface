import axios from 'axios';
import { EventManager } from './EventManager';
import { Event } from './Event';
import addSeconds from 'date-fns/addSeconds';
import isAfter from 'date-fns/isAfter';
import isBefore from 'date-fns/isBefore';
import differenceInSeconds from 'date-fns/differenceInSeconds';
import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';

export class PastEventManager extends EventManager {
	#forwardBuffer;
	#backwardBuffer;
    #isLoading
	constructor(timeManager) {
		super(timeManager);
        this.#isLoading = false;
	}

	getNewEvents() {
		//forwards
		// TODO: what happens if request takes too long and events are skipped
		if (this.timeManager.getDirection() === 1) {
			// buffer to small, load new events
			if (
				Math.abs(
					differenceInSeconds(
						this.timeManager.getNextTime(),
						this.#forwardBuffer
					)
				) <
				this.timeManager.getMultiplier() * 5 && !this.#isLoading && !this.timeManager.isPaused()
			) {
                this.#isLoading  =true;
				console.log('load forward puffer');
				console.log(this.#forwardBuffer.toISOString());
				let endTime = addSeconds(
					this.#forwardBuffer,
					this.timeManager.getMultiplier() * 20
				);
				if (isAfter(endTime, new Date())) {
					console.log(' end is after current time');
					endTime = new Date();
				}
				//console.log('endTime');
				//console.log(endTime);
				axios({
					method: 'get',
					url: process.env.REACT_APP_API_BASE_PATH + '/api/v1/events/events',
					params: {
						startTime: this.#forwardBuffer.toISOString(),
						endTime: endTime.toISOString(),
					},
				}).then((res) => {
					//console.log('got forward puffer');
					//kjkkconsole.log(res.data);
					res.data.forEach((event) =>
						this.events.push(
							new Event(
								new Date(event.time),
								event.rank,
								event.treeIndex,
								event.jobID,
								event.load
							)
						)
					);
					this.events.sort(function (a, b) {
						return differenceInMilliseconds(a.getTime(), b.getTime());
					});
					console.log(this.events);

					this.#forwardBuffer = endTime;
                        this.#isLoading = false;
				});
			}

			let newEvents = this.events.filter((event) =>
				isBefore(event.getTime(), this.timeManager.getNextTime())
			);
			this.events.splice(0, newEvents.length);
			//console.log(newEvents);

			return newEvents;
		} else {
			let events = this.events.filter(
				(event) =>
					isAfter(event.getTime(), this.timeManager.getNextTime()) &&
					isBefore(event.getTime(), this.timeManager.getLastTime())
			);
            return events;
		}
	}

	async getSystemState(userContext) {
		if (process.env.NODE_ENV === 'development') {
			return null;
		}
		// create a puffer
		// forwards
		// todo

		let nextTime = this.timeManager.getNextTime();
		this.#forwardBuffer = nextTime;
		this.#backwardBuffer = nextTime;
		this.getNewEvents();
		return axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/events/state?time=' +
				nextTime.toISOString(),
			headers: {
				Authentication: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
				console.log('initialEvents');
				console.log(res.data);
				let result =[];
				res.data.forEach((event) => {
					let newEvent = new Event(
						new Date(event.time),
						event.rank,
						event.treeIndex,
						event.jobID,
						event.load
					);
					result.push(newEvent);
				});
				return result;
			})
			.catch((e) => {
				console.log(e.message);
				throw e;
			});
	}
}
