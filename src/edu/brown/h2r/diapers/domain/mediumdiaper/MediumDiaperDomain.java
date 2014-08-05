package edu.brown.h2r.diapers.domain.mediumdiaper;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

import java.util.List;
import java.util.ArrayList;

/**
 * This is a domain generator for a simplified but still testable diaper domain.  It creates a world
 * containing two tables, a hamper, a dresser, and a human caregiver.  Its observations are parametrized
 * by an array of strings representing spoken word.
 */
public class MediumDiaperDomain implements DomainGenerator {

	/**
	 * Creates the domain.
	 * @return a new MediumDiaperDomain as a POMDPDomain
	 */
	public Domain generateDomain() {

		//Set up the domain object, forwarding calls through to static methods of the MediumDiaperDomain class
		Domain domain = new POMDPDomain() {
			@Override public POMDPState sampleInitialState() { return MediumDiaperDomain.sampleInitialStates(this); }
			@Override public boolean isTerminal(POMDPState s) { return MediumDiaperDomain.isTerminal(this, s); }
			@Override public List<POMDPState> getAllInitialStates() { return new ArrayList<POMDPState>(); }
		};

		//Create attributes for the caregiver's mental state, containers and contents, openness, and the state of having a rash
		Attribute mentalState = new Attribute(domain, Names.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);
		Attribute contents    = new Attribute(domain, Names.ATTR_CONTENTS, Attribute.AttributeType.MULTITARGETRELATIONAL);
		Attribute acontainer  = new Attribute(domain, Names.ATTR_CONTAINER, Attribute.AttributeType.RELATIONAL);
		Attribute open        = new Attribute(domain, Names.ATTR_OPEN, Attribute.AttributeType.DISC);
		Attribute rash        = new Attribute(domain, Names.ATTR_RASH, Attribute.AttributeType.DISC);

		//Openness and rashness are boolean attributes
		open.setDiscValuesForRange(0, 1, 1);
		rash.setDiscValuesForRange(0, 1, 1);

		//Create object classes for the human caregiver, the baby, contents and containers, and goals (mental state objects)
		ObjectClass human     = new ObjectClass(domain, Names.CLASS_HUMAN);
		ObjectClass baby      = new ObjectClass(domain, Names.CLASS_BABY);
		ObjectClass content   = new ObjectClass(domain, Names.CLASS_CONTENT);
		ObjectClass container = new ObjectClass(domain, Names.CLASS_CONTAINER);
		ObjectClass goal      = new ObjectClass(domain, Names.CLASS_GOAL);

		//Assign attributes to their proper classes
		human.addAttribute(mentalState);
		baby.addAttribute(rash);
		container.addAttribute(contents);
		container.addAttribute(open);
		content.addAttribute(acontainer);

		//Create actions for bringing, asking, opening, and waiting
		Action bring          = new Actions.BringAction(domain, Names.ACTION_BRING);
		Action ask            = new Actions.AskAction(domain, Names.ACTION_ASK);
		Action openAction     = new Actions.OpenAction(domain, Names.ACTION_OPEN);
		Action wait           = new Actions.WaitAction(domain, Names.ACTION_WAIT);

		//Create propositionalfunctions for finding an object at a location, detecting diaper rash, and detecting whether a container is open
		PropositionalFunction findFun = new PropFunctions.FindPropFun(domain, Names.PROP_FIND);
		PropositionalFunction rashFun = new PropFunctions.RashPropFun(domain, Names.PROP_RASH);
		PropositionalFunction openFun = new PropFunctions.OpenPropFun(domain, Names.PROP_OPEN);

		return domain;
	}

	public static boolean isTerminal(Domain d, POMDPState s) {
		return s.getObject(Names.OBJ_CAREGIVER).getStringValForAttribute(Names.ATTR_MENTAL_STATE).equals("done");
	}

	/**
	 * Samples the initial distribution over states and returns ONE state object.
	 *
	 * @param d		The domain in which the state should be valid
	 * @return 		A properly sampled initial POMDPState
	 */
	public static POMDPState sampleInitialStates(Domain d) {
		POMDPState s = new POMDPState();

		//Retrieve object classes from the domain.
		ObjectClass containerClass   = d.getObjectClass(Names.CLASS_CONTAINER);
		ObjectClass babyClass        = d.getObjectClass(Names.CLASS_BABY);
		ObjectClass contentClass     = d.getObjectClass(Names.CLASS_CONTENT);
		ObjectClass humanClass       = d.getObjectClass(Names.CLASS_HUMAN);

		//Create all the objects 
		ObjectInstance caregiver     = new ObjectInstance(humanClass, Names.OBJ_CAREGIVER);
		ObjectInstance baby          = new ObjectInstance(babyClass, Names.OBJ_BABY);
		ObjectInstance ointment      = new ObjectInstance(contentClass, Names.OBJ_OINTMENT);
		ObjectInstance oldClothes    = new ObjectInstance(contentClass, Names.OBJ_OLD_CLOTHES);
		ObjectInstance newClothes    = new ObjectInstance(contentClass, Names.OBJ_NEW_CLOTHES);
		ObjectInstance changingTable = new ObjectInstance(containerClass, Names.OBJ_CHANGING_TABLE);
	    ObjectInstance hamper        = new ObjectInstance(containerClass, Names.OBJ_HAMPER);
		ObjectInstance sideTable     = new ObjectInstance(containerClass, Names.OBJ_SIDE_TABLE);
	    ObjectInstance dresser       = new ObjectInstance(containerClass, Names.OBJ_DRESSER);

		//Set the 	proper values for objects' attributes
		changingTable.setValue(Names.ATTR_OPEN, 1);
	    hamper.setValue(Names.ATTR_OPEN, 1);
		sideTable.setValue(Names.ATTR_OPEN, 1);
		dresser.setValue(Names.ATTR_OPEN, 0);
		baby.setValue(Names.ATTR_RASH, new java.util.Random().nextBoolean() ? 1 : 0);

		//Place contents in the proper initial container
		placeObject(newClothes, dresser);
		placeObject(oldClothes, changingTable);
		placeObject(ointment, sideTable);

		//Add objects to the state, and have the caregiver decide on its mental state
		addObjects(s, caregiver, baby, oldClothes, newClothes, changingTable, hamper, sideTable, dresser, ointment);
		caregiverThink(d, s);

		//Et voila
		return s;
	}

	/**
	 * A helper method for adding multiple ObjectInstances to a State object.
	 *
	 * @param s		The state object to which the objects should be added
	 * @param objs	An array of object to be added to the state
	 */
	public static void addObjects(State s, ObjectInstance... objs) {
		for(ObjectInstance object : objs) { s.addObject(object); }
	}

	/**
	 * A helper method for placing an object in a container
	 *
	 * @param content		The object to be placed into the container
	 * @param container		The container into which the object should be placed
	 */
	public static void placeObject(ObjectInstance content, ObjectInstance container) {
		content.addRelationalTarget(Names.ATTR_CONTAINER, container.getName());
		container.addRelationalTarget(Names.ATTR_CONTENTS, content.getName());
	}

	/**
	 * A helper method for the caregiver to update its mental state based on the state of the 
	 * environment.
	 *
	 * @param domain		The domain in which the caregiver inhabits
	 * @param s				The current state of the world
	 */
	public static boolean caregiverThink(Domain d, State s) {
		ObjectInstance caregiver = s.getObject(Names.OBJ_CAREGIVER);
		ObjectClass goalClass = d.getObjectClass(Names.CLASS_GOAL);
		boolean arranged = false;

		ObjectInstance myGoal = null;

		//If the old clothing is still at the changing table, it needs to be at the hamper
		if(!d.getPropFunction(Names.PROP_FIND).isTrue(s, new String[]{Names.OBJ_HAMPER, Names.OBJ_OLD_CLOTHES})) {
			myGoal = new ObjectInstance(goalClass, Names.OBJ_OLD_CLOTHES + ":" + Names.OBJ_HAMPER);
		
		//If the baby has a rash and the lotion is not at the changing table, the lotion needs to be at the changing table
		} else if(d.getPropFunction(Names.PROP_RASH).isTrue(s, new String[]{}) && !d.getPropFunction(Names.PROP_FIND).isTrue(s, new String[]{Names.OBJ_CHANGING_TABLE, Names.OBJ_OINTMENT})) {
			myGoal = new ObjectInstance(goalClass, Names.OBJ_OINTMENT + ":" + Names.OBJ_CHANGING_TABLE);

		//If the new clothes aren't at the changing table, they need to be at the changing table
		} else if(!d.getPropFunction(Names.PROP_FIND).isTrue(s, new String[]{Names.OBJ_CHANGING_TABLE, Names.OBJ_NEW_CLOTHES})) {
			myGoal = new ObjectInstance(goalClass, Names.OBJ_NEW_CLOTHES + ":" + Names.OBJ_CHANGING_TABLE);

		//Otherwise, the problem is solved
		} else {
			myGoal = new ObjectInstance(goalClass, "null");
			arranged = true;
		}

		caregiver.addRelationalTarget(Names.ATTR_MENTAL_STATE, myGoal.getName());
		return arranged;
	}
}
