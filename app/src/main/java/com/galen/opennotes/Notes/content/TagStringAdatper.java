package com.galen.opennotes.Notes.content;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.galen.opennotes.R;

import java.util.List;

/**
 * Created by Galen on 5/24/17.
 */

public class TagStringAdatper extends RecyclerView.Adapter<TagStringViewHolder> {

    List<String> mList;

    public TagStringAdatper(List<String> mList) {
        this.mList = mList;
    }

    @Override
    public TagStringViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_recyvlerview_in_content, parent, false);
        TagStringViewHolder viewHolder = new TagStringViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TagStringViewHolder holder, int position) {
        holder.editText.setText(mList.get(position));
        holder.editText.setFocusable(false);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
