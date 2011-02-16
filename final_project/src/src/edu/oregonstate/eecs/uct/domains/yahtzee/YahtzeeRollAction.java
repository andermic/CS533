package edu.oregonstate.eecs.uct.domains.yahtzee;

public class YahtzeeRollAction extends YahtzeeAction {
	private int[] selected_;
	
	public YahtzeeRollAction(int[] selected) {
		selected_ = new int[YahtzeeState.getNumberOfValues()];
		for (int i = 0; i < YahtzeeState.getNumberOfValues(); i++)
			selected_[i] = selected[i];
	}
	
	public int[] getSelected() {
		int[] selected = new int[selected_.length];
		for (int i = 0; i < selected_.length; i++)
			selected[i] = selected_[i];
		return selected;
	}
	
	public int getSelected(int index) {
		return selected_[index];
	}
	
	@Override
	public int hashCode() {
		int code = 7;
		for (int i = 0; i < selected_.length; i++)
			code = 11 * code + selected_[i];
		return code;
	}
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof YahtzeeRollAction))
			return false;
		YahtzeeRollAction action = (YahtzeeRollAction) object;
		for (int i = 0; i < selected_.length; i++)
			if (selected_[i] != action.getSelected(i))
				return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("[ ");
		for (int i = 0; i < selected_.length; i++)
			output.append(selected_[i] + " ");
		output.append("]");
		return output.toString();
	}
}
