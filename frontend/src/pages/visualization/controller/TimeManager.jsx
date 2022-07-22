import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import addMilliseconds from 'date-fns/addMilliseconds';
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
			} else {
				let differenceInMillis = differenceInMilliseconds(
					currentTime,
					this.#lastTimeMeasured
				);
				this.#lastTimeMeasured = currentTime;
				let millisToAdd = differenceInMillis * this.#multiplier;
				let nextTime = addMilliseconds(this.#lastTime, millisToAdd);

				this.#nextTime = nextTime;
			}
		}
		return this.#nextTime;
	}

	setNextTime(time) {
		this.#nextTime = time;
		if (Math.abs(differenceInMilliseconds(time, new Date())) < 100) {
			this.#live = true;
		} else {
			this.#live = false;
		}
	}

	isLive() {
		return this.#live;
	}

	setLive() {
		this.#live = true;
	}
	getLastTime() {
		return this.#lastTime;
	}
    getMultiplier() {
        return this.#multiplier;
    }
}
