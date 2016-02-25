import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ChatApp.ChatCallback;

public class FiveInARow {
	
	private Map<ChatCallback, String> players;
	private int SIZE = 7;
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
	
	public void startGame(){
		active = true;
	}
	
	public void restart(){
		spacesUnocupied = SIZE*SIZE;
	    for(int i = 0; i < SIZE; ++i)
	    {
	      Arrays.fill(grid[i], ' ');
	    }
	    players.clear();
	    active = false;
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
		checkRows(grid);
		checkRows(convertGridVertical(grid));
	    checkRows(convertGridVertical(shiftDiagionalGrid(grid, true)));
	    checkRows(convertGridVertical(shiftDiagionalGrid(grid, false)));
	    if(spacesUnocupied <= 0){
	    	gameOver = true;
	    	winner = "Tied!";
	    }
		return gameOver;
	}
	private void checkRows(char[][] inputGrid){
		String rowStr = "";
		for(int i = 0; i < SIZE; i++){
			rowStr = new String(inputGrid[i]);
			matchString(rowStr);
		}
	}
	
	private char[][] convertGridVertical(char[][] inputArray)
	  {
	    char[][] convertedGrid = new char[inputArray.length][inputArray.length];
	    for(int i = 0; i < inputArray.length; ++i)
	    {
	      for(int j = 0; j < inputArray.length; ++j)
	      {
	        convertedGrid[j][i] = inputArray[i][j];
	      }
	    }

	    return convertedGrid;
	  }
	
	  private char[][] shiftDiagionalGrid(char[][] inputArray, boolean left)
	  {
	    char[][] shiftedArray = new char[inputArray.length][inputArray.length];
	    for (int i = 0; i < inputArray.length; ++i)
	    {
	      int offset = left ? i : inputArray.length-i;
	      System.arraycopy(inputArray[i], offset, shiftedArray[i], 0, inputArray[i].length-offset);
	      System.arraycopy(inputArray[i], 0, shiftedArray[i], inputArray[i].length-offset, offset);
	    }

	    return shiftedArray;
	  }
	
	private void matchString(String str){
		Pattern regex = Pattern.compile("X{5}");
		Matcher matcher = regex.matcher(str);
		if(matcher.find()){
			gameOver = true;
			winner = "X";
		}
		regex = Pattern.compile("O{5}");
		matcher = regex.matcher(str);
		if(matcher.find()){
			gameOver = true;
			winner = "O";
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
