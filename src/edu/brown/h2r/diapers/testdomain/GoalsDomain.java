package edu.brown.h2r.diapers.testdomain;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.util.Util;

import burlap.oomdp.auxiliary.DomainGenerator;
import burlap.oomdp.core.Attribute;
import burlap.oomdp.core.Domain;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.PropositionalFunction;
import burlap.oomdp.core.State;

import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.explorer.TerminalExplorer;

import java.util.List;
import java.util.ArrayList;

public class GoalsDomain implements DomainGenerator {

		/**
		 * This is a domain generator for a simplified but hopefully still valid diaper domain.  It creates
		 * a world containing two tables, a hamper, a dresser, and the human caregiver.  Its observations
		 * are parametrized by an array of strings representing spoken word.  As both the observation and 
		 * state representations are factored, this domain cannot return a list of all its possible states or
		 * all its possible observations, and as such PBVI (and other full-width solvers) are not applicable.
		 */
		public Domain generateDomain() {

				Domain domain = new POMDPDomain() {
					@Override public POMDPState getExampleState() {
						return GoalsDomain.sampleInitialStates(this);
					}
				};

				Attribute mentalState = new Attribute(domain, Names.ATTR_MENTAL_STATE, Attribute.AttributeType.RELATIONAL);
				Attribute contents = new Attribute(domain, Names.ATTR_CONTENTS, Attribute.AttributeType.MULTITARGETRELATIONAL);
				Attribute container = new Attribute(domain, Names.ATTR_CONTAINER, Attribute.AttributeType.RELATIONAL);
				Attribute open = new Attribute(domain, Names.ATTR_OPEN, Attribute.AttributeType.DISC);
				Attribute rash = new Attribute(domain, Names.ATTR_RASH, Attribute.AttributeType.DISC);
				open.setDiscValuesForRange(0, 1, 1);
				rash.setDiscValuesForRange(0, 1, 1);

				//The world consists of the human participant, the contents and containers and surfaces, and 
				//goal objects representing the human's mental state
				ObjectClass human = new ObjectClass(domain, Names.CLASS_HUMAN);
				ObjectClass baby = new ObjectClass(domain, Names.CLASS_BABY);
				ObjectClass content = new ObjectClass(domain, Names.CLASS_CONTENT);
				ObjectClass containerClass = new ObjectClass(domain, Names.CLASS_CONTAINER);
				ObjectClass goal = new ObjectClass(domain, Names.CLASS_GOAL);

				//The caregiver has a mental state
				human.addAttribute(mentalState);

				//The baby can have a rash or not
				baby.addAttribute(rash);

				//Containers have contents, and whether they are open or closed. 
				containerClass.addAttribute(contents);
				containerClass.addAttribute(open);

				//Contents have a current container
				content.addAttribute(container);

				//The robot can bring an object from one location to another, 
				//ask the human a question, open a container, or do nothing.
				Action bring = new BringAction(domain, Names.ACTION_BRING);
				Action ask = new AskAction(domain, Names.ACTION_ASK);
				Action openAction = new OpenAction(domain, Names.ACTION_OPEN);
				Action wait = new WaitAction(domain, Names.ACTION_WAIT);

				//Set up Prop. Functions for the human to think about
				PropositionalFunction findFun = new FindPropFun(domain, Names.PROP_FIND);
				PropositionalFunction rashFun = new RashPropFun(domain, Names.PROP_RASH);
				PropositionalFunction openFun = new OpenPropFun(domain, Names.PROP_OPEN);

				return domain;
		}

		/**
		 * Action for bringing an object to a container.  The container must be open for this to work.
		 */
		public class BringAction extends Action {
			public BringAction(Domain domain, String name) {
				super(name, domain, new String[]{Names.CLASS_CONTENT, Names.CLASS_CONTAINER});
			}

			@Override
			public boolean applicableInState(State st, String[] params) {
				PropositionalFunction open = domain.getPropFunction(Names.PROP_OPEN);
				return open.isTrue(st, st.getObject(params[0]).getStringValForAttribute(Names.ATTR_CONTAINER)) && open.isTrue(st, params[1]);
			}

			@Override
			protected State performActionHelper(State st, String[] params) {
				POMDPState ps = new POMDPState(st);
			
				grabObject(ps, ps.getObject(params[0]));
				placeObject(ps.getObject(params[0]), ps.getObject(params[1]));

				ps.setReward(-10);
				ps.setObservation(new Observation(domain, "null"));

				caregiverThink(domain, ps);

				return ps;
			}

			private void grabObject(State st, ObjectInstance obj) {
				ObjectInstance oldContainer = st.getObject(obj.getStringValForAttribute(Names.ATTR_CONTAINER));
				oldContainer.removeRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
				obj.clearRelationalTargets(Names.ATTR_CONTAINER);
			}

			private void placeObject(ObjectInstance obj, ObjectInstance cnt) {
				obj.addRelationalTarget(Names.ATTR_CONTAINER, cnt.getName());
				cnt.addRelationalTarget(Names.ATTR_CONTENTS, obj.getName());
			}
		}

		/**
		 * Action for querying the caregiver for an observation.
		 */
		public class AskAction extends Action {
			public AskAction(Domain domain, String name) {
				super(name, domain, new String[]{});
			}

			@Override
			protected State performActionHelper(State st, String[] params) {
				POMDPState ps = new POMDPState(st);
				ps.setReward(-2);
				ps.setObservation(GoalsDomain.makeObservationFor(domain, st));
				caregiverThink(domain, ps);
				return ps;
			}
		}

		/**
		 * Action for opening a container. Only applicable if the container is closed.
		 */
		public class OpenAction extends Action {
			public OpenAction(Domain domain, String name) {
				super(name, domain, new String[]{Names.CLASS_CONTAINER});
			}

			@Override
			public boolean applicableInState(State st, String[] params) {
				return st.getObject(params[0]).getStringValForAttribute(Names.ATTR_OPEN).equals("0");
			}

			@Override
			protected State performActionHelper(State st, String[] params) {
				POMDPState ps = new POMDPState(st);
				
				ps.getObject(params[0]).setValue(Names.ATTR_OPEN, 1);
				ps.setObservation(new Observation(domain, "null"));
				ps.setReward(-10);
				caregiverThink(domain, ps);
				return ps;
			}
		}

		/**
		 * Action for not doing anything.
		 */
		public class WaitAction extends Action {
			public WaitAction(Domain domain, String name) {
				super(name, domain, new String[]{});
			}

			@Override
			protected State performActionHelper(State st, String[] params) {
				POMDPState ps = new POMDPState(st);
				ps.setObservation(new Observation(domain, "null"));
				ps.setReward(-15);
				caregiverThink(domain, ps);
				return ps;
			}
		}

		public static Observation makeObservationFor(Domain d, State s) {
			String caregiverMentalState = s.getObject(Names.OBJ_CAREGIVER).getStringValForAttribute(Names.ATTR_MENTAL_STATE);

			return new Observation(d, caregiverMentalState);
		}

		public static POMDPState sampleInitialStates(Domain d) {
			POMDPState s = new POMDPState();

			ObjectClass humanClass = d.getObjectClass(Names.CLASS_HUMAN);
			ObjectClass babyClass = d.getObjectClass(Names.CLASS_BABY);
			ObjectClass contentClass = d.getObjectClass(Names.CLASS_CONTENT);
			ObjectClass containerClass = d.getObjectClass(Names.CLASS_CONTAINER);
			
			ObjectInstance caregiver = new ObjectInstance(humanClass, Names.OBJ_CAREGIVER);
			ObjectInstance baby = new ObjectInstance(babyClass, Names.OBJ_BABY);

			ObjectInstance lotion = new ObjectInstance(contentClass, Names.OBJ_LOTION);
			ObjectInstance oldClothes = new ObjectInstance(contentClass, Names.OBJ_OLD_CLOTHES);
			ObjectInstance newClothes = new ObjectInstance(contentClass, Names.OBJ_NEW_CLOTHES);

			ObjectInstance changingTable = new ObjectInstance(containerClass, Names.OBJ_CHANGING_TABLE);
			ObjectInstance hamper = new ObjectInstance(containerClass, Names.OBJ_HAMPER);
			ObjectInstance sideTable = new ObjectInstance(containerClass, Names.OBJ_SIDE_TABLE);
			ObjectInstance dresser = new ObjectInstance(containerClass, Names.OBJ_DRESSER);

			//All the tables are "open", the dresser is closed and must be opened
			changingTable.setValue(Names.ATTR_OPEN, 1);
			hamper.setValue(Names.ATTR_OPEN, 1);
			sideTable.setValue(Names.ATTR_OPEN, 1);
			dresser.setValue(Names.ATTR_OPEN, 0);

			//Flip a coin to see if the baby gets a rash
			if(new java.util.Random().nextBoolean()) {
				baby.setValue(Names.ATTR_RASH, 1);
			} else {
				baby.setValue(Names.ATTR_RASH, 0);
			}

			//Put objects in initial configuration
			placeObject(lotion, sideTable);
			placeObject(oldClothes, changingTable);
			placeObject(newClothes, dresser);

			addObjects(s, caregiver, baby, lotion, oldClothes, newClothes, changingTable, hamper, sideTable, dresser);
			caregiverThink(d, s);
			return s;
		}

		public static void addObjects(State s, ObjectInstance... objs) {
			for(ObjectInstance object : objs) {
				s.addObject(object); 
			}
		}

		public static void placeObject(ObjectInstance content, ObjectInstance container) {
			content.addRelationalTarget(Names.ATTR_CONTAINER, container.getName());
			container.addRelationalTarget(Names.ATTR_CONTENTS, content.getName());
		}

		public static void caregiverThink(Domain d, State s) {
			ObjectInstance caregiver = s.getObject(Names.OBJ_CAREGIVER);
			ObjectClass goalClass = d.getObjectClass(Names.CLASS_GOAL);
			
			ObjectInstance myGoal = null;
			POMDPState ps = (POMDPState) s;

			//If the old clothing is still at the changing table, we need to get that to the hamper
			if(!d.getPropFunction(Names.PROP_FIND).isTrue(s, new String[]{Names.OBJ_HAMPER, Names.OBJ_OLD_CLOTHES})) {
				myGoal = new ObjectInstance(goalClass, Names.OBJ_OLD_CLOTHES + ":" + Names.OBJ_HAMPER);
				ps.setReward(ps.getReward() + 1);

			//Then, if the baby has a rash, we need the lotion
			} else if(d.getPropFunction(Names.PROP_RASH).isTrue(s, new String[]{}) && !d.getPropFunction(Names.PROP_FIND).isTrue(s, new String[]{Names.OBJ_CHANGING_TABLE, Names.OBJ_LOTION})) {
				myGoal = new ObjectInstance(goalClass, Names.OBJ_LOTION + ":" + Names.OBJ_CHANGING_TABLE);
				ps.setReward(ps.getReward() + 3);

			//Then, if the new clothes aren't at the changing table, we need them
			} else if(!d.getPropFunction(Names.PROP_FIND).isTrue(s, new String[]{Names.OBJ_CHANGING_TABLE, Names.OBJ_NEW_CLOTHES})) {
				myGoal = new ObjectInstance(goalClass, Names.OBJ_NEW_CLOTHES + ":" + Names.OBJ_CHANGING_TABLE);
				ps.setReward(ps.getReward() + 5);

			//Otherwise, the problem is solved
			} else {
				myGoal = new ObjectInstance(goalClass, "done");
				ps.setReward(ps.getReward() + 10);
			}

			caregiver.addRelationalTarget(Names.ATTR_MENTAL_STATE, myGoal.getName());
		}

		public class FindPropFun extends PropositionalFunction {
			public FindPropFun(Domain domain, String name) {
				super(name, domain, new String[]{Names.CLASS_CONTAINER, Names.CLASS_CONTENT});
			}

			@Override
			public boolean isTrue(State st, String[] params) {
				return st.getObject(params[0]).getAllRelationalTargets(Names.ATTR_CONTENTS).contains(params[1]) &&
					   st.getObject(params[1]).getStringValForAttribute(Names.ATTR_CONTAINER).equals(params[0]);
			}
		}

		public class OpenPropFun extends PropositionalFunction {
			public OpenPropFun(Domain domain, String name) {
				super(name, domain, new String[]{Names.CLASS_CONTAINER});	
			}

			@Override
			public boolean isTrue(State st, String[] params) {
				return st.getObject(params[0]).getStringValForAttribute(Names.ATTR_OPEN).equals("1");
			}
		}

		public class RashPropFun extends PropositionalFunction {
			public RashPropFun(Domain domain, String name) {
				super(name, domain, new String[]{});
			}

			public boolean isTrue(State st, String[] params) {
				return st.getObject(Names.OBJ_BABY).getStringValForAttribute(Names.ATTR_RASH).equals("1");
			}
		}
}
