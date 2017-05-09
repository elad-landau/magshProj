/**
 * Created by Administrator on 1/22/2017.
 */

package com.ahlan.ahlanapp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import commonLibrary.*;



class Network implements Runnable
{
    private Socket socket;
    private String ip;
    private int port;
    private String mPhoneNumber;

    private final ReentrantLock lock;
    private Queue<Query> inQueue;
    private static final class lock {}
    private final Object lockMessages;
    private final Object lockChats;

    private static Network instance = null;
    private static Logger logger;

    private InputStream input;

    private List<ChatActivity> activeChats;

    public static Network getInstance()
    {
        if(instance == null)
            instance = new Network();
        return instance;
    }


    protected Network()
    {
        ip = "192.168.14.147";
        port = 7070;
        inQueue = new Queue<Query>();

        lock = new ReentrantLock();
        lockMessages = new lock();
        lockChats = new lock();

        logger = Logger.getLogger(Network.class.getName());

        activeChats = new ArrayList<ChatActivity>();
    }




    /*
    add a query to the out queue safely
     */
    protected void addToInQueue(Query q)
    {
        lock.lock();
        inQueue.enqueue(q);
        lock.unlock();
        synchronized (lockMessages) {
            lockMessages.notifyAll();
        }
    }

    /*
    safetly add chat to the list
     */
    public void addToActiveChatList(ChatActivity chat)
    {
        synchronized (lockChats)
        {
            activeChats.add(chat);
        }
    }

    /*
    saftly remove chat from the list
     */
    public void removeFromActiveChatList(ChatActivity chat)
    {
        synchronized (lockChats)
        {
            activeChats.remove(chat);
        }
    }



    /*
    saftly get the first object
    return null if the queue is empty
     */
    protected Query getFromInQueue()
    {
        Query q;

        lock.lock();
        if(!inQueue.isEmpty())
            q = inQueue.dequeue();
        else
            q = null;
        lock.unlock();

        return q;
    }


    public void run()
    {
        try {
            socket = new Socket(ip, port);
            input = socket.getInputStream();
            OutputStream output = socket.getOutputStream();
            new Thread(SendData.getInstance(output)).start();
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"Can't connect to server : "+e.toString());
        }


        while(true) {
            Query q;
            q = communicateWithServer();
            parseQuery(q);
        }
    }


    /*
    parse queries that got from the server
     */
    private void parseQuery(Query q)
    {
        switch(q.getOpCode())
        {
            case Constants.sendMessage_server:
                gotMessage(q.getMsg()[0]);
                Log.d("message","appertly success :"+q.getMsg()[0].GetData());
                break;
            default:
                addToInQueue(q);
                break;
        }
    }

    /*
    take care of message that sent to this device by another device
     */
    private void gotMessage(Message msg)
    {
        String destPhone = msg.get_destination();

        synchronized(lockChats)
        {
            for (int i = 0; i < activeChats.size(); i++) {
                if (activeChats.get(i).getChatPhone().compareTo(destPhone) == 0) {
                    activeChats.get(i).onGetMessage(msg);
                    return;
                }
            }
        }
        //Log.e("message","dont have that chat open");
        //maybe open the chat?
        // the user will get his message from the server
    }




/*
    public static String getMAC(Context context)
    {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }


    public void setMAC(Context context)
    {
        String MACAddress = Network.getMAC(context);
    }
*/
    /*
      deals with the network side of signing up
      return true if signUp done
      return false if problem occurred
       */
    public boolean signUp(String userName,String password,String phoneNumber, LoginActivity.UserRegisterTask registerTask)
    {
        mPhoneNumber = phoneNumber;
        String[] params = {userName,password,phoneNumber}; // need security for password
        Query q = new Query(Constants.signUp_client,params);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.signUp_server);
        if (answer.getStr()[0].compareTo(Integer.toString(Constants.success)) == 0)
            return true;
        registerTask.setFailure(answer.getStr()[1]);
        return false;

    }

    /*
    return true if there's user exists with this phone
    return false if not, or error occured
     */
    public boolean isUserExists(String phoneNumber)
    {
        String[] params = {phoneNumber};
        Query q = new Query(Constants.isUserExists_client,params);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.getUser_server);
        return answer.getStr()[0].compareTo(Integer.toString(Constants.success)) == 0;
    }

    /*
    ask the server for the history messages of this client and the parameter account
    return array of the messages
     */
    public Message[] getMessagesHistory(String targetPhoneNumber)
    {
        String[] params = {mPhoneNumber,targetPhoneNumber};
        Query q = new Query(Constants.getMessagesHistory_client,params);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.getMessagesHistory_server);
        return answer.getMsg();

    }


    /*
    ask the server for the history messages of this client and the parameter account
    return array of the messages
     */
    public Message[] getAllMessages()
    {
        String[] params = {mPhoneNumber};
        Query q = new Query(Constants.getAllMessages_client,params);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.getAllMessages_server);
        return answer.getMsg();

    }

    /*
    contants the server the ask the details of the user whos have the phonenumber paramter
    return null if there's problem, or the details (username,phonenumber)
     */
    public User getUserByPhone(String phoneNumber)
    {
        String[] params = {phoneNumber};
        Query q = new Query(Constants.getUser_client,params);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.getUser_server);
        if(answer.getStr()[0].compareTo(Integer.toString(Constants.failure)) == 0)
            return null;
        return new User(answer.getStr()[1],answer.getStr()[2]);
    }


    public boolean sendMessage(Message msg)
    {
        Message[] msgs = {msg}; //query work with array of messages
        Query q = new Query(Constants.sentMessage_client,msgs);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.sentMessage_server);
        return answer.getStr()[0].compareTo(Integer.toString(Constants.success)) == 0;
    }

    /*
    Generic method for all the methods that communicate with the server.
    the methid wait until message with the specific opcode arrives
     */
    private Query waitForResponse(int opcode)
    {
        Query q;
        while(true) {
            try {
                synchronized (lockMessages) {
               // lock.lock();
                while(inQueue.isEmpty()) {
                //    lock.unlock();
                        lockMessages.wait();
                    }
                }

            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Problem with condition on singUp :" + e.getMessage());
            }

            lock.lock();
            if (inQueue.isEmpty() || inQueue.peek().getOpCode() !=opcode)
                lock.unlock();
            else
                break;
        }
        q = inQueue.dequeue();
        lock.unlock();
        return q;
    }


    /*
    deals with the network side of signing in
     */
    public boolean signIn(String userName,String password, String phoneNumber, LoginActivity.UserLoginTask loginTask)
    {
        mPhoneNumber = phoneNumber;
        String[] params = {userName,password}; // need security for password
        Query q = new Query(Constants.signIn_client,params); // need to include the library
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.signIn_server);
        if(answer.getStr()[0].compareTo(Integer.toString(Constants.success)) == 0)
            return true;
        loginTask.setFailure(answer.getStr()[1]);
        return false;
    }




    public boolean sentMessage(String destination, String text)
    {
        Message[] msg = {new Message(text,mPhoneNumber,destination)};
        Query q = new Query(Constants.sentMessage_client,msg);
        SendData.getInstance().addToOutQueue(q);

        Query answer = waitForResponse(Constants.sentMessage_server);
        return Integer.getInteger(answer.getStr()[0]) == Constants.success;
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
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.e("message", "cant put thread to sleep");
                    return false;
                }

            }
        }
        catch(Exception e)
        {
            Log.e("message", "problem with getting available data from server");
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
