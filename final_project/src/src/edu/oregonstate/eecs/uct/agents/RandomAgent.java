package edu.oregonstate.eecs.uct.agents;

import java.util.List;

import edu.oregonstate.eecs.uct.Action;
import edu.oregonstate.eecs.uct.Agent;
import edu.oregonstate.eecs.uct.Simulator;
import edu.oregonstate.eecs.uct.State;

public class RandomAgent extends Agent {
    public RandomAgent() {
        name_ = "Random";
    }

    @Override
    public <S extends State, A extends Action> A selectAction(S state, Simulator<S, A> simulator) {
    	simulator.setState(state);
    	List<A> actions = simulator.getLegalActions();
    	return actions.get((int) (Math.random() * actions.size()));
    }
}
