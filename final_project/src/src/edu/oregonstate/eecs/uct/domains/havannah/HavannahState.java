package edu.oregonstate.eecs.uct.domains.havannah;

import edu.oregonstate.eecs.uct.State;

/**
 * Below is the layout of a size 9 Havannah 
 * board with location values.
 *     56 57 58 59 60
 *    50 51 52 53 54 55
 *   43 44 45 46 47 48 49
 *  35 36 37 38 39 40 41 42
 * 26 27 28 29 30 31 32 33 34
 *  18 19 20 21 22 23 24 25
 *   11 12 13 14 15 16 17
 *     5  6  7  8  9 10
 *      0  1  2  3  4
 */
public final class HavannahState extends State {
	private static final int SIDE_LENGTH = 5;
	private static final int SIZE = 2 * SIDE_LENGTH - 1;
	private static final int NUMBER_OF_LOCATIONS = 3 * SIDE_LENGTH * SIDE_LENGTH - 3 * SIDE_LENGTH + 1;
	private static final int NUMBER_OF_BYTES = NUMBER_OF_LOCATIONS / 8 + 1;
	
	private byte[][] bitBoards_;
	
	public HavannahState(byte[][] bitBoards, int agentTurn) {
		bitBoards_ = bitBoards;
		agentTurn_ = agentTurn;
	}
	
	public byte[][] getBitBoards() {
		byte[][] bitBoards = new byte[bitBoards_.length][bitBoards_[0].length];
		for (int i = 0; i < bitBoards_.length; i++)
			for (int j = 0; j < bitBoards_[0].length; j++)
				bitBoards[i][j] = bitBoards_[i][j];
		return bitBoards;
	}
	
	public static int getSideLength() {
		return SIDE_LENGTH;
	}
	
	public static int getSize() {
		return SIZE;
	}
	
	public static int getNumberOfLocations() {
		return NUMBER_OF_LOCATIONS;
	}
	
	public static int getNumberOfBytes() {
		return NUMBER_OF_BYTES;
	}
	
	@Override
	public int hashCode() {
		int code = 7;
		for (int i = 0; i < bitBoards_.length; i++)
			for (int j = 0; j < bitBoards_[0].length; j++)
				code = 11 * code + bitBoards_[i][j];
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof HavannahState))
			return false;
		HavannahState state = (HavannahState) object;
		byte[][] bitBoards = state.getBitBoards();
		for (int i = 0; i < bitBoards_.length; i++)
			for (int j = 0; j < bitBoards_[0].length; j++)
				if (bitBoards[i][j] != bitBoards_[i][j])
					return false;
		return true;
	}

	@Override
	public String toString() {
		String output = "";
		int current = 0;
		int length = SIDE_LENGTH;
		for (int i = 0; i < SIZE; i++) {
			StringBuilder line = new StringBuilder();
			for (int j = 0; j < SIDE_LENGTH - i - 1 || j < i - SIDE_LENGTH + 1; j++)
				line.append(" ");
			for (int j = 0; j < length; j++) {
				if ((bitBoards_[0][current / 8] & (1 << current % 8)) != 0)
					line.append("X ");
				else if ((bitBoards_[1][current / 8] & (1 << current % 8)) != 0)
					line.append("O ");
				else
					line.append("- ");
				current += 1;
			}
			if (i < SIDE_LENGTH - 1)
				length += 1;
			else
				length -= 1;
			line.append("\n");
			output = line + output;
		}
		return output;
	}
}
