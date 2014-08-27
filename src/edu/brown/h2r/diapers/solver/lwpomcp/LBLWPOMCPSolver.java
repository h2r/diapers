package edu.brown.h2r.diapers.solver.lwpomcp;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import burlap.debugtools.RandomFactory;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.GroundedAction;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.solver.datastructure.HistoryElement;
import edu.brown.h2r.diapers.util.Util;


public class LBLWPOMCPSolver extends Solver{

	protected WeightedMonteCarloNode root = new WeightedMonteCarloNode();


	private int NUM_PARTICLES = 16;
	private double GAMMA = 0.95;
	private double EPSILON = 1E-2;
	private double EXP_BONUS = 5;
	private int NUM_SIMS = NUM_PARTICLES;
	private int HORIZON = 88; // 88 steps with 0.95 as gamma and 0.01 as epsilon
	private int BRANCHING = 8;
	
	
	private Random randomNumber = RandomFactory.getMapped(0);
	
	
	
	
	public LBLWPOMCPSolver(){
		super();
		System.out.println("Using LBLWPOMCPSolver");
	};
	
	@Override
	public void setParams(Map<String, Double> params) {
		Double NP = params.get("NUM_PARTICLES");
		Double TA = params.get("TIME_ALLOWED");
		Double GM = params.get("GAMMA");
		Double EP = params.get("EPSILON");
		Double EB = params.get("EXP_BONUS");
		Double HZ = params.get("HORIZON");
		Double NS = params.get("NUM_SIMS");
		Double BG = params.get("BRANCHING");

		NUM_PARTICLES = (int) (NP == null ? NUM_PARTICLES : NP);
		GAMMA = GM == null ? GAMMA : GM;
		EPSILON = EP == null ? EPSILON : EP;
		EXP_BONUS = EB == null ? EXP_BONUS : EB;
		HORIZON = (int) (HZ == null ? HORIZON : HZ);
		NUM_SIMS = (int) (NS==null? NUM_SIMS : NS);
		NUM_SIMS = (int) (BG==null? BRANCHING : BG);
		
		

		System.out.println("Parameters successfully set to NUM=" + NUM_PARTICLES +  ", GAMMA=" + GAMMA + ", EPSILON=" + EPSILON + ", C=" + EXP_BONUS + ".");
		System.out.println("NUM_SIMS = " + NUM_SIMS);
	}
	
	
	
	@Override
	public void run() {
		
		//creating a set of initial states, this can be changed based on the domain
		Set<POMDPState> initialStateSet = new HashSet<POMDPState>();
		for(int i=0;i<20;i++){
			initialStateSet .add(domain.sampleInitialState());
		}
		
		List<POMDPState> initialStateList = new ArrayList<POMDPState>(initialStateSet);
		System.out.println("initial state size: " + initialStateSet.size());
	
		for(int i = 0; i < this.NUM_PARTICLES; ++i) {
			root.addParticle(initialStateList.get(i%initialStateList.size()),1);
		}
		
		
		while(true){
			int simulations = 0;
			root.normalizeWeights();
			while(simulations < NUM_SIMS) {
				simulations++;
				POMDPState s = root.sampleParticles();
				simulate(s, root, 0, null, null);
//				root.saveValues(); // this is for node explorer and might need a new one for weighted nodes
			}
			
			GroundedAction a = null;
			
			a = root.bestRealAction();
			
			environment.perform(a);
			Observation o = environment.observe(a);
			System.out.println("Agent observed " + o.getName());
			
			if(isSuccess(o) || environment.getSteps() > HORIZON) break;
			
			WeightedMonteCarloNode parent = root;
			WeightedMonteCarloNode newRoot = new WeightedMonteCarloNode();
			
			
			if(root.advance(a).advance(o) == null) {
				root.advance(a).addChild(o);		
			}
			
			root.pruneExcept(a);
			root.advance(a).pruneExcept(o);
			
			root = root.advance(a).advance(o);
			newRoot.setParticles(root.getParticles(),root.getWeights());
			root.prune();
			root = null;
			root = newRoot;
			
			while(root.particleCount() < this.NUM_PARTICLES) {
				
				POMDPState s = parent.sampleParticles();
//				System.out.println("parent particle: "+s.toString());
//				System.out.println("action: "+a.actionName());
				POMDPState s_ = (POMDPState) a.action.performAction(s, a.params);
//				Observation o_ = domain.makeObservationFor(a, s_);
//				System.out.println("obs name: "+o_.getName());
//				System.out.println("out particle: "+s_.toString());
				root.addParticle(s_,o,a);
			}
			
		}
		System.out.println("Agent solved the problem receiving total reward " + environment.getTotalReward() + ", discounted reward: "+ environment.getDiscountedReward() + " in " + environment.getSteps() + " steps.");
	}

	private double simulate(POMDPState state, WeightedMonteCarloNode node, int depth, Observation _o, GroundedAction _ga) {
//		System.out.println("was in simulate, node leaf value: " + node.isLeaf()+" is terminal: "+isTerminal(state));
//		if(isTerminal(state)){
//			System.out.println("State: " + state.toString());
//		}
//		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isTerminal(state)) return 0;
		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isSuccess(_o)) return 0;
		
//		if(!node.isLeaf()) {
//			System.out.println("This should not be printed at all");
//		}

		if(node.isLeaf()) {
			if(getGroundedActions(state).size() == 0) System.out.println("No actions for this state!");
//			System.out.println("This gets printed before each action");
			for(GroundedAction a : getGroundedActions(state)) {
				node.addChild(a);
			}

			double temp =  rollout(state, depth);
			return temp;
		}

		GroundedAction a = node.bestExploringAction(EXP_BONUS);
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);
		Observation o = domain.makeObservationFor(a, sPrime);
		double r = rewardFunction.reward(state, a, sPrime);

		if(!node.advance(a).hasChild(o)) {
			Map<HistoryElement,WeightedMonteCarloNode> children = node.advance(a).getMap();
			if(children.size()<=BRANCHING){
				node.advance(a).addChild(o);	
			}
			else{
				List<Observation> obsList = new ArrayList<Observation>();
				List<Double> obsProbabilityList = new ArrayList<Double>();
				List<Double> obsCDFList = new ArrayList<Double>();
				for(HistoryElement h : children.keySet()){
					Observation obsTemp = h.getObservation();
					obsList.add(obsTemp);
					obsProbabilityList.add(obsTemp.getProbability(sPrime, a));
				}
				Util.listNorm(obsProbabilityList);
				obsCDFList = Util.listCDF(obsProbabilityList);
				double tempRand = randomNumber.nextDouble();
				int ind=0;
				for(int count=0;count<obsCDFList.size();count++){
					if(tempRand < obsCDFList.get(count)){
						ind = count;
						break;
					}
				}
				o = null;
				o = obsList.get(ind);
			}
			
		}
		double expectedReward = r + this.GAMMA * simulate(sPrime, node.advance(a).advance(o), depth + 1, o , a);

		if(depth > 0 ) {
			node.addParticle(state,_o,_ga);
		}
		node.visit();
		node.advance(a).visit();
		node.advance(a).augmentValue((expectedReward - node.advance(a).getValue())/node.advance(a).getVisits());


		return expectedReward;
		
	}
	
	private double rollout(POMDPState state, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isTerminal(state)) {
			return 0;}
		
		GroundedAction a = getGroundedActions(state).get(RandomFactory.getMapped(0).nextInt(getGroundedActions(state).size()));
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);
		

		double temp = rewardFunction.reward(state, a, sPrime) + this.GAMMA * rollout(sPrime, depth + 1);
		return temp;
	}
	
	private List<GroundedAction> getGroundedActions(POMDPState state) {
		List<GroundedAction> result = new ArrayList<GroundedAction>();
		for(Action a : domain.getActions()) {
			result.addAll(a.getAllApplicableGroundedActions(state));
		}
		return result;
	}

}
