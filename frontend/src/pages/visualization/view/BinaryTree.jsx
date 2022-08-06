import Two from 'two.js';
import { BinaryTreeNode } from './BinaryTreeNode';
import { CoordPair } from './CoordPair';
export class BinaryTree {
	#displayedJobID;
	#jobStorage;
	#canvas;
	#two;
	#nodes;
	#lines;
	#processes;
    #clickedTreeIndex;
	constructor(jobStorage, canvas, processes) {
		this.#jobStorage = jobStorage;
		this.#canvas = canvas;
		this.#processes = processes;
		this.#two = new Two({ fitted: true });
		this.#two.appendTo(canvas);
		this.#nodes = [];
		this.#lines = [];
		this.#canvas = canvas;

		let lineGroup = this.#two.makeGroup();
		let circleGroup = this.#two.makeGroup();
		let textGroup = this.#two.makeGroup();
		for (let i = 0; i < processes; i++) {
			this.#lines[i] = this.#two.makeLine(10, 10, 10, 10);
			this.#lines[i].opacity = 0;
			this.#lines[i].linewidth = 0;
			lineGroup.add(this.#lines[i]);
			let newCircle = this.#two.makeCircle(0, 0, 6);
            newCircle.opacity = 0;
			circleGroup.add(newCircle);
			let newText = this.#two.makeText(0, 0, 0);
			newText.rotation = Math.PI / 2.0;
			newText.opacity = 0;
			newText.size = 10;
			newText.alignment = 'left';
			textGroup.add(newText);
			this.#nodes[i] = new BinaryTreeNode(newCircle, newText);
		}
		this.#two.update();
	}

	totalUpdate() {
		this.clearTree();
	}

	clearTree() {
		this.#displayedJobID = null;
		for (let i = 0; i < this.#processes; i++) {
			this.#lines[i].opacity = 0;
			this.#nodes[i].reset();
		}
		this.#two.update();
	}

	displayTree(jobID, clickedTreeIndex) {
		if (clickedTreeIndex == null) {
			return;
		}
        this.#clickedTreeIndex = clickedTreeIndex

		this.clearTree();
		this.#displayedJobID = jobID;
		let job = this.#jobStorage.getJob(jobID);
		let vertices = job.getVertices();
		vertices.forEach((vertex) => {
			let index = vertex.getTreeIndex();
			let node = this.#nodes[index];
			let biggestIndex = vertices[vertices.length - 1].getTreeIndex();
			let coords = this.getCoords(index, biggestIndex);
			node.setX(coords.getX());
			node.setY(coords.getY());
			node.setToJobTreeVertex(vertex, job);
            if (clickedTreeIndex === index) {
                node.setClickedOn(); 
            }
			// set the line to parent, if parent exists
			let parentVertex = job.getParent(index);
			if (parentVertex) {
				let parentCoords = this.getCoords(
					parentVertex.getTreeIndex(),
					biggestIndex
				);
				this.#setLineToVertex(this.#lines[index], coords, parentCoords, job);
			}
		});
        let subtree = job.getSubtree(clickedTreeIndex);
        subtree.getVertices().forEach(vertex => {
            let index = vertex.getTreeIndex();
            if (subtree.getParent(index)) {
                this.#lines[index].opacity = 1;
            }
        })
		this.#two.update();
	}
    
	#setLineToVertex(line, coords, parentCoords, job) {
		line.stroke = job.getColor();
		line.linewidth = 4;
		line.opacity = 0.4;
		line.vertices[0].x = coords.getX();
		line.vertices[0].y = coords.getY();
		line.vertices[1].x = parentCoords.getX();
		line.vertices[1].y = parentCoords.getY();
	}

	update(job, updatedTreeIndex, add, justForColor) {
		if (job.getJobID() !== this.#displayedJobID) {
			return;
		}
		let vertices = job.getVertices();
		let biggestIndex = vertices[vertices.length - 1].getTreeIndex();
		let vertex = job.getVertex(updatedTreeIndex);

		if (add) {
			// display vertex
			this.#nodes[updatedTreeIndex].setToJobTreeVertex(vertex, job);
			let coords = this.getCoords(updatedTreeIndex, biggestIndex);
            this.#nodes[updatedTreeIndex].setX(coords.getX());
            this.#nodes[updatedTreeIndex].setY(coords.getY())
            let subtree = job.getSubtree(this.#clickedTreeIndex);

			let parent = job.getParent(updatedTreeIndex);
			if (parent) {
				let parentCoords = this.getCoords(parent.getTreeIndex(), biggestIndex);
				this.#setLineToVertex(
					this.#lines[updatedTreeIndex],
					coords,
					parentCoords,
					job
				);
                if (subtree.getParent(updatedTreeIndex)) {
                    this.#lines[updatedTreeIndex].opacity = 1;
                }
			}
			let leftChild = job.getLeftChild(updatedTreeIndex);
			if (leftChild) {
				let childCoords = this.getCoords(
					leftChild.getTreeIndex(),
					biggestIndex
				);
				this.#setLineToVertex(
					this.#lines[leftChild.getTreeIndex()],
					childCoords,
					coords,
					job
				);
                if (subtree.getLeftChild(updatedTreeIndex)) {
                    this.#lines[leftChild.getTreeIndex()].opacity = 1;
                }
			}
			let rightChild = job.getRightChild(updatedTreeIndex);
			if (rightChild) {
				let childCoords = this.getCoords(
					rightChild.getTreeIndex(),
					biggestIndex
				);
				this.#setLineToVertex(
					this.#lines[rightChild.getTreeIndex()],
					childCoords,
					coords,
					job
				);
                if (subtree.getRightChild(updatedTreeIndex)) {
                    this.#lines[rightChild.getTreeIndex()].opacity = 1;
                }
			}
		} else {
			// remove vertex
			this.#nodes[updatedTreeIndex].reset();
			this.#lines[updatedTreeIndex].opacity = 0;
			let leftChild = job.getLeftChild(updatedTreeIndex);
			if (leftChild) {
				this.#lines[leftChild.getTreeIndex()].opacity = 0;
			}
			let rightChild = job.getRightChild(updatedTreeIndex);
			if (rightChild) {
				this.#lines[rightChild.getTreeIndex()].opacity = 0;
			}
		}
		this.#two.update();
	}

	getCoords(treeIndex, highestIndex) {
		function getDepth(index) {
			return Math.trunc(Math.log2(index + 1));
		}
		let availableHeight = this.#canvas.clientHeight - 10;
		let availableWidth = this.#canvas.clientWidth - 20;

		let depth = getDepth(treeIndex);
		let maxDepth = getDepth(highestIndex);
		let y = (availableHeight / (maxDepth + 2)) * (depth + 1);
		let amountNodesWithDepth = Math.pow(2, depth);
		let xSpacing = availableWidth / (2 * amountNodesWithDepth);
		let posInRow = treeIndex - Math.pow(2, depth) + 2;
		let x = 10 +  xSpacing + 2 * xSpacing * (posInRow - 1);
		return new CoordPair(x, y);
	}
}
