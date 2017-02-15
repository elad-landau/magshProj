package serverLibrary;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.LinkedList;
import commonLibrary.*;

public class Network
{
	private static Network instance = null;
	private int port;
	private int threads_number;
	private ServerSocket mainSocket;
	private Socket clientSocket;
	private boolean isStopped;
	private ExecutorService threadPool;
	
	
	
	/*
	 * A safe method for keeping only one instance of class
	 * 
	 * if instance hasn't been made yet, make one by the protected constructor
	 * 
	 * @return static instance of Network
	 */
	public static Network getInstance()
	{
		if(instance == null)
			instance = new Network();
		return instance;
	}
	
	
	/*
	 * protected constructor for only the class and subclass use
	 */
	protected Network()
	{
		
		
		clientSocket = null;
		this.port = ConfigurationManager.getInstance().getPort();
		this.threads_number = ConfigurationManager.getInstance().getThreads_number();
		threadPool = Executors.newFixedThreadPool(threads_number);
		System.out.println("started server socket");
		
		try
		{
		mainSocket = new ServerSocket(port);
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.CRITICAL , this.getClass().getName(),"Cannot open the port: "+e);
		}
		
		
	}
	
	
	public void serv()
	{
		listenAndAccept();
	}
	
	
	/*
	 * Listen for clients requests, and make the ClientHandler take care of each request (threads)
	 */
	private void listenAndAccept()
	{
		while(!isStopped())
		{
			clientSocket = null;
			try
			{
				clientSocket = mainSocket.accept();
			}
			catch(IOException e)
			{
				DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.ERROR ,this.getClass().getName(),"Error accepting client :"+e);
			}
			
			threadPool.execute(new ClientHandler(clientSocket));
		}
		threadPool.shutdown();
	}
	
	
	
	
	
	
	/*
	 * Synchronized method for checking the status of the 'isStopped' field
	 * 
	 * @return the status of 'isStopped' field
	 */
	private synchronized boolean isStopped()
	{
		return isStopped;
	}
	
	
	private synchronized void stop()
	{
		isStopped = true;
		try
		{
			mainSocket.close();
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.ERROR , this.getClass().getName(),"Error closing the server: "+e);
		}
	}
}
