package edu.brown.h2r.diapers.pomdp;

import edu.brown.h2r.diapers.util.ANSIColor;

import burlap.oomdp.singleagent.GroundedAction;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MonteCarloNode {
	private Map<HistoryElement, MonteCarloNode> children = new HashMap<HistoryElement, MonteCarloNode>();
	private List<POMDPState> beliefParticles = new ArrayList<POMDPState>();

	private int visits;
	private double value;

	public MonteCarloNode() {
		this.visits = 1;
		this.value = 0;
	}
 	
	public MonteCarloNode(int vis, double val) {
		this.visits = vis;
		this.value = val;
	}

	public synchronized void visit() {
		visits++;
	}

	public synchronized void augmentValue(double inc) {
		value += inc;
	}

	public synchronized void addParticle(POMDPState s) {
		beliefParticles.add(s);
	}

	public synchronized void removeParticle(int index) {
		beliefParticles.remove(index);
	}

	public POMDPState sampleParticles() {
		return beliefParticles.get(new java.util.Random().nextInt(beliefParticles.size()));
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

	public GroundedAction bestExploringAction(double C) {
		double maxValue = Double.NEGATIVE_INFINITY;
		GroundedAction bestAction = null;

		for(HistoryElement h : children.keySet()) {
			MonteCarloNode child = children.get(h);
			double test = child.getValue() + C * Math.sqrt(Math.log(this.getVisits())/child.getVisits());

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
		for(HistoryElement hist : children.keySet()) {
			if(hist.hashCode() == h.hashCode()) {
				return children.get(hist);
			}
		}
		return children.get(h);
	}

	public boolean hasChild(Observation o) {
		return children.containsKey(new HistoryElement(o));
	}

	public synchronized void addChild(Observation o) {
		addChild(new HistoryElement(o), 1, 0);
	}

	public synchronized void addChild(GroundedAction a) {
		addChild(new HistoryElement(a), 1, 0);
	}

	public synchronized void addChild(HistoryElement h) {
		addChild(h, 1, 0);
	}

	public synchronized void addChild(Observation o, int vis, double val) {
		addChild(new HistoryElement(o), vis, val);
	}

	public synchronized void addChild(GroundedAction a, int vis, double val) {
		addChild(new HistoryElement(a), vis, val);
	}

	public synchronized void addChild(HistoryElement h, int vis, double val) {
		this.children.put(h, new MonteCarloNode(vis, val));
	}

	public boolean isLeaf() {
		return this.children.isEmpty();
	}

	public int getVisits() {
		return this.visits;
	}

	public double getValue() {
		return this.value;
	}

	public List<POMDPState> getParticles() {
		return this.beliefParticles;
	}

	public Map<HistoryElement, MonteCarloNode> getMap() {
		return children;
	}
}
