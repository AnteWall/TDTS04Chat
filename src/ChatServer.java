import ChatApp.*;          // The package containing our stubs. 
import org.omg.CosNaming.*; // HelloServer will use the naming service. 
import org.omg.CosNaming.NamingContextPackage.*; // ..for exceptions. 

import java.util.HashMap;
import java.util.Map;

import org.omg.CORBA.*;     // All CORBA applications need these classes. 
import org.omg.PortableServer.*;   
import org.omg.PortableServer.POA;
 
class ChatImpl extends ChatPOA
{
    private ORB orb;

    private Map<ChatCallback, String> activeUsers;
    
    private FiveInARow game;
    
    public void setORB(ORB orb_val) {
        orb = orb_val;
    }
    
    public ChatImpl() {
    	super();
    	this.activeUsers = new HashMap<ChatCallback, String>();
    	this.game = new FiveInARow();
    }
    
    public boolean join(ChatCallback callObj, String user){
    	if(activeUsers.containsValue(user)){
    		callObj.callback("Username is already taken, choose another one.");
    		return false;
    	}else{
    		sendToAll(user + " joined that chatroom");
    		activeUsers.put(callObj, user);
    	}
    	callObj.callback("You nickname is " + user);
    	return true;
    }
    
    public boolean leave(ChatCallback callObj){
    	if(activeUsers.containsKey(callObj)){
    		sendToAll(activeUsers.get(callObj) + " has left the chatroom");
        	activeUsers.remove(callObj);
        	return true;
    	}
    	return false;
    }
    
    public String list(ChatCallback callObj){
    	String userListStr = "Active Users:\n";
    	for(String user : activeUsers.values()){
    		userListStr += user + "\n";
    	}
    	//callObj.callback(userListStr);
    	return userListStr;
    }

    public String say(ChatCallback callobj, String msg)
    {
        if(userActive(callobj)){
        	String user = activeUsers.get(callobj);
        	sendToAll(user + ": " + msg);
        }else{
            callobj.callback("Need to join before you can chat, type \"join <name>\" to join");
        }
        return "";
    }
    
    private boolean userActive(ChatCallback callObj){
    	if(activeUsers.containsKey(callObj)){
    		return true;
    	}
    	return false;
    }
    
    private void sendToAll(String msg){
    	for(ChatCallback callObj: activeUsers.keySet()){
    		callObj.callback(msg);
    	}
    };
}

public class ChatServer 
{
	
    public static void main(String args[]) 
    {
	try { 
	    // create and initialize the ORB
	    ORB orb = ORB.init(args, null); 
	    // create servant (impl) and register it with the ORB
	    ChatImpl chatImpl = new ChatImpl();
	    chatImpl.setORB(orb); 

	    // get reference to rootpoa & activate the POAManager
	    POA rootpoa = 
		POAHelper.narrow(orb.resolve_initial_references("RootPOA"));  
	    rootpoa.the_POAManager().activate(); 

	    // get the root naming context
	    org.omg.CORBA.Object objRef = 
		           orb.resolve_initial_references("NameService");
	    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	    // obtain object reference from the servant (impl)
	    org.omg.CORBA.Object ref = 
		rootpoa.servant_to_reference(chatImpl);
	    Chat cref = ChatHelper.narrow(ref);

	    // bind the object reference in naming
	    String name = "Chat";
	    NameComponent path[] = ncRef.to_name(name);
	    ncRef.rebind(path, cref);

	    // Application code goes below
	    System.out.println("ChatServer ready and waiting ...");
	    
	    // wait for invocations from clients
	    orb.run();
	}
	    
	catch(Exception e) {
	    System.err.println("ERROR : " + e);
	    e.printStackTrace(System.out);
	}

	System.out.println("ChatServer Exiting ...");
    }

}
