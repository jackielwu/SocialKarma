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

enum class SearchResultsType {
    POST, USER
}

class SearchResultsAdapter(private var type: SearchResultsType, private var results: MutableList<Any>, private val context: Context, private val delegate: PostAdapterDelegate?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setResults(newResults: MutableList<Any>) {
        this.results = newResults
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return results.size
    }

    override fun getItemViewType(position: Int): Int {
        when (type) {
            SearchResultsType.POST -> {
                return 0
            }
            SearchResultsType.USER -> {
                return 1
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        when (p1) {
            0 -> {
                val cellForRow = layoutInflater.inflate(R.layout.list_item, p0, false)
                return PostSearchViewHolder(cellForRow)

            }
            else -> {
                val cellForRow = layoutInflater.inflate(R.layout.user_row, p0, false)
                return UserSearchViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0.itemViewType) {
            0 -> {
                (p0 as PostSearchViewHolder).setupView(results, p1, context, delegate)
                (p0 as PostSearchViewHolder).didSelectRow(results, p1, context)
            }
            else -> {
                (p0 as UserSearchViewHolder).setupView(results, p1)
                (p0 as UserSearchViewHolder).didSelectRow(results, p1, context)
            }
        }
    }
}

class PostSearchViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(results: List<Any>, index: Int, context: Context, delegate: PostAdapterDelegate?) {
        val post: Post = results.get(index) as Post
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

        authorTextView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            val userId = post.author
            intent.putExtra(UserProfileActivity.EXTRA_USER_PROFILE_ID, userId)
            context.startActivity(intent)
        }

        upVoteButton.setOnClickListener {
            delegate?.upVoteButtonClicked(post)
        }
        downVoteButton.setOnClickListener {
            delegate?.downVoteButtonClicked(post)
        }
    }

    fun didSelectRow(posts: List<Any>, index: Int, context: Context) {
        this.view.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            val post = posts.get(index) as Post
            intent.putExtra(PostsFragment.EXTRA_POST_OBJ, post)
            context.startActivity(intent)
        }
    }
}

class UserSearchViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(results: List<Any>, index: Int) {
        val user: User = results.get(index) as User
        val usernameTextView = view.findViewById<TextView>(R.id.textView_user_row)
        usernameTextView.text = user.username
    }

    fun didSelectRow(users: List<Any>, index: Int, context: Context) {
        this.view.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            val user = users.get(index) as User
            intent.putExtra(UserProfileActivity.EXTRA_USER_PROFILE_ID, user.uid)
            context.startActivity(intent)
        }
    }
}