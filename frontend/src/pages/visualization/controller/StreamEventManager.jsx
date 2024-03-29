import { EventManager } from './EventManager';
import { Event } from './Event';
import axios from 'axios';
import isBefore from 'date-fns/isBefore';
import isAfter from 'date-fns/isAfter';
import isEqual from 'date-fns/isEqual';
/**
 * This class uses the Event-Stream provided by the API to provide the new Events.
 *
 * @extends EventManager
 * @author Simon Giek
 */
export class StreamEventManager extends EventManager {
	#stream;
	#lastTimeReceived;
	constructor(timeManager) {
		super(timeManager);
		this.#stream = null;
		this.#lastTimeReceived = new Date();
	}

	closeStream() {
		console.log('closed the stream');
		if (this.#stream) {
			console.log('aborted the stream');
			this.#stream.abort();
		}
	}

	getNewEvents(userContext) {
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
		window.onbeforeunload = () => {
			this.closeStream();
		};

		this.#stream = new XMLHttpRequest();
		let initialTime = this.timeManager.getNextTime();
		//TODO: stream authentification
		this.#stream.open(
			'GET',
			process.env.REACT_APP_API_BASE_PATH + '/api/v1/events/eventStream',
			true
		);
		this.#stream.setRequestHeader(
			'Authorization',
			'Bearer ' + userContext.user.token
		);
		this.#stream.onprogress = (event) => {
			let events = event.target.responseText.split('\n');
			// recover events if they get lost and no event is invoked for them
			let newEvents = [];
			let index = events.length - 2;
			while (true) {
                if (index == -1) {
                    break;
                }
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
				if (isAfter(newEvent.getTime(), initialTime)) {
					newEvents.push(newEvent);
				}
				index = index - 1;
			}
			this.#lastTimeReceived = newEvents[0].getTime();
			newEvents.reverse().forEach((event) => this.events.push(event));
		};
		this.#stream.send();
		return axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/events/state?time=' +
				initialTime.toISOString(),
			headers: {
				Authorization: 'Bearer ' + userContext.user.token,
			},
		}).then((res) => {
			let result = [];
			res.data.events.forEach((event) => {
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
		});
	}
}
