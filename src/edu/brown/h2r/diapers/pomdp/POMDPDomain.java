package edu.brown.h2r.diapers.pomdp;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.GroundedAction;

public class POMDPDomain extends SADomain {

	protected List<Observation> observations;
	protected Map<String, Observation> observationMap;

	public POMDPDomain() {
		super();

		observations = new ArrayList<Observation>();
		observationMap = new HashMap<String, Observation>();
	}

	public POMDPState sampleInitialState() {
		return new POMDPState();
	}
	
	public List<POMDPState> getAllInitialStates(){
		List<POMDPState> tempList = new ArrayList<POMDPState>();
		tempList.add(sampleInitialState());
		return tempList;
	}

	public boolean isSuccess(Observation o) {
		return false;
	}

	public boolean isTerminal(POMDPState s) {
		return false;
	}

	public Observation makeObservationFor(GroundedAction a, POMDPState s) {
		return new Observation(this, "");
	}

	public List<State> getAllStates() {
		return new ArrayList<State>();
	}

	public List<Observation> getObservations() {
		return new ArrayList<Observation>(observations);
	}

	public Observation getObservation(String name) {
		return observationMap.get(name);
	}

	public void addObservation(Observation observation) {
		if(!observationMap.containsKey(observation.getName())) {
			observations.add(observation);
			observationMap.put(observation.getName(), observation);
		}
	}
	
	public void visualizeState(State s){
		
	}

	@Override
	protected Domain newInstance() {
		return new POMDPDomain();
	}
}
