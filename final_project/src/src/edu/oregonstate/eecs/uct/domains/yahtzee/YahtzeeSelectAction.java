package edu.oregonstate.eecs.uct.domains.yahtzee;

public class YahtzeeSelectAction extends YahtzeeAction {
    private YahtzeeState.ScoreCategory scoreCategory_;
    
    public YahtzeeSelectAction(int scoreCategory) {
        scoreCategory_ = YahtzeeState.ScoreCategory.values()[scoreCategory];
    }
    
    public int getScoreCategory() {
        return scoreCategory_.ordinal();
    }
    
    @Override
    public int hashCode() {
        return 7 + scoreCategory_.ordinal();
    }
    
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof YahtzeeSelectAction))
            return false;
        YahtzeeSelectAction action = (YahtzeeSelectAction) object;
        return scoreCategory_.ordinal() == action.getScoreCategory();
    }
    
    @Override
    public String toString() {
        return scoreCategory_.toString();
    }
}
