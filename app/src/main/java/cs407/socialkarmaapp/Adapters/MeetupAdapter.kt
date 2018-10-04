package cs407.socialkarmaapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cs407.socialkarmaapp.R

class MeetupAdapter: RecyclerView.Adapter<MeetupViewHolder>() {
    override fun getItemCount(): Int {
        return 5
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MeetupViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val cellForRow = layoutInflater.inflate(R.layout.meetup_row, p0, false)
        return MeetupViewHolder(cellForRow)
    }

    override fun onBindViewHolder(p0: MeetupViewHolder, p1: Int) {
    }
}

class MeetupViewHolder(view: View): RecyclerView.ViewHolder(view) {

}