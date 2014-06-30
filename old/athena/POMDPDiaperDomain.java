package edu.brown.h2r.diapers.athena;

import java.util.List;
import java.util.ArrayList;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.util.Util;

import burlap.oomdp.auxiliary.DomainGenerator;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import burlap.oomdp.singleagent.explorer.VisualExplorer;

import edu.brown.h2r.diapers.athena.namespace.P;

public class POMDPDiaperDomain implements DomainGenerator {
	private double TransitionNoise = 0.0;
	
	public boolean setTransitionNoise(double TN) {
		this.TransitionNoise=TN;
		return true;
	}

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
		};

		Attribute attrMentalState = new Attribute(domain, P.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);

		ObjectClass stateHolderClass = new ObjectClass(domain, P.CLASS_STATE_HOLDER);
		stateHolderClass.addAttribute(attrMentalState);

		ObjectClass mentalStateClass = new ObjectClass(domain, P.CLASS_MENTAL_STATE);

		Action sXAdvanceAction = new AdvanceAction(domain, P.ACTION_SX_ADVANCE, P.OBJ_STATE_X, P.OBJ_STATE_A);
		Action sAAdvanceAction = new AdvanceAction(domain, P.ACTION_SA_ADVANCE, P.OBJ_STATE_A, P.OBJ_STATE_B);
		Action sBAdvanceAction = new AdvanceAction(domain, P.ACTION_SB_ADVANCE, P.OBJ_STATE_B, P.OBJ_STATE_C);
		Action sCAdvanceAction = new AdvanceAction(domain, P.ACTION_SC_ADVANCE, P.OBJ_STATE_C, P.OBJ_STATE_D);
		Action sDAdvanceAction = new AdvanceAction(domain, P.ACTION_SD_ADVANCE, P.OBJ_STATE_D, P.OBJ_STATE_E);
		Action sEAdvanceAction = new AdvanceAction(domain, P.ACTION_SE_ADVANCE, P.OBJ_STATE_E, P.OBJ_STATE_Y);

		Action speakAction = new SpeakAction(domain, P.ACTION_SPEAK);
		
		spawnObservations((POMDPDomain)domain, 7, 2);

		/*
		Observation stateXObservation = new SimpleStateObservation(domain, P.OBS_STATE_X, P.OBJ_STATE_X);
		Observation stateAObservation = new SimpleStateObservation(domain, P.OBS_STATE_A, P.OBJ_STATE_A);
		Observation stateBObservation = new SimpleStateObservation(domain, P.OBS_STATE_B, P.OBJ_STATE_B);
		Observation stateCObservation = new SimpleStateObservation(domain, P.OBS_STATE_C, P.OBJ_STATE_C);
		Observation stateDObservation = new SimpleStateObservation(domain, P.OBS_STATE_D, P.OBJ_STATE_D);
		Observation stateEObservation = new SimpleStateObservation(domain, P.OBS_STATE_E, P.OBJ_STATE_E);
		Observation stateYObservation = new SimpleStateObservation(domain, P.OBS_STATE_Y, P.OBJ_STATE_Y);
		Observation nullObservation = new Observation(domain, P.NULL_OBSERVATION);
*/
		
		return domain;
	}
	
	private void spawnObservations(POMDPDomain d, int dim, int gran) {
		
		List<List<Double>> observationDistros = Util.makeDistro(dim, gran);
		
		for(int i = 0; i < observationDistros.size(); ++i) {
			
			final List<Double> obs = observationDistros.get(i);
			new Observation(d, "OBSERVATION" + i) {
				@Override public double getProbability(State s, GroundedAction a) {
					int i = domain.getAllStates().indexOf(s);
					return obs.get(i);
				}
			};
		}
	}

/* ============================================================================
 * State access methods
 * ========================================================================= */

	public static POMDPState getNewState(Domain d) {
		POMDPState s = new POMDPState();

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
 * Observation definition change probability here for changing the learner's behaviour
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
				
			if(state.equals(this.my_state)) {//this.my_state
				return 0.8;
			}
			
			return 0.2/6;
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

			ObjectInstance holder = ps.getObject(P.OBJ_HOLDER);
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
				
				ObjectInstance test_holder = test_state.getObject(P.OBJ_HOLDER);
				String test_mental_state = (String) test_holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

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

		private String fromState;
		private String toState;

		public AdvanceAction(Domain domain, String name, String from_state, String to_state) {
			super(name, domain, new String[]{});
			fromState = from_state;
			toState = to_state;
		}

		@Override 
		public boolean applicableInState(State st, String[] params) {
			// ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
			// String mental_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];
			// return mental_state.equals(fromState);
			return true;
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			POMDPDomain dom = (POMDPDomain) domain;

			ObjectInstance holder = st.getObject(P.OBJ_HOLDER);
			holder.addRelationalTarget(P.ATTR_MENTAL_STATE, toState);

			ps.setObservation(dom.getObservation(P.NULL_OBSERVATION));
			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
			List<State> states = POMDPDiaperDomain.getAllStates(domain);

			ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
			String holder_state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

			for(State test_state : states) {

				ObjectInstance test_holder = test_state.getObject(P.OBJ_HOLDER);
				String test_mental_state = (String) test_holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(test_mental_state.equals(toState) && holder_state.equals(fromState) && !holder_state.equals(P.OBJ_STATE_Y)) {
					trans.add(new TransitionProbability(test_state, 1 - TransitionNoise));
				//}else if(test_mental_state.equals(fromState) && holder_state.equals(fromState) && !holder_state.equals(P.OBJ_STATE_Y)) {
				//	trans.add(new TransitionProbability(test_state, TransitionNoise));
				}
				else if(!holder_state.equals(fromState) && test_mental_state.equals(holder_state) && !holder_state.equals(P.OBJ_STATE_Y)) {
					trans.add(new TransitionProbability(test_state, 1- TransitionNoise));
				} else {
					trans.add(new TransitionProbability(test_state, TransitionNoise/6));
				}
			}

			return trans;
		}
	}
}