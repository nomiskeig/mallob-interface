import { EventManager } from './EventManager';
import { AppError } from '../../../global/errorHandler/AppError';
import { Event } from './Event';
import axios from 'axios';
import isBefore from 'date-fns/isBefore';
import isAfter from 'date-fns/isAfter';
import isEqual from 'date-fns/isEqual';
export class StreamEventManager extends EventManager {
	#stream;
	constructor(timeManager) {
		super(timeManager);
	}

	getNewEvents() {
		let lastTime = this.timeManager.getLastTime();
		let nextTime = this.timeManager.getNextTime();
        // TODO: should probably remove those entries from the event array, but does that work with the async stream?
		return this.events.filter((event) => (
				isAfter(event.time, lastTime) &&
				(isBefore(event.time, nextTime) || isEqual(event.time, nextTime))
		));
	}

	getSystemState(userContext) {
		if (process.env.NODE_ENV === 'development') {
			return null;
		}
		axios({
			method: 'get',
			url:
				process.env.REACT_APP_API_BASE_PATH +
				'/api/v1/events/state?time=' +
				this.timeManager.getNextTime().toISOString(),
			headers: {
				Authentication: 'Bearer ' + userContext.user.token,
			},
		})
			.then((res) => {
				return res.data;
			})
			.catch((e) => {
				console.log(e.message);
			});

		this.#stream = new XMLHttpRequest();
		this.#stream.open(
			'GET',
			process.env.REACT_APP_API_BASE_PATH + '/api/v1/events/eventStream'
		);
		this.#stream.onprogress = (event) => {
			let events = event.target.responseText.split('\n');
			let lastEventString = events[events.length - 2];
			let lastEvent = JSON.parse(lastEventString);

			console.log('stream received an event');
			console.log(lastEvent);
			let newEvent = new Event(
				new Date(lastEvent.time),
				lastEvent.rank,
				lastEvent.treeIndex,
				lastEvent.jobID,
				lastEvent.load
			);
			this.events.push(newEvent);
		};
		// TODO: Stream is missing authentification header
		// TODO: close stream somehow
		this.#stream.send();
	}
}
