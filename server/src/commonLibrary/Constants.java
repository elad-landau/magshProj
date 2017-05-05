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
    public static final int getUser_client = 104;
    public static final int isUserExists_client = 105;
    public static final int getMessagesHistory_client = 206;
    

    
    //server
    public static final int signUp_server = 201;
    public static final int signIn_server = 202;
    public static final int sentMessage_server = 203;
    public static final int sendMessage_server = 2030;
    public static final int getUser_server = 204;
    public static final int isUserExists_server = 205;
    public static final int getMessagesHistory_server = 206;
    

    //time
    public static final long timeBetweenSends = 1 *1000;
}
