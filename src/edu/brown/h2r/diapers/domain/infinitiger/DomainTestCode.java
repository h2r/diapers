package edu.brown.h2r.diapers.domain.infinitiger;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;
import edu.brown.h2r.diapers.domain.infinitiger.*;
import burlap.oomdp.core.Domain;

public class DomainTestCode {
	
	private static POMDPDomain pomdpDomain;
//	private static 
	
	public static void main(){
	pomdpDomain = (POMDPDomain) new InfinitigerDomain(1,1).generateDomain();
	
	
	
	}
	

}
