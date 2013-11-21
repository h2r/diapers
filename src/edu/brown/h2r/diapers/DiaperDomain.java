package edu.brown.h2r.diapers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.lang.ArrayIndexOutOfBoundsException;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import burlap.oomdp.singleagent.explorer.VisualExplorer;

public class DiaperDomain implements DomainGenerator {



/* ============================================================================
 * Definition of String constants
 * ========================================================================= */

	private static final String 	CLASS_HUMAN 				= "ClassHuman";
	private static final String		CLASS_ROBOT 				= "ClassRobot";
	private static final String		CLASS_CONTAINER				= "ClassContainer";
	private static final String		CLASS_PHYSOBJ				= "ClassPhysobj";
	private static final String		CLASS_STATE 				= "ClassState";
	private static final String		CLASS_REFEREE				= "ClassReferee";

	private static final String		ATTR_NEEDS_UPDATE			= "AttrNeedsUpdate";
	private static final String		ATTR_MENTAL_STATE 			= "AttrMentalState";
	private static final String		ATTR_PHYSOBJ_TYPE			= "AttrPOType";
	private static final String		ATTR_CONTAINER_TYPE			= "AttrCTType";
	private static final String		ATTR_CLEANLINESS			= "AttrCleanliness";
	private static final String		ATTR_MANIPULABLE			= "AttrManipulable";
	private static final String		ATTR_CONTENTS 				= "AttrContents";
	private static final String		ATTR_CONTAINER 				= "AttrContainer";

	private static final String		PO_TYPE_DIAPER				= "POTypeDiaper";
	private static final String		PO_TYPE_WIPES				= "POTypeWipes";
	private static final String 	PO_TYPE_CLOTHING			= "POTypeClothing";

	private static final String		CT_TYPE_CHANGINGTABLE		= "CTTypeChangingTable";
	private static final String		CT_TYPE_SIDETABLE			= "CTTypeSideTable";
	private static final String		CT_TYPE_HAMPER				= "CTTypeHamper";
	private static final String		CT_TYPE_DRESSER				= "CTTypeDresser";
	private static final String		CT_TYPE_TRASHCAN			= "CTTypeTrashCan";

	private static final String		ACTION_BRING			= "Bring";
	private static final String		ACTION_UPDATE			= "Update";
	private static final String		ACTION_WAIT				= "Wait";
	
	private static final String		OBJ_CAREGIVER			= "Caregiver";
	private static final String		OBJ_ROBOT				= "Robot";
	private static final String		OBJ_OLDDIAPER			= "OldDiaper";
	private static final String		OBJ_NEWDIAPER			= "NewDiaper";
	private static final String		OBJ_OLDPANTS			= "OldPants";
	private static final String		OBJ_NEWPANTS			= "NewPants";
	private static final String		OBJ_OLDSHIRT			= "OldShirt";
	private static final String		OBJ_NEWSHIRT			= "NewShirt";
	private static final String		OBJ_WIPES				= "Wipes";
	private static final String		OBJ_CHANGINGTABLE 		= "ChangingTable";
	private static final String		OBJ_SIDETABLE			= "SideTable";
	private static final String		OBJ_HAMPER				= "Hamper";
	private static final String		OBJ_DRESSER				= "Dresser";
	private static final String		OBJ_TRASHCAN			= "TrashCan";

	private static final String		OBJ_STATE_X				= "StateX";
	private static final String		OBJ_STATE_A				= "StateA";
	private static final String		OBJ_STATE_B				= "StateB";
	private static final String		OBJ_STATE_C				= "StateC";
	private static final String		OBJ_STATE_D				= "StateD";
	private static final String		OBJ_STATE_E				= "StateE";
	private static final String		OBJ_STATE_F				= "StateF";
	private static final String 	OBJ_STATE_Y				= "StateY";

	private static final String		OBJ_REFEREE				= "WorldReferee";

	public DiaperDomain() {}

/* ============================================================================
 * Domain generation
 * ========================================================================= */

	/**
	 * Generates a new DiaperDomain.
	 */
	public Domain generateDomain() {

		Domain domain = new SADomain();

		//Values for the physical object class' type attribute
		List<String> physobjTypes = new ArrayList<String>() {{
			add(PO_TYPE_DIAPER); 		add(PO_TYPE_WIPES);
			add(PO_TYPE_CLOTHING);
		}};

		//Values for the container object class' type attribute
		List<String> containerTypes = new ArrayList<String>() {{
			add(CT_TYPE_CHANGINGTABLE); add(CT_TYPE_SIDETABLE);
			add(CT_TYPE_HAMPER); 		add(CT_TYPE_DRESSER);
			add(CT_TYPE_TRASHCAN);
		}};

		//Attribute for the caregiver's mental state
		Attribute attrMentalState = new Attribute(domain, ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);

		//Attribute for the type of a physical object
		Attribute attrPhysobjType = new Attribute(domain, ATTR_PHYSOBJ_TYPE, Attribute.AttributeType.DISC);
		attrPhysobjType.setDiscValues(physobjTypes);

		//Attribute for the type of a container
		Attribute attrContainerType = new Attribute(domain, ATTR_CONTAINER_TYPE, Attribute.AttributeType.DISC);
		attrContainerType.setDiscValues(containerTypes);

		//Attribute for the cleanliness of an object
		Attribute attrCleanliness = new Attribute(domain, ATTR_CLEANLINESS, Attribute.AttributeType.DISC);
		attrCleanliness.setDiscValuesForRange(0, 1, 1);

		//Attribute for the manipulability of an object
		Attribute attrManipulable = new Attribute(domain, ATTR_MANIPULABLE, Attribute.AttributeType.DISC);
		attrManipulable.setDiscValuesForRange(0, 1, 1);

		//Attribute for whether the world needs to be updated
		Attribute attrNeedsUpdate = new Attribute(domain, ATTR_NEEDS_UPDATE, Attribute.AttributeType.DISC);
		attrNeedsUpdate.setDiscValuesForRange(0, 1, 1);

		//Attribute for the contents of a container
		Attribute attrContents = new Attribute(domain, ATTR_CONTENTS, Attribute.AttributeType.MULTITARGETRELATIONAL);

		//Attribute for the container holding a physical object
		Attribute attrContainer = new Attribute(domain, ATTR_CONTAINER, Attribute.AttributeType.RELATIONAL);

		

		//Class of the human caregiver
		ObjectClass humanClass = new ObjectClass(domain, CLASS_HUMAN);	
		humanClass.addAttribute(attrMentalState);

		//Class of the robot
		ObjectClass robotClass = new ObjectClass(domain, CLASS_ROBOT);

		//Class of a container (a location, like a table or dresser)
		ObjectClass containerClass = new ObjectClass(domain, CLASS_CONTAINER);
		containerClass.addAttribute(attrContainerType);
		containerClass.addAttribute(attrContents);

		//Class of a physical object (like a diaper or clothing)
		ObjectClass physobjClass = new ObjectClass(domain, CLASS_PHYSOBJ);
		physobjClass.addAttribute(attrPhysobjType);
		physobjClass.addAttribute(attrContainer);
		physobjClass.addAttribute(attrCleanliness);
		physobjClass.addAttribute(attrManipulable);

		//Class of a mental state object
		ObjectClass stateClass = new ObjectClass(domain, CLASS_STATE);

		//Class of the world (referee)
		ObjectClass refereeClass = new ObjectClass(domain, CLASS_REFEREE);
		refereeClass.addAttribute(attrNeedsUpdate);

		//Create actions
		Action bringAction = new BringAction(domain, ACTION_BRING);
		Action updateAction = new UpdateAction(domain, ACTION_UPDATE);
		Action waitAction = new WaitAction(domain, ACTION_WAIT);

		return domain;
	}



/* ============================================================================
 * Class definition for Action BRING(Physobj, Container)
 * ========================================================================= */

	/**
	 * A Bring action is the action of moving an object from one container to 
	 * another container.  Its parameters are P of class physobj and C of class
	 * container.  The result of this action is that P's old container does not
	 * contain P anymore, P's container field now points to C, and C now has another
	 * object, P, in its contents attribute.
	 */
	public class BringAction extends Action {

		public BringAction(Domain domain, String name) {
			super(name, domain, new String[]{CLASS_PHYSOBJ, CLASS_CONTAINER});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance object =  st.getObject(params[0]);
			ObjectInstance referee = st.getObject(OBJ_REFEREE);			

			boolean manipulable = object.getDiscValForAttribute(ATTR_MANIPULABLE) == 1;
			boolean updated = referee.getDiscValForAttribute(ATTR_NEEDS_UPDATE) == 0;
			return manipulable && updated;
		}

		@Override
		protected State performActionHelper(State st, String[] params) {
			ObjectInstance object = st.getObject(params[0]);
			ObjectInstance container = st.getObject(params[1]);
			ObjectInstance referee = st.getObject(OBJ_REFEREE);

			grabObject(st, object);	
			placeObject(object, container);

			referee.setValue(ATTR_NEEDS_UPDATE, 1);
			return st;
		}

		private void grabObject(State st, ObjectInstance obj) {
			String oldContainerName = (String) obj.getAllRelationalTargets(ATTR_CONTAINER).toArray()[0];

			ObjectInstance oldContainer = st.getObject(oldContainerName);
			oldContainer.removeRelationalTarget(ATTR_CONTENTS, obj.getName());
			obj.clearRelationalTargets(ATTR_CONTAINER);
		}

		private void placeObject(ObjectInstance obj, ObjectInstance cnt) {
			obj.addRelationalTarget(ATTR_CONTAINER, cnt.getName());
			cnt.addRelationalTarget(ATTR_CONTENTS, obj.getName());
		}
	}

/* ============================================================================
 * Class definition for Action UPDATE()
 * ========================================================================= */

	/**
	 * An UpdateAction runs mental state updating for the Caregiver based upon
	 * conditions involving the positions of the objects in the world.  Essentially
	 * encapsulates exogenous world changes.  In addition, flips the bit of the 
	 * referee's NeedsUpdate attribute.
	 */
	public class UpdateAction extends Action {
		public UpdateAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance referee = st.getObject(OBJ_REFEREE);

			boolean needsUpdate = referee.getDiscValForAttribute(ATTR_NEEDS_UPDATE) == 1;
			return needsUpdate;
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			tickWorld(st);
			resetReferee(st);
			return st;
		}

		private void tickWorld(State st) {
			ObjectInstance caregiver = st.getObject(OBJ_CAREGIVER);
			String state = (String) caregiver.getAllRelationalTargets(ATTR_MENTAL_STATE).toArray()[0];

			switch(state) {

				//Not yet started.  Can progress always.
				case OBJ_STATE_X:
					caregiver.addRelationalTarget(ATTR_MENTAL_STATE, OBJ_STATE_A);
					break;

				//Removed old clothing.  Can progress if all old clothing in hamper.  Old clothing becomes nonmanipulable.
				case OBJ_STATE_A:
					ObjectInstance oldPants = st.getObject(OBJ_OLDPANTS);
					ObjectInstance oldShirt = st.getObject(OBJ_OLDSHIRT);
					if(getContainer(oldPants).equals(OBJ_HAMPER) && getContainer(oldShirt).equals(OBJ_HAMPER)) {
						caregiver.addRelationalTarget(ATTR_MENTAL_STATE, OBJ_STATE_B);
						oldPants.setValue(ATTR_MANIPULABLE, 0);
						oldShirt.setValue(ATTR_MANIPULABLE, 0);
					}
					break;

				//Removed old diaper.  Can progress if old diaper put in trash.  Old diaper becomes nonmanipulable.
				case OBJ_STATE_B:
					ObjectInstance oldDiaper = st.getObject(OBJ_OLDDIAPER);
					if(getContainer(oldDiaper).equals(OBJ_TRASHCAN)) {
						caregiver.addRelationalTarget(ATTR_MENTAL_STATE, OBJ_STATE_C);
						oldDiaper.setValue(ATTR_MANIPULABLE, 0);
					}
					break;

				//Checked for contingencies.  Can progress always
				case OBJ_STATE_C:
					caregiver.addRelationalTarget(ATTR_MENTAL_STATE, OBJ_STATE_D);
					break;

				//Baby clean. Can progress if we're brought the new diaper.  It then becomes nonmanipulable
				case OBJ_STATE_D:
					ObjectInstance newDiaper = st.getObject(OBJ_NEWDIAPER);
					if(getContainer(newDiaper).equals(OBJ_CHANGINGTABLE)) {
						caregiver.addRelationalTarget(ATTR_MENTAL_STATE, OBJ_STATE_E);
						newDiaper.setValue(ATTR_MANIPULABLE, 0);
					}
					break;

				//Baby diapered.  Can progress if we're brought new clothing.  It then becomes nonmanipulable.
				case OBJ_STATE_E:
					ObjectInstance newShirt = st.getObject(OBJ_NEWSHIRT);
					ObjectInstance newPants = st.getObject(OBJ_NEWPANTS);
					if(getContainer(newShirt).equals(OBJ_CHANGINGTABLE) && getContainer(newPants).equals(OBJ_CHANGINGTABLE)) {
						caregiver.addRelationalTarget(ATTR_MENTAL_STATE, OBJ_STATE_Y);
						newShirt.setValue(ATTR_MANIPULABLE, 0);
						newPants.setValue(ATTR_MANIPULABLE, 0);
					}
					break;

				case OBJ_STATE_Y:
					System.out.println("Reached State Y!");
					break;
			}
		}

		private void resetReferee(State st) {
			ObjectInstance referee = st.getObject(OBJ_REFEREE);
			referee.setValue(ATTR_NEEDS_UPDATE, 0);
		}

		private String getContainer(ObjectInstance o) {
			return (String) o.getAllRelationalTargets(ATTR_CONTAINER).toArray()[0];
		}
	}

/* ============================================================================
 * Class definition for Action WAIT()
 * ========================================================================= */

	/**
	 * A Wait action is a dummy action which does not cause any effect on 
	 * the physical world.  This is here in case it is not advantageous at the
	 * time to take a physical Bring action and instead it is better to pass the
	 * turn back to the caregiver.  The result of this ticks the referee's bit back
	 * to 1
	 */ 
	public class WaitAction extends Action {
		public WaitAction(Domain domain, String name) {
			super(name, domain, new String[]{});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance referee = st.getObject(OBJ_REFEREE);

			boolean updated = referee.getDiscValForAttribute(ATTR_NEEDS_UPDATE) == 0;
			return updated;
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			ObjectInstance referee = st.getObject(OBJ_REFEREE);
			referee.setValue(ATTR_NEEDS_UPDATE, 1);
			return st;
		}
	}

/* ============================================================================
 * Create the initial state
 * ========================================================================= */

	public static State getNewState(Domain d) {
		State s = new State();

		//Retrieve classes
		ObjectClass humanClass = d.getObjectClass(CLASS_HUMAN);
		ObjectClass robotClass = d.getObjectClass(CLASS_ROBOT);
		ObjectClass containerClass = d.getObjectClass(CLASS_CONTAINER);
		ObjectClass physobjClass = d.getObjectClass(CLASS_PHYSOBJ);
		ObjectClass stateClass = d.getObjectClass(CLASS_STATE);
		ObjectClass refereeClass = d.getObjectClass(CLASS_REFEREE);

		//Create caregiver
		ObjectInstance caregiver = new ObjectInstance(humanClass, OBJ_CAREGIVER);

		//Create robot
		ObjectInstance robot = new ObjectInstance(robotClass, OBJ_ROBOT);

		//Create the referee
		ObjectInstance referee = new ObjectInstance(refereeClass, OBJ_REFEREE);
		referee.setValue(ATTR_NEEDS_UPDATE, 1);

		//Create all physical objects
		ObjectInstance oldDiaper = new ObjectInstance(physobjClass, OBJ_OLDDIAPER);
		makePhysobj(oldDiaper, PO_TYPE_DIAPER, 0, 1);
		ObjectInstance newDiaper = new ObjectInstance(physobjClass, OBJ_NEWDIAPER);
		makePhysobj(newDiaper, PO_TYPE_DIAPER, 1, 1);
		ObjectInstance wipes = new ObjectInstance(physobjClass, OBJ_WIPES);
		makePhysobj(wipes, PO_TYPE_WIPES, 1, 1);
		ObjectInstance oldPants = new ObjectInstance(physobjClass, OBJ_OLDPANTS);
		makePhysobj(oldPants, PO_TYPE_CLOTHING, 0, 1);
		ObjectInstance oldShirt = new ObjectInstance(physobjClass, OBJ_OLDSHIRT);
		makePhysobj(oldShirt, PO_TYPE_CLOTHING, 0, 1);
		ObjectInstance newPants = new ObjectInstance(physobjClass, OBJ_NEWPANTS);
		makePhysobj(newPants, PO_TYPE_CLOTHING, 1, 1);
		ObjectInstance newShirt = new ObjectInstance(physobjClass, OBJ_NEWSHIRT);
		makePhysobj(newShirt, PO_TYPE_CLOTHING, 1, 1);

		//Create all mental states
		ObjectInstance stateX = new ObjectInstance(stateClass, OBJ_STATE_X);
		ObjectInstance stateA = new ObjectInstance(stateClass, OBJ_STATE_A);
		ObjectInstance stateB = new ObjectInstance(stateClass, OBJ_STATE_B);
		ObjectInstance stateC = new ObjectInstance(stateClass, OBJ_STATE_C);
		ObjectInstance stateD = new ObjectInstance(stateClass, OBJ_STATE_D);
		ObjectInstance stateE = new ObjectInstance(stateClass, OBJ_STATE_E);
		ObjectInstance stateF = new ObjectInstance(stateClass, OBJ_STATE_F);
		ObjectInstance stateY = new ObjectInstance(stateClass, OBJ_STATE_Y);

		//Give caregiver mental state X
		caregiver.addRelationalTarget(ATTR_MENTAL_STATE, stateX.getName());

		//Create all containers
		ObjectInstance changingTable = new ObjectInstance(containerClass, OBJ_CHANGINGTABLE);
		makeContainer(changingTable, CT_TYPE_CHANGINGTABLE);
		ObjectInstance sideTable = new ObjectInstance(containerClass, OBJ_SIDETABLE);
		makeContainer(sideTable, CT_TYPE_SIDETABLE);
		ObjectInstance hamper = new ObjectInstance(containerClass, OBJ_HAMPER);
		makeContainer(hamper, CT_TYPE_HAMPER);
		ObjectInstance dresser = new ObjectInstance(containerClass, OBJ_DRESSER);
		makeContainer(dresser, CT_TYPE_DRESSER);
		ObjectInstance trashCan = new ObjectInstance(containerClass, OBJ_TRASHCAN);
		makeContainer(trashCan, CT_TYPE_TRASHCAN);

		//Fill containers
		addContents(changingTable, oldDiaper, oldPants, oldShirt);
		addContents(sideTable, newDiaper, wipes);
		addContents(dresser, newPants, newShirt);

		addObjects(s, caregiver, robot, oldDiaper, newDiaper, wipes, oldPants,
			oldShirt, newPants, newShirt, changingTable, sideTable, hamper,
			dresser, trashCan, referee);

		return s;
	}

	private static void makePhysobj(ObjectInstance obj, String type, int clean, int manip) {
		obj.setValue(ATTR_PHYSOBJ_TYPE, type);
		obj.setValue(ATTR_CLEANLINESS, clean);
		obj.setValue(ATTR_MANIPULABLE, manip);
	}

	private static void makeContainer(ObjectInstance cnt, String type) {
		cnt.setValue(ATTR_CONTAINER_TYPE, type);
	}

	private static void addContents(ObjectInstance cnt, ObjectInstance... conts) {
		for(ObjectInstance content : conts) {
			cnt.addRelationalTarget(ATTR_CONTENTS, content.getName());
			content.addRelationalTarget(ATTR_CONTAINER, cnt.getName());
		}
	}

	private static void addObjects(State s, ObjectInstance... objs) {
		for(ObjectInstance object : objs) {
			s.addObject(object);
		}
	}



/* ============================================================================
 * Main method
 * ========================================================================= */

	public static void main(String[] args) {
		DiaperDomain dg = new DiaperDomain();
		Domain domain = dg.generateDomain();
		State s = getNewState(domain);

		TerminalExplorer exp = new TerminalExplorer(domain);
		exp.exploreFromState(s);
	}
}
