package edu.oregonstate.eecs.uct.agents;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//import weka.classifiers.functions.LinearRegression;
//import weka.core.Instances;
import edu.oregonstate.eecs.uct.Action;
import edu.oregonstate.eecs.uct.Agent;
import edu.oregonstate.eecs.uct.Simulator;
import edu.oregonstate.eecs.uct.State;

/**
 * Enhanced UCT agent that can be run as normal
 * UCT or a modified version of UCT based on
 * input parameters.
 */
public final class UctAgent extends Agent {
	/** Number of trajectories UCT runs. */
	private int numberOfSimulations_;
	/** UCT Constant */
	private double uctConstant_;
	/** Number of state samples to inspect from each action. */
	private int sparseSampleSize_;
	/** Number of trees to build and evaluate. */
	private int numberOfEnsembles_;
	/** Indicates ensemble method. */
	private EnsembleMethod ensembleMethod_;
	/** Indicates simulation method. */
	private SimulationMethod simulationMethod_;
	/** Controls randomness in simulated games. */
	private double boltzmannTemperature_;
	
	public enum EnsembleMethod {
		ROOT_ENSEMBLE,
		ROOT_PARALLELIZATION,
		PLURALITY_VOTE,
		INSTANT_RUNOFF_VOTE,
		BORDA_COUNT_VOTE
	}
	
	public enum SimulationMethod {
		RANDOM,
		BOLTZMANN
	}
	
	/** If true then output root tree data. */
	private boolean verbose_ = false;
	
	private boolean recordData_ = true;
	private List<List<Long>> timingData_;
//	private long[] trajectoryTimes_;
//	private long[] randomSimulationTimes_;
//	private long[] uctSelectTimes_;
//	private long[] sparseSamplingTimes_;
//	private int currentSimulationIndex_;
	
	private double[] w_;
//	private LinearRegression linearRegression_;
//	private Instances instances_;
	
	/**
	 * Defines a general node in a UCT tree.
	 * The rewards_ field is lazily initialized
	 * because its size isn't known until
	 * the first rewards vector is passed to update.
	 */
	private abstract class Node {
		protected int[] rewards_ = null;
		protected int visits_ = 0;
		
		/**
		 * Adds rewards and increments visits.
		 * @param rewards for each agent.
		 */
		public void update(int[] rewards) {
			if (rewards_ == null)
				rewards_ = new int[rewards.length];
			for (int i = 0; i < rewards.length; i++)
				rewards_[i] += rewards[i];
			visits_++;
		}
		
		public int getVisits() {
			return visits_;
		}
		
		/**
		 * Assumes visits_ > 0.
		 * @param agentId get total reward for specific agent.
		 * @return accumulated reward for specified agent.
		 * @throws NullPointerException if visits_ < 1.
		 */
		public int getTotalReward(int agentId) {
			return rewards_[agentId];
		}
	}
	
	/**
	 * Holds a state and a list of pointers to action nodes.
	 * The action nodes represent all legal moves from the contained
	 * state.
	 */
	private class StateNode<S extends State, A extends Action> extends Node {
		private S state_;
		private List<ActionNode<S, A>> children_;
		
		public StateNode(S state, List<A> legalActions) {
			state_ = state;
			children_ = new ArrayList<ActionNode<S, A>>();
	    	for (A action: legalActions)
	    		children_.add(new ActionNode<S, A>(action));
		}
		
		/**
	     * Select child node with best UCT value.
	     * Always play a random unexplored action first.
	     * @return an action child node or null if none exist.
	     */
	    public ActionNode<S, A> uctSelect() {
			ActionNode<S, A> result = null;
			double bestUct = 0;
			double uctValue;
			for (ActionNode<S, A> child: children_) {
				if (child.getVisits() > 0)
					uctValue = ((double) child.getTotalReward(state_.getAgentTurn())) / child.getVisits() + 
						uctConstant_ * Math.sqrt(Math.log(getVisits()) / child.getVisits());
				else
					uctValue = Integer.MAX_VALUE - Math.random() * 1024;
	
				if (result == null || uctValue > bestUct) {
					bestUct = uctValue;
					result = child;
				}
			}
			return result;
		}
	    
	    /**
	     * Sends data to specific file
		 * TODO
		 * -update filepath name to be dynamic based on agent parameters and policy
	     */
//	    public void collectData() {
//	    	String filepath = "featureData/testData";
//			try {
//				BufferedWriter output = new BufferedWriter(new FileWriter(filepath, true));
//				for (ActionNode<S, A> child: children_) {
//					//None domain specific feature data
//					double qValue = child.getAvgReward(state_.getAgentTurn());
//					double[] featureVector = state_.getFeatureVector(child.getAction());
//					for (double feature: featureVector)
//						output.write(feature + ",");
//					output.write(String.valueOf(qValue) + "\n");
//				}
//				output.close();
//			} catch (IOException e) {
//				System.out.println(e);
//			}
//	    }

		public S getState() {
			return state_;
		}
		
		public List<ActionNode<S, A>> getChildren() {
			return children_;
		}
	}
	
	private class ActionNode<S extends State, A extends Action> extends Node {
		private A action_;
		private List<StateNode<S, A>> children_;
		
		public ActionNode(A action) {
			action_ = action;
			children_ = new ArrayList<StateNode<S, A>>();
		}
		
		/**
		 * Will take an action from the current simulator's
		 * state, create a new state node at the next state
		 * and return that state node. If sparse sampling
		 * limit has been reach then a random node is returned
		 * from the current list of children (this is faster).
		 * @param simulator used to simulate actions.
		 * @return selected child state node.
		 */
		public StateNode<S, A> selectChild(Simulator<S, A> simulator) {
			if (sparseSampleSize_ == -1) {
				Simulator<S, A> clone = simulator.copy();
				clone.takeAction(action_);
				S state = clone.getState();
				for (StateNode<S, A> child: children_)
					if (child.getState().equals(state))
						return child;
				StateNode<S, A> stateNode = new StateNode<S, A>(state, clone.getLegalActions());
				children_.add(stateNode);
				return stateNode;
			} else if (children_.size() < sparseSampleSize_) {
				Simulator<S, A> clone = simulator.copy();
				clone.takeAction(action_);
				StateNode<S, A> stateNode = new StateNode<S, A>(clone.getState(), clone.getLegalActions());
				children_.add(stateNode);
				return stateNode;
			} else
				return children_.get((int) (Math.random() * children_.size()));
		}
	}
	
	/**
	 * Create a traditional UCT agent.
	 * @param numSimulations the number of complete games played.
	 * @param uctConstant controls balance between exploration and exploitation.
	 */
	public UctAgent(int numSimulations, double uctConstant) {
		if (numSimulations < 1)
			throw new IllegalArgumentException("Number of Simulations < 1");
		if (uctConstant < 0)
			throw new IllegalArgumentException("UCT Constant > 0");
		name_ = "UCT";
		numberOfSimulations_ = numSimulations;
		uctConstant_ = uctConstant;
		sparseSampleSize_ = -1;
		numberOfEnsembles_ = 1;
		ensembleMethod_ = EnsembleMethod.ROOT_ENSEMBLE;
		simulationMethod_ = SimulationMethod.RANDOM;
	}
	
	/**
	 * UCT algorithm with sparse sampling of large stochastic
	 * state spaces.
	 * @param numSimulations the number of complete games played.
	 * @param uctConstant controls balance between exploration and exploitation.
	 * @param sparseSampleSize max number of sample states from any action
	 * node or infinite if equal to -1.
	 */
	public UctAgent(int numSimulations, double uctConstant, int sparseSampleSize) {
		this(numSimulations, uctConstant);
		if (sparseSampleSize < 1 && sparseSampleSize != -1)
			throw new IllegalArgumentException("Sparse Sample Size > 0 or = -1");
		sparseSampleSize_ = sparseSampleSize;
	}
	
	/**
	 * UCT algorithm with sparse sampling and ensemble methods.
	 * @param numSimulations the number of complete games played.
	 * @param uctConstant controls balance between exploration and exploitation.
	 * @param sparseSampleSize max number of sample states from any action
	 * node or infinite if equal to -1.
	 * @param ensembleTrials number of trees separate trees built.
	 */
	public UctAgent(int numSimulations,
				    double uctConstant,
				    int sparseSampleSize,
				    int ensembleTrials,
				    String ensembleMethod) {
		this(numSimulations, uctConstant, sparseSampleSize);
		if (ensembleTrials < 1)
			throw new IllegalArgumentException("Ensemble trials must be > 0");
		numberOfEnsembles_ = ensembleTrials;
		ensembleMethod_ = EnsembleMethod.valueOf(ensembleMethod);
	}
	
//	public UctAgent(int numSimulations, double uctK, int sampleSize, int ensembleTrials, String filepath) {
//		name_ = "UCT Ensemble";
//		numSimulations_ = numSimulations;
//		uctK_ = uctK;
//		sampleSize_ = sampleSize;
//		ensembleTrials_ = 1;
//		simulateRandom_ = false;
//		temperature_ = 1;
//		//initLinearRegression(filepath);
//	}
	
	@Override
	public <S extends State, A extends Action> A selectAction(S state, Simulator<S, A> simulator) {
		simulator.setState(state);
		List<A> legalActions = simulator.getLegalActions();
		//If only one action possible skip action selection algorithms
		if (legalActions.size() == 1)
			return legalActions.get(0);
//		if (numberOfSimulations_ <= legalActions.size())
//			if (maximumSimulations_ < legalActions.size())
//				maximumSimulations_ = legalActions.size();
//			throw new IllegalStateException("Number of simulations must be > number of legal actions from root = " +
//					legalActions.size() + ".");
		int agentTurn = simulator.getState().getAgentTurn();
		
		//Generate UCT trees and save root action values
		double[][] rootActionRewards = new double[numberOfEnsembles_][legalActions.size()];
		int[][] rootActionVisits = new int[numberOfEnsembles_][legalActions.size()];
		for (int i = 0; i < numberOfEnsembles_; i++) {
			StateNode<S, A> root = new StateNode<S, A>(simulator.getState(), legalActions);
			//If number of legal actions is larger then number of simulations then
			//number of simulations run is equal to number of legal actions so
			//that each root action gets selected at least once
			for (int j = 0; j < numberOfSimulations_ || j <= legalActions.size(); j++) {
				long start = 0;
				if (recordData_)
					start = System.nanoTime();
				playSimulation(root, simulator.copy());
				if (recordData_)
					timingData_.get(0).add(System.nanoTime() - start);
			}
			
			List<ActionNode<S,A>> children = root.getChildren();
			for (int j = 0; j < children.size(); j++) {
				rootActionRewards[i][j] = children.get(j).getTotalReward(agentTurn);
				rootActionVisits[i][j] = children.get(j).getVisits();
			}
		}
		if (verbose_) {
			System.out.println(state);
			for (int i = 0; i < rootActionRewards.length; i++) {
				System.out.print("[ ");
				int chosen = 0;
				double best = rootActionRewards[i][0] / rootActionVisits[i][0];
				for (int j = 0; j < rootActionRewards[i].length; j++) {
					System.out.print(rootActionRewards[i][j] + "/");
					System.out.print(rootActionVisits[i][j] + " ");
					if (rootActionRewards[i][j] / rootActionVisits[i][j] > best) {
						best = rootActionRewards[i][j] / rootActionVisits[i][j];
						chosen = j;
					}
				}
				System.out.print("] ");
				System.out.println("(" + chosen + ")");
			}
			selectActionIndex(rootActionRewards, rootActionVisits, EnsembleMethod.ROOT_ENSEMBLE, true);
			selectActionIndex(rootActionRewards, rootActionVisits, EnsembleMethod.ROOT_PARALLELIZATION, true);
			selectActionIndex(rootActionRewards, rootActionVisits, EnsembleMethod.PLURALITY_VOTE, true);
			selectActionIndex(rootActionRewards, rootActionVisits, EnsembleMethod.INSTANT_RUNOFF_VOTE, true);
			selectActionIndex(rootActionRewards, rootActionVisits, EnsembleMethod.BORDA_COUNT_VOTE, true);
		}
		return legalActions.get(selectActionIndex(rootActionRewards, rootActionVisits, ensembleMethod_, false));
	}
	
	private int selectActionIndex(double[][] rootActionsRewards, int[][] rootActionsVisits, EnsembleMethod ensembleMethod, boolean verbose) {
		int actionIndex = 0;
		int size = rootActionsRewards[0].length;
		switch(ensembleMethod) {
		case ROOT_ENSEMBLE:
			double[] qTable = new double[size];
			for (int i = 0; i < numberOfEnsembles_; i++)
				for (int j = 0; j < size; j++)
					qTable[j] += rootActionsRewards[i][j] / rootActionsVisits[i][j];
			for (int i = 1; i < qTable.length; i++)
				if (qTable[i] > qTable[actionIndex])
					actionIndex = i;
			if (verbose) {
				System.out.println(ensembleMethod);
				System.out.print("[ " + qTable[0] + " ");
				for (int i = 1; i < qTable.length; i++)
					System.out.print(qTable[i] + " ");
				System.out.println("] (" + actionIndex + ")");
			}
			break;
		case ROOT_PARALLELIZATION:
			double[] values = new double[size];
			int[] visits = new int[size];
			for (int i = 0; i < numberOfEnsembles_; i++)
				for (int j = 0; j < size; j++) {
					values[j] += rootActionsRewards[i][j];
					visits[j] += rootActionsVisits[i][j];
				}
			for (int i = 1; i < values.length; i++)
				if (values[i] / visits[i] > values[actionIndex] / visits[actionIndex])
					actionIndex = i;
			if (verbose) {
				System.out.println(ensembleMethod);
				System.out.print("[ " + values[0] / visits[0] + " ");
				for (int i = 1; i < size; i++)
					System.out.print(values[i] / visits[i] + " ");
				System.out.println("] (" + actionIndex + ")");
			}
			break;
		case PLURALITY_VOTE:
			int[] votes = new int[size];
			for (int i = 0; i < numberOfEnsembles_; i++) {
				int voteIndex = 0;
				double highestAvgReward = rootActionsRewards[i][0] / rootActionsVisits[i][0];
				for (int j = 1; j < size; j++) {
					double avgReward = rootActionsRewards[i][j] / rootActionsVisits[i][j];
					if (avgReward > highestAvgReward) {
						voteIndex = j;
						highestAvgReward = avgReward;
					}
				}
				votes[voteIndex] += 1;
			}
			List<Integer> selectedVotes = new LinkedList<Integer>();
			selectedVotes.add(0);
			for (int i = 1; i < votes.length; i++) {
				if (votes[i] >= votes[selectedVotes.get(0)]) {
					if (votes[i] > votes[selectedVotes.get(0)])
						selectedVotes.clear();
					selectedVotes.add(i);
				}
			}
			actionIndex = selectedVotes.get((int) (Math.random() * selectedVotes.size()));
			if (verbose) {
				System.out.println(ensembleMethod);
				System.out.print("[ " + votes[0] + " ");
				for (int i = 1; i < size; i++)
					System.out.print(votes[i] + " ");
				System.out.println("] (" + actionIndex + ")");
			}
			break;
		case INSTANT_RUNOFF_VOTE:
			List<int[]> ballots = new ArrayList<int[]>();
			for (int i = 0; i < numberOfEnsembles_; i++) {
				int[] ballot = new int[size];
				for (int j = 1; j < size; j++) {
					double avgReward = rootActionsRewards[i][j] / rootActionsVisits[i][j];
					for (int k = 1; j - k >= 0; k++) {
						if (avgReward > rootActionsRewards[i][ballot[j - k]] / rootActionsVisits[i][ballot[j - k]]) {
							ballot[j - k + 1] = ballot[j - k];
							ballot[j - k] = j;
						} else {
							ballot[j - k + 1] = j;
							break;
						}
					}
				}
				ballots.add(ballot);
			}
			//Eliminate 0 counts from ballots
			int[] counts = new int[size];
			for (int[] ballot: ballots)
				counts[ballot[0]] += 1;
			for (int i = 0; i < counts.length; i++) {
				if (counts[i] == 0) {
					for (int[] ballot: ballots) {
						boolean found = false;
						for (int j = 0; j < ballot.length; j++) {
							if (ballot[j] == i)
								found = true;
							if (found) {
								if (j < ballot.length - 1)
									ballot[j] = ballot[j + 1];
								else
									ballot[j] = -1;
							}
						}
					}
				}
			}
			actionIndex = -1;
			for (int i = 0; i < size && actionIndex == -1; i++) {
				counts = new int[size];
				for (int[] ballot: ballots)
					counts[ballot[0]] += 1;
				for (int j = 0; j < size && actionIndex == -1; j++)
					if (counts[j] > (int) (0.5 * ballots.size()))
						actionIndex = j;
				
				if (actionIndex == -1) {
					List<Integer> lowestIndices = new ArrayList<Integer>();
					for (int j = 0; j < counts.length; j++) {
						if (counts[j] > 0 && (lowestIndices.size() == 0 || counts[j] <= counts[lowestIndices.get(0)])) {
							if (lowestIndices.size() > 0 && counts[j] != counts[lowestIndices.get(0)])
								lowestIndices.clear();
							lowestIndices.add(j);
						}
					}
					int removedIndex = lowestIndices.get((int) (Math.random() * lowestIndices.size()));
					for (int[] ballot: ballots) {
						boolean found = false;
						for (int j = 0; j < ballot.length; j++) {
							if (ballot[j] == removedIndex)
								found = true;
							if (found) {
								if (j < ballot.length - 1)
									ballot[j] = ballot[j + 1];
								else
									ballot[j] = -1;
							}
						}
					}
				}
			}
			if (verbose) {
				System.out.println(ensembleMethod);
				System.out.println("(" + actionIndex + ")");
			}
			break;
		case BORDA_COUNT_VOTE:
			counts = new int[size];
			int[] voteRanks = new int[size];
			for (int i = 0; i < numberOfEnsembles_; i++) {
				voteRanks = new int[size];
				for (int j = 1; j < size; j++) {
					double avgReward = rootActionsRewards[i][j] / rootActionsVisits[i][j];
					for (int k = 1; j - k >= 0; k++) {
						if (avgReward < rootActionsRewards[i][voteRanks[j - k]] / rootActionsVisits[i][voteRanks[j - k]]) {
							voteRanks[j - k + 1] = voteRanks[j - k];
							voteRanks[j - k] = j;
						} else {
							voteRanks[j - k + 1] = j;
							break;
						}
					}
				}
				for (int j = 0; j < counts.length; j++)
					counts[voteRanks[j]] += j + 1;
			}
			List<Integer> selectedCounts = new LinkedList<Integer>();
			selectedCounts.add(0);
			for (int i = 1; i < counts.length; i++) {
				if (counts[i] >= counts[selectedCounts.get(0)]) {
					if (counts[i] > counts[selectedCounts.get(0)])
						selectedCounts.clear();
					selectedCounts.add(i);
				}
			}
			actionIndex = selectedCounts.get((int) (Math.random() * selectedCounts.size()));
			if (verbose) {
				System.out.println(ensembleMethod);
				System.out.print("[ " + counts[0] + " ");
				for (int i = 1; i < size; i++)
					System.out.print(counts[i] + " ");
				System.out.println("] (" + actionIndex + ")");
			}
			break;
		}
		return actionIndex;
	}
	
	/**
	 * This method walks down the tree making decisions
	 * of the best nodes as it goes.  When it reaches
	 * an unexplored leaf node it plays a random game
	 * to initialize that nodes value.
	 * @param node current state node being traversed in tree.
	 * @param simulator contains current state of game being played.
	 * @return rewards of simulated game are passed up the tree.
	 */
	private <S extends State, A extends Action> int[] playSimulation(StateNode<S, A> node, Simulator<S, A> simulator) {
		int[] rewards;
		long start = 0;
		
		if (simulator.isTerminalState() || node.getVisits() == 0) {
			if (recordData_)
				start = System.nanoTime();
			rewards = simulateGame(simulator);
			if (recordData_)
				timingData_.get(1).add(System.nanoTime() - start);
		} else {
			if (recordData_)
				start = System.nanoTime();
			ActionNode<S, A> actionNode = node.uctSelect();
			if (recordData_)
				timingData_.get(2).add(System.nanoTime() - start);
			rewards = playSimulation(actionNode, simulator);
		}
		
		node.update(rewards);
		return rewards;
	}
	
	/**
	 * This method walks down the tree making decisions
	 * of the best nodes as it goes.  When it reaches
	 * an unexplored leaf node it plays a random game
	 * to initialize that nodes value.
	 * @param node current action node being traversed in tree.
	 * @param simulator contains current state of game being played.
	 * @return rewards of simulated game are passed up the tree.
	 */
	private <S extends State, A extends Action> int[] playSimulation(ActionNode<S, A> node, Simulator<S, A> simulator) {
		long start = 0;
		if (recordData_)
			start = System.nanoTime();
		StateNode<S, A> child = node.selectChild(simulator);
		if (recordData_)
			timingData_.get(3).add(System.nanoTime() - start);
		simulator.setState(child.getState());
		int[] rewards = playSimulation(child, simulator);
		node.update(rewards);
		return rewards;
	}
	
	/**
	 * Quickly simulate a game from the current state and return accumulated reward.
	 * @param simulator a copy of the simulator you want to use to simulate game.
	 * @return accumulated reward from the game.
	 */
	private <S extends State, A extends Action> int[] simulateGame(Simulator<S, A> simulator) {
		List<A> actions = simulator.getLegalActions();
		int[] totalRewards = simulator.getRewards();
		while (!simulator.isTerminalState()) {
			switch (simulationMethod_) {
			case RANDOM:
				simulator.takeAction(actions.get((int) (Math.random() * actions.size())));
				break;
			case BOLTZMANN: //P(a|s) proportional to exp(Q(s,a) / T)
				double[] qValues = new double[actions.size()];
				double bestValue = -1.0;
				int bestAction = 0;
				S state = simulator.getState();
				for (int i = 0; i < actions.size(); i++) {
					double[] featureVector = null;//state.getFeatureVector(actions.get(i));
					double value = w_[w_.length - 1];
					for (int j = 0; j < featureVector.length; j++)
						value += featureVector[i] * w_[i];
					
					/*double[] vector = new double[9];
					for (int j = 0; j < featureVector.length; j++)
						vector[j] = featureVector[j];
					vector[8] = 0.0;
					Instance instance = new Instance(1.0, vector);
					instance.setDataset(instances_);
					instance.setClassMissing();
					double value = 0;
					try {
						value = linearRegression_.classifyInstance(instance);
					} catch (Exception e) {
						System.out.println(e);
						System.exit(1);
					}*/
					
//					if (value > bestValue)
//						bestAction = i;
					
					qValues[i] = value;
				}
				
				double[] boltzmannValues = new double[qValues.length];
				double total = 0;
				for (int i = 0; i < boltzmannValues.length; i++) {
					boltzmannValues[i] = Math.exp(qValues[i] / boltzmannTemperature_);
					total += qValues[i];
				}
				for (int i = 0; i < boltzmannValues.length; i++) {
					boltzmannValues[i] /= total;
				}
				
				double temp = Math.random();
				double sum = 0;
				for (int i = 0; i < boltzmannValues.length; i++) {
					sum += boltzmannValues[i];
					if (temp < sum) {
						bestAction = i;
						break;
					}
				}
				
				simulator.takeAction(actions.get(bestAction));
				break;
			}
			for (int i = 0; i < totalRewards.length; i++)
				totalRewards[i] += simulator.getRewards()[i];
			actions = simulator.getLegalActions();
		}
		return totalRewards;
	}
	
	/**
	 * 
	 * @param filepath is an arff file
	 */
//	public void initLinearRegression(String filepath) {
//		linearRegression_ = new LinearRegression();
//		try {
//			FileReader reader = new FileReader(filepath);
//			instances_ = new Instances(reader);
//			instances_.setClassIndex(instances_.numAttributes() - 1);
//			linearRegression_.buildClassifier(instances_);
//			w_ = linearRegression_.coefficients();
//			
//			//for (int i = 0; i < w_.length; i++)
//			//	System.out.println(w_[i]);
//			//System.out.println(linearRegression_.toString());
//			//System.exit(0);
//		} catch (Exception e) {
//			System.out.println(e);
//			System.exit(0);
//		}
//
//	}
	
	public List<List<Long>> getTimingData() {
		return timingData_;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append(super.toString());
		output.append("\n  Number of Simulations:     " + numberOfSimulations_);
		output.append("\n  UCT Constant:              " + uctConstant_);
		if (sparseSampleSize_ > 0)
			output.append("\n  Sparse Sample Size:        " + sparseSampleSize_);
		if (numberOfEnsembles_ > 1) {
			output.append("\n  Number of Ensembles:       " + numberOfEnsembles_);
			output.append("\n  Ensemble Method:           " + ensembleMethod_);
		}
//		output.append("\nStatistics");
//		output.append("\n  Average Simulation Time: " + ((double) totalSimulationTime_) / (numberOfEnsembles_ * numberOfSimulations_));
//		output.append("\n  Average Increase In Simulation Times: " + ((double) cumulativeDifferenceInSimulationTimes_) / (numberOfEnsembles_ * numberOfSimulations_));
//		output.append("\n  Average Random Game Time: " + ((double) cumulativeRandomGameTimes_) / (numberOfEnsembles_ * numberOfSimulations_));
		return output.toString();
	}
}
