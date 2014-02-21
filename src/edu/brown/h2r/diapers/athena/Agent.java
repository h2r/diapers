/**
 * Agent is an abstract class which can be run on an environment
 */
public abstract class Agent {
	protected Environment environment;

	public Agent(Environment e) {
		environment = e;
	}

	public abstract void run();
}