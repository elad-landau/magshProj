package serverLibrary;

import java.net.Socket;
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
		try
		{
		input = clientSocket.getInputStream();
		output = clientSocket.getOutputStream();
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog("warning", this.getClass().getName(), "error getting data from client : "+e);
		}
		
		while(true)
		{
			try
			{
				if(input.available()<4) // legnth of int
				{
					Thread.sleep(10);
				}
			}
			catch(IOException e)
			{
				DBWrapper.getInstance().writeLog("debug", this.getClass().getName(), "cant get 'input' available bytes : "+e.getMessage());
			}
			catch(InterruptedException e)
			{
				DBWrapper.getInstance().writeLog("debug", this.getClass().getName(), "problem with putting the thread to sleep : "+e.getMessage());
			}
			
			length = input.read(4, off, len)
		}
	}

}
