package com.ahlan.ahlanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.util.Log;

import java.util.List;

import commonLibrary.*;

public class ChatActivity extends AppCompatActivity {
    private List<Message> messages;
    private String chatName;
    private LinearLayout mLayout;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mLayout = (LinearLayout) findViewById(R.id.messagesLayout);


        /*Bundle b = getIntent().getExtras();
        if(b != null) {
            chatName =  b.getString("chatName");
            phoneNumber = b.getString("phoneNumber");
        }*/
        chatName =  "chatName";
        phoneNumber = "phoneNumber";


        onGetMessage(new Message("data", phoneNumber, "dest4"));
        onGetMessage(new Message("data", phoneNumber, "dest4"));
        onGetMessage(new Message("data", phoneNumber, "dest4"));
    }

    protected void onGetMessage(Message message) {
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        textView1.setText(message.GetData());
        textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
        mLayout.addView(textView1);

        //sendMessage("Message for testing","0585259393");
    }
/*
    private void sendMessage(String text,String DestPhoneNumber)
    {
        Message msg = new Message(text,phoneNumber,DestPhoneNumber);
        messages.add(msg);

        if(Network.getInstance().sendMessage(msg))
            Log.i("message","yeah");
        else
            Log.i("message","nope");

    }*/
}
