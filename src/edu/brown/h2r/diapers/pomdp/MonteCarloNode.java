class MonteCarloNode {
	private Map<HistoryElement, MonteCarloNode> children;
	private List<POMDPState> beliefParticles;

	private int visits;
	private double value;

	public MonteCarloNode() {
		return new MonteCarloNode(0,0);
	}
 	
	public MonteCarloNode(int vis double val) {
		this.visits = vis;
		this.value = val;
	}

	public void visit() {
		visits++
	}

	public void augmentValue(double inc) {
		value += inc;
	}

	public void addParticle(POMDPState s) {
		beliefParticles.add(s);
	}

	public POMDPState sampleParticles() {
		int index = (int) Math.random() * (beliefParticles.size() - 1);
		return beliefParticles.get(index);
	}

	public int particleCount() {
		return beliefParticles.size();
	}

	public GroundedAction bestRealAction() {
		double maxValue = Double.NEGATIVE_INFINITY;
		GroundedAction bestAction = null;
		
		for(HistoryElement h : children.keySet()) {
			if(children.get(h).getValue() > maxValue) {
				maxValue = children.get(h).getValue();
				bestAction = h.getAction();
			}
		}	

		return bestAction;
	}

	public GroundedAction bextExploringAction() {
		double maxValue = Double.NEGATIVE_INFINITY;
		GroundedAction bestAction = null;

		for(HistoryElement h : children.keySet()) {
			MonteCarloNode child = children.get(h);
			double test = child.getValue() + this.C * Math.sqrt(Math.log(this.getNumber())/child.getNumber());
			if(test > maxValue) {
				maxValue = test;
				bestAction = h.getAction();
			}
		}

		return bestAction;
	}

	public MonteCarloNode advance(GroundedAction a) {
		return advance(new HistoryElement(a));
	}

	public MonteCarloNode advance(Observation o) {
		return advance(new HistoryElement(o));
	}

	public MonteCarloNode advance(HistoryElement h) {
		return children.get(h);
	}

	public void addChild(Observation o) {
		addChild(new HistoryElement(o), 0, 0);
	}

	public void addChild(GroundedAction a) {
		addChild(new HistoryElement(a), 0, 0);
	}

	public void addChild(HistoryElement h) {
		addChild(h, 0, 0);
	}

	public void addChild(Observation o, int vis, double val) {
		addChild(new HistoryElement(o), vis, val);
	}

	public void addChild(GroundedAction a, int vis, double val) {
		addChild(new HistoryElement(a), vis, val);
	}

	public void addChild(HistoryElement h, int vis, double val) {
		List<HistoryElement> newNodeHistory = new ArrayList<HistoryElement>(history);
		newNodeHistory.add(h);
		this.children.put(h, new MonteCarloNode(newNodeHistory, vis, val));
	}
}
