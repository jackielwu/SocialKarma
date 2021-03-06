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
import android.widget.Toast
import cs407.socialkarmaapp.Models.Comment
import cs407.socialkarmaapp.Post
import cs407.socialkarmaapp.R
import cs407.socialkarmaapp.R.id.context
import cs407.socialkarmaapp.R.id.sort
import cs407.socialkarmaapp.UserProfileActivity
import org.w3c.dom.Text

enum class SortBy {
    LATEST, TOP
}

interface PostHeaderDelegate {
    fun sortByButtonClicked(sortBy: SortBy)
}

interface CommentAdapterDelegate {
    fun upVoteButtonClicked(comment: Comment)
    fun downVoteButtonClicked(comment: Comment)
    fun deleteComment(comment: Comment, atIndex: Int)
}

class PostDetailAdapter(private var post: Post?, private var comments: MutableList<Comment>, private val delegate: PostAdapterDelegate, private val headerDelegate: PostHeaderDelegate, private val commentDelegate: CommentAdapterDelegate, private val context: Context, private val showDeleteCommentButton: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var sortby: Int = 0

    fun setComments(newComments: MutableList<Comment>) {
        this.comments = newComments
        this.notifyDataSetChanged()
    }

    fun sortComments(sortBy: Int) {
        when (sortBy) {
            0 -> {
                this.comments.sortWith(Comparator { o1, o2 ->
                    o2.timestamp - o1.timestamp
                })
                this.sortby = 0
                this.notifyDataSetChanged()
            }
            1 -> {
                this.comments.sortWith(Comparator { o1, o2 ->
                    o2.votes - o1.votes
                })
                this.sortby = 1
                this.notifyDataSetChanged()
            }
        }
    }

    fun addToComments(newComments: List<Comment>) {
        val index = this.comments.size
        this.comments.addAll(newComments)
        this.notifyItemRangeInserted(index, newComments.size)
    }

    override fun getItemCount(): Int {
        this.post?.let {
            if (comments.size == 0) {
                return 2
            } else {
                return 2 + comments.size
            }
        } ?: run {
            return 0
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (position) {
            0 -> {
                return 0
            }
            1 -> {
                return 1
            }
            else -> {
                return 2
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.list_item, parent, false)
                return PostDetailViewHolder(cellForRow)
            }
            1 -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.post_header_row, parent, false)

                return CommentHeaderViewHolder(cellForRow)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.comment_row, parent, false)
                return CommentViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0.itemViewType) {
            0 -> {
                val viewHolder = p0 as PostDetailViewHolder
                this.post?.let {
                    viewHolder.setupView(it, delegate, context)
                }
            }
            1 -> {
                val viewHolder = p0 as CommentHeaderViewHolder
                val sortByButton = viewHolder.view.findViewById<Button>(R.id.button_post_sortby)
                when (sortby) {
                    0 -> {
                        sortByButton.setText("Sort By: {gmd-access-time}")
                    }
                    1 -> {
                        sortByButton.setText("Sort By: {gmd-thumb-up}")
                    }
                }
                sortByButton.setOnClickListener {
                    headerDelegate.sortByButtonClicked(SortBy.LATEST)
                }
            }
            else -> {
                val viewHolder = p0 as CommentViewHolder
                viewHolder.setupView(comments[p1 - 2], p1 - 2, showDeleteCommentButton, commentDelegate, context)
            }
        }
    }
}

class PostDetailViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(post: Post, delegate: PostAdapterDelegate, context: Context) {
        val titleTextView = view.findViewById<TextView>(R.id.textView_post_title)
        val authorTextView = view.findViewById<TextView>(R.id.textView_post_author)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_comment_description)
        val upVoteCountTextView = view.findViewById<TextView>(R.id.textView_post_upvote_count)
        val commentCountTextView = view.findViewById<TextView>(R.id.textView_post_comment_count)

        val upVoteButton = view.findViewById<Button>(R.id.button_post_upvote)
        val downVoteButton = view.findViewById<Button>(R.id.button_post_downvote)

        titleTextView.text = post.title
        authorTextView.text = post.authorName
        descriptionTextView.text = post.content
        upVoteCountTextView.text = "{gmd-thumb-up} " + post.upvoteCount
        commentCountTextView.text = "{gmd-mode-comment} " + post.commentCount

        authorTextView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            val userId = post.author
            intent.putExtra(UserProfileActivity.EXTRA_USER_PROFILE_ID, userId)
            context.startActivity(intent)
        }

        if (post.voted > 0) {
            upVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_rounded)
            downVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
        } else if (post.voted < 0) {
            downVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_rounded)
            upVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
        } else {
            upVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
            downVoteButton.background = ContextCompat.getDrawable(context, R.drawable.vote_button_neutral)
        }

        upVoteButton.setOnClickListener {
            delegate.upVoteButtonClicked(post)
        }

        downVoteButton.setOnClickListener {
            delegate.downVoteButtonClicked(post)
        }
    }
}

class CommentHeaderViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}

class CommentViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(comment: Comment, index: Int, showDeleteCommentButton: Boolean, delegate: CommentAdapterDelegate, context: Context) {
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