package edu.brown.h2r.diapers.tiger;

public class PointBasedValueIteration {

	private Domain domain;
	private RewardFunction reward_function
	private TerminalFunction terminal_function;
	private double gamma = -1;
	private BeliefStateHashFactory hash_factory;
	private double max_delta = -1;
	private int max_iterations = -1;

	private List<State> states;
	private List<GroundedAction> actions;
	private List<Observation> observations;
	private List<List<Double>> belief_points;

	private int num_states;
	private int num_actions;
	private int num_observations;
	private int num_belief_points;

	public PointBasedValueIteration() {}
	public PointBasedValueIteration(Domain domain, List<State> states, RewardFunction rf, TerminalFunction tf, double gamma, List<List<Double>> belief_points, double maxDelta, int maxIterations) {
		this.domain = domain;
		this.reward_function = rf;
		this.terminal_function = tf;
		this.gamma = gamma;
		this.hash_factory = hashFactory;
		this.max_delta = maxDelta;
		this.max_iterations = maxIterations;
		this.states = states;
		this.belief_points = belief_points;

		this.num_states = states.size();
		this.num_belief_points = belief_points.size();
	}

	public List<Tuple<GroundedAction, List<Double>>> doValueIteration() {
		if(!this.allInitialized()) {
			System.err.println("Cannot do value iteration because the following information has not been provided: " + this.reportUninitializedValues());
			return null;
		}

		List<Tuple<GroundedAction, List<Double>>> returnVectors = new ArrayList<Tuple<GroundedAction, List<Double>>>();

		double [][] vectorSetReward;
		double [][][][] vectorSetActionOb;
		double [][][] vectorSetActionBelief;

		for (int i = 0; i< this.max_iterations; i++){

			this.num_belief_points = this.belief_points.size()

			vectorSetReward = new double[num_actions][num_observations];
			if(i != 0) vectorSetActionOb = new double[num_actions][num_observations][returnVectors.size()][num_states];
			vectorSetActionBelief = new double[num_actions][num_belief_points][num_states];

			for(stateIndex = 0; stateIndex < num_states; ++stateIndex) {
				for(actionIndex = 0; actionIndex < num_actions; ++actionIndex) {
					vectorSetReward[actionIndex][stateIndex] = reward_function.reward(states[stateIndex], actions[actionindex], null);

					if(i == 0) continue;

					for(observationIndex = 0; observationIndex < num_observations; ++observationIndex) {
						for(rvIndex = 0; rvIndex < returnVectors.size(); ++rvIndex) {
							double nextStateSum = 0.0;

							for(sPrimeIndex = 0; sPrimeIndex < num_states; ++sPrimeIndex) {\
								
								double prob = 0.0;

								List<TransitionProbability> tprobs = actions[actionindex].getTransitions(states[stateIndex], "");
								for(TransitionProbability tp : tprobs) {
									if(tp.s == states[stateIndex]) {
										prob = tp.p;
									}
								}
								nextStateSum += prob * observations[observationIndex].getProbability(states[sPrimeIndex], actions[actionIndex]) * returnVectors.get(rvIndex).getY().get(sPrimeIndex);
							}

							vectorSetActionOb[actionIndex][observationIndex][rvIndex][stateIndex] = gamma * nextStateSum;
						}
					}
				}
			}

			for(actionIndex = 0; actionIndex < num_actions; ++action_index) {
				for(beliefIndex = 0; beliefIndex < num_belief_points; ++beliefIndex) {

					double[] sum = new double[num_states];

					for(int j = 0; j < num_states; ++j) {
						sum[j] = 0;
					}

					if(i == 0) continue;

					if(i != 0) double[] productArray = new double[returnVectors.size()];

					for(observationIndex = 0; observationindex < num_observations; ++observationIndex) {
						for(rvIndex = 0; rvIndex < returnVectors.size(); ++rvIndex) {
							double acc = 0.0;
							for(stateIndex = 0; stateIndex < num_states; ++stateIndex) {
								acc += vectorSetActionOb[actionIndex][observationIndex][rvIndex][stateIndex] * belief_points.get(beliefIndex).get(stateIndex);
							}

							productArray[rvIndex] = acc;
						}

						double max_value = Double.NEGATIVE_INFINITY;
						int max_index = -1;

						for(int j = 0; j < returnVectors.size(); ++j) {
							double test = productArray[j];
							if(test > max_value) {
								max_value = test;
								max_index = j;
							}
						}

						for(int j = 0; j < num_states; ++j) {
							sum[j] += vectorSetActionOb[actionIndex][observationIndex][max_index][j];
						}
					}

					for(int j = 0; j < num_states; ++j) {
						vectorSetActionBelief[actionIndex][beliefIndex][j] = vectorSetReward[actionIndex][j] + sum[j];
					}
				}
			}

			for(beliefIndex = 0; beliefIndex < num_belief_points; ++beliefIndex) {
				double[] productArray = new double[num_actions];
				for(actionIndex = 0; actionIndex < num_actions; ++actionIndex) {
					double acc = 0.0;
					for(int j = 0; j < num_states; ++j) {
						acc += vectorSetActionBelief[actionIndex][beliefIndex][j] * belief_points.get(beliefIndex).get(j);
					}
					productArray[actionIndex] = acc;
				}

				double max_value = Double.NEGATIVE_INFINITY;
				int max_index = -1;

				for(int j = 0; j < num_actions; ++j) {
					double test = productArray[j];
					if(test > max_value) {
						max_value = test;
						max_index = j;
					}
				}

				returnVectors.set(beliefIndex, new Tuple<GroundedAction, List<Double>>(actions[max_index], Arrays.asList(vectorSetActionBelief[max_index][beliefIndex])));
			}
		}

		return returnVectors;
	}

	private boolean allInitialized() {
		return this.domain != null && this.reward_function != null && this.terminal_function != null
			&& this.gamma != -1 && this.hash_factory != null && this.max_delta != -1 && this.max_iterations != -1;
	}

	private String reportUninitializedValues() {
		String result = "[";

		if(this.domain == null) result += "domain, ";
		if(this.reward_function == null) result += "reward function, ";
		if(this.terminal_function == null) result += "terminal function, ";
		if(this.gamma == -1) result += "gamma, ";
		if(this.hash_factory == null) result += "hash factory, ";
		if(this.max_delta == -1) result += "max delta, ";
		if(this.max_iterations == -1) result += "max iterations, ";
		if(this.states == null) result += "states, ";
		if(this.belief_points) result += "belief points, ";

		return result + "]";
	}

/* ============================================================================
 * Single-use setters for instance variables.
 * ========================================================================= */
	
	public boolean setBeliefPoints(List<List<Double>> belief_points) {
		if(this.belief_points == null) {
			this.belief_points = belief_points;
			return true;
		} else {
			System.err.println("Attempt to set belief points of PBVI instance who is already bound to belief points ignored");
			return false;
		}
	}

	public boolean setStates(List<State> states) {
		if(this.states == null) {
			this.states = states;
			return true;
		} else {
			System.err.println("Attempt to set states of PBVI instance who is already bound to a list of states ignored");
			return false;
		}
	}

	public boolean setDomain(Domain domain) {
		if(this.domain == null) {

			this.domain = domain;
			this.actions = this.domain.getActions();
			this.observations = this.domain.getObservations();
			this.num_actions = this.actions.size();
			this.num_observations = this.observations.size();

			return true;
		} else {
			System.err.println("Attempt to set domain of PBVI instance who is already bound to a domain ignored.");
			return false;
		}
	}

	public boolean setRewardFunction(RewardFunction rf) {
		if(this.reward_function == null) {
			this.reward_function = rf;
			return true;
		} else {
			System.err.println("Attempt to set reward function of PBVI instance who is already bound to a reward function ignored");
			return false;
		}
	}

	public boolean setTerminalFunction(TerminalFunction tf) {
		if(this.terminal_function == null) {
			this.terminal_function = tf;
			return true;
		} else {
			System.err.println("Attempt to set terminal function of PBVI instance who is already bound to a terminal funciton ignored");
			return false;
		}
	}

	public boolean setGamma(double gamma) {
		if(this.gamma == -1) {
			this.gamma = gamma;
			return true;
		} else {
			System.err.println("Attempt to set gamma of PBVI instance who is already bound to a gamma ignored");
			return false;
		}
	}
	
	public boolean setHashFactory(BeliefStateHashFactory hf) {
		if(this.hash_factory == null) {
			this.hash_factory = hf;
			return true;
		} else {
			System.err.println("Attempt to set hash factory of PBVI instance who is already bound to a hash factory ignored");
			return false;
		}
	}

	public boolean setMaxDelta(double md) {
		if(this.maxDelta == -1) {
			this.maxDelta = md;
			return true;
		} else {
			System.err.println("Attempt to set max delta of PBVI instance who is already bound to a max delta ignored");
			return false;
		}
	}

	public boolean setMaxIterations(int mi) {
		if(this.maxIterations == -1) {
			this.maxIterations = mi;
			return true;
		} else {
			System.err.println("Attempt to set max iterations of PBVI instance who is already bound to max iterations ignored");
			return false;
		}
	}
}

