public interface Environment {
	public Observation observe();
	public void perform(Action a, String[] params);
}