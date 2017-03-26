package commonLibrary;

import serverLibrary.ConfigurationManager;

public class User extends MemberEntity
{

	private static long serialVersionUID;
	private String password;
	private int phoneNumber;

	public User(String name,String password,int phoneNumber)
	{
		super(name);
		this.serialVersionUID = ConfigurationManager.getInstance().getUser_serial();
		this.password = password;
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
}