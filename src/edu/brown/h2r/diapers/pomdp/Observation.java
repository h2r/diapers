package edu.brown.h2r.diapers.pomdp;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;

/**
 * Observation is a class representing a single nuclear observation which could be
 * observed in a POMDP setting.  Each observation must be able to return a probability
 * of its occurrence based upon a State and GroundedAction.
 */ 
public class Observation {

	protected POMDPDomain domain;
	protected String name;

	public Observation(POMDPDomain domain, String name) {
		this.name = name;
		this.domain = domain;
		this.domain.addObservation(this);
	}

	public Observation(Domain domain, String name) {
		this.name = name;
		this.domain = (POMDPDomain) domain;
		this.domain.addObservation(this);
	}

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
	
	public String toString(GroundedAction a) {
		String result =  "[observation NAME=" + this.name + " PROBS=[";
		for(int i = 0; i < this.domain.getAllStates().size(); ++i) {
			State current = this.domain.getAllStates().get(i);
			result += (i + ": " + Math.floor(this.getProbability(current, a) * 1000)/1000 + ", ");
		}
		result += "]]";
		return result;
	}
	
	public String truncate(double d, int k) {
		String retVal = "";
		for(int i = 0; i < k; ++i) {
			retVal += d % Math.pow(10, -i);
		}
		return retVal;
	}

}
