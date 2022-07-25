export class AppError {
    #message;
    #type;
    constructor(message, type) {
        this.#message = message;
        this.#type = type;
    }

    getMessage() {
        return this.#message;
    }

    getType() {
        return this.#type;
    }
}
