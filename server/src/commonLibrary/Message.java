package commonLibrary;


import java.util.Date;

import serverLibrary.ConfigurationManager;

public class Message extends Utility
{
	private static long serialVersionUID;
	private String _data;
	private int _origin; // phone number
	private int _destination; //phone number
	private Date _sentTime;
	
	
	public Message(int data, int origin, String destination, Date sentTime) 
	{
		this._sentTime = _sentTime;
		this._data = data;
		this._origin = origin;
		this._destination = destination;
		this.serialVersionUID = ConfigurationManager.getInstance().getMessage_serial();
	}

	public Message() {
		this._sentTime = new Date();
		this._data = "";
		this._origin = "";
		this._destination = "";
		this.serialVersionUID = ConfigurationManager.getInstance().getMessage_serial();

	}
	
	//Variables Gets and Sets
	
	public String GetData()
	{
		return this._data;	
	}
	
	public void SetData(String data)
	{
		this._data = data;
	}

	public int getOrigin() {
		return _origin;
	}

	public void setOrigin(int _origin) {
		this._origin = _origin;
	}

	public int getDestination() {
		return _destination;
	}

	public void setDestination(int _destination) {
		this._destination = _destination;
	}

	public Date GetSentTime()
	{
		return this._sentTime;	
	}
	
	public void SetSentTime(Date sentTime)
	{
		this._sentTime = sentTime;
	}
	
	
}