package com.galen.opennotes.cautions;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.galen.opennotes.R;

/**
 * Created by Galen on 5/13/17.
 */

public class CautionViewHolder extends RecyclerView.ViewHolder {
    public TextView mTextView;

    public CautionViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.caution_view);
    }
}
