package edu.oregonstate.eecs.uct;

/**
 * An agent selects an action given the
 * current state of a simulator.  An agent
 * has a name to describe what type of agent
 * it is and a unique id.
 */
public abstract class Agent {
	/** Used to create new unique id_ values. */
    private static int nextId_ = 0;
    
    /** Uniquely identifies each Agent instance. */
    protected int id_;
    
    /** The name describes the type of agent. */
    protected String name_;
    
    public Agent() {
    	id_ = nextId_++;
    }

    /**
     * An agent takes a simulator at a current state
     * and selects an action to take.
     * @param state current state to take action from.
     * @param simulator simulator to use to determine action outcomes in domain.
     * @return an action to take from given state.
     */
	public abstract <S extends State, A extends Action> A selectAction(S state, Simulator<S, A> simulator);
	
	/**
	 * @return unique identifier for the agent.
	 */
	public int getId() {
		return id_;
	}
	
	/**
	 * @return name of the agent.
	 */
	public String getName() {
		return name_;
	}
    
	@Override
    public String toString() {
        return name_;
    }
}
