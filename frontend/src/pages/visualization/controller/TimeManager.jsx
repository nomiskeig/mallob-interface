import differenceInMilliseconds from 'date-fns/differenceInMilliseconds'
import addMilliseconds from 'date-fns/addMilliseconds'
export const TIME_FORWARD = 1;
export const TIME_BACKWARD = 0;
export class TimeManager {
	#lastTime;
	#live;
	#jump;
	#multiplier;
	#nextTime;
	#paused;
	constructor() {
		this.#lastTime = new Date();
		this.#live = true;
		this.#jump = false;
		this.#multiplier = 1;
		this.#nextTime = null;
		this.#paused = false;
	}

	getDirection() {
		return this.#multiplier >= 0 ? TIME_FORWARD : TIME_BACKWARD;
	}

	setMultiplier(m) {
		this.#multiplier = m;
	}

	updateTime() {
		this.#lastTime = this.#nextTime;
		this.#jump = false;
		this.#nextTime= null;
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
			// calculate next time based on old time and multiplier
			let currentTime = new Date();
			let differenceInMillis = differenceInMilliseconds(
				currentTime,
				this.#lastTime
			);
			let millisToAdd = differenceInMillis * this.#multiplier;
			let nextTime = addMilliseconds(currentTime, millisToAdd);

            this.#nextTime = nextTime;
		}
		return this.#nextTime;
	}

	setNextTime(time) {
		this.#nextTime = time;
		this.#live = false;
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
}
