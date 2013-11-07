package edu.brown.h2r.diapers;

import java.util.ArrayList;
import java.util.List;

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
	public static final String							ATT_AT = "at";
	public static final String							ATT_MOVEABLE_OBJECT_TYPE = "moveable_object_type";
	public static final String							ATT_PLACE_TYPE = "place_type";
	public static final String 							CLASS_MOVEABLE_OBJECT = "moveable_object";
	public static final String 							CLASS_PLACE = "place";
	public static final String							ACTION_MOVE = "move";
	
	public DiaperDomain() {
		}
	
	
	public Domain generateDomain() {
		Domain domain = new SADomain();
		
		List<String> moveable_object_types = new ArrayList<String>();
		moveable_object_types.add("diaper");
		moveable_object_types.add("wipes");
		moveable_object_types.add("pants");
		
		List<String> place_types = new ArrayList<String>();
		place_types.add("table");
		place_types.add("garbage");
				

				
		Attribute att_at = new Attribute(domain, ATT_AT, Attribute.AttributeType.RELATIONAL);
		Attribute moveable_object_type = new Attribute(domain, ATT_MOVEABLE_OBJECT_TYPE, Attribute.AttributeType.DISC);
		moveable_object_type.setDiscValues(moveable_object_types);
		
		
		
		ObjectClass blockClass = new ObjectClass(domain, CLASS_MOVEABLE_OBJECT);
		blockClass.addAttribute(att_at);
		blockClass.addAttribute(moveable_object_type);
		
		Attribute att_place_type = new Attribute(domain, ATT_PLACE_TYPE, Attribute.AttributeType.DISC);
		att_place_type.setDiscValues(place_types);
		
		
		ObjectClass placeClass = new ObjectClass(domain, CLASS_PLACE);
		placeClass.addAttribute(att_place_type);

		Action move= new MoveAction(ACTION_MOVE, domain);
		
		return domain;
	
	}
	
	public class MoveAction extends Action {

		public MoveAction(String name, Domain domain){
			super(name, domain, new String[]{CLASS_MOVEABLE_OBJECT, CLASS_PLACE});
		}
		
		@Override
		protected State performActionHelper(State st, String[] params) {
		
			ObjectInstance moveable_object = st.getObject(params[0]);
			ObjectInstance place = st.getObject(params[1]);
			
			moveable_object.setValue(ATT_AT, place.getName());

			
			return st;
		}
		
		
	}
	public static State getNewState(Domain d){
		State s = new State();
		ObjectClass class_place = d.getObjectClass(CLASS_PLACE);
		int i = 1;
		ObjectInstance table1 = new ObjectInstance(class_place, "table" + i++);
		table1.setValue(ATT_PLACE_TYPE, "table");
		
		ObjectInstance table2 = new ObjectInstance(class_place, "table" + i++);
		table2.setValue(ATT_PLACE_TYPE, "table");

		ObjectClass moveable_object = d.getObjectClass(CLASS_MOVEABLE_OBJECT);
		ObjectInstance diaper = new ObjectInstance(moveable_object, "diaper" + i++);
		diaper.setValue(ATT_MOVEABLE_OBJECT_TYPE, "diaper");
		diaper.setValue(ATT_AT, table1.getName());
		
		s.addObject(table1);
		s.addObject(table2);
		s.addObject(diaper);
		
		return s;
	}
	
	
	
	public static void main(String[] args) {
		DiaperDomain dg = new DiaperDomain();
		Domain domain = dg.generateDomain();
		State s = getNewState(domain);
		
		List<GroundedAction> grounded_actions = s.getAllGroundedActionsFor(domain.getAction(ACTION_MOVE));
		System.out.println("State: " + s);
		for (GroundedAction a : grounded_actions) {
			System.out.println("A: " + a);
		}
		
		TerminalExplorer exp = new TerminalExplorer(domain);
		
		exp.exploreFromState(s);
		
		
	}
	
}
