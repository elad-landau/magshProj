package com.ahlan.ahlanapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
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
    private String thisPhoneNumber;
    private String destPhoneNumber;

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
        chatName =  "chatName"; // the destenation phone number
        thisPhoneNumber = "phoneNumber"; // the src phonenumber
        mChatTitel = (TextView) findViewById(R.id.chatName);
        mChatTitel.setText(chatName);



        onGetMessage(new Message("data", thisPhoneNumber, "dest4"));
        onGetMessage(new Message("data", thisPhoneNumber, "dest4"));
        onGetMessage(new Message("data", thisPhoneNumber, "dest4"));
    }


    public String getChatName()
    {
        return chatName;
    }

    protected void onGetMessage(Message message) {
        TextView textView1 = new TextView(this);
        textView1.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        textView1.setText(message.GetData());
        mLayout.addView(textView1);
    }

    private void sendMessage(String text,String DestPhoneNumber)
    {
        Message msg = new Message(text,thisPhoneNumber,DestPhoneNumber);
        messages.add(msg);

        if(true)//Network.getInstance().sendMessage(msg))
            Log.i("message","yeah");
        else
            Log.i("message","nope");

    }
}
