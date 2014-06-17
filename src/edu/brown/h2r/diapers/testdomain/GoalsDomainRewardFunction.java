package edu.brown.h2r.diapers.testdomain;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.core.State;

import java.util.Map;
import java.util.HashMap;

public class GoalsDomainRewardFunction implements RewardFunction {

	private double N;
	private Map<String, Double> baselineRewards = new HashMap<String, Double>();
	private Map<String, Double> stateRewards = new HashMap<String, Double>();

	public GoalsDomainRewardFunction(int number) {
		N = number;

		baselineRewards.put(Names.ACTION_BRING, -N);
		baselineRewards.put(Names.ACTION_OPEN, -N);
		baselineRewards.put(Names.ACTION_WAIT, -N);
		baselineRewards.put(Names.ACTION_ASK, -N);

		stateRewards.put(Names.OBJ_OLD_CLOTHES + ":" + Names.OBJ_HAMPER, N/4);
		stateRewards.put(Names.OBJ_LOTION + ":" + Names.OBJ_CHANGING_TABLE, N/3);
		stateRewards.put(Names.OBJ_NEW_CLOTHES + ":" + Names.OBJ_CHANGING_TABLE, N/2);
		stateRewards.put("done", N);
	}

	public double reward(State s, GroundedAction a, State sprime) {
		double reward = baselineRewards.get(a.action.getName());

		String goal = sprime.getObject(Names.OBJ_CAREGIVER).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		//reward += stateRewards.get(goal);

		return reward;
	}
}
