package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.athena.namespace.P;
import edu.brown.h2r.diapers.athena.namespace.S;
import edu.brown.h2r.diapers.util.Tuple;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.SinglePFTF;
import burlap.oomdp.singleagent.common.UniformCostRF;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.auxiliary.common.UniversalStateParser;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.singleagent.Action;
import burlap.behavior.singleagent.planning.deterministic.informed.astar.AStar;
import burlap.behavior.singleagent.planning.deterministic.informed.Heuristic;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.singleagent.planning.deterministic.DeterministicPlanner;
import burlap.behavior.singleagent.planning.deterministic.TFGoalCondition;
import burlap.behavior.singleagent.planning.deterministic.SDPlannerPolicy;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.StateConditionTest;

import java.util.List;
import java.util.ArrayList;

public class LLPlanner {

	private Environment environment;
	private DiaperDomain diaperDomain;
	private Domain domain;
	private StateParser stateParser;
	private RewardFunction rewardFunction;
	private NameDependentStateHashFactory hashFactory;

	public LLPlanner(Environment e) {
		environment = e;

		diaperDomain = new DiaperDomain();
		domain = diaperDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		rewardFunction = new UniformCostRF();
		hashFactory = new NameDependentStateHashFactory();
	}

	public List<Tuple<Action, String[]>> convert(String pomdpAction) {

		List<Tuple<Action, String[]>> result = new ArrayList<Tuple<Action, String[]>>();

		switch(pomdpAction) {
			case P.ACTION_SX_ADVANCE:
				return planTo(S.OBJ_STATE_A);
			case P.ACTION_SA_ADVANCE:
				return planTo(S.OBJ_STATE_B);
			case P.ACTION_SB_ADVANCE:
				return planTo(S.OBJ_STATE_C);
			case P.ACTION_SC_ADVANCE:
				return planTo(S.OBJ_STATE_D);
			case P.ACTION_SD_ADVANCE:
				return planTo(S.OBJ_STATE_E);
			case P.ACTION_SE_ADVANCE:
				return planTo(S.OBJ_STATE_Y);
			case P.ACTION_SPEAK:

				Action update = domain.getAction(S.ACTION_UPDATE);
				Action wait = domain.getAction(S.ACTION_WAIT);
				String[] params = new String[]{};

				result.add(new Tuple<Action, String[]>(update, params));
				result.add(new Tuple<Action, String[]>(wait, params));
		}

		return result;
	}

	private List<Tuple<Action, String[]>> planTo(String state) {
		Heuristic h = makeHeuristic(state);

		TerminalFunction terminalFunction;
		switch(state) {
			case S.OBJ_STATE_X:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_X)); break;
			case S.OBJ_STATE_A:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_A)); break;
			case S.OBJ_STATE_B:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_B)); break;
			case S.OBJ_STATE_C:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_C)); break;
			case S.OBJ_STATE_D:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_D)); break;
			case S.OBJ_STATE_E:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_E)); break;
			case S.OBJ_STATE_Y:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_Y)); break;
			default:
				terminalFunction = new SinglePFTF(domain.getPropFunction(S.PROP_IN_STATE_Y)); break;
		}

		StateConditionTest goalCondition = new TFGoalCondition(terminalFunction);
		DeterministicPlanner planner = new AStar(domain, rewardFunction, goalCondition, hashFactory, h);
		planner.planFromState(environment.getCurrentState());

		Policy p = new SDPlannerPolicy(planner);
		EpisodeAnalysis ea = p.evaluateBehavior(environment.getCurrentState(), rewardFunction, terminalFunction);
		List<GroundedAction> actions = ea.actionSequence;

		List<Tuple<Action, String[]>> result = new ArrayList<Tuple<Action, String[]>>();

		for(GroundedAction a : actions) {
			result.add(new Tuple<Action, String[]>(a.action, a.params));
		}

		return result;
	}

	private Heuristic makeHeuristic(String state) {

		int s = -1;

		if(state.equals(S.OBJ_STATE_X)) {
			s = 0;
		} else if(state.equals(S.OBJ_STATE_A)) {
			s = 1;
		} else if(state.equals(S.OBJ_STATE_B)) {
			s = 2;
		} else if(state.equals(S.OBJ_STATE_C)) {
			s = 3;
		} else if(state.equals(S.OBJ_STATE_D)) {
			s = 4;
		} else if(state.equals(S.OBJ_STATE_E)) {
			s = 5;
		} else if(state.equals(S.OBJ_STATE_Y)) {
			s = 6;
		}

		final int goal = s;

		return new Heuristic() {
			@Override public double h(State s) {
				String mentalState = (String) s.getObject(S.OBJ_CAREGIVER).getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
				switch(mentalState) {
					case S.OBJ_STATE_X:
						return stateValue(goal, 0);
					case S.OBJ_STATE_A:
						return stateValue(goal, 1);
					case S.OBJ_STATE_B:
						return stateValue(goal, 2);
					case S.OBJ_STATE_C:
						return stateValue(goal, 3);
					case S.OBJ_STATE_D:
						return stateValue(goal, 4);
					case S.OBJ_STATE_E:
						return stateValue(goal, 5);
					case S.OBJ_STATE_Y:
						return stateValue(goal, 6);
					default:
						return 0;
				}
			}
		};
	}

	private int stateValue(int goalIndex, int currIndex) {
		return goalIndex < currIndex ? -100 : currIndex - goalIndex;
	}
}