import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;

public class AthenaAgent extends Agent {
	private POMDPDomain domain;
	private List<Double> currentBeliefState;
	private HLPlanner hlplanner;
	private LLPlanner llplanner;

	public AthenaAgent(Environment e) {
		super(e);
		init();
	}

	public void init() {
		hlplanner = new HLPlanner();
		llplanner = new LLPlanner();

		hlplanner.init();

		for(int i = 0; i < domain.getAllStates().size(); ++i) {
			currentBeliefState.add(1/domain.getAllStates().size());
		}

		domain = (POMDPDomain)(new POMDPDiaperDomain().generateDomain());
	}

	public void run() {

		while(!inGoalState()) {
			String bestPOMDPAction = hlplanner.getBestAction(currentBeliefState);
			List<Tuple<Action, String[]>> bestOOMDPActions = llplanner.convert(bestPOMDPAction);
			for(Tuple<Action, String[]> tup : bestOOMDPActions) {
				environment.perform(tup.getX(), tup.getY());
			}
			Observation o = environment.observe();
			currentBeliefState = forward(currentBeliefState, o, bestPOMDPAction, {});
		}
		System.out.println("Done!");
	}

	public boolean inGoalState() {
		List<POMDPState> allStates = domain.getAllStates();
		return currentObservation.getProbability(allStates.get(allStates))
	}

	private List<Double> forward(List<Double> prevBeliefState, Observation observation, Action action, String[] params) {
		List<POMDPState> allStates = domain.getAllStates();
		List<Double> newBeliefState = new ArrayList<Double>();

		for(int sPrimeIndex = 0; sPrimeIndex < allStates.size(); sPrimeIndex++) {
			double sum = 0;
			double obsProb = observation.getProbability(allStates.get(sPrimeIndex), null);

			for(int currStateIndex = 0; currStateIndex < allStates.size(); currStateIndex++) {
				double tprob = getTransitionProbability(allStates.get(currStateIndex), allStates.get(sPrimeIndex), action, params);
				tprob *= currentBeliefState.get(currStateIndex);
				sum += tprob;
			}

			sum *= obsProb;
			newBeliefState.set(sPrimeIndex, sum);
		}

		normalizeList(newBeliefState);
		return newBeliefState;
	}

	private double getTransitionProbability(State s1, State s2, Action a, String[] params) {
		List<TransitionProbability> tplist = a.getTransitions(s1, params);
		for(TransitionProbability tp : tplist) {
			if(tp.s.equals(s2)) {
				return tp.p;
			}
		}
		return 0.0;
	}

	public void normalizeList(List<Double> in) {
		double sum = 0;
		for(int i = 0; i < in.size(); ++i) {
			sum += in.get(i);
		}
		for(int i = 0; i < in.size(); ++i) {
			in.set(i, in.get(i)/sum);
		}
	}
}