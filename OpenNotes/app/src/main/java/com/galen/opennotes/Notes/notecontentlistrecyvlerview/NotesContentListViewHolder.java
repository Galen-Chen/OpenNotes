package com.galen.opennotes.Notes.notecontentlistrecyvlerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/19/17.
 */

public class NotesContentListViewHolder extends RecyclerView.ViewHolder {
    TextView mNotesTextView,mDateTextView;
    ImageView mImageView;
    View mRootView;

    public NotesContentListViewHolder(View itemView) {
        super(itemView);
        mNotesTextView= (TextView) itemView.findViewById(R.id.notecontentlist_thenotes_textview);
        mDateTextView= (TextView) itemView.findViewById(R.id.notecontentlist_datecreated_textview);
        mImageView= (ImageView) itemView.findViewById(R.id.notecontentlist_imageview);
        mRootView=itemView.findViewById(R.id.notecontentlist_rootView);
    }
}
