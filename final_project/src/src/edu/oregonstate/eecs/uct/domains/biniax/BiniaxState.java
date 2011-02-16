package edu.oregonstate.eecs.uct.domains.biniax;

import edu.oregonstate.eecs.uct.State;

/**
 * Defines a Biniax state.
 * Location Value - Representation:
 *  0 - empty
 *  x - single element, 0 < x < 10
 * xy - element pair, 0 < x < 10 and 0 < y < 10 and x < y 
 */
public final class BiniaxState extends State {
	private static final int WIDTH = 5;
	private static final int HEIGHT = 7;
	private static final int MAX_ELEMENTS = 9;
	
	private int[][] locations_;
	private int freeMoves_;
	
	public BiniaxState(int[][] locations, int freeMoves) {
		locations_ = locations;
		freeMoves_ = freeMoves;
	}
	
	public int[][] getLocations() {
		int[][] locations = new int[locations_.length][locations_[0].length];
		for (int i = 0; i < WIDTH; i++) {
			for (int j = 0; j < HEIGHT; j++) {
				locations[i][j] = locations_[i][j];
			}
		}
		return locations;
	}
	
	public int getLocation(int x, int y) {
		return locations_[x][y];
	}
	
	public int getFreeMoves() {
		return freeMoves_;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	public static int getMaxElements() {
		return MAX_ELEMENTS;
	}
	
	@Override
	public int hashCode() {
		int code = 7 + freeMoves_;
		for (int[] locations: locations_)
			for (int location: locations)
				code = 11 * code + location;
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BiniaxState))
			return false;
		BiniaxState biniaxState = (BiniaxState) object;
		if (freeMoves_ != biniaxState.getFreeMoves())
			return false;
		else {
			for (int i = 0; i < WIDTH; i++)
				for (int j = 0; j < HEIGHT; j++)
					if (locations_[i][j] != biniaxState.getLocation(i, j))
						return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		final String ELEMENTS = " ABCDEFGHIJKLMNOPQRS";
		StringBuilder output = new StringBuilder();
		output.append("(Free Moves:" + freeMoves_ + ")\n");
		for (int i = 0; i < WIDTH; i++)
			output.append("----");
		output.append("-\n");
		for (int j = 0; j < HEIGHT; j++) {
			output.append(":");
			for (int i = 0; i < WIDTH; i++) {
				if (i != 0)
					output.append(" ");
				if (locations_[i][j] == 0)
					output.append("   ");
				else if (locations_[i][j] == -1)
					output.append("<X>");
				else if (locations_[i][j] > 0 && locations_[i][j] < 10)
					output.append("[" + ELEMENTS.charAt(locations_[i][j]) + "]");
				else
					output.append(ELEMENTS.charAt(locations_[i][j] / 10) + "-" + ELEMENTS.charAt(locations_[i][j] % 10));
			}
			output.append(":\n");
		}
		output.append("-");
		for (int i = 0; i < WIDTH; i++)
			output.append("----");
		return output.toString();
	}
}
