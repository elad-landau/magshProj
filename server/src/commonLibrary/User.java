package commonLibrary;

import serverLibrary.ConfigurationManager;

public class User extends MemberEntity
{

	private static long serialVersionUID;

	public User(int id,String name)
	{
		super(id,name);
		this.serialVersionUID = ConfigurationManager.getInstance().getUser_serial();
	}
	
	
}