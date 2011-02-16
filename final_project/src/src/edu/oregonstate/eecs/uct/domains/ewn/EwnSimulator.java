package edu.oregonstate.eecs.uct.domains.ewn;

import java.util.ArrayList;

import edu.oregonstate.eecs.uct.Simulator;

public class EwnSimulator extends Simulator<EwnState, EwnAction> {
	private static final int NUMBER_OF_AGENTS = 2;
	private static final int DIE_SIDES = 6;
	
	public EwnSimulator() {
		setInitialState();
	}
	
	public EwnSimulator(EwnState state) {
		state_ = state;
		computeLegalActions();
	}
	
	@Override
	public Simulator<EwnState, EwnAction> copy() {
		return new EwnSimulator(state_);
	}

	@Override
	public void setInitialState() {
		//int[][] locations = new int[EwnState.getSize()][EwnState.getSize()];
		//state_ = new EwnState(locations, 0, 0);
		int[][] locations = new int[][] {
				{ 0, 0, 3, 2, 1},
				{ 0, 0, 0, 5, 4},
				{-6, 0, 0, 0, 6},
				{-4,-5, 0, 0, 0},
				{-1,-2,-3, 0, 0}
		};
		state_ = new EwnState(locations, (int) (Math.random() * DIE_SIDES) + 1, 0);
		computeLegalActions();
	}

	@Override
	public void takeAction(EwnAction action) {
		if (!legalActions_.contains(action))
			throw new IllegalArgumentException("Action " + action + " not possible from current state.");
		
		int[][] locations = state_.getLocations();
		int dieRoll = 0;
		
		if (isSetupPhase()) {
			if (!(action instanceof EwnSetupAction))
				throw new IllegalArgumentException("Action should be a setup action.");
			EwnSetupAction setupAction = (EwnSetupAction) action;
			if (state_.getAgentTurn() == 0) {
				locations[0][EwnState.getSize() - 1] = setupAction.getValue(0);
				locations[1][EwnState.getSize() - 1] = setupAction.getValue(1);
				locations[2][EwnState.getSize() - 1] = setupAction.getValue(2);
				locations[0][EwnState.getSize() - 2] = setupAction.getValue(3);
				locations[1][EwnState.getSize() - 2] = setupAction.getValue(4);
				locations[0][EwnState.getSize() - 3] = setupAction.getValue(5);
			} else {
				locations[EwnState.getSize() - 1][0] = -setupAction.getValue(0);
				locations[EwnState.getSize() - 2][0] = -setupAction.getValue(1);
				locations[EwnState.getSize() - 3][0] = -setupAction.getValue(2);
				locations[EwnState.getSize() - 1][1] = -setupAction.getValue(3);
				locations[EwnState.getSize() - 2][1] = -setupAction.getValue(4);
				locations[EwnState.getSize() - 1][2] = -setupAction.getValue(5);
				dieRoll = (int) (Math.random() * DIE_SIDES) + 1;
			}
		} else {
			if (!(action instanceof EwnMoveAction))
				throw new IllegalArgumentException("Action should be a move action.");
			EwnMoveAction moveAction = (EwnMoveAction) action;
			switch(moveAction.getDirection()) {
			case NORTH:
				locations[moveAction.getXLocation()][moveAction.getYLocation() + 1] = 
					locations[moveAction.getXLocation()][moveAction.getYLocation()];
				break;
			case EAST:
				locations[moveAction.getXLocation() + 1][moveAction.getYLocation()] = 
					locations[moveAction.getXLocation()][moveAction.getYLocation()];
				break;
			case SE:
				locations[moveAction.getXLocation() + 1][moveAction.getYLocation() - 1] = 
					locations[moveAction.getXLocation()][moveAction.getYLocation()];
				break;
			case SOUTH:
				locations[moveAction.getXLocation()][moveAction.getYLocation() - 1] = 
					locations[moveAction.getXLocation()][moveAction.getYLocation()];
				break;
			case WEST:
				locations[moveAction.getXLocation() - 1][moveAction.getYLocation()] = 
					locations[moveAction.getXLocation()][moveAction.getYLocation()];
				break;
			case NW:
				locations[moveAction.getXLocation() - 1][moveAction.getYLocation() + 1] = 
					locations[moveAction.getXLocation()][moveAction.getYLocation()];
				break;
			}
			locations[moveAction.getXLocation()][moveAction.getYLocation()] = 0;
			dieRoll = (int) (Math.random() * DIE_SIDES) + 1;
		}
		state_ = new EwnState(locations, dieRoll, getNextAgentTurn(state_.getAgentTurn()));
		computeLegalActions();
	}
	
	public boolean isSetupPhase() {
		return state_.getDieRoll() == 0;
	}
	
	@Override
	protected void computeLegalActions() {
		legalActions_ = new ArrayList<EwnAction>();
		if (getRewards()[0] == 0) {
			if (isSetupPhase()) {
				for (int i = 1; i <= 6; i++) {
					for (int j = 1; j <= 6; j++) {
						for (int k = 1; k <= 6; k++) {
							for (int l = 1; l <= 6; l++) {
								for (int m = 1; m <= 6; m++) {
									int n = 1;
									if (i == j)
										j++;
									while (i == k || j == k)
										k++;
									while (i == l || j == l || k == l)
										l++;
									while (i == m || j == m || k == m || l == m)
										m++;
									while (i == n || j == n || k == n || l == n || m == n)
										n++;
									if (j > 6 || k > 6 || l > 6 || m > 6 || n > 6)
										break;
									legalActions_.add(new EwnSetupAction(new int[] {i, j, k, l, m, n}));
								}
							}
						}
					}
				}
			} else {
				int roll = state_.getDieRoll();
				int current;
				if (state_.getAgentTurn() == 0)
					current = 1;
				else
					current = -1;
				
				//Find location(s) of moveable pieces
				int low = 0;
				int high = 7;
				int x1 = -1, x2 = -1, y1 = -1, y2 = -1;
				for (int i = 0; i < EwnState.getSize() && high != low; i++) {
					for (int j = 0; j < EwnState.getSize() && high != low; j++) {
						if (state_.getLocation(i, j) * current > 0) {
							int value = Math.abs(state_.getLocation(i, j));
							if (value == roll) {
								low = high = value;
								x1 = x2 = i;
								y1 = y2 = j;
							} else if (value < roll && low < value) {
								low = value;
								x1 = i;
								y1 = j;
							} else if (value > roll && high > value) {
								high = value;
								x2 = i;
								y2 = j;
							}
						}
					}
				}
				//Generate possible actions
				if (low != 0) {
					if (state_.getAgentTurn() == 0) {
						if (x1 != EwnState.getSize() - 1 && y1 != 0) {
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.SE));
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.EAST));
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.SOUTH));
						} else if (x1 != EwnState.getSize() - 1)
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.EAST));
						else if (y1 != 0)
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.SOUTH));
					} else {
						if (x1 != 0 && y1 != EwnState.getSize() - 1) {
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.NW));
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.WEST));
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.NORTH));
						} else if (x1 != 0)
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.WEST));
						else if (y1 != EwnState.getSize() - 1)
							legalActions_.add(new EwnMoveAction(x1, y1, EwnMoveAction.Direction.NORTH));
					}
				}
	
				if (high != 7 && low != high) {
					if (state_.getAgentTurn() == 0) {
						if (x2 != EwnState.getSize() - 1 && y2 != 0) {
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.SE));
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.EAST));
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.SOUTH));
						} else if (x2 != EwnState.getSize() - 1)
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.EAST));
						else if (y2 != 0)
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.SOUTH));
					} else {
						if (x2 != 0 && y2 != EwnState.getSize() - 1) {
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.NW));
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.WEST));
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.NORTH));
						} else if (x2 != 0)
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.WEST));
						else if (y2 != EwnState.getSize() - 1)
							legalActions_.add(new EwnMoveAction(x2, y2, EwnMoveAction.Direction.NORTH));
					}
				}
			}
		}
	}
	
	@Override
	public int[] getRewards() {
		if (state_.getLocation(EwnState.getSize() - 1, 0) > 0)
			return new int[] {1, -1};
		if (state_.getLocation(0, EwnState.getSize() - 1) < 0)
			return new int[] {-1, 1};
		boolean redFound = false;
		boolean blueFound = false;
		for (int i = 0; i < EwnState.getSize(); i++) {
			for (int j = 0; j < EwnState.getSize(); j++) {
				if (state_.getLocation(i, j) > 0)
					redFound = true;
				if (state_.getLocation(i, j) < 0)
					blueFound = true;
			}
		}
		if (!redFound && blueFound)
			return new int[] {-1, 1};
		if (redFound && !blueFound)
			return new int[] {1, -1};
		return new int[] {0, 0};
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}

	@Override
	public double[] getFeatureVector(EwnAction action) {
		// TODO Auto-generated method stub
		return null;
	}

}
