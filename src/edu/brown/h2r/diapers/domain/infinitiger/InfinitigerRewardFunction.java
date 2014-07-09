package edu.brown.h2r.diapers.domain.infinitiger;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.core.State;

public class InfinitigerRewardFunction implements RewardFunction {
	public double reward(State s, GroundedAction a, State sprime) {
		if(InfinitigerDomain.isTerminal(s)){return 0;}

		if(a.action.getName().equals(Names.ACTION_LISTEN)) return -1;
		if(s.getObject(a.params[0]).getDiscValForAttribute(Names.ATTR_TIGERNESS) == 1) return -100;
		return 10;

	}
}
