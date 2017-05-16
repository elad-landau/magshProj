package com.ahlan.ahlanapp;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


import commonLibrary.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,startChat_dialog.NoticeDialogListener {

    public static final int RESULT_REQ = 1;
    private User mUser;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private List<Message> messages = new ArrayList<>();
    private List<User> chatsUsers = new ArrayList<>();
    private TextView mUserNameView;
    private TextView mUserPhoneView;


    private static final class lock {}
    private Object lockMessages;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Network.getInstance();
        new Thread(Network.getInstance()).start();
        Network.getInstance().setMainActivity(this);
        lockMessages = new lock();
        mUser = new User ("", "");

        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, RESULT_REQ);
    }

    /*
   ask the server for the all the messages of this user
   then convert the array into list
    */
    private List<Message> createMessageList()
    {
        Message[] msg = Network.getInstance().getAllMessages();
        List<Message> msgs = new ArrayList<Message>();

        for(int i = 0;i<msg.length;i++)
            msgs.add(msg[i]);

        return msgs;
    }

    /*
    return array of users (name and phone number) of all the users the client has messages from/to
    return null if no messages
     */
    private void setUsersAtMessages()
    {
        synchronized (lockMessages) {
            if (messages.size() == 0)
                return;

        }
        List<String> pNumbers = new ArrayList<String>();

        synchronized (lockMessages) {
            for (int i = 0; i < messages.size(); i++) {
                String targetNumber;
                if (messages.get(i).get_origin() == mUser.getPhoneNumber())
                    targetNumber = messages.get(i).get_destination();
                else
                    targetNumber = messages.get(i).get_origin();

                if (!pNumbers.contains(targetNumber))
                    pNumbers.add(targetNumber);
            }
        }
        for(int i =0;i<pNumbers.size();i++){
            chatsUsers.add(Network.getInstance().getUserByPhone(pNumbers.get(i)));
            mAdapter.notifyItemInserted(messages.size() - 1);
        }
    }


    public void enterMessageToList(Message msg)
    {
        synchronized (lockMessages)
        {
            messages.add(msg);
        }
    }


    public User getUser()
    {
        return mUser;
    }


    /*
    take cate of dialog positive click
    access the phone number, ask the server is exist
    if success start new chat
    if fail present error message
     */
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        EditText phoneNumber = (EditText)dialog.getDialog().findViewById(R.id.phoneNumberBox);
        String phone = phoneNumber.getText().toString();

        dialog.getDialog().cancel();
        if(Network.getInstance().isUserExists(phone))
        {
            chatsUsers.add(Network.getInstance().getUserByPhone(phone));
            mAdapter.notifyItemInserted(0);
        }
        else
        {
            Context context = getApplicationContext();
            CharSequence text = "user is not exists";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context,text,duration);
            toast.show();
        }
    }

    /*
    take care of dialog negatice press
    just go backward
     */
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        dialog.getDialog().cancel();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_REQ || requestCode == Activity.RESULT_OK && data != null) {
            super.onActivityResult(requestCode, resultCode, data);

            try {
                mUser = Network.getInstance().getUserByPhone(data.getStringExtra("phoneNumber"));

            } catch (Exception ex) {
                finish();
            }
        }

        finishSetup();
    }

    private void finishSetup()
    {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//send message activity
                DialogFragment dialog = new startChat_dialog();
                dialog.show(getSupportFragmentManager(),"startChat_dialog");
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mUserNameView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
        mUserPhoneView = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_phone_number);

        mUserPhoneView.setText(mUser.getPhoneNumber());
        mUserNameView.setText(mUser.getName());


        synchronized (lockMessages) {
            messages = createMessageList();
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.chats_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ChatAdapter(chatsUsers);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        setUsersAtMessages();

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra("chatName", chatsUsers.get(position).getName());
                        intent.putExtra("destPhoneNumber", chatsUsers.get(position).getPhoneNumber());
                        intent.putExtra("userPhoneNumber", mUser.getPhoneNumber());
                        startActivity(intent);
                    }

                })
        );

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            Bundle b = new Bundle();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_send_message) {
            DialogFragment dialog = new startChat_dialog();
            dialog.show(getSupportFragmentManager(),"startChat_dialog");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

