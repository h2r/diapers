package edu.brown.h2r.diapers.solver.datastructure;

import edu.brown.h2r.diapers.util.ANSIColor;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;

import burlap.debugtools.RandomFactory;
import burlap.oomdp.singleagent.GroundedAction;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class MonteCarloNode {
	protected Map<HistoryElement, MonteCarloNode> children = new HashMap<HistoryElement, MonteCarloNode>();
	protected List<POMDPState> beliefParticles = new ArrayList<POMDPState>();
	protected List<Double> valueHistory = new ArrayList<Double>();
	protected Random rand = RandomFactory.getMapped(0);
//	private Random rand = new java.util.Random();

	protected int visits;
	protected double value;

	public MonteCarloNode() {
		this.visits = 0;
		this.value = 0;
	}
 	
	public MonteCarloNode(int vis, double val) {
		this.visits = vis;
		this.value = val;
	}

	public void pruneExcept(GroundedAction a) {
		pruneExcept(new HistoryElement(a));
	}

	public void pruneExcept(Observation o) {
		pruneExcept(new HistoryElement(o));
	}

	public void pruneExcept(HistoryElement h) {
		if(this.isLeaf()) return;

		List<HistoryElement> tbd = new ArrayList<HistoryElement>(); 
		for(HistoryElement elem : children.keySet()) {
			if(!elem.equals(h)) {
				children.get(elem).prune();
				tbd.add(elem);
			}
		}	
		for(HistoryElement elem : tbd) {
			children.remove(elem);
		}
		
	}

	public void prune() {
		if(this.isLeaf()) return;
		for(HistoryElement elem : children.keySet()) {
			children.get(elem).prune();
		}
		children.clear();
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

	public void saveValues() {
		valueHistory.add(value);
		for(HistoryElement he : children.keySet()) {
			children.get(he).saveValues();
		}
	}

	public List<Double> getValueHistory() {
		return valueHistory;
	}

	public synchronized void removeParticle(int index) {
		beliefParticles.remove(index);
	}

	public synchronized void removeRandomParticle() {
//		beliefParticles.remove(rand.nextInt(beliefParticles.size()));
		beliefParticles.remove(rand.nextInt(Integer.MAX_VALUE) % beliefParticles.size());
//		beliefParticles.remove(new java.util.Random(89).nextInt(beliefParticles.size()));
	}

	public POMDPState sampleParticles() {
//		return beliefParticles.get(rand.nextInt(beliefParticles.size()));
		return beliefParticles.get(rand.nextInt(Integer.MAX_VALUE) % beliefParticles.size());
//		return beliefParticles.get(new java.util.Random(89).nextInt(beliefParticles.size()));
	}

	public int particleCount() {
		return beliefParticles.size();
	}

	public GroundedAction bestRealAction() {
		if(this.isLeaf()) System.out.println("Requested action from leaf... :(");

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
			int childVisitCount = child.getVisits();
			double test =Double.MAX_VALUE;
			if(childVisitCount > 0){

			test = child.getValue() + C * Math.sqrt(Math.log(this.getVisits()+1)/childVisitCount);
			}
			

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

	public boolean hasChild(Observation o) {
		return children.containsKey(new HistoryElement(o));
	}

	public synchronized void addChild(Observation o) {
		addChild(new HistoryElement(o), 0, 0);
	}

	public synchronized void addChild(GroundedAction a) {
		addChild(new HistoryElement(a), 0, 0);
	}

	public synchronized void addChild(HistoryElement h) {
		addChild(h, 0, 0);
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
	
	public void setParticles(List<POMDPState> particlesList) {
		this.beliefParticles = particlesList;
	}

	public Map<HistoryElement, MonteCarloNode> getMap() {
		return children;
	}
}
