package com.ahlan.ahlanapp;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    private List<Message> messages;
    private String chatName;
    private TextView mChatTitel;
    private EditText mMessageText;
    private Button mSendButton;
    private LinearLayout mLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int thisPhoneNumber;
    private int destPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mMessageText = (EditText) findViewById(R.id.messageText);
        mSendButton = (Button) findViewById(R.id.send);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            chatName = null;
            destPhoneNumber = -1;
            thisPhoneNumber = -1;
        } else {
            chatName = extras.getString("chatName");
            destPhoneNumber = extras.getInt("phoneNumber");
            thisPhoneNumber = extras.getInt("userPhoneNumber");
        }

        mChatTitel = (TextView) findViewById(R.id.chatName);
        mChatTitel.setText(chatName);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(mMessageText.getText().toString());
                mMessageText.setText("");
            }
        });
        Network.getInstance().addToActiveChatList(this); //add this chat to the active chat lists
        messages = this.createMessageList();

    }
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
    }

    public String getChatPhone()
    {
        return Integer.toString(thisPhoneNumber);
    }

    //TODO:Create text view dynamically
    protected void onGetMessage(Message message) {

        messages.add(message);
        mAdapter.notifyItemInserted(messages.size() - 1);
    }

    private void sendMessage(String text)
    {
        Message msg = new Message(text,Integer.toString(thisPhoneNumber),Integer.toString(destPhoneNumber));
        messages.add(msg);
        mAdapter.notifyItemInserted(messages.size() - 1);
        Network.getInstance().sendMessage(msg);

    }
    private List<Message> createMessageList()
    {
        Message[] msg = Network.getInstance().getAllMessages();
        List<Message> msgs = new ArrayList<Message>();

        for(int i = 0;i<msg.length;i++)
            msgs.add(msg[i]);

        return msgs;
    }
}