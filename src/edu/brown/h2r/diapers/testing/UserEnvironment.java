package edu.brown.h2r.diapers.testing;

import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.GroundedAction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.util.ANSIColor;

public class UserEnvironment extends Environment {
	private StateParser sparse;

	public UserEnvironment(POMDPDomain d, RewardFunction r, StateParser s) {
		super(d, r);
		sparse = s;
	}

	public Observation observe(GroundedAction a) {
		if(sparse == null) return domain.makeObservationFor(a, currentState);

		String observation = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		ANSIColor.red("[UE] ");
		System.out.print("The current state is ");
		ANSIColor.red(sparse.stateToString(currentState));
		System.out.print(" Give an observation: ");

		try {
			observation = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Observation chosen = (observation.equals("") ? domain.makeObservationFor(a, currentState) : domain.getObservation(observation));

		return chosen;
	}
}
