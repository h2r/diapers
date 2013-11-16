package edu.brown.h2r.diapers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	private static final String 		CLASS_HUMAN 			= "ClassHuman";
	private static final String		CLASS_ROBOT 			= "ClassRobot";
	private static final String		CLASS_CONTAINER			= "ClassContainer";
	private static final String		CLASS_PHYSOBJ			= "ClassPhysobj";

	private static final String		ATTR_MENTAL_STATE 		= "AttrMentalState"
	private static final String		ATTR_PHYSOBJ_TYPE		= "AttrPOType";
	private static final String		ATTR_CONTENTS_TYPE		= "AttrCTType";
	private static final String		ATTR_CLEANLINESS		= "AttrCleanliness";
	private static final String		ATTR_MANIPULABLE		= "AttrManipulable";
	private static final String		ATTR_CONTENTS 			= "AttrContents";
	private static final String		ATTR_CONTAINER 			= "AttrContainer";

	private static final String		PO_TYPE_DIAPER			= "POTypeDiaper";
	private static final String		PO_TYPE_WIPES			= "POTypeWipes";
	private static final String 		PO_TYPE_CLOTHING		= "POTypeClothing";

	private static final String		CT_TYPE_CHANGINGTABLE		= "CTTypeChangingTable";
	private static final String		CT_TYPE_SIDETABLE		= "CTTypeSideTable";
	private static final String		CT_TYPE_HAMPER			= "CTTypeHamper";
	private static final String		CT_TYPE_DRESSER			= "CTTypeDresser";
	private static final String		CT_TYPE_TRASHCAN		= "CTTypeTrashCan";

	private static final String		STATE_X				= "stateX";
	private static final String		STATE_A				= "stateA";
	private static final String		STATE_B				= "stateB";
	private static final String		STATE_C				= "stateC";
	private static final String		STATE_D				= "stateD";
	private static final String		STATE_E 			= "stateE";
	private static final String		STATE_F				= "stateF";
	private static final String		STATE_Y 			= "stateY";

	private static final String		ACTION_BRING			= "BringAction";

	private static final String		OBJ_CAREGIVER			= "Caregiver";
	private static final String		OBJ_ROBOT			= "Robot";
	private static final String		OBJ_OLDDIAPER			= "OldDiaper";
	private static final String		OBJ_NEWDIAPER			= "NewDiaper";
	private static final String		OBJ_OLDPANTS			= "OldPants";
	private static final String		OBJ_NEWPANTS			= "NewPants";
	private static final String		OBJ_OLDSHIRT			= "OldShirt";
	private static final String		OBJ_NEWSHIRT			= "NewShirt";
	private static final String		OBJ_WIPES			= "Wipes";
	private static final String		OBJ_CHANGINGTABLE 		= "ChangingTable";
	private static final String		OBJ_SIDETABLE			= "SideTable";
	private static final String		OBJ_HAMPER			= "Hamper";
	private static final String		OBJ_DRESSER			= "Dresser";
	private static final String		OBJ_TRASHCAN			= "TrashCan";

	public DiaperDomain() {}



/* ============================================================================
 * Domain generation
 * ========================================================================= */

	public Domain generateDomain() {

		Domain domain = new SADomain();

		//Values for the physical object class' type attribute
		List<String> physobjTypes = new ArrayList<String>() {{
			add(PO_TYPE_DIAPER); 		add(PO_TYPE_WIPES);
			add(PO_TYPE_CLOTHING);
		}};

		//Values for the container object class' type attribute
		List<String> containerTypes = new ArrayList<String>() {{
			add(CT_TYPE_CHANGINGTABLE); 	add(CT_TYPE_SIDETABLE);
			add(CT_TYPE_HAMPER); 		add(CT_TYPE_DRESSER);
			add(CT_TYPE_TRASHCAN);
		}};

		//Values for the human object class' type attribute
		List<String> mentalStates = new ArrayList<String>() {{
			add(STATE_X); add(STATE_A); add(STATE_B);
			add(STATE_C); add(STATE_C); add(STATE_D);
			add(STATE_E); add(STATE_F); add(STATE_Y);
		}};



		//Attribute for the caregiver's mental state
		Attribute attrMentalState = new Attribute(domain, ATTR_MENTAL_STATE, Attribute.AttributeType.DISC);
		caregiverMentalState.setDiscValues(mentalStates);

		//Attribute for the type of a physical object
		Attribute attrPhysobjType = new Attribute(domain, ATTR_PHYSOBJ_TYPE, Attribute.AttributeType.DISC);
		physobjType.setDiscValues(physobjTypes);

		//Attribute for the type of a container
		Attribute attrContainerType = new Attribute(domain, ATTR_CONTAINER_TYPE, Attribute.AttributeType.DISC);
		containerType.setDiscValues(containerTypes);

		//Attribute for the cleanliness of an object
		Attribute attrCleanliness = new Attribute(domain, ATTR_CLEANLINESS, Attribute.AttributeType.DISC);
		attrCleanliness.setDiscValuesForRange(0, 1, 1);

		//Attribute for the manipulability of an object
		Attribute attrManipulable = new Attribute(domain, ATTR_MANIPULABLE, Attribute.AttributeType.DISC);
		attrManipulable.setDiscValuesForRange(0, 1, 1);

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



		//Action for bringing an object to a location
		Action bringAction = new BringAction(domain, ACTION_BRING);

		return domain;
	}



/* ============================================================================
 * Class definition for Action BRING(Physobj, Container)
 * ========================================================================= */

	public class BringAction extends Action {

		public BringAction(Domain domain, String name) {
			super(name, domain, new String[]{CLASS_PHYSOBJ, CLASS_CONTAINER});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance object = st.getObject(params[1]);

			return object.getDiscValForAttribute(ATTR_MANIPULABLE) == 1;	
		}

		@Override
		protected State performActionHelper(State st, String[] params) {
			ObjectInstance object = st.getObject(params[0]);
			ObjectInstance container = st.getObjecT(params[1]);

			grabObject(st, object);	
			placeObject(object, container);

			return st;
		}

		private void grabObject(State st, ObjectInstance obj) {
			String oldContainerName = obj.getAllRelationalTargets(ATTR_CONTAINER).toArray()[0];
			ObjectInstance oldContainer = st.getObject(oldContainerName);
			oldContainer.removeRelationalTarget(ATTR_CONTENTS, obj.getName());
			obj.clearRelationalTargets();
		}

		private void placeObject(ObjectInstance obj, ObjectInstance cnt) {
			obj.addRelationalTarget(ATTR_CONTAINER, cnt.getName());
			cnt.addRelationalTarget(ATTR_CONTENTS, obj.getName());
		}
	}



/* ============================================================================
 * Create the initial state
 * ========================================================================= */

	public static State getNewState(Domain dom) {
		State s = new State();

		//Retrieve classes
		ObjectClass humanClass = d.getObjectClass(CLASS_HUMAN);
		ObjectClass robotClass = d.getObjectClass(CLASS_ROBOT);
		ObjectClass containerClass = d.getObjectClass(CLASS_CONTAINER);
		ObjectClass physobjClass = d.getObjectClass(CLASS_PHYSOBJ);

		//Create caregiver
		ObjectInstance caregiver = new ObjectInstance(humanClass, OBJ_CAREGIVER);
		caregiver.setValue(ATTR_MENTAL_STATE, STATE_X);

		//Create robot
		ObjectInstance robot = new ObjectInstance(robotClass, OBJ_ROBOT);

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
			dresser, trashCan);

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
