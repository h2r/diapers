package edu.brown.h2r.diapers.athena;

import burlap.oomdp.core.Domain;

public class AgentDemo {
	public static void main(String... args) {

		System.out.println("AgentDemo running!");

		DiaperDomain dd = new DiaperDomain();
		Domain dom = dd.generateDomain();

		DiaperEnvironment env = new DiaperEnvironment(DiaperDomain.getNewState(dom));
		env.setObservationStyle(DiaperEnvironment.ObservationStyle.DETERMINISTIC);
		Agent agent = new AthenaAgent(env);

		agent.run();
	}
}