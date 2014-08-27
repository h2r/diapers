package edu.brown.h2r.diapers.solver.lwpomcp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import burlap.debugtools.RandomFactory;
import burlap.oomdp.singleagent.GroundedAction;

import edu.brown.h2r.diapers.pomdp.Observation;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.solver.datastructure.HistoryElement;
import edu.brown.h2r.diapers.util.Util;

public class WeightedMonteCarloNode{
	protected Map<HistoryElement, WeightedMonteCarloNode> children = new HashMap<HistoryElement, WeightedMonteCarloNode>();
	protected List<POMDPState> beliefParticles = new ArrayList<POMDPState>();
	private List<Double> particleWeights = new ArrayList<Double>();
	private List<Double> weightCDF = new ArrayList<Double>();
	private boolean listNormalizedFlag = false;
	private Random randomNumber = RandomFactory.getMapped(0);
	

	protected int visits;
	protected double value;
	 
	public WeightedMonteCarloNode(){
		this.visits = 0;
		this.value = 0;
	}
	
	public WeightedMonteCarloNode(int vis, double val){
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
	
	
	
	//overriding the addParticle method
	public synchronized void addParticle(POMDPState s, Observation o, GroundedAction a){
		beliefParticles.add(s);
		particleWeights.add(o.getProbability(s, a));
		listNormalizedFlag=false;
	}
	
	public synchronized void addParticle(POMDPState s, double probability){
		beliefParticles.add(s);
		particleWeights.add(probability);
		listNormalizedFlag=false;
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
			WeightedMonteCarloNode child = children.get(h);
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
	
	
	public int getVisits() {
		return this.visits;
	}

	public double getValue() {
		return this.value;
	}
	
	public List<POMDPState> getParticles() {
		return this.beliefParticles;
	}
	
	public  Map<HistoryElement, WeightedMonteCarloNode> getMap() {
		return this.children;
	}
	
	
	
	public WeightedMonteCarloNode advance(GroundedAction a) {
		return advance(new HistoryElement(a));
	}

	public WeightedMonteCarloNode advance(Observation o) {
		return advance(new HistoryElement(o));
	}

	public WeightedMonteCarloNode advance(HistoryElement h) {
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
		this.children.put(h, new WeightedMonteCarloNode(vis, val));
	}
	
	
	
	
	public POMDPState sampleParticles(){
		if(!listNormalizedFlag){
			System.err.println("WeightedMonteCarloNode: list not normalized before sampling");
			System.exit(-1);
		}
		double temp = randomNumber.nextDouble();
		for (int count=0;count<weightCDF.size();count++){
			if (temp < weightCDF.get(count)) {
//				System.out.println("random number: " + temp + " index returned " + count);
				return beliefParticles.get(count);
			}
			
		}
		System.err.println("WeightedMonteCarloNode: sampleparticles weights not summing to 1");
		return new POMDPState();
	}
	
	public void normalizeWeights(){
		if(this.particleWeights.size()==0){
			System.err.println("No weights added");
			System.exit(-1);
		}
		if(this.particleWeights.size()!=this.beliefParticles.size()){
			System.err.println("Weights list does not have the same size as the belief particles list");
			System.exit(-1);
		}
		Util.listNorm(this.particleWeights);
		this.weightCDF.addAll(Util.listCDF(particleWeights));
		listNormalizedFlag = true;
	}
	
	
	public boolean isLeaf() {
		return this.children.isEmpty();
	}
	
	public List<Double> getWeights(){
		return this.particleWeights;
	}
	
	public void setParticles(List<POMDPState> particlesList, List<Double> wtLst) {
		this.beliefParticles = particlesList;
		this.particleWeights = wtLst;
	}
	
	/*********************************************************************
	 * 
	 * test structure
	 * 
	 **********************************************************************/
	
	/*
	
	public List<Double> getweightList(){
		return this.particleWeights;
	}
	
	public List<Double> getCDF(){
		return this.weightCDF;
	}
	
	
	
	public static void main(String[] args){
		RandomFactory.seedMapped(0, 0);
		WeightedMonteCarloNode test = new WeightedMonteCarloNode();
		
		POMDPState s1 = new POMDPState();
		POMDPState s2 = new POMDPState();
		POMDPState s3 = new POMDPState();
		test.addParticle(s1, 3);
		test.addParticle(s2, 1);
		test.addParticle(s3, 1);
		test.normalizeWeights();
		List<Double> weightList = test.getweightList();
		List<Double> CDFList = test.getCDF();
		for(int i=0;i<weightList.size();i++){
			System.out.println("weight: "+ weightList.get(i) + " sum: "+ CDFList.get(i));
		}
		System.out.println(test.sampleParticles().toString());
	}
	*/

}
