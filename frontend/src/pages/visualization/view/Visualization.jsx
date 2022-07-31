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
	#hoveredRank;
    #clickedRank;
    #onClick
	constructor(canvas, update, processes, jobStorage, onClick) {
		this.#two = new Two({ fitted: true });
		this.#two.appendTo(canvas);
		this.#nodes = [];
		this.#connections = [];
		this.#canvas = canvas;
		this.#processes = processes;
		this.#jobStorage = jobStorage;
        this.#onClick = onClick;
		let connectionGroup = this.#two.makeGroup();
		let nodeGroup = this.#two.makeGroup();
		let textGroup = this.#two.makeGroup();
		for (let i = 0; i < processes; i++) {
			let coords = this.#getCoords(i);
			let newNode = this.#two.makeCircle(coords.getX(), coords.getY(), 6);
			nodeGroup.add(newNode);
			let newText = this.#two.makeText(i, coords.getX(), coords.getY() - 14);
			textGroup.add(newText);
			this.#nodes[i] = new VisualizationNode(newNode, newText, i);
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
			this.#nodes[i].registerCallbacks(
				this.onHoverEnter.bind(this),
				this.onHoverLeave.bind(this),
                (jobID, treeIndex) => {
                    onClick(jobID, treeIndex);
                    this.#clickedRank = i;
                }
			);
		}
        // set the correct height of the canvas
		let desiredHeight = this.#getCoords(this.#processes).getY() + 10 + 'px';
		canvas.style.height = desiredHeight;
		this.#two.height = desiredHeight;
		this.#two.bind('update', () => update());
		this.#two.play();
	}

	#getCoords(rank) {
		let margin = 10;
		let distance = 40;
		let perRow = Math.floor((this.#canvas.clientWidth - 2 * margin) / distance);
		let restPerRow = this.#canvas.clientWidth - distance * (perRow - 1);
		let xPos = restPerRow / 2 + distance * (rank % perRow);
		let yPos = 20 + distance * Math.floor(rank / perRow);
		return new CoordPair(xPos, yPos);
	}
	onHoverEnter(rank) {
		let jobID = this.#nodes[rank].getJobID();
		let treeIndex = this.#nodes[rank].getTreeIndex();
		this.#hoveredRank = rank;
		for (let i = 0; i < this.#processes; i++) {
			this.#nodes[i].updateOpacityForHover();
			this.#connections[i].hideForHover();
		}
		if (jobID === null) {
			for (let i = 0; i < this.#processes; i++) {
				this.#nodes[i].resetHover();
			}
			this.#nodes[this.#hoveredRank].showTreeIndexForHover();
			this.#jobStorage.getAllJobs().forEach((job) => {
				job.getVertices().forEach((vertex) => {
					this.#nodes[vertex.getRank()].updateOpacityForHover();
				});
			});
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
		this.#hoveredRank = null;
	}

	update(job, updatedTreeIndex, add) {
		let vertex = job.getVertex(updatedTreeIndex);
		let parentVertex = job.getParent(updatedTreeIndex);
		let leftChild = job.getLeftChild(updatedTreeIndex);
		let rightChild = job.getRightChild(updatedTreeIndex);
		let rank = vertex.getRank();
		let vertexCoords = this.#getCoords(rank);
		if (add) {
            // show node
			this.#nodes[rank].setToJobTreeVertex(vertex, job);
            
            // call the onClick function if ranks match so correct info is shown
            if (rank === this.#clickedRank) {
                this.#onClick(job.getJobID(), vertex.getTreeIndex(), rank);
            }
            
            // update connection from left child to vertex itself
			if (leftChild) {
				let connection = this.#connections[leftChild.getRank()];
				connection.useConnection(
					vertex,
					vertexCoords.getX(),
					vertexCoords.getY(),
					job
				);
			}
			// update connection from right child to vertex itself
			if (rightChild) {
				let connection = this.#connections[rightChild.getRank()];
				connection.useConnection(
					vertex,
					vertexCoords.getX(),
					vertexCoords.getY(),
					job
				);
			}
			// update connection from vertex to parent
			if (parentVertex) {
				let parentCoords = this.#getCoords(parentVertex.getRank());
				let connection = this.#connections[rank];
				connection.useConnection(
					parentVertex,
					parentCoords.getX(),
					parentCoords.getY(),
					job
				);
			}
		} else {
			this.#nodes[rank].reset();
			if (leftChild) {
				this.#connections[leftChild.getRank()].reset();
			}
			if (rightChild) {
				this.#connections[rightChild.getRank()].reset();
			}
			this.#connections[rank].reset();
		}
		if (this.#hoveredRank) {
			this.onHoverEnter(this.#hoveredRank);
		}
	}

	totalUpdate(jobs) {
        for (let i = 0; i < this.#processes; i++) {
            this.#nodes[i].reset();
            this.#connections[i].reset();
        }
        this.#onClick(null, null);
		jobs.forEach((job) => {
			let vertices = job.getVertices();
			vertices.forEach((vertex) => {
				let rank = vertex.getRank();
				this.#nodes[rank].setToJobTreeVertex(vertex, job);
				let parent = job.getParent(vertex.getTreeIndex());
				if (parent) {
					let parentCoords = this.#getCoords(parent.getRank());
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

    stop() {
        this.#two.pause();
    }
}
