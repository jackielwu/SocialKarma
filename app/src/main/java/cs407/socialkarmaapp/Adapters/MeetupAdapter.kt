package cs407.socialkarmaapp.Adapters

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.support.constraint.R.id.gone
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import cs407.socialkarmaapp.*
import cs407.socialkarmaapp.Models.*
import org.w3c.dom.Text

interface MeetupDelegate {
    fun rsvpButtonClicked(meetupId: String)
}

class MeetupAdapter(private var meetups: MutableList<Meetup>, private val context: Context, private val delegate: MeetupDelegate): RecyclerView.Adapter<MeetupViewHolder>() {
    fun setMeetups(newMeetups: MutableList<Meetup>) {
        this.meetups = newMeetups
        this.notifyDataSetChanged()
    }

    fun addToMeetups(newMeetups: List<Meetup>) {
        val index = this.meetups.size
        this.meetups.addAll(newMeetups)
        this.notifyItemRangeInserted(index, newMeetups.size)
    }

    override fun getItemCount(): Int {
        return meetups.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cellForRow = layoutInflater.inflate(R.layout.meetup_row, parent, false)
        return MeetupViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: MeetupViewHolder, p1: Int) {
        p0.setupView(meetups, p1, delegate, context)
        p0.didSelectRow(meetups, p1, context)
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