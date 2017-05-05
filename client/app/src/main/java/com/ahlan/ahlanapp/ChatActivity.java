package com.ahlan.ahlanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;

import java.util.List;

import commonLibrary.*;

public class ChatActivity extends AppCompatActivity {
    private List<Message> messages;
    private String chatName;
    private TextView mChatTitel;
    private LinearLayout mLayout;
    private int thisPhoneNumber;
    private int destPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mLayout = (LinearLayout) findViewById(R.id.messagesLayout);

        if (savedInstanceState == null) {
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
        } else {
            chatName = (String) savedInstanceState.getSerializable("chatName");
            destPhoneNumber = (int) savedInstanceState.getSerializable("phoneNumber");
            thisPhoneNumber = (int) savedInstanceState.getSerializable("userPhoneNumber");
        }

        mChatTitel = (TextView) findViewById(R.id.chatName);
        mChatTitel.setText(chatName);
    }


    public String getChatName()
    {
        return chatName;
    }

    protected void onGetMessage(Message message) {
        TextView textView = new TextView(this);
        textView.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        textView.setText(/*message.GetData()*/"SGRDTHJNL");
        mLayout.addView(textView);
    }

    private void sendMessage(String text)
    {
        Message msg = new Message(text,Integer.toString(thisPhoneNumber),Integer.toString(destPhoneNumber));
        messages.add(msg);

        if(true)//Network.getInstance().sendMessage(msg))
            Log.i("message","yeah");
        else
            Log.i("message","nope");

    }
}