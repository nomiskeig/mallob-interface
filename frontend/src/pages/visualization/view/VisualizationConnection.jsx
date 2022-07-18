export class VisualizationConnection {
    #line;
    #depth;
    #defaultOpacity;
    #defaultWidth = 3;
    constructor(line) {
        this.#line = line;
        this.#defaultOpacity = 0;
    }

    useConnection(jtv, x, y, job) {
        console.log(this.#line.vertices) 
        this.#depth = jtv.getDepth()
        this.#line.vertices[1].x = x;
        this.#line.vertices[1].y = y;
        this.#line.stroke = job.getColor(); 
        this.#line.linewidth = this.#defaultWidth; 
        this.#defaultOpacity = 1- 0.15*this.#depth;
        this.#line.opacity = this.#defaultOpacity;
    }
    reset() {
        this.#line.noStroke();

    }

    hideForHover() {
        this.#line.opacity = 0;
    }

    resetHover() {
        this.#line.opacity = this.#defaultOpacity;
        this.#line.linewidth = this.#defaultWidth;
    }

    updateOpacityForHover(otherDepth) {
        let dif = Math.abs(otherDepth - this.#depth);

        console.log(0.8 - dif * 0.1)
        this.#line.opacity= 0.8 - dif * 0.1
    }

    setWidthBasedOnDepth() {
        this.#line.linewidth = 15  - 1.5 * this.#depth 
        
    }

}
