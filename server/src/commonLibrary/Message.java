package commonLibrary;


import serverLibrary.ConfigurationManager;

public class Message extends Utility
{
	private static long serialVersionUID;
	private int opCode;
	private String _data;
	private MemberEntity _addresse;
	private MemberEntity _addressed;
	
	public Message(int opCode,String data, MemberEntity addresse, MemberEntity addressed) 
	{
		
		this._data = data;
		this._addresse = addresse;
		this._addressed = addressed;
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
	
	public MemberEntity GetAddresse()
	{
		return this._addresse;
	}
	
	public void SetAddresse(MemberEntity addresse)
	{
		this._addresse = addresse;
	}
	
	public MemberEntity GetAddressed()
	{
		return this._addressed;
	}
	
	public void SetAddressed(MemberEntity addressed)
	{
		this._addressed = addressed;
	}
	

	
	
	

}