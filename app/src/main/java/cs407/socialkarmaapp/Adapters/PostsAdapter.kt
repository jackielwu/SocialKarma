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
import cs407.socialkarmaapp.Models.Meetup

interface PostAdapterDelegate {
    fun upVoteButtonClicked(postId: String)
    fun downVoteButtonClicked(postId: String)
}

class PostsAdapter(private var posts: MutableList<Post>, private val context: Context, private val delegate: PostAdapterDelegate): RecyclerView.Adapter<PostViewHolder>() {
    fun setPosts(newPosts: MutableList<Post>) {
        this.posts = newPosts
        this.notifyDataSetChanged()
    }

    fun addToPosts(newPosts: List<Post>) {
        val index = this.posts.size
        this.posts.addAll(newPosts)
        this.notifyItemRangeInserted(index, newPosts.size)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.list_item, parent, false)
        return PostViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: PostViewHolder, p1: Int) {
        p0.setupView(posts, p1, delegate)
        p0.didSelectRow(posts, p1, context)
    }
}

class PostViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(posts: List<Post>, index: Int, delegate: PostAdapterDelegate) {
        val post = posts.get(index)
        val titleTextView = view.findViewById<TextView>(R.id.textView_post_title)
        val authorTextView = view.findViewById<TextView>(R.id.textView_post_author)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_post_description)
        val upVoteButton = view.findViewById<Button>(R.id.button_post_upvote)
        val downVoteButton = view.findViewById<Button>(R.id.button_post_downvote)
        val upVoteCountTextView = view.findViewById<TextView>(R.id.textView_post_upvote_count)
        val commentCountTextView = view.findViewById<TextView>(R.id.textView_post_comment_count)

        titleTextView.text = post.name
        authorTextView.text = post.authorName
        descriptionTextView.text = post.description
        upVoteCountTextView.text = "{gmd-thumb-up} " + post.vote_num
        commentCountTextView.text = "{gmd-mode-comment} " + post.comment_num

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
            intent.putExtra(PostsFragment.EXTRA_POST, post.postId)
            intent.putExtra(PostsFragment.EXTRA_POST_TITLE, post.name)
            context.startActivity(intent)
        }
    }
}