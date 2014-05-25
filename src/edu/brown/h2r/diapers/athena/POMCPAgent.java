package edu.brown.h2r.diapers.athena;

import edu.brown.h2r.diapers.pomdp.MonteCarloNode;
import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.tiger.TigerDomain;
import edu.brown.h2r.diapers.tiger.namespace.P;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ArrayList;

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
	private double totalReward = 0;

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
		domain = (POMDPDomain) new TigerDomain().generateDomain();
	}

	/**
	 * Updates a reference date at the start of the simulation
	 * phase of the algorithm.  This will be used to make sure
	 * an action is chosen in a reasonable amount of time.
	 */
	public void setTimer() {
		timer = new GregorianCalendar();
	}

	public void giveReward(double r) {
		totalReward += r;
	}

	/**
	 * Returns true when the allotted time for simulation has 
	 * elapsed.
	 */
	public boolean timeout() {
		Calendar now = new GregorianCalendar();
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
			root.addParticle((POMDPState)domain.getExampleState());
		}

		while(true) {
			setTimer();
			while(!timeout()) {
				POMDPState s = root.sampleParticles();
				this.simulate(s, root, 0);
			}

			GroundedAction a = root.bestRealAction();
			environment.perform(a.action, a.params);
			Observation o = environment.observe();

			if(isSuccess(o)) {
				break;
			}

			MonteCarloNode parent = root;
			root = root.advance(a).advance(o);

			while(root.particleCount() < this.NUM_PARTICLES) {
				POMDPState s = parent.sampleParticles();
				POMDPState s_ = (POMDPState) a.action.performAction(s, a.params);
				Observation o_ = s_.getObservation();
				if(this.compareObservations(o_, o)) {
					root.addParticle(s_);
				}
			}
		}

		System.out.println("Agent solved the problem receiving total reward " + totalReward);
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
		//System.out.println("NODE " + node);
		if(Math.pow(this.GAMMA, depth) < this.EPSILON) {
			//System.out.println("[POMCPAgent.simulate()] simulation hit bottom, returning 0");
			return 0;
		}

		if(node.isLeaf()) {
			//System.out.println("[POMCPAgent.simulate()] found leaf node, populating...]");
			for(GroundedAction a : getGroundedActions(state)) {
				//System.out.println("[POMCPAgent.simulate()] adding node for action " + a.action.getName());
				node.addChild(a);
			}

			//System.out.println("[POMCPAgent.simulate()] Beginning rollout...]");
			return this.rollout(state, depth);
		}

		//System.out.println("[POMCPAgent.simulate()] Selecting action to explore!");
		GroundedAction a = node.bestExploringAction();
		//System.out.println(a);
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);

		//System.out.println("[POMCPAgent.simulate()] Chose action " + a.action.getName());
		Observation o = sPrime.getObservation();
		double r = sPrime.getReward();

		//System.out.println("[POMCPAgent.simulate()] Executed agtion, received observation " + o.getName() + " and reward " + r);
		//System.out.println("[POMCPAgent.simulate()] Recurring...");

		//System.out.println("NEXT NODE " + node.advance(a));
		node.advance(a).addChild(o);
		double expectedReward = r + this.GAMMA * this.simulate(sPrime, node.advance(a).advance(o), depth + 1);

		//System.out.println("[POMCPAgent.simulate()] Updating visits and values");
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
		POMDPState sPrime = (POMDPState) a.action.performAction(state, a.params);
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

	private boolean isSuccess(Observation o) {
		String[] arr = new String[1];
		arr[0] = P.OBJ_REFEREE;
		return domain.getPropFunction(P.PROP_DOOR_OPENED).isTrue(environment.getCurrentState(), arr);
	}

	private boolean compareObservations(Observation o1, Observation o2) {
		return o1.getName().compareTo(o2.getName()) == 0;
	}
}