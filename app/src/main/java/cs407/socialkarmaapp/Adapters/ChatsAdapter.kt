package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.intentservice.chatui.ChatView
import cs407.socialkarmaapp.*
import cs407.socialkarmaapp.Models.Chat
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(private var chats: MutableList<Chat>, private var emptyContentType: EmptyContentViewHolder.EmptyContentType, private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setChats(newChats: MutableList<Chat>) {
        this.chats = newChats
        this.notifyDataSetChanged()
    }

    fun addToChats(newChats: List<Chat>) {
        val index = this.chats.size
        this.chats.addAll(newChats)
        this.notifyItemRangeInserted(index, newChats.size)
    }

    fun addChat(newChat: Chat) {
        val index = this.chats.size
        this.chats.add(newChat)
        this.notifyItemRangeInserted(index, 1)
    }

    fun setChat(index: Int) {
        this.notifyItemRangeChanged(index, 1)
    }

    fun removeChats() {
        this.chats.clear()
        this.notifyDataSetChanged()
    }

    fun setType(newType: EmptyContentViewHolder.EmptyContentType) {
        this.emptyContentType = newType
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        when (emptyContentType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                return chats.size
            }
            else -> {
                return 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (emptyContentType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.chat_item, parent, false)
                return ChatViewHolder(cellForRow)
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
                (p0 as ChatViewHolder).setupView(chats, p1)
                (p0 as ChatViewHolder).didSelectRow(chats, p1, context)
            }
            EmptyContentViewHolder.EmptyContentType.ERROR -> {
                (p0 as EmptyContentViewHolder).setupView("There seemed to be an error loading your messages.\nPlease try again.", emptyContentType)
            }
            EmptyContentViewHolder.EmptyContentType.EMPTY -> {
                (p0 as EmptyContentViewHolder).setupView("You don't have any messages right now.\nStart a conversation using the '+' button!", emptyContentType)
            }
        }
    }
}

class ChatViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(chats: List<Chat>, index: Int) {
        val chat = chats.get(index)
        val nameTextView = view.findViewById<TextView>(R.id.textView_message_sender)
        val messageTextView = view.findViewById<TextView>(R.id.textView_message_content)
        val timeTextView = view.findViewById<TextView>(R.id.textView_message_time)

        chat.partner?.username.let {
            nameTextView.text = chat.partner?.username
        }
        messageTextView.text = chat.lastMessage

        val sdf = SimpleDateFormat("MM/dd HH:mm")
        timeTextView.text = sdf.format(Date(chat.lastTimestamp * 1000))
    }

    fun didSelectRow(chats: List<Chat>, index: Int, context: Context) {
        this.view.setOnClickListener {
            val intent = Intent(context, ChatMessagesActivity::class.java)
            val chat = chats.get(index)
            intent.putExtra(MessagesFragment.EXTRA_MESSAGE_PARTNER, chat.partnerId)
            intent.putExtra(MessagesFragment.EXTRA_MESSAGE_PARTNER_NAME, chat.partner?.username)
            intent.putExtra(MessagesFragment.EXTRA_CHAT_ID, chat.chatId)
            intent.putExtra(MessagesFragment.EXTRA_CHAT_OBJECT, chat)
            context.startActivity(intent)
        }
    }
}