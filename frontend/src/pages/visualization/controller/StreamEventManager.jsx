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

	cancelStream() {
		if (this.#stream) {
			this.#stream.abort();
		}
	}

	getNewEvents() {
		window.onbeforeunload = () => {
			this.#stream.abort();
		};
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
		if (process.env.NODE_ENV === 'development') {
			return null;
		}
		let initialEvents = new Array();
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
		this.#stream.send();
		return axios({
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
				console.log('initialEvents');
				console.log(res.data);
				let result = new Array();
				res.data.forEach((event) => {
					let newEvent = new Event(
						event.time,
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
