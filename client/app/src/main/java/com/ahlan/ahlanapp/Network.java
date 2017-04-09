/**
 * Created by Administrator on 1/22/2017.
 */

package com.ahlan.ahlanapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


import com.ahlan.ahlanapp.*;
import commonLibrary.*;



public class Network implements Runnable
{
    private Socket socket;
    private String ip;
    private int port;
    private String MACAddress;
    private String mPhoneNumber;

    private final ReentrantLock lock;
    private final Condition alert;
    private Queue<Query> inQueue;

    private static Network instance = null;
    private static Logger logger = Logger.getLogger(Network.class.getName());

    private InputStream input;
    private OutputStream output;

    public static Network getInstance()
    {
        if(instance == null)
            instance = new Network();
        return instance;
    }


    protected Network()
    {
        ip = "127.0.0.1";
        port = 7070;
        inQueue = new Queue<Query>();
        lock = new ReentrantLock();
        alert = lock.newCondition();
    }


    /*
    add a query to the out queue safely
     */
    protected void addToInQueue(Query q)
    {
        lock.lock();
        inQueue.enqueue(q);
        lock.unlock();
    }


    public void run()
    {
        try {
            socket = new Socket(ip, port);
            input = socket.getInputStream();
            output = socket.getOutputStream();
            new Thread(SendData.getInstance(output)).start();
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"Can't connect to server : "+e.toString());
        }



        Query q;
        q = communicateWithServer();
        addToInQueue(q);
    }


    private void parseQuery(Query q)
    {
        switch(q.getOpCode())
        {


            default:
                break;
        }
    }


    public static String getMAC(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }


    public void setMAC(Context context)
    {
        MACAddress = Network.getMAC(context);
    }

    /*
      deals with the network side of signing up
      return true if signUp done
      return false if problem occurred
       */
    public void signUp(String userName,String password,String phoneNumber)
    {
        mPhoneNumber = phoneNumber;
        String[] params = {userName,password,phoneNumber}; // need security for password
        Query q = new Query(Constants.signUp_client,params); // need to include the library
        SendData.getInstance().addToOutQueue(q);
    }

    /*
    deals with the network side of signing in
     */
    public void signIn(String userName,String password)
    {
        String[] params = {userName,password}; // need security for password
        Query q = new Query(Constants.signIn_client,params); // need to include the library
        SendData.getInstance().addToOutQueue(q);
    }




    public void sentMessage(String destination, String text)
    {
        Message msg = new Message(text,mPhoneNumber,destination);
        Query q = new Query(Constants.sentMessage_client,msg);
        SendData.getInstance().addToOutQueue(q);
    }


    /*
    wait until there's a query from server, and return it
    if there's priblem, return null
     */
    public Query communicateWithServer()
    {
        int length;
        Query q;

        if(!waitForAnswer())
        {
            //TODO  take care of failure
            return null;
        }
        try {
            length = ClientHandler.getMessageLength(input);
            q = ClientHandler.getQuery(length,input);
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"problem with getting data from server : "+e.getMessage());
            q = null;
        }
        return q;
    }




    /*
    waiting for the server to have query ready
    return true if it has
    return false if a problem occurred (write to the log)
     */
    public boolean waitForAnswer()
    {
        try {
            while (/*input == null*/input.available() < 4) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "cant put thread to sleep");
                    return false;
                }

            }
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"problem with getting available data from server");
            return false;
        }
        return true;


    }

    public void close()
    {
        try {
            socket.close();
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"Problem with closing the port : "+e.toString());
        }
    }

}
