package edu.oregonstate.eecs.uct.agents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import edu.oregonstate.eecs.uct.Action;
import edu.oregonstate.eecs.uct.Agent;
import edu.oregonstate.eecs.uct.Simulator;
import edu.oregonstate.eecs.uct.State;

public class HumanAgent extends Agent {
    public HumanAgent() {
        name_ = "Human";
    }

	@Override
	public <S extends State, A extends Action> A selectAction(S state, Simulator<S, A> simulator) {
		simulator.setState(state);
		A action = null;
		List<A> legalActions = simulator.getLegalActions();
        if (legalActions.size() != 0) {
        	System.out.println(state);
        	System.out.print("Input Move (" + legalActions.size() + ") (");
            for (int i = 0; i < legalActions.size(); i++) {
                if (i == legalActions.size() - 1)
                    System.out.print(legalActions.get(i).toString() + ")\n");
                else
                    System.out.print(legalActions.get(i).toString() + ",");
            }
            do {
                String input = getInput();
                action = matchToAction(input, legalActions);
            } while (action == null);
        }
        return action;
	}

    private <A extends Action> A matchToAction(String input, List<A> actions) {
        for (int i = 0; i < actions.size(); i++) {
            if (actions.get(i).toString().toLowerCase().equals(input.toLowerCase()))
                return actions.get(i);
        }
        return null;
    }

    private String getInput() {
        String input = "";
		try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	        input = in.readLine();
	    } catch (IOException e) {}
        return input;
    }
}
