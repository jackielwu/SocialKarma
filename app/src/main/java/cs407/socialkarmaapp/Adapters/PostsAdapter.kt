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
    fun upVoteButtonClicked(post: Post)
    fun downVoteButtonClicked(post: Post)
    fun deletePost(post: Post, atIndex: Int)
}

class PostsAdapter(private var posts: MutableList<Post>, private val context: Context, private val delegate: PostAdapterDelegate, private val headerDelegate: PostHeaderDelegate, private val showSearchButton: Boolean, private val showDeletePostButton: Boolean, private var emptyContentType: EmptyContentViewHolder.EmptyContentType): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var sortby = 0
    fun setPosts(newPosts: MutableList<Post>) {
        this.posts = newPosts
        this.notifyDataSetChanged()
    }

    fun setType(newType: EmptyContentViewHolder.EmptyContentType) {
        this.emptyContentType = newType
        this.notifyDataSetChanged()
    }

    fun sortPosts(sortBy: Int) {
        when (sortBy) {
            0 -> {
                this.posts.sortWith(Comparator { o1, o2 ->
                    o2.timestamp - o1.timestamp
                })
                this.sortby = 0
                this.notifyDataSetChanged()
            }
            1 -> {
                this.posts.sortWith(Comparator { o1, o2 ->
                    o2.votes - o1.votes
                })
                this.sortby = 1
                this.notifyDataSetChanged()
            }
        }
    }

    fun addToPosts(newPosts: List<Post>) {
        val index = this.posts.size
        this.posts.addAll(newPosts)
        this.notifyItemRangeInserted(index, newPosts.size)
    }

    fun removePost(atIndex: Int) {
        this.posts.removeAt(atIndex)
        this.notifyItemRemoved(atIndex)
    }

    override fun getItemCount(): Int {
        when (emptyContentType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                return 1 + posts.size
            }
            else -> {
                return 1
            }
        }
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
        when (emptyContentType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                when (viewType) {
                    0 -> {
                        val layoutInflater = LayoutInflater.from(parent.context)
                        val cellForRow = layoutInflater.inflate(R.layout.post_header_row, parent, false)
                        when (sortby) {
                            0 -> {
                                cellForRow.findViewById<Button>(R.id.button_post_sortby).setText("Sort By: {gmd-access-time}")
                            }
                            1 -> {
                                cellForRow.findViewById<Button>(R.id.button_post_sortby).setText("Sort By: {gmd-thumb-up}")
                            }
                        }
                        return CommentHeaderViewHolder(cellForRow)
                    }
                    else -> {
                        val layoutInflater = LayoutInflater.from(parent.context)
                        val cellForRow = layoutInflater.inflate(R.layout.list_item, parent, false)
                        return PostViewHolder(cellForRow)
                    }
                }
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.empty_content_row, parent, false)
                return EmptyContentViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (emptyContentType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                when (p0.itemViewType) {
                    0 -> {
                        val viewHolder = p0 as CommentHeaderViewHolder
                        val sortByButton = viewHolder.view.findViewById<Button>(R.id.button_post_sortby)
                        val searchButton = viewHolder.view.findViewById<Button>(R.id.button_post_search)
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
                        if (showSearchButton) {
                            searchButton.visibility = View.VISIBLE
                            searchButton.setOnClickListener {
                                val intent = Intent(context, SearchActivity::class.java)
                                intent.putExtra(SearchActivity.EXTRA_RESULT_TYPE, 0)
                                context.startActivity(intent)
                            }
                        } else {
                            searchButton.visibility = View.GONE
                        }
                    }
                    else -> {
                        (p0 as PostViewHolder).setupView(posts, p1 - 1, showDeletePostButton, delegate, context)
                        (p0 as PostViewHolder).didSelectRow(posts, p1 - 1, context)
                    }
                }
            }
            EmptyContentViewHolder.EmptyContentType.ERROR -> {
                (p0 as EmptyContentViewHolder).setupView("There seemed to be an error loading posts.\nPlease try again.", emptyContentType)
            }
            EmptyContentViewHolder.EmptyContentType.EMPTY -> {
                (p0 as EmptyContentViewHolder).setupView("There seems to be no posts\nin the area right now.\n\nAdd one of your own using the '+' button!", emptyContentType)
            }
        }
    }
}

class PostViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(posts: List<Post>, index: Int, showDeletePostButton: Boolean, delegate: PostAdapterDelegate, context: Context) {
        val post = posts.get(index)
        val titleTextView = view.findViewById<TextView>(R.id.textView_post_title)
        val authorTextView = view.findViewById<TextView>(R.id.textView_post_author)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_comment_description)
        val upVoteButton = view.findViewById<Button>(R.id.button_post_upvote)
        val downVoteButton = view.findViewById<Button>(R.id.button_post_downvote)
        val upVoteCountTextView = view.findViewById<TextView>(R.id.textView_post_upvote_count)
        val commentCountTextView = view.findViewById<TextView>(R.id.textView_post_comment_count)
        val deleteButton = view.findViewById<Button>(R.id.button_post_delete)

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
            delegate.upVoteButtonClicked(post)
        }
        downVoteButton.setOnClickListener {
            delegate.downVoteButtonClicked(post)
        }

        if (showDeletePostButton) {
            deleteButton.visibility = View.VISIBLE
            deleteButton.setOnClickListener {
                delegate.deletePost(post, index)
            }
        } else {
            deleteButton.visibility = View.GONE
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