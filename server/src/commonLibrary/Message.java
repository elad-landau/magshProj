package commonLibrary;


import serverLibrary.ConfigurationManager;

public class Message extends Utility
{
	private static long serialVersionUID;
	private String _data;
	private String _origin; // phone number
	private String _destination; //phone number
	
	
	
	public Message(String data, String origin, String destination) 
	{
		
		this._data = data;
		this._origin = origin;
		this._destination = destination;
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


	
	
}