export class VisualizationNode {
	#circle;
	#text;
	#jobID;
	#treeIndex;
	#defaultRadius = 6;
	#rank;
	constructor(circle, text, rank) {
		this.#circle = circle;
		this.#text = text;
		this.#jobID = null;
		this.#treeIndex = null;
		this.#rank = rank;
	}

	reset() {
		this.#circle.fill = '#C4C4C4';
		this.#circle.linewidth = 0;
		this.#jobID = null;
		this.#treeIndex = null;
		this.#circle.radius = this.#defaultRadius;
		this.#text.opacity = 0;
	}

	registerCallbacks(onHoverEnter, onHoverLeave, onClick) {
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

	updateOpacityForHover() {
		this.#circle.opacity = 0.2;
		this.#text.opacity = 0;
	}

	resetHover() {
		this.#circle.opacity = 1;
		this.#text.opacity = 0;
	}

	showTreeIndexForHover() {
		this.#text.opacity = 1;
	}
	getJobID() {
		return this.#jobID;
	}
	getTreeIndex() {
		return this.#treeIndex;
	}
}
