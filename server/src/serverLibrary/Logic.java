package serverLibrary;


import java.util.concurrent.locks.*;

import commonLibrary.Message;
import commonLibrary.Query;
import commonLibrary.Queue;
import commonLibrary.Constants;



public class Logic implements Runnable
{
	private Queue<Query> queue;
	private static Logic instance;
	private final ReentrantLock lock;
	
	
	protected Logic()
	{
		queue = new Queue<Query>();
		lock = new ReentrantLock();
	}
	
	public static Logic getInstance()
	{
		if(instance == null)
			instance = new Logic();
		return instance;
	}
	
	
	public static void main(String args[])
	{
		System.out.println("adi");
		Thread logThread = new Thread(Logic.getInstance());
		DBWrapper DB = DBWrapper.getInstance();
		if( ConfigurationManager.getInstance() == null  || DBWrapper.getInstance()==null)
		{
			System.out.println("server faild to upload");
			try
			{
			Thread.sleep(5000);
			}
			catch(Exception e)
			{
				
			}
			return;
		}
		
		logThread.start();
		Network net = Network.getInstance();
		net.serv();


		
		//Network net = Network.getInstance();
		//net.serv();
	}
	
	
	
	public void addQuery(Query q)
	{
		lock.lock();
		queue.enqueue(q);
		lock.unlock();
	}
	
	
	public void run()
	{
		Query q;
		
		while(true)
		{
			lock.lock();
			while(queue.isEmpty())
			{
				lock.unlock();
				try
				{
				Thread.sleep(1000);
				}
				catch(Exception e)
				{
					DBWrapper.getInstance().writeLog(DBWrapper.LogLevels.WARNING , this.getClass().getName(), "queue faild at sleeping : "+e);
				}
				
				lock.lock();
			}
			
			q = queue.dequeue();
			
			
			lock.unlock();
			
		
			
			switch(q.getOpCode())
			{
			case Constants.empty_query:
				// do nothing
				break;
			case Constants.signUp_client:
				handleSignUp(q);
				break;
				
				default:
					
			}
		}
		
		
	}
	
	
	/*
	 * handle the user try to register
	 * send the user query with the result
	 */
	private void handleSignUp(Query q)
	{
		Query answer;
		String[] strs;
		if(DBWrapper.getInstance().signUp(q.getStr()[0], q.getStr()[1]))
		{
			strs = new String[1];
			strs[0] = Integer.toString(Constants.success);
		}
		else
		{
			strs = new String[2];
			strs[0] = Integer.toString(Constants.failure);
			strs[1] = "There was a problem to register you";
		}
			
		
		answer = new Query(Constants.signUp_server,strs);
		q.getHandler().sendData(answer);
	}
}
