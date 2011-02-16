package edu.oregonstate.eecs.uct.domains.ewn;

import edu.oregonstate.eecs.uct.State;

public class EwnState extends State {
	private static final int SIZE = 5;
	
	/**
	 *  0: empty
	 *  x: value of agent 1 location
	 * -x: value of agent 2 location
	 */
	private int[][] locations_;
	private int dieRoll_;
	
	public EwnState(int[][] locations, int dieRoll, int agentTurn) {
		locations_ = locations;
		dieRoll_ = dieRoll;
		agentTurn_ = agentTurn;
	}
	
	public int getLocation(int x, int y) {
		return locations_[x][y];
	}
	
	public int[][] getLocations() {
		int[][] locations = new int[SIZE][SIZE];
		for (int i = 0; i < locations_.length; i++)
			for (int j = 0; j < locations_[0].length; j++)
				locations[i][j] = locations_[i][j];
		return locations;
	}
	
	public int getDieRoll() {
		return dieRoll_;
	}
	
	public static int getSize() {
		return SIZE;
	}

	@Override
	public int hashCode() {
		int code = 7 + dieRoll_;
		for (int[] locations: locations_)
			for (int location: locations)
				code = 11 * code + location;
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EwnState))
			return false;
		EwnState state = (EwnState) object;
		for (int i = 0; i < locations_.length; i++)
			for (int j = 0; j < locations_[0].length; j++)
				if (locations_[i][j] == state.getLocation(i, j))
					return false;
		return dieRoll_ == state.getDieRoll();
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("(" + dieRoll_ + ")\n");
		for (int i = 0; i < 3 * SIZE + 1; i++)
			output.append("-");
		output.append("\n");
		for (int i = SIZE - 1; i >= 0; i--) {
			output.append(":");
			for (int j = 0; j < SIZE; j++) {
				if (j != 0)
					output.append("|");
				int value = locations_[j][i];
				if (value > 0)
					output.append("X" + value);
				else if (value < 0)
					output.append("O" + -1 * value);
				else
					output.append("  ");
			}
			output.append(":\n");
		}
		for (int i = 0; i < 3 * SIZE + 1; i++)
			output.append("-");
		return output.toString();
	}
}
