export class VisualizationNode {
	#circle;
	#text;
	#jobID;
	#treeIndex;
    #defaultRadius = 6;
	constructor(circle, text) {
		this.#circle = circle;
		this.#text = text;
        this.#jobID = null;
        this.#treeIndex = null;
        
        
	}

	reset() {
		this.#circle.fill = '#C4C4C4';
		this.#circle.linewidth = 0;
		this.#jobID = null;
		this.#treeIndex = null;
        this.#circle.radius = this.#defaultRadius;
        this.#text.opacity = 0;
	}

    registerHoverCallbacks(onHoverEnter, onHoverLeave) {

        this.#circle._renderer.elem.addEventListener('mouseover', () => {
            onHoverEnter(this.#jobID, this.#treeIndex);
                 
            if (!this.#jobID) {
                this.#circle.opacity = 1;

            }
        })
        this.#circle._renderer.elem.addEventListener('mouseout', () => {
            onHoverLeave();
        })
    }


     setToJobTreeVertex(jtv, job) {
        this.#circle.fill = job.getColor();
        //this.#circle.radius = 10 * 1/( 1+ jtv.getDepth())
        this.#circle.radius = Math.max(10 - 2 * jtv.getDepth(), 4)
        this.#jobID = job.getJobID();
        this.#treeIndex = jtv.getTreeIndex();
    }

    updateOpacityForHover() {
        this.#circle.opacity = 0.2

    }

    resetHover() {
        this.#circle.opacity = 1
        this.#text.opacity = 0;

    }

    showTreeIndexForHover() {
        this.#text.opacity = 1;
    }
}
