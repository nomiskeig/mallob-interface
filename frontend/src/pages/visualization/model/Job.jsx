/**
 * Class that represents a job which can be displayed in the visualization and as a binary tree. The job is represented as a binary tree.
 *
 * @author Simon Giek
 */
export class Job {
	#color;
	#outerColor;
	#jobID;
	#username;
	#userEmail;
	#jobName;
	#size;
	#vertices;
	/**
	 * The constructor.
	 *
	 * @param {int} jobID - The ID of the job.
	 * @param {String} color - The color of the job.
	 */
	constructor(jobID, color) {
		this.#vertices = [];
		this.#size = 0;
		this.#color = color;
		this.#jobID = jobID;
		this.#jobName = null;
		this.#username = null;
		this.#userEmail = null;
		this.#outerColor = null;
	}

	/**
	 * Adds the given vertex to the binary tree of the job.
	 *
	 * @param {JobTreeVertex} vertex - The vertex to add.
	 */
	addVertex(vertex) {
		let index = vertex.getTreeIndex();

		if (this.#vertices[index] === undefined) {
			this.#size++;
		}
		this.#vertices[index] = vertex;
	}

	/**
	 * Returns the vertex at the given index.
	 *
	 * @param {int} treeIndex - The index of the vertex to get.
	 * @returns {JobTreeVertex} The JobTreeVertex at the given index. Returns null if there is no vertex at the given index.
	 */
	getVertex(treeIndex) {
		if (this.#vertices[treeIndex] === undefined) {
			return null;
		}
		return this.#vertices[treeIndex];
	}

	/**
	 * Removes the vertex at the given index from the binary tree of the job.
	 *
	 * @param {int} treeIndex - The index of the vertex to remove.
	 */
	removeVertex(treeIndex) {
		// accessing an unused index in an array returns undefined, so we set removed vertices to undefined
		// in order to have everything consistent
		if (this.#vertices[treeIndex] !== undefined) {
			this.#size--;
		}
		delete this.#vertices[treeIndex];
	}

	/**
	 * Returns the vertex of which of its children is the vertex at the given index.
	 *
	 * @param {int} treeIndex - The index of the vertex of the child.
	 * @returns {JobTreeVertex} The vertex which is the parent of the vertex at the given index. Returns null if there is no such vertex.
	 */
	getParent(treeIndex) {
		let parentIndex = Math.floor((treeIndex + 1) / 2) - 1;
		if (this.#vertices[parentIndex] === undefined) {
			return null;
		}
		return this.#vertices[parentIndex];
	}

	/**
	 * Returns the vertex which is the left child of the vertex at the given index.
	 *
	 * @param {int} treeIndex - The index of the vertex of which to left child is returned.
	 * @returns {JobTreeVertex} The vertex which is the left child of the vertex at the given index. Returns null if there is no such vertex.
	 */
	getLeftChild(treeIndex) {
		let childIndex = 2 * (treeIndex + 1) - 1;
		if (this.#vertices[childIndex] === undefined) {
			return null;
		}
		return this.#vertices[childIndex];
	}

	/**
	 * Returns the vertex which is the right child of the vertex at the given index.
	 *
	 * @param {int} treeIndex - The index of the vertex of which to right child is returned.
	 * @returns {JobTreeVertex} The vertex which is the right child of the vertex at the given index. Returns null if there is no such vertex.
	 */
	getRightChild(treeIndex) {
		let childIndex = 2 * (treeIndex + 1);
		if (this.#vertices[childIndex] === undefined) {
			return null;
		}
		return this.#vertices[childIndex];
	}

	/**
	 * Returns the amount of vertices which currently make up the job.
	 *
	 * @returns {int} The size of the job.
	 */
	getSize() {
		return this.#size;
	}

	/**
	 * Returns all of the vertices which currently make up the job.
	 *
	 * @returns {JobTreeVertex[]} The vertices which currently make up the job.
	 */
	getVertices() {
		return this.#vertices.filter((vertex) => vertex !== undefined);
	}

	/**
	 * Retuns a new Job with the following vertices:
	 * - Vertices between the root of the tree and the vertex with the given index.
	 * - The vertex at the given index.
	 * - All recursive children of the vertex at the given index.
	 *
	 * @param {int} treeIndex - The index of the vertex to get the subtree of.
	 * @returns {Job} A new job containing all the vertices that make up the subtree.
	 */
	getSubtree(treeIndex) {
		let subtree = new Job(this.#jobID, this.#color);
		// walk to root of tree
		let currentIndex = treeIndex;
		while (currentIndex !== 0) {
			let parentIndex = Math.floor((currentIndex + 1) / 2) - 1;
			let parentVertex = this.getVertex(parentIndex);
			if (parentVertex) {
				subtree.addVertex(parentVertex);
			}
			currentIndex = parentIndex;
		}

		// walk to the leafs of tree
		let indices = [];
		indices.push(treeIndex);
		while (indices.length > 0) {
			let parentIndex = indices.pop();
			let parentVertex = this.getVertex(parentIndex);
			if (parentVertex) {
				subtree.addVertex(parentVertex);
			}
			let leftChildIndex = 2 * (parentIndex + 1) - 1;
			if (leftChildIndex <= this.#vertices.length) {
				indices.push(leftChildIndex);
			}
			let rightChildIndex = 2 * (parentIndex + 1);
			if (rightChildIndex <= this.#vertices.length) {
				indices.push(rightChildIndex);
			}
		}
		return subtree;
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
	 * Getter for the color of the job.
	 *
	 * @returns {String} The color of the job.
	 */
	getColor() {
		return this.#color;
	}

	/**
	 * Getter for the name of the job.
	 *
	 * @returns {String} The name of the job. Returns null if the name is not set.
	 */
	getJobName() {
		return this.#jobName;
	}

	/**
	 * Setter for the Color of the job.
	 *
	 * @param {String} color - The new color for the job.
	 */
	setColor(color) {
		this.#color = color;
	}

	/**
	 * Setter for the username of the user the job belongs to.
	 *
	 * @param {String} name - The name of the user the job belongs to.
	 */
	setUsername(name) {
		this.#username = name;
	}
    
	/**
	 * Getter for the username.
	 *
	 * @returns {String} The name of the user this job belongs to. Returns null if the username is not set.
	 */
	getUsername() {
		return this.#username;
	}

	/**
	 * Setter for the email of the user the job belongs to.
	 *
	 * @param {String} email - The email of the user the job belongs to.
	 */
	setUserEmail(email) {
		this.#userEmail = email;
	}

	/**
	 * Getter for the email of the user the job belongs to.
	 *
	 * @returns {String} The email of the user the job belongs to. Returns null if the email is not set.
	 */
	getUserEmail() {
		return this.#userEmail;
	}

	/**
	 * Setter for the name of the job.
	 *
	 * @param {String} name - The name of the job.
	 */
	setJobName(name) {
		this.#jobName = name;
	}

	/**
	 * Setter for the outer color of the job.
	 *
	 * @param {String} color - The outer color of the job.
	 */
	setOuterColor(color) {
		this.#outerColor = color;
	}
	/**
	 * Getter for the outer color of the job.
	 *
	 * @returns {String} The outer color of the job. Returns null if the outer color is not set.
	 */
	getOuterColor() {
		return this.#outerColor;
	}
}
