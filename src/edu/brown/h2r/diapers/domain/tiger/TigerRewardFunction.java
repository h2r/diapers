package edu.brown.h2r.diapers.domain.tiger;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;

public class TigerRewardFunction implements RewardFunction {
	public double reward(State s, GroundedAction a, State sprime) {
		String tigerLocation = sprime.getObject(Names.OBJ_TIGER).getStringValForAttribute(Names.ATTR_TIGER_LOCATION);
		if(a.action.getName().equals(Names.ACTION_OPEN_LEFT_DOOR)) {
			if(tigerLocation.equals(Names.DOOR_LEFT)) {
				return -100;
			} else {
				return 10;
			}
		} else if(a.action.getName().equals(Names.ACTION_OPEN_RIGHT_DOOR)) {
			if(tigerLocation.equals(Names.DOOR_RIGHT)) {
				return -100;
			} else {
				return 10;
			}
		} else if(a.action.getName().equals(Names.ACTION_LISTEN)) {
			return -1;
		}
		return 0;
	}
}
