package edu.brown.h2r.diapers.pomdp;

import burlap.oomdp.singleagent.GroundedAction;

import java.util.Set;
import java.util.HashSet;

/**
 * ObservationModel is an interface representing functions involving the sets and
 * distributions of Observations possible in a domain.
 */ 
public abstract class ObservationModel {

	protected POMDPDomain domain;

	public ObservationModel(POMDPDomain d) {
		domain = d;
	}

	/**
	 * Returns a new ObservationModel based on this ObservationModel which splits 
	 * each observation into n different observations who are named after the original
	 * ones with a number appended to the end.  
	 * @param n 	The number of observations to divide each observation into
	 * @return  	A new ObservationModel subclassed with the proper divisions
	 */
	public ObservationModel withMultiplicity(int n) {

		if(n < 2) return this;

		final double reductionFactor = 1/((double)n);
		final int number = n;

		return new ObservationModel(domain) {
			private ObservationModel oldObsModel = ObservationModel.this;

			@Override public Set<Observation> getAllObservations() {
				Set<Observation> newSet = new HashSet<Observation>();
				for(Observation oldObs : oldObsModel.getAllObservations()) {
					for(int i = 0; i < number; ++i) {
						newSet.add(new Observation(oldObs.getDomain(), oldObs.getName() + i));
					}
				}
				return newSet;
			}

			@Override public double omega(Observation o, POMDPState s, GroundedAction a) {
				Observation oldObs = oldObsModel.getObservationByName(o.getName().replaceAll("\\d*$", ""));
				return oldObsModel.omega(oldObs, s, a) * reductionFactor;
			}

			@Override public Set<Observation> getPossibleObservationsFor(POMDPState s, GroundedAction a) {
				Set<Observation> newSet = new HashSet<Observation>();
				for(Observation oldObs : oldObsModel.getAllObservations()) {
					for(int i = 0; i < number; ++i) {
						newSet.add(new Observation(oldObs.getDomain(), oldObs.getName() + i));
					}
				}
				return newSet;
			}

			@Override public Observation makeObservationFor(POMDPState s, GroundedAction a) {
				Observation oldObs = oldObsModel.makeObservationFor(s, a);
				return new Observation(oldObs.getDomain(), oldObs.getName() + new java.util.Random().nextInt(number));
			}

			@Override public boolean isSuccess(Observation o) {
				return oldObsModel.isSuccess(new Observation(o.getDomain(), o.getName().replaceAll("\\d*$", "")));
			}
		};
	}

	/**
	 * Retrieves the observation with a specific name
	 * @param name 	The name of the observation
	 * @return 		The observation with the specified name, or null if no observation
	 *    			exists with that name
	 */
	public Observation getObservationByName(String name) {
		for(Observation o : this.getAllObservations()) {
			if(o.getName().equals(name)) return o;
		}
		return null;
	}

	/**
	 * Retrieval method for O, the Set of Observations
	 * @return 		a Set of all possible Observations in this domain
	 */
	public abstract Set<Observation> getAllObservations();

	/**
	 * Returns OMEGA(o, s, a), the probability of observing observation o after
	 * taking action a and arriving in state s.
	 * @param o 	The observation received
	 * @param s 	The state arrived in 
	 * @param a 	The action taken 
	 * @return 		a double representing Pr(o|s,a)
	 */
	public abstract double omega(Observation o, POMDPState s, GroundedAction a);

	/**
	 * Returns a set of all possible observations which could be observed
	 * after arriving in state s after taking action a
	 * @param s  	The state arrived in
	 * @param a 	The action taken
	 * @return  	All observations possible given s, and a.
	 */
	public abstract Set<Observation> getPossibleObservationsFor(POMDPState s, GroundedAction a);

	/**
	 * Samples the distribution over observations possible after taking action a
	 * and arriving in state s.
	 * @param s 	The state arrived in
	 * @param a 	The action taken
	 * @param 		An observation for this (s, a) pair sampled with the appropriate distribution
	 */
	public abstract Observation makeObservationFor(POMDPState s, GroundedAction a);

	/**
	 * Returns true if Observation o is a success
	 */
	public abstract boolean isSuccess(Observation o);
}