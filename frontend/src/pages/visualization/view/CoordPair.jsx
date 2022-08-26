/**
 * This clas represent a two-dimensional coordinate.
 * 
 * @author Simon Giek
 */
export class CoordPair {
    #x; #y
    /**
     * The constructor.
     *
     * @param {float} x - The x coordinate.
     * @param {float} y - The y coordinate.
     */
    constructor(x, y) {
        this.#x = x;
        this.#y = y;
    }
    
    /**
     * Getter for the x coordinate.
     *
     * @returns {float} The x coordinate.
     */
    getX() {
        return this.#x;
    }

    /**
     * Getter for the y coordinate.
     *
     * @returns {float} The y coordinate.
     */
    getY() {
        return this.#y;
    }
}
