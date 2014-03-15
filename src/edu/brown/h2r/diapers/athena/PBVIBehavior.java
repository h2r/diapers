package edu.brown.h2r.diapers.athena;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;


import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.PointBasedValueIteration;
import edu.brown.h2r.diapers.util.Tuple;
import edu.brown.h2r.diapers.athena.namespace.P;

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
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.DiscreteStateHashFactory;

public class PBVIBehavior {
	private POMDPDiaperDomain diaperDomain;
	private static POMDPDomain domain;
	private StateParser stateParser;
	private static RewardFunction rewardFunction;
	private StateConditionTest goalCondition;
	private State initialState;
	private static NameDependentStateHashFactory hashFactory;

	//private static List<List<Double>> beliefPoints;
	//private static List<Tuple<GroundedAction, double[]>> result;
	//private static double[] valueArray;
	//private static String[] namesArray;


	public PBVIBehavior() {
		diaperDomain = new POMDPDiaperDomain();
		domain = (POMDPDomain) diaperDomain.generateDomain();
		stateParser = new UniversalStateParser(domain);
		initialState = diaperDomain.getNewState(domain);
		hashFactory = new NameDependentStateHashFactory();
		rewardFunction = new AthenaRewardFunction();
	}

	private DiscreteStateHashFactory makeDiaperDomainHashFactory() {
		final Attribute mentalState = domain.getAttribute(P.ATTR_MENTAL_STATE);
		return new DiscreteStateHashFactory(new HashMap<String, List<Attribute>>() 
		{{
			put(P.CLASS_STATE_HOLDER, new ArrayList<Attribute>()
			{{
				add(mentalState);
			}});
		}});
	}

	public class AthenaRewardFunction implements RewardFunction {
		public double reward(State s, GroundedAction a, State sprime) {
			if(a.toString().equals("act.speak")){
				return -0.5;
			}
			return -1;
		}
	}
/*
	public void doValueIteration(int doVI) throws IOException {
		//outputPath += !outputPath.endsWith("/") ? "/" : "";
		if(doVI==1)
		{	
			
		beliefPoints = makeBeliefPoints(domain.getAllStates().size(), 4);
		PointBasedValueIteration pbvi = makePBVIInstance(beliefPoints);

		System.out.println("[PBVIBehavior.doValueIteration] Running PBVI...");

		result = pbvi.doValueIteration();
		// need to write out this result
		
		CSVWriter beliefPointsWriter = new CSVWriter(new FileWriter("src\\edu\\brown\\h2r\\diapers\\data\\beliefPoints.csv"), ',');
		CSVWriter resultWriter = new CSVWriter(new FileWriter("src\\edu\\brown\\h2r\\diapers\\data\\result.csv"), ',');
		for(int beliefPointCounter = 0;beliefPointCounter < beliefPoints.size();beliefPointCounter++)
		{
			String [] tempString = new String[beliefPoints.get(beliefPointCounter).size()];
			for(int BPinternalCounter = 0; BPinternalCounter < beliefPoints.get(beliefPointCounter).size();BPinternalCounter++)
			{
				tempString[BPinternalCounter]=String.valueOf(beliefPoints.get(beliefPointCounter).get(BPinternalCounter));
		}
			beliefPointsWriter.writeNext(tempString);
		}
		
		for(int resultCounter = 0;resultCounter < result.size();resultCounter++)
		{
			String [] tempString = new String[result.get(resultCounter).getY().length+1];
			tempString[0]=result.get(resultCounter).getX().toString();
			for(int resultInternalCounter = 0; resultInternalCounter < result.get(resultCounter).getY().length;resultInternalCounter++)
			{
				tempString[resultInternalCounter+1]=String.valueOf(result.get(resultCounter).getY()[resultInternalCounter]);
		}
			resultWriter.writeNext(tempString);
		}
		resultWriter.close();
		beliefPointsWriter.close();
		}
		else
		{
			File currentDir = new File("");
			System.out.println(currentDir.getAbsolutePath());
			System.out.println("[PBVIBehavior.doValueIteration] Loading csv...");
			String BeliefPointPath = "src\\edu\\brown\\h2r\\diapers\\data\\beliefPoints.csv";
			String resultTuplePath =  "src\\edu\\brown\\h2r\\diapers\\data\\result.csv";
			
			List<List<Double>> beliefPointsTemp = new ArrayList<List<Double>>();
			
			CSVReader beliefPointReader = new CSVReader(new FileReader(BeliefPointPath));
			CSVReader resultReader = new CSVReader(new FileReader(resultTuplePath));
			
			String [] nextLine;
			while ((nextLine = beliefPointReader.readNext()) != null) {
				List<Double> beliefPoint = new ArrayList<Double>();
				for (int beliefPointCounter=0;beliefPointCounter<nextLine.length;beliefPointCounter++)
				{
					System.out.print(nextLine);
//					beliefPoint.add(1.0);
					beliefPoint.add(Double.parseDouble(nextLine[beliefPointCounter]));
				}
				beliefPointsTemp.add(beliefPoint);
			}
			beliefPoints = beliefPointsTemp;
//			String [] nextLine;
			List<Tuple<GroundedAction, double[]>> resultTemp = new ArrayList<Tuple<GroundedAction, double[]>>();
		    List<GroundedAction> actionList = domain.getExampleState().getAllGroundedActionsFor(domain.getActions());
			while ((nextLine = resultReader.readNext()) != null) {
				String actionName = nextLine[0];
				GroundedAction individualAction = null;
				for (int actionCount=0;actionCount<actionList.size();actionCount++)
				{
//					System.out.println(actionName);
//					System.out.println("bored");
//					System.out.println(actionList.get(actionCount).toString());
//					System.out.println("meToo");
					if(actionList.get(actionCount).toString().equals(actionName))
					{
						individualAction =actionList.get(actionCount);
						//System.out.println("trial");
//						System.out.println(individualAction.toString());
						break;
					}
				}
				double[] actionVector = new double[nextLine.length-1];
				for (int resultCounter=0;resultCounter<nextLine.length-1;resultCounter++)
				{
					actionVector[resultCounter]=Double.parseDouble(nextLine[resultCounter+1]);
				}
				resultTemp.add(new Tuple<GroundedAction, double[]>(individualAction,actionVector));
				
			}
			result=resultTemp;
			
			
		}

		//valueArray = new double[beliefPoints.size()];
		//namesArray = new String[beliefPoints.size()];

		//this.resolveNamesAndValues(namesArray, valueArray, beliefPoints, result);

		System.out.println("[PBVIBehavior.doValueIteration] Finished planning!");
		System.out.println("--------------------------------------------------");
		// for(int i = 0; i < beliefPoints.size(); ++i) {
		// 	System.out.println("Belief Point: " + beliefPoints.get(i));
		// 	System.out.println("ValueArray[" + i + "] = " + valueArray[i]);
		// 	System.out.println("NameArray[" + i + "] = " + namesArray[i]);
		// }
	}*/
/*
	public void resolveNamesAndValues(String[] names, double[] vals, List<List<Double>> belief_points, List<Tuple<GroundedAction, double[]>> pbvi_result) {
		for(int i = 0; i < belief_points.size(); ++i) {
			List<Double> list = belief_points.get(i);
			double[] tempArray = new double[pbvi_result.size()];

			for(int j = 0; j < pbvi_result.size(); ++j) {

				Tuple<GroundedAction, double[]> tuple = pbvi_result.get(j);
				double tempValue = 0.0;

				for(int k = 0; k < tuple.getY().length; ++k) {
					tempValue += list.get(k) * tuple.getY()[k];
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

			vals[i] = maxValue;
			names[i] =pbvi_result.get(maxIndex).getX().action.getName();
//			System.out.println(pbvi_result.get(i).getY()[6]);
			
//					pbvi_result.get(maxIndex).getX().action.getName();
		}
	}
	*/
/*
	public static List<List<Double>> makeBeliefPoints(int num_states, int granularity) {
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
*/
	public static PointBasedValueIteration makePBVIInstance(int granularity) {
		//final List<List<Double>> bp = belief_points;
		final int myGranularity=granularity;
		return new PointBasedValueIteration() {{
			setDomain(domain);
			setStates(domain.getAllStates());
			setGranularity(myGranularity);
			setHashFactory(hashFactory);
			setRewardFunction(rewardFunction);
			setGamma(0.95);
			setMaxDelta(0.001);
			setMaxIterations(30);
		}};
	}

/*	public static int findClosestBeliefPointIndex(List<Double> input_belief_point) {
		// this is wrong needs to be fixed
		int min_index = -1;
		double min_dist = Double.POSITIVE_INFINITY;
		for(int i = 0; i < beliefPoints.size(); ++i) {
			double dist = distance(beliefPoints.get(i), input_belief_point);
			if(dist < min_dist) {
				min_dist = dist;
				min_index = i;
			}
		}
		return min_index;

	}
*/
/*	public static double distance(List<Double> l1, List<Double> l2) {
		if(!(l1.size() == l2.size())) return Double.POSITIVE_INFINITY;
		double sqsum = 0.0;
		for(int i = 0; i < l1.size(); ++i) {
			sqsum += Math.pow((l1.get(i) - l2.get(i)), 2);
		}
		return Math.sqrt(sqsum);
	}
*/
	public static void main(String[] args) throws IOException {
		PBVIBehavior test = new PBVIBehavior();
		String dataPath = "src\\edu\\brown\\h2r\\diapers\\data\\";
		//test.setBeliefPoints(false,outputPath);
		//test.
		
		File currentDir = new File("");
		System.out.println(currentDir.getAbsolutePath());
		int granularity=4;
		boolean doCompleteVI=false;
		PointBasedValueIteration pbvi = makePBVIInstance(granularity);
		pbvi.setDataPath(dataPath);
		
		pbvi.doValueIteration(doCompleteVI);

		java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));

		while(true) {
			List<Double> input_bp = new ArrayList<Double>();
			try {
				System.out.println("Belief for state...");

				System.out.print("X: ");
				input_bp.add(Double.parseDouble(br.readLine()));

				System.out.print("A: ");
				input_bp.add(Double.parseDouble(br.readLine()));

				System.out.print("B: ");
				input_bp.add(Double.parseDouble(br.readLine()));

				System.out.print("C: ");
				input_bp.add(Double.parseDouble(br.readLine()));

				System.out.print("D: ");
				input_bp.add(Double.parseDouble(br.readLine()));

				System.out.print("E: ");
				input_bp.add(Double.parseDouble(br.readLine()));

				System.out.print("Y: ");
				input_bp.add(Double.parseDouble(br.readLine()));
			} catch (java.io.IOException e) {
			}

			//int i = findClosestBeliefPointIndex(input_bp);
			
			String j =pbvi.findClosestBeliefPointIndex(input_bp); 
			//System.out.println("Best POMDP Action: " + namesArray[j]);
			System.out.println("Best POMDP Action: " + j);
		}

		// List<List<Double>> bp_test = makeBeliefPoints(7, 2);
		// for(List<Double> list : bp_test) {
		// 	for(Double d : list) {
		// 		System.out.print(d + " ");
		// 	}
		// 	System.out.println();
		// }
	}
}