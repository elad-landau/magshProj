package commonLibrary;

import java.io.IOException;

public class Query extends Utility 
{
	private int opCode;
	private Union un;
	
	
	public Query(int opCode, Message msg)
	{
		this.opCode = opCode;
		this.un = new Union(msg);
	}
	
	public Query(int Opcode,String[] str)
	{
		this.opCode = Opcode;
		this.un = new Union(str);
	}
	
	
	/*
	 * return false for the union holding Message
	 * return true for the union holding string
	 */
	public boolean msgOrStr()
	{
		return un.msgOrStr();
	}
	
	public int getOpCode() {return opCode;}
	public Message getMsg() { return un.getMsg();}
	public String[] getStr() {return un.getStr();}
	
	
	private class Union
	{
		private Message msg;
		private String[] str;
		private boolean msgOrStr;
		
		public Union(Message msg)
		{
			this.msg = msg;
			str = null;
			msgOrStr = false;
		}
		public Union(String[] str)
		{
			this.str = str;
			msg = null;
			msgOrStr = true;
		}
		
		/*
		 * return false for the union holding Message
		 * return true fot the union holding string
		 */
		public boolean msgOrStr()
		{
			return msgOrStr;
		}
		
		public Message getMsg() {return msg;}
		public String[] getStr() {return str;}
		
		
		
	}

}