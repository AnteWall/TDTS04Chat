package ChatApp;


/**
* ChatApp/ChatOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Chat.idl
* Friday, March 11, 2016 4:20:40 PM CET
*/

public interface ChatOperations 
{
  String say (ChatApp.ChatCallback objref, String message);
  boolean join (ChatApp.ChatCallback objref, String user);
  boolean leave (ChatApp.ChatCallback objref);
  String list (ChatApp.ChatCallback objref);
  void joinGame (ChatApp.ChatCallback objref, String piece);
  void placeMarker (ChatApp.ChatCallback objref, String position);
} // interface ChatOperations
