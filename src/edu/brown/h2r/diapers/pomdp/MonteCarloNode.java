package edu.brown.h2r.diapers.pomdp;

import burlap.oomdp.singleagent.GroundedAction;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class MonteCarloNode {
	private Map<HistoryElement, MonteCarloNode> children;
	private List<POMDPState> beliefParticles;

	private int visits;
	private double value;

	private final double C = 0.5;

	public MonteCarloNode() {
		this.visits = 0;
		this.value = 0;
	}
 	
	public MonteCarloNode(int vis, double val) {
		this.visits = vis;
		this.value = val;
	}

	public void visit() {
		visits++;
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

	public GroundedAction bestExploringAction() {
		double maxValue = Double.NEGATIVE_INFINITY;
		GroundedAction bestAction = null;

		for(HistoryElement h : children.keySet()) {
			MonteCarloNode child = children.get(h);
			double test = child.getValue() + this.C * Math.sqrt(Math.log(this.getVisits())/child.getVisits());
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
}
