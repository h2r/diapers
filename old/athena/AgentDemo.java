package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.tiger.TigerDomain;
import edu.brown.h2r.diapers.tiger.TigerEnvironment;

import edu.brown.h2r.diapers.testdomain.GoalsDomain;
import edu.brown.h2r.diapers.testdomain.GoalsEnvironment;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;

import burlap.oomdp.core.Domain;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;

public class AgentDemo {
	public static void main(String... args) {

		System.out.println("AgentDemo running!");

		POMDPDomain goalsDomain = (POMDPDomain) new GoalsDomain().generateDomain();
		GoalsEnvironment env = new GoalsEnvironment(goalsDomain);
		Agent agent = new POMCPAgent(env);

		new AgentTester(agent, env, 100).runTests();
	}
}
