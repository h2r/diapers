package edu.brown.h2r.diapers.domain.rocksample;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;


import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.util.*;

import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;
import burlap.debugtools.RandomFactory;
import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

public class RockSampleDomain implements DomainGenerator{
	
	public static int sizeOfGrid;
	public static int numberOfRocks;
	public static double halfPowerDistance = 20;
//	public static Random randomAll = new Random(-20);
//	private Random rand = RandomFactory.getMapped(0);
	
	public RockSampleDomain(int size, int rocks){
		sizeOfGrid=size;
		numberOfRocks = rocks;
	};
	
	public RockSampleDomain(){
		sizeOfGrid=11;
		numberOfRocks = 11;
	};
	
	
	// 7,8 map 
	public static List<Tuple<Integer,Integer>> rockPositions_7_8 = new ArrayList<Tuple<Integer,Integer>>(){{
		add(new Tuple<Integer,Integer>(2, 0));
		add(new Tuple<Integer,Integer>(0, 1));
        add(new Tuple<Integer,Integer>(3, 1));
        add(new Tuple<Integer,Integer>(6, 3));
        add(new Tuple<Integer,Integer>(2, 4));
        add(new Tuple<Integer,Integer>(3, 4));
        add(new Tuple<Integer,Integer>(5, 5));
        add(new Tuple<Integer,Integer>(1, 6));
	}};
	
	// 11, 11 map 
		public static List<Tuple<Integer,Integer>> rockPositions_11_11 = new ArrayList<Tuple<Integer,Integer>>(){{
			add(new Tuple<Integer,Integer>(0, 3));
			add(new Tuple<Integer,Integer>(0, 7));
	        add(new Tuple<Integer,Integer>(1, 8));
	        add(new Tuple<Integer,Integer>(2, 4));
	        add(new Tuple<Integer,Integer>(3, 3));
	        add(new Tuple<Integer,Integer>(3, 8));
	        add(new Tuple<Integer,Integer>(4, 3));
	        add(new Tuple<Integer,Integer>(5, 8));
	        add(new Tuple<Integer,Integer>(6, 1));
	        add(new Tuple<Integer,Integer>(9, 3));
	        add(new Tuple<Integer,Integer>(9, 9));
		}};
	
	
	
	@Override
	public Domain generateDomain() {
		Domain domain = new POMDPDomain() {
			@Override public POMDPState sampleInitialState() { return RockSampleDomain.getNewState(this); }
			@Override public Observation makeObservationFor(GroundedAction a, POMDPState s) { return RockSampleDomain.makeObservationFor(this, a, s); }
			@Override public boolean isSuccess(Observation o) { return RockSampleDomain.isSuccess(o); }
			@Override public boolean isTerminal(POMDPState s) { return RockSampleDomain.isTerminal(this, s); }
			@Override
			public List<POMDPState> getAllInitialStates(){
				NameDependentStateHashFactory hashFactory = new NameDependentStateHashFactory();
				Set<StateHashTuple> tempSet = new HashSet<StateHashTuple>();
				for(int i = 0; i<Math.pow(numberOfRocks, 2)*10; i++){
					State s = RockSampleDomain.getNewState(this);
//					System.out.println(s.getStateDescription());
					StateHashTuple st =hashFactory.hashState(s);
//					System.out.println(st.hashCode());
					tempSet.add(st);
				}
				Set<POMDPState> noDups = new HashSet<POMDPState>();
				for (StateHashTuple shi : tempSet){
					noDups.add(new POMDPState(shi.s));
				}
				
				return new ArrayList<POMDPState>(noDups);
			}
			@Override 
			public void visualizeState(State s){
				int numberOfRocks = s.getObject(Names.OBJ_GRID).getDiscValForAttribute(Names.ATTR_NUMBER_OF_ROCKS);
				int gridSize = s.getObject(Names.OBJ_GRID).getDiscValForAttribute(Names.ATTR_GRID_SIZE);
				int xRoverPos = s.getObject(Names.OBJ_AGENT).getDiscValForAttribute(Names.ATTR_X);
				int yRoverPos = s.getObject(Names.OBJ_AGENT).getDiscValForAttribute(Names.ATTR_Y);
				String strOut = "\n";
				String endStrTemp = "";
				for(int i=0;i<gridSize;i++){
					String sTemp = "";
					String sTemp1 = "";
					for(int j=0;j<gridSize;j++){
						sTemp1 += "---";
						sTemp += "|  "; 
					}
					sTemp += "|";
					char[] sTempChars = sTemp.toCharArray();
					for(int j=0;j<numberOfRocks;j++){
//						ObjectInstance rock = s.getObject(Names.OBJ_ROCK+i);
						int yPosRock = s.getObject(Names.OBJ_ROCK+j).getDiscValForAttribute(Names.ATTR_Y);
						if (yPosRock==i){
							int xPosRock = s.getObject(Names.OBJ_ROCK+j).getDiscValForAttribute(Names.ATTR_X);
							System.out.println("Rock"+j+" xPos: " + xPosRock+" yPos: "+yPosRock);
							if(s.getObject(Names.OBJ_ROCK+j).getBooleanValue(Names.ATTR_VALUE)){
								
								sTempChars[3*xPosRock+1]='O';
							}
							else{
								sTempChars[3*xPosRock+1]='o';
							}
						}
						
						
					}
					
					if (yRoverPos==i){
						sTempChars[3*xRoverPos+2]='X';
					}
					strOut += sTemp1 + "\n" + String.valueOf(sTempChars) + "\n";
					endStrTemp=sTemp1;
					}
				strOut+=endStrTemp;
				System.out.println(strOut);
				  
				}
			
		};
		
		// Attributes
		
		Attribute xCord = new Attribute(domain, Names.ATTR_X, Attribute.AttributeType.INT);
		xCord.setLims(0, sizeOfGrid-1);
		
		Attribute yCord = new Attribute(domain, Names.ATTR_Y, Attribute.AttributeType.DISC);
		yCord.setDiscValuesForRange(0, sizeOfGrid-1, 1);
		
		Attribute rockNumber = new Attribute(domain, Names.ATTR_ROCK_NUMBER, Attribute.AttributeType.DISC);
		rockNumber.setDiscValuesForRange(0, numberOfRocks-1, 1);
		
		Attribute totalNumberOfRocks = new Attribute(domain, Names.ATTR_NUMBER_OF_ROCKS, Attribute.AttributeType.DISC);
		totalNumberOfRocks.setDiscValuesForRange(0, numberOfRocks, 1);
		
		Attribute gridSize = new Attribute(domain, Names.ATTR_GRID_SIZE, Attribute.AttributeType.DISC);
		gridSize.setDiscValuesForRange(0, sizeOfGrid, 1);
		
		Attribute wallDirection = new Attribute(domain, Names.ATTR_WALL_DIRECTION, Attribute.AttributeType.RELATIONAL);
		
		
		Attribute value = new Attribute(domain,Names.ATTR_VALUE, Attribute.AttributeType.BOOLEAN);
		
		Attribute collected = new Attribute(domain,Names.ATTR_COLLECTED, Attribute.AttributeType.BOOLEAN);
		
		Attribute complete = new Attribute(domain,Names.ATTR_COMPLETE, Attribute.AttributeType.BOOLEAN);
		
		//String attribute with cardinal directions
		Attribute direction = new Attribute(domain, Names.ATTR_DIRECTION, Attribute.AttributeType.STRING);
		
		//Object classes
		
		ObjectClass rockClass = new ObjectClass(domain, Names.CLASS_ROCK);
		rockClass.addAttribute(xCord);
		rockClass.addAttribute(yCord);
		rockClass.addAttribute(rockNumber);
		rockClass.addAttribute(value);
		rockClass.addAttribute(collected);
		
		ObjectClass roverClass = new ObjectClass(domain, Names.CLASS_AGENT);
		roverClass.addAttribute(xCord);
		roverClass.addAttribute(yCord);
		roverClass.addAttribute(complete);
		
		ObjectClass gridClass = new ObjectClass(domain, Names.CLASS_GRID);
		gridClass.addAttribute(gridSize);
		gridClass.addAttribute(totalNumberOfRocks);
		
		// two ideas: make each direction an object with a string attribute or just make wall objects have string attributes indicating direction
		ObjectClass wallClass = new ObjectClass(domain, Names.CLASS_WALL);
		wallClass.addAttribute(wallDirection);
		
		ObjectClass directionClass = new ObjectClass(domain, Names.CLASS_DIRECTION);
		directionClass.addAttribute(direction);
		
		
		
		// Actions
		Action move = new MoveAction(domain, Names.ACTION_MOVE);
		Action sample = new SampleAction(domain, Names.ACTION_SAMPLE);
		Action check = new CheckAction(domain, Names.ACTION_CHECK);
		
		
		// Observations
		// only two kinds of obsetvations here good or bad!
		
		Observation goodObs = new Observation(domain, Names.OBS_GOOD){
			@Override
			public double getProbability(State s, GroundedAction a){
				//TODO complete probability method - this depends on distance
				return 0.0;
			}
		};
		
		Observation badObs = new Observation(domain, Names.OBS_BAD){
			@Override
			public double getProbability(State s, GroundedAction a){
				//TODO complete probability method - this depends on distance
				return 0.0;
			}
		};
		
		Observation nullObs = new Observation(domain, Names.OBS_NULL){
			@Override
			public double getProbability(State s, GroundedAction a){
				//TODO complete probability method - this depends on distance
				return 0.0;
			}
		};
		
		Observation completeObs = new Observation(domain, Names.OBS_COMPLETE){
			@Override
			public double getProbability(State s, GroundedAction a){
				//TODO complete probability method - this depends on distance
				return 0.0;
			}
		};
		
		
		
		return domain;// need to put in domain here
	
	}

	
	//Actions
	public class MoveAction extends Action{
		public MoveAction(Domain domain, String name){
			super(name, domain, new String[]{Names.CLASS_DIRECTION});
		}

		@Override
		protected State performActionHelper(State s, String[] params) {
			
			State sPrime = new POMDPState(s);
			String direction = sPrime.getObject(params[0]).getName();
//			System.out.println(direction);
			ObjectInstance rover = sPrime.getObject(Names.OBJ_AGENT);
			
			if((rover.getDiscValForAttribute(Names.ATTR_X)==0 && direction.equals(Names.OBJ_WEST))||
					(rover.getDiscValForAttribute(Names.ATTR_Y)==sizeOfGrid-1 && direction.equals(Names.OBJ_SOUTH))||
					(rover.getDiscValForAttribute(Names.ATTR_Y)==0 && direction.equals(Names.OBJ_NORTH))){
				return sPrime;
			}
			
			if((rover.getDiscValForAttribute(Names.ATTR_X)==sizeOfGrid-1 && direction.equals(Names.OBJ_EAST))){
				rover.setValue(Names.ATTR_COMPLETE, true);
				return sPrime;
			}
			
			if(direction.equals(Names.OBJ_NORTH)){
				int yPos = rover.getDiscValForAttribute(Names.ATTR_Y);
				rover.setValue(Names.ATTR_Y, yPos-1);
			}
			else if(direction.equals(Names.OBJ_SOUTH)){
				int yPos = rover.getDiscValForAttribute(Names.ATTR_Y);
				rover.setValue(Names.ATTR_Y, yPos+1);
				}
			else if(direction.equals(Names.OBJ_EAST)){
				int xPos = rover.getDiscValForAttribute(Names.ATTR_X);
				rover.setValue(Names.ATTR_X, xPos+1);
			}
			else if(direction.equals(Names.OBJ_WEST)){
				int xPos = rover.getDiscValForAttribute(Names.ATTR_X);
				rover.setValue(Names.ATTR_X, xPos-1);
			}
			else{
				System.out.println("RockSampleDomain: MoveAction: I shouldn't be in the default at all");
			}
//			
			return sPrime;
		}
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			//TODO add transition probabilities
			System.out.println("RockSampleDomain: MoveAction: transition probabilities not set");
			return null;
		}
		@Override
		public boolean applicableInState(State s, String[] params){
//			State sPrime = new POMDPState(s);
			String direction = s.getObject(params[0]).getName();
//			System.out.println(direction);
			ObjectInstance rover = s.getObject(Names.OBJ_AGENT);
			
			if((rover.getDiscValForAttribute(Names.ATTR_X)==0 && direction.equals(Names.OBJ_WEST))||
					(rover.getDiscValForAttribute(Names.ATTR_Y)==sizeOfGrid-1 && direction.equals(Names.OBJ_SOUTH))||
					(rover.getDiscValForAttribute(Names.ATTR_Y)==0 && direction.equals(Names.OBJ_NORTH))){
				return false;
			}
			return true;
		}
		
	}
	
	
	public class SampleAction extends Action{
		public SampleAction(Domain domain, String name){
			super(name, domain, new String[]{});
		}

		@Override
		protected State performActionHelper(State s, String[] params) {
			
			State sPrime = new POMDPState(s);
			ObjectInstance rover = sPrime.getObject(Names.OBJ_AGENT);
			int xPos = rover.getDiscValForAttribute(Names.ATTR_X);
			int yPos = rover.getDiscValForAttribute(Names.ATTR_Y);
			for(int i=0; i < numberOfRocks; i++){
				ObjectInstance rock = sPrime.getObject(Names.OBJ_ROCK+i);
				if((rock.getDiscValForAttribute(Names.ATTR_X) == xPos) && (rock.getDiscValForAttribute(Names.ATTR_Y) == yPos)){
					rock.setValue(Names.ATTR_VALUE, false);
					rock.setValue(Names.ATTR_COLLECTED, true);	
				}
			}
			return sPrime;
		}
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			//TODO add transition probabilities
			System.out.println("RockSampleDomain: SampleAction: transition probabilities not set");
			return null;
		}
		@Override
		public boolean applicableInState(State s, String [] params){
			ObjectInstance rover = s.getObject(Names.OBJ_AGENT);
			int xPos = rover.getDiscValForAttribute(Names.ATTR_X);
			int yPos = rover.getDiscValForAttribute(Names.ATTR_Y);
			for(int i=0; i < numberOfRocks; i++){
				ObjectInstance rock = s.getObject(Names.OBJ_ROCK+i);
				if((rock.getDiscValForAttribute(Names.ATTR_X) == xPos) && (rock.getDiscValForAttribute(Names.ATTR_Y) == yPos) && !rock.getBooleanValue(Names.ATTR_COLLECTED)){
					return true;	
				}
			}
			return false; 
		}
		
		
	}
	
	public class CheckAction extends Action{
		public CheckAction(Domain domain, String name){
			super(name, domain, new String[]{Names.CLASS_ROCK});
		}

		@Override
		protected State performActionHelper(State s, String[] params) {
			State sPrime = new POMDPState(s); 
			return sPrime;
		}
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			//TODO add transition probabilities
			System.out.println("RockSampleDomain: CheckAction: transition probabilities not set");
			return null;
		}
		
	}
	
	
	
	
	protected static boolean isTerminal(POMDPDomain pomdpDomain, POMDPState s) {
		
		State sPrime = new POMDPState(s);
		ObjectInstance rover = sPrime.getObject(Names.OBJ_AGENT);
		if (rover.getBooleanValue(Names.ATTR_COMPLETE)){return true;}
		return false;
	}
	
	protected static boolean isTerminal(POMDPState s) {
		
		State sPrime = new POMDPState(s);
		ObjectInstance rover = sPrime.getObject(Names.OBJ_AGENT);
		if (rover.getBooleanValue(Names.ATTR_COMPLETE)){return true;}
		return false;
	}

	protected static boolean isSuccess(Observation o) {
		
		if(o.getName().equals(Names.OBS_COMPLETE)){return true;}
		return false;
	}

	protected static Observation makeObservationFor(POMDPDomain pomdpDomain,
			GroundedAction a, POMDPState s) {
		// TODO Auto-generated method stub
		ObjectInstance rover = s.getObject(Names.OBJ_AGENT);
		
		if(rover.getBooleanValue(Names.ATTR_COMPLETE)){
			Observation o = pomdpDomain.getObservation(Names.OBS_COMPLETE);
			return o;
		}
		
		if(a.action.getName().equals(Names.ACTION_CHECK)){
			ObjectInstance rock = s.getObject(a.params[0]);
			double xPosRock = (double)rock.getDiscValForAttribute(Names.ATTR_X);
			double yPosRock = (double)rock.getDiscValForAttribute(Names.ATTR_Y);
			double xPosRover = (double)rover.getDiscValForAttribute(Names.ATTR_X);
			double yPosRover = (double)rover.getDiscValForAttribute(Names.ATTR_Y);
			double distance = Math.sqrt((xPosRock-xPosRover)*(xPosRock-xPosRover) +(yPosRock-yPosRover)*(yPosRock-yPosRover));
			double efficiency = (1 + Math.pow(2, -distance / halfPowerDistance)) * 0.5;
//			java.util.Random random = new java.util.Random();
//			if(random.nextDouble()<efficiency){
			
			if(RandomFactory.getMapped(0).nextDouble()<efficiency){	
				return rock.getBooleanValue(Names.ATTR_VALUE) ? pomdpDomain.getObservation(Names.OBS_GOOD) : pomdpDomain.getObservation(Names.OBS_BAD); 
			}
			else{
				return rock.getBooleanValue(Names.ATTR_VALUE) ? pomdpDomain.getObservation(Names.OBS_BAD) : pomdpDomain.getObservation(Names.OBS_GOOD); 
			}
		}
		
		return pomdpDomain.getObservation(Names.OBS_NULL);
	}

	protected static POMDPState getNewState(POMDPDomain pomdpDomain) {
		
		
		POMDPState s = new POMDPState();
		
		
		ObjectClass rockClass = pomdpDomain.getObjectClass(Names.CLASS_ROCK);
		ObjectClass agentClass = pomdpDomain.getObjectClass(Names.CLASS_AGENT);
		ObjectClass wallClass = pomdpDomain.getObjectClass(Names.CLASS_WALL);
		ObjectClass directionClass = pomdpDomain.getObjectClass(Names.CLASS_DIRECTION);
		ObjectClass gridClass = pomdpDomain.getObjectClass(Names.CLASS_GRID);
		
		
		ObjectInstance roverAgent = new ObjectInstance(agentClass, Names.OBJ_AGENT);
		roverAgent.setValue(Names.ATTR_X, Math.floor((int)(0)));
		roverAgent.setValue(Names.ATTR_Y, Math.floor((int)(sizeOfGrid/2)));
		roverAgent.setValue(Names.ATTR_COMPLETE, false);
		s.addObject(roverAgent);
		
		ObjectInstance grid = new ObjectInstance(gridClass, Names.OBJ_GRID);
		grid.setValue(Names.ATTR_GRID_SIZE, sizeOfGrid);
		grid.setValue(Names.ATTR_NUMBER_OF_ROCKS, numberOfRocks);
		s.addObject(grid);
		
		List<Tuple<Integer,Integer>> rockPositions = new ArrayList<Tuple<Integer,Integer>>();
		if (sizeOfGrid == 11){
			rockPositions = rockPositions_11_11;
		}
		else
		{
			rockPositions = rockPositions_7_8;
		}
		
		for(int i = 0; i<numberOfRocks;i++){
			ObjectInstance rockObject = new ObjectInstance(rockClass, Names.OBJ_ROCK+i);
//			boolean value = new java.util.Random().nextBoolean();
			boolean value = RandomFactory.getMapped(0).nextBoolean();
			rockObject.setValue(Names.ATTR_X, rockPositions.get(i).getX());
			rockObject.setValue(Names.ATTR_Y, rockPositions.get(i).getY());
			rockObject.setValue(Names.ATTR_VALUE, value);
			rockObject.setValue(Names.ATTR_COLLECTED, false);
			rockObject.setValue(Names.ATTR_ROCK_NUMBER, i);
			
			s.addObject(rockObject);
		}
		// adding direction objects - adding direction objects so params can point to them
		
		ObjectInstance east = new ObjectInstance(directionClass, Names.OBJ_EAST);
		east.setValue(Names.ATTR_DIRECTION, Names.DIR_EAST);
		s.addObject(east);
		
		
		ObjectInstance north = new ObjectInstance(directionClass, Names.OBJ_NORTH);
		north.setValue(Names.ATTR_DIRECTION, Names.DIR_NORTH);
		s.addObject(north);
		
		ObjectInstance south = new ObjectInstance(directionClass, Names.OBJ_SOUTH);
		south.setValue(Names.ATTR_DIRECTION, Names.DIR_SOUTH);
		s.addObject(south);
		
		
		
		ObjectInstance west = new ObjectInstance(directionClass, Names.OBJ_WEST);
		west.setValue(Names.ATTR_DIRECTION, Names.DIR_WEST);
		s.addObject(west);
		
		// adding wall objects - relational objects point to directions - these could just have been string objects!
		ObjectInstance westWall = new ObjectInstance(wallClass, Names.OBJ_WALL_WEST);
		westWall.addRelationalTarget(Names.ATTR_WALL_DIRECTION, Names.OBJ_WEST);
		s.addObject(westWall);
		
		
		ObjectInstance northWall = new ObjectInstance(wallClass, Names.OBJ_WALL_NORTH);
		northWall.addRelationalTarget(Names.ATTR_WALL_DIRECTION, Names.OBJ_NORTH);
		s.addObject(northWall);
		
		ObjectInstance southWall = new ObjectInstance(wallClass, Names.OBJ_WALL_SOUTH);
		southWall.addRelationalTarget(Names.ATTR_WALL_DIRECTION, Names.OBJ_SOUTH);
		s.addObject(southWall);
		
		ObjectInstance eastWall = new ObjectInstance(wallClass, Names.OBJ_WALL_EAST);
		eastWall.addRelationalTarget(Names.ATTR_WALL_DIRECTION, Names.OBJ_EAST);
		s.addObject(eastWall);
		
		
		
//		System.out.println(s.getCompleteStateDescription());
		return s;
	}
	

}
