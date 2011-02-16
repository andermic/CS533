package edu.oregonstate.eecs.uct;

/**
 * A state represents the current state
 * of some domain.
 * A state should override toString(),
 * equals() and hashCode().
 */
public abstract class State {
	/** Each state keeps track of the agent to move next */
	protected int agentTurn_;
	
	public int getAgentTurn() {
		return agentTurn_;
	}
}