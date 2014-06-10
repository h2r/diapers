package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.tiger.TigerDomain;
import edu.brown.h2r.diapers.tiger.TigerEnvironment;

import edu.brown.h2r.diapers.testdomain.GoalsDomain;
import edu.brown.h2r.diapers.testdomain.GoalsEnvironment;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;

import burlap.oomdp.core.Domain;

public class AgentDemo {
	public static void main(String... args) {

		System.out.println("AgentDemo running!");

		POMDPDomain goalsDomain = (POMDPDomain) new GoalsDomain().generateDomain();
		GoalsEnvironment env = new GoalsEnvironment(goalsDomain.getExampleState());

		Agent agent = new POMCPAgent(env);
		env.addAgent(agent);

		/*
		Domain tigerDomain = new TigerDomain().generateDomain();

		TigerEnvironment env = new TigerEnvironment(TigerDomain.getNewState(tigerDomain));
		Agent agent = new POMCPAgent(env);
		env.addAgent(agent);
		*/

		/*
		DiaperDomain dd = new DiaperDomain();
		Domain dom = dd.generateDomain();

		DiaperEnvironment env = new DiaperEnvironment(DiaperDomain.getNewState(dom));
		env.setObservationStyle(DiaperEnvironment.ObservationStyle.DETERMINISTIC);
		Agent agent = new AthenaAgent(env); */

		agent.run();
	}
}
