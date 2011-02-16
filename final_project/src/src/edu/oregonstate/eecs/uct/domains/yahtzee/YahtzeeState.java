package edu.oregonstate.eecs.uct.domains.yahtzee;

import edu.oregonstate.eecs.uct.State;

public class YahtzeeState extends State {
	private static final int NUMBER_OF_DICE = 5;
	private static final int NUMBER_OF_VALUES = 6;
	private static final int NUMBER_OF_SCORES = ScoreCategory.values().length;
	private int[] diceValues_;
	private int rolls_;
	private int[] scores_;
	
	public enum ScoreCategory {
		ONES, TWOS, THREES, FOURS, FIVES, SIXES,
		THREE_OF_KIND, FOUR_OF_KIND, FULL_HOUSE,
		SMALL_STRAIGHT, LARGE_STRAIGHT, YAHTZEE, CHANCE
	}
	
	public YahtzeeState(int[] diceValues, int rolls, int[] scores) {
		diceValues_ = diceValues;
		rolls_ = rolls;
		scores_ = scores;
	}
	
	public static int getNumberOfDice() {
		return NUMBER_OF_DICE;
	}
	
	public static int getNumberOfValues() {
		return NUMBER_OF_VALUES;
	}
	
	public static int getNumberOfScores() {
		return NUMBER_OF_SCORES;
	}
	
	public int getDiceValue(int index) {
		return diceValues_[index];
	}
	
	public int[] getDiceValues() {
		int[] diceValues = new int[NUMBER_OF_VALUES];
		for (int i = 0; i < NUMBER_OF_VALUES; i++)
			diceValues[i] = diceValues_[i];
		return diceValues;
	}
	
	public int getRolls() {
		return rolls_;
	}
	
	public int[] getScores() {
		int[] scores = new int[NUMBER_OF_SCORES];
		for (int i = 0; i < NUMBER_OF_SCORES; i++)
			scores[i] = scores_[i];
		return scores;
	}
	
	public int getScore(int index) {
		return scores_[index];
	}
	
	@Override
	public int hashCode() {
		int code = 7 + rolls_;
		for (int i = 0; i < NUMBER_OF_VALUES; i++)
			code = 11 * code + diceValues_[i];
		for (int i = 0; i < NUMBER_OF_SCORES; i++)
			code = 11 * code + scores_[i];
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof YahtzeeState))
			return false;
		YahtzeeState state = (YahtzeeState) object;
		for (int i = 0; i < NUMBER_OF_VALUES; i++)
			if (diceValues_[i] != state.getDiceValue(i))
				return false;
		if (rolls_ != state.getRolls())
			return false;
		for (int i = 0; i < NUMBER_OF_SCORES; i++)
			if (scores_[i] != state.getScore(i))
				return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		ScoreCategory[] scoreCategories = ScoreCategory.values();
		for (int i = 0; i < NUMBER_OF_SCORES; i++)
			if (scores_[i] != -1)
				output.append(scoreCategories[i].name() + ": " + scores_[i] + "\n");
		output.append("Rolls: " + rolls_ + "\n");
		output.append("Dice: [ ");
		for (int i = 0; i < diceValues_.length; i++) {
			output.append(diceValues_[i] + " ");
		}
		output.append("]\n");
		for (int i = 0; i < NUMBER_OF_SCORES; i++)
			if (scores_[i] == -1)
				output.append(scoreCategories[i].name() + " ");
		output.append("\n");
		return output.toString();
	}
}
