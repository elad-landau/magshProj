package com.ahlan.ahlanapp;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import commonLibrary.ClientHandler;
import commonLibrary.Constants;
import commonLibrary.Query;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Administrator on 4/7/2017.
 */

public class GetData implements Runnable
{
    private InputStream input;
    private static Logger logger = Logger.getLogger(GetData.class.getName());

    public GetData(InputStream input)
    {
        this.input = input;
    }


    public void run()
    {
        while(true)
            communicateWithServer();
    }




    public boolean communicateWithServer()
    {
        int length;
        Query q;

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
}
