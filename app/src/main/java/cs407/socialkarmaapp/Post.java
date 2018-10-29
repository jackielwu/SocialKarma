package cs407.socialkarmaapp;

import java.io.Serializable;

public class Post implements Serializable {
    private String postId;
    private int upvoteCount, commentCount;
    private String title, content, author, authorName;
    private int timestamp;

    public Post(String postId, int vote_num, int comment_num, String name, String content, String author, int timestamp, String authorName) {
        this.postId = postId;
        this.title = name;
        this.content = content;
        this.author = author;
        this.authorName = authorName;
        this.upvoteCount = vote_num;
        this.commentCount = comment_num;
        this.timestamp = timestamp;
    }

    public int getVote_num() {
        return upvoteCount;
    }

    public String getVote_num_string() {
        return upvoteCount+"";
    }
    public int getComment_num() {
        return commentCount;
    }

    public String getPostId() {
        return postId;
    }
    public String getComment_num_String() {
        return "comment " + commentCount;
    }
    public String getName() {
        return title;
    }
    public String getDescription() {
        return content;
    }
    public String getAuthor() {
        return author;
    }
    public String getAuthorName() {
        return authorName;
    }
}
