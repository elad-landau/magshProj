package commonLibrary;

import java.util.LinkedList;

public class Queue<T>
{
	private LinkedList<T> queue;
	
	public Queue()
	{
		queue = new LinkedList<T>();
	}
	
	public boolean isEmpty()
	{
		return queue.size() ==0;
	}
	
	public void enqueue(T item)
	{
		queue.add(item);
	}
	
	public T dequeue()
	{
		T item = (T)queue.get(1);
		queue.remove(1);
		return item;
	}
	
	public T peek()
	{
		T item = (T)queue.get(1);
		return item;
	}
}
