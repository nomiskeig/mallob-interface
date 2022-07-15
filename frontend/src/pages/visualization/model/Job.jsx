import { JobTreeVertex } from './JobTreeVertex';
export class Job {
	#color;
	#jobID;
	#username;
	#userEmail;
	#jobName;
	#size;
	#vertices;
	constructor(jobID) {
		this.#vertices = Array();
		this.#size = 0;
		this.#color = null;
		this.#jobID = jobID;
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
		this.#vertices[treeIndex] = undefined;
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

	getSubtree(treeIndex) {
		let subtree = new Array();
		// walk to root of tree
		let currentIndex = treeIndex;
		while (currentIndex !== 0) {
			let parentIndex = Math.floor((currentIndex + 1) / 2) - 1;
			let parentVertex = this.getVertex(parentIndex);
			if (parentVertex) {
				subtree.push(parentVertex);
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
				subtree.push(parentVertex);
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
}
