package cs407.socialkarmaapp;

import java.io.Serializable;

public class Post implements Serializable {
    private String postId;
    private int votes, commentCount;
    private String title, content, author, authorName;
    private int timestamp;

    public Post(String postId, int votes, int commentCount, String title, String content, String author, int timestamp, String authorName) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorName = authorName;
        this.votes = votes;
        this.commentCount = commentCount;
        this.timestamp = timestamp;
    }
    public Post() {
        this.postId = "-LPu0HlkFJEMUzfezUCt"; // need to fix. it is uid of a@a.com
    }
    public int getUpvoteCount() {
        return votes;
    }

    public String getup() {
        return votes+"";
    }
    public int getCommentCount() {
        return commentCount;
    }

    public String getPostId() {
        return postId;
    }
    public String getComment_num_String() {
        return "comment " + commentCount;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getAuthor() {
        return author;
    }
    public String getAuthorName() {
        return authorName;
    }
    public int getTimestamp() {return timestamp;}
}
