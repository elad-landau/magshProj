package commonLibrary;


import java.text.SimpleDateFormat;
import java.util.Date;

import serverLibrary.ConfigurationManager;

public class Message extends Utility
{
	private static long serialVersionUID;
	private String _data;
	private String _origin; // phone number
	private String _destination; //phone number
	private String _sentTime;
	private static final SimpleDateFormat DateFormat  = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	
	public Message(String data, String origin, String destination, Date sentTime) 
	{
		this._sentTime = _sentTime;
		this._data = data;
		this._origin = origin;
		this._destination = destination;
		this.serialVersionUID = ConfigurationManager.getInstance().getMessage_serial();
	}
	
	
	public Message(String data, String origin, String destination) 
	{
		this._sentTime = DateFormat.format(new Date());
		this._data = data;
		this._origin = origin;
		this._destination = destination;
		//this.serialVersionUID = ConfigurationManager.getInstance().getMessage_serial();
	}

	public Message() {
		this._sentTime = DateFormat.format(new Date());
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
	
	public String get_origin() {
		return _origin;
	}

	public void set_origin(String _origin) {
		this._origin = _origin;
	}

	public String get_destination() {
		return _destination;
	}

	public void set_destination(String _destination) {
		this._destination = _destination;
	}

	public String GetSentTime()
	{
		return this._sentTime;	
	}
	
	public void SetSentTime(String sentTime)
	{
		this._sentTime = sentTime;
	}
	/*
	public void SetSentTime(String sentTime)
	{
		this._sentTime = new Date(sentTime);
	}
	*/
}