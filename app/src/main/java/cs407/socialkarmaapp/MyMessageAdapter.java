package cs407.socialkarmaapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 9/14/2017.
 */

//we need to extend the ArrayAdapter class as we are building an adapter
public class MyMessageAdapter extends ArrayAdapter<message_item> {

    //the list values in the List of type hero
    List<message_item> message_items;

    //activity context
    Context context;

    //the layout resource file for the list items
    int resource;

    //constructor initializing the values
    public MyMessageAdapter(Context context, int resource, List<message_item> message_items) {
        super(context, resource, message_items);
        this.context = context;
        this.resource = resource;
        this.message_items = message_items;
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
        TextView textViewName = view.findViewById(R.id.textView_message_sender);
        TextView textViewContext = view.findViewById(R.id.textView_message_content);
        TextView textViewTime = view.findViewById(R.id.textView_message_time);
        //getting the hero of the specified position
        message_item item = message_items.get(position);

        //adding values to the list item
        textViewName.setText(item.getName());
        textViewContext.setText(item.getContext());
        textViewTime.setText(item.getTime());


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