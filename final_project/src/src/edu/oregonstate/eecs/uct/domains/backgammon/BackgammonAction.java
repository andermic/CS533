package edu.oregonstate.eecs.uct.domains.backgammon;

import edu.oregonstate.eecs.uct.Action;


public class BackgammonAction implements Action {
	/**
	 * List of individual moves with 
	 * moves_[index][0] being location and
	 * moves_[index][1] being distance
	 */
	private byte[][] moves_;
	
	public BackgammonAction(byte[][] moves) {
		moves_ = moves;
	}
	
	public int getMoveLocation(int index) {
		return moves_[index][0];
	}
	
	public int getMoveDistance(int index) {
		return moves_[index][1];
	}
	
	public int getNumMoves() {
		return moves_.length;
	}
	
	public boolean isIsomorphic(BackgammonAction action) {
		if (moves_.length != action.getNumMoves())
			return false;
		boolean[] found = new boolean[moves_.length];
		for (int i = 0; i < action.getNumMoves(); i++) {
			for (int j = 0; j < moves_.length; j++) {
				if (found[j] == false &&
						action.getMoveLocation(i) == moves_[j][0] &&
						action.getMoveDistance(i) == moves_[j][1]) {
					found[j] = true;
					break;
				}
			}
		}
		for (int i = 0; i < found.length; i++)
			if (found[i] == false)
				return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int code = 7;
		for (byte[] moves: moves_)
			for (byte move: moves)
				code = 11 * code + move;
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof BackgammonAction))
			return false;
		BackgammonAction action = (BackgammonAction) object;
		for (int i = 0; i < moves_.length; i++)
			for (int j = 0; j < moves_[i].length; j++)
				if (moves_[i][0] != action.getMoveLocation(i) ||
						moves_[i][1] != action.getMoveDistance(i))
					return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("[ ");
		for (int i = 0; i < moves_.length; i++)
			output.append(moves_[i][0] + "/" + moves_[i][1] + " ");
		output.append("]");
		return output.toString();
	}
}
