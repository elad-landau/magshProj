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
	
	
	public Message(String data, String _origin, String _destination, Date _sentTime) 
	{
		this._sentTime = DateFormat.format(_sentTime);
		this._data = data;
		this.serialVersionUID = ConfigurationManager.getInstance().getMessage_serial();
		
		if(_origin != "" && _origin.charAt(0) != '0')
			this._origin = '0' + _origin;
		else
			this._origin =_origin;
		
		if(_destination != "" &&_destination.charAt(0) != '0')
			this._destination = '0' + _destination;
		else
			this._destination = _destination;
	}
	
	
	public Message(String data, String _origin, String _destination) 
	{
		this._sentTime = DateFormat.format(new Date());
		this._data = data;
		//this.serialVersionUID = ConfigurationManager.getInstance().getMessage_serial();
		
		if(_origin != "" && _origin.charAt(0) != '0')
			this._origin = '0' + _origin;
		else
			this._origin =_origin;
		
		if(_destination != "" &&_destination.charAt(0) != '0')
			this._destination = '0' + _destination;
		else
			this._destination = _destination;
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
		if(_origin != "" && _origin.charAt(0) != '0')
			this._origin = '0' + _origin;
		else
			this._origin =_origin;
	}

	public String get_destination() {
		return _destination;
	}

	public void set_destination(String _destination) {
		if(_destination != "" &&_destination.charAt(0) != '0')
			this._destination = '0' + _destination;
		else
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