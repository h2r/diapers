package edu.brown.h2r.diapers.testdomain;

import java.util.Map;
import java.util.HashMap;

public class Names {
	public static final String ATTR_MENTAL_STATE = "attr.mentalState";
	public static final String ATTR_CONTENTS = "attr.contents";
	public static final String ATTR_CONTAINER = "attr.container";
	public static final String ATTR_OPEN = "attr.open";
	public static final String ATTR_RASH = "attr.rash";

	public static final String CLASS_HUMAN = "class.human";
	public static final String CLASS_BABY = "class.baby";
	public static final String CLASS_CONTENT = "class.content";
	public static final String CLASS_CONTAINER = "class.container";
	public static final String CLASS_GOAL = "class.goal";

	public static final String ACTION_BRING = "action.bring";
	public static final String ACTION_ASK = "action.ask";
	public static final String ACTION_OPEN = "action.open";
	public static final String ACTION_WAIT = "action.wait";

	public static final String PROP_FIND = "prop.find";
	public static final String PROP_RASH = "prop.rash";
	public static final String PROP_OPEN = "prop.open";

	public static final String OBJ_OLD_CLOTHES = "obj.oldClothes";
	public static final String OBJ_NEW_CLOTHES = "obj.newClothes";
	public static final String OBJ_LOTION = "obj.lotion";
	public static final String OBJ_CAREGIVER = "obj.caregiver";
	public static final String OBJ_BABY = "obj.baby";
	public static final String OBJ_CHANGING_TABLE = "obj.changingTable";
	public static final String OBJ_SIDE_TABLE = "obj.sideTable";
	public static final String OBJ_HAMPER = "obj.hamper";
	public static final String OBJ_DRESSER = "obj.dresser";

	public static Map<String, String> prettyNames = new HashMap<String, String>();

	static {
		prettyNames.put(OBJ_OLD_CLOTHES, "the old clothes");
		prettyNames.put(OBJ_NEW_CLOTHES, "the new clothes");
		prettyNames.put(OBJ_LOTION, "the lotion");
		prettyNames.put(OBJ_CAREGIVER, "the caregiver");
		prettyNames.put(OBJ_BABY, "the baby");
		prettyNames.put(OBJ_CHANGING_TABLE, "the changing table");
		prettyNames.put(OBJ_SIDE_TABLE, "the side table");
		prettyNames.put(OBJ_HAMPER, "the hamper");
		prettyNames.put(OBJ_DRESSER, "the dresser");
	}

	public static String prettify(String name) {
		String res = prettyNames.get(name);
		return res == null ? "???" : res;
	}
}
