import { JobTreeVertex } from './JobTreeVertex';
export class Job {
	#color;
    #outerColor;
	#jobID;
	#username;
	#userEmail;
	#jobName;
	#size;
	#vertices;
	constructor(jobID, color) {
		this.#vertices = Array();
		this.#size = 0;
		// TODO: make color correct, this is just for testing purposes

		this.#color = color;
		this.#jobID = jobID;
		this.#jobName = null;
        this.#username = null;
        this.#userEmail = null;
        this.#outerColor = null;
	}

	addVertex(vertex) {
		let index = vertex.getTreeIndex();

		if (this.#vertices[index] == undefined) {
			this.#size++;
		}
		this.#vertices[index] = vertex;
	}

	getVertex(treeIndex) {
		if (this.#vertices[treeIndex] == undefined) {
			return null;
		}
		return this.#vertices[treeIndex];
	}

	removeVertex(treeIndex) {
		// accessing an unused index in an array returns undefined, so we set removed vertices to undefined
		// in order to have everything consistent
		if (this.#vertices[treeIndex] !== undefined) {
			this.#size--;
		}
		delete this.#vertices[treeIndex];
	}

	getParent(treeIndex) {
		let parentIndex = Math.floor((treeIndex + 1) / 2) - 1;
		if (this.#vertices[parentIndex] == undefined) {
			return null;
		}
		return this.#vertices[parentIndex];
	}

	getLeftChild(treeIndex) {
		let childIndex = 2 * (treeIndex + 1) - 1;
		if (this.#vertices[childIndex] == undefined) {
			return null;
		}
		return this.#vertices[childIndex];
	}
	getRightChild(treeIndex) {
		let childIndex = 2 * (treeIndex + 1);
		if (this.#vertices[childIndex] == undefined) {
			return null;
		}
		return this.#vertices[childIndex];
	}

	getSize() {
		return this.#size;
	}

	getVertices() {
		return this.#vertices;
	}

	getSubtree(treeIndex) {
		let subtree = new Job(this.#jobID, this.#color);
		if (treeIndex === null) {
			return subtree;
		}
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
		let indices = new Array();
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

	getJobID() {
		return this.#jobID;
	}
	getColor() {
		// TODO: correct color
		return this.#color;
	}
	getJobName() {
		return this.#jobName;
	}
	setColor(color) {
		this.#color = color;
	}

	setUsername(name) {
		this.#username = name;
	}
	getUsername() {
		return this.#username;
	}

	setUserEmail(email) {
		this.#userEmail = email;
	}

	getUserEmail() {
		return this.#userEmail;
	}

	setJobName(name) {
		this.#jobName = name;
	}
    setOuterColor(color) {
        this.#outerColor = color;
    }
    getOuterColor() {
        return this.#outerColor;
    }
}
