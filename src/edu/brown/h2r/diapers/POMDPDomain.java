package edu.brown.h2r.diapers;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class POMDPDomain extends SADomain {
	protected List<Observation> observations;
	protected Map<String, Observation> observationMap;

	public POMDPDomain() {
		super();

		observations = new ArrayList<Observation>();
		observationMap = new HashMap<String, Observation>();
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

	@Override
	protected Domain newInstance() {
		return new POMDPDomain();
	}
}