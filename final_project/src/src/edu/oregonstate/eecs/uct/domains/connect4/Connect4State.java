 package edu.oregonstate.eecs.uct.domains.connect4;

import edu.oregonstate.eecs.uct.State;

/**
 * A connect four board state.
 */
public final class Connect4State extends State {
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	public enum Location {
		EMPTY, WHITE, BLACK
	}

	private int[][] locations_;
	
	public Connect4State(int[][] locations, int agentTurn) {
		locations_ = locations;
		agentTurn_ = agentTurn;
	}
	
	public int getLocation(int x, int y) {
		return locations_[x][y];
	}
	
	public int[][] getLocations() {
		int[][] locations = new int[WIDTH][HEIGHT];
		for (int i = 0; i < locations_.length; i++)
			for (int j = 0; j < locations_[0].length; j++)
				locations[i][j] = locations_[i][j];
		return locations;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	@Override
	public int hashCode() {
		int code = 7;
		for (int[] locations: locations_)
			for (int location: locations)
				code = 11 * code + location;
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Connect4State))
			return false;
		Connect4State connect4State = (Connect4State) object;
		for (int i = 0; i < locations_.length; i++)
			for (int j = 0; j < locations_[0].length; j++)
				if (locations_[i][j] != connect4State.getLocation(i, j))
					return false;
		return true;
	}

	@Override
	public String toString() {
		final String PIECES = " XO";
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < 2 * WIDTH + 1; i++)
			output.append("-");
		output.append("\n");
		for (int i = HEIGHT - 1; i >= 0; i--) {
			output.append(":");
			for (int j = 0; j < WIDTH; j++) {
				if (j != 0)
					output.append("|");
				output.append(PIECES.charAt(locations_[j][i]));
			}
			output.append(":\n");
		}
		for (int i = 0; i < 2 * WIDTH + 1; i++)
			output.append("-");
		return output.toString();
	}
}
