package edu.brown.h2r.diapers.solver.pomcp;

import burlap.oomdp.core.State;
import burlap.oomdp.singleagent.GroundedAction;
import burlap.oomdp.auxiliary.StateParser;


import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;

import edu.brown.h2r.diapers.pomdp.Observation;

public class SimulationRecord {

	private List<Turn> simulationHistory = new ArrayList<Turn>();
	private StateParser sparse;

	private class Turn {
		public String state; 
		public String action;
		public String observation;
		public String policy;
		public double reward;
		public Turn(String s, String a, String o, String p, double r) {
			state = s;
			action = a;
			observation = o;
			policy = p;
			reward = r;
		}
	}

	public SimulationRecord(StateParser s) {
		sparse = s;
	}		

	public SimulationRecord() {}

	public void recordTurn(String s, String a, String o, String p, double r) {
		simulationHistory.add(new Turn(s, a, o, p, r));
	}

	public void recordTurn(State s, GroundedAction a, Observation o, String p, double r) {
		String sname = "[no state parser]";

		if(sparse != null) {
			sname = sparse.stateToString(s);
		}
		simulationHistory.add(new Turn(sname, nameAction(a), o.getName(), p, r));
	}

	private String nameAction(GroundedAction a) {
		String result = "";

		result += (a.action.getName() + "(");
	
		for(String param : a.params) {
			result += (param + ",");
		}

		result += ")";
		return result;
	}

	public SimulationRecord slice(int start, int end) {
		SimulationRecord result = new SimulationRecord();
		for(int i = start; i < end; ++i) {
			Turn t = simulationHistory.get(i);
			result.recordTurn(t.state, t.action, t.observation, t.policy, t.reward);
		}
		return result;
	}

	@Override
	public String toString() {
		String res = "";

		for(int i = 0; i < simulationHistory.size(); ++i) {
			Turn turn = simulationHistory.get(i);
			res += ("TURN " + i + "\n");
			res += ("POLICY " + turn.policy + "\n");
			res += ("STATE " + turn.state + "\n");
			res += ("ACTION " + turn.action + "\n");
			res += ("OBSERVATION " + turn.observation + "\n");
			res += ("REWARD " + turn.reward + "\n");
			res += ("\n");
		}

		return res;
	}		

	public void toFile(String filepath) {
		try {
			File out = new File(filepath);
			if(!out.exists()) out.createNewFile();

			FileWriter fw = new FileWriter(out.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(this.toString());
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
