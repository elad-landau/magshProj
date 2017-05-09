package com.ahlan.ahlanapp;

import android.app.Activity;
import android.database.sqlite.*;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;


import commonLibrary.*;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RESULT_REQ = 1;
    private User mUser;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Message> messages;
    private List<User> chatsUsers;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.chats_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        messages = createMessageList();
        // specify an adapter
        chatsUsers = getUsersAtMessages();
        mAdapter = new ChatAdapter(chatsUsers);
        mRecyclerView.setAdapter(mAdapter);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//send message activity
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG) //open message chat
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, mRecyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        mIntent = new Intent(MainActivity.this, ChatActivity.class);
                        mIntent.putExtra("chatName", chatsUsers.get(position).getName());
                        mIntent.putExtra("phoneNumber", Integer.getInteger(chatsUsers.get(position).getPhoneNumber()));
                        mIntent.putExtra("userPhoneNumber", Integer.getInteger(mUser.getPhoneNumber()));
                        startActivity(mIntent);
                    }

                })
        );

        //TODO: when new user added
        //chatsUsers.add(newUser)
        //mAdapter.notifyItemInserted(chatsUsers.size() - 1);

        //TODO: Start the Login activity for phoneNumber
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
    private List<User> getUsersAtMessages()
    {
        if(messages.size() ==0)
            return null;

        List<String> pNumbers = new ArrayList<String>();
        List<User> users;
        for(int i =0;i<messages.size();i++)
        {
            String targetNumber;
            if(messages.get(i).get_origin() == mUser.getPhoneNumber())
                targetNumber = messages.get(i).get_destination();
            else
                targetNumber = messages.get(i).get_origin();

            if(!pNumbers.contains(targetNumber))
                pNumbers.add(targetNumber);
        }

        users = new ArrayList<User>(pNumbers.size());
        for(int i =0;i<users.size();i++)
            users.set(i, Network.getInstance().getUserByPhone(pNumbers.get(i)));
        return users;
    }




    public User getUser()
    {
        return mUser;
    }

    //TODO: Get the extra "phoneNumber" from the Login activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {

            mUser.setPhoneNumber(data.getStringExtra("phoneNumber"));
        } catch (Exception ex) {
            finish();
        }

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

        } else if (id == R.id.nav_chat_group) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
