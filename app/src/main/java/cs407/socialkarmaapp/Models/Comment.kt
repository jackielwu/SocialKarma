package cs407.socialkarmaapp.Models

class Comment(val postCommentId: String, val author: String, val authorName: String, val postId: String, val comment: String, val timestamp: Int, var votes: Int)