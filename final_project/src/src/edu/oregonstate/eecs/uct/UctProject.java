package edu.oregonstate.eecs.uct;

import edu.oregonstate.eecs.uct.agents.*;
import edu.oregonstate.eecs.uct.domains.backgammon.BackgammonSimulator;
import edu.oregonstate.eecs.uct.domains.biniax.*;
import edu.oregonstate.eecs.uct.domains.connect4.*;
import edu.oregonstate.eecs.uct.domains.ewn.EwnSimulator;
import edu.oregonstate.eecs.uct.domains.havannah.HavannahSimulator;
import edu.oregonstate.eecs.uct.domains.yahtzee.YahtzeeSimulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to run tests on between
 * agents and simulators.
 */
public class UctProject {
	/**
	 * If no arguments are provided then the
	 * program runs in interactive mode. Otherwise
	 * if a file path argument is provided that
	 * file will be read.
	 * @param args should be of length 0, 1 or 2
	 */
    public <S extends State, A extends Action> UctProject(String[] args) {
        Simulator<S, A> world;
        Simulator<S, A> simulatedWorld;
    	List<Agent> agents = new ArrayList<Agent>();
    	Arbiter<S, A> arbiter;
    	if (args.length == 0) { //Interactive Mode
	    	world = selectSimulator();
	    	agents = selectAgents(world);
	        arbiter = new Arbiter(world, agents);
	        arbiter.runSimulations(world.copy(), 1);
	        System.out.println(arbiter.getHistory());
	        System.out.println(arbiter);
    	} else if (args.length <= 2) { //Read flat text file
			List<String> lines = inputTestFile(args[0]);
			int numTrials = Integer.parseInt(lines.get(0));
			world = selectSimulator(lines.get(1).split(" "));
			simulatedWorld = selectSimulator(lines.get(2).split(" "));
			
			List<List<List<String>>> lists = new ArrayList<List<List<String>>>();
			for (int i = 0; i < world.getNumberOfAgents(); i++)
				lists.add(expandIntervals(lines.get(3 + i).split(" ")));
			
			List<int[]> values = new ArrayList<int[]>();
			List<String[]> agentArgs = new ArrayList<String[]>();
			int numIterations = 1;
			for (int i = 0; i < lists.size(); i++) {
				for (int j = 0; j < lists.get(i).size(); j++)
					numIterations *= lists.get(i).get(j).size();
				values.add(new int[lists.get(i).size()]);
				agentArgs.add(new String[lists.get(i).size()]);
			}
			
			for (int i = 0; i < numIterations; i++) {
				agents = new ArrayList<Agent>();
				for (int j = 0; j < world.getNumberOfAgents(); j++) {
					for (int k = 0; k < agentArgs.get(j).length; k++)
						agentArgs.get(j)[k] = lists.get(j).get(k).get(values.get(j)[k]);
					agents.add(selectAgent(agentArgs.get(j)));
				}
	    		arbiter = new Arbiter(world, agents);
	    		
	    		arbiter.runSimulations(simulatedWorld, numTrials);
		    	
		    	//Output format
		    	//avgRewards, stdRewards, avgMoveTime, stdMoveTime, agent, param1, param2, ... paramN
		    	DecimalFormat df = new DecimalFormat("#.###");
		    	String output = "";
		    	String temp;
		    	int buffer = 10;
		    	for (int j = 0; j < agents.size(); j++) {
		    		double[] rewardsData = arbiter.getRewardsData(j);
		    		double[] avgMoveTimeData = arbiter.getAvgMoveTimeData(j);
		    		temp = df.format(Utility.computeMean(rewardsData));
		    		for (int k = temp.length(); k < buffer; k++)
		    			output += " ";
		    		output += temp + ",";
		    		temp = df.format(Utility.computeStandardDeviation(rewardsData));
		    		for (int k = temp.length(); k < buffer; k++)
		    			output += " ";
		    		output += temp + ",";
		    		temp = df.format(Utility.computeMean(avgMoveTimeData));
		    		for (int k = temp.length(); k < buffer; k++)
		    			output += " ";
		    		output += temp + ",";
		    		temp = df.format(Utility.computeStandardDeviation(avgMoveTimeData));
		    		for (int k = temp.length(); k < buffer; k++)
		    			output += " ";
		    		output += temp + ",";
		    		for (int k = 0; k < agentArgs.get(j).length; k++) {
		    			for (int l = agentArgs.get(j)[k].length(); l < buffer; l++)
		    				output += " ";
		    			output += agentArgs.get(j)[k] + ",";
		    		}
		    		output = output.substring(0, output.length() -1) + "\n";
		    	}
		    	
		    	if (args.length == 2)
		    		recordResults(args[1], output);
		    	else
		    		recordResults(args[0] + "_results", output);
		    	
		    	//Update values
		    	for (int j = 0; j < values.size(); j++) {
		    		for (int k = 0; k < values.get(j).length; k++) {
		    			if (values.get(j)[k] == lists.get(j).get(k).size() - 1) {
		    				values.get(j)[k] = 0;
		    			} else {
		    				values.get(j)[k] += 1;
		    				break;
		    			}
		    		}
		    	}
			}
    	} else
    		throw new IllegalArgumentException("Illegal arguments\nusage: UctProject [test_filepath [results_filepath]]");
    }
    
    /**
     * Inputs a test file and removes all comments and empty lines.
     * @param filepath
     * @return
     */
    private List<String> inputTestFile(String filepath) {
    	List<String> lines = new ArrayList<String>();
    	try {
    		BufferedReader input = new BufferedReader(new FileReader(filepath));
			String line = input.readLine();
			while (line != null) {
				line = line.trim();
				if (line.length() > 0 && line.charAt(0) != '#')
					lines.add(line);
				line = input.readLine();
			}
			input.close();
    	} catch (IOException exception) {
    		throw new IllegalArgumentException("Filename " + filepath + " invalid.");
    	}
    	return lines;
    }
    
    private void recordResults(String filepath, String results) {
    	try {
    		BufferedWriter output = new BufferedWriter(new FileWriter(filepath, true));
    		output.write(results);
    		output.close();
    	} catch (IOException exception) {
    		throw new IllegalArgumentException("Could not write to " + filepath);
    	}
    }
    
    private List<List<String>> expandIntervals(String[] args) {
    	List<List<String>> list = new ArrayList<List<String>>();
    	for (int i = 0; i < args.length; i++) {
            ArrayList<String> temp = new ArrayList<String>();
    		if (args[i].charAt(0) == '[' && args[i].charAt(args[i].length() - 1) == ']') {
    			String[] elements = args[i].substring(1, args[i].length() - 1).split(",");
    			for (int j = 0; j < elements.length; j++) {
		    		String[] parts = elements[j].split(":");
		    		if (parts.length == 3) {
		    			int min = Integer.parseInt(parts[0]);
		    			int steps = Integer.parseInt(parts[1]);
		    			int max = Integer.parseInt(parts[2]);
		    			for (int k = 0; k <= steps; k++)
		    				temp.add(String.valueOf(min + k * ((max - min) / steps)));
		    		} else
                        temp.add(elements[j]);
    			}
    		} else
                temp.add(args[i]);
            list.add(temp);
    	}
    	return list;
    }
    
    private Simulator selectSimulator(String[] args) {
    	if (args[0].equalsIgnoreCase("Backgammon"))
    		return new BackgammonSimulator();
    	else if (args[0].equalsIgnoreCase("Biniax"))
    		return new BiniaxSimulator();
    	else if (args[0].equalsIgnoreCase("Connect4"))
        	return new Connect4Simulator2();
    	else if (args[0].equalsIgnoreCase("EWN"))
    		return new EwnSimulator();
    	else if (args[0].equalsIgnoreCase("Havannah"))
    		return new HavannahSimulator();
    	else if (args[0].equalsIgnoreCase("Yahtzee"))
    		return new YahtzeeSimulator();
    	else
    		throw new IllegalArgumentException("Invalid simulator name: " + args[0]);
    }
    
    /**
     * Choose a simulator for testing.
     */
    private Simulator selectSimulator() {
        while (true) {
        	System.out.println("-Select Simulator-");
        	System.out.println("1 Backgammon");
            System.out.println("2 Biniax");
            System.out.println("3 Connect 4");
            System.out.println("4 Einstein Wurfelt Nicht");
            System.out.println("5 Havannah");
            System.out.println("6 Yahtzee");
        	switch (getIntegerInput()) {
        	case 1:
        		return new BackgammonSimulator();
            case 2:
            	return new BiniaxSimulator();
            case 3:
            	return new Connect4Simulator2();
            case 4:
            	return new EwnSimulator();
            case 5:
            	return new HavannahSimulator();
            case 6:
            	return new YahtzeeSimulator();
            }
        }
    }
    
    private <S extends State, A extends Action> Agent selectAgent(String[] args) {
        if (args[0].equals("Random"))
        	return new RandomAgent();
        else if (args[0].equals("UCT")) {
        	return new UctAgent(Integer.parseInt(args[1]), Double.parseDouble(args[2]), 
        			Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
        } else if (args[0].equals("Expectimax"))
        	return new ExpectimaxAgent(Integer.parseInt(args[1]), 
        			Integer.parseInt(args[2]), Integer.parseInt(args[3]));
        throw new IllegalArgumentException("Invalid Agent Selection");
    }
    
    private List<Agent> selectAgents(Simulator simulator) {
    	int numAgents = simulator.getNumberOfAgents();
    	List<Agent> agents = new ArrayList<Agent>();
	    for (int i = 0; i < numAgents; i++) {
	    	System.out.println("-Select Agent-");
	        System.out.println("1 Human Agent");
	        System.out.println("2 Random Agent");
	        System.out.println("3 UCT Agent");
	        System.out.println("4 Expectimax Agent");
	        boolean valid = false;
	        while (!valid) {
	            switch (getIntegerInput()) {
	            case 1:
	            	agents.add(new HumanAgent());
	            	valid = true;
	            	break;
	            case 2:
	            	agents.add(new RandomAgent());
	            	valid = true;
	            	break;
	            case 3:
	            	System.out.print("Num Simulations: ");
	            	int numSimulations = getIntegerInput();
	            	System.out.print("UCTK: ");
	            	double uctK = getDoubleInput();
	            	System.out.print("Sample Size: ");
	            	int sampleSize = getIntegerInput();
	            	System.out.print("Ensemble Size: ");
	            	int ensembleTrials = getIntegerInput();
	            	agents.add(new UctAgent(numSimulations, uctK, sampleSize, ensembleTrials, "ROOT_ENSEMBLE"));
	            	valid = true;
	            	break;
	            case 4:
	            	System.out.print("Max Depth: ");
	            	int maxDepth = getIntegerInput();
	            	System.out.print("Sample Size: ");
	            	sampleSize = getIntegerInput();
	            	System.out.print("Num Simulations: ");
	            	numSimulations = getIntegerInput();
	            	agents.add(new ExpectimaxAgent(maxDepth, sampleSize, numSimulations));
	            	valid = true;
	            	break;
	            default:
	            	System.out.println("Invalid Selection");
	            	break;
	            }
	        }
    	}
        return agents;
    }
    
    private String getInput() {
        String input = "";
		try {
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	        input = in.readLine();
	    } catch (IOException e) {}
        return input;
    }
    
    private int getIntegerInput() {
	    while (true) {
			try {
		        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		        String input = in.readLine();
            	int selection = Integer.parseInt(input);
            	return selection;
		    } catch (IOException e) {
		    } catch (NumberFormatException e) {}
        }
    }
    
    private double getDoubleInput() {
    	while (true) {
			try {
		        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		        String input = in.readLine();
            	double selection = Double.parseDouble(input);
            	return selection;
		    } catch (IOException e) {
		    } catch (NumberFormatException e) {}
        }
    }
	
    public static void main(String[] args) {
        new UctProject(args);
	}
}
