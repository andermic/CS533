package edu.oregonstate.eecs.uct.domains.biniax;

import edu.oregonstate.eecs.uct.Simulator;

import java.util.ArrayList;

/**
 * Biniax is a single agent stochastic domain.
 */
public final class BiniaxSimulator extends Simulator<BiniaxState, BiniaxAction> {
	private static final int NUMBER_OF_AGENTS = 1;
	private static final int BUFFER = 3;
	private static final int NUMBER_OF_FREE_MOVES = 2;
	private static final int INITIAL_ELEMENTS = 4;
	private static final int ELEMENT_INCREMENT_INTERVAL = 32;
	
	/** Length of game. */
	private int turns_;
	/** The number of distinct element types that can be generated */
    private int numElements_;
	
	/**
	 * Create a Biniax simulator and set to initial state.
	 */
	public BiniaxSimulator() {
        setInitialState();
	}
	
	/**
	 * Create a Biniax simulator and set the state.
	 * @param state simulator set to given biniax state.
	 * @param numElements number of elements possible in element pairs.
	 * @param turns the total number of turns game has gone on for.
	 */
	private BiniaxSimulator(BiniaxState state, int numElements, int turns) {
		state_ = state;
		numElements_ = numElements;
		turns_ = turns;
		computeLegalActions();
	}
	
	public Simulator<BiniaxState, BiniaxAction> copy() {
		return new BiniaxSimulator(state_, numElements_, turns_);
	}
	
	@Override
	public void setInitialState() {
		numElements_ = INITIAL_ELEMENTS;
		turns_ = 0;
		int[][] locations = new int[BiniaxState.getWidth()][BiniaxState.getHeight()];
		for (int i = 0; i < BiniaxState.getHeight(); i++) {
			int emptyLocation = (int) (Math.random() * BiniaxState.getWidth());
			for (int j = 0; j < BiniaxState.getWidth(); j++)
				if (j != emptyLocation && i < BiniaxState.getHeight() - BUFFER) {
					locations[j][i] = generateRandomElementPair();
					if (i == BiniaxState.getHeight() - BUFFER - 1)
						locations[j][i] = locations[j][i] % 10 + 10;
				}
		}
		locations[BiniaxState.getWidth() / 2][BiniaxState.getHeight() - 1] = 1;
        state_ = new BiniaxState(locations, NUMBER_OF_FREE_MOVES);
        computeLegalActions();
	}
	
	@Override
	public void takeAction(BiniaxAction action) {
		if (!legalActions_.contains(action))
			throw new IllegalArgumentException("Action " + action + " not possible from current state.");
		
        int[][] locations = state_.getLocations();
        int freeMoves = state_.getFreeMoves();
        int[] elementLocation = getElementLocation();
        int x = elementLocation[0];
        int y = elementLocation[1];
		int element = state_.getLocation(x, y);
		
		locations[x][y] = 0;
		switch (action) {
		case NORTH:
            y--;
			break;
		case EAST:
            x++;
			break;
		case SOUTH:
            y++;
			break;
		case WEST:
            x--;
			break;
		}
		
		if (locations[x][y] / 10 == element)
			element = locations[x][y] % 10;
		else if (locations[x][y] % 10 == element)
		    element = locations[x][y] / 10;
        locations[x][y] = element;
        
        freeMoves--;
		if (freeMoves == 0) {
            freeMoves = NUMBER_OF_FREE_MOVES;
			//Move all elements down
        	int emptyLocation = (int) (Math.random() * BiniaxState.getWidth());
            for (int i = BiniaxState.getHeight() - 1; i >= 0; i--) {
                for (int j = 0; j < BiniaxState.getWidth(); j++) {
	                if (i == 0) {
					    if (j != emptyLocation)
					    	locations[j][i] = generateRandomElementPair();
					    else
					    	locations[j][i] = 0;
	                } else
	                	locations[j][i] = locations[j][i - 1];
                }
            }
            //Move element back up if possible
        	if (locations[x][y] == 0) {
        		locations[x][y] = element;
        		if (y < BiniaxState.getHeight() - 1)
        			locations[x][y + 1] = 0;
        	} else if (locations[x][y] / 10 == element) {
        		locations[x][y] = locations[x][y] % 10;
        		if (y < BiniaxState.getHeight() - 1)
        			locations[x][y + 1] = 0;
        	} else if (locations[x][y] % 10 == element) {
        		locations[x][y] = locations[x][y] / 10;
        		if (y < BiniaxState.getHeight() - 1)
        			locations[x][y + 1] = 0;
        	}
		}
		turns_++;
		if (turns_ >= ELEMENT_INCREMENT_INTERVAL && numElements_ < BiniaxState.getMaxElements()) {
			turns_ = 0;
			numElements_++;
		}
		state_ = new BiniaxState(locations, freeMoves);
		computeLegalActions();
	}
	
	/**
	 * Creates a random element pair of dissimilar elements
	 * The elements are always in order from smallest to largest
	 * @return int of random values from 0 to numElements_ - 1
	 */
	private int generateRandomElementPair() {
		int element1 = ((int) (Math.random() * numElements_)) + 1;
		int element2 = ((int) (Math.random() * (numElements_ - 1))) + 1;
		if (element1 <= element2)
			return element1 * 10 + element2 + 1;
		else
			return element2 * 10 + element1;
	}
	
	private int[] getElementLocation() {
		for (int i = 0; i < BiniaxState.getWidth(); i++)
			for (int j = 0; j < BiniaxState.getHeight(); j++)
				if (state_.getLocation(i, j) > 0 && state_.getLocation(i, j) < 10)
					return new int[] {i, j};
		throw new IllegalStateException("Element does not exist");
	}

	/**
	 * A legal action is one that moves the single
	 * element to an empty space or an element pair that
	 * contains that element and avoids being pushed off
	 * the board.
	 * @return List of legal actions
	 */
	@Override
	protected void computeLegalActions() {
		legalActions_ = new ArrayList<BiniaxAction>();
		int[] elementLocation = getElementLocation();
		int x = elementLocation[0];
		int y = elementLocation[1];
		int element = state_.getLocation(x, y);
		int[][] locations = state_.getLocations();
		
		if (y != 0 && (locations[x][y - 1] == 0
				|| locations[x][y - 1] / 10 == element
				|| locations[x][y - 1] % 10 == element))
			legalActions_.add(BiniaxAction.NORTH);
		
		if (x != BiniaxState.getWidth() - 1) {
			int nextElement = 0;
			if (locations[x + 1][y] == 0)
				nextElement = element;
			else if (locations[x + 1][y] / 10 == element)
				nextElement = locations[x + 1][y] % 10;
			else if (locations[x + 1][y] % 10 == element)
				nextElement = locations[x + 1][y] / 10;
			
			if (nextElement != 0) {
				if (state_.getFreeMoves() > 1 || y < BiniaxState.getHeight() - 1
						|| locations[x + 1][y - 1] == 0
						|| locations[x + 1][y - 1] / 10 == nextElement
						|| locations[x + 1][y - 1] % 10 == nextElement)
				legalActions_.add(BiniaxAction.EAST);
			}
		}
		
		if (y != BiniaxState.getHeight() - 1
				&& (locations[x][y + 1] == 0
						|| locations[x][y + 1] / 10 == element
						|| locations[x][y + 1] % 10 == element))
			legalActions_.add(BiniaxAction.SOUTH);
		
		if (x != 0) {
			int nextElement = 0;
			if (locations[x - 1][y] == 0)
				nextElement = element;
			else if (locations[x - 1][y] / 10 == element)
				nextElement = locations[x - 1][y] % 10;
			else if (locations[x - 1][y] % 10 == element)
				nextElement = locations[x - 1][y] / 10;
			
			if (nextElement != 0) {
				if (state_.getFreeMoves() > 1 || y < BiniaxState.getHeight() - 1
						|| locations[x - 1][y - 1] == 0
						|| locations[x - 1][y - 1] / 10 == nextElement
						|| locations[x - 1][y - 1] % 10 == nextElement)
				legalActions_.add(BiniaxAction.WEST);
			}
		}
	}

	/**
	 * Each state has a reward of 1
	 * thus survival for as long as
	 * possible is the goal.
	 */
	@Override
	public int[] getRewards() {
		return new int[] {1};
	}
	
	@Override
	public int getNumberOfAgents() {
		return NUMBER_OF_AGENTS;
	}
	
	@Override
	public double[] getFeatureVector(BiniaxAction action) {
		// TODO Auto-generated method stub
		return null;
	}
}
