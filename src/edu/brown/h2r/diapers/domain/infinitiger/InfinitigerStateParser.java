package edu.brown.h2r.diapers.domain.infinitiger;

import burlap.oomdp.core.State;
import burlap.oomdp.auxiliary.StateParser;

public class InfinitigerStateParser implements StateParser {
	public String stateToString(State s) {
		int ldt = s.getObject(Names.OBJ_LEFT_DOOR).getDiscValForAttribute(Names.ATTR_TIGERNESS);
		return ldt == 1 ? "<TIGER LEFT>" : "<TIGER RIGHT>";
	}
	
	public State stringToState(String s) {
		return new State();
	}
}
