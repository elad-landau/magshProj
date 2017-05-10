package com.ahlan.ahlanapp;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import commonLibrary.ClientHandler;
import commonLibrary.Constants;
import commonLibrary.Query;
import commonLibrary.Queue;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Administrator on 4/7/2017.
 */

public class SendData implements Runnable {

    private OutputStream output;
    private static Logger logger = Logger.getLogger(SendData.class.getName());

    private Queue<Query> outQueue;
    private final ReentrantLock lock;

    private long lastSend;

    private static SendData instance;

    protected SendData(OutputStream output) {
        this.output = output;
        outQueue = new Queue<Query>();
        lock = new ReentrantLock();
    }

    /*
    used by the network class at first call. used to initialize the object
     */
    public static SendData getInstance(OutputStream output)
    {
        if(instance == null)
            instance = new SendData(output);
        return instance;
    }

    /*
    use all the other times, when there's no need to pass the OutPutStream object
     */
    public static SendData getInstance()
    {
        while(instance == null) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("sleep", "cant put the thread to sleep : " + e.getMessage());
            }
        }
        return instance;
    }

    protected void addToOutQueue(Query q)
    {
        lock.lock();
        outQueue.enqueue(q);
        lock.unlock();
    }

    public void run() {
        Query q;

        lastSend = System.currentTimeMillis();
        while (true) {
            lock.lock();
            while (outQueue.isEmpty() && (System.currentTimeMillis() - lastSend <= Constants.timeBetweenSends)) {
                lock.unlock();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    logger.log(Level.INFO, "cant put thread to sleep at network 'run' method (no message,time for null" +
                            "message hasn't come :" + e.toString());
                }
                lock.lock();
            }
            if (!outQueue.isEmpty()) {
                q = outQueue.dequeue();
                lock.unlock();
            } else {
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

}
