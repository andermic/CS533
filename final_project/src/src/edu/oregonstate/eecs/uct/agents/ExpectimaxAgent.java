package edu.oregonstate.eecs.uct.agents;

import java.util.List;

import edu.oregonstate.eecs.uct.Action;
import edu.oregonstate.eecs.uct.Agent;
import edu.oregonstate.eecs.uct.Simulator;
import edu.oregonstate.eecs.uct.State;

/**
 * The Brute Force Agent explore the entire tree up to a certain depth.
 */
public class ExpectimaxAgent extends Agent {
	/** Max depth of search tree. */
    private int maxDepth_;
    /** Sparse sample size of states. */
    private int sampleSize_;
    /** Number of Monte-Carlo simulations run at leaves of tree. */
    private int numSimulations_;
    
    public ExpectimaxAgent(int maxDepth, int sampleSize, int numSimulations) {
    	if (maxDepth < 1 || sampleSize < 1 || numSimulations < 1)
    		throw new IllegalArgumentException("Max depth > 0 : Sample Size > 0 : Number of Simulations > 0");
    	name_ = "Expectimax";
    	maxDepth_ = maxDepth;
    	sampleSize_ = sampleSize;
    	numSimulations_ = numSimulations;
    }
	
	@Override
	public <S extends State, A extends Action> A selectAction(S state, Simulator<S, A> simulator) {
		simulator.setState(state);
		List<A> actions = simulator.getLegalActions();
		double[][] qValues = new double[actions.size()][simulator.getNumberOfAgents()];
		for (int i = 0; i < actions.size(); i++) {
			for (int j = 0; j < sampleSize_; j++) {
				Simulator<S, A> clone = simulator.copy();
				clone.takeAction(actions.get(i));
				double[] values = sparseSampleTree(clone, maxDepth_ - 1);
				int[] rewards = clone.getRewards();
				for (int k = 0; k < qValues[i].length; k++)
					qValues[i][k] = rewards[k] + values[k];
			}
			for (int j = 0; j < qValues[i].length; j++)
				qValues[i][j] /= sampleSize_;
		}
		//Find max qValue
		int best = 0;
		for (int i = 1; i < actions.size(); i++)
			if (qValues[i][simulator.getState().getAgentTurn()] > qValues[best][simulator.getState().getAgentTurn()])
				best = i;
		return actions.get(best);
	}
	
	private <S extends State, A extends Action> double[] sparseSampleTree(Simulator<S, A> simulator, int horizon) {
		List<A> actions = simulator.getLegalActions();
		if (actions.size() == 0) { //if terminal state return reward
			int[] rewards = simulator.getRewards();
			double[] values = new double[simulator.getNumberOfAgents()];
			for (int i = 0; i < values.length; i++)
				values[i] = rewards[i];
			return values;
		}
		double[][] qValues = new double[actions.size()][simulator.getNumberOfAgents()];
		for (int i = 0; i < actions.size(); i++) { //i = action index
			if (horizon > 0) {
				for (int j = 0; j < sampleSize_; j++) { //j = number of samples of taking action i
					Simulator<S, A> clone = simulator.copy();
					clone.takeAction(actions.get(i));
					double[] values = sparseSampleTree(clone, horizon - 1);
					int[] rewards = clone.getRewards();
					for (int k = 0; k < qValues[i].length; k++)
						qValues[i][k] = rewards[k] + values[k];
				}
				for (int j = 0; j < qValues[i].length; j++)
					qValues[i][j] /= sampleSize_;
			} else {
				double[] totalRewards = new double[simulator.getNumberOfAgents()];
				for (int k = 0; k < numSimulations_; k++) {
					double[] rewards = simulateGame(simulator.copy());
					for (int l = 0; l < rewards.length; l++)
						totalRewards[l] += rewards[l];
				}
				for (int k = 0; k < totalRewards.length; k++)
					totalRewards[k] /= numSimulations_;
				qValues[i] = totalRewards;
			}
		}
		//Find max qValue
		int best = 0;
		for (int i = 1; i < actions.size(); i++)
			if (qValues[i][simulator.getState().getAgentTurn()] > qValues[best][simulator.getState().getAgentTurn()])
				best = i;
		return qValues[best];
	}
	
	private <S extends State, A extends Action> double[] simulateGame(Simulator<S, A> simulator) {
		List<A> actions = simulator.getLegalActions();
		int[] rewards = simulator.getRewards();
		double[] totalRewards = new double[rewards.length];
		for (int i = 0; i < rewards.length; i++)
			totalRewards[i] += rewards[i];
		while (actions.size() > 0) {
			simulator.takeAction(actions.get((int) (Math.random() * actions.size())));
			for (int i = 0; i < totalRewards.length; i++)
				totalRewards[i] += simulator.getRewards()[i];
			actions = simulator.getLegalActions();
		}
		return totalRewards;
	}
}
