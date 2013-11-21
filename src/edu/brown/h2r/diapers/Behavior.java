package edu.brown.h2r.diapers;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.SinglePFTF;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.TFGoalCondition;
import burlap.behavior.singleagent.planning.deterministic.SDPlannerPolicy;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.statehashing.DiscreteStateHashFactory;

/**
 * Behavior is a class in which one can demo various types of planning behavior for the
 * Diaper changing domain.
 *
 * @author Izaak Baker (iebaker)
 */
public class Behavior {

	DiaperDomain diaperDomain;
	Domain domain;
	StateParser stateParser;
	RewardFunction rewardFunction;
	TerminalFunction terminalFunction;
	StateConditionTest goalCondition;
	State initialState;
	DiscreteStateHashFactory hashFactory;

	/**
	 * Constructor.  Doesn't take arguments.  Sets up all aspects of the behavior class.
	 */
	public Behavior() {
		diaperDomain = new DiaperDomain();
		domain = diaperDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		rewardFunction = new UniformCostRF();
		terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_Y));
		goalCondition = new TFGoalCondition(terminalFunction);
		initialState = DiaperDomain.getNewState(domain);
		hashFactory = new DiscreteStateHashFactory();
	}

	/**
	 * Runs Breadth First Search on the diaper domain.  Prints results to a specified output file. 
	 *
	 * @param outputPath		A path to a directory where the output file should be placed
	 */
	public void doBFS(String outputPath) {
		outputPath += !outputPath.endsWith("/") ? "/" : "";

		DeterministicPlanner planner = new BFS(domain, goalCondition, hashFactory);
		planner.planFromState(initialState);

		Policy p = new SDPlannerPolicy(planner);
		p.evaluateBehavior(initialState, rewardFunction, terminalFunction).writeToFile(outputPath + "planResult", stateParser);
	}

	/**
	 * Runs an example search algorithm
	 */
	public static void main(String[] args) {
		Behavior test = new Behavior();
		String outputPath = "output/";

		test.doBFS(outputPath);
	}
}