package edu.brown.h2r.diapers.athena;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.lang.ArrayIndexOutOfBoundsException;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.SADomain;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;
import burlap.oomdp.singleagent.explorer.VisualExplorer;

import edu.brown.h2r.diapers.athena.namespace.S;

/**
 * Domain generator for the Diaper changing domain.
 *
 * @author Izaak Baker (iebaker)
 */
public class DiaperDomain implements DomainGenerator {

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
			add(S.PO_TYPE_DIAPER); 		add(S.PO_TYPE_WIPES);
			add(S.PO_TYPE_CLOTHING);
		}};

		//Values for the container object class' type attribute
		List<String> containerTypes = new ArrayList<String>() {{
			add(S.CT_TYPE_CHANGINGTABLE); add(S.CT_TYPE_SIDETABLE);
			add(S.CT_TYPE_HAMPER); 		add(S.CT_TYPE_DRESSER);
			add(S.CT_TYPE_TRASHCAN);
		}};

		//Attribute for the caregiver's mental state
		Attribute attrMentalState = new Attribute(domain, S.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);

		//Attribute for the type of a physical object
		Attribute attrPhysobjType = new Attribute(domain, S.ATTR_PHYSOBJ_TYPE, Attribute.AttributeType.DISC);
		attrPhysobjType.setDiscValues(physobjTypes);

		//Attribute for the type of a container
		Attribute attrContainerType = new Attribute(domain, S.ATTR_CONTAINER_TYPE, Attribute.AttributeType.DISC);
		attrContainerType.setDiscValues(containerTypes);

		//Attribute for the cleanliness of an object
		Attribute attrCleanliness = new Attribute(domain, S.ATTR_CLEANLINESS, Attribute.AttributeType.DISC);
		attrCleanliness.setDiscValuesForRange(0, 1, 1);

		//Attribute for the manipulability of an object
		Attribute attrManipulable = new Attribute(domain, S.ATTR_MANIPULABLE, Attribute.AttributeType.DISC);
		attrManipulable.setDiscValuesForRange(0, 1, 1);

		//Attribute for whether the world needs to be updated
		Attribute attrNeedsUpdate = new Attribute(domain, S.ATTR_NEEDS_UPDATE, Attribute.AttributeType.DISC);
		attrNeedsUpdate.setDiscValuesForRange(0, 1, 1);

		//Attribute for the contents of a container
		Attribute attrContents = new Attribute(domain, S.ATTR_CONTENTS, Attribute.AttributeType.MULTITARGETRELATIONAL);

		//Attribute for the container holding a physical object
		Attribute attrContainer = new Attribute(domain, S.ATTR_CONTAINER, Attribute.AttributeType.RELATIONAL);

		//Class of the human caregiver
		ObjectClass humanClass = new ObjectClass(domain, S.CLASS_HUMAN);	
		humanClass.addAttribute(attrMentalState);

		//Class of the robot
		ObjectClass robotClass = new ObjectClass(domain, S.CLASS_ROBOT);

		//Class of a container (a location, like a table or dresser)
		ObjectClass containerClass = new ObjectClass(domain, S.CLASS_CONTAINER);
		containerClass.addAttribute(attrContainerType);
		containerClass.addAttribute(attrContents);

		//Class of a physical object (like a diaper or clothing)
		ObjectClass physobjClass = new ObjectClass(domain, S.CLASS_PHYSOBJ);
		physobjClass.addAttribute(attrPhysobjType);
		physobjClass.addAttribute(attrContainer);
		physobjClass.addAttribute(attrCleanliness);
		physobjClass.addAttribute(attrManipulable);

		//Class of a mental state object
		ObjectClass stateClass = new ObjectClass(domain, S.CLASS_STATE);

		//Class of the world (referee)
		ObjectClass refereeClass = new ObjectClass(domain, S.CLASS_REFEREE);
		refereeClass.addAttribute(attrNeedsUpdate);

		//Create actions
		Action bringAction = new BringAction(domain, S.ACTION_BRING);
		Action updateAction = new UpdateAction(domain, S.ACTION_UPDATE);
		Action waitAction = new WaitAction(domain, S.ACTION_WAIT);

		//Create propositional function
		PropositionalFunction pfInStateY = new InStateYPropFun(domain, S.PROP_IN_STATE_Y);

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
			super(name, domain, new String[]{S.CLASS_PHYSOBJ, S.CLASS_CONTAINER});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance object =  st.getObject(params[0]);
			ObjectInstance referee = st.getObject(S.OBJ_REFEREE);			

			boolean manipulable = object.getDiscValForAttribute(S.ATTR_MANIPULABLE) == 1;
			boolean updated = referee.getDiscValForAttribute(S.ATTR_NEEDS_UPDATE) == 0;
			return manipulable && updated;
		}

		@Override
		protected State performActionHelper(State st, String[] params) {
			ObjectInstance object = st.getObject(params[0]);
			ObjectInstance container = st.getObject(params[1]);
			ObjectInstance referee = st.getObject(S.OBJ_REFEREE);

			grabObject(st, object);	
			placeObject(object, container);

			referee.setValue(S.ATTR_NEEDS_UPDATE, 1);
			return st;
		}

		private void grabObject(State st, ObjectInstance obj) {
			String oldContainerName = (String) obj.getAllRelationalTargets(S.ATTR_CONTAINER).toArray()[0];

			ObjectInstance oldContainer = st.getObject(oldContainerName);
			oldContainer.removeRelationalTarget(S.ATTR_CONTENTS, obj.getName());
			obj.clearRelationalTargets(S.ATTR_CONTAINER);
		}

		private void placeObject(ObjectInstance obj, ObjectInstance cnt) {
			obj.addRelationalTarget(S.ATTR_CONTAINER, cnt.getName());
			cnt.addRelationalTarget(S.ATTR_CONTENTS, obj.getName());
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
			ObjectInstance referee = st.getObject(S.OBJ_REFEREE);

			boolean needsUpdate = referee.getDiscValForAttribute(S.ATTR_NEEDS_UPDATE) == 1;
			return needsUpdate;
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			tickWorld(st);
			resetReferee(st);
			return st;
		}

		private void tickWorld(State st) {
			ObjectInstance caregiver = st.getObject(S.OBJ_CAREGIVER);
			String state = (String) caregiver.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];

			switch(state) {

				//Not yet started.  Can progress always.
				case S.OBJ_STATE_X:
					caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, S.OBJ_STATE_A);
					break;

				//Removed old clothing.  Can progress if all old clothing in hamper.  Old clothing becomes nonmanipulable.
				case S.OBJ_STATE_A:
					ObjectInstance oldPants = st.getObject(S.OBJ_OLDPANTS);
					ObjectInstance oldShirt = st.getObject(S.OBJ_OLDSHIRT);
					if(getContainer(oldPants).equals(S.OBJ_HAMPER) && getContainer(oldShirt).equals(S.OBJ_HAMPER)) {
						caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, S.OBJ_STATE_B);
						oldPants.setValue(S.ATTR_MANIPULABLE, 0);
						oldShirt.setValue(S.ATTR_MANIPULABLE, 0);
					}
					break;

				//Removed old diaper.  Can progress if old diaper put in trash.  Old diaper becomes nonmanipulable.
				case S.OBJ_STATE_B:
					ObjectInstance oldDiaper = st.getObject(S.OBJ_OLDDIAPER);
					if(getContainer(oldDiaper).equals(S.OBJ_TRASHCAN)) {
						caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, S.OBJ_STATE_C);
						oldDiaper.setValue(S.ATTR_MANIPULABLE, 0);
					}
					break;

				//Checked for contingencies.  Can progress always
				case S.OBJ_STATE_C:
					caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, S.OBJ_STATE_D);
					break;

				//Baby clean. Can progress if we're brought the new diaper.  It then becomes nonmanipulable
				case S.OBJ_STATE_D:
					ObjectInstance newDiaper = st.getObject(S.OBJ_NEWDIAPER);
					if(getContainer(newDiaper).equals(S.OBJ_CHANGINGTABLE)) {
						caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, S.OBJ_STATE_E);
						newDiaper.setValue(S.ATTR_MANIPULABLE, 0);
					}
					break;

				//Baby diapered.  Can progress if we're brought new clothing.  It then becomes nonmanipulable.
				case S.OBJ_STATE_E:
					ObjectInstance newShirt = st.getObject(S.OBJ_NEWSHIRT);
					ObjectInstance newPants = st.getObject(S.OBJ_NEWPANTS);
					if(getContainer(newShirt).equals(S.OBJ_CHANGINGTABLE) && getContainer(newPants).equals(S.OBJ_CHANGINGTABLE)) {
						caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, S.OBJ_STATE_Y);
						newShirt.setValue(S.ATTR_MANIPULABLE, 0);
						newPants.setValue(S.ATTR_MANIPULABLE, 0);
					}
					break;

				case S.OBJ_STATE_Y:
					System.out.println("Reached State Y!");
					break;
			}
		}

		private void resetReferee(State st) {
			ObjectInstance referee = st.getObject(S.OBJ_REFEREE);
			referee.setValue(S.ATTR_NEEDS_UPDATE, 0);
		}

		private String getContainer(ObjectInstance o) {
			return (String) o.getAllRelationalTargets(S.ATTR_CONTAINER).toArray()[0];
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
			ObjectInstance referee = st.getObject(S.OBJ_REFEREE);

			boolean updated = referee.getDiscValForAttribute(S.ATTR_NEEDS_UPDATE) == 0;
			return updated;
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			ObjectInstance referee = st.getObject(S.OBJ_REFEREE);
			referee.setValue(S.ATTR_NEEDS_UPDATE, 1);
			return st;
		}
	}

/* ============================================================================
 * Class definition for In State Y Prop. Function
 * ========================================================================= */

	public class InStateYPropFun extends PropositionalFunction {
		public InStateYPropFun(Domain domain, String name) {
			super(name, domain, new String[]{S.CLASS_HUMAN});
		}

		@Override
		public boolean isTrue(State st, String[] params) {
			ObjectInstance person = st.getObject(params[0]);
			String mentalState = (String) person.getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];
			boolean inStateY = mentalState.equals(S.OBJ_STATE_Y);
			return inStateY;
		}
	}

/* ============================================================================
 * Create the initial state
 * ========================================================================= */

	public static State getNewState(Domain d) {
		State s = new State();

		//Retrieve classes
		ObjectClass humanClass = d.getObjectClass(S.CLASS_HUMAN);
		ObjectClass robotClass = d.getObjectClass(S.CLASS_ROBOT);
		ObjectClass containerClass = d.getObjectClass(S.CLASS_CONTAINER);
		ObjectClass physobjClass = d.getObjectClass(S.CLASS_PHYSOBJ);
		ObjectClass stateClass = d.getObjectClass(S.CLASS_STATE);
		ObjectClass refereeClass = d.getObjectClass(S.CLASS_REFEREE);

		//Create caregiver
		ObjectInstance caregiver = new ObjectInstance(humanClass, S.OBJ_CAREGIVER);

		//Create robot
		ObjectInstance robot = new ObjectInstance(robotClass, S.OBJ_ROBOT);

		//Create the referee
		ObjectInstance referee = new ObjectInstance(refereeClass, S.OBJ_REFEREE);
		referee.setValue(S.ATTR_NEEDS_UPDATE, 1);

		//Create all physical objects
		ObjectInstance oldDiaper = new ObjectInstance(physobjClass, S.OBJ_OLDDIAPER);
		makePhysobj(oldDiaper, S.PO_TYPE_DIAPER, 0, 1);
		ObjectInstance newDiaper = new ObjectInstance(physobjClass, S.OBJ_NEWDIAPER);
		makePhysobj(newDiaper, S.PO_TYPE_DIAPER, 1, 1);
		ObjectInstance wipes = new ObjectInstance(physobjClass, S.OBJ_WIPES);
		makePhysobj(wipes, S.PO_TYPE_WIPES, 1, 1);
		ObjectInstance oldPants = new ObjectInstance(physobjClass, S.OBJ_OLDPANTS);
		makePhysobj(oldPants, S.PO_TYPE_CLOTHING, 0, 1);
		ObjectInstance oldShirt = new ObjectInstance(physobjClass, S.OBJ_OLDSHIRT);
		makePhysobj(oldShirt, S.PO_TYPE_CLOTHING, 0, 1);
		ObjectInstance newPants = new ObjectInstance(physobjClass, S.OBJ_NEWPANTS);
		makePhysobj(newPants, S.PO_TYPE_CLOTHING, 1, 1);
		ObjectInstance newShirt = new ObjectInstance(physobjClass, S.OBJ_NEWSHIRT);
		makePhysobj(newShirt, S.PO_TYPE_CLOTHING, 1, 1);

		//Create all mental states
		ObjectInstance stateX = new ObjectInstance(stateClass, S.OBJ_STATE_X);
		ObjectInstance stateA = new ObjectInstance(stateClass, S.OBJ_STATE_A);
		ObjectInstance stateB = new ObjectInstance(stateClass, S.OBJ_STATE_B);
		ObjectInstance stateC = new ObjectInstance(stateClass, S.OBJ_STATE_C);
		ObjectInstance stateD = new ObjectInstance(stateClass, S.OBJ_STATE_D);
		ObjectInstance stateE = new ObjectInstance(stateClass, S.OBJ_STATE_E);
		ObjectInstance stateY = new ObjectInstance(stateClass, S.OBJ_STATE_Y);

		//Give caregiver mental state X
		caregiver.addRelationalTarget(S.ATTR_MENTAL_STATE, stateX.getName());

		//Create all containers
		ObjectInstance changingTable = new ObjectInstance(containerClass, S.OBJ_CHANGINGTABLE);
		makeContainer(changingTable, S.CT_TYPE_CHANGINGTABLE);
		ObjectInstance sideTable = new ObjectInstance(containerClass, S.OBJ_SIDETABLE);
		makeContainer(sideTable, S.CT_TYPE_SIDETABLE);
		ObjectInstance hamper = new ObjectInstance(containerClass, S.OBJ_HAMPER);
		makeContainer(hamper, S.CT_TYPE_HAMPER);
		ObjectInstance dresser = new ObjectInstance(containerClass, S.OBJ_DRESSER);
		makeContainer(dresser, S.CT_TYPE_DRESSER);
		ObjectInstance trashCan = new ObjectInstance(containerClass, S.OBJ_TRASHCAN);
		makeContainer(trashCan, S.CT_TYPE_TRASHCAN);

		//Fill containers
		addContents(changingTable, oldDiaper, oldPants, oldShirt);
		addContents(sideTable, newDiaper, wipes);
		addContents(dresser, newPants, newShirt);

		//Add all the objects to the state
		addObjects(s, stateX, stateA, stateB, stateC, stateD, stateE, stateY, 
			hamper, robot, wipes, oldShirt, oldPants, 
			newShirt, newPants, oldDiaper, newDiaper, oldPants, dresser, hamper,  sideTable, changingTable,
			trashCan, referee, caregiver);

		return s;
	}

	private static void makePhysobj(ObjectInstance obj, String type, int clean, int manip) {
		obj.setValue(S.ATTR_PHYSOBJ_TYPE, type);
		obj.setValue(S.ATTR_CLEANLINESS, clean);
		obj.setValue(S.ATTR_MANIPULABLE, manip);
	}

	private static void makeContainer(ObjectInstance cnt, String type) {
		cnt.setValue(S.ATTR_CONTAINER_TYPE, type);
	}

	private static void addContents(ObjectInstance cnt, ObjectInstance... conts) {
		for(ObjectInstance content : conts) {
			cnt.addRelationalTarget(S.ATTR_CONTENTS, content.getName());
			content.addRelationalTarget(S.ATTR_CONTAINER, cnt.getName());
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
