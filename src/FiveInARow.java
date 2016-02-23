import java.util.HashMap;
import java.util.Map;

import ChatApp.ChatCallback;

public class FiveInARow {
	
	private Map<ChatCallback, String> players;
	private int SIZE = 5;
	private String [][] grid;
	private boolean active;
	
	public FiveInARow(){
		players = new HashMap<>();
		grid = new String[SIZE][SIZE];
	}
	
	public void addPlayer(ChatCallback callObj,String user){
		players.put(callObj, user);
	}
	
	public String getBoard(){
		String board = "";
		for(int y = 0; y < grid.length; y++){
			board = "|";
			for(int x = 0; x < grid[y].length; x++){
				board += " |";
			}
		}
		return board;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
