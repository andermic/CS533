package edu.oregonstate.eecs.uct.domains.yahtzee;

import java.util.ArrayList;

import edu.oregonstate.eecs.uct.Simulator;

public class YahtzeeSimulator extends Simulator<YahtzeeState, YahtzeeAction> {
    public static final int NUMBER_OF_AGENTS = 1;

    public YahtzeeSimulator() {
        setInitialState();
    }
    
    public YahtzeeSimulator(YahtzeeState state) {
        state_ = state;
        computeLegalActions();
    }
    
    @Override
    public Simulator<YahtzeeState, YahtzeeAction> copy() {
        return new YahtzeeSimulator(state_);
    }

    @Override
    public void setInitialState() {
        int[] diceValues = new int[YahtzeeState.getNumberOfValues()];
        for (int i = 0; i < YahtzeeState.getNumberOfDice(); i++)
            diceValues[(int) (Math.random() * YahtzeeState.getNumberOfValues())] += 1;
        int[] scores = new int[YahtzeeState.getNumberOfScores()];
        for (int i = 0; i < YahtzeeState.getNumberOfScores(); i++)
            scores[i] = -1;
        state_ = new YahtzeeState(diceValues, 1, scores);
        computeLegalActions();
    }
    
    @Override
    protected void computeLegalActions() {
        legalActions_ = new ArrayList<YahtzeeAction>();
        if (areCategoriesLeft()) {
            if (state_.getRolls() < 3) {
                int[] diceValues = state_.getDiceValues();
                for (int i = 0; i <= diceValues[0]; i++)
                    for (int j = 0; j <= diceValues[1]; j++)
                        for (int k = 0; k <= diceValues[2]; k++)
                            for (int l = 0; l <= diceValues[3]; l++) 
                                for (int m = 0; m <= diceValues[4]; m++)
                                    for (int n = 0; n <= diceValues[5]; n++)
                                        legalActions_.add(new YahtzeeRollAction(new int[] {i, j, k, l, m, n}));
            } else {
                int[] scores = state_.getScores();
                int yahtzee = checkYahtzee(state_.getDiceValues());
                if (yahtzee == -1 || scores[yahtzee] != -1) {
                    for (int i = 0; i < scores.length; i++)
                        if (scores[i] == -1)
                            legalActions_.add(new YahtzeeSelectAction(i));
                } else {
                    legalActions_.add(new YahtzeeSelectAction(yahtzee));
                    if (scores[YahtzeeState.ScoreCategory.YAHTZEE.ordinal()] == -1)
                        legalActions_.add(new YahtzeeSelectAction(YahtzeeState.ScoreCategory.YAHTZEE.ordinal()));
                }
            }
        }
    }
    
    private boolean areCategoriesLeft() {
        for (int i = 0; i < YahtzeeState.getNumberOfScores(); i++)
            if (state_.getScore(i) == -1)
                return true;
        return false;
    }
    
    private int checkYahtzee(int[] diceValues) {
        for (int i = 0; i < diceValues.length; i++)
            if (diceValues[i] == YahtzeeState.getNumberOfDice())
                return i;
        return -1;
    }

    @Override
    public int[] getRewards() {
        int reward = 0;
        if (legalActions_.size() == 0) {
            int[] scores = state_.getScores();
            for (int i = 0; i < 6; i++)
                reward += scores[i];
            if (reward >= 63)
                reward += 35;
            for (int i = 6; i < YahtzeeState.getNumberOfScores(); i++)
                reward += scores[i];
        }
        return new int[] {reward};
    }

    @Override
    public void takeAction(YahtzeeAction action) {
        if (!legalActions_.contains(action))
            throw new IllegalArgumentException("Action " + action + " not possible from current state.");
        
        int[] diceValues = state_.getDiceValues();
        int rolls = state_.getRolls();
        int[] scores = state_.getScores();
        int yahtzee = checkYahtzee(diceValues);
        if (yahtzee != -1 && scores[YahtzeeState.ScoreCategory.YAHTZEE.ordinal()] >= 50)
            scores[YahtzeeState.ScoreCategory.YAHTZEE.ordinal()] += 100;
        
        if (action instanceof YahtzeeRollAction) {
            YahtzeeRollAction rollAction = (YahtzeeRollAction) action;
            diceValues = rollAction.getSelected();
            int numSelected = 0;
            for (int i = 0; i < diceValues.length; i++)
                numSelected += diceValues[i];
            for (int i = numSelected; i < YahtzeeState.getNumberOfDice(); i++)
                diceValues[(int) (Math.random() * YahtzeeState.getNumberOfValues())] += 1;
            rolls++;
        } else {
            YahtzeeSelectAction selectAction = (YahtzeeSelectAction) action;
            int category = selectAction.getScoreCategory();
            scores[category] = 0;
            //compute score
            switch(category) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                scores[category] = diceValues[category] * (category + 1);
                break;
            case 6:
                boolean found = false;
                for (int i = 0; i < diceValues.length; i++)
                    if (diceValues[i] >= 3)
                        found = true;
                if (found)
                    for (int i = 0; i < diceValues.length; i++)
                        scores[category] += diceValues[i] * (i + 1);
                break;
            case 7:
                found = false;
                for (int i = 0; i < diceValues.length; i++)
                    if (diceValues[i] >= 4)
                        found = true;
                if (found)
                    for (int i = 0; i < diceValues.length; i++)
                        scores[category] += diceValues[i] * (i + 1);
                break;
            case 8:
                boolean two = false;
                boolean three = false;
                for (int i = 0; i < diceValues.length; i++) {
                    if (diceValues[i] == 2)
                        two = true;
                    else if (diceValues[i] == 3)
                        three = true;
                }
                if (two && three)
                    scores[category] = 25;
                break;
            case 9:
                int count = 0;
                boolean seque=false;
                for (int i = 0; i < diceValues.length; i++) {
                    if (diceValues[i] > 0) {
                        if(!seque)
                            seque=true;
                        count++;
                    }
                    else {
                        seque=false;
                        count=0;
                    }
                }
                if (count >= 4)
                    scores[category] = 30;
                break;
            case 10:
                 count = 0;
                 for (int i = 1; i<=4; i++) {
                     if (diceValues[i] ==1)
                         count++;
                 }
                 if(count!=4)
                     break;
                 else if(diceValues[0]==1 || diceValues[5]==1)
                     scores[category]=40;
                 break;
            case 11:
                for (int i = 0; i < diceValues.length; i++)
                    if (diceValues[i] == 5)
                        scores[category] = 50;
                break;
            case 12:
                for (int i = 0; i < diceValues.length; i++)
                    scores[category] += diceValues[i] * (i + 1);
                break;
            }
            diceValues = new int[YahtzeeState.getNumberOfValues()];
            for (int i = 0; i < YahtzeeState.getNumberOfDice(); i++)
                diceValues[(int) (Math.random() * YahtzeeState.getNumberOfValues())] += 1;
            rolls = 1;
        }
        state_ = new YahtzeeState(diceValues, rolls, scores);
        computeLegalActions();
    }
    
    @Override
    public int getNumberOfAgents() {
        return NUMBER_OF_AGENTS;
    }

    @Override
    public double[] getFeatureVector(YahtzeeAction action) {
        // TODO Auto-generated method stub
        return null;
    }
}
