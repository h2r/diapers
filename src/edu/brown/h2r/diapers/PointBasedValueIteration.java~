package edu.brown.h2r.diapers.tiger;

public class PointBasedValueIteration {

	private Domain domain;
	private RewardFunction reward_function
	private TerminalFunction terminal_function;
	private double gamma = -1;
	private BeliefStateHashFactory hash_factory;
	private double max_delta = -1;
	private int max_iterations = -1;

	public PointBasedValueIteration() {}
	public PointBasedValueIteration(Domain domain, RewardFunction rf, TerminalFunction tf, double gamma, BeliefStateHashfactory hashFactory, double maxDelta, int maxIterations) {
		this.domain = domain;
		this.reward_function = rf;
		this.terminal_function = tf;
		this.gamma = gamma;
		this.hash_factory = hashFactory;
		this.max_delta = maxDelta;
		this.max_iterations = maxIterations;
	}

	public Tuple<GroundedAction, List<Double>> doValueIteration() {
		if(!this.allInitialized()) {
			System.err.println("Cannot do value iteration because the following information has not been provided: " + this.reportUninitializedValues());
			return null;
		}

		
	}

	private boolean allInitialized() {
		return this.domain != null && this.reward_function != null && this.terminal_function != null
			&& this.gamma != -1 && this.hash_factory != null && this.max_delta != -1 && this.max_iterations != -1;
	}

	private String reportUninitializedValues() {
		String result = "[";

		if(this.domain == null) result += "domain, ";
		if(this.reward_function == null) result += "reward function, ";
		if(this.terminal_function == null) result += "terminal function, ";
		if(this.gamma == -1) result += "gamma, ";
		if(this.hash_factory == null) result += "hash factory, ";
		if(this.max_delta == -1) result += "max delta, ";
		if(this.max_iterations == -1) result += "max iterations, ";

		return result + "]";
	}

/* ============================================================================
 * Single-use setters for instance variables.
 * ========================================================================= */

	public boolean setDomain(Domain domain) {
		if(this.domain == null) {
			this.domain = domain;
			return true;
		} else {
			System.err.println("Attempt to set domain of PBVI instance who is already bound to a domain ignored.");
			return false;
		}
	}

	public boolean setRewardFunction(RewardFunction rf) {
		if(this.reward_function == null) {
			this.reward_function = rf;
			return true;
		} else {
			System.err.println("Attempt to set reward function of PBVI instance who is already bound to a reward function ignored");
			return false;
		}
	}

	public boolean setTerminalFunction(TerminalFunction tf) {
		if(this.terminal_function == null) {
			this.terminal_function = tf;
			return true;
		} else {
			System.err.println("Attempt to set terminal function of PBVI instance who is already bound to a terminal funciton ignored");
			return false;
		}
	}

	public boolean setGamma(double gamma) {
		if(this.gamma == -1) {
			this.gamma = gamma;
			return true;
		} else {
			System.err.println("Attempt to set gamma of PBVI instance who is already bound to a gamma ignored");
			return false;
		}
	}
	
	public boolean setHashFactory(BeliefStateHashFactory hf) {
		if(this.hash_factory == null) {
			this.hash_factory = hf;
			return true;
		} else {
			System.err.println("Attempt to set hash factory of PBVI instance who is already bound to a hash factory ignored");
			return false;
		}
	}

	public boolean setMaxDelta(double md) {
		if(this.maxDelta == -1) {
			this.maxDelta = md;
			return true;
		} else {
			System.err.println("Attempt to set max delta of PBVI instance who is already bound to a max delta ignored");
			return false;
		}
	}

	public boolean setMaxIterations(int mi) {
		if(this.maxIterations == -1) {
			this.maxIterations = mi;
			return true;
		} else {
			System.err.println("Attempt to set max iterations of PBVI instance who is already bound to max iterations ignored");
			return false;
		}
	}
}

