package commonLibrary;

import serverLibrary.ConfigurationManager;



public abstract class MemberEntity extends Utility
{
	
	private static long serialVersionUID;
	protected int _id;
	protected String _name;
	
	
	
	public int getId() {
		return this._id;
	}

	public void setId(int id) {
		this._id = id;
	}

	public String getName() {
		return this._name;
	}

	public void setName(String name) {
		this._name = name;
	}
	
	
	public MemberEntity()
	{
		this._id = 0;
		this._name = "";
	}
	
	public MemberEntity(int id,String name)
	{
		this._id = id;
		this._name = name;
		this.serialVersionUID = ConfigurationManager.getInstance().getMemberEntity_serial();
	}
	
	
	public boolean isEqual(MemberEntity mem)
	{
		return (this._id == mem._id && this._name.equals(mem._name));
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
