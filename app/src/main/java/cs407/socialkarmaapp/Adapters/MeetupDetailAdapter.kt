package cs407.socialkarmaapp.Adapters

import android.content.Intent
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cs407.socialkarmaapp.Models.Meetup
import cs407.socialkarmaapp.R
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class MeetupDetailAdapter(private var meetup: Meetup?, private val delegate: MeetupDelegate): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun setMeetup(newMeetup: Meetup) {
        this.meetup = newMeetup
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        this.meetup?.let {
            if (it.usersAttending.size == 0) {
                return 1
            } else {
                return 2 + it.usersAttending.size
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
            else -> {
                return 1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.meetup_row, parent, false)
                return MeetupDetailViewHolder(cellForRow)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val cellForRow = layoutInflater.inflate(R.layout.meetup_header_row, parent, false)
                return MeetupAttendingViewHolder(cellForRow)
            }
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        when (p0.itemViewType) {
            0 -> {
                val viewHolder = p0 as MeetupDetailViewHolder
                this.meetup?.let {
                    viewHolder.setupView(it, delegate)
                }
            }
            else -> {
                val viewHolder = p0 as MeetupAttendingViewHolder
                if (p1 == 1) {
                    viewHolder.view.findViewById<TextView>(R.id.textView_meetup_attending).text = "Users Attending"
                } else {
                    val attendingTextView = viewHolder.view.findViewById<TextView>(R.id.textView_meetup_attending)
                    attendingTextView.text = meetup!!.usersAttending.get(p1 - 2).username
                    attendingTextView.setTextColor(Color.BLACK)
                }
            }
        }
    }
}

class MeetupDetailViewHolder(val view: View): RecyclerView.ViewHolder(view) {
    fun setupView(meetup: Meetup, delegate: MeetupDelegate) {
        val titleTextView = view.findViewById<TextView>(R.id.textView_title)
        val organizerTextView = view.findViewById<TextView>(R.id.textView_organizer)
        val descriptionTextView = view.findViewById<TextView>(R.id.textView_description)
        val rsvpButton = view.findViewById<Button>(R.id.button_rsvp)
        val timesTextView = view.findViewById<TextView>(R.id.textView_meetup_detail_time)
        val locationTextView = view.findViewById<TextView>(R.id.textView_meetup_detail_location)

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
        }

        if (meetup.location.name != null) {
            locationTextView.visibility = View.VISIBLE
            locationTextView.text = "Where:\n" + meetup.location.name
        } else {
            locationTextView.visibility = View.GONE
        }

        timesTextView.visibility = View.VISIBLE
        val sdf = SimpleDateFormat("MM/dd/yy hh:mm")
        timesTextView.text = "When:\n" + sdf.format(Date(meetup.startTime * 1000)) + "\n~ " + sdf.format(Date(meetup.endTime * 1000))

        rsvpButton.setOnClickListener {
            delegate.rsvpButtonClicked(meetup.meetupId)
        }
    }
}

class MeetupAttendingViewHolder(val view: View): RecyclerView.ViewHolder(view) {

}