package edu.brown.h2r.diapers.domain.rocksample;

import burlap.oomdp.auxiliary.StateParser;
import burlap.oomdp.core.ObjectInstance;
import burlap.oomdp.core.State;

public class RockSampleDomainStateParser implements StateParser{
	
	
		public String stateToString(State s) {
			String str = "";
			ObjectInstance rover = s.getObject(Names.OBJ_AGENT);
			int xPos = rover.getDiscValForAttribute(Names.ATTR_X);
			int yPos = rover.getDiscValForAttribute(Names.ATTR_Y);
			str+="ra_" +xPos+"_"+yPos + "_"; 
			
			int numberOfRocks = s.getObject(Names.OBJ_GRID).getDiscValForAttribute(Names.ATTR_NUMBER_OF_ROCKS);
			for(int i=0;i<numberOfRocks;i++){
				ObjectInstance rock = s.getObject(Names.OBJ_ROCK+i);
				int xRockPos = rock.getDiscValForAttribute(Names.ATTR_X);
				int yRockPos = rock.getDiscValForAttribute(Names.ATTR_Y);
				String value = rock.getBooleanValue(Names.ATTR_VALUE) ? "t_": "f_";
				str+="ro"+ i+"_"+xRockPos+"_"+yRockPos+"_"+ value;
			}
			
			
			return str.substring(0, str.length()-1);
		}
		
		public State stringToState(String s) {
			return new State();
		}
	

}
