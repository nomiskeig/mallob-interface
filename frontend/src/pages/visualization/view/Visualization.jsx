import Two from 'two.js';
import { VisualizationNode } from './VisualizationNode';
import { CoordPair } from './CoordPair';
import { VisualizationConnection } from './VisualizationConnection';
export class Visualization {
	#processes;
	#two;
	#jobStorage;
	#nodes;
	#connections;
	#canvas;
	constructor(canvas, update, processes, jobStorage) {
		this.#two = new Two({ fitted: true });
		this.#two.appendTo(canvas);
		this.#nodes = new Array();
		this.#connections = new Array();
		this.#canvas = canvas;
		this.#processes = processes;
		this.#jobStorage = jobStorage;
		let connectionGroup = this.#two.makeGroup();
		let nodeGroup = this.#two.makeGroup();
		let textGroup = this.#two.makeGroup();
		for (let i = 0; i < processes; i++) {
			let coords = this.#getCoords(i);
			let newNode = this.#two.makeCircle(coords.getX(), coords.getY(), 6);
			nodeGroup.add(newNode);
			let newText = this.#two.makeText(i, coords.getX(), coords.getY() - 14);
            textGroup.add(newText)
			this.#nodes[i] = new VisualizationNode(newNode, newText);
			this.#nodes[i].reset();
			let newConnection = this.#two.makeLine(
				coords.getX(),
				coords.getY(),
				0,
				0
			);
			connectionGroup.add(newConnection);
			this.#connections[i] = new VisualizationConnection(newConnection);
			this.#connections[i].reset();
		}
		this.#two.update();
		for (let i = 0; i < processes; i++) {
			this.#nodes[i].registerHoverCallbacks(
				this.onHoverEnter.bind(this),
				this.onHoverLeave.bind(this)
			);
		}
		// this is ugly but at least it works
		let desiredHeight = this.#getCoords(this.#processes).getY() + 40 + 'px';
		canvas.style.height = desiredHeight;
		this.#two.height = desiredHeight;
		this.#two.bind('update', () => update());
		this.#two.play();
	}

	#getCoords(rank) {
		let margin = 30;
		let distance = 40;
		let perRow = Math.floor((this.#canvas.clientWidth - 2 * margin) / distance);
		let restPerRow = this.#canvas.clientWidth - distance * (perRow - 1);
		let xPos = restPerRow / 2 + distance * (rank % perRow);
		let yPos = margin + distance * Math.floor(rank / perRow);
		return new CoordPair(xPos, yPos);
	}
	onHoverEnter(jobID, treeIndex) {
		// TODO: update connections
		for (let i = 0; i < this.#processes; i++) {
			this.#nodes[i].updateOpacityForHover();
			this.#connections[i].hideForHover();
		}
		if (!jobID) {
			return;
		}
		let job = this.#jobStorage.getJob(jobID);
		let hoveredVertex = job.getVertex(treeIndex);
		console.log(
			'hovered: treeIndex: ' + treeIndex + ' rank: ' + hoveredVertex.getRank()
		);
		job.getVertices().forEach((vertex) => {
			let rank = vertex.getRank();
			this.#nodes[rank].resetHover();
			this.#nodes[rank].showTreeIndexForHover();
		});
		// TODO: only show connections of subtree on hover
		job
			.getSubtree(treeIndex)
			.getVertices()
			.forEach((vertex) => {
				let rank = vertex.getRank();
				if (job.getParent(vertex.getTreeIndex())) {
					this.#connections[rank].updateOpacityForHover(
						hoveredVertex.getDepth()
					);
					this.#connections[rank].setWidthBasedOnDepth();
				}
			});
	}
	onHoverLeave() {
		for (let i = 0; i < this.#processes; i++) {
			this.#nodes[i].resetHover();
			this.#connections[i].resetHover();
		}
	}

	update(job, updatedTreeIndex, add) {
		let vertex = job.getVertex(updatedTreeIndex);
		let parentVertex = job.getParent(updatedTreeIndex);
		let leftChild = job.getLeftChild(updatedTreeIndex);
		let rightChild = job.getRightChild(updatedTreeIndex);
		let rank = vertex.getRank();
		let vertexCoords = this.#getCoords(rank);
		if (add) {
			// display node
			this.#nodes[rank].setToJobTreeVertex(vertex, job);
			// update connection from left child to vertex itself
			if (leftChild) {
				this.#connections[leftChild.getRank()].useConnection(
					vertex,
					vertexCoords.getX(),
					vertexCoords.getY(),
					job
				);
			}
			// update connection from right child to vertex itself
			if (rightChild) {
				this.#connections[rightChild.getRank()].useConnection(
					vertex,
					vertexCoords.getX(),
					vertexCoords.getY(),
					job
				);
			}
			// update connection from vertex to parent
			if (parentVertex) {
				let parentCoords = this.#getCoords(parentVertex.getRank());
				this.#connections[rank].useConnection(
					parentVertex,
					parentCoords.getX(),
					parentCoords.getY(),
					job
				);
			}
		} else {
			// reset vertex and connections going in and out
			this.#nodes[rank].reset();
			if (leftChild) {
				this.#connections[leftChild.getRank()].reset();
			}
			if (rightChild) {
				this.#connections[rightChild.getRank()].reset();
			}
			this.#connections[rank].reset();
		}
	}

	totalUpdate(jobs) {
		jobs.forEach((job) => {
			let vertices = job.getVertices();
			vertices.forEach((vertex) => {
				console.log('iterating over vertex:');
				console.log(vertex);
				let rank = vertex.getRank();
				this.#nodes[rank].setToJobTreeVertex(vertex, job);
				let parent = job.getParent(vertex.getTreeIndex());
				if (parent) {
					console.log(parent);
					console.log('found a parent with index: ' + parent.getRank());
					let parentCoords = this.#getCoords(parent.getRank());
					console.log('parentcoords');
					console.log(parentCoords);
					this.#connections[rank].useConnection(
						vertex,
						parentCoords.getX(),
						parentCoords.getY(),
						job
					);
				}
			});
		});
	}
}
