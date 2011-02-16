package edu.oregonstate.eecs.uct.domains.connect4;

import edu.oregonstate.eecs.uct.Simulator;
import edu.oregonstate.eecs.uct.domains.connect4.Connect4State.Location;

import java.util.ArrayList;

/**
 * The older slower Connect 4 simulator.
 * Didn't get rid of it because it aleady has getFeatureVector 
 * implemented and the new one does not.
 */
public final class Connect4Simulator extends Simulator<Connect4State, Connect4Action> {
	private static final int NUMBER_OF_AGENTS = 2;
	private static final Connect4State INITIAL_STATE = 
		new Connect4State(new int[Connect4State.getWidth()][Connect4State.getHeight()], 0);
	
	public Connect4Simulator() {
		setInitialState();
	}
	
	public Connect4Simulator(Connect4State state) {
		state_ = state;
		computeLegalActions();
	}
	
	@Override
	public Simulator<Connect4State, Connect4Action> copy() {
		return new Connect4Simulator(state_);
	}
	
	@Override
	public void setInitialState() {
		state_ = INITIAL_STATE;
		computeLegalActions();
	}
	
	@Override
	public void takeAction(Connect4Action action) {
		if (!legalActions_.contains(action))
			throw new IllegalArgumentException("Action " + action + " not possible from current state.");
		
		int x = action.getLocation();
		int y = getTopEmptyLocation(x);
    	Connect4State.Location location;
        if (state_.getAgentTurn() == 0)
        	location = Connect4State.Location.WHITE;
        else
        	location = Connect4State.Location.BLACK;
    	int[][] locations = state_.getLocations();
    	locations[x][y] = location.ordinal();
		state_ = new Connect4State(locations, getNextAgentTurn(state_.getAgentTurn()));
		computeLegalActions();
	}
	
	@Override
	protected void computeLegalActions() {
		legalActions_ = new ArrayList<Connect4Action>();
		int[] reward = getRewards();
		if (reward[0] == 0 && reward[1] == 0) {
			for (int i = 0; i < Connect4State.getWidth(); i++) {
				if (state_.getLocation(i, Connect4State.getHeight() - 1) == Location.EMPTY.ordinal())
					legalActions_.add(Connect4Action.valueOf(i));
			}
		}
	}
	
	private int getTopEmptyLocation(int column) {
    	for (int j = 0; j < Connect4State.getHeight(); j++)
    		if (state_.getLocation(column, j) == Location.EMPTY.ordinal())
    			return j;
    	return Connect4State.getHeight();
	}
	
	/**
     * Gets the state reward based on if any top piece is part
     * of a 4 in a row.  This assumes that the state is legal
     * if the state is not legal then there may be a 4 in a 
     * row that does not contain a top piece.
     * @returns {agent0 reward, agent1 reward}
     * -1 - loss
     *  0 - draw
     *  1 - win
     */
	@Override
	public int[] getRewards() {
		int[] reward = new int[getNumberOfAgents()];
        for (int i = 0; i < Connect4State.getWidth(); i++) {
        	//Get next empty location
        	int emptyLocation = Connect4State.getHeight();
        	for (int j = 0; j < Connect4State.getHeight(); j++) {
        		if (state_.getLocation(i, j) == Location.EMPTY.ordinal()) {
        			emptyLocation = j;
        			break;
        		}
        	}
        	//If emptyLocation == 0 then no pieces in that column to check
        	if (emptyLocation > 0) {
	        	int x = i;
	        	int y = emptyLocation - 1;
	        	int location = state_.getLocation(x, y);
	        	int count = 1;
		        //Vertical
	        	if (y > 2) {
	        		for (int j = 1; j <= y; j++) {
	        			if (location != state_.getLocation(x, y - j))
	        				break;
	        			count++;
	        		}
	        	}
		        //Horizontal
	        	if (count < 4) {
			        count = 1;
			        for (int j = 1; x - j >= 0; j++) {
			        	if (location != state_.getLocation(x - j, y))
			        		break;
			        	count++;
			        }
			        for (int j = 1; x + j < Connect4State.getWidth(); j++) {
			        	if (location != state_.getLocation(x + j, y))
			        		break;
			        	count++;
			        }
	        	}
		        //Diagonal
	        	if (count < 4) {
			        count = 1;
			        boolean diag1 = true;
			        boolean diag2 = true;
			        for (int j = 1; diag1 && diag2; j++) {
			        	if (x - j >= 0 && y - j >= 0 && location == state_.getLocation(x - j, y - j))
			        		count++;
			        	else
			        		diag1 = false;
			        	if (x + j < Connect4State.getWidth() && y + j < Connect4State.getHeight() && location == state_.getLocation(x + j, y + j))
			        		count++;
			        	else
			        		diag2 = false;
			        }
	        	}
	        	if (count < 4) {
			        count = 1;
			        boolean diag1 = true;
			        boolean diag2 = true;
			        for (int j = 1; diag1 && diag2; j++) {
			        	if (x - j >= 0 && y + j < Connect4State.getHeight() && location == state_.getLocation(x - j, y + j))
			        		count++;
			        	else
			        		diag1 = false;
			        	if (x + j < Connect4State.getWidth() && y - j >= 0 && location == state_.getLocation(x + j, y - j))
			        		count++;
			        	else
			        		diag2 = false;
			        }
	        	}
	        	//Set reward
	        	if (count > 3) {
	            	if (state_.getAgentTurn() == 0) {
	            		reward[0] = -1;
	            		reward[1] = 1;
	            	} else {
	            		reward[0] = 1;
	            		reward[1] = -1;
	            	}
	            }
        	}
        }
        return reward;
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}

	/**
	 * Computes the following features and returns
	 * them as a vector.
	 * returns int[]
	 *   0 - number of 2 in a row after move
	 *   1 - number of 3 in a row after move
	 *   2 - number of 4 in a row after move (0 or 1)
	 *   3 - number of possible 2 in a rows for opponent
	 *   4 - number of possible 3 in a rows for opponent
	 *   5 - number of possible 4 in a rows for opponent (0 or 1)
	 *   6 - winning lines for player
	 *   7 - winning lines for opponent
	 */
	@Override
	public double[] getFeatureVector(Connect4Action action) {
		Connect4Simulator simulator = new Connect4Simulator();
		simulator.setState(state_);
		simulator.takeAction(action);
		return simulator.getFeatureVector();
	}
	
	private double[] getFeatureVector() {
		double[] featureVector = new double[8];
		Location player = null;
		Location opponent = null;
		if (state_.getAgentTurn() == 0) {
			player = Location.BLACK;
			opponent = Location.WHITE;
		} else {
			player = Location.WHITE;
			opponent = Location.BLACK;
		}
		//Column
		for (int i = 0; i < Connect4State.getWidth(); i++) {
			int playerCount = 0;
			int opponentCount = 0;
			boolean end = false;
			for (int j = 0; j < Connect4State.getHeight() && !end; j++) {
				if (state_.getLocation(i, j) == player.ordinal()) {
					if (opponentCount == 2)
						featureVector[3]++;
					else if (opponentCount == 3)
						featureVector[4]++;
					else if (opponentCount == 4)
						featureVector[5]++;
					playerCount++;
					opponentCount = 0;
				} else if (state_.getLocation(i, j) == opponent.ordinal()) {
					if (playerCount == 2)
						featureVector[0]++;
					else if (playerCount == 3)
						featureVector[1]++;
					else if (playerCount == 4)
						featureVector[2]++;
					opponentCount++;
					playerCount = 0;
				} else { //Location is empty
					if (opponentCount > 0) {
						if (opponentCount == 1)
							featureVector[3]++;
						else if (opponentCount == 2)
							featureVector[4]++;
						else if (opponentCount == 3)
							featureVector[5]++;
						if (opponentCount + (Connect4State.getHeight() - j) >= 4)
							featureVector[7]++;
					} else if (playerCount > 0) {
						if (playerCount == 2)
							featureVector[0]++;
						else if (playerCount == 3)
							featureVector[1]++;
						else if (playerCount == 4)
							featureVector[2]++;
						if (playerCount + (Connect4State.getHeight() -j ) >= 4)
							featureVector[6]++;
					}
					end = true;
				}
			}
		}
		//Row
		int[] ys = new int[] {0, 1, 2, 3, 4, 5};
		int[] xs = new int[] {0, 0, 0, 0, 0, 0};
		int[] lengths = new int[] {7, 7, 7, 7, 7, 7};
		int[] line;
		boolean[] playable;
		for (int i = 0; i < lengths.length; i++) {
			line = new int[lengths[i]];
			playable = new boolean[lengths[i]];
			for (int j = 0; j < lengths[i]; j++) {
				int x = xs[i] + j;
				int y = ys[i];
				line[j] = state_.getLocation(x, y);
				if (state_.getLocation(x, y) == Location.EMPTY.ordinal() && (y == 0 || state_.getLocation(x, y - 1) != Location.EMPTY.ordinal()))
					playable[j] = true;
				else
					playable[j] = false;
			}
			checkLine(player, line, playable, featureVector);
		}
		//Diagnal
		ys = new int[] {2, 1, 0, 0, 0, 0};
		xs = new int[] {0, 0, 0, 1, 2, 3};
		lengths = new int[] {4, 5, 6, 6, 5, 4};
		for (int i = 0; i < lengths.length; i++) {
			line = new int[lengths[i]];
			playable = new boolean[lengths[i]];
			for (int j = 0; j < lengths[i]; j++) {
				int x = xs[i] + j;
				int y = ys[i] + j;
				line[j] = state_.getLocation(x, y);
				if (state_.getLocation(x, y) == Location.EMPTY.ordinal() && (y == 0 || state_.getLocation(x, y - 1) != Location.EMPTY.ordinal()))
					playable[j] = true;
				else
					playable[j] = false;
			}
			checkLine(player, line, playable, featureVector);
		}
		//Diagnal
		ys = new int[] {0, 0, 0, 0, 1, 2};
		xs = new int[] {3, 4, 5, 6, 6, 6};
		//lengths = new int[] {4, 5, 6, 6, 5, 4};
		for (int i = 0; i < lengths.length; i++) {
			line = new int[lengths[i]];
			playable = new boolean[lengths[i]];
			for (int j = 0; j < lengths[i]; j++) {
				int x = xs[i] - j;
				int y = ys[i] + j;
				line[j] = state_.getLocation(x, y);
				if (state_.getLocation(x, y) == Location.EMPTY.ordinal() && (y == 0 || state_.getLocation(x, y - 1) != Location.EMPTY.ordinal()))
					playable[j] = true;
				else
					playable[j] = false;
			}
			checkLine(player, line, playable, featureVector);
		}
		return featureVector;
	}
	
	private void checkLine(Location player, int[] line, boolean[] playable, double[] featureVector) {
		Location opponent;
		if (player == Location.BLACK)
			opponent = Location.WHITE;
		else
			opponent = Location.BLACK;
		int playerCount = 0;
		int opponentCount = 0;
		int opponentBonus = 0;
		for (int i = 0; i < line.length; i++) {
			if (line[i] == player.ordinal()) {
				playerCount++;
			} else {
				if (playerCount == 2)
					featureVector[0]++;
				else if (playerCount == 3)
					featureVector[1]++;
				else if (playerCount >= 4)
					featureVector[2]++;
				playerCount = 0;
			}
			if (line[i] == opponent.ordinal()) {
				opponentCount++;
			} else {
				if (line[i] == Location.EMPTY.ordinal() && playable[i]) {
					//if (opponentCount > 0) {
						if (opponentCount == 1)
							featureVector[3]++;
						else if (opponentCount== 2)
							featureVector[4]++;
						else if (opponentCount == 3)
							featureVector[5]++;
					//}
					opponentBonus = 1;
					opponentCount = 0;
				} else {
					if (opponentCount + opponentBonus == 2)
						featureVector[3]++;
					else if (opponentCount + opponentBonus == 3)
						featureVector[4]++;
					else if (opponentCount + opponentBonus == 4)
						featureVector[5]++;
					opponentBonus = 0;
					opponentCount = 0;
				}
			}
		}
		int[] empties = new int[line.length];
		for (int i = 0; i < line.length - 3; i++) {
			boolean hasPlayer = false;
			boolean hasOpponent = false;
			int empty = -1;
			for (int j = i; j < i + 4; j++) {
				if (line[j] == player.ordinal()) {
					hasPlayer = true;
				} else if (line[j] == opponent.ordinal()) {
					hasOpponent = true;
				} else {
					if (empties[j] == 0 && empty == -1)
						empty = j;
				}
			}
			if (empty != -1) {
				if (hasPlayer && !hasOpponent) {
					featureVector[6]++;
					empties[empty]++;
				} else if (!hasPlayer && hasOpponent) {
					featureVector[7]++;
					empties[empty]++;
				}
			}
		}
	}
}
