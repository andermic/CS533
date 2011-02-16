package edu.oregonstate.eecs.uct.domains.havannah;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.uct.Action;

public class HavannahAction implements Action {
	private static List<HavannahAction> havannahActions_ = new ArrayList<HavannahAction>();
	private int location_;
	
	private HavannahAction(int location) {
		location_ = location;
	}
	
	public static HavannahAction valueOf(int location) {
		if (location >= havannahActions_.size())
			for (int i = havannahActions_.size(); i <= location; i++)
				havannahActions_.add(new HavannahAction(i));
		return havannahActions_.get(location);
	}
	
	public static HavannahAction valueOf(int x, int y) {
		int sideLength = HavannahState.getSideLength();
		int size = HavannahState.getSize();
		int location = x;
		for (int i = 0; i < y || i < sideLength; i++)
			location += i + sideLength;
		for (int i = 1; i < y; i++)
			location += size - i;
		return valueOf(location);
	}
	
	public int getLocation() {
		return location_;
	}
	
	public int getX() {
		int sideLength = HavannahState.getSideLength();
		int size = HavannahState.getSize();
		int x = location_;
		int length = 0;
		int shift = 0;
		for (int i = 0; i < size && x - length >= 0; i++) {
			x -= length;
			if (i < sideLength)
				length = i + sideLength;
			else {
				length = size + sideLength - i - 1;
				shift += 1;
			}
		}
		return x + shift;
	}
	
	public int getY() {
		int sideLength = HavannahState.getSideLength();
		int size = HavannahState.getSize();
		int location = location_;
		int y = 0;
		while (location >= 0) {
			if (y < sideLength)
				location -= sideLength + y;
			else
				location -= size + sideLength - y - 1;
			y += 1;
		}
		return y - 1;
	}
	
	@Override
	public int hashCode() {
		return location_;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof HavannahAction))
			return false;
		HavannahAction action = (HavannahAction) object;
		return location_ == action.getLocation();
	}
	
	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ")";
	}
}
