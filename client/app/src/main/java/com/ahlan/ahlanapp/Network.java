/**
 * Created by Administrator on 1/22/2017.
 */

package com.ahlan.ahlanapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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
    private long lastSend;
    private Queue<Query> outQueue;
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
        outQueue = new Queue<Query>();
        inQueue = new Queue<Query>();
        lock = new ReentrantLock();

    }


    /*
    add a query to the out queue safely
     */
    private void addToOutQueue(Query q)
    {
        lock.lock();
        outQueue.enqueue(q);
        lock.unlock();
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
            lastSend = System.currentTimeMillis();
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"Can't connect to server : "+e.toString());
        }



        Query q;
        while(true)
        {
             lock.lock();
            while(outQueue.isEmpty() && (System.currentTimeMillis() -lastSend <= Constants.timeBetweenSends))
            {
                lock.unlock();
                try {
                    Thread.sleep(10);
                }
                catch(InterruptedException e)
                {
                    logger.log(Level.INFO,"cant put thread to sleep at network 'run' method (no message,time for null" +
                            "message hasn't come :"+e.toString());
                }
                lock.lock();
            }
            if(!outQueue.isEmpty())
            {
                q = outQueue.dequeue();
                lock.unlock();
            }
            else
            {
                lock.unlock();
                //q = new Query(Constants.empty_query,new String[0]);
                continue;

            }
            sendData(q);
        }
    }


    /*
     a private method
     deals with serializtion and the sending of date to the server
      */
    private void sendData(Query q)
    {

        try {
            byte[] data = q.serialize();
            byte[] dataLength = ClientHandler.intToByteArray(data.length);
            output.write(dataLength);
            output.write(data);
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"cant write to server : "+e.toString());
        }
        catch (ClassNotFoundException e)
        {
            logger.log(Level.WARNING,"cant convert query into bytes : "+e.toString());
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
    public boolean signUp(String userName,String password,String phoneNumber)
    {
        mPhoneNumber = phoneNumber;
        String[] params = {userName,password,phoneNumber}; // need security for password
        Query q = new Query(Constants.signUp_client,params); // need to include the library
        return communicateWithServer(q);
    }

    /*
    deals with the network side of signing in
     */
    public void signIn(String userName,String password)
    {
        String[] params = {userName,password}; // need security for password
        Query q = new Query(Constants.signIn_client,params); // need to include the library
        communicateWithServer(q);
    }

    public void sentMessage(int destination, String text)
    {
        Message msg = new Message(text,mPhoneNumber,destination);
        Query q = new Query(Constants.sentMessage_client,msg);
        communicateWithServer(q);
    }

    private void communicateWithServer(Query q)
    {
        addToOutQueue(q);
        waitForAnswer();
    }

    private boolean waitForAnswer()
    {
        return false;
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
