package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.Observation;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.core.State;

public interface Environment {
	public Observation observe();
	public void perform(Action a, String[] params);
	public State getCurrentState();
}