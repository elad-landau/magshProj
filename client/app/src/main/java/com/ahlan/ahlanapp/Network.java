/**
 * Created by Administrator on 1/22/2017.
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


import android.app.DownloadManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.ahlan.ahlanapp.*;



public class Network
{
    Socket socket;
    String ip;
    int port;
    Network instance = null;
    InputStream input;
    OutputStream output;
    String MACAddress;
    private static Logger logger = Logger.getLogger(Network.class.getName());


    public Network getInstance()
    {
        if(instance == null)
            instance = new Network();
        return instance;
    }


    protected Network()
    {
        ip = "127.0.0.1";
        port = 7070;

        try {
            socket = new Socket(ip, port);
            input = socket.getInputStream();
            output = socket.getOutputStream();
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"Can't connect to server : "+e.toString());
        }


    }


    /*
     a private method
     deals with serializtion and the sending of date to the server
      */
    private void sendData(Que q)
    {
        byte[] data = q.serialize();
        try {
            output.write(data);
        }
        catch(IOException e)
        {
            logger.log(Level.WARNING,"cant write to server : "+e.toString());
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
    deals with the network side of signing in
     */
    public void signIn(String userName,String password,String email)
    {
        String[] params = {userName,password,email}; // need security for password
        Query q = new Query(Constants.signIn_client,params); // need to include the library
        sendData(q);
    }

    //public signup(String email,String )

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
