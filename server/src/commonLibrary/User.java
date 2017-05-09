package commonLibrary;

import serverLibrary.ConfigurationManager;

public class User extends MemberEntity
{

	private static long serialVersionUID;
	private String password;
	private String phoneNumber;
	private ClientHandler han;

	public User(String name,String password,String phoneNumber,ClientHandler han)
	{
		super(name);
		this.serialVersionUID = ConfigurationManager.getInstance().getUser_serial();
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.han = han;
	}
	
	public User(String name,String phoneNumber)
	{
		super(name,0);
		this.password = "";
		this.phoneNumber = phoneNumber;
		this.han = null;
	}
	
	public boolean equals(User user)
	{
		String thisPhone;
		String dstPhone;
		
		if(this.phoneNumber.charAt(0) != '0')
			thisPhone = "0" + this.phoneNumber;
		else
			thisPhone = this.phoneNumber;
		
		if(user.phoneNumber.charAt(0) != '0')
			dstPhone = "0" + user.phoneNumber;
		else
			dstPhone = user.phoneNumber;
		
		return thisPhone.compareTo(dstPhone) == 0;
	}
	
	public ClientHandler getHan() {
		return han;
	}

	public void setHan(ClientHandler han) {
		this.han = han;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
}