
import {AppError} from '../../../context/AppError';

/**
 * This class represents a vertex in the binary tree of a job.
 *
 * @author Simon Giek
 */
export class JobTreeVertex {


    #depth;
    #rank;
    #treeIndex;
    /**
     * The Constructor.
     *
     * @param {int} rank - The rank on which this vertex is processing.
     * @param {int} treeIndex - The index of the vertex in the binary tree. 
     * @throws {AppError} - If either the rank or the treeIndex is negative.
     */
    constructor(rank, treeIndex) {
        if (rank < 0) {
            throw new AppError('the rank can not be negative') 
        }
        if (treeIndex < 0) {
            throw new AppError('the treeIndex can not be negative')
        }
        this.#rank = rank;
        this.#treeIndex = treeIndex;
        this.#depth = Math.trunc(Math.log2(this.#treeIndex + 1))
    }
    
    /**
     * Getter for the depth of the vertex in the binary tree.
     *
     * @returns {int} The depth of the vertex.
     */
    getDepth() {
        return this.#depth;
    }

    /**
     * Getter for the rank of the vertex.
     *
     * @returns {int} The rank of the vertex.
     */
    getRank() {
        return this.#rank;
    }
   
    /**
     * Getter for the tree index of the vertex.
     *
     * @returns {int} The tree index of the vertex.
     */
    getTreeIndex() {
        return this.#treeIndex;
    }
}
