package edu.brown.h2r.diapers.domain.mediumdiaper;

import edu.brown.h2r.diapers.pomdp.POMDPState;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.Domain;

public class Actions {
	
	/**
	 * This class represents the action BRING(CONTENT, CONTAINER) for bringing
	 * an object from one part of the scene to another.  Both the container currently
	 * holding the content, and the container to which it will be brought must
	 * be open for the action to be applicable.
	 */
	public static class BringAction extends Action {
		public BringAction(Domain domain, String name) {
			super(name, domain, new String[]{Names.CLASS_CONTENT, Names.CLASS_CONTAINER});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			PropositionalFunction open = domain.getPropFunction(Names.PROP_OPEN);
			return open.isTrue(st, st.getObject(params[0]).getStringValForAttribute(Names.ATTR_CONTAINER)) && open.isTrue(st, params[1]);
		}

		@Override
		protected State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);

			grabObject(ps, ps.getObject(params[0]));
			placeObject(ps.getObject(params[0]), ps.getObject(params[1]));

			MediumDiaperDomain.caregiverThink(domain, ps);
			return ps;
		}

		private void grabObject(State st, ObjectInstance obj) {
			ObjectInstance oldContainer = st.getObject(obj.getStringValForAttribute(Names.ATTR_CONTAINER));
			oldContainer.removeRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			obj.clearRelationalTargets(Names.ATTR_CONTAINER);
		}

		private void placeObject(ObjectInstance obj, ObjectInstance cnt) {
			obj.addRelationalTarget(Names.ATTR_CONTAINER, cnt.getName());
			cnt.addRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
		}
	}
	
	/**
	 * This class represents the action ASK() for propting the caregiver to return
	 * an observation representing its current goal.  Always applicable.
	 */
	public static class AskAction extends Action {
		public AskAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		protected State performActionHelper(State st, String[] params) {

			POMDPState ps = new POMDPState(st);
			boolean done = MediumDiaperDomain.caregiverThink(domain, ps);
			if(done) {
				ps.getObject(Names.OBJ_CAREGIVER).setValue(Names.ATTR_MENTAL_STATE, "done");
			}
			return ps;
		}
	}

	/**
	 * This class represents the action OPEN(CONTAINER) for changing a container's state
	 * from closed to open.  Only applicable if the container is currently closed.
	 */
	public static class OpenAction extends Action {
		public OpenAction(Domain domain, String name) {
			super(name, domain, new String[]{Names.CLASS_CONTAINER});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			return st.getObject(params[0]).getStringValForAttribute(Names.ATTR_OPEN).equals("0");
		}

		@Override
		protected State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			ps.getObject(params[0]).setValue(Names.ATTR_OPEN, 1);
			MediumDiaperDomain.caregiverThink(domain, ps);
			return ps;
		}
	}

	/**
	 * This class represents the action WAIT() for doing nothing.  Always applicable.
	 */
	public static class WaitAction extends Action {
		public WaitAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		protected State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			MediumDiaperDomain.caregiverThink(domain, ps);
			return ps;
		}
	}
}
