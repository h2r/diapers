package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.Observation;
import edu.brown.h2r.diapers.POMDPDomain;
import edu.brown.h2r.diapers.POMDPState;

import burlap.oomdp.auxiliary.DomainGenerator;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import burlap.oomdp.singleagent.explorer.VisualExplorer;

import edu.brown.h2r.diapers.athena.namespace.P;

public class POMDPDiaperDomain implements DomainGenerator {

	public POMDPDiaperDomain() {}

/* ============================================================================
 * Domain generation!
 * ========================================================================= */

	public Domain generateDomain() {

		Domain domain = new POMDPDomain() {
			@Override
			public POMDPState getExampleState() {
				return POMDPDiaperDomain.getNewState(this);
			}

			@Override
			public List<State> getAllStates() {
				return POMDPDiaperDomain.getAllStates(this);
			}
		}

		Attribute attrMentalState = new Attribute(domain, P.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);

		ObjectClass stateHolderClass = new ObjectClass(domain, P.CLASS_STATE_HOLDER);
		stateHolderClass.addAttribute(attrMentalState);

		ObjectClass mentalStateClass = new ObjectClass(domain, P.CLASS_MENTAL_STATE);

		Action advanceAction = new AdvanceAction(domain, P.ACTION_ADVANCE);
		Action speakAction = new SpeakAction(domain, P.ACTION_SPEAK);

		Observation stateXObservation = new SimpleStateObservation(domain, P.OBS_STATE_X, P.OBJ_STATE_X);
		Observation stateAObservation = new SimpleStateObservation(domain, P.OBS_STATE_A, P.OBJ_STATE_A);
		Observation stateBObservation = new SimpleStateObservation(domain, P.OBS_STATE_B, P.OBJ_STATE_B);
		Observation stateCObservation = new SimpleStateObservation(domain, P.OBS_STATE_C, P.OBJ_STATE_C);
		Observation stateDObservation = new SimpleStateObservation(domain, P.OBS_STATE_D, P.OBJ_STATE_D);
		Observation stateEObservation = new SimpleStateObservation(domain, P.OBS_STATE_E, P.OBJ_STATE_E);
		Observation stateYObservation = new SimpleStateObservation(domain, P.OBS_STATE_Y, P.OBJ_STATE_Y);
		Observation nullObservation = new Observation(domain, P.NULL_OBSERVATION);

		return domain;
	}

/* ============================================================================
 * State access methods
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

		holder.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_X);	

		addObjects(s, holder, stateX, stateA, stateB, stateC, stateD, stateE, stateY);	

		return s;
	}

	private static void addObjects(State s, ObjectInstance... objs) {
		for(ObjectInstance object : objs) {
			s.addObject(object);
		}
	}

	public static List<State> getAllStates(Domain d) {
		List<State> ret_val = new ArrayList<State>();

		State s1 = new State();
		State s2 = new State();
		State s3 = new State();
		State s4 = new State();
		State s5 = new State();
		State s6 = new State();
		State s7 = new State();

		ret_val.add(s1);
		ret_val.add(s2);
		ret_val.add(s3);
		ret_val.add(s4);
		ret_val.add(s5);
		ret_val.add(s6);
		ret_val.add(s7);

		ObjectClass stateHolderClass = d.getObjectClass(P.CLASS_STATE_HOLDER);
		ObjectClass mentalStateClass = d.getObjectClass(P.CLASS_MENTAL_STATE);

		ObjectInstance stateX = new ObjectInstance(mentalStateClass, P.OBJ_STATE_X);
		ObjectInstance stateA = new ObjectInstance(mentalStateClass, P.OBJ_STATE_A);
		ObjectInstance stateB = new ObjectInstance(mentalStateClass, P.OBJ_STATE_B);
		ObjectInstance stateC = new ObjectInstance(mentalStateClass, P.OBJ_STATE_C);
		ObjectInstance stateD = new ObjectInstance(mentalStateClass, P.OBJ_STATE_D);
		ObjectInstance stateE = new ObjectInstance(mentalStateClass, P.OBJ_STATE_E);
		ObjectInstance stateY = new ObjectInstance(mentalStateClass, P.OBJ_STATE_Y);

		for(int i = 0; i < ret_val.size(); ++i) {
			State temp = ret_val.get(i);
			addObjects(temp, stateX, stateA, stateB, stateC, stateD, stateE, stateY);
		}

		ObjectInstance sh1 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh1.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_X);
		s1.addObject(sh1);

		ObjectInstance sh2 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh2.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_A);
		s2.addObject(sh2);

		ObjectInstance sh3 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh3.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_B);
		s3.addObject(sh3);

		ObjectInstance sh4 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh4.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_C);
		s4.addObject(sh4);

		ObjectInstance sh5 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh5.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_D);
		s5.addObject(sh5);

		ObjectInstance sh6 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh6.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_E);
		s6.addObject(sh6);

		ObjectInstance sh7 = new ObjectInstance(stateHolderClass, P.OBJ_HOLDER);
		sh7.addRelationalTarget(P.ATTR_MENTAL_STATE, P.OBJ_STATE_Y);
		s7.addObject(sh7);

		return ret_val;
	}

/* ============================================================================
 * Observation definition
 * ========================================================================= */

	public class SimpleStateObservation extends Observation {
		
		private String my_state;

		public SimpleStateObservation(Domain d, String s, String state) {
			super((POMDPDomain)d, s);
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

/* ============================================================================
 * Speak action definition
 * ========================================================================= */

	public class SpeakAction extends Action {
		public SpeakAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			POMDPDomain dom = (POMDPDomain) domain;

			ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
			String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

			switch(state) {
				case P.OBJ_STATE_X:
					ps.setObservation(dom.getObservation(P.OBS_STATE_X));
					break;
				case P.OBJ_STATE_A:
					ps.setObservation(dom.getObservation(P.OBS_STATE_A));
					break;
				case P.OBJ_STATE_B:
					ps.setObservation(dom.getObservation(P.OBS_STATE_B));
					break;
				case P.OBJ_STATE_C:
					ps.setObservation(dom.getObservation(P.OBS_STATE_C));
					break;
				case P.OBJ_STATE_D:
					ps.setObservation(dom.getObservation(P.OBS_STATE_D));
					break;
				case P.OBJ_STATE_E:
					ps.setObservation(dom.getObservation(P.OBS_STATE_E));
					break;
				case P.OBJ_STATE_Y:
					ps.setObservation(dom.getObservation(P.OBS_STATE_Y));
					break;
			}

			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();

			List<State> states = POMDPDiaperDomain.getAllStates(domain);

			ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
			String holder_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

			for(State test_state : states) {
				
				ObjectInstance test_holder = current_state.getObject(P.OBJ_HOLDER);
				String test_mental_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(holder_state.equals(test_mental_state)) {
					trans.add(new TransitionProbability(test_state, 1));
				} else {
					trans.add(new TransitionProbability(test_state, 0));
				}
			}

			return trans;
		}
	}

/* ============================================================================
 * Advance action definition
 * ========================================================================= */

	public class AdvanceAction extends Action {
		public AdvanceAction(Domain domain, String name) {
			super(name, domain, new String[]{P.CLASS_MENTAL_STATE});
		}

		@Override 
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
			String mental_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
			return !mental_state.equals(P.OBJ_STATE_Y);
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			POMDPDomain dom = (POMDPDomain) domain;

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

			ps.addObservation(dom.getObservation(P.NULL_OBSERVATION));
			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
			List<State> states = POMDPDiaperDomain.getAllStates(domain);

			ObjectInstance mental_state = st.getObject(params[0]);

			for(State test_state : states) {

				ObjectInstance test_holder = current_state.getObject(P.OBJ_HOLDER);
				String test_mental_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(test_mental_state.equals(mental_state)) {
					trans.put(new TransitionProbability(test_state, 1));
				} else {
					trans.put(new TransitionProbability(test_state, 0));
				}
			}

			return trans;
		}
	}
}