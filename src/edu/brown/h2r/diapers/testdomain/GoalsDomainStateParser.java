package edu.brown.h2r.diapers.testdomain;

import burlap.oomdp.core.State;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.auxiliary.StateParser;

import java.util.Map;
import java.util.HashMap;

public class GoalsDomainStateParser implements StateParser {

	public String stateToString(State s) {
		
		String result = "";
			
		String lotionContainer = s.getObject(Names.OBJ_LOTION).getStringValForAttribute(Names.ATTR_CONTAINER);
		String oldClothesContainer = s.getObject(Names.OBJ_OLD_CLOTHES).getStringValForAttribute(Names.ATTR_CONTAINER);
		String newClothesContainer = s.getObject(Names.OBJ_NEW_CLOTHES).getStringValForAttribute(Names.ATTR_CONTAINER);
		int dresserOpen = s.getObject(Names.OBJ_DRESSER).getDiscValForAttribute(Names.ATTR_OPEN);
		int babyRash = s.getObject(Names.OBJ_BABY).getDiscValForAttribute(Names.ATTR_RASH);
		String caregiverState = s.getObject(Names.OBJ_CAREGIVER).getStringValForAttribute(Names.ATTR_MENTAL_STATE);

		result += "The lotion is in " + Names.prettify(lotionContainer) + ". ";
		result += "The old clothes are in " + Names.prettify(oldClothesContainer) + ". ";
		result += "The new clothes are in " + Names.prettify(newClothesContainer) + ". ";
		result += "The dresser is " + (dresserOpen == 1 ? "open" : "shut") + ". ";
		result += "The baby " + (babyRash == 1 ? "has" : "doesn't have") + " a rash. ";
		result += "The caregiver's mental state is \'" + caregiverState + "\'. ";

		return result;
	
	}

	public State stringToState(String str) {
		return new State();
		//I don't need this yet
	}
}
