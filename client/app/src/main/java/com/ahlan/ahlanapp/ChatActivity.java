package com.ahlan.ahlanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            chatName = null;
            destPhoneNumber = null;
            thisPhoneNumber = null;
        } else {
            chatName = extras.getString("chatName");
            destPhoneNumber = extras.getString("destPhoneNumber");
            thisPhoneNumber = extras.getString("userPhoneNumber");
        }

        setContentView(R.layout.activity_chat);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
        mMessageText = (EditText) findViewById(R.id.messageText);
        mSendButton = (Button) findViewById(R.id.send);
        mRecyclerView.setHasFixedSize(true);



        mAdapter = new MessageAdapter(messages);
        mAdapter.setUserPhone(thisPhoneNumber);
        mAdapter.setDestName(chatName);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        Network.getInstance().addToActiveChatList(this); //add this chat to the active chat lists
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
    } */

    public String getChatPhone()
    {
        return destPhoneNumber;
    }

    public void onGetMessage(Message message) {

        messages.add(message);
        mAdapter.notifyItemInserted(messages.size() - 1);
        //mRecyclerView.scrollToPosition(messages.size()-1);
    }

    private void sendMessage(String text)
    {
        Message msg = new Message(text, thisPhoneNumber, destPhoneNumber);
        messages.add(msg);
        mAdapter.notifyItemInserted(messages.size() - 1);
        mRecyclerView.scrollToPosition(messages.size()-1);
        Network.getInstance().sendMessage(msg);

    }
    private void createMessageList()
    {
        Message[] msg = Network.getInstance().getAllMessages();

        for(int i = 0;i<msg.length;i++) {
            if((msg[i].get_destination() == thisPhoneNumber && msg[i].get_origin() == destPhoneNumber)
                    ||(msg[i].get_destination() == destPhoneNumber && msg[i].get_origin() == thisPhoneNumber))
                messages.add(msg[i]);
        }

    }
}
