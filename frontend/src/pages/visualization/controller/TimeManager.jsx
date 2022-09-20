import differenceInMilliseconds from 'date-fns/differenceInMilliseconds';
import addMilliseconds from 'date-fns/addMilliseconds';
import isAfter from 'date-fns/isAfter';
export const TIME_FORWARD = 1;
export const TIME_BACKWARD = 0;
/**
 * This class is responsible for managing the point in time which is displayed by the visualization.
 *
 * @author Simon Giek
 */
export class TimeManager {
	#lastTime;
	#live;
	#jump;
	#multiplier;
	#nextTime;
	#paused;
	#lastTimeMeasured;
    /**
     * The constructor.
     *
     */
	constructor() {
		this.#lastTime = new Date();
		this.#live = true;
		this.#jump = false;
		this.#multiplier = 1;
		this.#nextTime = null;
		this.#paused = false;
		this.#lastTimeMeasured = new Date();
	}

    /**
     * Returns the current direction.
     *
     * @returns {int} 1 if time is going forward, 0 if the time is going backwards.
     */
	getDirection() {
		return this.#multiplier >= 0 ? TIME_FORWARD : TIME_BACKWARD;
	}

    /**
     * Sets the multiplier for the replay speed.
     *
     * @param {float} m - The new replay speed.
     */
	setMultiplier(m) {
		this.#multiplier = m;
	}

    /**
     * Updates the TimeManager to the next point in time.
     *
     */
	updateTime() {
		if (this.#nextTime) {
			this.#lastTime = this.#nextTime;
		}
		this.#jump = false;
		this.#nextTime = null;
	}

    /**
     * Getter for the paused attribute.
     *
     * @returns {boolean} True if the replay is currently paused, false if it is not.
     */
	isPaused() {
		return this.#paused;
	}
    /**
     * Setter for the paused attribute.
     *
     * @param {boolean} paused - The paused attribute.
     */
	setPaused(paused) {
		this.#paused = paused;
		if (this.#live) {
			this.#jump = true;
		}
		this.#live = false;
	}

    /**
     * Sets jump to true.
     *
     */
	setJump() {
		this.#jump = true;
	}
    /**
     * Getter for the jump attribute.
     *
     * @returns {boolean} True if a jump is required, false if it is not.
     */
	getJump() {
		return this.#jump;
	}

    /**
     * Calculates and returns the next time.
     *
     * @returns {Date} The next point in time to display.
     */
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
				let nextTime = addMilliseconds(this.#lastTime, millisToAdd);
				if (isAfter(nextTime, new Date())) {
					nextTime = new Date();
				}
				this.#nextTime = nextTime;
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

    /**
     * Setter for the next time.
     *
     * @param {Date} time - The next time.
     */
	setNextTime(time) {
		this.#nextTime = time;
	}

    /**
     * Getter for the live attribute.
     *
     * @returns {boolean} True if the visualization is live, false if it is not.
     */
	isLive() {
		return this.#live;
	}

    /**
     * Getter for the last time.
     *
     * @returns {Date} The last time.
     */
	getLastTime() {
		return this.#lastTime;
	}
    /**
     * Getter for the multiplier.
     *
     * @returns {float} The current multiplier.
     */
	getMultiplier() {
		return this.#multiplier;
	}
}
