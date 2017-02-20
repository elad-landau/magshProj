package serverLibrary;

import java.util.LinkedList;
import java.util.concurrent.locks.*;

import commonLibrary.Message;
import commonLibrary.Query;
import commonLibrary.Queue;

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
<<<<<<< HEAD
=======
		System.out.println("adi");
		Thread logThread = new Thread(Logic.getInstance());
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
>>>>>>> aee4cd708391266cb99938d94ae3e7179d43a1fd

		DBWrapper DB = DBWrapper.getInstance();
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
			case 0:
				break;
				
				
				default:
					
			}
		}
		
		
	}
}
