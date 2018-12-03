package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import cs407.socialkarmaapp.*
import cs407.socialkarmaapp.Models.Message
import cs407.socialkarmaapp.Models.Chat
import java.text.SimpleDateFormat
import java.util.*

class ChatMessagesAdapter(private var messages: MutableList<Message>, private var chat: Chat, private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setMessages(newMessages: MutableList<Message>) {
        this.messages = newMessages
        this.notifyDataSetChanged()
    }

    fun addToMessages(newMessages: List<Message>) {
        val index = this.messages.size
        this.messages.addAll(newMessages)
        this.notifyItemRangeInserted(index, newMessages.size)
    }

    fun setMessage(index: Int) {
        this.notifyItemRangeChanged(index, 1)
    }

    fun addMessage(newMessage: Message) {
        val index = this.messages.size
        this.messages.add(newMessage)
        this.notifyItemRangeInserted(index, 1)
    }

    fun setChat(newChat: Chat) {
        this.chat = newChat
        this.notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        if (messages[position].userId != FirebaseAuth.getInstance().currentUser?.uid) {
            return 0
        } else {
            return 1
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        when (viewType) {
            0 -> {
                val cellForRow = layoutInflater.inflate(R.layout.chat_message_row_left, parent, false)
                return LeftChatMessageViewHolder(cellForRow)
            }
            else -> {
                val cellForRow = layoutInflater.inflate(R.layout.chat_message_row_right, parent, false)
                return RightChatMessageViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0.itemViewType) {
            0 -> {
                (p0 as LeftChatMessageViewHolder).setupView(messages, p1)
            }
            else -> {
                (p0 as RightChatMessageViewHolder).setupView(messages, chat, p1)
            }
        }
    }
}

class RightChatMessageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(messages: List<Message>, chat: Chat, index: Int) {
        val message = messages.get(index)
        val messageTextView = view.findViewById<TextView>(R.id.textView_message_row_right)
        val messageTimeTextView = view.findViewById<TextView>(R.id.textView_message_row_right_timestamp)

        messageTextView.text = message.message

        val sdf = SimpleDateFormat("MM/dd HH:mm")
        messageTimeTextView.text = sdf.format(Date(message.timestamp * 1000))

        val readReceiptTextView = view.findViewById<TextView>(R.id.textView_message_read_receipt)
        if (index == messages.size - 1) {
            if (chat.isReadReceipt) {
                readReceiptTextView.visibility = View.VISIBLE
            } else {
                readReceiptTextView.visibility = View.GONE
            }
        } else {
            readReceiptTextView.visibility = View.GONE
        }
    }
}

class LeftChatMessageViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(messages: List<Message>, index: Int) {
        val message = messages.get(index)
        val messageTextView = view.findViewById<TextView>(R.id.textView_message_row_left)
        val messageTimeTextView = view.findViewById<TextView>(R.id.textView_message_row_left_timestamp)

        messageTextView.text = message.message

        val sdf = SimpleDateFormat("MM/dd HH:mm")
        messageTimeTextView.text = sdf.format(Date(message.timestamp * 1000))
    }
}