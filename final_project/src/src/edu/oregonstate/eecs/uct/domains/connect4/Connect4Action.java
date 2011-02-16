package edu.oregonstate.eecs.uct.domains.connect4;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.uct.Action;

/**
 * Represents a Connect 4 action.
 */
public final class Connect4Action implements Action {
	/** Holds list of all possible actions. */
	private static List<Connect4Action> connect4Actions_ = new ArrayList<Connect4Action>();
	/** Slot location to place piece. */
	private int location_;
	
	/**
	 * Create a Connect 4 action by specifying a move location.
	 * @param location value assumes range from 0 to Connect4State.WIDTH - 1.
	 */
	private Connect4Action(int location) {
		location_ = location;
	}
	
	/**
	 * Returns the Connect 4 action representation
	 * of the int location.
	 * @param location slot location to place piece.
	 * @return a Connect 4 action.
	 */
	public static Connect4Action valueOf(int location) {
		if (connect4Actions_.size() <= location)
			for (int i = connect4Actions_.size(); i <= location; i++)
				connect4Actions_.add(new Connect4Action(i));
		return connect4Actions_.get(location);
	}

    public int getLocation() {
        return location_;
    }
    
    @Override
    public int hashCode() {
    	return location_;
    }
    
    @Override
    public boolean equals(Object object) {
    	if (!(object instanceof Connect4Action))
    		return false;
    	Connect4Action connect4Action = (Connect4Action) object;
    	return location_ == connect4Action.getLocation();
    }
    
    @Override
    public String toString() {
    	return String.valueOf(location_ + 1);
    }
}
