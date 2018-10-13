package com.galen.opennotes.Notes.notecontentlistrecyvlerview;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galen.opennotes.Notes.content.NoteContentActivity;
import com.galen.opennotes.Notes.content.NoteContentObjects;
import com.galen.opennotes.R;

import java.util.List;

/**
 * Created by Galen on 5/19/17.
 */

public class NotesContentListAdapter extends RecyclerView.Adapter<NotesContentListViewHolder> {
    public static final String SEND_KEY_TO_CONTENT_FROM_NOTECONTENTLIST= "DDDADFADFEFAD";
    public static final String SEND_POSTION_TO_CONTENT="ADFEFASDFADSF";
    List<NoteContentObjects> mList;
    Context mContext;
    String mKey;

    public NotesContentListAdapter(List<NoteContentObjects> mList, Context mContext, String mKey) {
        this.mList = mList;
        this.mContext = mContext;
        this.mKey = mKey;
    }

    @Override
    public NotesContentListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notescontentlist_list_layout, parent, false);
        NotesContentListViewHolder viewHolder = new NotesContentListViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotesContentListViewHolder holder, final int position) {

        holder.mNotesTextView.setText(mList.get(position).getNotesYouWrite());
        holder.mDateTextView.setText(mList.get(position).getTimeCreated());
        holder.mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteContentActivity.class);
                intent.putExtra(SEND_KEY_TO_CONTENT_FROM_NOTECONTENTLIST,mKey);
                intent.putExtra(SEND_POSTION_TO_CONTENT,position);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
