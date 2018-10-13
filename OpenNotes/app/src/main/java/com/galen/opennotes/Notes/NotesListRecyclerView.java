package com.galen.opennotes.Notes;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/24/17.
 */

public class NotesListRecyclerView extends RecyclerView.ViewHolder {
    TextView titleView,typeView,dateView;
    ImageView imageView;
    View rootView;

    public NotesListRecyclerView(View itemView) {
        super(itemView);

        typeView= (TextView) itemView.findViewById(R.id.notes_list_type);
        titleView= (TextView) itemView.findViewById(R.id.notes_list_title);
     imageView= (ImageView) itemView.findViewById(R.id.image_for_noteslist);
       dateView = (TextView) itemView.findViewById(R.id.date_create_view);
         rootView = itemView.findViewById(R.id.notes_list_rootView);
    }
}
