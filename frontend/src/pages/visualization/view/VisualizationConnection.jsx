export class VisualizationConnection {
    #line;
    #depth = 0;
    #defaultOpacity = 0;
    #defaultWidth = 3;
    constructor(line) {
        this.#line = line;
    }

    useConnection(jtv, x, y, job) {
        this.#depth = jtv.getDepth()
        this.#line.vertices[1].x = x;
        this.#line.vertices[1].y = y;
        this.#line.stroke = job.getColor(); 
        this.#line.linewidth = this.#defaultWidth; 
        this.#defaultOpacity = Math.max(1- 0.15*this.#depth, 0.1);
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
        if (this.#defaultOpacity == 0) {
            return
        }
        let dif = Math.abs(otherDepth - this.#depth);
        this.#line.opacity= Math.max(0.8 - dif * 0.1, 0.1)
    }

    setWidthBasedOnDepth() {
        this.#line.linewidth = Math.max(15  - 1.5 * this.#depth, 1)
        
    }

}
