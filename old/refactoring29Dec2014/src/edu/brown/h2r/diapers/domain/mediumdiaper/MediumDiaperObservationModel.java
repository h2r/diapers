package edu.brown.h2r.diapers.domain.mediumdiaper;

import edu.brown.h2r.diapers.pomdp.ObservationModel;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;

import burlap.oomdp.singleagent.GroundedAction;

import java.util.Set;
import java.util.HashSet;

public class MediumDiaperObservationModel extends ObservationModel {
	public MediumDiaperObservationModel(POMDPDomain d) {
		super(d);
	}

	public Set<Observation> getAllObservations() {
		Set<Observation> obsSet = new HashSet<Observation>();

		obsSet.add(new Observation(domain, "null"));
		obsSet.add(new Observation(domain, "done"));
		obsSet.add(new Observation(domain, Names.OBJ_OLD_CLOTHES + ":" + Names.OBJ_HAMPER));
		obsSet.add(new Observation(domain, Names.OBJ_OINTMENT + ":" + Names.OBJ_CHANGING_TABLE));
		obsSet.add(new Observation(domain, Names.OBJ_NEW_CLOTHES + ":" + Names.OBJ_CHANGING_TABLE));

		return obsSet;
	}

	public double omega(Observation o, POMDPState s, GroundedAction a) {
		if(a.action.getName().equals(Names.ACTION_ASK)) {
			return o.getName().equals(s.getObject(Names.OBJ_CAREGIVER).getStringValForAttribute(Names.ATTR_MENTAL_STATE)) ? 1 : 0;
		} else {
			return o.getName().equals("null") ? 1 : 0;
		}
	}

	public Set<Observation> getPossibleObservationsFor(POMDPState s, GroundedAction a) {
		Set<Observation> obsSet = new HashSet<Observation>();
		obsSet.add(makeObservationFor(s, a));
		return obsSet;
	}

	public Observation makeObservationFor(POMDPState s, GroundedAction a) {
		if(a.action.getName().equals(Names.ACTION_ASK)) {
			return new Observation(domain, s.getObject(Names.OBJ_CAREGIVER).getStringValForAttribute(Names.ATTR_MENTAL_STATE));
		} else {
			return new Observation(domain, "null");
		}
	}

	public boolean isSuccess(Observation o) {
		return o.getName().equals("done");
	}
}