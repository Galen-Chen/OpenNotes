package com.galen.opennotes.cautions;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.galen.opennotes.R;

import java.util.List;

/**
 * Created by Galen on 5/13/17.
 */

public class CautionsViewAdapter extends RecyclerView.Adapter<CautionViewHolder> {

    List<Cautions> mList;
    Context mContext;

    public CautionsViewAdapter(List<Cautions> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @Override
    public CautionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.caution_view, parent, false);
        CautionViewHolder viewHolder = new CautionViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CautionViewHolder holder, final int position) {
        holder.mTextView.setText(mList.get(position).getmCautions());


        holder.mTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                //big beg here!

                    int id = mList.get(position).getmID();

                    CautionDBopenHelper.getInstance(mContext).deleteCaution(id);

                    mList=CautionDBopenHelper.getInstance(mContext).getCautions();

                    notifyItemRemoved(position);


                Toast.makeText(mContext, "This caution is removed!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    public void updateCautionList(List<Cautions> list){
        mList=list;
        notifyDataSetChanged();
    }
}
