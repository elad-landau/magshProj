package com.ahlan.ahlanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import commonLibrary.*;

public class ChatActivity extends AppCompatActivity {
    private List<Message> messages;
    private String chatName;
    private LinearLayout mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        if(b != null) {
            chatName =  b.getString("chatName");
        }
        mLayout = (LinearLayout) findViewById(R.id.messagesLayout);
        for(int i= 0; i<messages.size(); i++)
            onGetMessage(messages.get(i));

    }

    protected void onGetMessage(Message message) {
        final Toolbar.LayoutParams lparams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        final TextView textView = new TextView(this);
        textView.setLayoutParams(lparams);
        textView.setText(message.get_destination());
        mLayout.addView(textView);
    }
}
