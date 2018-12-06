package cs407.socialkarmaapp.Models

class Message() {
    var message: String = ""
    var timestamp: Long = 0
    var userId: String = ""

    constructor(message: String, timestamp: Long, userId: String): this() {
        this.message = message
        this.timestamp = timestamp
        this.userId = userId
    }
}