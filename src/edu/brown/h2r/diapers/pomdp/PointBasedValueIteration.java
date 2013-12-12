package edu.brown.h2r.diapers.pomdp;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.oomdp.core.TransitionProbability;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;

import burlap.behavior.statehashing.StateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;

import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.athena.namespace.P;

public class PointBasedValueIteration {

	private POMDPDomain domain;
	private StateHashFactory hash_factory;
	private RewardFunction reward_function;
	private double gamma = -1;
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
	public PointBasedValueIteration(POMDPDomain domain, StateHashFactory hashFactory, List<State> states, RewardFunction rf, double gamma, List<List<Double>> belief_points, double maxDelta, int maxIterations) {
		this.domain = domain;
		this.reward_function = rf;
		this.hash_factory = hashFactory;
		this.gamma = gamma;
		this.max_delta = maxDelta;
		this.max_iterations = maxIterations;
		this.states = states;
		this.belief_points = belief_points;

		this.num_states = states.size();
		this.num_belief_points = belief_points.size();
	}

	public List<Tuple<GroundedAction, double[]>> doValueIteration() {
		if(!this.allInitialized()) {
			System.err.println("Cannot do value iteration because the following information has not been provided: " + this.reportUninitializedValues());
			return null;
		}

		// System.out.println("NUMBER OF STATES " + num_states);
		// System.out.println("STATES " + states.size());

		// System.out.println("Gamma:" + gamma);

		List<Tuple<GroundedAction, double[]>> returnVectors = new ArrayList<Tuple<GroundedAction, double[]>>();
		for(int k = 0; k < num_belief_points; ++k) {
			returnVectors.add(null);
		}

		double [][] vectorSetReward = null;
		double [][][][] vectorSetActionOb = null;
		double [][][] vectorSetActionBelief = null;

		for (int i = 0; i< this.max_iterations; i++){

			this.num_belief_points = this.belief_points.size();

			vectorSetReward = new double[num_actions][num_observations];
			if(i != 0) vectorSetActionOb = new double[num_actions][num_observations][returnVectors.size()][num_states];
			vectorSetActionBelief = new double[num_actions][num_belief_points][num_states];

			for(int stateIndex = 0; stateIndex < num_states; ++stateIndex) {
				for(int actionIndex = 0; actionIndex < num_actions; ++actionIndex) {
					vectorSetReward[actionIndex][stateIndex] = reward_function.reward(states.get(stateIndex), actions.get(actionIndex), null);
					//System.out.println("Iteration " + i + ", VSR[" + actionIndex + "][" +stateIndex + "]: " + vectorSetReward[actionIndex][stateIndex]);
					//System.out.println("    " + "Action at index " + actionIndex + " is " + actions.get(actionIndex).action.getName());
					//System.out.println("    " + "State at index " + stateIndex + " is\n" + states.get(stateIndex));
					if(i == 0) continue;

					for(int observationIndex = 0; observationIndex < num_observations; ++observationIndex) {
						for(int rvIndex = 0; rvIndex < returnVectors.size(); ++rvIndex) {
							double nextStateSum = 0.0;

							for(int sPrimeIndex = 0; sPrimeIndex < num_states; ++sPrimeIndex) {
								
								double prob = 0.0;

								List<TransitionProbability> tprobs = actions.get(actionIndex).action.getTransitions(states.get(stateIndex), new String[]{""});
								for(TransitionProbability tp : tprobs) {

									if(this.statesAreEqual(tp.s, states.get(sPrimeIndex))) {
										prob = tp.p;
										// if(actions.get(actionIndex).action.getName().equals(P.ACTION_SX_ADVANCE) && tp.p == 1) {
										// System.out.println("----------");
										// System.out.println("S  = " + states.get(stateIndex).getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
										// System.out.println("A  = " + actions.get(actionIndex).action.getName());
										// System.out.println("S' = " + states.get(sPrimeIndex).getObject(P.OBJ_HOLDER).getAllRelationalTargets(P.ATTR_MENTAL_STATE).toArray()[0]);
										// System.out.println("Probability = " + tp.p); }
										//System.out.println("probability["+stateIndex+"]["+actionIndex+"]["+sPrimeIndex+"]= " + prob);
									}
								}
								nextStateSum += prob * observations.get(observationIndex).getProbability(states.get(sPrimeIndex), actions.get(actionIndex)) * returnVectors.get(rvIndex).getY()[sPrimeIndex];
							}

							vectorSetActionOb[actionIndex][observationIndex][rvIndex][stateIndex] = gamma * nextStateSum;
						}
					}
				}
			}

			for(int actionIndex = 0; actionIndex < num_actions; ++actionIndex) {
				for(int beliefIndex = 0; beliefIndex < num_belief_points; ++beliefIndex) {

					double[] sum = new double[num_states];

					for(int j = 0; j < num_states; ++j) {
						sum[j] = 0;
					}

					if(i == 0) continue;

					double[] productArray = null;
					if(i != 0) productArray = new double[returnVectors.size()];

					for(int observationIndex = 0; observationIndex < num_observations; ++observationIndex) {
						for(int rvIndex = 0; rvIndex < returnVectors.size(); ++rvIndex) {
							double acc = 0.0;
							for(int stateIndex = 0; stateIndex < num_states; ++stateIndex) {
								acc += vectorSetActionOb[actionIndex][observationIndex][rvIndex][stateIndex] * belief_points.get(beliefIndex).get(stateIndex);
								//System.out.println("Belief Point output"+beliefIndex  +" " + stateIndex+" |"+ belief_points.get(beliefIndex).get(stateIndex) + "|");
							}

							//System.out.println("RVINDEX " + rvIndex + ", ACC " + acc);
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

						//System.out.println("ObsIndex: " + observationIndex + ", Max Val: " + max_value + ", Max Ind: " + max_index);

						for(int j = 0; j < num_states; ++j) {
							sum[j] += vectorSetActionOb[actionIndex][observationIndex][max_index][j];
						}
					}

					for(int j = 0; j < num_states; ++j) {
						vectorSetActionBelief[actionIndex][beliefIndex][j] = vectorSetReward[actionIndex][j] + sum[j];
					}
				}
			}

		//	System.out.println("Iteration Number " + i);
			for(int beliefIndex = 0; beliefIndex < num_belief_points; ++beliefIndex) {
			//	System.out.println("   Belief Index: " + beliefIndex);
				double[] productArray = new double[num_actions];
				for(int actionIndex = 0; actionIndex < num_actions; ++actionIndex) {
					//System.out.println("      Action Index: " + actionIndex);
					double acc = 0.0;
					for(int j = 0; j < num_states; ++j) {
						//System.out.println("j: " + j + ", BP[beliefIndex][j]: " + belief_points.get(beliefIndex).get(j));
						acc += vectorSetActionBelief[actionIndex][beliefIndex][j] * belief_points.get(beliefIndex).get(j);
					}
					//System.out.println("      P.A. at that index: " + acc);
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
				returnVectors.set(beliefIndex, new Tuple<GroundedAction, double[]>(actions.get(max_index), vectorSetActionBelief[max_index][beliefIndex]));
			}

		}

		return returnVectors;
	}

	private boolean statesAreEqual(State s1, State s2) {
		StateHashTuple st1 = hash_factory.hashState(s1);
		StateHashTuple st2 = hash_factory.hashState(s2);
		return st1.equals(st2);
	}

	private List<Double> doubArrConv(double[] d) {
		List<Double> ret = new ArrayList<Double>();
		for(int i = 0; i < d.length; ++i) {
			ret.add(new Double(d[i]));
		}
		return ret;
	}

	private boolean allInitialized() {
		return this.domain != null && this.reward_function != null
			&& this.gamma != -1 && this.max_delta != -1 && this.max_iterations != -1 && this.states != null
			&& this.belief_points != null && this.hash_factory != null;
	}

	private String reportUninitializedValues() {
		String result = "[";

		if(this.domain == null) result += "domain, ";
		if(this.reward_function == null) result += "reward function, ";
		if(this.gamma == -1) result += "gamma, ";
		if(this.max_delta == -1) result += "max delta, ";
		if(this.max_iterations == -1) result += "max iterations, ";
		if(this.states == null) result += "states, ";
		if(this.hash_factory == null) result += "hash factory, ";
		if(this.belief_points == null) result += "belief points, ";

		return result + "]";
	}

/* ============================================================================
 * Single-use setters for instance variables.
 * ========================================================================= */
	
	public boolean setBeliefPoints(List<List<Double>> belief_points) {
		if(this.belief_points == null) {
			this.belief_points = belief_points;
			this.num_belief_points = this.belief_points.size();
			return true;
		} else {
			System.err.println("Attempt to set belief points of PBVI instance who is already bound to belief points ignored");
			return false;
		}
	}

	public boolean setHashFactory(StateHashFactory hf) {
		if(this.hash_factory == null) {
			this.hash_factory = hf;
			return true;
		} else {
			System.err.println("Attempt to set hash factory of PBVI instance who is already bound to a hash factory ignored");
			return false;
		}
	}

	public boolean setStates(List<State> states) {
		if(this.states == null) {
			this.states = states;
			this.num_states = this.states.size();
			return true;
		} else {
			System.err.println("Attempt to set states of PBVI instance who is already bound to a list of states ignored");
			return false;
		}
	}

	public boolean setDomain(POMDPDomain domain) {
		if(this.domain == null) {

			this.domain = domain;
			this.observations = this.domain.getObservations();
			this.num_observations = this.observations.size();

			POMDPState s = this.domain.getExampleState();
			this.actions = s.getAllGroundedActionsFor(this.domain.getActions());
			// for(GroundedAction a : this.actions) {
			// 	System.out.println(a.action.getName());
			// }
			this.num_actions = this.actions.size();

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

	public boolean setGamma(double gamma) {
		if(this.gamma == -1) {
			this.gamma = gamma;
			return true;
		} else {
			System.err.println("Attempt to set gamma of PBVI instance who is already bound to a gamma ignored");
			return false;
		}
	}

	public boolean setMaxDelta(double md) {
		if(this.max_delta == -1) {
			this.max_delta = md;
			return true;
		} else {
			System.err.println("Attempt to set max delta of PBVI instance who is already bound to a max delta ignored");
			return false;
		}
	}

	public boolean setMaxIterations(int mi) {
		if(this.max_iterations == -1) {
			this.max_iterations = mi;
			return true;
		} else {
			System.err.println("Attempt to set max iterations of PBVI instance who is already bound to max iterations ignored");
			return false;
		}
	}
}

