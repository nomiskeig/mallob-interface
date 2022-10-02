import Two from 'two.js';
import { VisualizationNode } from './VisualizationNode';
import { CoordPair } from './CoordPair';
import { VisualizationConnection } from './VisualizationConnection';
import {JobUpdateListener} from '../model/JobUpdateListener'
/**
 * This class displays the jobs.
 *
 * @extends JobUpdateListener
 * @author Simon Giek
 */
export class Visualization extends JobUpdateListener {
	#processes;
	#two;
	#jobStorage;
	#nodes;
	#connections;
	#canvas;
	#hoveredRank;
	#clickedRank = null;
	#onClick;
    /**
     * The constructor.
     *
     * @param {HTMLElement} canvas - The element on which the visualization is rendered.
     * @param {Method} update - The update method from the VisualizationPageManager.
     * @param {int} processes - The amount of processes to display.
     * @param {JobStorage} jobStorage - The instance of the jobStorage.
     * @param {Method} onClick - The onClick method from the VisualizationPageManager.
     */
	constructor(canvas, update, processes, jobStorage, onClick) {
        super();
		this.#two = new Two({ fitted: true });
		this.#two.appendTo(canvas);
		this.#nodes = [];
		this.#connections = [];
		this.#canvas = canvas;
		this.#processes = processes;
		this.#jobStorage = jobStorage;
		this.#onClick = onClick;

		new ResizeObserver((entries) => {
			entries.forEach((entry) => {
				this.#two.width = entry.contentRect.width;
				let desiredHeight = '600px';
                canvas.style.height = desiredHeight
				this.#two.height = desiredHeight;
                // update location of nodes and lines
                for (let i = 0; i < this.#processes; i++) {
                    let coords = this.#getCoords(i);
                    this.#nodes[i].setCoords(coords.getX(), coords.getY())
                    let connection = this.#connections[i];
                    let otherCoords = connection.getOtherRank() ? this.#getCoords(connection.getOtherRank()) : null;
                    let otherX = otherCoords ? otherCoords.getX() : 0;
                    let otherY = otherCoords ? otherCoords.getY() : 0;

                    connection.setCoords(coords.getX(), coords.getY(), otherX, otherY)
                }

			});
		}).observe(document.querySelector('.visualizationCanvas'));
		canvas.addEventListener('mousedown', (event) => {
			if (event.target.tagName === 'path') {
				return;
			}
			onClick(null, null);
			this.#clickedRank = null;
			this.onHoverLeave();
		});
		this.#clickedRank = null;
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
					this.#hoveredRank = i;
					this.onHoverEnter(i);
				}
			);
		}
		// set the correct height of the canvas
		let desiredHeight = '600px';
		canvas.style.height = desiredHeight;
        this.#two.height = desiredHeight;
		this.#two.bind('update', () => update());
		this.#two.play();
	}

    /**
     * Calculates the position of the given rank and returns it.
     *
     * @param {int} rank - The rank to get the position of.
     * @returns {CoordPair} An instance of CoordPair with the coordinates.
     */
	#getCoords(rank) {
        let radius = 200;
		//let perRow = Math.floor((this.#canvas.clientWidth - 2 * margin) / distance);
		//let restPerRow = this.#canvas.clientWidth - distance * (perRow - 1);
		//let xPos = restPerRow / 2 + distance * (rank % perRow);
		//let yPos = 20 + distance * Math.floor(rank / perRow);
        let xPos = this.#canvas.clientWidth / 2 + Math.cos(2*Math.PI*rank/this.#processes) * radius;
        let yPos = this.#canvas.clientHeight / 2 + Math.sin(2*Math.PI*rank/this.#processes) * radius;
		return new CoordPair(xPos, yPos);
	}
    /**
     * Updates the visualization in such a way that the given rank is highlighted.
     *
     * @param {int} rank - The rank to highlight.
     */
	onHoverEnter(rank) {
		if (this.#clickedRank !== null && this.#clickedRank !== rank) {
			return;
		}
		let jobID = this.#nodes[rank].getJobID();
		let treeIndex = this.#nodes[rank].getTreeIndex();
		if (!this.#clickedRank) {
			this.#hoveredRank = rank;
		}
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
    /**
     * Resets the highlighting.
     *
     */
	onHoverLeave() {
		if (this.#clickedRank !== null) {
			return;
		}
		for (let i = 0; i < this.#processes; i++) {
			this.#nodes[i].resetHover();
			this.#connections[i].resetHover();
		}
		this.#hoveredRank = null;
	}

	update(job, updatedTreeIndex, add, justForColor) {
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
			if (rank === this.#clickedRank && !justForColor) {
				this.#onClick(job.getJobID(), vertex.getTreeIndex());
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
			if (rank === this.#clickedRank) {
				this.#onClick(null, null);
			}
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
		if (this.#clickedRank === null) {
			return;
		}
		let nodeClicked = this.#nodes[this.#clickedRank];
		this.#onClick(nodeClicked.getJobID(), nodeClicked.getTreeIndex());
	}

    /**
     * Stops the instance of two.js.
     *
     */
	stop() {
		this.#two.pause();
	}
}
