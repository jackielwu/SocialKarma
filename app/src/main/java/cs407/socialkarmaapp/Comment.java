package cs407.socialkarmaapp;

public class Comment {
    private String author, authorName, comment, postId;
    private int timestamp, vote;

    public Comment() {

    }

    public String getAuthor() {
        return author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getComment() {
        return comment;
    }

    public String getPostId() {
        return postId;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getVote() {
        return vote;
    }
}
