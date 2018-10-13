package com.galen.opennotes.wall;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/25/17.
 */

public class WallViewHolder extends RecyclerView.ViewHolder {
    TextView userNamerView,datecreateView,theNoteView;
    ImageView imageView;

    public WallViewHolder(View itemView) {
        super(itemView);

   userNamerView= (TextView) itemView.findViewById(R.id.wall_notes_username);
    datecreateView= (TextView) itemView.findViewById(R.id.wall_notes_datecreated);
    theNoteView= (TextView) itemView.findViewById(R.id.wall_notes_thenotes);
        imageView = (ImageView) itemView.findViewById(R.id.wall_notes_image);

    }
}
