package edu.oregonstate.eecs.uct.domains.havannah;

import java.util.ArrayList;

import edu.oregonstate.eecs.uct.Simulator;

public class HavannahSimulator extends Simulator<HavannahState, HavannahAction> {
	private static final int NUMBER_OF_AGENTS = 2;
	private static final int[] CORNERS = new int[] {
		0,
		HavannahState.getSideLength() - 1,
		(3 * HavannahState.getSideLength() * HavannahState.getSideLength() - HavannahState.getSideLength()) / 2 - HavannahState.getSize(),
		(3 * HavannahState.getSideLength() * HavannahState.getSideLength() - HavannahState.getSideLength()) / 2 - 1,
		HavannahState.getNumberOfLocations() - HavannahState.getSideLength(),
		HavannahState.getNumberOfLocations() - 1
	};
	private static final int[][] SIDES = getSides();
	private static final HavannahState INITIAL_STATE = initialState();
	
	public HavannahSimulator() {
		setInitialState();
	}
	
	private HavannahSimulator(HavannahState state) {
		state_ = state;
		computeLegalActions();
	}
	
	@Override
	public Simulator<HavannahState, HavannahAction> copy() {
		return new HavannahSimulator(state_);
	}
	
	@Override
	public void setInitialState() {
		state_ = INITIAL_STATE;
		computeLegalActions();
	}
	
	private static HavannahState initialState() {
		return new HavannahState(new byte[2][HavannahState.getNumberOfBytes()], 0);
	}
	
	private static int[][] getSides() {
		int[][] sides = new int[6][HavannahState.getSideLength() - 2];
		for (int i = 0; i < HavannahState.getSideLength() - 2; i++) {
			sides[0][i] = i + 1;
			sides[1][i] = HavannahState.getSideLength() * (i + 1) + (i * (i + 1) / 2);
			sides[2][i] = CORNERS[2] + (i + 1) * HavannahState.getSize() - (i * (i + 1) / 2);
			sides[3][i] = HavannahState.getNumberOfLocations() - i - 2;
			sides[4][i] = CORNERS[3] + (i + 1) * (HavannahState.getSize() - 1) - (i * (i + 1) / 2);
			sides[5][i] = HavannahState.getSideLength() * (i + 2) + ((i + 1) * (i + 2) / 2) - 1;
		}
		return sides;
	}
	
	@Override
	public void takeAction(HavannahAction action) {
		if (!legalActions_.contains(action))
			throw new IllegalArgumentException("Action " + action + " not possible from current state.");
		byte[][] bitBoards = state_.getBitBoards();
		bitBoards[state_.getAgentTurn()][action.getLocation() / 8] |= 1 << (action.getLocation() % 8);
		state_ = new HavannahState(bitBoards, getNextAgentTurn(state_.getAgentTurn()));
		computeLegalActions();
	}

	@Override
	public void computeLegalActions() {
		legalActions_ = new ArrayList<HavannahAction>();
		if (getRewards()[0] == 0) {
			byte[][] bitBoards = state_.getBitBoards();
			for (int i = 0; i < bitBoards[0].length; i++)
				for (int j = 0; j < 8 && j + i * 8 < HavannahState.getNumberOfLocations(); j++)
					if ((bitBoards[0][i] & (1 << j)) == 0 && (bitBoards[1][i] & (1 << j)) == 0)
						legalActions_.add(HavannahAction.valueOf(8 * i + j));
		}
	}

	@Override
	public int[] getRewards() {
		byte[][] bitBoards = state_.getBitBoards();
		byte[] visited = new byte[bitBoards[0].length];
		byte[][] ringVisited = new byte[bitBoards.length][bitBoards[0].length];
		for (int i = 0; i < HavannahState.getNumberOfLocations(); i++) {
			if ((visited[i / 8] & (1 << i % 8)) == 0) { //check hasn't been visited
				if ((bitBoards[0][i / 8] & (1 << i % 8)) != 0 ||
						(bitBoards[1][i / 8] & (1 << i % 8)) != 0) { //check non empty location
					int result = dfsCornersSides(i, bitBoards, visited);
					//count corners
					int corners = 0;
					for (int k = 0; k < 6; k++) {
						if (result % 2 == 1)
							corners += 1;
						result >>= 1;
					}
					//count sides
					int sides = 0;
					for (int k = 0; k < 6; k++) {
						if (result % 2 == 1)
							sides += 1;
						result >>= 1;
					}
					
					if (corners >= 2 || sides >= 3) {
						if ((bitBoards[0][i / 8] & (1 << i % 8)) != 0)
							return new int[] {1,-1};
						else
							return new int[] {-1,1};
					}
				}
			}
			for (int j = 0; j < bitBoards.length; j++) {
				if ((ringVisited[j][i / 8] & (1 << i % 8)) == 0 &&
						(bitBoards[j][i / 8] & (1 << i % 8)) == 0) {
					if (dfsRings(i, bitBoards[j], ringVisited[j]) == 0) {
						if (j == 0)
							return new int[] {1,-1};
						else
							return new int[] {-1,1};
					}
				}
			}
		}
		return new int[NUMBER_OF_AGENTS];
	}
	
	private int dfsCornersSides(int location, byte[][] bitBoards, byte[] visited) {
		int value = getCornerMask(location) | getSideMask(location);
		visited[location / 8] |= (1 << location % 8);
		int x = getXY(location)[0];
		int y = getXY(location)[1];
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i + j != 0 && x + i >= 0 && y + j >= 0 && x + i < HavannahState.getSize() && y + j < HavannahState.getSize()) {
					if (y + j < HavannahState.getSideLength() && x + i < HavannahState.getSideLength() + y + j ||
							y + j >= HavannahState.getSideLength() && x + i > y + j - HavannahState.getSideLength()) {
						int nextLocation = getLocation(x + i, y + j);
						if (((bitBoards[0][location / 8] & (1 << location % 8)) != 0 && 
								(bitBoards[0][nextLocation / 8] & (1 << nextLocation % 8)) != 0) ||
								((bitBoards[1][location / 8] & (1 << location % 8)) != 0 &&
										(bitBoards[1][nextLocation / 8] & (1 << nextLocation % 8)) != 0)) {
							if ((visited[nextLocation / 8] & (1 << nextLocation % 8)) == 0)
								value |= dfsCornersSides(nextLocation, bitBoards, visited);
						}
					}
				}
			}
		}
		return value;
	}
	
	private int dfsRings(int location, byte[] bitBoard, byte[] visited) {
		int value = getCornerMask(location) | getSideMask(location);
		visited[location / 8] |= (1 << location % 8);
		int[] temp = getXY(location);
		int x = temp[0];
		int y = temp[1];
		
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i + j != 0 && x + i >= 0 && y + j >= 0 && x + i < HavannahState.getSize() && y + j < HavannahState.getSize()) {
					if (y + j <= HavannahState.getSideLength() && x + i < HavannahState.getSideLength() + y + j ||
							y + j >= HavannahState.getSideLength() && x + i >= y + j - HavannahState.getSideLength()) {
						int nextLocation = getLocation(x + i, y + j);
						if ((bitBoard[nextLocation / 8] & (1 << nextLocation % 8)) == 0 &&
								(visited[nextLocation / 8] & (1 << nextLocation % 8)) == 0)
							value |= dfsRings(nextLocation, bitBoard, visited);
					}
				}
			}
		}
		return value;
	}
	
	private int[] getXY(int location) {
		int sideLength = HavannahState.getSideLength();
		int size = HavannahState.getSize();
		int temp = location;
		int y = 0;
		int x = 0;
		int shift = 0;
		while (temp >= 0) {
			x = temp;
			if (y < sideLength)
				temp -= sideLength + y;
			else {
				temp -= size + sideLength - y - 1;
				shift += 1;
			}
			y += 1;
		}
		y -= 1;
		x += shift;
		return new int[] {x, y};
	}
	
	private int getLocation(int x, int y) {
		int sideLength = HavannahState.getSideLength();
		int size = HavannahState.getSize();
		int location = 0;
		for (int i = 0; i < y && i < sideLength; i++)
			location += i + sideLength;
		if (y >= 5)
			location -= 1;
		for (int i = 0; i < y - sideLength; i++)
			location += size - i - 2;
		return location + x;
	}
	
	private int getCornerMask(int location) {
		for (int i = 0; i < CORNERS.length; i++)
			if (CORNERS[i] == location)
				return 1 << i;
		return 0;
	}
	
	private int getSideMask(int location) {
		for (int i = 0; i < SIDES.length; i++)
			for (int j = 0; j < SIDES[i].length; j++)
				if (SIDES[i][j] == location)
					return 1 << (i + 6);
		return 0;
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}

	@Override
	public double[] getFeatureVector(HavannahAction action) {
		// TODO Auto-generated method stub
		return null;
	}
}
