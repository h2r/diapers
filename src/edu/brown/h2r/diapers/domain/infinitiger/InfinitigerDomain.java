package edu.brown.h2r.diapers.domain.infinitiger;

import java.util.List;
import java.util.ArrayList;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

import edu.brown.h2r.diapers.pomdp.*;


public class InfinitigerDomain implements DomainGenerator {
	
	private static int iterations;
	private static int observationsPerState;
	
	public InfinitigerDomain(int i, int o) {
		iterations = i;
		observationsPerState = o;
	}
	
	public Domain generateDomain() {
		Domain domain = new POMDPDomain() {
			@Override public POMDPState sampleInitialState() { return InfinitigerDomain.getNewState(this); }
			@Override public Observation makeObservationFor(GroundedAction a, POMDPState s) { return InfinitigerDomain.makeObservationFor(this, a, s); }
			@Override public boolean isSuccess(Observation o) { return InfinitigerDomain.isSuccess(o); }
			@Override public boolean isTerminal(POMDPState s) { return InfinitigerDomain.isTerminal(this, s); }
		};
			
		Attribute tigerness = new Attribute(domain, Names.ATTR_TIGERNESS, Attribute.AttributeType.DISC);
		tigerness.setDiscValuesForRange(0, 1, 1);

		Attribute position = new Attribute(domain, Names.ATTR_POSITION, Attribute.AttributeType.DISC);
		position.setDiscValues(new ArrayList<String>() {{ add(Names.LEFT); add(Names.RIGHT); }});

		Attribute index = new Attribute(domain, Names.ATTR_INDEX, Attribute.AttributeType.DISC);
		index.setDiscValuesForRange(0, iterations, 1);

		ObjectClass doorClass = new ObjectClass(domain, Names.CLASS_DOOR);
		doorClass.addAttribute(tigerness);
		doorClass.addAttribute(position);
		doorClass.addAttribute(index);

		ObjectClass indexerClass = new ObjectClass(domain, Names.CLASS_INDEXER);
		indexerClass.addAttribute(index);

		Action openDoor = new OpenAction(domain, Names.ACTION_OPEN_DOOR);
		Action listen = new ListenAction(domain, Names.ACTION_LISTEN);

		for(int i = 0; i < observationsPerState; ++i) {
			Observation left = new Observation(domain, Names.OBS_LEFT_DOOR + i);
			Observation right = new Observation(domain, Names.OBS_RIGHT_DOOR + i);
		}

		Observation nullObs = new Observation(domain, Names.OBS_NULL);
		Observation complete = new Observation(domain, Names.OBS_COMPLETE);

		return domain;
	}

	public class OpenAction extends Action {
		public OpenAction(Domain domain, String name) {
			super(name, domain, new String[]{Names.CLASS_DOOR});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance door = st.getObject(params[0]);
			ObjectInstance indexer = st.getObject(Names.OBJ_INDEXER);

			return door.getDiscValForAttribute(Names.ATTR_INDEX) == indexer.getDiscValForAttribute(Names.ATTR_INDEX);
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);

			ObjectInstance indexer = ps.getObject(Names.OBJ_INDEXER);
			int currIndex = indexer.getDiscValForAttribute(Names.ATTR_INDEX);
			indexer.setValue(Names.ATTR_INDEX, currIndex + 1);

			return ps;
		}
	}

	public class ListenAction extends Action {
		public ListenAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			return new POMDPState(st);
		}
	}

	public static POMDPState getNewState(Domain d) {
		POMDPState s = new POMDPState();

		ObjectClass doorClass = d.getObjectClass(Names.CLASS_DOOR);
		ObjectClass indexerClass = d.getObjectClass(Names.CLASS_INDEXER);
		
		ObjectInstance indexer = new ObjectInstance(indexerClass, Names.OBJ_INDEXER);
		indexer.setValue(Names.ATTR_INDEX, 0);
		s.addObject(indexer);

		for(int i = 0; i < iterations; ++i) {
			ObjectInstance leftDoor = new ObjectInstance(doorClass, Names.OBJ_LEFT_DOOR + i);
			ObjectInstance rightDoor = new ObjectInstance(doorClass, Names.OBJ_RIGHT_DOOR + i);

			leftDoor.setValue(Names.ATTR_POSITION, Names.LEFT);
			leftDoor.setValue(Names.ATTR_INDEX, i);

			rightDoor.setValue(Names.ATTR_POSITION, Names.RIGHT);
			rightDoor.setValue(Names.ATTR_INDEX, i);

			boolean doorChoice = new java.util.Random().nextBoolean();
			leftDoor.setValue(Names.ATTR_TIGERNESS, doorChoice ? 0 : 1);
			rightDoor.setValue(Names.ATTR_TIGERNESS, doorChoice ? 1 : 0);

			s.addObject(leftDoor);
			s.addObject(rightDoor);
		}

		return s;
	}

	public static Observation makeObservationFor(POMDPDomain d, GroundedAction a, POMDPState s) {
		ObjectInstance indexer = s.getObject(Names.OBJ_INDEXER);
		int index = indexer.getDiscValForAttribute(Names.ATTR_INDEX);

		if(index == iterations) {
			return d.getObservation(Names.OBS_COMPLETE);
		}

		ObjectInstance leftDoor = s.getObject(Names.OBJ_LEFT_DOOR + index);
		ObjectInstance rightDoor = s.getObject(Names.OBJ_RIGHT_DOOR + index);

		int leftDoorTiger = leftDoor.getDiscValForAttribute(Names.ATTR_TIGERNESS);
		java.util.Random random = new java.util.Random();

		if(a.action.getName().equals(Names.ACTION_LISTEN)) {
			
			Observation left = d.getObservation(Names.OBS_LEFT_DOOR + random.nextInt(observationsPerState));
			Observation right = d.getObservation(Names.OBS_RIGHT_DOOR + random.nextInt(observationsPerState));

			if(leftDoorTiger == 1) {
				return random.nextDouble() < 0.7 ? left : right;
			} else {
				return random.nextDouble() < 0.7 ? right : left;
			}
		} else {
			return d.getObservation(Names.OBS_NULL);
		}
	}

	public static boolean isSuccess(Observation o) {
		return o.getName().equals(Names.OBS_COMPLETE);
	}

	public static boolean isTerminal(POMDPDomain d, POMDPState s) {
		return s.getObject(Names.OBJ_INDEXER).getDiscValForAttribute(Names.ATTR_INDEX) == iterations;
	}
}
