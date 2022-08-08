export class BinaryTreeNode {
    #text;
    #circle;
    
    constructor(circle, text) {
        this.#text = text;
        this.#circle = circle;
    }

    setToJobTreeVertex(jtv, job) {
        this.#circle.opacity = 1;
        this.#circle.fill = job.getColor();
        this.#text.opacity = 1;
        this.#circle.opacity = 1
        this.#text.value = jtv.getRank();
		if (job.getOuterColor()) {
			this.#circle.stroke = job.getOuterColor();
			this.#circle.linewidth = '3';
		}
    }

    setX(x) {
        this.#circle.position.x = x;
        this.#text.position.x = x;
    }
    setY(y) {
        this.#circle.position.y = y;
        this.#text.position.y = y + 15;
    }
    reset() {
        this.#circle.opacity = 0;
        this.#text.opacity = 0;
        this.#circle.linewidth = '0'
        this.#circle.radius = 6;

    }

    setClickedOn() {
        this.#circle.radius = 10;
    }

    resetClickedOn() {
        this.#circle.radius = 6;

    }

}
