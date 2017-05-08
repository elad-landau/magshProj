package com.ahlan.ahlanapp;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private List<Message> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
            }
        }
    // Provide a suitable constructor (depends on the kind of dataset)
    public ChatAdapter(List<Message> myDataset) {
            mDataset = myDataset;
        }

    // Create new views (invoked by the layout manager)
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_list, parent, false);
            // set the view's size, margins, paddings and layout parameters
            //...//
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset.get(position));

        }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(mDataset.size() ==0)
            return 0;

        List<String> pNumbers = new ArrayList<String>();

        for(int i =0;i<mDataset.size();i++)
        {
            String targetNumber;
            if(mDataset.get(i).get_origin() == mUser.getPhoneNumber())
                targetNumber = mDataset.get(i).get_destination();
            else
                targetNumber = mDataset.get(i).get_origin();

            if(!pNumbers.contains(targetNumber))
                Numbers.add(targetNumber);
        }
        return Numbers.size();
    }
}
