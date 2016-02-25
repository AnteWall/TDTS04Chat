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
    	}else if(activeUsers.containsKey(callObj)){
    		callObj.callback("You are aleady registred");
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
    
    public void joinGame(ChatCallback callObj, String piece){
    	if(game.isActive()){
    		callObj.callback("Game is already in progress");
    		return;
    	}
    	if(game.addPlayer(callObj, activeUsers.get(callObj))){
    		game.addPlayer(callObj, piece);
    		callObj.callback("Joined the game as : " + piece);
    		if(game.getPLayers().size() == 2){
    			sendToPlayers("Game has begun! Type Place <pos> to place. 'X' will start");
    			game.startGame();
    			sendToPlayers(game.getBoard());
    		}
    	}else{
    		callObj.callback("Piece has already been taken, try with the other piece!");
    	}
    }
    
    public void placeMarker(ChatCallback callObj, String position){
    	if(!game.isActive()){
    		callObj.callback("Game not started yet, more players need to join");
    		return;
    	}
    	if(game.activePlayer(callObj)){
    		game.placeMarker(callObj, position);
    		sendToPlayers(game.getBoard());
    		if(game.isGameOver()){
    			sendToPlayers("Game Over!!! Winner is: " + game.getWinner());
    			game.restart();
    		}
    	}else{
    		callObj.callback("You are not active in this game session");
    	}
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
    
    private void sendToPlayers(String msg){
    	for(ChatCallback callObj: game.getPLayers().keySet())
    	{
    		callObj.callback(msg);
    	}    
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
