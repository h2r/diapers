package edu.brown.h2r.diapers;

/**
 * Observation is a class representing a single nuclear observation which could be
 * observed in a POMDP setting.  Each observation must be able to return a probability
 * of its occurrence based upon a State and GroundedAction.
 */ 
public class Observation {

	protected Domain domain;
	protected String name;

	public Observation(Domain domain, String name) {
		this.name = name;
		this.domain = domain;
		this.domain.addObservation(this);
	}

	/**
	 * Returns the probability of observing this observation in state s after having taken
	 * action a.  Should be overridden in implementation.
	 *
	 * @param s 	The state in which we arrived
	 * @param a 	The action we took to get there
	 * @return 		The probability of observing this observation
	 */
	public double getProbability(State s, GroundedAction a) {
		return 0;
	}

	public String getName() {
		return name;
	}

	public Domain getDomain() {
		return domain;
	}

	@Override
	public boolean equals(Object obj) {
		Observation obs = (Observation) obj;
		return obs.getName().equals(name);
	}

	@Override 
	public int hashCode() {
		return name.hashCode();
	}

}