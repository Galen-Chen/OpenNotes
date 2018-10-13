package com.galen.opennotes.Notes.content;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/19/17.
 */

public class TagsViewHolder extends RecyclerView.ViewHolder {
    EditText editText;
    Button saveButton;
    public TagsViewHolder(View itemView) {
        super(itemView);
        editText= (EditText) itemView.findViewById(R.id.add_tag_edittext);
        saveButton= (Button) itemView.findViewById(R.id.save_tag_button);
    }
}
