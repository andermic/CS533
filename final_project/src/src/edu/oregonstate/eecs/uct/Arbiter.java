package edu.oregonstate.eecs.uct;

import java.util.ArrayList;
import java.util.List;

/**
 * Arbiter is used to regulate agents with the simulator.
 */
public class Arbiter<S extends State, A extends Action> {
	/** Real world domain being used. */
	private Simulator<S, A> world_;
	/** Agents taking action in domain. */
	private List<Agent> agents_;
	/** Keeps track of actions taken and states visited. */
	private History<S, A> history_;
	private List<long[]> totalMoveTimeData_;
	private List<double[]> totalRewardsData_;
	private List<int[]> actionCountsData_;
	
	public Arbiter(Simulator<S, A> world, List<Agent> agents) {
		if (world.getNumberOfAgents() != agents.size())
			throw new IllegalArgumentException("Expects " + world.getNumberOfAgents() + 
											   " agents: " + agents.size() + " provided");
		world_ = world;
		agents_ = agents;
		totalMoveTimeData_ = new ArrayList<long[]>();
		totalRewardsData_ = new ArrayList<double[]>();
		actionCountsData_ = new ArrayList<int[]>();
	}
	
	/**
	 * Play a single game to the end.
	 * @param simulatedWorld simulator used by agents to determine move.
	 */
	private void runSimulation(Simulator<S, A> simulatedWorld, List<Agent> agents, int[] agentMoveOrder) {
		long[] totalMoveTime = new long[agents.size()];
		double[] totalRewards = new double[agents.size()];
		int[] actionCounts = new int[agents.size()];
		world_.setInitialState();
		history_ = new History<S, A>(world_.getState());
		for (int i = 0; i < totalRewards.length; i++)
			totalRewards[i] = world_.getRewards()[i];
        while (!world_.isTerminalState()) {
    		int agentTurn = world_.getState().getAgentTurn();
        	long startTime = System.currentTimeMillis();
    		A action = agents.get(agentTurn).selectAction(world_.getState(), simulatedWorld.copy());
    		totalMoveTime[agentTurn] += System.currentTimeMillis() - startTime;
    		world_.takeAction(action);
    		for (int i = 0; i < totalRewards.length; i++)
    			totalRewards[i] += world_.getRewards()[i];
    		actionCounts[agentTurn]++;
        	history_.add(world_.getState(), action);
        }
        long[] totalMoveTime2 = new long[agents.size()];
		double[] totalRewards2 = new double[agents.size()];
		int[] actionCounts2 = new int[agents.size()];
        for (int i = 0; i < agents_.size(); i++) {
        	totalMoveTime2[i] = totalMoveTime[agentMoveOrder[i]];
        	totalRewards2[i] = totalRewards[agentMoveOrder[i]];
        	actionCounts2[i] = actionCounts[agentMoveOrder[i]];
        }
        totalMoveTimeData_.add(totalMoveTime2);
        totalRewardsData_.add(totalRewards2);
        actionCountsData_.add(actionCounts2);
	}
	
	public void runSimulations(Simulator<S, A> simulatedWorld, int numTrials) {
		int moveOrderDisplacement = 0;
		int[] agentMoveOrder = new int[agents_.size()];
		List<Agent> agents = new ArrayList<Agent>();
		for (int i = 0; i < agents_.size(); i++)
			agents.add(agents_.get(i));
		for (int i = 0; i < numTrials; i++) {
			//Rotate Agent Order
			for (int j = 0; j < agentMoveOrder.length; j++)
				agentMoveOrder[j] = (j + moveOrderDisplacement) % agents_.size();
	        runSimulation(simulatedWorld, agents, agentMoveOrder);
	        agents.add(0, agents.remove(agents.size() - 1));
	        moveOrderDisplacement += 1;
		}
	}
	
	public Simulator<S, A> getSimulator() {
		return world_;
	}
	
	public List<Agent> getAgents() {
		return agents_;
	}
	
	public History<S, A> getHistory() {
		return history_;
	}
	
	public double[] getRewardsData(int agentId) {
		double[] rewardsData = new double[totalRewardsData_.size()];
		for (int i = 0; i < totalRewardsData_.size(); i++)
			rewardsData[i] = totalRewardsData_.get(i)[agentId];
		return rewardsData;
	}
	
	public double[] getAvgMoveTimeData(int agentId) {
		double[] avgMoveTimeData = new double[totalRewardsData_.size()];
		for (int i = 0; i < totalRewardsData_.size(); i++) {
			avgMoveTimeData[i] = totalMoveTimeData_.get(i)[agentId] / (double) actionCountsData_.get(i)[agentId];
		}
		return avgMoveTimeData;
	}
	
	@Override
	public String toString() {
		String output = "";
		output += world_.toString() + "\n";
		for (int i = 0; i < agents_.size(); i++)
			output += agents_.get(i).toString() + "\n";
		return output;
	}
}
