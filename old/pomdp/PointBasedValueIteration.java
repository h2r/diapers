package edu.brown.h2r.diapers.pomdp;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
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
//import edu.brown.h2r.diapers.athena.namespace.P;

public class PointBasedValueIteration {

	private POMDPDomain domain;
	private StateHashFactory hash_factory;
	private RewardFunction reward_function;
	private double gamma = -1;
	private double max_delta = -1;
	private int max_iterations = -1;
	private int granularity= -1;

	private List<State> states;
	private List<GroundedAction> actions;
	private List<Observation> observations;
	private List<List<Double>> belief_points;
	private List<Tuple<GroundedAction, double[]>> alphaVectors;
	private String dataPath= null;
	private double[] valueArray;
	private String[] namesArray;
	

	private int num_states;
	private int num_actions;
	private int num_observations;
	private int num_belief_points;

	public PointBasedValueIteration() {}
	public PointBasedValueIteration(POMDPDomain domain, StateHashFactory hashFactory, List<State> states, RewardFunction rf, double gamma, int granularity, double maxDelta, int maxIterations) {
		this.domain = domain;
		this.reward_function = rf;
		this.hash_factory = hashFactory;
		this.gamma = gamma;
		this.max_delta = maxDelta;
		this.max_iterations = maxIterations;
		this.states = states;
		this.granularity = granularity;

		this.num_states = states.size();
		
		//this.num_belief_points = belief_points.size();
	}
	
	public List<List<Double>> getBeliefPoints(){
		if(belief_points.equals(null)){
			System.out.println("The belief points were not set");
		}
		
		return this.belief_points;
	}
	
	public List<Tuple<GroundedAction, double[]>> getAplhaVectors(){
		if(alphaVectors.equals(null)){
			System.out.println("VI not performed alpha vectors empty");
		}
		
		return this.alphaVectors;
	}
	
	public double[] getValueArray(){
		if(valueArray.equals(null)){
			System.out.println("VI not performed value array empty");
		}
		
		return valueArray;
	}
	
	public String[] getNamesArray(){
		if(namesArray.equals(null)){
			System.out.println("VI not performed names array empty");
		}
		
		return this.namesArray;
	}
	
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

	
	

	public boolean doValueIteration(boolean calculateVI) {
		if(!this.allInitialized() && calculateVI ) {
			System.err.println("Cannot do value iteration because the information needed has not been provided: " + this.reportUninitializedValues());
			return false;// null;
		}
		
		if(calculateVI){
			this.belief_points= makeBeliefPoints(this.num_states, this.granularity);
			this.num_belief_points = this.belief_points.size();
			
			System.out.println("here");
			
			

		// System.out.println("NUMBER OF STATES " + num_states);
		// System.out.println("STATES " + states.size());

		// System.out.println("Gamma:" + gamma);

		List<Tuple<GroundedAction, double[]>> returnVectors = new ArrayList<Tuple<GroundedAction, double[]>>();
		for(int k = 0; k < this.num_belief_points; ++k) {
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
										/*if (i==1){
										System.out.println("probability["+stateIndex+"]["+actionIndex+"]["+sPrimeIndex+"]= " + prob);
										System.out.println(observations.get(observationIndex).toString(actions.get(actionIndex)));
										//System.out.println("Observation at "+ observations.get(observationIndex).getName() + " is " +observations.get(observationIndex).getProbability(states.get(sPrimeIndex), actions.get(actionIndex)));
										System.out.println("    " + "Action at index " + actionIndex + " is " + actions.get(actionIndex).action.getName());
										System.out.println("    " + "State at index " + stateIndex + " is\n" + states.get(stateIndex));
										}*/
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
		try{
			CSVWriter beliefPointsWriter = new CSVWriter(new FileWriter("src\\edu\\brown\\h2r\\diapers\\data\\beliefPoints.csv"), ',');
			CSVWriter resultWriter = new CSVWriter(new FileWriter("src\\edu\\brown\\h2r\\diapers\\data\\result.csv"), ',');
			for(int beliefPointCounter = 0;beliefPointCounter < this.belief_points.size();beliefPointCounter++)
			{
				String [] tempString = new String[this.belief_points.get(beliefPointCounter).size()];
				for(int BPinternalCounter = 0; BPinternalCounter < this.belief_points.get(beliefPointCounter).size();BPinternalCounter++)
				{
					tempString[BPinternalCounter]=String.valueOf(this.belief_points.get(beliefPointCounter).get(BPinternalCounter));
			}
				beliefPointsWriter.writeNext(tempString);
			}
			
			for(int resultCounter = 0;resultCounter < returnVectors.size();resultCounter++)
			{
				String [] tempString = new String[returnVectors.get(resultCounter).getY().length+1];
				tempString[0]=returnVectors.get(resultCounter).getX().toString();
				for(int resultInternalCounter = 0; resultInternalCounter < returnVectors.get(resultCounter).getY().length;resultInternalCounter++)
				{
					tempString[resultInternalCounter+1]=String.valueOf(returnVectors.get(resultCounter).getY()[resultInternalCounter]);
			}
				resultWriter.writeNext(tempString);
			}
			resultWriter.close();
			beliefPointsWriter.close();	
		}catch(Exception e){
			String pathError="Check the data path for pbvi ("+this.dataPath+") is valid, system error:";
			System.out.println(pathError + e); 
			 System.exit(0);
		}
		
		this.alphaVectors=returnVectors;
		}
		else{
			try{
				
				System.out.println("[PBVIBehavior.doValueIteration] Loading csv...why??");
				String BeliefPointPath = this.dataPath + "beliefPoints.csv";
				String resultTuplePath =  this.dataPath + "result.csv";
				List<List<Double>> beliefPointsTemp = new ArrayList<List<Double>>();
				
				CSVReader beliefPointReader = new CSVReader(new FileReader(BeliefPointPath));
				CSVReader resultReader = new CSVReader(new FileReader(resultTuplePath));
				
				String [] nextLine;
				while ((nextLine = beliefPointReader.readNext()) != null) {
					List<Double> beliefPoint = new ArrayList<Double>();
					for (int beliefPointCounter=0;beliefPointCounter<nextLine.length;beliefPointCounter++)
					{
						System.out.print(nextLine);
//						beliefPoint.add(1.0);
						beliefPoint.add(Double.parseDouble(nextLine[beliefPointCounter]));
					}
					beliefPointsTemp.add(beliefPoint);
				}
				this.belief_points = beliefPointsTemp;
				
				List<Tuple<GroundedAction, double[]>> resultTemp = new ArrayList<Tuple<GroundedAction, double[]>>();
			    //List<GroundedAction> actionList = domain.getExampleState().getAllGroundedActionsFor(domain.getActions());
				while ((nextLine = resultReader.readNext()) != null) {
					String actionName = nextLine[0];
					GroundedAction individualAction = null;
					for (int actionCount=0;actionCount<this.actions.size();actionCount++)
					{
//						System.out.println(actionName);
//						System.out.println("bored");
//						System.out.println(actionList.get(actionCount).toString());
//						System.out.println("meToo");
						if(this.actions.get(actionCount).toString().equals(actionName))
						{
							individualAction =this.actions.get(actionCount);
							//System.out.println("trial");
//							System.out.println(individualAction.toString());
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
				this.alphaVectors=resultTemp;
				
			}catch(Exception e){
				String pathError="Check the data path for pbvi ("+this.dataPath+") is valid, system error:";
				System.out.println(pathError + e); 
				 System.exit(0);	
			}
		}
		
		this.valueArray = new double[belief_points.size()];
		this.namesArray = new String[belief_points.size()];
		this.resolveNamesAndValues(this.namesArray, this.valueArray, belief_points, alphaVectors);
			

		return true;// returnVectors;
	}

	
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
	
	
	public String findClosestBeliefPointIndex(List<Double> input_belief_point) {
		// this is wrong needs to be fixed
		int maxIndex = -1;
		//double min_dist = Double.POSITIVE_INFINITY;
		//System.out.println("heressssss");
		//System.out.println(this.alphaVectors.size());
		List<Double> sum =new ArrayList<Double>(this.alphaVectors.size());
		for(int i = 0; i < this.alphaVectors.size(); ++i) {
			Double tempSum=0.0;
			for (int j=0; j < this.alphaVectors.get(i).getY().length;++j){
				tempSum+=this.alphaVectors.get(i).getY()[j]*input_belief_point.get(j);
				}
			sum.add(i, tempSum);
			
		}
		Double maxValue=Double.NEGATIVE_INFINITY;
		
		for(int i=0;i<sum.size();++i){
			if(sum.get(i)>maxValue){
				maxValue=sum.get(i);
				maxIndex=i;
			}
		}
			
		//return maxIndex;
		return alphaVectors.get(maxIndex).getX().toString();
	}

	private boolean allInitialized() {
		return this.domain != null && this.reward_function != null
			&& this.gamma != -1 && this.max_delta != -1 && this.max_iterations != -1 && this.states != null
			&& this.granularity != -1 && this.hash_factory != null && this.dataPath !=null;
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
	
	
	
	
	
	public boolean setDataPath(String dataPath) {
		if(this.dataPath == null) {
			if(!dataPath.substring(dataPath.length()-2).equals("\\")){
				dataPath=dataPath+"\\";
				}
			this.dataPath = dataPath;
			return true;
		} else {
			System.err.println("Attempt to set data path of PBVI instance who is already bound to a data path ignored");
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
	
	public boolean setGranularity(int granularity) {
		if(this.granularity == -1) {
			this.granularity = granularity;
			return true;
		} else {
			System.err.println("Attempt to set granularity of PBVI instance who is already bound to a granularity ignored");
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

