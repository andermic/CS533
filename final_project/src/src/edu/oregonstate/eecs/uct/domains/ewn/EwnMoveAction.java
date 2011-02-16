package edu.oregonstate.eecs.uct.domains.ewn;

public class EwnMoveAction extends EwnAction {
	public enum Direction {
		NORTH, EAST, SE, SOUTH, WEST, NW
	}
	
	private int xLocation_;
	private int yLocation_;
	private Direction direction_;
	
	public EwnMoveAction(int xLocation, int yLocation, Direction direction) {
		xLocation_ = xLocation;
		yLocation_ = yLocation;
		direction_ = direction;
	}
	
	public int getXLocation() {
		return xLocation_;
	}
	
	public int getYLocation() {
		return yLocation_;
	}
	
	public Direction getDirection() {
		return direction_;
	}
	
	@Override
    public int hashCode() {
		int code = 7 + xLocation_;
		code = 11 * code + yLocation_;
		code = 11 * code + direction_.ordinal();
    	return code;
    }
    
    @Override
    public boolean equals(Object object) {
    	if (!(object instanceof EwnMoveAction))
    		return false;
    	EwnMoveAction action = (EwnMoveAction) object;
    	return direction_ == action.getDirection() && 
    		   xLocation_ == action.getXLocation() &&
    		   yLocation_ == action.getYLocation();
    }
    
    @Override
    public String toString() {
    	return "(" + xLocation_ + "," + yLocation_ + ") " + direction_.toString();
    }
}
