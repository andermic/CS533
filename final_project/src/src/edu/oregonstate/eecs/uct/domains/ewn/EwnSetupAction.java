package edu.oregonstate.eecs.uct.domains.ewn;

public class EwnSetupAction extends EwnAction {
	private static final int NUMBER_OF_PIECES = 6;
	private int[] values_;
	
	public EwnSetupAction(int[] values) {
		boolean[] used = new boolean[NUMBER_OF_PIECES];
		values_ = new int[NUMBER_OF_PIECES];
		for (int i = 0; i < values_.length; i++) {
			values_[i] = values[i];
			if (used[values[i] - 1])
				throw new IllegalArgumentException("Illegal Setup Action: " + values);
			used[values[i] - 1] = true;
		}
	}
	
	public int getValue(int index) {
		return values_[index];
	}
	
	@Override
    public int hashCode() {
		int code = 7;
		for (int value: values_)
			code = 11 * code + value;
    	return code;
    }
    
    @Override
    public boolean equals(Object object) {
    	if (!(object instanceof EwnSetupAction))
    		return false;
    	EwnSetupAction action = (EwnSetupAction) object;
    	for (int i = 0; i < values_.length; i++)
    		if (values_[i] != action.getValue(i))
    			return false;
    	return true;
    }
    
    @Override
    public String toString() {
    	StringBuilder output = new StringBuilder();
    	output.append("[ ");
    	for (int i = 0; i < NUMBER_OF_PIECES; i++)
    		output.append(values_[i] + " ");
    	output.append("]");
    	return output.toString();
    }
}
