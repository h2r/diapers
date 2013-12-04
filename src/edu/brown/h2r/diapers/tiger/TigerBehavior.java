package edu.brown.h2r.diapers.tiger;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.SinglPFTF;

import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;

import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.singleagent.planning.OOMDPPlanner;
import burlap.behavior.singleagent.planning.QComputablePlanner;
import burlap.behavior.singleagent.planning.commonpolicies.GreedyQPolicy;
import burlap.behavior.singleagent.planning.deterministic.TFGoalCondition;
import burlap.behavior.statehashing.NameDependentStateHashFactory;

public class TigerBehavior {
	private TigerDomain tigerDomain;
	private POMDPDomain domain;
	private StateParser stateParser;
	private RewardFunction rewardFunction;
	private TerminalFunction terminalFunction;
	private StateConditionTest goalCondition;
	private State initialState;
	private NameDependentStateHashFactory hashFactory;

	public TigerBehavior() {
		tigerDomain = new TigerDomain();
		domain = (POMDPDomain) tigerDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		terminalFunction = new SinglePFTF(domain.getPropFunction(P.PROP_DOOR_OPENED));
		goalCondition = new TFGoalCondition(terminalFunction);
		initialState = TigerDomain.getNewState(domain);
		hashFactory = new NameDependentStateHashFactory();

		//TODO: Reward function?
	}

	public void doValueIteration(String outputPath) {
		outputPath += !outputPath.endsWith("/") ? "/" : "";

		System.out.println("[TigerBehavior.doValueIteration] Running VI Planner...");

		System.out.println("<unimplemented>");
		//OOMDPPlanner planner = new PointBasedValueIteration(domain, rewardFunction, terminalFunction, 1, hashFactory, 0.001, 100);
		//planner.planFromState(initialState);

		System.out.println("[TigerBehavior.doValueIteration] Planner finished running.");

		// Policy p = /* some kind of policy run on our planner */

		//EpisodeAnalysis ea = p.evaluateBehavior(initialState, rewardFunction, terminalFunction);
		//ea.writeToFile(outputPath + "planResult", stateParser);
		System.out.println("[TigerBehavior.doValueIteration] PLAN GENERATED:");
		System.out.println(ea.getActionSequenceString("\n"));
	}

	public static void main(String[] args) {
		TigerBehavior test = new TigerBehavior();
		String outputPath = "output/";

		test.doValueIteration(outputPath);
	}
}