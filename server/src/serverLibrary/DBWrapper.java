package serverLibrary;

import commonLibrary.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;


public class DBWrapper 
{
	private static DBWrapper instance = null;
	private Connection connection = null;
	
	private final String usersTable = "Users";
	private final String groupsTable = "Groups";
	private final String messagesTable = "Messages";
	private final String logTable = "Log";
	private SimpleDateFormat DateFormat;
	
	private final int uniqueErrorCode = 19;
	
	public enum LogLevels {
	INFO{
		@Override
		public String toString() {
			return "INFO";
		}
	},		
	DEBUG{
		@Override
		public String toString() {
			return "DEBUG";
		}
	},
	WARNING{
	    @Override
	    public String toString() {
	    	return "WARNING";
	    }
	},
	ERROR{
	    @Override
	    public String toString() {
	    	return "ERROR";
	    }
	},
	CRITICAL{
		@Override
		public String toString() {
			return "CRITICAL";
		}
	}
	};
	
	protected DBWrapper() {
		DateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {//Open the data base
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:serverData.db");
		} catch (SQLException | ClassNotFoundException ex) {
			System.out.println(ex.getMessage());
			System.err.println( ex.getClass().getName() + ": " + ex.getMessage() );
		}
	}
	
	public void createAllTables()
	{
		List<String> dataTypes = new ArrayList<String>();
		List<String> columnName = new ArrayList<String>();

		
		dataTypes.add("STRING NOT NULL");
		columnName.add("phoneNumber");
		
		dataTypes.add("INTEGER PRIMARY KEY AUTOINCREMENT");
		columnName.add("id");
		 
		dataTypes.add("STRING");
		columnName.add("name");
	
		dataTypes.add("STRING");
		columnName.add("password");
		
		//Turn phoneNumber into unique column
		dataTypes.add("(phoneNumber)");
		columnName.add("UNIQUE");
			
		createTable(usersTable, dataTypes, columnName);
		

		dataTypes.clear();
		columnName.clear();
		
		dataTypes.add("INTEGER");
		columnName.add("destination");
		
		dataTypes.add("INTEGER");
		columnName.add("origin");

		dataTypes.add("STRING");
		columnName.add("messageText");
		
		dataTypes.add("TIME");
		columnName.add("sendTime");
		
		createTable(messagesTable, dataTypes, columnName);
		
		
		dataTypes.clear();
		columnName.clear();

		dataTypes.add("STRING");
		columnName.add("logLevel");
		
		dataTypes.add("STRING");
		columnName.add("class");
		
		dataTypes.add("TIME");
		columnName.add("time");
		
		dataTypes.add("STRING");
		columnName.add("message");
		
		createTable(logTable, dataTypes, columnName);
	}
	
	private void createTable(String tabelName, List<String> dataTypes, List<String> columnName) {
		String sql = "CREATE TABLE " + tabelName + " (";
		for (int i = 0; i < columnName.size(); i++) {
		    sql += columnName.get(i) + " " + dataTypes.get(i);
		    if(i != columnName.size()-1)
		    	sql += ", ";
		}
		sql += ");";
		try
		{
			runCommand(sql);
		}catch(SQLException ex) {
			writeLog(DBWrapper.LogLevels.WARNING , DBWrapper.class.getName(), "not able to create DB table" + tabelName + ". :" + ex.getMessage());
		}
	}

	public static DBWrapper getInstance() {
		if (instance == null) {
			instance = new DBWrapper();
		}
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}
	 
	private PreparedStatement runCommand(String sql) throws SQLException
	{
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			stmt.execute();
		} catch (SQLException ex) {
			throw ex;
		}
		return stmt;
	}

	/**
	 * Write a log record.
	 * @param logLevel - get log kind ('info', 'debug', 'warning', 'error', 'critical').
	 * @param Class - the class the record occur in ( this.getClass.getName() ).
	 * @param message -the record message.
	 * @return true if the record has written successfully, false if not.
	 */
	public boolean writeLog(LogLevels logLevel, String Class, String message)
	{
		Date date = new Date();
		
		String sql = "INSERT INTO " +
				logTable +
				"(logLevel, class, time, message) " +
				"VALUES(\"" +
				logLevel.toString() +"\", \"" + Class + "\" , '" + DateFormat.format(date) + "', \"" + message + "\");";
		try{
			runCommand(sql);
		}catch (SQLException ex) {
			return false;
		}
		return true;
	}

	
	public boolean isUsernameAndPasswordMatch(String userName, String password)
	{
		ResultSet rs;
		String sql = "SELECT password FROM " +
				usersTable +
				" WHERE name = \"" +
				userName + "\";";
		try{
			rs = runCommand(sql).getResultSet();
			return password.equals(rs.getString("password"));
		}catch (SQLException ex) {
			return false;
		}
	}
	
	public boolean signUp(String userName, String password, String phoneNumber)
	{
		String sql = "INSERT INTO " + usersTable
				+ "(phoneNumber, name, password ) "
				+ "VALUES(\""
				+ phoneNumber + "\", \""
				+ userName + "\",\""
				+ password + "\");";
		try{
			runCommand(sql);
		}catch (SQLException ex) {
			if(ex.getErrorCode() == uniqueErrorCode)
			{
				this.writeLog(DBWrapper.LogLevels.WARNING, this.getClass().getName(),"Username already exists");
			}
			return false;
		}
		return true;
	}
	
	public boolean signUp(String userName, String password)
	{
		String sql = "INSERT INTO " + usersTable
				+ "(name, password) "
				+ "VALUES(\""
				+ userName + "\",\""
				+ password + "\");";
		try{
			runCommand(sql);
		}catch (SQLException ex) {
			if(ex.getErrorCode() == uniqueErrorCode)
			{
				this.writeLog(DBWrapper.LogLevels.WARNING, this.getClass().getName(),"Username already exists");
			}
			return false;
		}
		return true;
	}
	
	public boolean isUserExist(String userName)
	{
		ResultSet rs;
		boolean exist = false;
		String sql = "SELECT * FROM " + usersTable
				+ " WHERE name = \""
				+ userName + "\";";
		try{
			rs = runCommand(sql).getResultSet();
			exist = rs.next();
		}catch (SQLException ex) {
			return false;
		}
		return exist;
	}
	
	public User getUser(String phoneNumber) 
	{
		ClientHandler han = null;
		ResultSet rs;
		String sql = "SELECT * FROM " +
				usersTable +
				" WHERE phoneNumber = \"" +
				phoneNumber + "\";";
		try{
			rs = runCommand(sql).getResultSet();
			rs.getString("");
			User user = new User(rs.getString("name"), rs.getString("password"), phoneNumber, han);
			return user;
		}
		catch(SQLException e)
		{
			this.writeLog(DBWrapper.LogLevels.ERROR, this.getClass().getName(), "problem with getting the user who has the phone :"+phoneNumber+", :"+e.getMessage());
			return null;
		}
	}
	
	public String getPhoneByName(String userName)
	{
		ResultSet rs;
		String sql = "select phoneNumber from "+usersTable+" where name = \""+userName+"\" ;";
		
		try
		{
			rs = runCommand(sql).getResultSet();
			//rs.getString("");
			return rs.getString("phoneNumber");
		}
		catch(SQLException e)
		{
			this.writeLog(DBWrapper.LogLevels.ERROR, this.getClass().getName(), "problem with getting the phoneNumber who has the name :"+userName+", :"+e.getMessage());
			return null;
		}
	}
	
	public Vector<Message> getChat(String userName)
	{
		Vector<Message> messages = new Vector<Message>();
		Message message = new Message();
		ResultSet rs;
		String sql = "select * from " +
			messagesTable +
			" where name = \"" +
			userName +
			"\" ;";
			
		try
		{
			rs = runCommand(sql).getResultSet();
			while (rs.next()) {
				message.setDestination(rs.getInt("destination"));
				message.setOrigin(rs.getInt("origin"));
				message.SetData(rs.getString("messageText"));
				message.SetSentTime(rs.getTime("sendTime"));
				messages.add(message);
			}
		}
		catch(SQLException e)
		{
			this.writeLog(DBWrapper.LogLevels.ERROR, this.getClass().getName(), "problem with getting the phoneNumber who has the name :"+userName+", :"+e.getMessage());
			return null;
		}
		return messages;
	}
}
