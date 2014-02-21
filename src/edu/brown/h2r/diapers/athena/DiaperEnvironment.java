public class DiaperEnvironment implements Environment {
	private State currentState;

	public DiaperEnvironment(State initialState) {
		currentState = initialState;
	}

	public Observation observe() {

		final String caregiverRealState = (String) st.getObject(S.OBJ_CAREGIVER).getAllRelationalTargets(S.ATTR_MENTAL_STATE).toArray()[0];

		return new Observation() {
			@Override public double getProbability(State s, GroundedAction a) {
				ObjectInstance holder = s.getObject(P.OBJ_HOLDER);
				String state = (String) holder.getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0];

				if(state.equals(caregiverRealState)) {
					return 1;
				}
				return 0;
			}
		}
	}

	public void perform(Action a, String[] params) {
		if(a.applicableInState(currentState, params)) {
			currentState = a.performActionHelper(currentState, params);
		} else {
			System.err.println("Agent requested to perform an action which is not possible in the current state.");
		}
	}
}