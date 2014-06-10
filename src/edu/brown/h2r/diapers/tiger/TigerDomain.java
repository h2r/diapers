package edu.brown.h2r.diapers.tiger;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.tiger.namespace.P;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import burlap.oomdp.auxiliary.DomainGenerator;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import burlap.oomdp.singleagent.explorer.VisualExplorer;

public class TigerDomain implements DomainGenerator {
	public TigerDomain() {}

/* ============================================================================
 * Domain generation!
 * ========================================================================= */

	public Domain generateDomain() {

		Domain domain = new POMDPDomain() {
			@Override
			public POMDPState getExampleState() {
				return TigerDomain.getNewState(this);
			}

			@Override
			public List<State> getAllStates() {
				return TigerDomain.getAllStates(this);
			}
		};

		List<String> possibleLocations = new ArrayList<String>() {{
			add(P.DOOR_LEFT); add(P.DOOR_RIGHT);  
		}};

		Attribute tigerLocation = new Attribute(domain, P.ATTR_TIGER_LOCATION, Attribute.AttributeType.DISC);
		tigerLocation.setDiscValues(possibleLocations);

		Attribute isDoorOpen = new Attribute(domain, P.ATTR_DOOR_OPEN, Attribute.AttributeType.DISC);
		isDoorOpen.setDiscValuesForRange(0, 1, 1);

		ObjectClass tigerClass = new ObjectClass(domain, P.CLASS_TIGER);
		tigerClass.addAttribute(tigerLocation);

		ObjectClass refereeClass = new ObjectClass(domain, P.CLASS_REFEREE);
		refereeClass.addAttribute(isDoorOpen);

		Action openLeftDoor = new OpenDoorAction(domain, P.ACTION_OPEN_LEFT_DOOR);
		Action openRightDoor = new OpenDoorAction(domain, P.ACTION_OPEN_RIGHT_DOOR);
		Action listen = new ListenAction(domain, P.ACTION_LISTEN);

		Observation leftDoorObservation = new DoorObservation(domain, P.LEFT_DOOR_OBSERVATION, P.DOOR_LEFT);
		Observation rightDoorObservation = new DoorObservation(domain, P.RIGHT_DOOR_OBSERVATION, P.DOOR_RIGHT);
		Observation nullObservation = new Observation(domain, P.NULL_OBSERVATION);

		PropositionalFunction doorOpenedPF = new DoorOpenedPF(domain, P.PROP_DOOR_OPENED);

		return domain;
	}

/* ============================================================================
 * Initial state generation
 * ========================================================================= */

	public static POMDPState getNewState(Domain d) {
		POMDPState s = new POMDPState();

		ObjectClass tigerClass = d.getObjectClass(P.CLASS_TIGER);
		ObjectClass refereeClass = d.getObjectClass(P.CLASS_REFEREE);

		ObjectInstance referee = new ObjectInstance(refereeClass, P.OBJ_REFEREE);
		ObjectInstance tiger = new ObjectInstance(tigerClass, P.OBJ_TIGER);

		referee.setValue(P.ATTR_DOOR_OPEN, 0);

		if(new java.util.Random().nextDouble() > 0.5) {
			tiger.setValue(P.ATTR_TIGER_LOCATION, P.DOOR_LEFT);
		} else {
			tiger.setValue(P.ATTR_TIGER_LOCATION, P.DOOR_RIGHT);
		}

		s.addObject(tiger);
		s.addObject(referee);

		return s;
	}

	public static List<State> getAllStates(Domain d) {
		List<State> ret_val = new ArrayList<State>();

		State s1 = new State();
		State s2 = new State();

		ObjectClass tigerClass = d.getObjectClass(P.CLASS_TIGER);
		ObjectClass refereeClass = d.getObjectClass(P.CLASS_REFEREE);

		ObjectInstance referee = new ObjectInstance(refereeClass, P.OBJ_REFEREE);
		ObjectInstance tiger = new ObjectInstance(tigerClass, P.OBJ_TIGER);

		referee.setValue(P.ATTR_DOOR_OPEN, 0);
		tiger.setValue(P.ATTR_TIGER_LOCATION, P.DOOR_LEFT);

		s1.addObject(tiger);
		s1.addObject(referee);

		ret_val.add(s1);

		ObjectInstance referee2 = new ObjectInstance(refereeClass, P.OBJ_REFEREE);
		ObjectInstance tiger2 = new ObjectInstance(tigerClass, P.OBJ_TIGER);

		referee2.setValue(P.ATTR_DOOR_OPEN, 0);
		tiger2.setValue(P.ATTR_TIGER_LOCATION, P.DOOR_RIGHT);

		s2.addObject(tiger2);
		s2.addObject(referee2);

		ret_val.add(s2);

		return ret_val;
	}

/* ============================================================================
 * Observation definition
 * ========================================================================= */

	public class DoorObservation extends Observation {
		
		private String my_door;

		public DoorObservation(Domain d, String s, String door) {
			super(d, s);
			this.my_door = door;
		}

		@Override
		public double getProbability(State s, GroundedAction a) {
			ObjectInstance tiger = s.getObject(P.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(P.ATTR_TIGER_LOCATION);

			if(door.equals(this.my_door)) {
				return 0.7;
			}
			return 0.3;
		}
	}

/* ============================================================================
 * Action definition
 * ========================================================================= */

	public class ListenAction extends Action {
		public ListenAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			POMDPDomain dom = (POMDPDomain) domain;

			ObjectInstance tiger = ps.getObject(P.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(P.ATTR_TIGER_LOCATION);
			Random gen = new Random();
			ps.setReward(-1);

			if(door.equals(P.DOOR_LEFT)) {
				if(gen.nextFloat() < 0.3) {
					ps.setObservation(dom.getObservation(P.RIGHT_DOOR_OBSERVATION));
				} else {
					ps.setObservation(dom.getObservation(P.LEFT_DOOR_OBSERVATION));
				}
			} else if(door.equals(P.DOOR_RIGHT)) {
				if(gen.nextFloat() < 0.3) {
					ps.setObservation(dom.getObservation(P.LEFT_DOOR_OBSERVATION));
				} else {
					ps.setObservation(dom.getObservation(P.RIGHT_DOOR_OBSERVATION));
				}
			}

			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
			List<State> states = TigerDomain.getAllStates(domain);
			
			State s1 = states.get(0);
			State s2 = states.get(1);	

			ObjectInstance tiger = s.getObject(P.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(P.ATTR_TIGER_LOCATION);

			if(door.equals(P.DOOR_LEFT)) {
				trans.add(new TransitionProbability(s1, 1));
				trans.add(new TransitionProbability(s2, 0));
			} else if(door.equals(P.DOOR_RIGHT)) {
				trans.add(new TransitionProbability(s1, 0));
				trans.add(new TransitionProbability(s2, 1));
			}

			return trans;
		}
	}

	public class OpenDoorAction extends Action {

		public OpenDoorAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);
			POMDPDomain dom = (POMDPDomain) domain;

			ps.setObservation(dom.getObservation(P.NULL_OBSERVATION));
			

			ObjectInstance referee = ps.getObject(P.OBJ_REFEREE);
			referee.setValue(P.ATTR_DOOR_OPEN, 1);

			ObjectInstance tiger = ps.getObject(P.OBJ_TIGER);
			String tigerLocation = tiger.getStringValForAttribute(P.ATTR_TIGER_LOCATION);

			if(tigerLocation.equals(P.DOOR_LEFT) && this.getName().equals(P.ACTION_OPEN_LEFT_DOOR)) {
				ps.setReward(-100);
			} else if(tigerLocation.equals(P.DOOR_RIGHT) && this.getName().equals(P.ACTION_OPEN_RIGHT_DOOR)) {
				ps.setReward(-100);
			} else {
				ps.setReward(10);
			}

			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
			List<State> states = TigerDomain.getAllStates(domain);
			
			State s1 = states.get(0);
			State s2 = states.get(1);	

			ObjectInstance tiger = s.getObject(P.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(P.ATTR_TIGER_LOCATION);

			if(door.equals(P.DOOR_LEFT)) {
				trans.add(new TransitionProbability(s1, 0));
				trans.add(new TransitionProbability(s2, 0));
			} else if(door.equals(P.DOOR_RIGHT)) {
				trans.add(new TransitionProbability(s1, 0));
				trans.add(new TransitionProbability(s2, 0));
			}

			return trans;
		}
	}

/* ============================================================================
 * Prop function definition
 * ========================================================================= */

	public class DoorOpenedPF extends PropositionalFunction {
		public DoorOpenedPF(Domain domain, String name) {
			super(name, domain, new String[]{P.CLASS_REFEREE});
		}

		@Override
		public boolean isTrue(State st, String[] params) {
			ObjectInstance referee = st.getObject(params[0]);
			boolean doorOpen = referee.getDiscValForAttribute(P.ATTR_DOOR_OPEN) == 1;
			return doorOpen;
		}
	}

/* ============================================================================
 * Main method
 * ========================================================================= */

	public static void main(String[] args) {
		TigerDomain td = new TigerDomain();
		POMDPDomain domain = (POMDPDomain) td.generateDomain();
		POMDPState s = (POMDPState) getNewState(domain);

		Action listen = domain.getAction(P.ACTION_LISTEN);
		POMDPState after_listening = (POMDPState) listen.performAction(s, new String[]{});
		Observation o = after_listening.getObservation();
		System.out.println("Observation: " + o.getName());

		TerminalExplorer exp = new TerminalExplorer(domain);
		exp.exploreFromState(s);
	}

}
