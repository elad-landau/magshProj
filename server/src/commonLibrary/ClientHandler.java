package commonLibrary;

import commonLibrary.*;
import serverLibrary.DBWrapper;
import serverLibrary.Logic;
import serverLibrary.DBWrapper.LogLevels;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/*
 * The class that will handle the clients requests
 */
public class ClientHandler implements Runnable
{
	protected Socket clientSocket = null;
	InputStream input;
	OutputStream output;
	
	public ClientHandler(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
	}
	
	
	/* 
	 * the method that process the client request
	 * 
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		int length;
		Query q;
		
		
		//connecting to client
		try
		{
		input = clientSocket.getInputStream();
		output = clientSocket.getOutputStream();
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.WARNING , this.getClass().getName(), "error getting data from client : "+e);
		}
		
		try
		{
			//contacting with the client
			while(true)
			{
				
				try
				{
				length = getMessageLength(input);
				}
				catch(IOException e)
				{
					DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.DEBUG, this.getClass().getName(), "cant get 'input' available bytes : "+e.getMessage());
					length = -1;
				}
				catch(InterruptedException e)
				{
					DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.DEBUG, this.getClass().getName(), "problem with putting the thread to sleep : "+e.getMessage());
					length = -1;
				}
				
				
				if(length == -1)
				{
					//disconnect client
					DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.INFO,this.getClass().getName() ,"client hasn't responsed for 1 second");
					disconnectClient();
					break;
				}
				
				if(length == 0) // client is connecting but has nothing to send
					continue;
				
				try
				{
				q = getQuery(length,input);
				}
				catch(Exception e)
				{
					DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.ERROR, this.getClass().getName(), "problem with getting message from client : "+e.getMessage());
					continue;
				}
				q.setHandler(this);
				Logic.getInstance().addQuery(q);
				
			}
		}
		catch(Exception e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.INFO,this.getClass().getName() ,"There's problem communicating with client : "+e.getMessage());
		}
	}
	
	
	/*
	 * the method wait and return the length of the message coming from the client
	 * if client exceeds the time limit of additional 0
	 */
	public static int getMessageLength(InputStream input) throws IOException, InterruptedException
	{
		int length;
		int count = 0;
		byte[] buffer = new byte[4];
		
		try
		{
			while(input.available()<4 && count <100) // length of int
			{
				Thread.sleep(10);
				count++;
			}
			
			if(count< 100)
				input.read(buffer, 0, 4);
			else
				return -1;
			
		}
		catch(IOException e)
		{
			throw e;
		}
		catch(InterruptedException e)
		{
			throw e;
		}
		
		ByteBuffer wrapped = ByteBuffer.wrap(buffer);
		return wrapped.getInt();
		
	}

	
	/*
	 * safety disconnect the client
	 */
	private void disconnectClient()
	{	
		try
		{
			input.close();
			output.close();
			clientSocket.close();
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.DEBUG , this.getClass().getName(), "problem with disconnecting client: + "+e.getMessage());
		}
	}
	
	
	/*
	 * get 'length' bytes from the client, and return it in the form of 'Query'
	 */
	public static Query getQuery(int length,InputStream input) throws Exception
	{
		byte[] buffer = new byte[1024];
		Utility util;
		
		try
		{
		input.read(buffer, 0, length);
		util = Utility.deserialize(buffer);
		}
		catch(Exception e)
		{
			throw e;
		}
		
		return (Query)util;
	}
	
	
	/*
	 * send the query q to the output stream
	 * first send the length, and then the query serialize
	 */
	public static void sendData(Query q,OutputStream output) throws IOException, ClassNotFoundException
	{
		try {
	        byte[] data = q.serialize();
	        byte[] dataLength = intToByteArray(data.length);
	        output.write(dataLength);
	        output.write(data);
	     }
	   catch(IOException e)
		{
	       throw e;
	    }
	    catch (ClassNotFoundException e)
	    {
	    	throw e;
	    }
	}
	
	/*
	 * using the static method for more comfort server using
	 * write exception to the db Logger
	 */
	public void sendData(Query q)
	{
		try
		{
			ClientHandler.sendData(q, output);
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.WARNING,this.getClass().getName(),"cant write to server : "+e.toString());
		}
		catch(ClassNotFoundException e)
		{
			DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.WARNING,this.getClass().getName(),"cant convert query into bytes : "+e.toString());
		}
	}
	
	public static byte[] intToByteArray(int num)
	{
       return ByteBuffer.allocate(4).putInt(num).array();
	}
}
