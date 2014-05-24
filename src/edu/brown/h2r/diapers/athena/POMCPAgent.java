package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.MonteCarloNode;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;

/**
 * POMCPAgent is an agent whose behavior is driven by an implementation
 * of David Silver's POMCP algorithm.  It's behavior can be tweaked by 
 * altering the value of some constants: The number of particles used to
 * approximate the belief state, the time allowed for simulation in 
 * choosing an action, the learning constant, the exploration constant,
 * and the simulation distance.
 */
public class POMCPAgent extends Agent {
	
	private POMDPDomain domain;
	private MonteCarloNode root = new MonteCarloNode();

	private Calendar timer;

	private final int NUM_PARTICLES = 50;
	private final long TIME_ALLOWED = 10000;
	private final double GAMMA = 0.95;
	private final double EPSILON = 1E-5;
	private final double C = 0.5;

	/**
	 * Constructor.
	 */
	public POMCPAgent(Environment e) {
		super(e);
		domain = (POMDPDomain) new TigerDomain.generateDomain();
	}

	/**
	 * Updates a reference date at the start of the simulation
	 * phase of the algorithm.  This will be used to make sure
	 * an action is chosen in a reasonable amount of time.
	 */
	public void setTimer() {
		timer = new Calendar();
	}

	/**
	 * Returns true when the allotted time for simulation has 
	 * elapsed.
	 */
	public boolean timeout() {
		Calendar now = new Calendar();
		if(now.getTimeInMillis() >= timer.getTimeInMillis() + this.TIME_ALLOWED) {
			return true;
		}
		return false;
	}
		
	/**
	 * The agent's main interaction cycle. The process involves running
	 * many simulations to approximate the value function, choosing
	 * a best action, executing that action in the environment to receive
	 * an observation, and updating the particle filter state 
	 * representation.
	 */
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

			while(root.particleCount() < this.NUM_PARTICLES) {
				POMDPState s = parent.sampleParticles();
				POMDPState s_ = a.performAction(s);
				Observation o_ = s_.getObservation();
				if(this.compareObserations(o_, s_)) {
					root.addParticle(s_);
				}
			}
		}
	}

	/**
	 * Performs an informed recursive exploration of the state space 
	 * starting with a representative particle in a particular
	 * state.  During each simulation, existing nodes are traversed and
	 * one node is added for each new history explored.
	 * 
	 * @param state 	The state particle to use in this simulation
	 * @param node		The point in the history to begin this simulation
	 * @param depth 	The current distance from the beginning of the simulation
	 *
	 * @return the expected discounted reward of being in the given state
	 * following the given history and executing an optimal policy.
	 */
	public double simulate(POMDPState state, MonteCarloNode node, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON) {
			return 0;
		}

		if(node.isLeaf()) {
			for(GroundedAction a : getGroundedActions(state)) {
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

	/**
	 * Performs a random search of the state space starting at a specific
	 * state and continuing until the search exceeds the allotted
	 * precision.  This is used in simulations which are exploring
	 * more than one step into a new history.
	 */
	private double rollout(POMDPState state, int depth) {
		if(Math.pow(this.GAMMA, depth) < this.EPSILON) {
			return 0;
		}

		GroundedAction a = getGroundedActions(state).get((int)(Math.random() * getGroundedActions(state).size()));
		POMDPState sPrime = a.performAction(state);
		return sPrime.getReward() + this.GAMMA * this.rollout(sPrime, depth + 1);
	}

	/**
	 * Constructs a list of all grounded actions from a given state
	 *
	 * @param state 	The state in which the actions are to be performed.
	 */
	private List<GroundedAction> getGroundedActions(POMDPState state) {
		List<GroundedAction> result = new ArrayList<GroundedAction>();
		for(Action a : domain.getActions()) {
			result.addAll(state.getAllGroundedActionsFor(a));
		}
		return result;
	}
}
