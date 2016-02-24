import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ChatApp.ChatCallback;

public class FiveInARow {
	
	private Map<ChatCallback, String> players;
	private int SIZE = 5;
	private char [][] grid;
	private boolean active;
	private int spacesUnocupied;
	private boolean gameOver;
	private String winner;
	
	public FiveInARow(){
		players = new HashMap<>();
		grid = new char[SIZE][SIZE];
		restart();
	}
	
	public boolean addPlayer(ChatCallback callObj,String piece){
		if(players.containsValue(piece)){
			return false;
		}
		players.put(callObj, piece);
		return true;
	}
	
	public void restart(){
		spacesUnocupied = SIZE*SIZE;
	    for(int i = 0; i < SIZE; ++i)
	    {
	      Arrays.fill(grid[i], ' ');
	    }
	    players.clear();
	}
	
	public String getBoard(){
		String board = "  A B C D E F G H I J K L M N ".substring(0, (SIZE*2)+2);
		board += "\n";
		for(int y = 0; y < grid.length; y++){
			board += y + "|";
			for(int x = 0; x < grid[y].length; x++){
				board += grid[y][x] + "|";
			}
			board += "\n";
		}
		return board;
	}
	
	public boolean isGameOver(){
		checkRows();
		return gameOver;
	}
	private void checkRows(){
		String rowStr = "";
		for(int i = 0; i < SIZE; i++){
			rowStr = new String(grid[i]);
			Pattern regex = Pattern.compile("X{5}");
			Matcher matcher = regex.matcher(rowStr);
			if(matcher.find()){
				gameOver = true;
				winner = "X";
			}
			regex = Pattern.compile("O{5}");
			matcher = regex.matcher(rowStr);
			if(matcher.find()){
				gameOver = true;
				winner = "O";
			}
		}
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Map<ChatCallback, String> getPLayers() {
		return players;
	}

	public boolean activePlayer(ChatCallback callObj) {
		if(players.containsKey(callObj))
			return true;
		return false;
	}

	public void placeMarker(ChatCallback callObj, String position) {
		// TODO Auto-generated method stub
	    int x = Character.toUpperCase(position.charAt(0)) - 'A';
	    int y = position.charAt(1) - '0';
	    if (y < 0 || y > SIZE-1 || x < 0 || x > SIZE-1)
	    {
	    	callObj.callback("Invalid position");
	        return;
	    }
	    if (grid[y][x] == 'X' || grid[y][x] == 'O')
	    {
	    	callObj.callback("Place already taken");
	        return;
	    }
	    grid[y][x] = players.get(callObj).charAt(0);
	    spacesUnocupied--;
	}

	public String getWinner() {
		return winner;
	}
}
