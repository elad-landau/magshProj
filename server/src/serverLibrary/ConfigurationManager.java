package serverLibrary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class ConfigurationManager
{
	 
	 InputStream inputStream;
	 private static ConfigurationManager instance;
	 private Properties prop;
	 
	 			
	 
	 public long group_serial;
	 public long memberEntity_serial;
	 public long message_serial;
	 public long user_serial;
	 public int port;
	 public int threads_number;
	 public String date_format;
	 
	 
	
	 
	 public long getGroup_serial() {
		return group_serial;
	}

	public long getMemberEntity_serial() {
		return memberEntity_serial;
	}

	public long getMessage_serial() {
		return message_serial;
	}

	public long getUser_serial() {
		return user_serial;
	}

	public int getPort() {
		return port;
	}

	public int getThreads_number() {
		return threads_number;
	}

	public String getDate_format()
	{
		return date_format;
	}
	
	public static ConfigurationManager getInstance()
	 {
		  if (instance == null) {
			  try
			  {
				  instance = new ConfigurationManager();
			  }
			  catch(FileNotFoundException e)
			  {
				  instance = null;
				  System.out.println("the problem : "+e.getMessage());
			  }
		  }
		  return instance;
	 }
	 
	 protected ConfigurationManager() throws FileNotFoundException
	 {
	 
		  try
		  {
		   prop = new Properties();
		   String propFileName = "config.properties";
		 
		   inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
		 
		   if (inputStream != null) 
		   {
			System.out.println("inputStream isnt null");
		    prop.load(inputStream);
		   } 
		   else 
		   {
			   throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			   //System.out.println("inputStream is null");
		   }
		 
		   // get the property value and print it out
		   group_serial = Long.parseLong(prop.getProperty("group_serial"));
		   memberEntity_serial = Long.parseLong(prop.getProperty("memberEntity_serial"));
		   message_serial = Long.parseLong(prop.getProperty("message_serial"));
		   user_serial = Long.parseLong(prop.getProperty("user_serial"));
		   port = Integer.parseInt(prop.getProperty("port"));
		   threads_number = Integer.parseInt(prop.getProperty("threads_number"));
		   date_format = prop.getProperty("date_format");
		 
		  } catch (Exception e) {
			
		  } finally {
		   try {
		    inputStream.close();
		   } catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		   }
		  }
	 }
}