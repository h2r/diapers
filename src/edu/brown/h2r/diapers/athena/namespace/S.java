package edu.brown.h2r.diapers.athena.namespace;

/**
 * S is a utility class containing the string names for all the objects
 * in the diaper changing world.  These are used in the terminal visualizer.
 * They are placed in a separate class to clear up clutter in DiaperDomain and
 * to allow all classes access to these names.  Almost never should raw strings
 * be used in other classes.
 *
 * @author Izaak Baker (iebaker)
 */ 
public class S {

/* ============================================================================
 * Object class names
 * ========================================================================= */

	public static final String CLASS_HUMAN = "class.human";
	public static final String CLASS_ROBOT = "class.robot";
	public static final String CLASS_CONTAINER = "class.container";
	public static final String CLASS_PHYSOBJ = "class.physobj";
	public static final String CLASS_STATE = "class.state";
	public static final String CLASS_REFEREE = "class.referee";

/* ============================================================================
 * Attribute names
 * ========================================================================= */

	public static final String ATTR_NEEDS_UPDATE = "attr.needsUpdate";
	public static final String ATTR_MENTAL_STATE = "attr.mentalState";
	public static final String ATTR_PHYSOBJ_TYPE = "attr.physobjType";
	public static final String ATTR_CONTAINER_TYPE = "attr.containerType";
	public static final String ATTR_CLEANLINESS = "attr.cleanliness";
	public static final String ATTR_MANIPULABLE = "attr.manipulable";
	public static final String ATTR_CONTAINER = "attr.container";
	public static final String ATTR_CONTENTS = "attr.contents";

/* ============================================================================
 * Physical Object types
 * ========================================================================= */

	public static final String PO_TYPE_DIAPER = "type.Diaper";
	public static final String PO_TYPE_WIPES = "type.Wipes";
	public static final String PO_TYPE_CLOTHING = "type.Clothing";

/* ============================================================================
 * Container types
 * ========================================================================= */

	public static final String CT_TYPE_CHANGINGTABLE = "type.changingTable";
	public static final String CT_TYPE_SIDETABLE = "type.sideTable";
	public static final String CT_TYPE_HAMPER = "type.hamper";
	public static final String CT_TYPE_DRESSER = "type.dresser";
	public static final String CT_TYPE_TRASHCAN = "type.trashCan";

/* ============================================================================
 * Actions!
 * ========================================================================= */

	public static final String ACTION_BRING = "bring";
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_WAIT = "wait";

/* ============================================================================
 * Objects
 * ========================================================================= */

	public static final String OBJ_CAREGIVER = "caregiver";
	public static final String OBJ_ROBOT = "robot";
	public static final String OBJ_OLDDIAPER = "olddiaper";
	public static final String OBJ_NEWDIAPER = "newdiaper";
	public static final String OBJ_OLDPANTS = "oldpants";
	public static final String OBJ_NEWPANTS = "newpants";
	public static final String OBJ_OLDSHIRT = "oldshirt";
	public static final String OBJ_NEWSHIRT = "newshirt";
	public static final String OBJ_WIPES = "wipes";
	public static final String OBJ_CHANGINGTABLE = "changingtable";
	public static final String OBJ_SIDETABLE = "sidetable";
	public static final String OBJ_HAMPER = "hamper";
	public static final String OBJ_DRESSER = "dresser";
	public static final String OBJ_TRASHCAN = "trashcan";
	public static final String OBJ_STATE_X = "stateX";
	public static final String OBJ_STATE_A = "stateA";
	public static final String OBJ_STATE_B = "stateB";
	public static final String OBJ_STATE_C = "stateC";
	public static final String OBJ_STATE_D = "stateD";
	public static final String OBJ_STATE_E = "stateE";
	public static final String OBJ_STATE_Y = "stateY";
	public static final String OBJ_REFEREE = "referee";

/* ============================================================================
 * Propositional Functions
 * ========================================================================= */

	public static final String PROP_IN_STATE_Y = "prop.inStateY";
}
