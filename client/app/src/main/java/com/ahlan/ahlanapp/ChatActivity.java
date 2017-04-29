package com.ahlan.ahlanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;
import java.util.Vector;

import commonLibrary.*;

public class ChatActivity extends AppCompatActivity {
    private List<Message> messages;
    private String chatName;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            chatName =  b.getString("chatName");
            phoneNumber = b.getString("phoneNumber");
        }

        sendMessage("Message for testing","0585259393");
    }

    private void sendMessage(String text,String DestPhoneNumber)
    {
        Message msg = new Message(text,phoneNumber,DestPhoneNumber);
        messages.add(msg);

        if(Network.getInstance().sendMessage(msg))
            Log.i("message","yeah");
        else
            Log.i("message","nope");
    }
}
