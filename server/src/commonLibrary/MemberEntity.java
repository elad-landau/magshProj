package commonLibrary;

import serverLibrary.ConfigurationManager;



public abstract class MemberEntity extends Utility
{
	
	private static long serialVersionUID;
	protected String _name;
	
	

	public String getName() {
		return this._name;
	}

	public void setName(String name) {
		this._name = name;
	}
	
	
	public MemberEntity()
	{
		this._name = "";
	}
	
	public MemberEntity(String name)
	{
		this._name = name;
		this.serialVersionUID = ConfigurationManager.getInstance().getMemberEntity_serial();
	}
	
	public MemberEntity(String name, long serial)
	{
		this._name = name;
		this.serialVersionUID = serial;
	}
	
	
	public boolean isEqual(MemberEntity mem)
	{
		return (this._name.equals(mem._name));
	}
	
	
	/*
	 * translate the object to stream of bytes (for sending)
	 
	public void getSerialObj(ObjectOutputStream stream) throws IOException
	{
		stream.writeInt(this._id);
		stream.writeObject(this._name);
	}

	
	/*
	 * translate a stream of bytes to an object
	 
	public void readSerialObj(ObjectInputStream stream) throws IOException, ClassNotFoundException 
	{
		this._id = (int) stream.readInt();
		this._name = (String) stream.readObject();
		
	} */
	
	
	
	
} 
