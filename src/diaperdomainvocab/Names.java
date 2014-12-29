package diaperdomainvocab;

/**
 * Names is a utility class containing the string names for all the objects
 * in the diaper changing world, including the mental attributes of the human while chaning the diaper. 
 *
 * @author nakul gopalan (ngopalan)
 */ 
public class Names {


/* ============================================================================
 * Classes
 * ========================================================================= */

	public static final String CLASS_STATE_HOLDER = "cls.stateHolder";
	public static final String CLASS_MENTAL_STATE = "cls.mentalState";
	
/* ============================================================================
 * Object class names
 * ========================================================================= */

	public static final String CLASS_HUMAN = "class.human";
	public static final String CLASS_BABY = "class.baby";
	public static final String CLASS_ROBOT = "class.robot";
	public static final String CLASS_CONTAINER = "class.container";
	public static final String CLASS_PHYSOBJ = "class.physobj";
	public static final String CLASS_STATE = "class.state";
	
	public static final String CLASS_OBSERVATION = "class.observation";

/* ============================================================================
 * Attribute names
 * ========================================================================= */

	public static final String ATTR_MENTAL_STATE = "attr.mentalState";
	public static final String ATTR_PHYSOBJ_TYPE = "attr.physobjType";
	public static final String ATTR_BABY_RASH = "attr.rash";
	public static final String ATTR_CONTAINER_TYPE = "attr.containerType";
	public static final String ATTR_CLEANLINESS = "attr.cleanliness";
	public static final String ATTR_MANIPULABLE = "attr.manipulable";
	public static final String ATTR_MEDICINE = "attr.medicine";
	public static final String ATTR_CONTAINER = "attr.container";
	public static final String ATTR_CONTENTS = "attr.contents";
	public static final String ATTR_WEARING = "attr.wearing";
	
	public static final String ATTR_OBS = "attr.obs";
	public static final String ATTR_OBS_SENTENCE = "attr.obsSentence";


/*==============================================================================
 * Mental State types
 */
	public static final String MS_TYPE_START = "type.start";
	public static final String MS_TYPE_RASH = "type.rash";
	public static final String MS_TYPE_NO_RASH = "type.noRash";
	public static final String MS_TYPE_GOAL = "type.goal";
	
/* ============================================================================
 * Physical Object types
 * ========================================================================= */

	public static final String PO_TYPE_DIAPER = "type.Diaper";
	public static final String PO_TYPE_WIPES = "type.Wipes";
	public static final String PO_TYPE_OINTMENT = "type.Ointment";

/* ============================================================================
 * Container types
 * ========================================================================= */

	public static final String CT_TYPE_CHANGINGTABLE = "type.changingTable";
	public static final String CT_TYPE_SIDETABLE = "type.sideTable";
	public static final String CT_TYPE_TRASHCAN = "type.trashCan";

/* ============================================================================
 * Actions!
 * ========================================================================= */

	public static final String ACTION_BRING_DIAPER = "bringDiaper";
	public static final String ACTION_BRING_OINTMENT = "bringOintment";
	public static final String ACTION_NULL = "null";
	
/*==============================================================================
 * Human Actions
 ===============================================================================*/
	public static final String HUMAN_ACTION_REMOVE_DIAPER = "removeDiaper";
	public static final String HUMAN_ACTION_APPLY_OINTMENT = "applyOintment";
	public static final String HUMAN_ACTION_CHANGE_DIAPER = "changeDiaper";

/* ============================================================================
 * Objects
 * ========================================================================= */

	public static final String OBJ_HUMAN = "human";
	public static final String OBJ_ROBOT = "robot";
	public static final String OBJ_BABY = "baby";
	
	public static final String OBJ_OLDDIAPER = "olddiaper";
	public static final String OBJ_NEWDIAPER = "newdiaper";
	public static final String OBJ_WIPES = "wipes";
	public static final String OBJ_OINTMENT = "ointment";
	public static final String OBJ_CHANGINGTABLE = "changingtable";
	public static final String OBJ_SIDETABLE = "sidetable";
	public static final String OBJ_TRASHCAN = "trashcan";
	
	// observation object
	public static final String OBSERVATION_OBJ = "observation.obj";
	
//	public static final String OBJ_STATE_START = "obj.stateStart";
//	public static final String OBJ_STATE_NO_RASH = "obj.stateNoRash";
//	public static final String OBJ_STATE_RASH = "obj.stateRash";
//	public static final String OBJ_STATE_GOAL = "obj.stateGoal";

/* ============================================================================
 * Propositional Functions
 * ========================================================================= */
	
	public static final String PROP_IN_QUERIED_STATE = "prop.inQueriedState";
	public static final String PROP_IN_GOAL_STATE = "prop.inGoalState";
/*=============================================================================
 * Observations
 */
	public static final String OBS_START = "obs.start";
	public static final String OBS_RASH = "obs.rash";
	public static final String OBS_NO_RASH = "obs.no_rash";
	public static final String OBS_GOAL = "obs.goal";
}
