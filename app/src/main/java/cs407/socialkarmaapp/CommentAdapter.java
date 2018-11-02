package cs407.socialkarmaapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 4/17/2018.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mCtx;
    private List<Comment> commentList;

    public CommentAdapter(Context mCtx, List<Comment> commentList) {
        this.mCtx = mCtx;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recycleview_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
//        holder.textViewAuthor.setText(comment.getAuthor());
        holder.textViewAuthorName.setText(comment.getAuthorName());
        holder.textViewComment.setText(comment.getComment());
        holder.textViewPostId.setText(comment.getPostId());
        holder.textViewTimestamp.setText( (comment.getTimestamp()+"") );
        holder.textViewVote.setText( (comment.getVote()+"") );
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAuthor, textViewAuthorName, textViewComment, textViewPostId, textViewTimestamp, textViewVote;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            //textViewAuthor = itemView.findViewById(R.id.text_view_author);
            textViewVote = itemView.findViewById(R.id.text_view_vote);
            textViewComment = itemView.findViewById(R.id.text_view_Comment);
            textViewAuthorName = itemView.findViewById(R.id.text_view_authorName);
            textViewPostId = itemView.findViewById(R.id.text_view_PostId);
            textViewTimestamp = itemView.findViewById(R.id.text_view_Timestamp);
        }
    }
}
