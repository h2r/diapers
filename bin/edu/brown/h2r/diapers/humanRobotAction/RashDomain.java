package edu.brown.h2r.diapers.humanRobotAction;

import java.util.ArrayList;
import java.util.List;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import burlap.oomdp.auxiliary.DomainGenerator;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.TransitionProbability;
import burlap.behavior.statehashing.NameDependentStateHashFactory;


import burlap.oomdp.singleagent.Action;
//import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.common.SinglePFTF;

//import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.singleagent.auxiliary.StateReachability;
//import burlap.behavior.singleagent.auxiliary

public class RashDomain implements DomainGenerator {

	public RashDomain() {};
	
	@Override
	public Domain generateDomain() {
		Domain domain = new POMDPDomain(){
		@Override
		public POMDPState getExampleState() {
			return RashDomain.getNewState(this);
		}
		@Override
		public List<State> getAllStates(){
			return RashDomain.listAllStates(this);
		}
		};
		
		// First setting up mental states
		Attribute attrMentalState = new Attribute(domain,S.ATTR_MENTAL_STATE,Attribute.AttributeType.RELATIONAL);
		
		// Setting up physical objects
		List<String> physicalObjectsList = new ArrayList<String>(){{add(S.PO_TYPE_DIAPER);
			add(S.PO_TYPE_WIPES);add(S.PO_TYPE_OINTMENT);}};
		// Setting up container objects	
		List<String> containerList = new ArrayList<String>(){{add(S.CT_TYPE_CHANGINGTABLE);
		add(S.CT_TYPE_SIDETABLE);add(S.CT_TYPE_TRASHCAN);}};
		
		// Is object Clean
		Attribute attrCleanliness = new Attribute(domain, S.ATTR_CLEANLINESS,Attribute.AttributeType.DISC);
		attrCleanliness.setDiscValuesForRange(0, 1, 1);
		// Is object Manipulatable
		Attribute attrManipulable =  new Attribute(domain, S.ATTR_MANIPULABLE, Attribute.AttributeType.DISC);
		attrManipulable.setDiscValuesForRange(0, 1, 1);
		// Is object a medicine
		Attribute attrMedicine = new Attribute(domain, S.ATTR_MEDICINE, Attribute.AttributeType.DISC);
		attrMedicine.setDiscValuesForRange(0, 1, 1);
		//Which physical object
		Attribute attrPhysicalOb = new Attribute(domain,S.ATTR_PHYSOBJ_TYPE,Attribute.AttributeType.DISC);
		attrPhysicalOb.setDiscValues(physicalObjectsList);
		//Which container object
		Attribute attrContainerOb = new Attribute(domain,S.ATTR_CONTAINER_TYPE, Attribute.AttributeType.DISC);
		attrContainerOb.setDiscValues(containerList);
		
		// Relational attribute for physical objects
		Attribute attrContainer = new Attribute(domain,S.ATTR_CONTAINER, Attribute.AttributeType.RELATIONAL);
		
        // Relational attribute for wearing
		Attribute attrWearing = new Attribute(domain, S.ATTR_WEARING, Attribute.AttributeType.RELATIONAL);
		
		
		// Rash Attribute
		Attribute attrRash = new Attribute(domain, S.ATTR_BABY_RASH,Attribute.AttributeType.DISC);
		attrRash.setDiscValuesForRange(0, 1, 1);
		
		
		// Relational attribute for containers
		Attribute attrContents = new Attribute (domain,S.ATTR_CONTENTS,Attribute.AttributeType.MULTITARGETRELATIONAL);
		
		// adding object classes with their respective attributes
		ObjectClass mentalStateClass = new ObjectClass(domain, S.CLASS_STATE);
		
		ObjectClass human = new ObjectClass(domain, S.CLASS_HUMAN);
		human.addAttribute(attrMentalState);
		
		ObjectClass robot = new ObjectClass(domain, S.CLASS_ROBOT);
		
		ObjectClass baby = new ObjectClass(domain, S.CLASS_BABY);
		baby.addAttribute(attrWearing);
		baby.addAttribute(attrRash);
		
		ObjectClass physicalObject = new ObjectClass(domain,S.CLASS_PHYSOBJ);
		physicalObject.addAttribute(attrContainer);
		physicalObject.addAttribute(attrMedicine);
		physicalObject.addAttribute(attrManipulable);
		physicalObject.addAttribute(attrCleanliness);
		physicalObject.addAttribute(attrPhysicalOb);
		
		ObjectClass containerObject = new ObjectClass(domain,S.CLASS_CONTAINER);
		containerObject.addAttribute(attrContents);
		containerObject.addAttribute(attrContainerOb);
		
		// Actions 3 limited(grounded actions) in the first set bring ointment/ bring diaper/ null
		Action bringDiaper = new BringDiaper(S.ACTION_BRING_DIAPER, domain);
		Action bringOintment = new BringOintment(S.ACTION_BRING_OINTMENT, domain);
		Action nullAction = new NullAction(S.ACTION_NULL, domain);
//		Action nullAction = new nullAction(domain, S.ACTION_NULL);
//        Action nullAction = new burlap.oomdp.singleagent.common.NullAction("NOP", domain, "");
//		GroundedAction bringDiaper = new GroundedAction(RashDomain.BringAction, new String[]{S})
		
		
		//Propositional functions, one for goal, other for querying per state
		
		PropositionalFunction inGoalState = new GoalStatePropositionFunction(S.PROP_IN_GOAL_STATE , domain);
		PropositionalFunction inStateQuery = new InStatePropositionFunction(S.PROP_IN_QUERIED_STATE,domain);
		
//		TerminalFunction tf = new SinglePFTF(inGoalState);
		return domain;
	}
	
	
	
	
	public static POMDPState getNewState(Domain d){
		// To Do create initial state
		POMDPState ps = new POMDPState();
		// objects diaper - clean/ dirty, ointment, 2 tables, one hamper, baby with rash or not, wearing old diaper,
		//human in start state
		
		// getting object classes
		ObjectClass humanClass = d.getObjectClass(S.CLASS_HUMAN);
		ObjectClass babyClass = d.getObjectClass(S.CLASS_BABY);
		ObjectClass containerClass = d.getObjectClass(S.CLASS_CONTAINER);
		ObjectClass physicalObjectClass = d.getObjectClass(S.CLASS_PHYSOBJ);
		ObjectClass mentalState = d.getObjectClass(S.CLASS_STATE);
		// dunno what to do with class robot perhaps useful to make more realistic domains!
		
		// create objects
		ObjectInstance cleanDiaper = new ObjectInstance(physicalObjectClass, S.OBJ_NEWDIAPER);
		setPhyObjects(cleanDiaper,S.PO_TYPE_DIAPER,0,1,1);
		ObjectInstance dirtyDiaper = new ObjectInstance(physicalObjectClass, S.OBJ_OLDDIAPER);
		setPhyObjects(dirtyDiaper, S.PO_TYPE_DIAPER, 0, 0, 1);
		ObjectInstance ointment = new ObjectInstance(physicalObjectClass, S.OBJ_OINTMENT);
		setPhyObjects(ointment,S.PO_TYPE_OINTMENT,1,0,1);
		ObjectInstance changingTable = new ObjectInstance(containerClass, S.OBJ_CHANGINGTABLE);
		createContainers(changingTable,S.CT_TYPE_CHANGINGTABLE);
		// changing table is empty
		ObjectInstance sideTable = new ObjectInstance(containerClass,S.OBJ_SIDETABLE);
		createContainers(sideTable,S.CT_TYPE_SIDETABLE);
		// adding contents to side table alone 
		addContents(sideTable,cleanDiaper,ointment);
		ObjectInstance trashCan = new ObjectInstance(containerClass, S.OBJ_TRASHCAN);
		createContainers(trashCan,S.CT_TYPE_TRASHCAN);
		//trash can is empty at start
		
		//creating the mental states
		ObjectInstance startState = new ObjectInstance(mentalState, S.MS_TYPE_START);
		ObjectInstance rashState = new ObjectInstance(mentalState, S.MS_TYPE_RASH);
		ObjectInstance noRashState = new ObjectInstance(mentalState, S.MS_TYPE_NO_RASH);
		ObjectInstance goalState = new ObjectInstance(mentalState, S.MS_TYPE_GOAL);
		
		// creating human's mental state at start
		ObjectInstance human = new ObjectInstance(humanClass, S.OBJ_HUMAN);
		human.addRelationalTarget(S.ATTR_MENTAL_STATE, startState.getName());
		
		// creating a baby and setting its rashState - this rash state is temporary, this is to prevent 
		// no rash from being cut off during learning I guess.
		ObjectInstance baby = new ObjectInstance(babyClass, S.OBJ_BABY);
		baby.addRelationalTarget(S.ATTR_WEARING, S.OBJ_OLDDIAPER);
//		dirtyDiaper.addRelationalTarget(S.ATTR_CONTAINER_TYPE, S.OBJ_BABY);
		// 
		baby.setValue(S.ATTR_BABY_RASH, 0);// this means initially there is not rash which meaningless
		
		addObjectsToState(ps, baby, human, dirtyDiaper, cleanDiaper, changingTable, sideTable, trashCan, ointment,
				startState, rashState, noRashState, goalState);
		
		
		return ps;
	}
	
	public static List<State> listAllStates(POMDPDomain d){
		//This is just a bad idea, need a better way to define transition functions
		
		class GoalStatePropositionFunctionLocal extends PropositionalFunction{
			String goalStateName = S.MS_TYPE_GOAL;
			GoalStatePropositionFunctionLocal(String name, Domain d){
				super(name, d, new String[]{});
			}
			@Override
			public boolean isTrue(State s, String[] params){
				ObjectInstance human = s.getObject(S.OBJ_HUMAN);
				String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
				return mentalState.equals(goalStateName);
			}
			
		}
		
		
		PropositionalFunction inGoalState = new GoalStatePropositionFunctionLocal(S.PROP_IN_QUERIED_STATE, d);
//		TerminalFunction tf = new SinglePFTF(inGoalState);
		State s = d.getExampleState();
//		System.out.println("RashDomain: state print");
//		System.out.println(s.getCompleteStateDescription());
		NameDependentStateHashFactory hashFactory = new NameDependentStateHashFactory();
		return StateReachability.getReachableStates(s,d,hashFactory);
	}
	
	public static void setPhyObjects(ObjectInstance obj, String type, int medicine, int clean, int manipulable){
		obj.setValue(S.ATTR_PHYSOBJ_TYPE, type);
		obj.setValue(S.ATTR_CLEANLINESS, clean);
		obj.setValue(S.ATTR_MANIPULABLE, manipulable);
		obj.setValue(S.ATTR_MEDICINE, medicine);
		return;
	}
	
	public static void createContainers(ObjectInstance obj, String type){
		obj.setValue(S.ATTR_CONTAINER_TYPE, type);
	}
	
	public static void addContents(ObjectInstance container, ObjectInstance... contents){
		for( ObjectInstance obj : contents){
			container.addRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(S.ATTR_CONTAINER, container.getName());
		}
	}
	
	public static void addObjectsToState(State s, ObjectInstance... objects){
		for(ObjectInstance obj: objects){
			s.addObject(obj);
		}
	}
	
	// propositional functions
	public class GoalStatePropositionFunction extends PropositionalFunction{
		String goalStateName = S.MS_TYPE_GOAL;
		public GoalStatePropositionFunction(String name, Domain d){
			super(name, d, new String[]{});
		}
		@Override
		public boolean isTrue(State s, String[] params){
			ObjectInstance human = s.getObject(S.OBJ_HUMAN);
			String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
			return mentalState.equals(goalStateName);
		}
		
	}
	
	public class InStatePropositionFunction extends PropositionalFunction{
		public InStatePropositionFunction(String name, Domain d){
			super(name, d, new String[]{S.CLASS_HUMAN, S.CLASS_MENTAL_STATE});
		}
		@Override
		public boolean isTrue(State s, String[] params){
			ObjectInstance human = s.getObject(params[0]);
			ObjectInstance stateQueried = s.getObject(params[1]);
			String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
			return mentalState.equals(stateQueried.getName());
		}
	}
	
	
	
    // Actions
	public class BringAction extends Action{
		public BringAction(String name,Domain d){
			super(name, d, new String[]{S.CLASS_PHYSOBJ,S.CLASS_CONTAINER});
		}
		@Override
		public boolean applicableInState(State st, String[] params){
			ObjectInstance object = st.getObject(params[0]);
			return object.getDiscValForAttribute(S.ATTR_MANIPULABLE)==1;
		}
		@Override
		protected State performActionHelper(State s, String[] params){
			POMDPState ps = new POMDPState(s);
//			POMDPDomain PDomain = (POMDPDomain) domain;
			ObjectInstance obj = ps.getObject(params[0]);
			ObjectInstance containerTo = ps.getObject(params[1]);
			ObjectInstance containerFrom = ps.getObject((String) obj.getAllRelationalTargets(S.ATTR_CONTAINER).toArray()[0]);
			
			containerFrom.removeRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			containerTo.addRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(S.ATTR_CONTAINER, containerTo.getName());
			
			//human actions: This is the  human transition function basically
			ObjectInstance human=ps.getObject(S.OBJ_HUMAN);
			ObjectInstance baby=ps.getObject(S.OBJ_BABY);
			String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
					
			switch(mentalState){
			
			case(S.MS_TYPE_START):{
				// if human in start state, doesn't need anything brought, hence stays in same state
				//set Observations based on mental state of output
//				PDomain.getObservation(mentalState);
				break;
			}
			case(S.MS_TYPE_RASH):{
				// if in rash state needs ointment, if ointment then updating
				// rash state, observation
				if(obj.getName().equals(S.OBJ_OINTMENT) && containerTo.getName().equals(S.OBJ_CHANGINGTABLE)){
				human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_NO_RASH);
				baby.setValue(S.ATTR_BABY_RASH, 0);
				// need to set observations still
				}
				//else we change nothing
				break;
				
			}
			case(S.MS_TYPE_NO_RASH):{
				
				if(obj.getName().equals(S.OBJ_NEWDIAPER) && containerTo.getName().equals(S.OBJ_CHANGINGTABLE)){
					human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_GOAL);
					baby.addRelationalTarget(S.ATTR_WEARING, S.OBJ_NEWDIAPER);
					containerTo.removeRelationalTarget(S.ATTR_CONTENTS, S.OBJ_NEWDIAPER);
					obj.removeRelationalTarget(S.ATTR_CONTAINER, containerTo.getName());
					// no new relational targets have been added to the new diaper which seems wrong 
					// need to set observations here?
					
				}
				break;
			}
			case(S.MS_TYPE_GOAL):{
				break;
			}
			default:{
				break;
			}
			}
			return ps;
			
		}
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			POMDPDomain PDomain = (POMDPDomain) domain;
			List<TransitionProbability> transitionList = new ArrayList<TransitionProbability>();
			List<State> stateList = PDomain.getAllStates();
			State realNextState = performAction(s, params);
			for(State sNext : stateList){
				if (realNextState.equals(sNext)){
					transitionList.add(new TransitionProbability(sNext,1.0));
				}
				else{
					transitionList.add(new TransitionProbability(sNext, 0.0));
				}
			}
			return transitionList;
		}
	}
	
	public class BringDiaper extends Action{
		public BringDiaper(String name, Domain d){
			super(name, d, new String[]{});
		}
		@Override
		public boolean applicableInState(State st, String[] params){
			ObjectInstance object = st.getObject(S.OBJ_NEWDIAPER);
			return object.getDiscValForAttribute(S.ATTR_MANIPULABLE)==1;
		}
		@Override
		protected State performActionHelper(State s, String[] params){
			POMDPState ps = new POMDPState(s);
//			POMDPDomain PDomain = (POMDPDomain) domain;
			ObjectInstance human=ps.getObject(S.OBJ_HUMAN);
			ObjectInstance baby=ps.getObject(S.OBJ_BABY);
			if(!baby.getStringValForAttribute(S.ATTR_WEARING).equals(S.OBJ_NEWDIAPER)){
			ObjectInstance obj = ps.getObject(S.OBJ_NEWDIAPER);
			ObjectInstance containerTo = ps.getObject(S.OBJ_CHANGINGTABLE);
			ObjectInstance containerFrom = ps.getObject((String) obj.getAllRelationalTargets(S.ATTR_CONTAINER).toArray()[0]);
			
			containerFrom.removeRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			containerTo.addRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(S.ATTR_CONTAINER, containerTo.getName());
			
			
			//human actions: This is the  human transition function basically
			
			String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
					
			switch(mentalState){
			
			case(S.MS_TYPE_START):{
				// if human in start state, doesn't need anything brought, hence stays in same state
				//set Observations based on mental state of output
//				PDomain.getObservation(mentalState);
				break;
			}
			case(S.MS_TYPE_RASH):{
				// if in rash state do not need diapers so do nothing
//				if(obj.getName().equals(S.OBJ_OINTMENT) && containerTo.getName().equals(S.OBJ_CHANGINGTABLE)){
//				human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_NO_RASH);
//				baby.setValue(S.ATTR_BABY_RASH, 0);
				// need to set observations still
//				}
				// need to set observations still
				//else we change nothing
				break;
				
			}
			case(S.MS_TYPE_NO_RASH):{
				
				if(obj.getName().equals(S.OBJ_NEWDIAPER) && containerTo.getName().equals(S.OBJ_CHANGINGTABLE)){
					human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_GOAL);
					baby.addRelationalTarget(S.ATTR_WEARING, S.OBJ_NEWDIAPER);
					containerTo.removeRelationalTarget(S.ATTR_CONTENTS, S.OBJ_NEWDIAPER);
					obj.removeRelationalTarget(S.ATTR_CONTAINER, containerTo.getName());
					// no new relational targets have been added to the new diaper which seems wrong 
					// need to set observations here?
					
				}
				break;
			}
			case(S.MS_TYPE_GOAL):{
				break;
			}
			default:{
				break;
			}
			}
			}
			return ps;
			
		}
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			POMDPDomain PDomain = (POMDPDomain) domain;
			List<TransitionProbability> transitionList = new ArrayList<TransitionProbability>();
//			List<State> stateList = PDomain.getAllStates();
			State realNextState = performAction(s, params);
//			for(State sNext : stateList){
//				if (realNextState.equals(sNext)){
					transitionList.add(new TransitionProbability(realNextState,1.0));
//				}
//				else{
//					transitionList.add(new TransitionProbability(sNext, 0.0));
//				}
//			}
			return transitionList;
		}
	}
	
	public class BringOintment extends Action{
		public BringOintment(String name, Domain d){
			super(name, d, new String[]{});
		}
		@Override
		public boolean applicableInState(State st, String[] params){
			ObjectInstance object = st.getObject(S.OBJ_OINTMENT);
			return object.getDiscValForAttribute(S.ATTR_MANIPULABLE)==1;
		}
		@Override
		protected State performActionHelper(State s, String[] params){
			POMDPState ps = new POMDPState(s);
//			POMDPDomain PDomain = (POMDPDomain) domain;
			ObjectInstance obj = ps.getObject(S.OBJ_OINTMENT);
			ObjectInstance containerTo = ps.getObject(S.OBJ_CHANGINGTABLE);
			ObjectInstance containerFrom = ps.getObject((String) obj.getAllRelationalTargets(S.ATTR_CONTAINER).toArray()[0]);
			
			containerFrom.removeRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			containerTo.addRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(S.ATTR_CONTAINER, containerTo.getName());
			
			//human actions: This is the  human transition function basically
			ObjectInstance human=ps.getObject(S.OBJ_HUMAN);
			ObjectInstance baby=ps.getObject(S.OBJ_BABY);
			String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
					
			switch(mentalState){
			
			case(S.MS_TYPE_START):{
				// if human in start state, doesn't need anything brought, hence stays in same state
				//set Observations based on mental state of output
//				PDomain.getObservation(mentalState);
				break;
			}
			case(S.MS_TYPE_RASH):{
				// if in rash state do not need diapers so do nothing
				if(obj.getName().equals(S.OBJ_OINTMENT) && containerTo.getName().equals(S.OBJ_CHANGINGTABLE)){
				human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_NO_RASH);
				baby.setValue(S.ATTR_BABY_RASH, 0);
//				 need to set observations still
				}
				// need to set observations still
				//else we change nothing
				break;
				
			}
			case(S.MS_TYPE_NO_RASH):{
				// if rash solved no need to get ointment...
//				if(obj.getName().equals(S.OBJ_NEWDIAPER) && containerTo.getName().equals(S.OBJ_CHANGINGTABLE)){
//					human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_GOAL);
//					baby.addRelationalTarget(S.ATTR_WEARING, S.OBJ_NEWDIAPER);
//					containerTo.removeRelationalTarget(S.ATTR_CONTENTS, S.OBJ_NEWDIAPER);
//					obj.removeRelationalTarget(S.ATTR_CONTAINER, containerTo.getName());
//					// no new relational targets have been added to the new diaper which seems wrong 
//					// need to set observations here?
//					
//				}
				
				break;
			}
			case(S.MS_TYPE_GOAL):{
				break;
				}
			default:{
				break;
			}
			}
			return ps;
			
		}
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			POMDPDomain PDomain = (POMDPDomain) domain;
			List<TransitionProbability> transitionList = new ArrayList<TransitionProbability>();
//			List<State> stateList = PDomain.getAllStates();
			State realNextState = performAction(s, params);
//			for(State sNext : stateList){
//				if (realNextState.equals(sNext)){
					transitionList.add(new TransitionProbability(realNextState,1.0));
//				}
//				else{
//					transitionList.add(new TransitionProbability(sNext, 0.0));
//				}
//			}
			return transitionList;
		}
	}
	
	public class NullAction extends Action{
		public NullAction(String name, Domain d){
		super(name, d, new String[]{});
		}
		@Override
		public boolean applicableInState(State s, String[] params){
			return true;
		}
		@Override
		public State performActionHelper(State s, String[] params){
			POMDPState ps = new POMDPState(s);
//			POMDPDomain pd = (POMDPDomain) domain;
			// robot does nothing
			ObjectInstance human = ps.getObject(S.OBJ_HUMAN);
			ObjectInstance baby = ps.getObject(S.OBJ_BABY);
			String mentalState = (String) human.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
			switch(mentalState){
			case(S.MS_TYPE_START):{
				// remove diapers, check for rash, change mental state based on rash, put oldDiaper on table
				ObjectInstance oldDiaper = ps.getObject(S.OBJ_OLDDIAPER);
				baby.clearRelationalTargets(S.ATTR_WEARING);
				oldDiaper.addRelationalTarget(S.ATTR_CONTAINER, S.OBJ_CHANGINGTABLE);
				ObjectInstance changingTable = ps.getObject(S.OBJ_CHANGINGTABLE);
				changingTable.addRelationalTarget(S.ATTR_CONTENTS, oldDiaper.getName());
				double tempRand = Math.random();
				if (tempRand < 0.5){
					// baby has rash
					baby.setValue(S.ATTR_BABY_RASH, 1);
					human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_RASH);
					
				}
				else{
					baby.setValue(S.ATTR_BABY_RASH, 0);
					human.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_NO_RASH);
				}
				// add observations
				
				break;
			}
			case(S.MS_TYPE_RASH):{
				break;
			}
			case(S.MS_TYPE_NO_RASH):{
				break;
			}
			case(S.MS_TYPE_GOAL):{
				break;
			}
			default:{
				break;
			}
			}
			
			
			return ps;
		}
		
		@Override
		public List<TransitionProbability> getTransitions(State s, String[] params){
			POMDPDomain PDomain = (POMDPDomain) domain;
			List<TransitionProbability> transitionList = new ArrayList<TransitionProbability>();
			ObjectInstance human = s.getObject(S.OBJ_HUMAN);
			String mentalState = human.getStringValForAttribute(S.ATTR_MENTAL_STATE);
//			List<State> stateList = PDomain.getAllStates();
			if(mentalState.equals(S.MS_TYPE_START)){
			State nextStateRash = performAction(s, params);
			// updating so it reflects a rash state
			ObjectInstance humanRash = nextStateRash.getObject(S.OBJ_HUMAN);
			ObjectInstance babyRash = nextStateRash.getObject(S.OBJ_BABY);
			babyRash.setValue(S.ATTR_BABY_RASH, 1);
			humanRash.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_RASH);
			
			
			State nextStateNoRash = performAction(s, params);
			// updating so it shows a no rash state
			ObjectInstance humanNoRash = nextStateNoRash.getObject(S.OBJ_HUMAN);
			ObjectInstance babyNoRash = nextStateNoRash.getObject(S.OBJ_BABY);
			babyNoRash.setValue(S.ATTR_BABY_RASH, 0);
			humanNoRash.addRelationalTarget(S.ATTR_MENTAL_STATE, S.MS_TYPE_NO_RASH);
			
//			for(State sNext : stateList){
//				if (nextStateRash.equals(sNext)){
					transitionList.add(new TransitionProbability(nextStateRash,0.5));
//				}
//				else if(nextStateNoRash.equals(sNext)){
					transitionList.add(new TransitionProbability(nextStateNoRash,0.5));
//				}
//				else{
//					transitionList.add(new TransitionProbability(sNext, 0.0));
//				}
//			}
			}
			else{
				State nextState = performAction(s,params);
				transitionList.add(new TransitionProbability(nextState,1.0));
			}
			return transitionList;
		}
		
	}
	
	
}


