package serverLibrary;


import java.util.Vector;
import java.util.concurrent.locks.*;

import commonLibrary.Message;
import commonLibrary.Query;
import commonLibrary.Queue;
import commonLibrary.User;
import commonLibrary.Constants;



public class Logic implements Runnable
{
	private Queue<Query> queue;
	private static Logic instance;
	private final ReentrantLock lock;
	private Vector<User> onlineUsers;
	
	protected Logic()
	{
		queue = new Queue<Query>();
		lock = new ReentrantLock();
		onlineUsers = new Vector<User>();
	}
	
	public static Logic getInstance()
	{
		if(instance == null)
			instance = new Logic();
		return instance;
	}
	
	
	public static void main(String[] args)
	{
		Thread logThread = new Thread(Logic.getInstance());
		DBWrapper.getInstance();
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
				
			case Constants.signIn_client:
				handleSignIn(q);
				break;
				
			case Constants.sentMessage_client:
				handleSentMessage(q);
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
		User user = null;
		
		if(DBWrapper.getInstance().isUserExist(q.getStr()[0]))
		{
			strs = new String[2];
			strs[0] = Integer.toString(Constants.failure);
			strs[1] = "username already exist";
		}
		else if(DBWrapper.getInstance().signUp(q.getStr()[0], q.getStr()[1]))
		{
			strs = new String[1];
			strs[0] = Integer.toString(Constants.success);
			user = new User(q.getStr()[0], q.getStr()[1],Integer.parseInt( q.getStr()[2]) , q.getHandler() );
			onlineUsers.addElement(user);
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

	
	
	private void handleSignIn(Query q)
	{
		Query answer;
		String strs[];
		
		if(!DBWrapper.getInstance().isUserExist(q.getStr()[0]))
		{
			strs = new String[2];
			strs[0] = Integer.toString(Constants.failure);
			strs[1] = "username isn't exist";
		}
		
		if(DBWrapper.getInstance().isUsernameAndPasswordMatch(q.getStr()[0],q.getStr()[1]))
		{
			//int phoneNumber = DBWrapper.getPhoneByName(q.getStr()[0]);
			int phoneNumber = 5;
			User user = new User(q.getStr()[0],q.getStr()[1] , phoneNumber, q.getHandler());
			onlineUsers.addElement(user);
			strs = new String[1];
			strs[0] = Integer.toString(Constants.success);
		}
		
		else
		{
			strs = new String[2];
			strs[0] = Integer.toString(Constants.failure);
			strs[1] = "username and password don't match";
		}

		
		answer = new Query(Constants.signIn_server,strs);
		q.getHandler().sendData(answer);
	}
	
	private void handleSentMessage(Query q)
	{
		Query answer,message;
		String[] strs;
		
		//
		//
		//find User by phoneNumber
		//
		//
		int indexOfUser = onlineUsers.indexOf(q.getMsg().getDestination());
		if(indexOfUser == -1) // not found
		{
			strs = new String[2];
			strs[0] =Integer.toString(Constants.failure);
			strs[1] = "user not connected";
			
			answer = new Query(Constants.sentMessage_server,strs);
			q.getHandler().sendData(answer);
		}
		
		strs = new String[1];
		strs[0] =Integer.toString(Constants.success);
		answer = new Query(Constants.sentMessage_server,strs);
		
		message = new Query(Constants.sendMessage_server,q.getMsg());
		
		
		q.getHandler().sendData(answer);
		onlineUsers.get(indexOfUser).getHan().sendData(message);
	}

}