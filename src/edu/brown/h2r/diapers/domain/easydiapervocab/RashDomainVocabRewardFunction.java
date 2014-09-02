package edu.brown.h2r.diapers.domain.easydiapervocab;


import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;

public class RashDomainVocabRewardFunction implements RewardFunction {
	public double reward(State s, GroundedAction a, State sprime) {
		if(s.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE).equals(Names.MS_TYPE_GOAL)){
			return 0.0;
		}

		
		return -1.0;

	}
}
