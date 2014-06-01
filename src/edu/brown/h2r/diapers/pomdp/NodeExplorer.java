package edu.brown.h2r.diapers.pomdp;

import edu.brown.h2r.diapers.util.ANSIColor;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.NumberFormatException;

public class NodeExplorer {
	
	private MonteCarloNode current;
	private List<MonteCarloNode> children = new ArrayList<MonteCarloNode>();
	private List<String> names = new ArrayList<String>();
	private List<Double> values = new ArrayList<Double>();
	private List<Integer> visits = new ArrayList<Integer>();
	private List<MonteCarloNode> history = new ArrayList<MonteCarloNode>();
	private boolean stop = false;

	public void explore(MonteCarloNode node) {
		use(node);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while(!stop) {
			ANSIColor.red("\n[Node Explorer] $ ");
			String input = "";
			try {	
				input = reader.readLine();
			} catch (IOException e) {
				System.err.println("Error interpreting input");
			}
			interpret(input);
		}
	}

	private void interpret(String input) {
		try {

			int index = Integer.parseInt(input);

			if(index >= children.size()) {
				System.err.println("Invalid index, try again...");
				return;
			}


			System.out.print("Moving to child ");
			ANSIColor.red(names.get(index) + "\n");

			history.add(current);
			use(children.get(index));
			return;

		} catch(NumberFormatException e) {
		}

		switch(input) {
			case "stop":
				stop = true;
				break;
			case "children":
				displayChildren();
				break;
			case "back":
				if(!history.isEmpty()) {
					use(history.remove(history.size() - 1));
					System.out.println("Moving back one step...");
				} else {
					System.err.println("Nowhere to go 0_o");
				}
				break;
			default:
				System.out.println("Unknown command, please try again...");
				break;
		}
		
		return;
	}

	private void use(MonteCarloNode node) {
		current = node;
		Map<HistoryElement, MonteCarloNode> kids = node.getMap();
		children = new ArrayList<MonteCarloNode>();
		names = new ArrayList<String>();
		for(HistoryElement h : kids.keySet()) {
			children.add(kids.get(h));
			names.add(h.getName());
			values.add(kids.get(h).getValue());
			visits.add(kids.get(h).getVisits());
		}
	}

	private void displayChildren() {
		for(int i = 0; i < children.size(); ++i) {
			double val = Math.round(values.get(i) * 1000)/1000;
			System.out.println("(" + i + ") " + names.get(i) + " [" + val + "] [" + visits.get(i) + "]");
		}
	}
}
