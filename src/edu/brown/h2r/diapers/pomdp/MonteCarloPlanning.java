package edu.brown.h2r.diapers.pomdp;

public class MonteCarloPlanning {

	private double C = 0.5;
	private double EPSILON = 0.001;
	private double DISCOUNT_FACTOR = 0.8;
	private int TIMEOUT_LENGTH = 10;
	private POMDPDomain domain;
	private Map<List<HistoryElement>, SearchTreeNode> tree;

	public MonteCarloPlanning(POMDPDomain d) {
		this.domain = d;
		this.tree = new HashMap<List<HistoryElement>, SearchTreeNode>();
	}

	public GroundedAction search(List<HistoryElement> history) {
		for(int i = 0; i < this.TIMEOUT_LENGTH; ++i) {
			POMDPState state;
			if(history.isEmpty()) {
				state = this.domain.getNewState();
			} else {
				state = //Get random state from the current belief
			}
			this.simulate(state, history, 0);
		}
		return this.getBestAction(/)
	}

	public double simulate(POMDPState state, List<HistoryElement> history, int depth) {
		if(Math.pow(this.DISCOUNT_FACTOR, depth) < this.EPSILON) {
			return 0.0;
		}

		if(!this.tree.containsKey(history)) {
			for(GroundedAction a : /* All the grounded actions for the current state */) {
				this.tree.put(history.appendCopy(a), new SearchTreeNode(0, 0, new ArrayList<POMDPState>()));
			}
			return this.rollout(state, history, depth);
		}

		GroundedAction a = this.getBestAction(history);
		POMDPState sPrime = domain.performAction(state, a);
		Observation o = sPrime.getObservation();
		double r = domain.reward(state, a, sPrime);
		double R = r + this.DISCOUNT_FACTOR * this.simulate(sPrime, history.appendCopy(a, o), depth + 1);
		this.tree.get(history).addParticle(state);
		this.tree.get(history).incrementNumber();

		SearchTreeNode stn = this.tree.get(/* history with a */);

		stn.incrementNumber();
		stn.augmentValue((R - stn.getValue())/stn.getNumber());
		return R;
	}

	private GroundedAction getBestAction(List<HistoryElement> history) {
		GroundedAction temp = null;
		double maxValue = Double.NEGATIVE_INFINITY;

		for(GroundedAction a : domain.getAllActions()) {
			List<HistoryElement> newHistory = new ArrayList<HistoryElement>(history);
			newHistory.add(new HistoryElement(a));
			int oldNumber = this.tree.get(history).getNumber();
			int newNumber = this.tree.get(newHistory).getNumber();
			double value = this.tree.get(newHistory).getValue() + this.C * Math.sqrt(Math.log(oldNumber)/newNumber);
			if(value > maxValue) {
				maxValue = value;
				temp = a;
			}
		}

		return temp;
	}
}



private class HistoryElement {
	
	private Observation observation;
	private GroundedAction action;
	private boolean isAction = false;
	private boolean isObservation = false;

	public HistoryElement(Observation o) {
		this.observation = o;
		this.isObservation = true;
	}

	public HistoryElement(GroundedAction a) {
		this.action = a;
		this.isAction = true;
	}

	public boolean isAction() {
		return this.isAction;
	}

	public boolean isObservation() {
		return this.isObservation;
	}
}

private class SearchTreeNode {
	
	private int number;
	private double value;
	private Set<POMDPState> particles;

	public SearchTreeNode() {
		this.number = 0;
		this.value = 0.0;
		this.particles = new HashSet<POMDPState>();
	}

	public int getNumber() {
		return this.number;
	}

	public double getValue() {
		return this.value;
	}

	public Set<POMDPState> getParticles() {
		return this.particles;
	}

	public void incrementNumber() {
		this.number++;
	}

	public void augmentValue(double increase) {
		this.value += increase;
	}

	public void addParticle(POMDPState s) {
		this.particles.add(s);

	}
}