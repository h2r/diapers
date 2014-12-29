package diaperdomain;

import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;

public class RashDomainTerminalFunction implements TerminalFunction{

	@Override
	public boolean isTerminal(State s) {
//		return false;
		ObjectInstance human=s.getObject(Names.OBJ_HUMAN);
		return ((String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0]).equals(Names.MS_TYPE_GOAL);
	}
	
}