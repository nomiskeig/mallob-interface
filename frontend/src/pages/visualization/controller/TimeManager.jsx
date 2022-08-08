import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import addMilliseconds from 'date-fns/addMilliseconds';
import isAfter from 'date-fns/isAfter';
export const TIME_FORWARD = 1;
export const TIME_BACKWARD = 0;
export class TimeManager {
	#lastTime;
	#live;
	#jump;
	#multiplier;
	#nextTime;
	#paused;
	#lastTimeMeasured;
	constructor() {
		this.#lastTime = new Date();
		this.#live = true;
		this.#jump = false;
		this.#multiplier = 1;
		this.#nextTime = null;
		this.#paused = false;
		this.#lastTimeMeasured = new Date();
	}

	getDirection() {
		return this.#multiplier >= 0 ? TIME_FORWARD : TIME_BACKWARD;
	}

	setMultiplier(m) {
		this.#multiplier = m;
	}

	updateTime() {
		if (this.#nextTime) {
			this.#lastTime = this.#nextTime;
		}
		this.#jump = false;
		this.#nextTime = null;
	}

	isPaused() {
		return this.#paused;
	}
	setPaused(paused) {
		this.#paused = paused;
		if (this.#live) {
			this.#jump = true;
		}
		this.#live = false;
	}

	setJump() {
		this.#jump = true;
	}
	getJump() {
		return this.#jump;
	}

	getNextTime() {
		if (!this.#nextTime) {
			let currentTime = new Date();
			if (this.#paused) {
				this.#lastTimeMeasured = currentTime;
				this.#nextTime = this.#lastTime;
				return this.#nextTime;
			} else {
				let differenceInMillis = differenceInMilliseconds(
					currentTime,
					this.#lastTimeMeasured
				);
				this.#lastTimeMeasured = currentTime;
				let millisToAdd = differenceInMillis * this.#multiplier;
				// TODO: this can at most be the current time or at least the startTime
				let nextTime = addMilliseconds(this.#lastTime, millisToAdd);
				if (isAfter(nextTime, new Date())) {
					nextTime = new Date();
				}
				this.#nextTime = nextTime;
				// set to live so that the stream is used again when next time is current enough
			}
		}
		if (
			!this.#live && !this.#paused &&
			Math.abs(differenceInMilliseconds(this.#nextTime, new Date())) < 100
		) {
			this.#live = true;
			this.#jump = true;
			this.#multiplier = 1;
		} else if (
           
			this.#live &&
			Math.abs(differenceInMilliseconds(this.#nextTime, new Date())) >= 100
		) {
			// set live to false if the difference is too big
			this.#live = false;
            this.jump = true;
		}
		return this.#nextTime;
	}

	setNextTime(time) {
		this.#nextTime = time;
	}

	isLive() {
		return this.#live;
	}

	getLastTime() {
		return this.#lastTime;
	}
	getMultiplier() {
		return this.#multiplier;
	}
}
