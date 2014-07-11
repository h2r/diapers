package edu.brown.h2r.diapers.solver.pomcp;

import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.testing.Environment;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.singleagent.RewardFunction;

import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

public class POMCPSolver extends Solver {
	private MonteCarloNode root = new MonteCarloNode();

	private Calendar timer;

	private int NUM_PARTICLES = 10;
	private long TIME_ALLOWED = 500;
	private double GAMMA = 0.99;
	private double EPSILON = 1E-2;
	private double EXP_BONUS = 130;

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

		NUM_PARTICLES = (int) (NP == null ? NUM_PARTICLES : NP);
		TIME_ALLOWED = (long) (TA == null ? TIME_ALLOWED : TA);
		GAMMA = GM == null ? GAMMA : GM;
		EPSILON = EP == null ? EPSILON : EP;
		EXP_BONUS = EB == null ? EXP_BONUS : EB;
	}

	private void setTimer() {
		timer = new GregorianCalendar();
	}

	private boolean timeout() {
		return new GregorianCalendar().getTimeInMillis() >= timer.getTimeInMillis() + this.TIME_ALLOWED;
	}

	private long timeElapsed() {
		return new GregorianCalendar().getTimeInMillis() - timer.getTimeInMillis();
	}

	@Override
	public void run() {
		for(int i = 0; i < this.NUM_PARTICLES; ++i) {
			root.addParticle(domain.sampleInitialState());
		}

		while(true) {
			
			int simulations = 0; 
			setTimer();
			while(!timeout()) {
				simulations++;
				POMDPState s = root.sampleParticles();
				simulate(s, root, 0);
			}

			if(userMode) new NodeExplorer(sparse).explore(root); 
			
			GroundedAction a = root.bestRealAction();
			environment.perform(a);
			Observation o = environment.observe(a);
			System.out.println("Agent observed " + o.getName());

			if(isSuccess(o)) break;

			MonteCarloNode parent = root;

			if(root.advance(a).advance(o) == null) root.advance(a).addChild(o);		
			root = root.advance(a).advance(o);

			while(root.particleCount() < this.NUM_PARTICLES) {
				POMDPState s = parent.sampleParticles();
				POMDPState s_ = (POMDPState) a.action.performAction(s, a.params);
				Observation o_ = domain.makeObservationFor(a, s_);
				if(compareObservations(o, o_)) root.addParticle(s_);
			}
			while(root.particleCount() > this.NUM_PARTICLES) {
				root.removeRandomParticle();
			}
		}

		System.out.println("Agent solved the problem receiving total reward " + environment.getTotalReward());
	}
	
	private double simulate(POMDPState state, MonteCarloNode node, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isTerminal(state)) return 0;

		if(node.isLeaf()) {
			for(GroundedAction a : getGroundedActions(state)) {
				node.addChild(a);
			}

			return rollout(state, depth);
		}

		GroundedAction a = node.bestExploringAction(EXP_BONUS);
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);
		Observation o = domain.makeObservationFor(a, sPrime);
		double r = rewardFunction.reward(state, a, sPrime);

		if(!node.advance(a).hasChild(o)) node.advance(a).addChild(o);
		double expectedReward = r + this.GAMMA * simulate(sPrime, node.advance(a).advance(o), depth + 1);

		if(depth > 0) node.addParticle(state);
		node.visit();
		node.advance(a).visit();
		node.advance(a).augmentValue((expectedReward - node.advance(a).getValue())/node.advance(a).getVisits());

		return expectedReward;
	}

	private double rollout(POMDPState state, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON || isTerminal(state)) return 0;

		GroundedAction a = getGroundedActions(state).get(new java.util.Random().nextInt(getGroundedActions(state).size()));
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);

		return rewardFunction.reward(state, a, sPrime) + this.GAMMA * rollout(sPrime, depth + 1);
	}

	private List<GroundedAction> getGroundedActions(POMDPState state) {
		List<GroundedAction> result = new ArrayList<GroundedAction>();
		for(Action a : domain.getActions()) {
			result.addAll(state.getAllGroundedActionsFor(a));
		}
		return result;
	}

	private boolean compareObservations(Observation o1, Observation o2) {
		return o1.getName().equals(o2.getName());
	}
}
