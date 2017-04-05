package commonLibrary;

public class Constants
{
    //opcodes

    public static final int empty_query = 0;
    public static final int success = 0;
    public static final int failure = 1;
    
    
    //client
    public static final int signUp_client = 101;
    public static final int signIn_client = 102;
    public static final int sentMessage_client = 103;


    
    //server
    public static final int signUp_server = 201;
    public static final int signIn_server = 202;
    public static final int sentMessage_server = 203;
    public static final int sendMessage_server = 2030;
    

    //time
    public static final long timeBetweenSends = 1 *1000;
}
