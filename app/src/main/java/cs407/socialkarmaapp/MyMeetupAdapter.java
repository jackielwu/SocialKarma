package cs407.socialkarmaapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 9/14/2017.
 */

//we need to extend the ArrayAdapter class as we are building an adapter
public class MyMeetupAdapter extends ArrayAdapter<meetup_item> {

    //the list values in the List of type hero
    List<meetup_item> meetup_items;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public MyMeetupAdapter(Context context, int resource, List<meetup_item> meetup_items) {
        super(context, resource, meetup_items);
        this.context = context;
        this.resource = resource;
        this.meetup_items = meetup_items;
    }

    //this will return the ListView Item as a View
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //we need to get the view of the xml for our list item
        //And for this we need a layoutinflater
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        //getting the view
        View view = layoutInflater.inflate(resource, null, false);

        //getting the view elements of the list from the view
        TextView textViewName = view.findViewById(R.id.meetup_name);
        TextView textViewTitle = view.findViewById(R.id.meetup_title);
        TextView textViewContext = view.findViewById(R.id.meetup_contexts);

        //getting the hero of the specified position
        meetup_item item = meetup_items.get(position);

        //adding values to the list item
        textViewName.setText(item.getName());
        textViewContext.setText(item.getContext());
        textViewTitle.setText(item.getContext());



        //finally returning the view
        return view;
    }

//    //this method will remove the item from the list
//    private void removeHero(final int position) {
//        //Creating an alert dialog to confirm the deletion
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Are you sure you want to delete this?");
//
//        //if the response is positive in the alert
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                //removing the item
//                item_list.remove(position);
//
//                //reloading the list
//                notifyDataSetChanged();
//            }
//        });
//
//        //if response is negative nothing is being done
//        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//            }
//        });
//
//        //creating and displaying the alert dialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }
}