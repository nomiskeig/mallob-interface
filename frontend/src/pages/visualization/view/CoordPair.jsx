export class CoordPair {
    #x; #y
    constructor(x, y) {
        this.#x = x;
        this.#y = y;
    }
    
    getX() {
        return this.#x;
    }

    getY() {
        return this.#y;
    }
}
