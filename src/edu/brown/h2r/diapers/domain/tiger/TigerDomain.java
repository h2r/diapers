package edu.brown.h2r.diapers.domain.tiger;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;

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
			public POMDPState sampleInitialState() {
				return TigerDomain.getNewState(this);
			}

			@Override
			public List<State> getAllStates() {
				return TigerDomain.getAllStates(this);
			}

			@Override
			public Observation makeObservationFor(GroundedAction a, POMDPState s) {
				return TigerDomain.makeObservationFor(this, a, s);
			}

			@Override
			public boolean isSuccess(Observation o) {
				return TigerDomain.isSuccess(o);
			}

			@Override
			public boolean isTerminal(POMDPState s) {
				return TigerDomain.isTerminal(this, s);
			}
		};

		List<String> possibleLocations = new ArrayList<String>() {{
			add(Names.DOOR_LEFT); add(Names.DOOR_RIGHT);  
		}};

		Attribute tigerLocation = new Attribute(domain, Names.ATTR_TIGER_LOCATION, Attribute.AttributeType.DISC);
		tigerLocation.setDiscValues(possibleLocations);

		Attribute isDoorOpen = new Attribute(domain, Names.ATTR_DOOR_OPEN, Attribute.AttributeType.DISC);
		isDoorOpen.setDiscValuesForRange(0, 1, 1);

		ObjectClass tigerClass = new ObjectClass(domain, Names.CLASS_TIGER);
		tigerClass.addAttribute(tigerLocation);

		ObjectClass refereeClass = new ObjectClass(domain, Names.CLASS_REFEREE);
		refereeClass.addAttribute(isDoorOpen);

		Action openLeftDoor = new OpenDoorAction(domain, Names.ACTION_OPEN_LEFT_DOOR);
		Action openRightDoor = new OpenDoorAction(domain, Names.ACTION_OPEN_RIGHT_DOOR);
		Action listen = new ListenAction(domain, Names.ACTION_LISTEN);

		Observation leftDoorObservation = new DoorObservation(domain, Names.LEFT_DOOR_OBSERVATION, Names.DOOR_LEFT);
		Observation rightDoorObservation = new DoorObservation(domain, Names.RIGHT_DOOR_OBSERVATION, Names.DOOR_RIGHT);
		Observation deathObservation = new Observation(domain, Names.DEATH_OBSERVATION);
		Observation surviveObservation = new Observation(domain, Names.SURVIVE_OBSERVATION);
		Observation nullObservation = new Observation(domain, Names.NULL_OBSERVATION);

		PropositionalFunction doorOpenedPF = new DoorOpenedPF(domain, Names.PROP_DOOR_OPENED);

		return domain;
	}

/* ============================================================================
 * Initial state generation
 * ========================================================================= */

	public static POMDPState getNewState(Domain d) {
		POMDPState s = new POMDPState();

		ObjectClass tigerClass = d.getObjectClass(Names.CLASS_TIGER);
		ObjectClass refereeClass = d.getObjectClass(Names.CLASS_REFEREE);

		ObjectInstance referee = new ObjectInstance(refereeClass, Names.OBJ_REFEREE);
		ObjectInstance tiger = new ObjectInstance(tigerClass, Names.OBJ_TIGER);

		referee.setValue(Names.ATTR_DOOR_OPEN, 0);

		if(new java.util.Random().nextDouble() > 0.5) {
			tiger.setValue(Names.ATTR_TIGER_LOCATION, Names.DOOR_LEFT);
		} else {
			tiger.setValue(Names.ATTR_TIGER_LOCATION, Names.DOOR_RIGHT);
		}

		s.addObject(tiger);
		s.addObject(referee);

		return s;
	}

	public static List<State> getAllStates(Domain d) {
		List<State> ret_val = new ArrayList<State>();

		State s1 = new State();
		State s2 = new State();

		ObjectClass tigerClass = d.getObjectClass(Names.CLASS_TIGER);
		ObjectClass refereeClass = d.getObjectClass(Names.CLASS_REFEREE);

		ObjectInstance referee = new ObjectInstance(refereeClass, Names.OBJ_REFEREE);
		ObjectInstance tiger = new ObjectInstance(tigerClass, Names.OBJ_TIGER);

		referee.setValue(Names.ATTR_DOOR_OPEN, 0);
		tiger.setValue(Names.ATTR_TIGER_LOCATION, Names.DOOR_LEFT);

		s1.addObject(tiger);
		s1.addObject(referee);

		ret_val.add(s1);

		ObjectInstance referee2 = new ObjectInstance(refereeClass, Names.OBJ_REFEREE);
		ObjectInstance tiger2 = new ObjectInstance(tigerClass, Names.OBJ_TIGER);

		referee2.setValue(Names.ATTR_DOOR_OPEN, 0);
		tiger2.setValue(Names.ATTR_TIGER_LOCATION, Names.DOOR_RIGHT);

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
			ObjectInstance tiger = s.getObject(Names.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(Names.ATTR_TIGER_LOCATION);

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

			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
			List<State> states = TigerDomain.getAllStates(domain);
			
			State s1 = states.get(0);
			State s2 = states.get(1);	

			ObjectInstance tiger = s.getObject(Names.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(Names.ATTR_TIGER_LOCATION);

			if(door.equals(Names.DOOR_LEFT)) {
				trans.add(new TransitionProbability(s1, 1));
				trans.add(new TransitionProbability(s2, 0));
			} else if(door.equals(Names.DOOR_RIGHT)) {
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

			ObjectInstance referee = ps.getObject(Names.OBJ_REFEREE);
			referee.setValue(Names.ATTR_DOOR_OPEN, 1);

			return ps;
		}

		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params) {
			List<TransitionProbability> trans = new ArrayList<TransitionProbability>();
			List<State> states = TigerDomain.getAllStates(domain);
			
			State s1 = states.get(0);
			State s2 = states.get(1);	

			ObjectInstance tiger = s.getObject(Names.OBJ_TIGER);
			String door = tiger.getStringValForAttribute(Names.ATTR_TIGER_LOCATION);

			if(door.equals(Names.DOOR_LEFT)) {
				trans.add(new TransitionProbability(s1, 0));
				trans.add(new TransitionProbability(s2, 0));
			} else if(door.equals(Names.DOOR_RIGHT)) {
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
			super(name, domain, new String[]{Names.CLASS_REFEREE});
		}

		@Override
		public boolean isTrue(State st, String[] params) {
			ObjectInstance referee = st.getObject(params[0]);
			boolean doorOpen = referee.getDiscValForAttribute(Names.ATTR_DOOR_OPEN) == 1;
			return doorOpen;
		}
	}

	public static Observation makeObservationFor(POMDPDomain d, GroundedAction a, POMDPState s) {
		String tigerLocation = s.getObject(Names.OBJ_TIGER).getStringValForAttribute(Names.ATTR_TIGER_LOCATION);
		if(a.action.getName().equals(Names.ACTION_LISTEN)) {
			Observation correct = (tigerLocation.equals(Names.DOOR_LEFT)) ? d.getObservation(Names.LEFT_DOOR_OBSERVATION) : d.getObservation(Names.RIGHT_DOOR_OBSERVATION);
			Observation incorrect = (tigerLocation.equals(Names.DOOR_LEFT)) ? d.getObservation(Names.RIGHT_DOOR_OBSERVATION) : d.getObservation(Names.LEFT_DOOR_OBSERVATION);
			if(new java.util.Random().nextDouble() < 0.3) {
				return incorrect;
			} else {
				return correct;
			}
		} else {
			if(d.getPropFunction(Names.PROP_DOOR_OPENED).isTrue(s, new String[]{Names.OBJ_REFEREE})) {
				if((a.action.getName().equals(Names.ACTION_OPEN_LEFT_DOOR) && tigerLocation.equals(Names.DOOR_LEFT)) ||
				   (a.action.getName().equals(Names.ACTION_OPEN_RIGHT_DOOR) && tigerLocation.equals(Names.DOOR_RIGHT))) {
					return d.getObservation(Names.DEATH_OBSERVATION);
				} else {
					return d.getObservation(Names.SURVIVE_OBSERVATION);
				}
			} else {
				return d.getObservation(Names.NULL_OBSERVATION);
			}
		}
	}

	public static boolean isSuccess(Observation o) {
		return o.getName().equals(Names.DEATH_OBSERVATION) || o.getName().equals(Names.SURVIVE_OBSERVATION);
	}

	public static boolean isTerminal(POMDPDomain d, POMDPState s) {
		return d.getPropFunction(Names.PROP_DOOR_OPENED).isTrue(s, new String[]{Names.OBJ_REFEREE});
	}

/* ============================================================================
 * Main method
 * ========================================================================= */

	public static void main(String[] args) {
		TigerDomain td = new TigerDomain();
		POMDPDomain domain = (POMDPDomain) td.generateDomain();
		POMDPState s = (POMDPState) getNewState(domain);

		Action listen = domain.getAction(Names.ACTION_LISTEN);
		POMDPState after_listening = (POMDPState) listen.performAction(s, new String[]{});
		Observation o = after_listening.getObservation();
		System.out.println("Observation: " + o.getName());

		TerminalExplorer exp = new TerminalExplorer(domain);
		exp.exploreFromState(s);
	}

}
