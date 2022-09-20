/**
 * This class reprsents a process in the visualization.
 * 
 * @author Simon Giek
 */
export class VisualizationNode {
	#circle;
	#text;
	#jobID;
	#treeIndex;
	#defaultRadius = 6;
	#rank;
    /**
     * The constructor.
     *
     * @param {Circle} circle - The circle to use.
     * @param {Text} text - The text to use.
     * @param {int} rank - The rank of the process.
     */
	constructor(circle, text, rank) {
		this.#circle = circle;
		this.#text = text;
		this.#jobID = null;
		this.#treeIndex = null;
		this.#rank = rank;
	}

    /**
     * Resets the node.
     *
     */
	reset() {
		this.#circle.fill = '#C4C4C4';
		this.#circle.linewidth = 0;
		this.#jobID = null;
		this.#treeIndex = null;
		this.#circle.radius = this.#defaultRadius;
		this.#text.opacity = 0;
	}

    /**
     * Registers the given callbacks on the eventListeners of the elements created by Two.js
     *
     * @param {Method} onHoverEnter - The onHoverEnter method of the Visualization-class.
     * @param {Method} onHoverLeave - The onHoverLeave method of hte Visualization-class.
     * @param {Method} onClick - The onClick method of the VisualizationPageManager.
     */
	registerCallbacks(onHoverEnter, onHoverLeave, onClick) {
        this.#circle._renderer.elem.style.cursor = 'pointer'
        this.#text._renderer.elem.style.cursor = 'default'
		this.#circle._renderer.elem.addEventListener('mouseover', () => {
			onHoverEnter(this.#rank);
		});
		this.#circle._renderer.elem.addEventListener('mouseout', () => {
			onHoverLeave();
		});
		this.#circle._renderer.elem.addEventListener('mousedown', () => {
			onClick(this.#jobID, this.#treeIndex);
		});
	}

    /**
     * Updates the Paramters to match the given paramters.
     *
     * @param {JobTreeVertex} jtv - The JobTreeVertex to macth.
     * @param {Job} job - The job the JobTreeVertex belongs to.
     */
	setToJobTreeVertex(jtv, job) {
		this.#circle.fill = job.getColor();
		this.#circle.radius = Math.max(10 - 2 * jtv.getDepth(), 4);
		this.#jobID = job.getJobID();
		this.#treeIndex = jtv.getTreeIndex();
		if (job.getOuterColor()) {
			this.#circle.stroke = job.getOuterColor();
			this.#circle.linewidth = '3';
		}
	}

    /**
     * Reduces the opacity of this process.
     *
     */
	updateOpacityForHover() {
		this.#circle.opacity = 0.2;
		this.#text.opacity = 0;
	}

    /**
     * Resets the appearance of the process.
     *
     */
	resetHover() {
		this.#circle.opacity = 1;
		this.#text.opacity = 0;
	}

    /**
     * Displays the index above the process.
     *
     */
	showTreeIndexForHover() {
		this.#text.opacity = 1;
	}
    /**
     * Getter for the ID of the job.
     *
     * @returns {int} The ID of the job.
     */
	getJobID() {
		return this.#jobID;
	}
    /**
     * Getter for the tree index.
     *
     * @returns {int} The tree index.
     */
	getTreeIndex() {
		return this.#treeIndex;
	}
    /**
     * Sets the coordinates of the process to the given coordinates.
     *
     * @param {float} x - The new x coordinate. 
     * @param {float} y - The new y coordinate. 
     */
    setCoords(x, y) {
        this.#circle.position.x = x;
        this.#circle.position.y = y;
        this.#text.position.x = x;
        this.#text.position.y = y-14;
    }
}
