package edu.oregonstate.eecs.uct.domains.backgammon;

import java.util.ArrayList;
import java.util.List;

import edu.oregonstate.eecs.uct.Simulator;

public class BackgammonSimulator extends Simulator<BackgammonState, BackgammonAction> {
	private static final int NUMBER_OF_AGENTS = 2;
	
	public BackgammonSimulator() {
		setInitialState();
	}
	
	private BackgammonSimulator(BackgammonState state) {
		state_ = state;
		computeLegalActions();
	}

	@Override
	public Simulator<BackgammonState, BackgammonAction> copy() {
		return new BackgammonSimulator(state_);
	}
	
	@Override
	public void setInitialState() {
		byte[] locations = new byte[] {0,2,0,0,0,0,-5,0,-3,0,0,0,5,-5,0,0,0,3,0,5,0,0,0,0,-2,0};
		byte[] dice = new byte[BackgammonState.getNumberOfDice()];
		int agentTurn;
		
		do {
			dice[0] = (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1);
			dice[1] = (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1);
		} while (dice[0] == dice[1]);
		
		if (dice[0] > dice[1]) {
			dice[1] = (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1);
			agentTurn = 0;
		} else {
			dice[0] = (byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1);
			agentTurn = 1;
		}
		state_ = new BackgammonState(locations, dice, agentTurn);
		computeLegalActions();
	}
	
	@Override
	public void takeAction(BackgammonAction action) {
		if (!legalActions_.contains(action))
			throw new IllegalArgumentException("Action " + action + " not possible from current state.");
		
		byte[] locations = state_.getLocations();
		
		if (action.getMoveDistance(0) != 0) {
			for (int i = 0; i < action.getNumMoves(); i++) {
				int from = action.getMoveLocation(i);
				int distance = action.getMoveDistance(i);
				byte piece;
				if (locations[from] > 0)
					piece = 1;
				else
					piece = -1;
				int to = from + distance * piece;
				if (to > 0 && to < BackgammonState.getNumberOfLocations() - 1) {
					if (locations[to] * piece < 0) {
						locations[to] = piece;
						if (piece > 0)
							locations[25] -= piece;
						else
							locations[0] -= piece;
					} else
						locations[to] += piece;
				}
				locations[from] -= piece;
			}
		}
		byte[] dice = new byte[] {
			(byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1),
			(byte) (Math.random() * BackgammonState.getNumberOfDieFaces() + 1)
		};
		state_ = new BackgammonState(locations, dice, getNextAgentTurn(state_.getAgentTurn()));
		computeLegalActions();
	}
	
	private boolean isDuplicateMove(List<byte[]> initialMoves, byte die, byte[][] move) {
		for (byte[] initialMove: initialMoves)
			if (move[1][1] == die && initialMove[0] == move[1][0])
				return true;
		return false;
	}

	@Override
	protected void computeLegalActions() {
		legalActions_ = new ArrayList<BackgammonAction>();
		byte[] locations = state_.getLocations();
		byte[] dice = state_.getDice();
		byte piece;
		List<List<byte[]>> moves = new ArrayList<List<byte[]>>();
		
		if (state_.getAgentTurn() == 0)
			piece = 1;
		else
			piece = -1;
		
		if (dice[0] != dice[1]) {
			moves.add(getLegalMoves(locations, dice, piece));
			for (int i = 0; i < moves.get(0).size(); i++) {
				locations = state_.getLocations();
				locations[moves.get(0).get(i)[0]] -= piece;
				int movedTo = moves.get(0).get(i)[0] + moves.get(0).get(i)[1] * piece;
				if (movedTo > 0 && movedTo < BackgammonState.getNumberOfLocations() - 1) {
					if (locations[movedTo] == -piece)
						locations[movedTo] = piece;
					else
						locations[movedTo] += piece;
				}
				moves.add(getLegalMoves(locations, dice, piece));
				if (moves.get(1).size() > 0) {
					for (int j = 0; j < moves.get(1).size(); j++) {
						byte[][] move = new byte[2][2];
						move[0] = moves.get(0).get(i);
						move[1] = moves.get(1).get(j);
						if (move[0][1] != move[1][1] && !isDuplicateMove(moves.get(0), dice[0], move))
							legalActions_.add(new BackgammonAction(move));
					}
				} else if (legalActions_.size() == 0 || (legalActions_.get(0).getNumMoves() == 1 
						&& legalActions_.get(0).getMoveDistance(0) < moves.get(0).get(i)[1])) {
					byte[][] move = new byte[1][2];
					move[0] = moves.get(0).get(i);
					BackgammonAction newAction = new BackgammonAction(move);
					boolean found = false;
					for (BackgammonAction action: legalActions_)
						if (action.isIsomorphic(newAction)) {
							found = true;
							break;
						}
					if (found == false)
						legalActions_.add(newAction);
				}
				moves.remove(moves.size() - 1);
			}
		} else {
			byte[] tmpLoc1 = new byte[BackgammonState.getNumberOfLocations()];
			byte[] tmpLoc2 = new byte[BackgammonState.getNumberOfLocations()];
			byte[] die = new byte[] {dice[0]};
			moves.add(getLegalMoves(locations, die, piece));
			for (int i = 0; i < moves.get(0).size(); i++) {
				locations = state_.getLocations();
				locations[moves.get(0).get(i)[0]] -= piece;
				int movedTo = moves.get(0).get(i)[0] + moves.get(0).get(i)[1] * piece;
				if (movedTo >= 0 && movedTo < BackgammonState.getNumberOfLocations()) {
					if (locations[movedTo] == -piece)
						locations[movedTo] = piece;
					else
						locations[movedTo] += piece;
				}
				moves.add(getLegalMoves(locations, die, piece));
				if (moves.get(1).size() > 0) {
					for (int p = 0; p < i; p++)
						for (int q = 0; q < moves.get(1).size(); q++)
							if (moves.get(1).get(q)[0] == moves.get(0).get(p)[0])
								moves.get(1).remove(q);
					for (int n = 0; n < locations.length; n++)
						tmpLoc1[n] = locations[n];
					for (int j = 0; j < moves.get(1).size(); j++) {
						for (int n = 0; n < locations.length; n++)
							locations[n] = tmpLoc1[n];
						locations[moves.get(1).get(j)[0]] -= piece;
						movedTo = moves.get(1).get(j)[0] + moves.get(1).get(j)[1] * piece;
						if (movedTo >= 0 && movedTo < BackgammonState.getNumberOfLocations()) {
							if (locations[movedTo] == -piece)
								locations[movedTo] = piece;
							else
								locations[movedTo] += piece;
						}
						moves.add(getLegalMoves(locations, die, piece));
						if (moves.get(2).size() > 0) {
							for (int p = 0; p < j; p++)
								for (int q = 0; q < moves.get(2).size(); q++)
									if (moves.get(2).get(q)[0] == moves.get(1).get(p)[0])
										moves.get(2).remove(q);
							for (int n = 0; n < locations.length; n++)
								tmpLoc2[n] = locations[n];
							for (int k = 0; k < moves.get(2).size(); k++) {
								for (int n = 0; n < locations.length; n++)
									locations[n] = tmpLoc2[n];
								locations[moves.get(2).get(k)[0]] -= piece;
								movedTo = moves.get(2).get(k)[0] + moves.get(2).get(k)[1] * piece;
								if (movedTo >= 0 && movedTo < BackgammonState.getNumberOfLocations()) {
									if (locations[movedTo] == -piece)
										locations[movedTo] = piece;
									else
										locations[movedTo] += piece;
								}
								moves.add(getLegalMoves(locations, die, piece));
								if (moves.get(3).size() > 0) {
									for (int p = 0; p < k; p++)
										for (int q = 0; q < moves.get(3).size(); q++)
											if (moves.get(3).get(q)[0] == moves.get(2).get(p)[0])
												moves.get(3).remove(q);
									for (int l = 0; l < moves.get(3).size(); l++) {
										byte[][] move = new byte[4][2];
										move[0] = moves.get(0).get(i);
										move[1] = moves.get(1).get(j);
										move[2] = moves.get(2).get(k);
										move[3] = moves.get(3).get(l);
										BackgammonAction newAction = new BackgammonAction(move);
										boolean found = false;
										for (BackgammonAction action: legalActions_)
											if (action.isIsomorphic(newAction)) {
												found = true;
												break;
											}
										if (found == false)
											legalActions_.add(newAction);
									}
								} else {
									byte[][] move = new byte[4][2];
									move[0] = moves.get(0).get(i);
									move[1] = moves.get(1).get(j);
									move[2] = moves.get(2).get(k);
									BackgammonAction newAction = new BackgammonAction(move);
									boolean found = false;
									for (BackgammonAction action: legalActions_)
										if (action.isIsomorphic(newAction)) {
											found = true;
											break;
										}
									if (found == false)
										legalActions_.add(newAction);
								}
								moves.remove(moves.size() - 1);
							}
						} else {
							byte[][] move = new byte[2][2];
							move[0] = moves.get(0).get(i);
							move[1] = moves.get(1).get(j);
							BackgammonAction newAction = new BackgammonAction(move);
							boolean found = false;
							for (BackgammonAction action: legalActions_)
								if (action.isIsomorphic(newAction)) {
									found = true;
									break;
								}
							if (found == false)
								legalActions_.add(newAction);
						}
						moves.remove(moves.size() - 1);
					}
				} else {
					byte[][] move = new byte[1][2];
					move[0] = moves.get(0).get(i);
					BackgammonAction newAction = new BackgammonAction(move);
					boolean found = false;
					for (BackgammonAction action: legalActions_)
						if (action.isIsomorphic(newAction)) {
							found = true;
							break;
						}
					if (found == false)
						legalActions_.add(newAction);
				}
				moves.remove(moves.size() - 1);
			}
		}
		if (legalActions_.size() == 0 && getRewards()[0] == 0)
			legalActions_.add(new BackgammonAction(new byte[1][2]));
	}
	
	/**
	 * Gets a list of partial move actions from
	 * temporary state.
	 * @param locations
	 * @param dice
	 * @param piece
	 * @return
	 */
	private List<byte[]> getLegalMoves(byte[] locations, byte[] dice, byte piece) {
		List<byte[]> moves = new ArrayList<byte[]>();
		boolean moveOff = canMoveOff(locations, piece);
		if (piece > 0 && locations[0] > 0) {
			for (int i = 0; i < dice.length; i++) {
				int next = dice[i];
				if (locations[next] * piece >= -1)
					moves.add(new byte[] {0,dice[i]});
			}
		} else if (piece < 0 && locations[25] < 0) {
			for (int i = 0; i < dice.length; i++) {
				int next = 25 - dice[i];
				if (locations[next] * piece >= -1)
					moves.add(new byte[] {25,dice[i]});
			}
		} else {
			for (int j = 1; j < BackgammonState.getNumberOfLocations() - 1; j++) {
				if (piece * locations[j] > 0) {
					for (int k = 0; k < dice.length; k++) {
						int next = j + dice[k] * piece;
						if ((next > 0 && next < BackgammonState.getNumberOfLocations() - 1 && locations[next] * piece >= -1) || 
								(moveOff && (next <= 0 || next >= BackgammonState.getNumberOfLocations() - 1)))
							moves.add(new byte[] {(byte) j,dice[k]});
					}
				}
			}
		}
		return moves;
	}
	
//	private List<int[]> getLegalMoves(int[] locations, int die, int piece) {
//		List<int[]> moves = new ArrayList<int[]>();
//		boolean moveOff = canMoveOff(locations, piece);
//		if (piece > 0 && locations[0] > 0) {
//			int next = die;
//			if (locations[next] * piece >= -1)
//				moves.add(new int[] {0,die});
//		} else if (piece < 0 && locations[25] < 0) {
//			int next = 25 - die;
//			if (locations[next] * piece >= -1)
//				moves.add(new int[] {25,die});
//		} else {
//			for (int j = 1; j < BackgammonState.getNumberOfLocations() - 1; j++) {
//				if (piece * locations[j] > 0) {
//					int next = j + die * piece;
//					if ((next > 0 && next < BackgammonState.getNumberOfLocations() - 1 && locations[next] * piece >= -1) || 
//							(moveOff && (next <= 0 || next >= BackgammonState.getNumberOfLocations() - 1)))
//						moves.add(new int[] {j,die});
//				}
//			}
//		}
//		return moves;
//	}
	
	/**
	 * Checks if a player can start moving
	 * pieces off of the board.
	 * @param locations
	 * @param piece
	 * @return
	 */
	private boolean canMoveOff(byte[] locations, byte piece) {
		if (piece > 0) {
			for (int i = 0; i < 19; i++)
				if (locations[i] > 0)
					return false;
		} else {
			for (int i = 7; i < BackgammonState.getNumberOfLocations(); i++)
				if (locations[i] < 0)
					return false;
		}
		return true;
	}

	@Override
	public int[] getRewards() {
		boolean pos = false, neg = false;
		for (int i = 0; i < BackgammonState.getNumberOfLocations(); i++) {
			if (state_.getLocation(i) > 0)
				pos = true;
			else if (state_.getLocation(i) < 0)
				neg = true;
		}
		
		if (!pos)
			return new int[] {1,-1};
		else if (!neg)
			return new int[] {-1,1};
		
		return new int[] {0,0};
	}
	
	@Override
	public boolean isTerminalState() {
		return getRewards()[0] != 0;
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}
	
	@Override
	public double[] getFeatureVector(BackgammonAction action) {
		// TODO Auto-generated method stub
		return null;
	}
}
