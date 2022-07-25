export class AppError {
    #message;
    constructor(message) {
        this.#message = message;
    }

    getMessage() {
        return this.#message;
    }
}
