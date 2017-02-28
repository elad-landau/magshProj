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

		
		dataTypes.add("INTEGER NOT NULL PRIMARY KEY");
		columnName.add("ID");
		 
		dataTypes.add("STRING");
		columnName.add("name");
	
		dataTypes.add("STRING");
		columnName.add("password");
		
		dataTypes.add("STRING");
		columnName.add("currentIP");
		
		//Turn name into an unique column
		dataTypes.add("(name)");
		columnName.add("UNIQUE");
			
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
				"(logLevel, class, time, message)" +
				"VALUES ('" +
				logLevel.toString() + "', " + Class + ", '" + DateFormat.format(date) + "', '" + message + "');";
		try{
			runCommand(sql);
		}catch (SQLException ex) {
			return false;
		}
		return true;
	}

	
	public boolean signIn(String userName, String password)
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
	
	public boolean signUp(String userName, String password, String currentIP) throws Exception
	{
		String sql = "INSERT INTO " + usersTable
				+ "(name, password, currentIP) "
				+ "VALUES(\""
				+ userName + "\",\""
				+ password + "\",\""
				+ currentIP + "\");";
		try{
			runCommand(sql);
		}catch (SQLException ex) {
			if(ex.getErrorCode() == uniqueErrorCode)
			{
				Exception e = new Exception("Username already exists");
				throw e;
			}
			return false;
		}
		return true;
	}
	
	public boolean signUp(String userName, String password) throws Exception
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
				Exception e = new Exception("Username already exists");
				throw e;
			}
			return false;
		}
		return true;
	}
	
	public boolean isUserExist(String userName)
	{
		ResultSet rs;
		boolean exist = false;
		String sql = "SELECT * FROM" + usersTable
				+ "WHERE name = \""
				+ userName + "\";";
		try{
			rs = runCommand(sql).getResultSet();
			exist = rs.next();
		}catch (SQLException ex) {
			return false;
		}
		return exist;
	}
}
