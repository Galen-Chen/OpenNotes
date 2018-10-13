package com.galen.opennotes.myrecords;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/24/17.
 */

public class MyRecordsViewHolder extends RecyclerView.ViewHolder {
    TextView titleView,hoursView;
    View rootView;

    public MyRecordsViewHolder(View itemView) {
        super(itemView);

         titleView = (TextView) itemView.findViewById(R.id.record_title_view);
         hoursView = (TextView) itemView.findViewById(R.id.this_recod_total_hours_view);
        rootView=itemView.findViewById(R.id.my_recordslist_rootview);
    }
}
