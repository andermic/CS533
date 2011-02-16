package edu.oregonstate.eecs.uct;

import java.util.ArrayList;
import java.util.List;

/**
 * A simulator allows a game to be played in
 * a given domain.  The state and action types are
 * both from the given domain.
 */
public abstract class Simulator<S extends State, A extends Action> {
	/** Every simulator contains a current state. */
	protected S state_;
	/** List of legal actions from current state. */
	protected List<A> legalActions_;
	
	/**
	 * Create a copy of the simulator.
	 * @return an identical copy of the simulator.
	 */
	public abstract Simulator<S, A> copy();
	
	/**
	 * Sets the simulator to an initial state.
	 */
	public abstract void setInitialState();
	
	/**
	 * Sets simulator to an arbitrary state.
	 * @param state any state.
	 */
	public void setState(S state) {
		state_ = state;
		computeLegalActions();
	}
	
	/**
	 * A simulator can take an action to change its current state.
	 * @param action action to be taken.
	 * @exception throws IllegalArgumentException 
	 * if action is illegal from current state.
	 */
	public abstract void takeAction(A action);
	
	/**
	 * This method populates legalActions_.
	 * It must be called whenever the state
	 * of the simulator changes.
	 */
	protected abstract void computeLegalActions();
	
	/**
	 * Based on the current state this method
	 * will return a list of possible actions
	 * that may be taken. This method assumes
	 * that all actions are immutable objects.
	 * Otherwise, it must be overridden.
	 * @return a List of legal actions from current state.
	 */
	public List<A> getLegalActions() {
		List<A> legalActions = new ArrayList<A>();
			for (A action: legalActions_)
				legalActions.add(action);
		return legalActions;
	}
	
	/**
	 * Rewards for each agent may be indexed
	 * by that agent's id.
	 * @return array of rewards for each agent.
	 */
	public abstract int[] getRewards();
	
	/**
	 * A state is terminal if there are no
	 * legal actions from that state.
	 * @return true if state is terminal.
	 */
	public boolean isTerminalState() {
		return legalActions_.size() == 0;
	}
	
	/**
	 * This method assumes that agents
	 * take alternating turns.
	 * @param agentTurn agent turn.
	 * @return the agent id for next turn.
	 */
	public int getNextAgentTurn(int agentTurn) {
		return (agentTurn + 1) % getNumberOfAgents();
	}
	
	/**
	 * Gets the number of agents taking actions
	 * in the given domain.
	 * @return number of agents.
	 */
	public abstract int getNumberOfAgents();
	
	/**
	 * State that represents the current
	 * state in the simulator is returned.
	 * @return current state.
	 */
	public S getState() {
		return state_;
	}
	
	/**
	 * Used to generate a vector of features given
	 * the current state and an action taken from that state.
	 * @param action action taken from current state.
	 * @return feature vector given current state and action taken.
	 */
	public abstract double[] getFeatureVector(A action);
	
	@Override
	public String toString() {
		return state_.toString();
	}
}
