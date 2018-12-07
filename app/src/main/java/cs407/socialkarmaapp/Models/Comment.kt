package cs407.socialkarmaapp.Models

class Comment() {
    var postCommentId: String = ""
    var author: String = ""
    var authorName: String = ""
    var postId: String = ""
    var comment: String = ""
    var timestamp: Int = 0
    var votes: Int = 0
    var voted: Int = 0

    constructor(postCommentId: String, author: String, authorName: String, postId: String, comment: String, timestamp: Int, votes: Int, voted: Int) : this() {
        this.postCommentId = postCommentId
        this.author = author
        this.authorName = authorName
        this.postId = postId
        this.comment = comment
        this.timestamp = timestamp
        this.votes = votes
        this.voted = voted
    }
}