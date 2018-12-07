package cs407.socialkarmaapp;

import java.io.Serializable;
import java.util.Map;

public class Post implements Serializable {
    private String postId;
    private int votes, commentCount;
    private String title, content, author, authorName;
    private int timestamp;
    private Map<String, Double> coordinates;
    private int voted;

    public Post() {}

    public Post(String postId, int votes, int commentCount, String title, String content, String author, int timestamp, String authorName, Map<String, Double> coordinates, int voted) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.author = author;
        this.authorName = authorName;
        this.votes = votes;
        this.commentCount = commentCount;
        this.timestamp = timestamp;
        this.coordinates = coordinates;
        this.voted = voted;
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
    public int getVoted() { return voted; }
    public Map<String, Double> getCoordinates() { return coordinates; }
    public int getTimestamp() { return timestamp; }
    public int getVotes() { return votes; }
    public void setVotes(int vote) {this.votes = vote; }
    public void setPostId(String postId) {this.postId = postId;}
    public void setVoted(int voted) {this.voted = voted;}
}
