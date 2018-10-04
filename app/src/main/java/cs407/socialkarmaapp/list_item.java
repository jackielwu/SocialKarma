package cs407.socialkarmaapp;

public class list_item {
    int vote_num, comment_num;
    String name, context;

    public list_item(int vote_num, int comment_num, String name, String context) {
        this.name = name;
        this.context = context;
        this.vote_num = vote_num;
        this.comment_num = comment_num;
    }

    public int getVote_num() {
        return vote_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public String getName() {
        return name;
    }

    public String getContext() {
        return context;
    }


}
