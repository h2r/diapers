package edu.brown.h2r.diapers.testing;

import edu.brown.h2r.diapers.domain.tiger.TigerDomain;
import edu.brown.h2r.diapers.domain.tiger.TigerRewardFunction;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerDomain;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerRewardFunction;

import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.solver.pomcp.POMCPSolver;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;

import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.RewardFunction;

public class Demo {

	private static Solver solver;
	private static POMDPDomain domain;
	private static Environment environment;
	private static RewardFunction reward = new UniformCostRF();

	public static void main(String[] args) {
		for(String arg : args) {
			if(arg.startsWith("D")) {
				switch(arg.split("=")[1]) {
					case "tiger":
						domain = (POMDPDomain) new TigerDomain().generateDomain();
						reward = new TigerRewardFunction();
						break;
					case "infinitiger":
						domain = (POMDPDomain) new InfinitigerDomain(10, 1).generateDomain();
						reward = new InfinitigerRewardFunction();
						break;
				}
			} else if(arg.startsWith("S")) {
				switch(arg.split("=")[1]) {
					case "pomcp":
						solver = new POMCPSolver();
						break;
				}
			}
		}

		if(solver != null && domain != null) {
			solver.init(domain, reward);
			solver.run();
		}	
	}
}
