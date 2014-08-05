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

	protected ObservationModel obsModel;

	public POMDPDomain() {
		super();
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
		return obsModel.isSuccess(o);
	}

	public boolean isTerminal(POMDPState s) {
		return false;
	}

	public Observation makeObservationFor(GroundedAction a, POMDPState s) {
		return obsModel.makeObservationFor(s, a);
	}

	public ObservationModel getObservationModel() {
		return obsModel;
	}

	public void setObservationModel(ObservationModel om) {
		obsModel = om;
	}

	public List<State> getAllStates() {
		return new ArrayList<State>();
	}

	public List<Observation> getObservations() {
		return new ArrayList<Observation>(obsModel.getAllObservations());
	}

	public Observation getObservation(String name) {
		return obsModel.getObservationByName(name);
	}

	public void addObservation(Observation observation) {
		return;
	}

	@Override
	protected Domain newInstance() {
		return new POMDPDomain();
	}
}
