package edu.brown.h2r.diapers.domain.infinitiger;


import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;




import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

import edu.brown.h2r.diapers.pomdp.*;




public class InfinitigerDomain implements DomainGenerator {
	
	private static int iterations;
	private static int observationsPerState;
	private static double noise = 0.15;
	
	public InfinitigerDomain(int i, int o) {
		iterations = i;
		observationsPerState = o;
	}
	public InfinitigerDomain(int i, int o, double n) {
		iterations = i;
		observationsPerState = o;
		noise = n;
	}
	
	public Domain generateDomain() {
		Domain domain = new POMDPDomain() {
			@Override public POMDPState sampleInitialState() { return InfinitigerDomain.getNewState(this); }
			@Override public Observation makeObservationFor(GroundedAction a, POMDPState s) { return InfinitigerDomain.makeObservationFor(this, a, s); }
			@Override public boolean isSuccess(Observation o) { return InfinitigerDomain.isSuccess(o); }
			@Override public boolean isTerminal(POMDPState s) { return InfinitigerDomain.isTerminal(this, s); }
			@Override
			public List<POMDPState> getAllInitialStates(){
				NameDependentStateHashFactory hashFactory = new NameDependentStateHashFactory();
				Set<StateHashTuple> tempSet = new HashSet<StateHashTuple>();
				for(int i = 0; i<Math.pow(iterations, 2)*10; i++){
					tempSet.add(hashFactory.hashState(InfinitigerDomain.getNewState(this)));
				}
				Set<POMDPState> noDups = new HashSet<POMDPState>();
				for (StateHashTuple shi : tempSet){
					noDups.add(new POMDPState(shi.s));
				}
				
				return new ArrayList<POMDPState>(noDups);
			}
		};
			
		Attribute tigerness = new Attribute(domain, Names.ATTR_TIGERNESS, Attribute.AttributeType.DISC);
		tigerness.setDiscValuesForRange(0, 1, 1);

		Attribute index = new Attribute(domain, Names.ATTR_INDEX, Attribute.AttributeType.DISC);
		index.setDiscValuesForRange(0, iterations+1, 1);

		Attribute position = new Attribute(domain, Names.ATTR_POSITION, Attribute.AttributeType.DISC);
		position.setDiscValues(new ArrayList<String>() {{ add(Names.LEFT); add(Names.RIGHT); }});




		ObjectClass doorClass = new ObjectClass(domain, Names.CLASS_DOOR);
		doorClass.addAttribute(tigerness);
		doorClass.addAttribute(position);


		ObjectClass indexerClass = new ObjectClass(domain, Names.CLASS_INDEXER);
		indexerClass.addAttribute(index);

		Action openDoor = new OpenAction(domain, Names.ACTION_OPEN_DOOR);
		Action listen = new ListenAction(domain, Names.ACTION_LISTEN);

		for(int i = 0; i < observationsPerState; ++i) {
			Observation left = new Observation(domain, Names.OBS_LEFT_DOOR + i){
				@Override
				public double getProbability(State s, GroundedAction a){
					if(a.action.getName().equals(Names.ACTION_LISTEN)){
						ObjectInstance leftDoor = s.getObject(Names.OBJ_LEFT_DOOR);
						int leftDoorTiger = leftDoor.getDiscValForAttribute(Names.ATTR_TIGERNESS);
						if(leftDoorTiger == 1){
							return (1-noise)/observationsPerState;
						}
						else {return (noise)/observationsPerState;}
					}
					
					
					return 0.0;
				} 
			};
			Observation right = new Observation(domain, Names.OBS_RIGHT_DOOR + i){
				@Override
				public double getProbability(State s, GroundedAction a){
					if(a.action.getName().equals(Names.ACTION_LISTEN)){
						ObjectInstance leftDoor = s.getObject(Names.OBJ_LEFT_DOOR);
						int leftDoorTiger = leftDoor.getDiscValForAttribute(Names.ATTR_TIGERNESS);
						if(leftDoorTiger == 0){
							return (1-noise)/observationsPerState;
						}
						else {return (noise)/observationsPerState;}
					}
					
					
					return 0.0;
				} 
			};

		}

		Observation nullObs = new Observation(domain, Names.OBS_NULL){
			@Override
			public double getProbability(State s, GroundedAction a){
				if(a.action.getName().equals(Names.ACTION_OPEN_DOOR)){
					return 0.5;
				}
				
				
				return 0.0;
			} 
		};
		Observation complete = new Observation(domain, Names.OBS_COMPLETE){
			@Override
			public double getProbability(State s, GroundedAction a){
			ObjectInstance indexer = s.getObject(Names.OBJ_INDEXER);
			int index = indexer.getDiscValForAttribute(Names.ATTR_INDEX);

			if(index == iterations) {
				return 1.0;
			}
			return 0.0;
			}
		};
		return domain;
	}

	public class OpenAction extends Action {
		public OpenAction(Domain domain, String name) {
			super(name, domain, new String[]{Names.CLASS_DOOR});
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			POMDPState ps = new POMDPState(st);

			int index = ps.getObject(Names.OBJ_INDEXER).getDiscValForAttribute(Names.ATTR_INDEX);
			ps.getObject(Names.OBJ_INDEXER).setValue(Names.ATTR_INDEX, index + 1);

			boolean doorChoice = new java.util.Random().nextBoolean();
			ps.getObject(Names.OBJ_LEFT_DOOR).setValue(Names.ATTR_TIGERNESS, doorChoice ? 0 : 1);
			ps.getObject(Names.OBJ_RIGHT_DOOR).setValue(Names.ATTR_TIGERNESS, doorChoice ? 1 : 0);



			return ps;
		}
		
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			
			State nextState = performActionHelper(s, params);
			nextState.getObject(Names.OBJ_LEFT_DOOR).setValue(Names.ATTR_TIGERNESS, 1);
			nextState.getObject(Names.OBJ_RIGHT_DOOR).setValue(Names.ATTR_TIGERNESS, 0);
			List<TransitionProbability> TPList = new ArrayList<TransitionProbability>();
			TPList.add(new TransitionProbability(nextState,0.5));
			
			
			State nextState1 = performActionHelper(s, params);
			nextState1.getObject(Names.OBJ_LEFT_DOOR).setValue(Names.ATTR_TIGERNESS, 0);
			nextState1.getObject(Names.OBJ_RIGHT_DOOR).setValue(Names.ATTR_TIGERNESS, 1);
			
			TPList.add(new TransitionProbability(nextState1,0.5));
			return TPList;
			
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


		ObjectInstance leftDoor = new ObjectInstance(doorClass, Names.OBJ_LEFT_DOOR);
		ObjectInstance rightDoor = new ObjectInstance(doorClass, Names.OBJ_RIGHT_DOOR);

		leftDoor.setValue(Names.ATTR_POSITION, Names.LEFT);
		rightDoor.setValue(Names.ATTR_POSITION, Names.RIGHT);

		boolean doorChoice = new java.util.Random().nextBoolean();
		leftDoor.setValue(Names.ATTR_TIGERNESS, doorChoice ? 0 : 1);
		rightDoor.setValue(Names.ATTR_TIGERNESS, doorChoice ? 1 : 0);

		s.addObject(leftDoor);


		s.addObject(rightDoor);





		return s;
	}

	public static Observation makeObservationFor(POMDPDomain d, GroundedAction a, POMDPState s) {
		ObjectInstance indexer = s.getObject(Names.OBJ_INDEXER);
		int index = indexer.getDiscValForAttribute(Names.ATTR_INDEX);

		if(index == iterations) {
			return d.getObservation(Names.OBS_COMPLETE);
		}

		ObjectInstance leftDoor = s.getObject(Names.OBJ_LEFT_DOOR);
		ObjectInstance rightDoor = s.getObject(Names.OBJ_RIGHT_DOOR);

		int leftDoorTiger = leftDoor.getDiscValForAttribute(Names.ATTR_TIGERNESS);
		java.util.Random random = new java.util.Random();

		if(a.action.getName().equals(Names.ACTION_LISTEN)) {
			
			Observation left = d.getObservation(Names.OBS_LEFT_DOOR + random.nextInt(observationsPerState));
			Observation right = d.getObservation(Names.OBS_RIGHT_DOOR + random.nextInt(observationsPerState));

			if(leftDoorTiger == 1) {
				return random.nextDouble() < 1-noise ? left : right;
			} else {
				return random.nextDouble() < 1-noise ? right : left;
			}
		} else {
			return d.getObservation(Names.OBS_NULL);
		}
	}

	public static boolean isSuccess(Observation o) {
		return o.getName().equals(Names.OBS_COMPLETE);
	}

	public static boolean isTerminal(POMDPDomain d, State s) {
		return s.getObject(Names.OBJ_INDEXER).getDiscValForAttribute(Names.ATTR_INDEX) == iterations;
	}
	
	public static boolean isTerminal(State s) {
		return s.getObject(Names.OBJ_INDEXER).getDiscValForAttribute(Names.ATTR_INDEX) == iterations;
	}
}
