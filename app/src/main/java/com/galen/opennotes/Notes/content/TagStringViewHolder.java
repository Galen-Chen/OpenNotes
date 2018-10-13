package com.galen.opennotes.Notes.content;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/24/17.
 */

public class TagStringViewHolder extends RecyclerView.ViewHolder {
    EditText editText;

    public TagStringViewHolder(View itemView) {
        super(itemView);
        editText= (EditText) itemView.findViewById(R.id.add_tag_edittext);
    }
}
