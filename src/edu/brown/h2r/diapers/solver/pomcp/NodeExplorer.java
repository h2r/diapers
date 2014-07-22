package edu.brown.h2r.diapers.solver.pomcp;

import edu.brown.h2r.diapers.util.ANSIColor;
import edu.brown.h2r.diapers.pomdp.POMDPState;
import edu.brown.h2r.diapers.domain.tiger.Names;

import burlap.oomdp.core.State;
import burlap.oomdp.auxiliary.StateParser;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.NumberFormatException;

public class NodeExplorer {
	
	
	private MonteCarloNode current;
	private List<MonteCarloNode> children = new ArrayList<MonteCarloNode>();
	private List<String> names = new ArrayList<String>();
	private List<Double> values = new ArrayList<Double>();
	private List<Integer> visits = new ArrayList<Integer>();
	private List<List<Double>> valueHistories = new ArrayList<List<Double>>();
	private List<MonteCarloNode> history = new ArrayList<MonteCarloNode>();
	private List<String> path = new ArrayList<String>();
	private boolean stop = false;
	private StateParser sparse;

	public NodeExplorer(StateParser s) {
		sparse = s;
	}

	public void explore(MonteCarloNode node) {
		use(node);
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		while(!stop) {
			ANSIColor.red("\n[Node Explorer] ");
			printPath();
			String input = "";
			try {	
				input = reader.readLine();
			} catch (IOException e) {
				System.err.println("Error interpreting input");
			}
			interpret(input);
		}
	}

	private void printPath() {
		int i = 0;
		for(String s : path) {
			i++;
			String arrow = s.endsWith(" ") ? "> " : " > ";
			String maybespace = s.endsWith(" ") ? "" : " ";
			if(i < path.size()) {
				System.out.print(s + arrow);
			} else {
				System.out.print(s + maybespace);
			}
		}
		System.out.print("$ ");
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
			path.add(names.get(index));

			history.add(current);
			use(children.get(index));
			return;

		} catch(NumberFormatException e) {
		}

		if(input.startsWith("histories")) {
			String[] pieces = input.split(" ");

			if(pieces[1].equals("visualize")) {
				if(pieces.length > 2) {
					visualize(pieces[2]);
				} else {
					visualize("histories_output.html");
				}
				return;
			}

			int start = Integer.parseInt(pieces[1]);
			int end = Integer.parseInt(pieces[2]);

			displayValueHistories(start, end);
			return;
		}

		switch(input) {
			case "stop":
				stop = true;
				break;
			case "children":
				displayChildren();
				break;
			case "belief":
				displayBelief();
				break;
			case "back":
				if(!history.isEmpty()) {
					use(history.remove(history.size() - 1));
					path.remove(path.size() - 1);
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
		children.clear();
		names.clear();
		values.clear();
		visits.clear();
		valueHistories.clear();
		for(HistoryElement h : kids.keySet()) {
			children.add(kids.get(h));
			names.add(h.getName());
			values.add(kids.get(h).getValue());
			visits.add(kids.get(h).getVisits());
			valueHistories.add(kids.get(h).getValueHistory());
		}
	}

	private void displayBelief() {
		List<POMDPState> particles = current.getParticles();
		Map<String, Integer> states = new HashMap<String, Integer>();

		for(POMDPState s : particles) {
			String stateName = nameState(s);

			if(states.containsKey(stateName)) {
				states.put(stateName, states.get(stateName) + 1);
			} else {
				states.put(stateName, 1);
			}
		}

		for(String stateName : states.keySet()) {
			double thisstate = (double) states.get(stateName);
			double total = (double) particles.size();

			double percent = thisstate/total;
			System.out.println(stateName + " " + states.get(stateName) + " (" + threePlaces(percent) + ")");
		}
	}

	private void displayChildren() {
		for(int i = 0; i < children.size(); ++i) {
			double val = threePlaces(values.get(i));
			System.out.println("(" + i + ") " + names.get(i) + " [" + val + "] [" + visits.get(i) + "]");
		}
	}

	private void visualize(String filename) {
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filename)))) {

			writer.write("<html><head>\n");
			writer.write("<script src='http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js'></script>\n");
			writer.write("<script src='http://code.highcharts.com/highcharts.js'></script>\n");
			writer.write("</head><body>\n");
			writer.write("<div id='chart' style='width:100%; height:100%;'></div>\n");
			writer.write("<script>");
			writer.write("$(function() { $('#chart').highcharts({ chart: { type: 'line', zoomType: 'x' }, yAxis: { type: 'linear' }, series: [");

			for(int i = 0; i < children.size(); ++i) {
				writer.write("{");
				writer.write("name: '" + names.get(i) + "',");
				writer.write("data: [");
				for(Double d : valueHistories.get(i)) {
					writer.write(d + ",");
				}
				writer.write("]},\n");
			}
				
			writer.write("]});});\n");
			writer.write("</script></body></html>\n");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void displayValueHistories(int start, int end) {
		for(int i = 0; i < children.size(); ++i) {
			System.out.print(names.get(i) + " [");
			for(int j = start; j < end; ++j) {
				double d = valueHistories.get(i).get(j);
				System.out.print(threePlaces(d) + ",");
			}
			System.out.println("]");
		}
	}

	private String nameState(State s) {
		return sparse.stateToString(s);
	}

	private double threePlaces(double d) {
		return (double) Math.round(d * 1000)/1000;
	}
}
