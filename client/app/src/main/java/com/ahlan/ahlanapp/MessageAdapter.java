package com.ahlan.ahlanapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import commonLibrary.*;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MViewHolder> {
    private List<Message> mDataset;
    private String mUser;
    private String DestName;


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

    public MessageAdapter(List<Message> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public MessageAdapter.MViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_list, parent, false);

        return new MViewHolder(itemView);
    }

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


    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setUserPhone(String user){this.mUser = user;}

    public void setDestName(String DestName){this.DestName = DestName;}

}
