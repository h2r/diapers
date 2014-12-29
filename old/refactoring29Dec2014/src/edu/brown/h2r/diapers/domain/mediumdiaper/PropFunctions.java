package edu.brown.h2r.diapers.domain.mediumdiaper;

import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;

public class PropFunctions {
	public static class FindPropFun extends PropositionalFunction {
		public FindPropFun(Domain domain, String name) {
			super(name, domain, new String[]{Names.CLASS_CONTAINER, Names.CLASS_CONTENT});
		}

		@Override
		public boolean isTrue(State st, String[] params) {
			return st.getObject(params[0]).getAllRelationalTargets(Names.ATTR_CONTENTS).contains(params[1]) &&
				st.getObject(params[1]).getStringValForAttribute(Names.ATTR_CONTAINER).equals(params[0]);
		}
	}

	public static class OpenPropFun extends PropositionalFunction {
		public OpenPropFun(Domain domain, String name) {
			super(name, domain, new String[]{Names.CLASS_CONTAINER});
		}

		@Override
		public boolean isTrue(State st, String[] params) {
			return st.getObject(params[0]).getStringValForAttribute(Names.ATTR_OPEN).equals("1");
		}
	}

	public static class RashPropFun extends PropositionalFunction {
		public RashPropFun(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public boolean isTrue(State st, String[] params) {
			return st.getObject(Names.OBJ_BABY).getStringValForAttribute(Names.ATTR_RASH).equals("1");
		}
	}
}
