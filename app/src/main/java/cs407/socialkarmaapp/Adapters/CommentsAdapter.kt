package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cs407.socialkarmaapp.*
import cs407.socialkarmaapp.Models.Comment

class CommentsAdapter(private var comments: MutableList<Comment>, private val context: Context, private val delegate: CommentAdapterDelegate, private val headerDelegate: PostHeaderDelegate, private val showDeleteCommentButton: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setPosts(newComments: MutableList<Comment>) {
        this.comments = newComments
        this.notifyDataSetChanged()
    }

    fun sortPosts(sortBy: Int) {
        when (sortBy) {
            0 -> {
                this.comments.sortWith(Comparator { o1, o2 ->
                    o2.timestamp - o1.timestamp
                })
                this.notifyDataSetChanged()
            }
            1 -> {
                this.comments.sortWith(Comparator { o1, o2 ->
                    o2.votes - o1.votes
                })
                this.notifyDataSetChanged()
            }
        }
    }

    fun addToPosts(newComments: MutableList<Comment>) {
        val index = this.comments.size
        this.comments.addAll(newComments)
        this.notifyItemRangeInserted(index, newComments.size)
    }

    fun removeComment(atIndex: Int) {
        this.comments.removeAt(atIndex)
        this.notifyItemRemoved(atIndex + 1)
    }

    override fun getItemCount(): Int {
        return 1 + comments.size
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> {
                return 0
            }
            else -> {
                return 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.post_header_row, parent, false)
                return CommentHeaderViewHolder(cellForRow)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.comment_row, parent, false)
                return CommentsViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0.itemViewType) {
            0 -> {
                val viewHolder = p0 as CommentHeaderViewHolder
                val sortByButton = viewHolder.view.findViewById<Button>(R.id.button_post_sortby)
                sortByButton.setOnClickListener {
                    headerDelegate.sortByButtonClicked(SortBy.LATEST)
                }
            }
            else -> {
                (p0 as CommentsViewHolder).setupView(comments, p1 - 1, showDeleteCommentButton, delegate, context)
            }
        }
    }
}

class CommentsViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(comments: List<Comment>, index: Int, showDeleteCommentButton: Boolean, delegate: CommentAdapterDelegate, context: Context) {
        val comment = comments.get(index)
        val authorTextView = view.findViewById<TextView>(R.id.textView_comment_author)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_comment_description)
        val upVoteButton = view.findViewById<Button>(R.id.button_comment_upvote)
        val downVoteButton = view.findViewById<Button>(R.id.button_comment_downvote)
        val votesTextView = view.findViewById<TextView>(R.id.textView_comment_upvote_count)
        val deleteButton = view.findViewById<Button>(R.id.button_comment_delete)

        authorTextView.text = comment.authorName
        descriptionTextView.text = comment.comment
        votesTextView.text = "{gmd-thumb-up} " + comment.votes

        authorTextView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            val userId = comment.author
            intent.putExtra(UserProfileActivity.EXTRA_USER_PROFILE_ID, userId)
            context.startActivity(intent)
        }

        if (comment.voted > 0) {
            upVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_rounded)
            downVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
        } else if (comment.voted < 0) {
            downVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_rounded)
            upVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
        } else {
            upVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
            downVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
        }

        upVoteButton.setOnClickListener {
            delegate.upVoteButtonClicked(comment)
        }

        downVoteButton.setOnClickListener {
            delegate.downVoteButtonClicked(comment)
        }

        if (showDeleteCommentButton) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                delegate.deleteComment(comment, index)
            }
        } else {
            deleteButton.visibility = View.GONE
        }
    }

}