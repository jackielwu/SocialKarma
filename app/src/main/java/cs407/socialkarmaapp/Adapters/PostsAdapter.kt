package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cs407.socialkarmaapp.*

interface PostAdapterDelegate {
    fun upVoteButtonClicked(postId: String)
    fun downVoteButtonClicked(postId: String)
}

class PostsAdapter(private var posts: MutableList<Post>, private val context: Context, private val delegate: PostAdapterDelegate, private val headerDelegate: PostHeaderDelegate): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setPosts(newPosts: MutableList<Post>) {
        this.posts = newPosts
        this.notifyDataSetChanged()
    }

    fun sortPosts(sortBy: Int) {
        when (sortBy) {
            0 -> {
                this.posts.sortWith(Comparator { o1, o2 ->
                    o2.timestamp - o1.timestamp
                })
                this.notifyDataSetChanged()
            }
            1 -> {
                this.posts.sortWith(Comparator { o1, o2 ->
                    o2.votes - o1.votes
                })
                this.notifyDataSetChanged();
            }
        }
    }

    fun addToPosts(newPosts: List<Post>) {
        val index = this.posts.size
        this.posts.addAll(newPosts)
        this.notifyItemRangeInserted(index, newPosts.size)
    }

    override fun getItemCount(): Int {
        return 1 + posts.size
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
                val cellForRow = layoutInflater.inflate(R.layout.list_item, parent, false)
                return PostViewHolder(cellForRow)
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
                (p0 as PostViewHolder).setupView(posts, p1 - 1, delegate)
                (p0 as PostViewHolder).didSelectRow(posts, p1 - 1, context)
            }
        }
    }
}

class PostViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(posts: List<Post>, index: Int, delegate: PostAdapterDelegate) {
        val post = posts.get(index)
        val titleTextView = view.findViewById<TextView>(R.id.textView_post_title)
        val authorTextView = view.findViewById<TextView>(R.id.textView_post_author)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_comment_description)
        val upVoteButton = view.findViewById<Button>(R.id.button_post_upvote)
        val downVoteButton = view.findViewById<Button>(R.id.button_post_downvote)
        val upVoteCountTextView = view.findViewById<TextView>(R.id.textView_post_upvote_count)
        val commentCountTextView = view.findViewById<TextView>(R.id.textView_post_comment_count)

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

    fun didSelectRow(posts: List<Post>, index: Int, context: Context) {
        this.view.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            val post = posts.get(index)
            intent.putExtra(PostsFragment.EXTRA_POST_OBJ, post)
            context.startActivity(intent)
        }
    }
}