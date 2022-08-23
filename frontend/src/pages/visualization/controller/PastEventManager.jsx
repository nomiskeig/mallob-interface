import axios from 'axios';
import { EventManager } from './EventManager';
import {Event} from './Event'
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

	getNewEvents(userContext) {
		//forwards
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
				let endTime = addSeconds(
					this.#forwardBuffer,
					this.timeManager.getMultiplier() * 20
				);
				if (isAfter(endTime, new Date())) {
					endTime = new Date();
				}
				axios({
					method: 'get',
					url: process.env.REACT_APP_API_BASE_PATH + '/api/v1/events/events',
					params: {
						startTime: this.#forwardBuffer.toISOString(),
						endTime: endTime.toISOString(),
					},
                    headers: {
                        Authorization: 'Bearer ' + userContext.user.token,
                    }
				}).then((res) => {
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

					this.#forwardBuffer = endTime;
                        this.#isLoading = false;
				});
			}

			let newEvents = this.events.filter((event) =>
				isBefore(event.getTime(), this.timeManager.getNextTime())
			);
			this.events.splice(0, newEvents.length);

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
		//if (process.env.NODE_ENV === 'development') {
		//	return null;
		//}

		let nextTime = this.timeManager.getNextTime();
		this.#forwardBuffer = nextTime;
		this.#backwardBuffer = nextTime;
		this.getNewEvents(userContext);
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
	}
}
