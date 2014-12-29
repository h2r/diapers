package edu.brown.h2r.diapers.testing;

import edu.brown.h2r.diapers.domain.rocksample.RockSampleDomain;
import edu.brown.h2r.diapers.domain.rocksample.RockSampleDomainStateParser;
import edu.brown.h2r.diapers.domain.rocksample.RockSampleRewardFunction;
import edu.brown.h2r.diapers.domain.tiger.TigerDomain;
import edu.brown.h2r.diapers.domain.tiger.TigerRewardFunction;
import edu.brown.h2r.diapers.domain.easydiaper.RashDomain;
import edu.brown.h2r.diapers.domain.easydiaper.RashDomainRewardFunction;
import edu.brown.h2r.diapers.domain.easydiapervocab.RashDomainVocab;
import edu.brown.h2r.diapers.domain.easydiapervocab.RashDomainVocabObsModel;
import edu.brown.h2r.diapers.domain.easydiapervocab.RashDomainVocabRewardFunction;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerDomain;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerRewardFunction;
import edu.brown.h2r.diapers.domain.infinitiger.InfinitigerStateParser;
import edu.brown.h2r.diapers.domain.mediumdiaper.MediumDiaperDomain;
import edu.brown.h2r.diapers.domain.mediumdiaper.MediumDiaperObservationModel;
import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.solver.lwpomcp.LBLWPOMCPSolver;
import edu.brown.h2r.diapers.solver.lwpomcp.LWPOMCPSolver;
import edu.brown.h2r.diapers.solver.pomcp.POMCPSolver;
import edu.brown.h2r.diapers.solver.uct.UCTSolver;
import edu.brown.h2r.diapers.pomdp.ObservationModel;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.solver.pbvi.PointBasedValueIteration;

import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.auxiliary.StateParser;
import burlap.debugtools.RandomFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class Demo {

	private static Solver solver;
	private static Map<String, Double> params;
	private static POMDPDomain domain;
	private static Environment environment;
	private static RewardFunction reward = new UniformCostRF();
	private static boolean user = false;
	private static StateParser sparse;
	private static int obsNum = 1;
	private static ObservationModel obsModel =null;
	

	public static void main(String[] args) {
		RandomFactory.seedMapped(0, 10000);
		Random rand = RandomFactory.getMapped(0);
	System.out.println("Demo running... parsing input...... new domain");
	for(int totalCount = 0;totalCount<1;totalCount++){
		System.out.println("run number: "+totalCount);
		for(String arg : args) {
			if(arg.startsWith("D")) {
				switch(arg.split("=")[1]) {
					case "tiger":
						domain = (POMDPDomain) new TigerDomain().generateDomain();
						reward = new TigerRewardFunction();
						break;
					case "infinitiger":
						domain = (POMDPDomain) new InfinitigerDomain(2, 1).generateDomain();
						reward = new InfinitigerRewardFunction();
						sparse = new InfinitigerStateParser();
						break;
					case "easydiaper":
						domain = (POMDPDomain) new RashDomain(10).generateDomain();
						reward = new RashDomainRewardFunction();
						break;
					case "easydiapervocab":
						domain = (POMDPDomain) new RashDomainVocab(1).generateDomain();
						reward = new RashDomainVocabRewardFunction();
						obsModel = new RashDomainVocabObsModel(domain);
						break;
					case "mediumdiaper":
						domain = (POMDPDomain) new MediumDiaperDomain().generateDomain();
						reward = new UniformCostRF();
						obsModel = new MediumDiaperObservationModel(domain);
						break;
					case "rocksample":
						domain = (POMDPDomain) new RockSampleDomain().generateDomain();
						reward = new RockSampleRewardFunction();
						sparse = new RockSampleDomainStateParser();
						break;
				}
			}else if(arg.startsWith("O")) {
								obsNum = Integer.parseInt(arg.split("=")[1]);
								System.out.println("More observations! " + obsNum);
							} 
			else if(arg.startsWith("S")) {
				switch(arg.split("=")[1]) {
					case "pomcp":
						solver = new POMCPSolver();
						break;
					case "pbvi":
						solver = new PointBasedValueIteration();
						break;
					case "uct":
						solver = new UCTSolver();
						break;
					case "lwpomcp":
						solver = new LWPOMCPSolver();
						break;
					case "lblwpomcp":
						solver = new LBLWPOMCPSolver();
						break;
						
				}
			} else if(arg.startsWith("P")) {
				params = parseFile(arg.split("=")[1]);
			} else if(arg.equals("U")) {
				user = true;
			}
		}
		
		if(obsModel != null){
				domain.setObservationModel(obsModel.withMultiplicity(obsNum));
				}

		if(solver != null && domain != null) {
			long startTime = System.currentTimeMillis();
			System.out.println("Parse successful, running");
			if(params != null) {
				solver.init(domain, reward, params);
			} else {
				solver.init(domain, reward);
			}
			if(user) {
				solver.userMode(sparse);
			}
			solver.run();
			long totalTime = System.currentTimeMillis() - startTime;
			System.out.println("total time: " + totalTime);
		} else {
			System.out.println("Unable to create solver and/or domain, check your arguments...");
		
		}
		domain = null;
		solver=null;
		sparse=null;
		obsModel =null;
		obsNum = 1;
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
