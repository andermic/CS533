package edu.oregonstate.eecs.uct.domains.connect4;

import java.util.ArrayList;

import edu.oregonstate.eecs.uct.Simulator;

public class Connect4Simulator2 extends Simulator<Connect4State2, Connect4Action> {
	private static final int NUMBER_OF_AGENTS = 2;
	private static final long ALL_LOCATIONS = (1L << ((Connect4State2.getHeight() + 1) * Connect4State2.getWidth())) - 1;
	private static final long FIRST_COLUMN = (1L << Connect4State2.getHeight() + 1) - 1;
	private static final long BOTTOM_ROW = ALL_LOCATIONS / FIRST_COLUMN;
	private static final long ABOVE_TOP_ROW = BOTTOM_ROW << Connect4State2.getHeight();
	private static final Connect4State2 INITIAL_STATE = new Connect4State2(new long[2], 0);
	
	private int[] height_;

	public Connect4Simulator2() {
		setInitialState();
	}
	
	public Connect4Simulator2(Connect4State2 state, int[] height) {
		state_ = state;
		height_ = height;
		computeLegalActions();
	}

	@Override
	public Simulator<Connect4State2, Connect4Action> copy() {
		return new Connect4Simulator2(state_, height_);
	}

	@Override
	public void setInitialState() {
		state_ = INITIAL_STATE;
		computeLegalActions();
	}
	
	private void computeHeight() {
		height_ = new int[Connect4State2.getWidth()];
		long[] bitBoards = state_.getBitBoards();
		long bitBoard = bitBoards[0] | bitBoards[1];
		for (int i = 0; i < Connect4State2.getWidth(); i++) {
			height_[i] = (Connect4State2.getHeight() + 1) * i;
			while ((bitBoard & (1L << height_[i])) != 0)
				height_[i]++;
		}
	}

	@Override
	public void takeAction(Connect4Action action) {
		if (!legalActions_.contains(action))
			throw new IllegalArgumentException("Action " + action + " not possible from current state.");
		long[] bitBoards = state_.getBitBoards();
		bitBoards[state_.getAgentTurn()] ^= (1L << (height_[action.getLocation()]++));
		state_ = new Connect4State2(bitBoards, getNextAgentTurn(state_.getAgentTurn()));
		computeLegalActions();
	}
	
	@Override
	protected void computeLegalActions() {
		legalActions_ = new ArrayList<Connect4Action>();
		computeHeight();
		if (getRewards()[0] == 0) {
			long bitBoard = state_.getBitBoards()[state_.getAgentTurn()];
			for (int i = 0; i < Connect4State2.getWidth(); i++)
				if (((bitBoard | (1L << height_[i])) & ABOVE_TOP_ROW) == 0)
					legalActions_.add(Connect4Action.valueOf(i));
		}
	}
	
	@Override
	public int[] getRewards() {
		long[] bitBoards = state_.getBitBoards();
		int height = Connect4State2.getHeight();
		
		for (int i = 0; i < bitBoards.length; i++) {
			long bitBoard = bitBoards[i];
			long diagonal1 = bitBoard & (bitBoard >> height);
			long horizontal = bitBoard & (bitBoard >> (height + 1));
			long diagonal2 = bitBoard & (bitBoard >> (height + 2));
			long vertical = bitBoard & (bitBoard >> 1);
		    if (((diagonal1 & (diagonal1 >> 2 * height)) |
		            (horizontal & (horizontal >> 2 * (height + 1))) |
		            (diagonal2 & (diagonal2 >> 2 * (height + 2))) |
		            (vertical & (vertical >> 2))) != 0) {
				if (i == 0)
					return new int[] {1,-1};
				else
					return new int[] {-1,1};
			}
		}
		return new int[] {0,0};
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}
	
	@Override
	public double[] getFeatureVector(Connect4Action action) {
		// TODO Auto-generated method stub
		return null;
	}
}
