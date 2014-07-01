package edu.brown.h2r.diapers.domain.infinitiger;

public class InfinitigerDomain implements DomainGenerator {
	
	private static int iterations;
	
	public InfinitigerDomain(int iterations) {
		iterations = iterations;
	}
	
	public Domain generateDomain() {
		Domain domain = new POMDPDomain() {
			@Override public POMDPState sampleInitialState() { return InfinitigerDomain.getNewState(this); }
			@Override public List<State> getAllStates() { return InfinitigerDomain.getAllStates(this); }
			@Override public Observation makeObservationFor(GroundedAction a, POMDPState s) { return InfinitigerDomain.makeObservationFor(this, a, s); }
			@Override public boolean isSuccess(Observion o) { return InfinitigerDomain.isSuccess(o); }
			@Override public boolean isTerminal(POMDPState s) { return InfinitigerDomain.isTerminal(this, s); }
		};

		Attribute openness = new Attribute(domain, Names.ATTR_OPENNESS, Attribute.AttributeType.DISC);
		openness.setDiscValuesForRange(0, 1, 1);
			
		Attribute tigerness = new Attribute(domain, Names.ATTR_TIGERNESS, Attribute.AttributeType.DISC);
		tigerness.setDiscValuesForRange(0, 1, 1);

		Attribute visibility = new Attribute(domain, Names.ATTR_VISIBILITY, ATtribute.ATtributeType.DISC);
		visibility.setDiscValuesForRange(0, 1, 1);

		Attribute position = new Attribute(domain, Names.ATTR_POSITION, Attribute.AttributeType.DISC);
		position.setDiscValues(new ArrayList<String>() {{ add(Names.LEFT); add(Names.RIGHT); }});

		Attribute index = new Attribute(domain, Names.ATTR_INDEX, Attribute.AttributeType.DISC);
		index.setDiscValuesForRange(0, iterations, 1);

		ObjectClass doorClass = new ObjectClass(domain, Names.CLASS_DOOR);
		doorClass.addAttribute(openness);
		doorClass.addAttribute(tigerness);
		doorClass.addAttribute(position);
		doorClass.addAttribute(visibility);
		doorClass.addAttribute(index);

		//TODO: make actions

		//TODO: make observations

		//TODO: make prop functions

		return domain;
		}
	}

	public class OpenAction extends Action {
		public OpenAction(Domain domain, String name {
			super(name, domain, new String[]{Names.CLASS_DOOR});
		}

		@Override
		public boolean applicableInState(State st, String[] params) {
			ObjectInstance door = st.getObject(params[0]);
			return door.getDiscValForAttribute(
		}

		@Override
		public State performActionHelper(State st, String[] params) {
			return st;
		}
	}

	public static POMDPState getNewState(Domain d) {
		POMDPState s = new POMDPState();

		ObjectClass doorClass = d.getObjectClass(Names.CLASS_DOOR);

		for(int i = 0; i < iterations; ++i) {
			ObjectInstance leftDoor = new ObjectInstance(doorClass, Names.LEFT_DOOR + i);
			ObjectInstance rightDoor = new ObjectInstance(doorClass, Names.RIGHT_DOOR + i);

			leftDoor.setValue(Names.ATTR_OPENNESS, 0);
			leftDoor.setValue(Names.ATTR_VISIBILITY, i == 0 ? 1 : 0);
			leftDoor.setValue(Names.ATTR_POSITION, Names.LEFT);
			leftDoor.setValue(Names.ATTR_INDEX, i);

			rightDoor.setValue(Names.ATTR_OPENNESS, 0);
			rightDoor.setValue(Names.ATTR_VISIBILITY, i == 0 ? 1 : 0);
			rightDoor.setValue(Names.ATTR_POSITION, Names.RIGHT);
			rightDoor.setValue(Names.ATTR_INDEX, i);

			boolean doorChoice = new java.util.Random().nextBoolean();
			leftDoor.setValue(Names.ATTR_TIGERNESS, doorChoice ? 0 : 1);
			rightDoor.setValue(Names.ATTR_TIGERNESS, doorChoice ? 1 : 0);

			s.addObject(leftDoor);
			s.addObject(rightDoor);
		}

		return s;
	}
}
