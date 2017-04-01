/**
 * Created by Administrator on 1/22/2017.
 */

package com.ahlan.ahlanapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.sql.ClientInfoStatus;
import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;


import android.app.DownloadManager;
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

    private final ReentrantLock lock;
    private long lastSend;
    private Queue<Query> queue;

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
        queue = new Queue<Query>();
        lock = new ReentrantLock();

    }


    /*
    add a query to the queue safely
     */
    private void addToQueue(Query q)
    {
        lock.lock();
        queue.enqueue(q);
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
            while(queue.isEmpty() && (System.currentTimeMillis() -lastSend <= Constants.timeBetweenSends))
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
            if(!queue.isEmpty())
            {
                q = queue.dequeue();
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
    public boolean signUp(String userName,String password)
    {

        String[] params = {userName,password}; // need security for password
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

    public boolean communicateWithServer(Query q)
    {
        int length;
        addToQueue(q);

        if(!waitForAnswer())
        {
            //TODO  take care of failure
            return false;
        }
        try {
            length = ClientHandler.getMessageLength(input);
            q = ClientHandler.getQuery(length,input);
        }
        catch(Exception e)
        {
            logger.log(Level.WARNING,"problem with getting data from server : "+e.getMessage());
        }
        return q.getStr()[0].compareTo(Integer.toString(Constants.success)) == 0;
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
