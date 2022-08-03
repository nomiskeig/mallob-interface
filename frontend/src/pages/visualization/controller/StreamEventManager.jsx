import { EventManager } from './EventManager';
import { Event } from './Event';
import axios from 'axios';
import isBefore from 'date-fns/isBefore';
import isAfter from 'date-fns/isAfter';
import isEqual from 'date-fns/isEqual';
export class StreamEventManager extends EventManager {
	#stream;
	#lastTimeReceived;
	constructor(timeManager) {
		super(timeManager);
		this.#stream = null;
		this.#lastTimeReceived = null;
	}

	closeStream() {
		if (this.#stream) {
			this.#stream.abort();
		}
	}

	getNewEvents() {
		let nextTime = this.timeManager.getNextTime();
		let newEvents = this.events.filter(
			(event) =>
				isBefore(event.getTime(), nextTime) ||
				isEqual(event.getTime(), nextTime)
		);
		this.events.splice(0, newEvents.length);
		return newEvents;
	}

	async getSystemState(userContext) {
        window.onbeforeunload = () =>  {
            this.closeStream();
        }
		//if (process.env.NODE_ENV === 'development') {
		//	return null;
		//}
        console.log('getting system state')
		this.#stream = new XMLHttpRequest();
		let initialTime = this.timeManager.getNextTime();
		//TODO: stream authentification
        console.log(this.#stream)
		this.#stream.open(
			'GET',
			process.env.REACT_APP_API_BASE_PATH + '/api/v1/events/eventStream',
			true
		);
		this.#stream.onprogress = (event) => {
			let events = event.target.responseText.split('\n');
			if (!this.#lastTimeReceived) {
				let lastEventString = events[events.length - 2];

				console.log('tast4Eventsk');
				for (let i = 2; i < 6; i++) {
					//console.log(JSON.parse(events[events.length - i]));
				}
				let lastEvent = JSON.parse(lastEventString);
				console.log('lastEvent');
				console.log(lastEvent);
				let newEvent = new Event(
					new Date(lastEvent.time),
					lastEvent.rank,
					lastEvent.treeIndex,
					lastEvent.jobID,
					lastEvent.load
				);
				this.#lastTimeReceived = newEvent.getTime();
				if (isAfter(newEvent.getTime(), initialTime)) {
					this.events.push(newEvent);
				}
			} else {
				// recover events if they get lost and no event is invoked for them, i do not know why that happens
				let newEvents = [];
				let index = events.length - 2;
				while (true) {
					let lastEvent = JSON.parse(events[index]);
					let date = new Date(lastEvent.time);
					if (!isAfter(date, this.#lastTimeReceived)) {
						break;
					}
					let newEvent = new Event(
						date,
						lastEvent.rank,
						lastEvent.treeIndex,
						lastEvent.jobID,
						lastEvent.load
					);
					console.log(
						'last time received: ' + this.#lastTimeReceived.toISOString()
					);
					console.log(lastEvent);
					if (isAfter(newEvent.getTime(), initialTime)) {
						newEvents.push(newEvent);
					}
					index = index - 1;
				}
				this.#lastTimeReceived = newEvents[0].getTime();
				newEvents.reverse().forEach((event) => this.events.push(event));
			}
		};
		this.#stream.onloadstart = () => {
			console.log('loaded');
		};
		this.#stream.send();
		return axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/events/state?time=' +
				initialTime.toISOString(),
			headers: {
				Authentication: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
				console.log('initialEvents');
				console.log(res.data);
				let result = [];
				res.data.forEach((event) => {
					let newEvent = new Event(
						new Date(event.time),
						event.rank,
						event.treeIndex,
						event.jobID,
						event.load
					);
					//if (isAfter(newEvent.getTime(), initialTime)) {

					result.push(newEvent);
					//}
				});
				console.log(result);
				return result;
			})
	}
}
