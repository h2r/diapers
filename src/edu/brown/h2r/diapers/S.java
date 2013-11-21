package edu.brown.h2r.diapers;

public class S {

	//Object class names
	public static final String CLASS_HUMAN = "class.human";
	public static final String CLASS_ROBOT = "class.robot";
	public static final String CLASS_CONTAINER = "class.container";
	public static final String CLASS_PHYSOBJ = "class.physobj";
	public static final String CLASS_STATE = "class.state";
	public static final String CLASS_REFEREE = "class.referee";

	//Attribute names
	public static final String ATTR_NEEDS_UPDATE = "attr.needsUpdate";
	public static final String ATTR_MENTAL_STATE = "attr.mentalState";
	public static final String ATTR_PHYSOBJ_TYPE = "attr.physobjType";
	public static final String ATTR_CONTAINER_TYPE = "attr.containerType";
	public static final String ATTR_CLEANLINESS = "attr.cleanliness";
	public static final String ATTR_MANIPULABLE = "attr.manipulable";
	public static final String ATTR_CONTAINER = "attr.container";
	public static final String ATTR_CONTENTS = "attr.contents";

	//Types of physical object
	public static final String PO_TYPE_DIAPER = "type.Diaper";
	public static final String PO_TYPE_WIPES = "type.Wipes";
	public static final String PO_TYPE_CLOTHING = "type.Clothing";

	//Types of container
	public static final String CT_TYPE_CHANGINGTABLE = "type.changingTable";
	public static final String CT_TYPE_SIDETABLE = "type.sideTable";
	public static final String CT_TYPE_HAMPER = "type.hamper";
	public static final String CT_TYPE_DRESSER = "type.dresser";
	public static final String CT_TYPE_TRASHCAN = "type.trashCan";

	//Actions
	public static final String ACTION_BRING = "bring";
	public static final String ACTION_UPDATE = "update";
	public static final String ACTION_WAIT = "wait";

	//Objects (physical)
	public static final String OBJ_CAREGIVER = "caregiver";
	public static final String OBJ_ROBOT = "robot";
	public static final String OBJ_OLDDIAPER = "oldDiaper";
	public static final String OBJ_NEWDIAPER = "newDiaper";
	public static final String OBJ_OLDPANTS = "oldPants";
	public static final String OBJ_NEWPANTS = "newPants";
	public static final String OBJ_OLDSHIRT = "oldShirt";
	public static final String OBJ_NEWSHIRT = "newShirt";
	public static final String OBJ_WIPES = "wipes";
	public static final String OBJ_CHANGINGTABLE = "changingTable";
	public static final String OBJ_SIDETABLE = "sideTable";
	public static final String OBJ_HAMPER = "hamper";
	public static final String OBJ_DRESSER = "dresser";
	public static final String OBJ_TRASHCAN = "trashCan";

	//Objects (mental)
	public static final String OBJ_STATE_X = "stateX";
	public static final String OBJ_STATE_A = "stateA";
	public static final String OBJ_STATE_B = "stateB";
	public static final String OBJ_STATE_C = "stateC";
	public static final String OBJ_STATE_D = "stateD";
	public static final String OBJ_STATE_E = "stateE";
	public static final String OBJ_STATE_F = "stateF";
	public static final String OBJ_STATE_Y = "stateY";

	//Referee
	public static final String OBJ_REFEREE = "referee";
}