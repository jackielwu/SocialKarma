package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.support.constraint.R.id.gone
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import cs407.socialkarmaapp.*
import cs407.socialkarmaapp.Models.*
import org.w3c.dom.Text

interface MeetupDelegate {
    fun rsvpButtonClicked(meetupId: String)
}

class MeetupAdapter(private var meetups: MutableList<Meetup>, private var emptyType: EmptyContentViewHolder.EmptyContentType, private val context: Context, private val delegate: MeetupDelegate): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setMeetups(newMeetups: MutableList<Meetup>) {
        this.meetups = newMeetups
        this.notifyDataSetChanged()
    }

    fun addToMeetups(newMeetups: List<Meetup>) {
        val index = this.meetups.size
        this.meetups.addAll(newMeetups)
        this.notifyItemRangeInserted(index, newMeetups.size)
    }

    fun setEmptyType(newEmptyContentType: EmptyContentViewHolder.EmptyContentType) {
        this.emptyType = newEmptyContentType
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        when (emptyType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                return meetups.size
            }
            else -> {
                return 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (emptyType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.meetup_row, parent, false)
                return MeetupViewHolder(cellForRow)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.empty_content_row, parent, false)
                return EmptyContentViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (emptyType) {
            EmptyContentViewHolder.EmptyContentType.NOTEMPTY -> {
                (p0 as MeetupViewHolder).setupView(meetups, p1, delegate, context)
                (p0 as MeetupViewHolder).didSelectRow(meetups, p1, context)
            }
            EmptyContentViewHolder.EmptyContentType.ERROR -> {
                (p0 as EmptyContentViewHolder).setupView("There seemed to be an error loading meetups.\nPlease try again.", emptyType)
            }
            EmptyContentViewHolder.EmptyContentType.EMPTY -> {
                (p0 as EmptyContentViewHolder).setupView("There seems to be no upcoming meetups\nin the area right now.\n\nAdd one of your own using the '+' button!", emptyType)
            }
        }
    }
}

class EmptyContentViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    enum class EmptyContentType {
        NOTEMPTY, EMPTY, ERROR
    }
    fun setupView(emptyContentText: String, type: EmptyContentType) {
        val emptyImageView = view.findViewById<ImageView>(R.id.imageView_empty_content)
        val emptyTextView = view.findViewById<TextView>(R.id.textView_empty_content)
        emptyTextView.text = emptyContentText

        when (type) {
            EmptyContentType.EMPTY -> {
                emptyImageView.setImageResource(R.drawable.baseline_inbox_black_48dp)
            }
            EmptyContentType.ERROR -> {
                emptyImageView.setImageResource(R.drawable.baseline_error_outline_black_48dp)
            }
        }
    }
}

class MeetupViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(meetups: List<Meetup>, index: Int, delegate: MeetupDelegate, context: Context) {
        val meetup = meetups.get(index)
        val titleTextView = view.findViewById<TextView>(R.id.textView_title)
        val organizerTextView = view.findViewById<TextView>(R.id.textView_organizer)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_description)
        val rsvpButton = view.findViewById<Button>(R.id.button_rsvp)
        val locationTextView = view.findViewById<TextView>(R.id.textView_meetup_detail_location)
        val timesTextView = view.findViewById<TextView>(R.id.textView_meetup_detail_time)

        titleTextView.text = meetup.title
        organizerTextView.text = meetup.organizerName
        meetup.shortDescription?.let {
            descriptionTextView.text = meetup.shortDescription
        } ?: run {
            descriptionTextView.visibility = View.GONE
        }

        if (meetup.attending != null && meetup.attending) {
            rsvpButton.text = "Attending"
            rsvpButton.isEnabled = false
        } else {
            rsvpButton.text = "RSVP"
            rsvpButton.isEnabled = true
        }

        organizerTextView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            val userId = meetup.organizer
            intent.putExtra(UserProfileActivity.EXTRA_USER_PROFILE_ID, userId)
            context.startActivity(intent)
        }

        locationTextView.visibility = View.GONE
        timesTextView.visibility = View.GONE

        rsvpButton.setOnClickListener {
            delegate.rsvpButtonClicked(meetup.meetupId)
        }
    }

    fun didSelectRow(meetups: List<Meetup>, index: Int, context: Context) {
        this.view.setOnClickListener {
            val intent = Intent(context, MeetupDetailActivity::class.java)
            val meetup = meetups.get(index)
            intent.putExtra(MeetupActivity.EXTRA_MEETUP, meetup.meetupId)
            intent.putExtra(MeetupActivity.EXTRA_MEETUP_TITLE, meetup.title)
            context.startActivity(intent)
        }
    }
}