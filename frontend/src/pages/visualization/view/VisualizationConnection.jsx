/**
 * This class represents a connection between the vertices of two processes.
 * 
 * @author Simon Giek
 */
export class VisualizationConnection {
	#line;
	#depth = 0;
	#defaultOpacity = 0;
	#defaultWidth = 3;
    #otherRank = null;
    /**
     * The constructor.
     *
     * @param {Line} line - The line used be the instance.
     */
	constructor(line) {
		this.#line = line;
	}

    /**
     * Sets the line to match the given parameters.
     *
     * @param {JobTreeVertex} jtv - The JobTreeVertex to set the line to.
     * @param {float} x - The x coordinate of the other rank.
     * @param {float} y - The y coordinate of the other rank.
     * @param {Job} job - The job the JobTreeVertex belongs to.
     */
	useConnection(jtv, x, y, job) {
		this.#depth = jtv.getDepth();
		this.#line.vertices[1].x = x;
		this.#line.vertices[1].y = y;
		this.#line.stroke = job.getColor();
		this.#line.linewidth = this.#defaultWidth;
		this.#defaultOpacity = Math.max(1 - 0.15 * this.#depth, 0.1);
		this.#line.opacity = this.#defaultOpacity;
        this.#otherRank = jtv.getRank();
	}
    /**
     * Resets the connection.
     *
     */
	reset() {
		this.#line.noStroke();
	}

    /**
     * Hides the connection.
     *
     */
	hideForHover() {
		this.#line.opacity = 0;
	}

    /**
     * Resets the appearance of the connection.
     *
     */
	resetHover() {
		this.#line.opacity = this.#defaultOpacity;
		this.#line.linewidth = this.#defaultWidth;
	}
    /**
     * Getter for the rank which the connection connects to.
     *
     * @returns {int} The other rank.
     */
    getOtherRank() {
        return this.#otherRank;
    }

    /**
     * Updates the opacity based the own depth and the given other depth
     *
     * @param {int} otherDepth - The other depth.
     */
	updateOpacityForHover(otherDepth) {
		if (this.#defaultOpacity === 0) {
			return;
		}
		let dif = Math.abs(otherDepth - this.#depth);
		this.#line.opacity = Math.max(0.8 - dif * 0.1, 0.1);
	}

    /**
     * Updates the width of the connection based on the depth.
     *
     */
	setWidthBasedOnDepth() {
		this.#line.linewidth = Math.max(15 - 1.5 * this.#depth, 1);
	}
    /**
     * Sets the coordinates of the connection.
     *
     * @param {float} x1 - The x coordinate of the first rank.
     * @param {float} y1 - The y coordinate of the first rank.
     * @param {float} x2 - The x coordinate of the second rank.
     * @param {float} y2 - The y coordinate of the second rank.
     */
    setCoords(x1, y1, x2, y2) {
        this.#line.vertices[0].x = x1;
        this.#line.vertices[0].y = y1;
        this.#line.vertices[1].x = x2;
        this.#line.vertices[1].y = y2;
    }
}
