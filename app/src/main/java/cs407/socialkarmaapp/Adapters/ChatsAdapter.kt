package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import cs407.socialkarmaapp.*
import cs407.socialkarmaapp.Models.Chat
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(private var chats: MutableList<Chat>, private val context: Context): RecyclerView.Adapter<ChatViewHolder>() {
    fun setChats(newChats: MutableList<Chat>) {
        this.chats = newChats
        this.notifyDataSetChanged()
    }

    fun addToChats(newChats: List<Chat>) {
        val index = this.chats.size
        this.chats.addAll(newChats)
        this.notifyItemRangeInserted(index, newChats.size)
    }

    fun setChat(index: Int) {
        this.notifyItemRangeChanged(index, 1)
    }

    override fun getItemCount(): Int {
        return chats.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: ChatViewHolder, p1: Int) {
        p0.setupView(chats, p1)
        p0.didSelectRow(chats, p1, context)
    }
}

class ChatViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(chats: List<Chat>, index: Int) {
        val chat = chats.get(index)
        val nameTextView = view.findViewById<TextView>(R.id.textView_message_sender)
        val messageTextView = view.findViewById<TextView>(R.id.textView_message_content)
        val timeTextView = view.findViewById<TextView>(R.id.textView_message_time)

        nameTextView.text = ""
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