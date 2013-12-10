package edu.brown.h2r.diapers.tiger;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.PointBasedValueIteration;
import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.tiger.namespace.P;

import burlap.oomdp.singleagent.RewardFunction;
import burlap.oomdp.singleagent.common.SinglePFTF;

import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.auxiliary.common.UniversalStateParser;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;
import burlap.oomdp.core.ObjectClass;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.Attribute;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.behavior.singleagent.Policy;
import burlap.behavior.singleagent.EpisodeAnalysis;
import burlap.behavior.singleagent.planning.StateConditionTest;
import burlap.behavior.singleagent.planning.OOMDPPlanner;
import burlap.behavior.singleagent.planning.QComputablePlanner;
import burlap.behavior.singleagent.planning.commonpolicies.GreedyQPolicy;
import burlap.behavior.singleagent.planning.deterministic.TFGoalCondition;
import burlap.behavior.statehashing.DiscreteStateHashFactory;

public class TigerBehavior {
	private TigerDomain tigerDomain;
	private POMDPDomain domain;
	private StateParser stateParser;
	private RewardFunction rewardFunction;
	private StateConditionTest goalCondition;
	private State initialState;
	private DiscreteStateHashFactory hashFactory;

	public TigerBehavior() {
		tigerDomain = new TigerDomain();
		domain = (POMDPDomain) tigerDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		initialState = TigerDomain.getNewState(domain);
		hashFactory = this.makeTigerHashFactory();
		rewardFunction = new TigerRewardFunction();
	}

	private DiscreteStateHashFactory makeTigerHashFactory() {
		final Attribute tigerLocation = domain.getAttribute(P.ATTR_TIGER_LOCATION);
		return new DiscreteStateHashFactory(new HashMap<String, List<Attribute>>() 
		{{
			put(P.CLASS_TIGER, new ArrayList<Attribute>() 
			{{
				add(tigerLocation);
			}});
		}});
	}

	public class TigerRewardFunction implements RewardFunction {
		public double reward(State s, GroundedAction a, State sprime) {

			if(a.action.getName().equals(P.ACTION_LISTEN)) return -1;

			ObjectInstance tiger = s.getObject(P.OBJ_TIGER);

			String tigerLoc = tiger.getStringValForAttribute(P.ATTR_TIGER_LOCATION);

			String doorOpened = a.action.getName().equals(P.ACTION_OPEN_RIGHT_DOOR) ? P.DOOR_RIGHT : P.DOOR_LEFT;

			//System.out.println(tigerLoc + ", " + doorOpened);
			if(tigerLoc.equals(doorOpened)) {
			//	System.out.println("Rewarding -100");
				return -100;
			} else {
			//	System.out.println("Rewarding 10");
				return 100;
			}
		}
	}

	public void doValueIteration(String outputPath) {
		outputPath += !outputPath.endsWith("/") ? "/" : "";

		System.out.println("[TigerBehavior.doValueIteration] Setting up belief state points...");

		final List<List<Double>> bp = new ArrayList<List<Double>>();
		for(int i = 0; i < 11; ++i) {
			List<Double> sublist = new ArrayList<Double>();
			sublist.add(new Double((10.0 - (double)i)/10.0));
			sublist.add(new Double((double)i/10.0));
			bp.add(sublist);
		}

		System.out.println("Number of belief points: " + bp.size());

		System.out.println("[TigerBehavior.doValueIteration] Constructing PBVI planner...");

		PointBasedValueIteration pbvi = new PointBasedValueIteration() {{
			setDomain(domain);
			setStates(domain.getAllStates());
			setBeliefPoints(bp);
			setHashFactory(hashFactory);
			setRewardFunction(rewardFunction);
			setGamma(0.8);
			setMaxDelta(0.001);
			setMaxIterations(100);
		}};

		System.out.println("[TigerBehavior.doValueIteration] Running PBVI Planner...");


		List<Tuple<GroundedAction, double[]>> result = null;

		try {
			result = pbvi.doValueIteration();
		} catch (Exception e) {
			System.out.println("Nullpointer");
			System.exit(0);
		}

		System.out.println("[TigerBehavior.doValueIteration] Planner returned...");

		System.out.println(result.size());

		for(int i = 0; i < result.size(); ++i) {
			Tuple<GroundedAction, double[]> tup = result.get(i);
			System.out.println(i + " Action: " + tup.getX().action.getName());
			System.out.println(i + " List: " + tup.getY().length);
			for(int j = 0; j < tup.getY().length; ++j) {
				System.out.println("   " + tup.getY()[j]);
			}
		}

		double[] valueArray = new double[bp.size()];
		String[] nameArray = new String[bp.size()];
		for(int k = 0; k < bp.size(); ++k) {

			List<Double> list = bp.get(k);
			double[] tempArray = new double[result.size()];

			for(int j = 0; j < result.size(); ++j) {

				Tuple<GroundedAction, double[]> tup = result.get(j);
				double tempValue = 0.0;

				for(int i = 0; i < tup.getY().length; ++i) {
					tempValue += list.get(i) * tup.getY()[i];
				}

				tempArray[j] = tempValue;
			}

			double maxValue = Double.NEGATIVE_INFINITY;
			int maxIndex = -1;

			for(int l = 0; l < tempArray.length; ++l) {
				if(tempArray[l] > maxValue) {
					maxValue = tempArray[l];
					maxIndex = l;
				}
			}

			valueArray[k] = maxValue;
			nameArray[k] = result.get(maxIndex).getX().action.getName();
		}

		System.out.println("--------------------");
		for(int i = 0; i < bp.size(); ++i) {
			System.out.println("Belief Point: " + bp.get(i));
			System.out.println("ValueArray[" + i + "]: " + valueArray[i]);
			System.out.println("NameArray[" + i + "]: " + nameArray[i]);
		}

	}

	public static void main(String[] args) {
		TigerBehavior test = new TigerBehavior();
		String outputPath = "output/";

		test.doValueIteration(outputPath);
	}
}
