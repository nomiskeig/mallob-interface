
import {AppError} from '../../../global/errorHandler/AppError';

export class JobTreeVertex {


    #depth;
    #rank;
    #treeIndex;
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
    
    getDepth() {
        return this.#depth;
    }

    getRank() {
        return this.#rank;
    }
   
    getTreeIndex() {
        return this.#treeIndex;
    }
}
