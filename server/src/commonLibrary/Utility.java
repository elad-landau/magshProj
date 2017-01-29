package commonLibrary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

public abstract class Utility implements java.io.Serializable
{
	
	public static Utility deserialize(byte[] bytes) throws IOException, ClassNotFoundException 
	{ 
		try(ByteArrayInputStream b = new ByteArrayInputStream(bytes))
		{ 
			try(ObjectInputStream o = new ObjectInputStream(b))
			{ 
				return (Utility)(o.readObject());
			} 
			
		}
	}
	
	public static byte[] serialize(Object obj) throws IOException, ClassNotFoundException 
	{ 
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
				ObjectOutput out = new ObjectOutputStream(bos)) 
		{ 
			out.writeObject(obj); 
			return bos.toByteArray();
		} 
		
	}
	
	public byte[] serialize() throws IOException, ClassNotFoundException
	{
		return Utility.serialize(this);
	}
		
}
