package edu.brown.h2r.diapers.solver.pomcp;

import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.solver.datastructure.MonteCarloNode;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.testing.Environment;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.RewardFunction;
import burlap.debugtools.RandomFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class POMCPSolver extends Solver {
	protected MonteCarloNode root = new MonteCarloNode();

	private Calendar timer;
	private boolean testWithNumSim = true;
	

	private int NUM_PARTICLES = 128;
	private long TIME_ALLOWED = 500;
	private double GAMMA = 0.95;
	private double EPSILON = 1E-2;
	private double EXP_BONUS = 20;
	private int NUM_SIMS = NUM_PARTICLES;
	private int HORIZON = 88;// 88 steps for .95 to be less than Epsilon = 0.01


	protected boolean particles = true;

	public POMCPSolver() {
		super();
	}

	@Override
	public void setParams(Map<String, Double> params) {
		Double NP = params.get("NUM_PARTICLES");
		Double TA = params.get("TIME_ALLOWED");
		Double GM = params.get("GAMMA");
		Double EP = params.get("EPSILON");
		Double EB = params.get("EXP_BONUS");
		Double HZ = params.get("HORIZON");
		Double NS = params.get("NUM_SIMS");

		NUM_PARTICLES = (int) (NP == null ? NUM_PARTICLES : NP);
		TIME_ALLOWED = (long) (TA == null ? TIME_ALLOWED : TA);
		GAMMA = GM == null ? GAMMA : GM;
		EPSILON = EP == null ? EPSILON : EP;
		EXP_BONUS = EB == null ? EXP_BONUS : EB;
		HORIZON = (int) (HZ == null ? HORIZON : HZ);
		NUM_SIMS = (int) (NS==null? NUM_SIMS : NS);
		if(TA!=null && NS==null){
			testWithNumSim = false;
		}
		

		System.out.println("Parameters successfully set to NUM=" + NUM_PARTICLES + ", TIME=" + TIME_ALLOWED + ", GAMMA=" + GAMMA + ", EPSILON=" + EPSILON + ", C=" + EXP_BONUS + ".");
		System.out.println("NUM_SIMS = " + NUM_SIMS + " testWithNumSims = " + testWithNumSim);
	}

	protected void setTimer() {
		timer = new GregorianCalendar();
	}

	protected boolean timeout() {
		return new GregorianCalendar().getTimeInMillis() >= timer.getTimeInMillis() + this.TIME_ALLOWED;
	}

	private long timeElapsed() {
		return new GregorianCalendar().getTimeInMillis() - timer.getTimeInMillis();
	}

	@Override
	public void run() {
//		System.out.println("in run");
		for(int i = 0; i < this.NUM_PARTICLES; ++i) {
			root.addParticle(domain.sampleInitialState());
		}
		boolean obsExists = true;

		while(true) {
			
			int simulations = 0; 
			setTimer();
//			while(!timeout()) {
			while(((simulations < NUM_SIMS) || !testWithNumSim ) && (!timeout() || testWithNumSim) && obsExists) {
				simulations++;
				POMDPState s = root.sampleParticles();
				simulate(s, root, 0);
				root.saveValues();
			}
			
			GroundedAction a = null;

			if(userMode) new NodeExplorer(sparse).explore(root); 
			if(obsExists){
				System.out.println("POMCPSolver: regular actions");
				a = root.bestRealAction();
			}
			else{
//				java.util.Random randomTemp = new java.util.Random();
				System.out.println("POMCPSolver: random actions");
				List<GroundedAction> gaList = getGroundedActions((POMDPState)environment.getCurrentState()); 
//				a = gaList.get(randomTemp.nextInt(gaList.size()));
				a = gaList.get(RandomFactory.getMapped(0).nextInt(gaList.size()));
			}
			environment.perform(a);
			Observation o = environment.observe(a);
			System.out.println("Agent observed " + o.getName());

			if(isSuccess(o) || environment.getSteps() > HORIZON) break;

			MonteCarloNode parent = root;
			MonteCarloNode newRoot = new MonteCarloNode();
			
			if(obsExists){

			if(root.advance(a).advance(o) == null) {
				obsExists = false;
				root.advance(a).addChild(o);		
			}
			root.pruneExcept(a);
			root.advance(a).pruneExcept(o);
			root = root.advance(a).advance(o);
//			System.out.println("RootValue before:" + root.getVisits());
			newRoot.setParticles(root.getParticles());
			root.prune();
			root = null;
			
			root = newRoot;
//			System.out.println("RootValue:" + root.getVisits());
			
			long timeStart = System.currentTimeMillis();

			while(obsExists && root.particleCount() < this.NUM_PARTICLES) {
				
				POMDPState s = parent.sampleParticles();
				POMDPState s_ = (POMDPState) a.action.performAction(s, a.params);
				Observation o_ = domain.makeObservationFor(a, s_);
				if(compareObservations(o, o_)) root.addParticle(s_);
			}
			while(root.particleCount() > this.NUM_PARTICLES) {
				root.removeRandomParticle();
			}
			}
		}

		System.out.println("Agent solved the problem receiving total reward " + environment.getTotalReward() + ", discounted reward: "+ environment.getDiscountedReward() + " in " + environment.getSteps() + " steps.");
//		System.out.println("out of run");
	}
	
	protected double simulate(POMDPState state, MonteCarloNode node, int depth) {
//		System.out.println("POMCPSolver: in simulate");
		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isTerminal(state)) return 0;

		if(node.isLeaf()) {
			if(getGroundedActions(state).size() == 0) System.out.println("No actions for this state!");
			for(GroundedAction a : getGroundedActions(state)) {
				node.addChild(a);
			}

			double temp =  rollout(state, depth);
//			System.out.println("POMCPSolver: out of simulate");
			return temp;
		}

		GroundedAction a = node.bestExploringAction(EXP_BONUS);
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);
		Observation o = domain.makeObservationFor(a, sPrime);
		double r = rewardFunction.reward(state, a, sPrime);

		if(!node.advance(a).hasChild(o)) node.advance(a).addChild(o);
		double expectedReward = r + this.GAMMA * simulate(sPrime, node.advance(a).advance(o), depth + 1);

		if(depth > 0 && particles) node.addParticle(state);
		node.visit();
		node.advance(a).visit();
		node.advance(a).augmentValue((expectedReward - node.advance(a).getValue())/node.advance(a).getVisits());
//		System.out.println("POMCPSolver: out of simulate");

		return expectedReward;
		
	}

	private double rollout(POMDPState state, int depth) {
//		System.out.println("POMCPSolver: in rollout");
		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isTerminal(state)) {
//			System.out.println("POMCPSolver: in simulate");
			return 0;}
		
//		GroundedAction a = getGroundedActions(state).get(new java.util.Random().nextInt(getGroundedActions(state).size()));
		GroundedAction a = getGroundedActions(state).get(RandomFactory.getMapped(0).nextInt(getGroundedActions(state).size()));
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);
		

		double temp = rewardFunction.reward(state, a, sPrime) + this.GAMMA * rollout(sPrime, depth + 1);
//		System.out.println("POMCPSolver: out of rollout");
		return temp;
	}

	private List<GroundedAction> getGroundedActions(POMDPState state) {
//		System.out.println("POMCPSolver: in get grounded actions");
		List<GroundedAction> result = new ArrayList<GroundedAction>();
		for(Action a : domain.getActions()) {
//			result.addAll(state.getAllGroundedActionsFor(a));
			result.addAll(a.getAllApplicableGroundedActions(state));
		}
//		System.out.println("POMCPSolver: out of get grounded actions");
		return result;
	}

	private boolean compareObservations(Observation o1, Observation o2) {
		return o1.getName().equals(o2.getName());
	}
}
