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
		try
		{
		InputStream input = clientSocket.getInputStream();
		OutputStream output = clientSocket.getOutputStream();
		}
		catch(IOException e)
		{
			DBWrapper.getInstance().writeLog("warning", this.getClass().getName(), "error getting data from client : "+e);
		}
	}

}
