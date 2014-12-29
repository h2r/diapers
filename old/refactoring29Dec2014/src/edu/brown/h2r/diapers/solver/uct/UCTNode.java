package edu.brown.h2r.diapers.solver.uct;

import edu.brown.h2r.diapers.solver.datastructure.MonteCarloNode;
import edu.brown.h2r.diapers.solver.datastructure.HistoryElement;
import edu.brown.h2r.diapers.pomdp.POMDPState;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class UCTNode extends MonteCarloNode {
	
	private Map<POMDPState, Double> beliefState = new HashMap<POMDPState, Double>();

	public UCTNode() {
		super();
	}		

	public UCTNode(int vis, double val) {
		super(vis, val);
	}

	public Map<POMDPState, Double> getBeliefState() {
		return beliefState;
	}

	public POMDPState sampleBelief() {
		double boundary = 0;
		double choice = new java.util.Random().nextDouble();
		
		for(POMDPState s : beliefState.keySet()) {
			if(choice >= boundary && choice <= boundary + beliefState.get(s)) {
				return s;
			}
			boundary += beliefState.get(s);
		}

		//If we're here, the belief state is all zero
		List<POMDPState> states = new ArrayList<POMDPState>(beliefState.keySet());
		return states.get(new java.util.Random().nextInt(states.size()));
	}

	@Override
	public synchronized void addChild(HistoryElement h, int vis, double val) {
		children.put(h, new UCTNode(vis, val));
	}
}
