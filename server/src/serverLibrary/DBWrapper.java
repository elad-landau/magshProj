package serverLibrary;

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
	
	public enum LogLevels {INFO , DEBUG , WARNING , ERROR , CRITICAL};
	
	

	protected DBWrapper() throws  SQLException ,ClassNotFoundException {
		DateFormat = new SimpleDateFormat(ConfigurationManager.getInstance().getDate_format());
		
		try {//Open the data base
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:serverData.db");
		} 
		catch (SQLException | ClassNotFoundException ex) 
		{
			throw ex;
		}
	}
	
	public void createAllTables()
	{
		List<String> dataTypes = new ArrayList<String>();
		List<String> columnName = new ArrayList<String>();

		
		dataTypes.add("INTEGER NOT NULL PRIMARY KEY");
		columnName.add("ID");
		 
		dataTypes.add("STRING");
		columnName.add("name");
	
		dataTypes.add("STRING");
		columnName.add("password");
		
		dataTypes.add("STRING");
		columnName.add("currentIP");
			
		createTable(usersTable, dataTypes, columnName);
		
		
		dataTypes.clear();
		columnName.clear();
		
		dataTypes.add("INTEGER NOT NULL PRIMARY KEY");
		columnName.add("ID");
		
		dataTypes.add("STRING");
		columnName.add("name");
		
		dataTypes.add("INTEGER ARRAY[50]");
		columnName.add("users");
		
		createTable(groupsTable, dataTypes, columnName);
		
		
		dataTypes.clear();
		columnName.clear();
		
		dataTypes.add("INTEGER");
		columnName.add("toUserID");
		
		dataTypes.add("INTEGER");
		columnName.add("fromUserID");
		
		dataTypes.add("INTEGER");
		columnName.add("groupID");

		dataTypes.add("TEXT");
		columnName.add("messageText");
		
		dataTypes.add("TIME");
		columnName.add("sendTime");
		
		dataTypes.add("STRING");
		columnName.add("sendStatus");
		
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
			try
			{
			instance = new DBWrapper();
			}
			catch(Exception ex)
			{
				System.out.println("the problem with db Constructor is : "+ex.getMessage());
				instance = null;
			}
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
				"(logLevel, class, time, message)" +
				"VALUES ('" +
				logLevel + "', " + Class + ", '" + DateFormat.format(date) + "', '" + message + "');";
		try{
			runCommand(sql);
		}catch (SQLException ex) {
			return false;
		}
		return true;
	}

	
	
	
}
