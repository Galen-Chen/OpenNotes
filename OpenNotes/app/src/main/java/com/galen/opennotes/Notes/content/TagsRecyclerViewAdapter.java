package com.galen.opennotes.Notes.content;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.galen.opennotes.R;

import java.util.List;

/**
 * Created by Galen on 5/19/17.
 */

public class TagsRecyclerViewAdapter extends RecyclerView.Adapter<TagsViewHolder> {
    private List<TagObject> mList;
    private OnItemClickLisnter mLisnter;


    public interface OnItemClickLisnter {
        void saveTags();

    }

    public TagsRecyclerViewAdapter(List<TagObject> mList, OnItemClickLisnter mLisnter) {
        this.mList = mList;
        this.mLisnter = mLisnter;
    }

    @Override
    public TagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_recyvlerview_in_content, parent, false);
        TagsViewHolder viewHolder = new TagsViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TagsViewHolder holder, int position) {
        final EditText mEdittext = holder.editText;
        final Button msaveButton = holder.saveButton;
        mEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                msaveButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    msaveButton.setVisibility(View.GONE);
                } else if (s.length() > 0) {

                    msaveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mLisnter.saveTags();
                            msaveButton.setVisibility(View.GONE);

                        }
                    });
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
