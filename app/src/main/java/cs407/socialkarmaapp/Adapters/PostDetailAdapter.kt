package cs407.socialkarmaapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cs407.socialkarmaapp.Models.Comment
import cs407.socialkarmaapp.Post
import cs407.socialkarmaapp.R
import org.w3c.dom.Text

enum class SortBy {
    LATEST, OLDEST
}

interface PostHeaderDelegate {
    fun sortByButtonClicked(sortBy: SortBy)
}

interface CommentAdapterDelegate {
    fun upVoteButtonClicked(postId: String)
    fun downVoteButtonClicked(postId: String)
}

class PostDetailAdapter(private var post: Post?, private var comments: MutableList<Comment>, private val delegate: PostAdapterDelegate, private val headerDelegate: PostHeaderDelegate, private val commentDelegate: CommentAdapterDelegate): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
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
                    viewHolder.setupView(it, delegate)
                }
            }
            1 -> {
                val viewHolder = p0 as CommentHeaderViewHolder
                val sortByButton = viewHolder.view.findViewById<Button>(R.id.button_post_sortby)
                sortByButton.setOnClickListener {
                    headerDelegate.sortByButtonClicked(SortBy.LATEST)
                }
            }
            else -> {
                val viewHolder = p0 as CommentViewHolder
                viewHolder.setupView(comments[p1 - 2], commentDelegate)
            }
        }
    }
}

class PostDetailViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(post: Post, delegate: PostAdapterDelegate) {
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

        upVoteButton.setOnClickListener {
            delegate.upVoteButtonClicked(post.postId)
        }

        downVoteButton.setOnClickListener {
            delegate.downVoteButtonClicked(post.postId)
        }
    }
}

class CommentHeaderViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}

class CommentViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(comment: Comment, delegate: CommentAdapterDelegate) {
        val authorTextView = view.findViewById<TextView>(R.id.textView_comment_author)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_comment_description)
        val upVoteButton = view.findViewById<Button>(R.id.button_comment_upvote)
        val downVoteButton = view.findViewById<Button>(R.id.button_comment_downvote)

        authorTextView.text = comment.authorName
        descriptionTextView.text = comment.comment

        upVoteButton.setOnClickListener {
            delegate.upVoteButtonClicked(comment.postCommentId)
        }

        downVoteButton.setOnClickListener {
            delegate.downVoteButtonClicked(comment.postCommentId)
        }
    }
}