package com.ahlan.ahlanapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.Shape;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.StateSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import commonLibrary.*;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MViewHolder> {
    private List<Message> mDataset;
    private String mUser;
    private int selected_item;
    private String DestName;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MViewHolder extends RecyclerView.ViewHolder {
        public TextView mFrom;
        public TextView mMessageText;
        public TextView mMessageDate;

        public MViewHolder(View v) {
            super(v);
            mFrom = (TextView)v.findViewById(R.id.from);
            mMessageText = (TextView)v.findViewById(R.id.message_text);
            mMessageDate = (TextView)v.findViewById(R.id.message_time);
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public MessageAdapter(List<Message> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MessageAdapter.MViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list, parent, false);

        return new MViewHolder(itemView);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MViewHolder holder, int position) {
        holder.mMessageText.setText(mDataset.get(position).GetData());
        holder.mMessageDate.setText(mDataset.get(position).GetSentTime());

        if(mDataset.get(position).get_origin().compareTo(this.mUser) == 0) {
            holder.mFrom.setText("Me");
        }
        else{
            holder.mFrom.setText(DestName);
        }


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setUserPhone(String user){this.mUser = user;}

    public void setDestName(String DestName){this.DestName = DestName;}

}
