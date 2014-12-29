package diaperdomainvocab;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;

public class RashDomainVocabTerminalFunction implements TerminalFunction{

	@Override
	public boolean isTerminal(State s) {
//		return false;
//		System.out.println(s.getCompleteStateDescription());
		ObjectInstance human=s.getObject(Names.OBJ_HUMAN);
//		String test=human.getStringValForAttribute(Names.ATTR_MENTAL_STATE);
//		return test.equals(Names.MS_TYPE_GOAL);
		return ((String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0]).equals(Names.MS_TYPE_GOAL);
	}
	
}