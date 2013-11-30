package edu.brown.h2r.diapers;

import burlap.oomdp.auxiliary.DomainGenerator;

public class POMDPDiaperDomain implements DomainGenerator {

	public POMDPDiaperDomain() {}

/* ============================================================================
 * Domain generation!
 * ========================================================================= */

	public Domain generateDomain() {
		Domain domain = new POMDPDomain();

		Attribute attrMentalState = new Attribute(domain, P.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);

		ObjectClass stateHolderClass = new ObjectClass(domain, P.CLASS_STATE_HOLDER);
		stateHolderClass.addAttribute(attrMentalState);

		ObjectClass mentalStateClass = new ObjectClass(domain, P.CLASS_MENTAL_STATE);

		Action advanceAction = new AdvanceAction(domain, P.ACTION_ADVANCE);
		Action speakAction = new SpeakAction(domain, P.ACTION_SPEAK);

		Observation stateAObservation = new SimpleStateObservation(domain, P.OBS1, P.OBJ_STATE_A);
		Observation stateBObservation = new SimpleStateObservation(domain, P.OBS2, P.OBJ_STATE_B);
		Observation stateCObservation = new SimpleStateObservation(domain, P.OBS3, P.OBJ_STATE_C);
		Observation stateDObservation = new SimpleStateObservation(domain, P.OBS4, P.OBJ_STATE_D);
		Observation stateEObservation = new SimpleStateObservation(domain, P.OBS5, P.OBJ_STATE_E);

		return domain;
	}

/* ============================================================================
 * Initial state generation
 * ========================================================================= */

	public static State getNewState(Domain d) {
		State s = new State();

		ObjectClass stateHolderClass = d.getObjectClass(P.CLASS_STATE_HOLDER);
		ObjectClass mentalStateClass = d.getObjectClass(P.CLASS_MENTAL_STATE);

		ObjectInstance holder = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);

		ObjectInstance stateX = new ObjectInstance(mentalStateClass, P.OBJ_STATE_X);
		ObjectInstance stateA = new ObjectInstance(mentalStateClass, P.OBJ_STATE_A);
		ObjectInstance stateB = new ObjectInstance(mentalStateClass, P.OBJ_STATE_B);
		ObjectInstance stateC = new ObjectInstance(mentalStateClass, P.OBJ_STATE_C);
		ObjectInstance stateD = new ObjectInstance(mentalStateClass, P.OBJ_STATE_D);
		ObjectInstance stateE = new ObjectInstance(mentalStateClass, P.OBJ_STATE_E);
		ObjectInstance stateY = new ObjectInstance(mentalStateClass, P.OBJ_STATE_Y);

		holder.addRelationalTarget(P.ATTR_MENTAL_STATE, stateX.getName());	

		this.addObjects(s, holder, stateX, stateA, stateB, stateC, stateD, stateE, stateY);	

		return s;
	}

	private static void addObjects(State s, ObjectInstance... objs) {
		for(ObjectInstance object : objs) {
			s.addObject(object);
		}
	}

/* ============================================================================
 * Observation definition
 * ========================================================================= */

	public class SimpleStateObservation extends Observation {
		
		private String my_state;

		public SimpleStateObservation(Domain d, String s, String state) {
			super(d, s);
			this.my_state = state;
		}

		@Override
		public double getProbability(State s, GroundedAction a) {
				ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
				String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(state.equals(this.my_state)) {
					return 1;
				}
				return 0;
			}
		}
	}

/* ============================================================================
 * Action definition
 * ========================================================================= */

	public class SpeakAction extends Action {
		public SpeakAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			return st;
		}
	}

	public class AdvanceAction extends Action {
		public AdvanceAction(Domain domain, String name) {
			super(name, domain, new String[]{P.CLASS_MENTAL_STATE});
		}

		@Override 
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
			String mental_state = (String) holder.getAllRelationalTargets().toArray()[0];
			return !mental_state.equals(P.OBJ_STATE_Y);
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			ObjectInstance mental_state = st.getObject(params[0]);
			ObjectInstance holder = st.getObject(P.OBJ_HOLDER);

			switch(mental_state.getName()) {
				case P.OBJ_STATE_X:
					holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_A);
					break;

				case P.OBJ_STATE_A:
					holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_B);
					break;

				case P.OBJ_STATE_B:
					holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_C);
					break;

				case P.OBJ_STATE_C:
					holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_D);
					break;

				case P.OBJ_STATE_D:
					holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_E);
					break;

				case P.OBJ_STATE_E:
					holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_Y);
					break;

			}
		}
	}
}