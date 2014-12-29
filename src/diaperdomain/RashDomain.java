package diaperdomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import burlap.oomdp.auxiliary.DomainGenerator;

import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;

import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.Attribute.AttributeType;
import burlap.behavior.statehashing.DiscreteStateHashFactory;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.pomdp.BeliefState;
import burlap.oomdp.singleagent.pomdp.BeliefStatistic;
import burlap.oomdp.singleagent.pomdp.ObservationFunction;
import burlap.oomdp.singleagent.pomdp.PODomain;
import burlap.oomdp.singleagent.pomdp.POEnvironment;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.auxiliary.StateEnumerator;
////import burlap.oomdp.singleagent.GroundedAction;
//import burlap.oomdp.singleagent.common.SinglePFTF;
//import burlap.behavior.singleagent.auxiliary.StateEnumerator;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.planning.commonpolicies.GreedyQPolicy;
import burlap.behavior.singleagent.pomdp.BeliefMDPPolicyAgent;
import burlap.behavior.singleagent.pomdp.POMDPEpisodeAnalysis;
import burlap.behavior.singleagent.pomdp.pbvi.PBVI;
import burlap.behavior.singleagent.pomdp.pomcp.LBLWPOMCP;
import burlap.behavior.singleagent.pomdp.pomcp.LWPOMCP;
import burlap.behavior.singleagent.pomdp.pomcp.MonteCarloNodeAgent;
import burlap.behavior.singleagent.pomdp.pomcp.POMCP;
//import burlap.behavior.singleagent.auxiliary
import burlap.oomdp.core.TerminalFunction;



public class RashDomain implements DomainGenerator {

	private static double noise = 0.20;
	private static int observationTypes = 4;
	private static int repeatedObservations = 1;

	//	protected List<Observation> observations = new ArrayList<Observation>();;
	//	protected Map<String, Observation> observationMap = new HashMap<String, Observation>();

	public RashDomain() {};
	
	
	public RashDomain(int repeatObs) {
		repeatedObservations = repeatObs;
	};


	@Override
	public Domain generateDomain() {
		PODomain domain = new PODomain();


		// First setting up mental states
		Attribute attrMentalState = new Attribute(domain,Names.ATTR_MENTAL_STATE,Attribute.AttributeType.RELATIONAL);

		// Setting up physical objects
		List<String> physicalObjectsList = new ArrayList<String>(){{add(Names.PO_TYPE_DIAPER);
		add(Names.PO_TYPE_WIPES);add(Names.PO_TYPE_OINTMENT);}};
		// Setting up container objects	
		List<String> containerList = new ArrayList<String>(){{add(Names.CT_TYPE_CHANGINGTABLE);
		add(Names.CT_TYPE_SIDETABLE);add(Names.CT_TYPE_TRASHCAN);}};

		// Is object Clean
		Attribute attrCleanliness = new Attribute(domain, Names.ATTR_CLEANLINESS,Attribute.AttributeType.DISC);
		attrCleanliness.setDiscValuesForRange(0, 1, 1);
		// Is object Manipulatable
		Attribute attrManipulable =  new Attribute(domain, Names.ATTR_MANIPULABLE, Attribute.AttributeType.DISC);
		attrManipulable.setDiscValuesForRange(0, 1, 1);
		// Is object a medicine
		Attribute attrMedicine = new Attribute(domain, Names.ATTR_MEDICINE, Attribute.AttributeType.DISC);
		attrMedicine.setDiscValuesForRange(0, 1, 1);
		//Which physical object
		Attribute attrPhysicalOb = new Attribute(domain,Names.ATTR_PHYSOBJ_TYPE,Attribute.AttributeType.DISC);
		attrPhysicalOb.setDiscValues(physicalObjectsList);
		//Which container object
		Attribute attrContainerOb = new Attribute(domain,Names.ATTR_CONTAINER_TYPE, Attribute.AttributeType.DISC);
		attrContainerOb.setDiscValues(containerList);

		// Relational attribute for physical objects
		Attribute attrContainer = new Attribute(domain,Names.ATTR_CONTAINER, Attribute.AttributeType.RELATIONAL);

		// Relational attribute for wearing
		Attribute attrWearing = new Attribute(domain, Names.ATTR_WEARING, Attribute.AttributeType.RELATIONAL);


		// Rash Attribute
		Attribute attrRash = new Attribute(domain, Names.ATTR_BABY_RASH,Attribute.AttributeType.DISC);
		attrRash.setDiscValuesForRange(0, 1, 1);


		// Relational attribute for containers
		Attribute attrContents = new Attribute (domain,Names.ATTR_CONTENTS,Attribute.AttributeType.MULTITARGETRELATIONAL);

		// adding object classes with their respective attributes
		ObjectClass mentalStateClass = new ObjectClass(domain, Names.CLASS_STATE);

		ObjectClass human = new ObjectClass(domain, Names.CLASS_HUMAN);
		human.addAttribute(attrMentalState);

		ObjectClass robot = new ObjectClass(domain, Names.CLASS_ROBOT);

		ObjectClass baby = new ObjectClass(domain, Names.CLASS_BABY);
		baby.addAttribute(attrWearing);
		baby.addAttribute(attrRash);

		ObjectClass physicalObject = new ObjectClass(domain,Names.CLASS_PHYSOBJ);
		physicalObject.addAttribute(attrContainer);
		physicalObject.addAttribute(attrMedicine);
		physicalObject.addAttribute(attrManipulable);
		physicalObject.addAttribute(attrCleanliness);
		physicalObject.addAttribute(attrPhysicalOb);

		ObjectClass containerObject = new ObjectClass(domain,Names.CLASS_CONTAINER);
		containerObject.addAttribute(attrContents);
		containerObject.addAttribute(attrContainerOb);

		// Actions 3 limited(grounded actions) in the first set bring ointment/ bring diaper/ null
		Action bringDiaper = new BringDiaper(Names.ACTION_BRING_DIAPER, domain);
		Action bringOintment = new BringOintment(Names.ACTION_BRING_OINTMENT, domain);
		Action nullAction = new NullAction(Names.ACTION_NULL, domain);
		//		Action nullAction = new nullAction(domain, Names.ACTION_NULL);
		//        Action nullAction = new burlap.oomdp.singleagent.common.NullAction("NOP", domain, "");
		//		GroundedAction bringDiaper = new GroundedAction(RashDomain.BringAction, new String[]{Names})

		//		for(int obsCount = 0;obsCount < repeatedObservations; obsCount++){
		////			System.out.println("was here");
		//			Observation startStateObs = new SimpleObservation(domain, Names.OBS_START+"#" + obsCount, Names.MS_TYPE_START );
		//			Observation rashObs = new SimpleObservation(domain, Names.OBS_RASH+"#" + obsCount, Names.MS_TYPE_RASH);
		//			Observation noRashObs = new SimpleObservation(domain, Names.OBS_NO_RASH+"#" + obsCount, Names.MS_TYPE_NO_RASH);
		//			Observation goalObs = new SimpleObservation(domain, Names.OBS_GOAL+"#" + obsCount,Names.MS_TYPE_GOAL);
		//		}


		// attributes for observations

		Attribute obAtt = new Attribute(domain, Names.ATTR_OBS, AttributeType.DISC);
		List<String> observationTypesList = new ArrayList<String>();
		observationTypesList.add(Names.OBS_GOAL);
		observationTypesList.add(Names.OBS_NO_RASH);
		observationTypesList.add(Names.OBS_RASH);
		observationTypesList.add(Names.OBS_START);
		String[] obsFull = new String[observationTypesList.size() * RashDomain.repeatedObservations];
		for(int i =0;i<RashDomain.repeatedObservations;i++){
			for(int j=0;j<observationTypesList.size();j++){
//				obsFull[i*RashDomain.repeatedObservations  + j] = observationTypesList.get(j) + "#" + i;
				obsFull[i*observationTypesList.size() + j] = observationTypesList.get(j) + "#" + i;
			}
		}
		obAtt.setDiscValues(obsFull);

		ObjectClass obClass = new ObjectClass(domain, Names.CLASS_OBSERVATION);
		obClass.addAttribute(obAtt);

		new EasyDiaperObservationFunction(domain, RashDomain.repeatedObservations, RashDomain.noise);


		StateEnumerator senum = new StateEnumerator(domain, new NameDependentStateHashFactory());
		senum.findReachableStatesAndEnumerate(RashDomain.getNewState(domain));
		System.out.println("num states enumerated: "+ senum.numStatesEnumerated());
		domain.setStateEnumerator(senum);



		//Propositional functions, one for goal, other for querying per state

		//				PropositionalFunction inGoalState = new GoalStatePropositionFunction(Names.PROP_IN_GOAL_STATE , domain);
		//		PropositionalFunction inStateQuery = new InStatePropositionFunction(Names.PROP_IN_QUERIED_STATE,domain);

		//				TerminalFunction tf = new SinglePFTF(inGoalState);
		return domain;
	}




	public static State getNewState(Domain d){
		// To Do create initial state
		State ps = new State();
		// objects diaper - clean/ dirty, ointment, 2 tables, one hamper, baby with rash or not, wearing old diaper,
		//human in start state

		// getting object classes
		ObjectClass humanClass = d.getObjectClass(Names.CLASS_HUMAN);
		ObjectClass babyClass = d.getObjectClass(Names.CLASS_BABY);
		ObjectClass containerClass = d.getObjectClass(Names.CLASS_CONTAINER);
		ObjectClass physicalObjectClass = d.getObjectClass(Names.CLASS_PHYSOBJ);
		ObjectClass mentalState = d.getObjectClass(Names.CLASS_STATE);
		// dunno what to do with class robot perhaps useful to make more realistic domains!

		// create objects
		ObjectInstance cleanDiaper = new ObjectInstance(physicalObjectClass, Names.OBJ_NEWDIAPER);
		setPhyObjects(cleanDiaper,Names.PO_TYPE_DIAPER,0,1,1);
		ObjectInstance dirtyDiaper = new ObjectInstance(physicalObjectClass, Names.OBJ_OLDDIAPER);
		setPhyObjects(dirtyDiaper, Names.PO_TYPE_DIAPER, 0, 0, 1);
		ObjectInstance ointment = new ObjectInstance(physicalObjectClass, Names.OBJ_OINTMENT);
		setPhyObjects(ointment,Names.PO_TYPE_OINTMENT,1,0,1);
		ObjectInstance changingTable = new ObjectInstance(containerClass, Names.OBJ_CHANGINGTABLE);
		createContainers(changingTable,Names.CT_TYPE_CHANGINGTABLE);
		// changing table is empty
		ObjectInstance sideTable = new ObjectInstance(containerClass,Names.OBJ_SIDETABLE);
		createContainers(sideTable,Names.CT_TYPE_SIDETABLE);
		// adding contents to side table alone 
		addContents(sideTable,cleanDiaper,ointment);
		ObjectInstance trashCan = new ObjectInstance(containerClass, Names.OBJ_TRASHCAN);
		createContainers(trashCan,Names.CT_TYPE_TRASHCAN);
		//trash can is empty at start

		//creating the mental states
		ObjectInstance startState = new ObjectInstance(mentalState, Names.MS_TYPE_START);
		ObjectInstance rashState = new ObjectInstance(mentalState, Names.MS_TYPE_RASH);
		ObjectInstance noRashState = new ObjectInstance(mentalState, Names.MS_TYPE_NO_RASH);
		ObjectInstance goalState = new ObjectInstance(mentalState, Names.MS_TYPE_GOAL);

		// creating human's mental state at start
		ObjectInstance human = new ObjectInstance(humanClass, Names.OBJ_HUMAN);
		human.addRelationalTarget(Names.ATTR_MENTAL_STATE, startState.getName());

		// creating a baby and setting its rashState - this rash state is temporary, this is to prevent 
		// no rash from being cut off during learning I guess.
		ObjectInstance baby = new ObjectInstance(babyClass, Names.OBJ_BABY);
		baby.addRelationalTarget(Names.ATTR_WEARING, Names.OBJ_OLDDIAPER);
		//		dirtyDiaper.addRelationalTarget(Names.ATTR_CONTAINER_TYPE, Names.OBJ_BABY);
		// 
		baby.setValue(Names.ATTR_BABY_RASH, 0);// this means initially there is not rash which meaningless

		addObjectsToState(ps, baby, human, dirtyDiaper, cleanDiaper, changingTable, sideTable, trashCan, ointment,
				startState, rashState, noRashState, goalState);


		//		StateParser sP = new StateJSONParser(d);
		//		System.out.println("RashDomain: State parsed output: "+ sP.stateToString(ps));
		return ps;
	}



	public static void setPhyObjects(ObjectInstance obj, String type, int medicine, int clean, int manipulable){
		obj.setValue(Names.ATTR_PHYSOBJ_TYPE, type);
		obj.setValue(Names.ATTR_CLEANLINESS, clean);
		obj.setValue(Names.ATTR_MANIPULABLE, manipulable);
		obj.setValue(Names.ATTR_MEDICINE, medicine);
		return;
	}

	public static void createContainers(ObjectInstance obj, String type){
		obj.setValue(Names.ATTR_CONTAINER_TYPE, type);
	}

	public static void addContents(ObjectInstance container, ObjectInstance... contents){
		for( ObjectInstance obj : contents){
			container.addRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(Names.ATTR_CONTAINER, container.getName());
		}
	}

	public static void addObjectsToState(State s, ObjectInstance... objects){
		for(ObjectInstance obj: objects){
			s.addObject(obj);
		}
	}

	// propositional functions
	//	public class GoalStatePropositionFunction extends PropositionalFunction{
	//		String goalStateName = Names.MS_TYPE_GOAL;
	//		public GoalStatePropositionFunction(String name, Domain d){
	//			super(name, d, new String[]{});
	//		}
	//		@Override
	//		public boolean isTrue(State s, String[] params){
	//			ObjectInstance human = s.getObject(Names.OBJ_HUMAN);
	//			String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];
	//			return mentalState.equals(goalStateName);
	//		}
	//
	//	}
	//
	//	public class InStatePropositionFunction extends PropositionalFunction{
	//		public InStatePropositionFunction(String name, Domain d){
	//			super(name, d, new String[]{Names.CLASS_HUMAN, Names.CLASS_MENTAL_STATE});
	//		}
	//		@Override
	//		public boolean isTrue(State s, String[] params){
	//			ObjectInstance human = s.getObject(params[0]);
	//			ObjectInstance stateQueried = s.getObject(params[1]);
	//			String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];
	//			return mentalState.equals(stateQueried.getName());
	//		}
	//	}




	public class EasyDiaperObservationFunction extends ObservationFunction{

		private double noise = 0.20;
		private int observationTypes = 4;
		private int repeatedObservations = 1;
		private List<State> allObservations = new ArrayList<State>();
		private Map<String, String> stateToObservationName = new HashMap<String,String>();
		private Map<String, String> observationNameToMentalStates = new HashMap<String,String>();


		public EasyDiaperObservationFunction(PODomain domain) {
			super(domain);
			this.initObservations();
		}

		public EasyDiaperObservationFunction(PODomain domain, int repeatObs) {
			super(domain);
			this.repeatedObservations=repeatObs;
			this.initObservations();
		}

		public EasyDiaperObservationFunction(PODomain domain, int repeatObs, double noise) {
			super(domain);
			this.repeatedObservations=repeatObs;
			this.noise = noise;
			this.initObservations();
		}

		@Override
		public List<State> getAllPossibleObservations() {
			return this.allObservations;
		}

		@Override
		public double getObservationProbability(State observation, State state,
				GroundedAction action) {
			// get the observation's attribute for value get its key compare to the mental state and return the idea probability
			//make sure goal state probabilities are counted right... here they are not
			String oVal = observation.getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS).replaceAll("#(.*)$", "");
			String obsMentalState = this.observationNameToMentalStates.get(oVal);

			String mentalState  = state.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);

			if(obsMentalState.equals(Names.MS_TYPE_GOAL) && obsMentalState.equals(mentalState)){
				return 1.0/this.repeatedObservations;
			}
			else if(obsMentalState.equals(Names.MS_TYPE_GOAL) && !obsMentalState.equals(mentalState)){
				return 0.0;
			}
			if(mentalState.equals(obsMentalState)){
				return (1.0-this.noise)/this.repeatedObservations;
			}
			else{
				return this.noise/((this.observationTypes-2)*this.repeatedObservations);
			}

			//			return this.noise/this.repeatedObservations;
		}

		protected void initObservations(){
			for(int i=0;i<this.repeatedObservations;i++){
				this.allObservations.add(startStateObs(i));
				this.allObservations.add(rashObs(i));
				this.allObservations.add(noRashObs(i));
				this.allObservations.add(goalObs(i));
			}
			// creating a forward key of state to observations
			this.stateToObservationName.put(Names.MS_TYPE_START, Names.OBS_START);
			this.stateToObservationName.put(Names.MS_TYPE_RASH, Names.OBS_RASH);
			this.stateToObservationName.put(Names.MS_TYPE_NO_RASH, Names.OBS_NO_RASH);
			this.stateToObservationName.put(Names.MS_TYPE_GOAL, Names.OBS_GOAL);

			//creating a reverse key of observations to keys
			this.observationNameToMentalStates.put(Names.OBS_START,Names.MS_TYPE_START);
			this.observationNameToMentalStates.put(Names.OBS_RASH, Names.MS_TYPE_RASH);
			this.observationNameToMentalStates.put(Names.OBS_NO_RASH, Names.MS_TYPE_NO_RASH);
			this.observationNameToMentalStates.put(Names.OBS_GOAL, Names.MS_TYPE_GOAL);
		}

		protected State startStateObs(int i){
			State obsStart = new State();
			ObjectInstance obL = new ObjectInstance(this.domain.getObjectClass(Names.CLASS_OBSERVATION), Names.OBSERVATION_OBJ);
			obL.setValue(Names.ATTR_OBS, Names.OBS_START+ "#"+i);
			obsStart.addObject(obL);
			return obsStart;
		}

		protected State rashObs(int i){
			State obsRash = new State();
			ObjectInstance obL = new ObjectInstance(this.domain.getObjectClass(Names.CLASS_OBSERVATION), Names.OBSERVATION_OBJ);
			obL.setValue(Names.ATTR_OBS, Names.OBS_RASH+ "#"+i);
			obsRash.addObject(obL);
			return obsRash;
		}

		protected State noRashObs(int i){
			State obsNoRash = new State();
			ObjectInstance obL = new ObjectInstance(this.domain.getObjectClass(Names.CLASS_OBSERVATION), Names.OBSERVATION_OBJ);
			obL.setValue(Names.ATTR_OBS, Names.OBS_NO_RASH+"#"+i);
			obsNoRash.addObject(obL);
			return obsNoRash;
		}

		protected State goalObs(int i){
			State obsGoal = new State();
			ObjectInstance obL = new ObjectInstance(this.domain.getObjectClass(Names.CLASS_OBSERVATION), Names.OBSERVATION_OBJ);
			obL.setValue(Names.ATTR_OBS, Names.OBS_GOAL+ "#"+i);
			obsGoal.addObject(obL);
			return obsGoal;
		}

		@Override
		public boolean isTerminalObservation(State observation) {
			String oVal = observation.getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS).replaceAll("#(.*)$", "");
			String obsMentalState = this.observationNameToMentalStates.get(oVal);
			return obsMentalState.equals(Names.MS_TYPE_GOAL);
		}


	}





	// Actions
	/*
	public class BringAction extends Action{
		public BringAction(String name,Domain d){
			super(name, d, new String[]{Names.CLASS_PHYSOBJ,Names.CLASS_CONTAINER});
		}
		@Override
		public boolean applicableInState(State st, String[] params){
			ObjectInstance object = st.getObject(params[0]);
			return object.getDiscValForAttribute(Names.ATTR_MANIPULABLE)==1;
		}
		@Override
		protected State performActionHelper(State s, String[] params){
			POMDPState ps = new POMDPState(s);
			POMDPDomain PDomain = (POMDPDomain) domain;
			ObjectInstance obj = ps.getObject(params[0]);
			ObjectInstance containerTo = ps.getObject(params[1]);
			ObjectInstance containerFrom = ps.getObject((String) obj.getAllRelationalTargets(Names.ATTR_CONTAINER).toArray()[0]);

			containerFrom.removeRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			containerTo.addRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(Names.ATTR_CONTAINER, containerTo.getName());

			//human actions: This is the  human transition function basically
			ObjectInstance human=ps.getObject(Names.OBJ_HUMAN);
			ObjectInstance baby=ps.getObject(Names.OBJ_BABY);
			String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];

			switch(mentalState){

			case(Names.MS_TYPE_START):{
				// if human in start state, doesn't need anything brought, hence stays in same state
				//set Observations based on mental state of output
//				PDomain.getObservation(mentalState);
				ps.setObservation(PDomain.getObservation(Names.OBS_START));
				break;
			}
			case(Names.MS_TYPE_RASH):{
				// if in rash state needs ointment, if ointment then updating
				// rash state, observation
				if(obj.getName().equals(Names.OBJ_OINTMENT) && containerTo.getName().equals(Names.OBJ_CHANGINGTABLE)){
				human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_NO_RASH);
				baby.setValue(Names.ATTR_BABY_RASH, 0);
				// need to set observations still
				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH));
				}
				//else we change nothing
				else{
				ps.setObservation(PDomain.getObservation(Names.OBS_RASH));
				}
				break;

			}
			case(Names.MS_TYPE_NO_RASH):{

				if(obj.getName().equals(Names.OBJ_NEWDIAPER) && containerTo.getName().equals(Names.OBJ_CHANGINGTABLE)){
					human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_GOAL);
					baby.addRelationalTarget(Names.ATTR_WEARING, Names.OBJ_NEWDIAPER);
					containerTo.removeRelationalTarget(Names.ATTR_CONTENTS, Names.OBJ_NEWDIAPER);
					obj.removeRelationalTarget(Names.ATTR_CONTAINER, containerTo.getName());
					// no new relational targets have been added to the new diaper which seems wrong 
					// need to set observations here?
					ps.setObservation(PDomain.getObservation(Names.OBS_GOAL));

				}
				else{
				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH));
				}
				break;
			}
			case(Names.MS_TYPE_GOAL):{
				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL));
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
	 */

	public class BringDiaper extends Action{
		public BringDiaper(String name, Domain d){
			super(name, d, new String[]{});
		}
		@Override
		public boolean applicableInState(State st, String[] params){
			ObjectInstance object = st.getObject(Names.OBJ_NEWDIAPER);
			return object.getDiscValForAttribute(Names.ATTR_MANIPULABLE)==1;
		}
		@Override
		protected State performActionHelper(State s, String[] params){
			State ps = new State(s);
			//			PODomain PDomain = (PODomain) domain;

			ObjectInstance baby=ps.getObject(Names.OBJ_BABY);
			if(!baby.getStringValForAttribute(Names.ATTR_WEARING).equals(Names.OBJ_NEWDIAPER)){
				ObjectInstance obj = ps.getObject(Names.OBJ_NEWDIAPER);
				ObjectInstance containerTo = ps.getObject(Names.OBJ_CHANGINGTABLE);
				ObjectInstance containerFrom = ps.getObject((String) obj.getAllRelationalTargets(Names.ATTR_CONTAINER).toArray()[0]);

				containerFrom.removeRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
				containerTo.addRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
				obj.addRelationalTarget(Names.ATTR_CONTAINER, containerTo.getName());


				//human actions: This is the  human transition function basically
				ObjectInstance human=ps.getObject(Names.OBJ_HUMAN);
				String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];

				switch(mentalState){

				case(Names.MS_TYPE_START):{
					// if human in start state, doesn't need anything brought, hence stays in same state
					//set Observations based on mental state of output
					//				PDomain.getObservation(mentalState);

					/*
				double tempDouble=Math.random();
				if(tempDouble>noise){
				java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
					switch(tempNoise){
					case(0):{
						java.util.Random randomTemp = new java.util.Random();
						ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						java.util.Random randomTemp = new java.util.Random();
						ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: bring diaper observation mess up 1");
					}
				}

					 */
					break;
				}
				case(Names.MS_TYPE_RASH):{
					// if in rash state do not need diapers so do nothing
					//				if(obj.getName().equals(Names.OBJ_OINTMENT) && containerTo.getName().equals(Names.OBJ_CHANGINGTABLE)){
					//				human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_NO_RASH);
					//				baby.setValue(Names.ATTR_BABY_RASH, 0);
					// need to set observations still
					//				}
					// need to set observations still
					//else we change nothing
					//				ps.setObservation(PDomain.getObservation(Names.OBS_RASH));
					/*
				double tempDouble=Math.random();
				if(tempDouble>noise){
					java.util.Random randomTemp = new java.util.Random();
					ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
					java.util.Random randomTemp = new java.util.Random();
					switch(tempNoise){
					case(0):{
						ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						ps.setObservation(PDomain.getObservation(Names.OBS_START+ "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: bring diaper observation mess up 2");
					}
				}
					 */
					break;

				}
				case(Names.MS_TYPE_NO_RASH):{

					if(obj.getName().equals(Names.OBJ_NEWDIAPER) && containerTo.getName().equals(Names.OBJ_CHANGINGTABLE)){
						human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_GOAL);
						baby.addRelationalTarget(Names.ATTR_WEARING, Names.OBJ_NEWDIAPER);
						containerTo.removeRelationalTarget(Names.ATTR_CONTENTS, Names.OBJ_NEWDIAPER);
						obj.removeRelationalTarget(Names.ATTR_CONTAINER, containerTo.getName());
						// no new relational targets have been added to the new diaper which seems wrong 
						// need to set observations here?


						//				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL));

						/*ps.setObservation(PDomain.getObservation(Names.OBS_GOAL));
						 */
					}
					else{
						/*
						 * 	double tempDouble=Math.random();
					if(tempDouble>noise){
						java.util.Random randomTemp = new java.util.Random();
						ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + randomTemp.nextInt(repeatedObservations)));

					}
					else{
						int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
						java.util.Random randomTemp = new java.util.Random();
						switch(tempNoise){
						case(0):{
							ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						case(1):{
							ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						default:System.out.println("RashDomain: bring diaper observation mess up 3");
						}
					}
						 */
					}

					break;
				}
				case(Names.MS_TYPE_GOAL):{
					//				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL));
					/*
				java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL + "#" + randomTemp.nextInt(repeatedObservations)));
					 */
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
			//			POMDPDomain PDomain = (POMDPDomain) domain;
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
			ObjectInstance object = st.getObject(Names.OBJ_OINTMENT);
			return object.getDiscValForAttribute(Names.ATTR_MANIPULABLE)==1;
		}
		@Override
		protected State performActionHelper(State s, String[] params){
			State ps = new State(s);
			//			PODomain PDomain = (PODomain) domain;
			ObjectInstance obj = ps.getObject(Names.OBJ_OINTMENT);
			ObjectInstance containerTo = ps.getObject(Names.OBJ_CHANGINGTABLE);
			ObjectInstance containerFrom = ps.getObject((String) obj.getAllRelationalTargets(Names.ATTR_CONTAINER).toArray()[0]);

			containerFrom.removeRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			containerTo.addRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			obj.addRelationalTarget(Names.ATTR_CONTAINER, containerTo.getName());

			//human actions: This is the  human transition function basically
			ObjectInstance human=ps.getObject(Names.OBJ_HUMAN);
			ObjectInstance baby=ps.getObject(Names.OBJ_BABY);
			String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];

			switch(mentalState){

			case(Names.MS_TYPE_START):{
				// if human in start state, doesn't need anything brought, hence stays in same state
				//set Observations based on mental state of output
				//				PDomain.getObservation(mentalState);
				//				ps.setObservation(PDomain.getObservation(Names.OBS_START));
				/*
				double tempDouble=Math.random();
				java.util.Random randomTemp = new java.util.Random();
				if(tempDouble>noise){
				ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
					switch(tempNoise){
					case(0):{
						ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH+ "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: bring ointment observation mess up 4");
					}
				}
				 */
				break;
			}
			case(Names.MS_TYPE_RASH):{
				// if in rash state do not need diapers so do nothing
				if(obj.getName().equals(Names.OBJ_OINTMENT) && containerTo.getName().equals(Names.OBJ_CHANGINGTABLE)){
					human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_NO_RASH);
					baby.setValue(Names.ATTR_BABY_RASH, 0);
					//				 need to set observations still

					// need to set observations still
					//else we change nothing
					//				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH));
					/*
				double tempDouble=Math.random();
				java.util.Random randomTemp = new java.util.Random();
				if(tempDouble>noise){
				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
					switch(tempNoise){
					case(0):{
						ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: bring ointment observation mess up 4");
					}
				}
					 */
				}
				else{
					/*
					double tempDouble=Math.random();
					java.util.Random randomTemp = new java.util.Random();
					if(tempDouble>noise){
					ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
					}
					else{
						int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
						switch(tempNoise){
						case(0):{
							ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						case(1):{
							ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						default:System.out.println("RashDomain: bring ointment observation mess up 4");
						}
					}
					 */
				}
				break;

			}
			case(Names.MS_TYPE_NO_RASH):{
				// if rash solved no need to get ointment...
				//				if(obj.getName().equals(Names.OBJ_NEWDIAPER) && containerTo.getName().equals(Names.OBJ_CHANGINGTABLE)){
				//					human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_GOAL);
				//					baby.addRelationalTarget(Names.ATTR_WEARING, Names.OBJ_NEWDIAPER);
				//					containerTo.removeRelationalTarget(Names.ATTR_CONTENTS, Names.OBJ_NEWDIAPER);
				//					obj.removeRelationalTarget(Names.ATTR_CONTAINER, containerTo.getName());
				//					// no new relational targets have been added to the new diaper which seems wrong 
				//					// need to set observations here?
				//					
				//				}
				//				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH));


				/*
				double tempDouble=Math.random();
				java.util.Random randomTemp = new java.util.Random();
				if(tempDouble>noise){
				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes-2));
					switch(tempNoise){
					case(0):{
						ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: bring ointment observation mess up 4");
					}
				}
				 */

				break;
			}
			case(Names.MS_TYPE_GOAL):{
				//				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL));
				/*java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL + "#" + randomTemp.nextInt(repeatedObservations)));
				 */
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
			//			POMDPDomain PDomain = (POMDPDomain) domain;
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
			State ps = new State(s);
			//			PODomain PDomain = (PODomain) domain;
			// robot does nothing
			ObjectInstance human = ps.getObject(Names.OBJ_HUMAN);
			ObjectInstance baby = ps.getObject(Names.OBJ_BABY);
			String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];
			switch(mentalState){
			case(Names.MS_TYPE_START):{
				// remove diapers, check for rash, change mental state based on rash, put oldDiaper on table
				ObjectInstance oldDiaper = ps.getObject(Names.OBJ_OLDDIAPER);
				baby.clearRelationalTargets(Names.ATTR_WEARING);
				oldDiaper.addRelationalTarget(Names.ATTR_CONTAINER, Names.OBJ_CHANGINGTABLE);
				ObjectInstance changingTable = ps.getObject(Names.OBJ_CHANGINGTABLE);
				changingTable.addRelationalTarget(Names.ATTR_CONTENTS, oldDiaper.getName());
				double tempRand = Math.random();

				if (tempRand < 0.5){
					// baby has rash
					baby.setValue(Names.ATTR_BABY_RASH, 1);
					human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_RASH);
					//					ps.setObservation(pd.getObservation(Names.OBS_RASH));\
					/*
					double tempDouble=Math.random();
					if(tempDouble>noise){
						java.util.Random randomTemp = new java.util.Random();
						ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
					}
					else{
						int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
						java.util.Random randomTemp = new java.util.Random();
						switch(tempNoise){
						case(0):{
							ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						case(1):{
							ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						default:System.out.println("RashDomain: null action observation mess up 4");
						}
					}
					 */
				}
				else{
					baby.setValue(Names.ATTR_BABY_RASH, 0);
					human.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_NO_RASH);
					//					ps.setObservation(pd.getObservation(Names.OBS_NO_RASH));

					/*
					double tempDouble=Math.random();
					if(tempDouble>noise){
						java.util.Random randomTemp = new java.util.Random();
						ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
					}
					else{
						java.util.Random randomTemp = new java.util.Random();
						int tempNoise = (int) Math.floor(Math.random()*(observationTypes-2));
						switch(tempNoise){
						case(0):{
							ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						case(1):{
							ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
							break;
						}
						default:System.out.println("RashDomain: null action observation mess up 4");
						}
					}
					 */

				}
				// add observations

				break;
			}
			case(Names.MS_TYPE_RASH):{
				//				ps.setObservation(pd.getObservation(Names.OBS_RASH));

				/*
				double tempDouble=Math.random();
				if(tempDouble>noise){
					java.util.Random randomTemp = new java.util.Random();
					ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					java.util.Random randomTemp = new java.util.Random();
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
					switch(tempNoise){
					case(0):{
						ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: null action observation mess up 4");
					}
				}
				 */
				break;
			}
			case(Names.MS_TYPE_NO_RASH):{
				//				ps.setObservation(pd.getObservation(Names.OBS_NO_RASH));

				/*
				double tempDouble=Math.random();
				if(tempDouble>noise){
					java.util.Random randomTemp = new java.util.Random();
					ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
				}
				else{
					java.util.Random randomTemp = new java.util.Random();
					int tempNoise = (int) Math.floor(Math.random()*(observationTypes-2));
					switch(tempNoise){
					case(0):{
						ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					case(1):{
						ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
						break;
					}
					default:System.out.println("RashDomain: null action observation mess up 4");
					}
				}
				 */
				break;
			}
			case(Names.MS_TYPE_GOAL):{
				//				ps.setObservation(pd.getObservation(Names.OBS_GOAL));
				/*java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_GOAL + "#" + randomTemp.nextInt(repeatedObservations)));
				 */
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
			//			POMDPDomain PDomain = (POMDPDomain) domain;
			List<TransitionProbability> transitionList = new ArrayList<TransitionProbability>();
			ObjectInstance human = s.getObject(Names.OBJ_HUMAN);
			String mentalState = human.getStringValForAttribute(Names.ATTR_MENTAL_STATE);
			//			List<State> stateList = PDomain.getAllStates();
			if(mentalState.equals(Names.MS_TYPE_START)){
				State nextStateRash = performAction(s, params);
				// updating so it reflects a rash state
				ObjectInstance humanRash = nextStateRash.getObject(Names.OBJ_HUMAN);
				ObjectInstance babyRash = nextStateRash.getObject(Names.OBJ_BABY);
				babyRash.setValue(Names.ATTR_BABY_RASH, 1);
				humanRash.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_RASH);


				State nextStateNoRash = performAction(s, params);
				// updating so it shows a no rash state
				ObjectInstance humanNoRash = nextStateNoRash.getObject(Names.OBJ_HUMAN);
				ObjectInstance babyNoRash = nextStateNoRash.getObject(Names.OBJ_BABY);
				babyNoRash.setValue(Names.ATTR_BABY_RASH, 0);
				humanNoRash.addRelationalTarget(Names.ATTR_MENTAL_STATE, Names.MS_TYPE_NO_RASH);

				transitionList.add(new TransitionProbability(nextStateRash,0.5));

				transitionList.add(new TransitionProbability(nextStateNoRash,0.5));

			}
			else{
				State nextState = performAction(s,params);
				transitionList.add(new TransitionProbability(nextState,1.0));
			}
			return transitionList;
		}

	}



	/*

	public static POMDPState getObservation(POMDPState ps, GroundedAction ga, POMDPDomain PDomain){
		//		s.setObservation(o)
		String mentalState = ps.getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
		switch(mentalState){
		case(Names.MS_TYPE_START):{
			//				ps.setObservation(dom.getObservation(Names.OBS_START));
			double tempDouble=Math.random();
			if(tempDouble>noise){
				java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_START+ "#" + randomTemp.nextInt(repeatedObservations)));
			}
			else{
				java.util.Random randomTemp = new java.util.Random();
				int tempNoise = (int) Math.floor(Math.random()*(observationTypes-2));
				switch(tempNoise){
				case(0):{
					ps.setObservation(PDomain.getObservation(Names.OBS_RASH+ "#" + randomTemp.nextInt(repeatedObservations)));
					break;
				}
				case(1):{
					ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH+ "#" + randomTemp.nextInt(repeatedObservations)));
					break;
				}
				default:System.out.println("RashDomain: get observation mess up 4");
				}
			}

		}

		break;
		case(Names.MS_TYPE_RASH):{
			//				s.setObservation(dom.getObservation(Names.OBS_RASH));
			double tempDouble=Math.random();
			if(tempDouble>noise){
				java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
			}
			else{
				java.util.Random randomTemp = new java.util.Random();
				int tempNoise = (int) Math.floor(Math.random()*(observationTypes -2));
				switch(tempNoise){
				case(0):{
					ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
					break;
				}
				case(1):{
					ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
					break;
				}
				default:System.out.println("RashDomain: get observation mess up 4");
				}
			}
			break;
		}
		case(Names.MS_TYPE_NO_RASH):{
			//				s.setObservation(dom.getObservation(Names.OBS_NO_RASH));
			double tempDouble=Math.random();
			if(tempDouble>noise){
				java.util.Random randomTemp = new java.util.Random();
				ps.setObservation(PDomain.getObservation(Names.OBS_NO_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
			}
			else{
				java.util.Random randomTemp = new java.util.Random();
				int tempNoise = (int) Math.floor(Math.random()*(observationTypes-2));
				switch(tempNoise){
				case(0):{
					ps.setObservation(PDomain.getObservation(Names.OBS_RASH + "#" + randomTemp.nextInt(repeatedObservations)));
					break;
				}
				case(1):{
					ps.setObservation(PDomain.getObservation(Names.OBS_START + "#" + randomTemp.nextInt(repeatedObservations)));
					break;
				}
				default:System.out.println("RashDomain: get observation mess up 4");
				}
			}
			break;
		}
		case(Names.MS_TYPE_GOAL):{
			//			
			java.util.Random randomTemp = new java.util.Random();
			ps.setObservation(PDomain.getObservation(Names.OBS_GOAL + "#" + randomTemp.nextInt(repeatedObservations)));

			break;
		}
		default:
			break;
		}


		return ps;
	}
	 */

	public static BeliefState getInitialBeliefState(PODomain domain){
		BeliefState bs = new BeliefState(domain);
		//		bs.initializeBeliefsUniformly();
		int count = 0;
		double[] beliefVector = bs.getBeliefVector();
		StateEnumerator senum = domain.getStateEnumerator();
		for(int i=0;i<senum.numStatesEnumerated();i++){
			State s = senum.getStateForEnumertionId(i);
			ObjectInstance human = s.getObject(Names.OBJ_HUMAN);
			String mentalState = (String) human.getAllRelationalTargets(Names.ATTR_MENTAL_STATE).toArray()[0];
			if(mentalState.equals(Names.MS_TYPE_START)){
				count++;
				beliefVector[i] = 1;
			}
			else{
				beliefVector[i]=0;
			}
		}
		if(count>0){
			for(int i=0;i<senum.numStatesEnumerated();i++){
				beliefVector[i]=beliefVector[i]/count;
			}
		}
		else{
			System.out.println("RashDomain: Domain not initialized correctly");
			beliefVector = null;
		}

		bs.setBeliefCollection(beliefVector);
		return bs;
	}


	public static void main(String [] args){

		int numIt = 1000;

		String testOf = "LWPOMCP";
		
		for(int obspow2 = 0;obspow2<8;obspow2++){

		int noObservations = (int)Math.pow(4, obspow2);
		double discountForPlanning = 0.95;

		List<Double> PBVIArray = new ArrayList<Double>();
		List<Double> POMCPArray = new ArrayList<Double>();
		List<Double> LWPOMCPArray = new ArrayList<Double>();
		List<Double> LBLWPOMCPArray = new ArrayList<Double>();

		List<Double> PBVITimeArray = new ArrayList<Double>();
		List<Double> POMCPTimeArray = new ArrayList<Double>();
		List<Double> LWPOMCPTimeArray = new ArrayList<Double>();
		List<Double> LBLWPOMCPTimeArray = new ArrayList<Double>();

		List<Double> PBVIDiscountedArray = new ArrayList<Double>();
		List<Double> POMCPDiscountedArray = new ArrayList<Double>();
		List<Double> LWPOMCPDiscountedArray = new ArrayList<Double>();
		List<Double> LBLWPOMCPDiscountedArray = new ArrayList<Double>();

		for(int mainCount = 0;mainCount<numIt;mainCount++){

			System.out.println("run number: " + mainCount);

			RashDomain rdGen = new RashDomain(noObservations);
			PODomain rd = (PODomain) rdGen.generateDomain();

			RewardFunction rf = new RashDomainRewardFunction();
			TerminalFunction tf = new RashDomainTerminalFunction();
			BeliefState bs = RashDomain.getInitialBeliefState(rd);


			long startTime;
			long totalTime;

			POEnvironment env = new POEnvironment(rd, rf, tf);
			env.setCurMPDStateTo(bs.sampleStateFromBelief());
			POMDPEpisodeAnalysis ea;

			/* PBVI things that work*/
			if(testOf.equals("PBVI")){
				startTime = System.currentTimeMillis();
				PBVI pbvi = new PBVI(rd,rf,tf,discountForPlanning, new NameDependentStateHashFactory(),10,10,4);

				//		bs.initializeBeliefsUniformly();
				System.out.println("Begining pbvi planning.");
				pbvi.planFromBeliefStatistic(bs);
				System.out.println("End pbvi planning.");

				Policy p = new GreedyQPolicy(pbvi);




				BeliefMDPPolicyAgent PBVIagent = new BeliefMDPPolicyAgent(rd, p);
				PBVIagent.setEnvironment(env);
				PBVIagent.setBeliefState(bs);
				ea = PBVIagent.actUntilTerminalOrMaxSteps(20);

				totalTime = System.currentTimeMillis() - startTime;

				for(int i = 0; i < ea.numTimeSteps()-1; i++){
					String tval = ea.getState(i).getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
					double tempRW = ea.getReward(i+1);
					String obsVal = ea.getObservation(i).getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS);
					System.out.println(tval + ": " + ea.getAction(i).toString() + " , Observation: " + obsVal + ", reward: " +tempRW );
				}
				PBVIArray.add(ea.getDiscountedReturn(1.0));
				PBVIDiscountedArray.add(ea.getDiscountedReturn(discountForPlanning));
				PBVITimeArray.add((double)totalTime);
			}


			if(testOf.equals("POMCP")){


				System.out.println("POMCP Results");
				startTime = System.currentTimeMillis();
				POMCP pomcpPlanner = new POMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 5.0, 32);
				//		LWPOMCP pomcpPlanner = new LWPOMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 5.0, 128);
				//		LBLWPOMCP pomcpPlanner = new LBLWPOMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 3.0, 32, 8);
				pomcpPlanner.planFromBeliefStatistic(bs);
				env = new POEnvironment(rd, rf, tf);

				MonteCarloNodeAgent mcAgent = new MonteCarloNodeAgent(pomcpPlanner);
				env.setCurMPDStateTo(bs.sampleStateFromBelief());
				mcAgent.setEnvironment(env);

				ea=null;

				ea = mcAgent.actUntilTerminalOrMaxSteps(20);

				totalTime = System.currentTimeMillis() - startTime;

				for(int i = 0; i < ea.numTimeSteps()-1; i++){
					String tval = ea.getState(i).getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
					double tempRW = ea.getReward(i+1);
					String obsVal = ea.getObservation(i).getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS);
					System.out.println(tval + ": " + ea.getAction(i).toString() + " , Observation: " + obsVal + ", reward: " +tempRW );
				}

				POMCPArray.add(ea.getDiscountedReturn(1.0));
				POMCPDiscountedArray.add(ea.getDiscountedReturn(discountForPlanning));
				POMCPTimeArray.add((double)totalTime);
			}



			if(testOf.equals("LWPOMCP")){
				System.out.println("LWPOMCP Results");
				startTime = System.currentTimeMillis();
				//			POMCP pomcpPlanner = new POMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 20.0, 32);
				LWPOMCP lwpomcpPlanner = new LWPOMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 10.0, 32);
				//		LBLWPOMCP pomcpPlanner = new LBLWPOMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 3.0, 32, 8);
				lwpomcpPlanner.planFromBeliefStatistic(bs);
				env = new POEnvironment(rd, rf, tf);

				MonteCarloNodeAgent mcAgent = new MonteCarloNodeAgent(lwpomcpPlanner);
				env.setCurMPDStateTo(bs.sampleStateFromBelief());
				mcAgent.setEnvironment(env);

				ea=null;

				ea = mcAgent.actUntilTerminalOrMaxSteps(20);

				totalTime = System.currentTimeMillis() - startTime;

				for(int i = 0; i < ea.numTimeSteps()-1; i++){
					String tval = ea.getState(i).getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
					double tempRW = ea.getReward(i+1);
					String obsVal = ea.getObservation(i).getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS);
					System.out.println(tval + ": " + ea.getAction(i).toString() + " , Observation: " + obsVal + ", reward: " +tempRW );
				}

				LWPOMCPArray.add(ea.getDiscountedReturn(1.0));
				LWPOMCPDiscountedArray.add(ea.getDiscountedReturn(discountForPlanning));
				LWPOMCPTimeArray.add((double)totalTime);
			}

			if(testOf.equals("LBLWPOMCP")){
				System.out.println("LBLWPOMCP Results");
				startTime = System.currentTimeMillis();
				//			POMCP pomcpPlanner = new POMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 20.0, 32);
				//		LWPOMCP pomcpPlanner = new LWPOMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 5.0, 128);
				LBLWPOMCP lblwpomcpPlanner = new LBLWPOMCP(rd, rf, tf, 0.95, new NameDependentStateHashFactory(), 88, 5.0, 32, 8);
				lblwpomcpPlanner.planFromBeliefStatistic(bs);
				env = new POEnvironment(rd, rf, tf);

				MonteCarloNodeAgent mcAgent = new MonteCarloNodeAgent(lblwpomcpPlanner);
				env.setCurMPDStateTo(bs.sampleStateFromBelief());
				mcAgent.setEnvironment(env);

				ea=null;

				ea = mcAgent.actUntilTerminalOrMaxSteps(20);

				totalTime = System.currentTimeMillis() - startTime;

				for(int i = 0; i < ea.numTimeSteps()-1; i++){
					String tval = ea.getState(i).getObject(Names.OBJ_HUMAN).getStringValForAttribute(Names.ATTR_MENTAL_STATE);
					double tempRW = ea.getReward(i+1);
					String obsVal = ea.getObservation(i).getFirstObjectOfClass(Names.CLASS_OBSERVATION).getStringValForAttribute(Names.ATTR_OBS);
					System.out.println(tval + ": " + ea.getAction(i).toString() + " , Observation: " + obsVal + ", reward: " +tempRW );
				}

				LBLWPOMCPArray.add(ea.getDiscountedReturn(1.0));
				LBLWPOMCPDiscountedArray.add(ea.getDiscountedReturn(discountForPlanning));
				LBLWPOMCPTimeArray.add((double)totalTime);
			}





		}

		if(testOf.equals("PBVI")){

		System.out.println("PBVI Results complete run: num observations " + noObservations *4);
		System.out.println("Average Reward: " + meanReward(PBVIArray) + " with confidence interval: " + confInterval(PBVIArray));
		System.out.println("Average Discounted Reward: " + meanReward(PBVIDiscountedArray) + " with confidence interval: " + confInterval(PBVIDiscountedArray));
		System.out.println("Average Time: " + meanReward(PBVITimeArray) + " with confidence interval: " + confInterval(PBVITimeArray));
		}



		if(testOf.equals("POMCP")){
		System.out.println("POMCP Results complete run: num observations " + noObservations *4);
		System.out.println("Average Reward: " + meanReward(POMCPArray) + " with confidence interval: " + confInterval(POMCPArray));
		System.out.println("Average Discounted Reward: " + meanReward(POMCPDiscountedArray) + " with confidence interval: " + confInterval(POMCPDiscountedArray));
		System.out.println("Average Time: " + meanReward(POMCPTimeArray) + " with confidence interval: " + confInterval(POMCPTimeArray));

		}
		
		
		if(testOf.equals("LWPOMCP")){
		System.out.println("LWPOMCP Results complete run: num observations " + noObservations *4);
		System.out.println("Average Reward: " + meanReward(LWPOMCPArray) + " with confidence interval: " + confInterval(LWPOMCPArray));
		System.out.println("Average Discounted Reward: " + meanReward(LWPOMCPDiscountedArray) + " with confidence interval: " + confInterval(LWPOMCPDiscountedArray));
		System.out.println("Average Time: " + meanReward(LWPOMCPTimeArray) + " with confidence interval: " + confInterval(LWPOMCPTimeArray));

		}
		
		if(testOf.equals("LBLWPOMCP")){
		System.out.println("LBLWPOMCP Results complete run: num observations " + noObservations *4);
		System.out.println("Average Reward: " + meanReward(LBLWPOMCPArray) + " with confidence interval: " + confInterval(LBLWPOMCPArray));
		System.out.println("Average Discounted Reward: " + meanReward(LBLWPOMCPDiscountedArray) + " with confidence interval: " + confInterval(LBLWPOMCPDiscountedArray));
		System.out.println("Average Time: " + meanReward(LBLWPOMCPTimeArray) + " with confidence interval: " + confInterval(LBLWPOMCPTimeArray));

		}
		 
		
		}


	}

	public static double meanReward(List<Double> rewardList){
		double sum=0.0;
		for(Double rwd:rewardList){
			sum+=rwd;
		}
		return sum/rewardList.size();
	}

	public static double stdDev(List<Double> rwdList){
		double mean = meanReward(rwdList);
		double sqSum = 0.0;
		for(double rwd : rwdList){
			sqSum += (rwd - mean)*(rwd - mean);
		}
		return Math.sqrt(sqSum/rwdList.size());
	}

	public static double confInterval(List<Double> rwdList){
		double mean = meanReward(rwdList);
		double sqSum = 0.0;
		for(double rwd : rwdList){
			sqSum += (rwd - mean)*(rwd - mean);
		}
		return 1.96*Math.sqrt(sqSum/rwdList.size())/Math.sqrt(rwdList.size());
	}





}


