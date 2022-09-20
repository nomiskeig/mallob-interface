/**
 * This class is used by the BinaryTree class to display the nodes of the binary tree.
 *
 * @author Simon Giek
 */
export class BinaryTreeNode {
	#text;
	#circle;

	/**
	 * The constructor.
	 *
	 * @param {Circle} circle - The circle to operate on.
	 * @param {Text} text - The text to operate on.
	 */
	constructor(circle, text) {
		this.#text = text;
		this.#circle = circle;
	}

	/**
	 * Sets the private text and circle to match the parameters.
	 *
	 * @param {JobTreeVertex} jtv - The JobTreeVertex to match.
	 * @param {Job} job - The job the JobTreeVertex belongs to.
	 */
	setToJobTreeVertex(jtv, job) {
		this.#circle.opacity = 1;
		this.#circle.fill = job.getColor();
		this.#text.opacity = 1;
		this.#circle.opacity = 1;
		this.#text.value = jtv.getRank();
		if (job.getOuterColor()) {
			this.#circle.stroke = job.getOuterColor();
			this.#circle.linewidth = '3';
		}
	}

	/**
	 * Sets the x position to the given x value.
	 *
	 * @param {float} x - The new x position.
	 */
	setX(x) {
		this.#circle.position.x = x;
		this.#text.position.x = x;
	}
	/**
	 * Sets the y position to the given y value.
	 *
	 * @param {float} y - The new y position.
	 */
	setY(y) {
		this.#circle.position.y = y;
		this.#text.position.y = y + 15;
	}
	/**
	 * Resets the vertex.
	 *
	 */
	reset() {
		this.#circle.opacity = 0;
		this.#text.opacity = 0;
		this.#circle.linewidth = '0';
		this.#circle.radius = 6;
	}

	/**
	 * Increases the radius of the vertex.
	 *
	 */
	setClickedOn() {
		this.#circle.radius = 10;
	}

	/**
	 * Resets the radius of the vertex.
	 *
	 */
	resetClickedOn() {
		this.#circle.radius = 6;
	}
}
