package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.MonteCarloNode;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;

public class POMCPAgent extends Agent {
	
	private POMDPDomain domain;
	private MonteCarloNode root = new MonteCarloNode();

	private Date init;

	private final int NUM_PARTICLES = 50;
	private final long TIME_ALLOWED = 10000;

	public POMCPAgent(Environment e) {
		super(e);
		domain = (POMDPDomain) new TigerDomain.generateDomain();
	}

	public void setTimer() {
		init = new Date();
	}

	public boolean timeout() {
		Date d = new Date();
		//TODO: compare dates, return true or false
	}
		
	public void run() {
		for(int i = 0; i < this.NUM_PARTICLES; ++i) {
			root.addParticle((POMDPState)domain.getNewState());
		}

		while(true) {
			setTimer();
			while(!timeout()) {
				POMDPState s = root.sampleParticles();
				this.simulate(s, root, 0);
			}

			GroundedAction a = root.bestRealAction();
			Observation o = environment.perform(a);
			if(isSuccess(o)) {
				break;
			}

			MonteCarloNode parent = root;
			root = root.advance(a).advance(o);

			while(root.particleCount() < K) {
				POMDPState s = parent.sampleParticles();
				POMDPState s_ = a.performAction(s);
				Observation o_ = s_.getObservation();
				if(this.compareObserations(o_, s_)) {
					root.addParticle(s_);
				}
			}
		}
	}

	public double simulate(POMDPState state, MonteCarloNode node, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON) {
			return 0;
		}

		if(node.isLeaf()) {
			for(GroundedAction a : domain.getAllGroundedActionsFor(state)) {
				node.addChild(a);
			}
			return this.rollout(state, depth);
		}

		GroundedAction a = node.bestExploringAction();
		POMDPState sPrime = (POMDPState) a.performAction(state);

		Observation o = sPrime.getObservation();
		double r = sPrime.getReward();

		node.advance(a).addChild(o);
		double expectedReward = r + this.GAMMA * this.simulate(sPrime, node.advance(a).advance(o), depth + 1);

		node.addParticle(state);
		node.visit();
		node.advance(a).visit();
		node.advance(a).augmentValue((expectedReward - node.advance(a).getValue())/node.advance(a).getVisits());

		return expectedReward;
	}

	private double rollout(POMDPState state, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON) {
			return 0;
		}

		GroundedAction a = domain.getAllGroundedActionsFor(state).get((int)(Math.random() * domain.getAllGroundedActionsFor(state).size()));
		POMDPState sPrime = a.performAction(state);
		return sPrime.getReward() + this.GAMMA * this.rollout(sPrime, depth + 1);
	}
}
