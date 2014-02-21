package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.PointBasedValueIteration;
import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.athena.namespace.P;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;
import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.oomdp.singleagent.RewardFunction;

import java.util.List;
import java.util.ArrayList;

public class HLPlanner {

	private PointBasedValueIteration pbvi;
	private List<Tuple<GroundedAction, double[]>> result;

	private POMDPDiaperDomain diaperDomain;
	private POMDPDomain domain;
	private StateParser stateParser;
	private RewardFunction rewardFunction;
	private StateConditionTest goalCondition;
	private State initialState;
	private NameDependentStateHashFactory hashFactory;

	private List<List<Double>> beliefPoints;

	public void init() {
		diaperDomain = new POMDPDiaperDomain();
		domain = (POMDPDomain) diaperDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		initialState = diaperDomain.getNewState(domain);
		hashFactory = new NameDependentStateHashFactory();
		rewardFunction = new HLPRewardFunction();
		beliefPoints = makeBeliefPoints(domain.getAllStates().size(), 4);

		pbvi = makePBVIInstance(beliefPoints);
		result = pbvi.doValueIteration();
	}

	public String getBestAction(List<Double> beliefState) {
		return pbvi.findClosestBeliefPointIndex(beliefState, result);
	}

	public class HLPRewardFunction implements RewardFunction {
		public double reward(State s, GroundedAction a, State sprime) {
			if(a.toString().equals(P.ACTION_SPEAK)) {
				return -0.5;
			}
			return -1;
		}
	}

	private PointBasedValueIteration makePBVIInstance(List<List<Double>> beliefPoints) {
		final List<List<Double>> bp = beliefPoints;
		return new PointBasedValueIteration() {{
			setDomain(domain);
			setStates(domain.getAllStates());
			setBeliefPoints(bp);
			setHashFactory(hashFactory);
			setRewardFunction(rewardFunction);
			setGamma(0.95);
			setMaxDelta(0.001);
			setMaxIterations(30);
		}};
	}

	private static List<List<Double>> makeBeliefPoints(int num_states, int granularity) {
		List<List<Double>> result = new ArrayList<List<Double>>();
		int num = multichoose(num_states, granularity);

		for(int bIndex = 0; bIndex < num; ++bIndex) {
			List<Double> temp;
			while(true) {
				temp = new ArrayList<Double>();
				for(int i = 0; i < num_states; ++i) {
					temp.add(0.0);
				}
				for(int sCount = 0; sCount < granularity; ++sCount) {
					int index = (int) (new java.util.Random().nextDouble() * num_states);
					temp.set(index, temp.get(index) + 1/(double)granularity);
				}
				if(!result.contains(temp)) {
					break;
				} else {
					continue;
				}
			}

			listNorm(temp);
			result.add(temp);
		}

		return result;
	}

	public static void listNorm(List<Double> list) {
		double sum = 0.0;
		for(int i = 0; i < list.size(); ++i) {
			sum += list.get(i);
		}
		for(int i = 0; i < list.size(); ++i) {
			list.set(i, list.get(i)/sum);
		}
	}

	public static int factorial(int n) {
		if(n == 0) {
			return 1;
		}
		return n * factorial(n - 1);
	}

	public static int multichoose(int n, int k) {
		return factorial(n + k - 1)/(factorial(k) * factorial(n - 1));
	}
}