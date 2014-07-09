package edu.brown.h2r.diapers.testing;

import edu.brown.h2r.diapers.domain.tiger.TigerDomain;
import edu.brown.h2r.diapers.domain.tiger.TigerRewardFunction;
import edu.brown.h2r.diapers.domain.easydiaper.RashDomain;
import edu.brown.h2r.diapers.domain.easydiaper.RashDomainRewardFunction;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerDomain;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerRewardFunction;

import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.solver.pomcp.POMCPSolver;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.solver.pbvi.PointBasedValueIteration;

import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.RewardFunction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

public class Demo {

	private static Solver solver;
	private static Map<String, Double> params;
	private static POMDPDomain domain;
	private static Environment environment;
	private static RewardFunction reward = new UniformCostRF();

	public static void main(String[] args) {
	System.out.println("Demo running... parsing input...");
		for(String arg : args) {
			if(arg.startsWith("D")) {
				switch(arg.split("=")[1]) {
					case "tiger":
						domain = (POMDPDomain) new TigerDomain().generateDomain();
						reward = new TigerRewardFunction();
						break;
					case "infinitiger":
						domain = (POMDPDomain) new InfinitigerDomain(4, 1).generateDomain();
						reward = new InfinitigerRewardFunction();
						break;
					case "easydiaper":
						domain = (POMDPDomain) new RashDomain().generateDomain();
						reward = new RashDomainRewardFunction();
						break;
				}
			} else if(arg.startsWith("S")) {
				switch(arg.split("=")[1]) {
					case "pomcp":
						solver = new POMCPSolver();
						break;
					case "pbvi":
						solver = new PointBasedValueIteration();
						break;
						
				}
			} else if(arg.startsWith("P")) {
				params = parseFile(arg.split("=")[1]);
			}
		}

		if(solver != null && domain != null) {
			long startTime = System.currentTimeMillis();
			System.out.println("Parse successful, running");
			if(params != null) {
				solver.init(domain, reward, params);
			} else {
				solver.init(domain, reward);
			}
			solver.run();
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("total time: " + totalTime);
		} else {
			System.out.println("Unable to create solver and/or domain, check your arguments...");
		}
	}

	public static Map<String, Double> parseFile(String filename) {
		Map<String, Double> retval = new HashMap<String, Double>();
		String line = "";


		try(BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			while((line = reader.readLine()) != null) {
				String[] pieces = line.split("=");
				retval.put(pieces[0], Double.parseDouble(pieces[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return retval;

	}
}
