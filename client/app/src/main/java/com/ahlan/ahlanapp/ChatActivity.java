package com.ahlan.ahlanapp;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import commonLibrary.*;

public class ChatActivity extends AppCompatActivity {
    private List<Message> messages = new ArrayList<>();
    private String chatName;
    private TextView mChatTitel;
    private EditText mMessageText;
    private Button mSendButton;
    private LinearLayout mLayout;
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;
    private String thisPhoneNumber;
    private String destPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mMessageText = (EditText) findViewById(R.id.messageText);
        mSendButton = (Button) findViewById(R.id.send);
        mRecyclerView.setHasFixedSize(true);

        Network.getInstance().addToActiveChatList(this); //add this chat to the active chat lists

        mAdapter = new MessageAdapter(messages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            chatName = null;
            destPhoneNumber = "";
            thisPhoneNumber = "";
        } else {
            chatName = extras.getString("chatName");
            destPhoneNumber = extras.getString("destPhoneNumber");
            thisPhoneNumber = extras.getString("userPhoneNumber");
        }

        this.createMessageList();

        mChatTitel = (TextView) findViewById(R.id.chatName);
        mChatTitel.setText(chatName);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mMessageText.getText().toString().compareTo("") != 0)
                {
                sendMessage(mMessageText.getText().toString());
                mMessageText.setText("");
                }
            }
        });

        //prepareMessagesData();
    }

    private void prepareMessagesData() {//for test
        Message movie = new Message("Mad Max: Fury Road", "Action & Adventure", "2015");
        messages.add(movie);

        movie = new Message("Inside Out", "Animation, Kids & Family", "2015");
        messages.add(movie);

        movie = new Message("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        messages.add(movie);

        movie = new Message("Shaun the Sheep", "Animation", "2015");
        messages.add(movie);

        movie = new Message("The Martian", "Science Fiction & Fantasy", "2015");
        messages.add(movie);

        movie = new Message("Mission: Impossible Rogue Nation", "Action", "2015");
        messages.add(movie);

        movie = new Message("Up", "Animation", "2009");
        messages.add(movie);

        movie = new Message("Star Trek", "Science Fiction", "2009");
        messages.add(movie);

        movie = new Message("The LEGO Movie", "Animation", "2014");
        messages.add(movie);

        movie = new Message("Iron Man", "Action & Adventure", "2008");
        messages.add(movie);

        movie = new Message("Aliens", "Science Fiction", "1986");
        messages.add(movie);

        movie = new Message("Chicken Run", "Animation", "2000");
        messages.add(movie);

        movie = new Message("Back to the Future", "Science Fiction", "1985");
        messages.add(movie);

        movie = new Message("Raiders of the Lost Ark", "Action & Adventure", "1981");
        messages.add(movie);

        movie = new Message("Goldfinger", "Action & Adventure", "1965");
        messages.add(movie);

        movie = new Message("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        messages.add(movie);

        mAdapter.notifyDataSetChanged();
    }


    /* //TODO: crash
    @Override
    public void onBackPressed()
    {
        getParent().onBackPressed();
        Network.getInstance().removeFromActiveChatList(this);
        super.onBackPressed();
    }
    protected void onDestroy()
    {
        getParent().onBackPressed();
        Network.getInstance().removeFromActiveChatList(this);
        super.onDestroy();
    }*/

    public String getChatPhone()
    {
        return thisPhoneNumber;
    }

    protected void onGetMessage(Message message) {

        messages.add(message);
        mAdapter.notifyItemInserted(messages.size() - 1);
        mRecyclerView.scrollToPosition(messages.size()-1);
    }

    private void sendMessage(String text)
    {
        Message msg = new Message(text,thisPhoneNumber,destPhoneNumber);
        messages.add(msg);
        mAdapter.notifyItemInserted(messages.size() - 1);
        mRecyclerView.scrollToPosition(messages.size()-1);
        Network.getInstance().sendMessage(msg);

    }
    private void createMessageList()
    {
        Message[] msg = Network.getInstance().getAllMessages();

        for(int i = 0;i<msg.length;i++) {
            if((msg[i].get_destination().compareTo(thisPhoneNumber) == 0 && msg[i].get_origin().compareTo(destPhoneNumber) == 0)
                    ||(msg[i].get_destination().compareTo(destPhoneNumber) == 0 && msg[i].get_origin().compareTo(thisPhoneNumber) == 0))
                messages.add(msg[i]);
        }

    }
}
