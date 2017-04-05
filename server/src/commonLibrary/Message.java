package commonLibrary;


import serverLibrary.ConfigurationManager;

public class Message extends Utility
{
	private static long serialVersionUID;
	private String _data;
	private int _origin;
	private int _destination;
	
	
	
	public Message(String data, int origin, int destination) 
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
	
	
	
}