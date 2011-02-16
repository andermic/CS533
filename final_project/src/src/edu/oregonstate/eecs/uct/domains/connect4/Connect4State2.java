package edu.oregonstate.eecs.uct.domains.connect4;

import edu.oregonstate.eecs.uct.State;

/**
 * State represented by a bitBoard described below:
 * .  .  .  .  .  .  . Row above top row
 * 5 12 19 26 33 40 47
 * 4 11 18 25 32 39 46
 * 3 10 17 24 31 38 45
 * 2  9 16 23 30 37 44
 * 1  8 15 22 29 36 43
 * 0  7 14 21 28 35 42
 */
public class Connect4State2 extends State {
	private static final int WIDTH = 7;
	private static final int HEIGHT = 6;
	
	private long[] bitBoards_;
	
	public Connect4State2(long[] bitBoards, int agentTurn) {
		bitBoards_ = bitBoards;
		agentTurn_ = agentTurn;
	}
	
	public long[] getBitBoards() {
		long[] bitBoards = new long[bitBoards_.length];
		for (int i = 0; i < bitBoards_.length; i++)
			bitBoards[i] = bitBoards_[i];
		return bitBoards;
	}
	
	public static int getWidth() {
		return WIDTH;
	}
	
	public static int getHeight() {
		return HEIGHT;
	}
	
	@Override
	public int hashCode() {
		return (int) (11 * bitBoards_[0] + bitBoards_[1]);
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof Connect4State2))
			return false;
		Connect4State2 state = (Connect4State2) object;
		long[] bitBoards = state.getBitBoards();
		return bitBoards[0] == bitBoards_[0] && bitBoards[1] == bitBoards_[1];
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < 2 * WIDTH + 3; i++)
			output.append("-");
		output.append("\n");
		for (int i = HEIGHT - 1; i >= 0; i--) {
			output.append(": ");
			for (int j = i; j < ((HEIGHT + 1) * WIDTH); j += (HEIGHT + 1)) {
				long mask = 1L << j;
				if ((bitBoards_[0] & mask) != 0)
					output.append("X");
				else if ((bitBoards_[1] & mask) != 0)
					output.append("O");
				else
					output.append("-");
				output.append(" ");
			}
			output.append(":\n");
		}
		for (int i = 0; i < 2 * WIDTH + 3; i++)
			output.append("-");
		return output.toString();
	}
}