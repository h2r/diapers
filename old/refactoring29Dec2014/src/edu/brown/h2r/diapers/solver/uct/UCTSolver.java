package edu.brown.h2r.diapers.solver.uct;

import edu.brown.h2r.diapers.solver.Solver;
import edu.brown.h2r.diapers.solver.pomcp.POMCPSolver;
import edu.brown.h2r.diapers.solver.datastructure.MonteCarloNode;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.pomdp.Observation;

import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.singleagent.Action;
import burlap.oomdp.core.TransitionProbability;
import burlap.oomdp.core.State;
import burlap.oomdp.core.TerminalFunction;
import burlap.behavior.statehashing.NameDependentStateHashFactory;
import burlap.behavior.statehashing.StateHashTuple;
import burlap.behavior.singleagent.auxiliary.StateReachability;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class UCTSolver extends POMCPSolver {

	public UCTSolver() {
		super();
		root = new UCTNode();
		particles = false;
	}

	@Override
	public void run() {

		double numInitStates = domain.getAllInitialStates().size();
		for(POMDPState s : domain.getAllInitialStates()) {
			((UCTNode)root).getBeliefState().put(s, 1/numInitStates);
		}

		for(State s : exploreForStates()) {
			((UCTNode)root).getBeliefState().put((POMDPState)s, 0.0);
		}
		
		while(true) {

			//Run simulations
			int simulations = 0;
			setTimer();
			while(!timeout()) {
				simulations++;
				POMDPState s = ((UCTNode)root).sampleBelief();
				simulate(s, root, 0);
				root.saveValues();
			}			

			//Choose and perform the best action, breaking for success
			GroundedAction a = root.bestRealAction();
			environment.perform(a);
			Observation o = environment.observe(a);
			System.out.println("Agent observed " + o.getName());

			if(isSuccess(o)) break;

			//Advance through the tree
			MonteCarloNode parent = root;
			if(root.advance(a).advance(o) == null) root.advance(a).addChild(o);
			root = (UCTNode) root.advance(a).advance(o);
			
			//Perform an exact belief state update
			double likelihood = 0;
			double normalizer = 1;

			for(POMDPState s_ : ((UCTNode)parent).getBeliefState().keySet()) {
				double sumOfTransitionProbs = 0;

				for(POMDPState s : ((UCTNode)parent).getBeliefState().keySet()) {
					sumOfTransitionProbs += (T(s_, s, a) * ((UCTNode)parent).getBeliefState().get(s));
				}

				likelihood = O(o, s_, a) * sumOfTransitionProbs;
				((UCTNode)root).getBeliefState().put(s_, likelihood);

				normalizer += O(o, s_, a) * sumOfTransitionProbs;
			}

			for(POMDPState s : ((UCTNode)root).getBeliefState().keySet()) {
				double l = ((UCTNode)root).getBeliefState().get(s);
				((UCTNode)root).getBeliefState().put(s, l/normalizer);
			}
		}
	}	

	private double O(Observation o, POMDPState s, GroundedAction a) {
		return o.getProbability(s, a);
	}	

	private double T(POMDPState s_, POMDPState s, GroundedAction a) {
		List<TransitionProbability> tProbs = a.action.getTransitions(s, a.params);
		for(TransitionProbability tp : tProbs) {
			if(tp.s.equals(s_)) return tp.p;
		}
		return 0;
	}

	private class StupidTerminalFunction implements TerminalFunction {
		@Override public boolean isTerminal(State s) {
			return UCTSolver.this.isTerminal((POMDPState)s);
		}
	}

	private List<State> exploreForStates() {
		NameDependentStateHashFactory hf = new NameDependentStateHashFactory();
		Set<State> stateSet = new HashSet<State>();
		for(State s : domain.getAllInitialStates()) {
			stateSet.addAll(StateReachability.getReachableStates(s, domain, hf, new StupidTerminalFunction()));
		}
		Set<StateHashTuple> tempSet = new HashSet<StateHashTuple>();
		for(State s : stateSet) {
			StateHashTuple tempTuple = hf.hashState(s);
			if(!tempSet.contains(tempTuple)) tempSet.add(tempTuple);
		}
		Set<State> noDup = new HashSet<State>();
		for(StateHashTuple shi : tempSet) {
			noDup.add(shi.s);
		}
		return new ArrayList<State>(noDup);
	}
}
