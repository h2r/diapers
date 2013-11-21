package edu.brown.h2r.diapers;

//First import the universe
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.SinglePFTF;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.ObjectInstance;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.singleagent.planning.OOMDPPlanner;
import burlap.behavior.singleagent.planning.QComputablePlanner;
import burlap.behavior.singleagent.planning.commonpolicies.GreedyQPolicy;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.TFGoalCondition;
import burlap.behavior.singleagent.planning.deterministic.SDPlannerPolicy;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.singleagent.planning.deterministic.informed.Heuristic;
import burlap.behavior.statehashing.NameDependentStateHashFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * Behavior is a class in which one can demo various types of planning behavior for the
 * Diaper changing domain.
 *
 * @author Izaak Baker (iebaker)
 */
public class Behavior {

	private DiaperDomain diaperDomain;
	private Domain domain;
	private StateParser stateParser;
	private RewardFunction rewardFunction;
	private TerminalFunction terminalFunction;
	private StateConditionTest goalCondition;
	private State initialState;
	private NameDependentStateHashFactory hashFactory;

/* ============================================================================
 * Constructor
 * ========================================================================= */

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
		hashFactory = new NameDependentStateHashFactory();
	}

/* ============================================================================
 * Breadth First Search
 * ========================================================================= */

	/**
	 * Runs Breadth First Search on the diaper domain.  Prints full results to an output file, and
	 * prints the raw action sequence to the command line.
	 *
	 * @param outputPath		A path to a directory where the output file should be placed
	 */
	public void doBFS(String outputPath) {
		outputPath += !outputPath.endsWith("/") ? "/" : "";

		System.out.println("[Behavior.doBFS] Running BFS planner...");
		DeterministicPlanner planner = new BFS(domain, goalCondition, hashFactory);
		planner.planFromState(initialState);
		System.out.println("[Behavior.doBFS] Planner returned, received policy.");

		Policy p = new SDPlannerPolicy(planner);
		EpisodeAnalysis ea = p.evaluateBehavior(initialState, rewardFunction, terminalFunction);
		ea.writeToFile(outputPath + "planResult", stateParser);
		System.out.println("[Behavior.doBFS] PLAN GENERATED:");
		System.out.println(ea.getActionSequenceString("\n"));
	}

/* ============================================================================
 * A* Search
 * ========================================================================= */

	/**
	 * Runs A* search on the diaper domain using the mental-states-left heuristic. Prints full 
	 * results to an output file, and prints the raw action sequence to the command line.
	 *
	 * @param outputPath 		A path to a directory where the output file should be placed
	 */
	public void doAStar(String outputPath) {
		outputPath += !outputPath.endsWith("/") ? "/" : "";

		Heuristic mslHeuristic = new Heuristic() {
			@Override
			public double h(State s) {
				ObjectInstance caregiver = s.getObject(S.OBJ_CAREGIVER);
				String mentalState = (String) caregiver.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
				switch(mentalState) {
					case S.OBJ_STATE_X:
						return -6;
					case S.OBJ_STATE_A:
						return -5;
					case S.OBJ_STATE_B:
						return -4;
					case S.OBJ_STATE_C:
						return -3;
					case S.OBJ_STATE_D:
						return -2;
					case S.OBJ_STATE_E:
						return -1;
					case S.OBJ_STATE_Y:
						return 0;
					default:
						return 0;
				}
			}
		};

		System.out.println("[Behavior.doAStar] Running AStar planner...");
		DeterministicPlanner planner = new AStar(domain, rewardFunction, goalCondition, hashFactory, mslHeuristic);
		planner.planFromState(initialState);
		System.out.println("[Behavior.doAStar] Planner returned, received policy.");

		Policy p = new SDPlannerPolicy(planner);
		EpisodeAnalysis ea = p.evaluateBehavior(initialState, rewardFunction, terminalFunction);
		ea.writeToFile(outputPath + "planResult", stateParser);
		System.out.println("[Behavior.doAStar] PLAN GENERATED:");
		System.out.println(ea.getActionSequenceString("\n"));
	}

/* ============================================================================
 * Value Iteration
 * ========================================================================= */

	public void doValueIteration(String outputPath) {
		outputPath += !outputPath.endsWith("/") ? "/" : "";

		System.out.println("[Behavior.doValueIteration] Running VI Planner...");
		OOMDPPlanner planner = new ValueIteration(domain, rewardFunction, terminalFunction, 0.99, hashFactory, 0.001, 100);
		planner.planFromState(initialState);
		System.out.println("[Behavior.doValueIteration] Planner returned, received policy.");

		Policy p = new GreedyQPolicy((QComputablePlanner)planner);
		EpisodeAnalysis ea = p.evaluateBehavior(initialState, rewardFunction, terminalFunction);
		ea.writeToFile(outputPath + "planResult", stateParser);
		System.out.println("[Behavior.doValueIteration] PLAN GENERATED:");
		System.out.println(ea.getActionSequenceString("\n"));

	}

/* ============================================================================
 * Main method
 * ========================================================================= */

	/**
	 * Runs a terminal UI which prompts the user for which algorithm to run
	 */
	public static void main(String[] args) {
		Behavior test = new Behavior();
		String outputPath = "output/";

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		//Ignore this gross collection of named breaks and while loops :( It's probably 
		//bad java and I should probably feel bad.  That being said it works.  
		collectinput:
		while(true) {
			try {

				String input = "";

				whichalgo:
				while(true) {
					System.out.println("[Behavior.main] Which algorithm? 1 = BFS, 2 = A*, 3 = VI");
					input = br.readLine();
					switch(input) {
						case "1":
							test.doBFS(outputPath);
							break whichalgo;
						case "2":
							test.doAStar(outputPath);
							break whichalgo;
						case "3":
							test.doValueIteration(outputPath);
							break whichalgo;
						default:
							System.out.println("[Behavior.main] Invalid option.");
							continue;
					}
				}

				restart:
				while(true) {
					System.out.println("[Behavior.main] Plan again? Y or N");
					input = br.readLine();
					switch(input) {
						case "Y":
							break restart;
						case "N":
							break collectinput;
						default:
							System.out.println("[Behavior.main] Invalid option.");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
