package edu.brown.h2r.diapers.humanRobotAction;

import java.util.ArrayList;
import java.util.List;

import edu.brown.h2r.diapers.pomdp.POMDPDomain;

import burlap.oomdp.core.Domain;
import burlap.oomdp.core.State;

public class RashBehaviour {
	private RashDomain rashDomain;
	private List<State> stateList;
	private POMDPDomain domain;
	public RashBehaviour(){
		rashDomain = new RashDomain();
		domain = (POMDPDomain)rashDomain.generateDomain();
		stateList = domain.getAllStates();
	}

	


	public static void main(String[] args){
		RashBehaviour rashBehaviour = new RashBehaviour();
//		List<State> listStates = rashBehaviour.stateList;
//		for(State s : listStates){
//			System.out.println(s);
//		}
		System.out.println(rashBehaviour.stateList.size());
		List<State> stateList = rashBehaviour.stateList;
		for(State s: stateList){
			System.out.println("state: "+ stateList.indexOf(s));
			System.out.println(s);
		}
		if(false){
		List<String> actionList = new ArrayList<String>();
		State s = rashBehaviour.domain.getExampleState();
		System.out.println("example state: "+ s);
		State sTick = rashBehaviour.domain.getAction(S.ACTION_NULL).performAction(s, new String[]{});
		actionList.add(S.ACTION_NULL);
		System.out.println(sTick);
		
		if(false){
		System.out.println("bring diaper");
		
		State sTick1 = rashBehaviour.domain.getAction(S.ACTION_BRING_DIAPER).performAction(sTick, new String[]{});
//		actionList.add(S.ACTION_NULL);
		System.out.println(sTick1);
		
		System.out.println("bring ointment");
		sTick1 = rashBehaviour.domain.getAction(S.ACTION_BRING_OINTMENT).performAction(sTick, new String[]{});
//		actionList.add(S.ACTION_NULL);
		System.out.println(sTick1);
		}
		
		if(true){
		String mentalState1 = sTick.getObject(S.OBJ_HUMAN).getStringValForAttribute(S.ATTR_MENTAL_STATE);
		if (mentalState1.equals(S.MS_TYPE_RASH)){
			sTick = rashBehaviour.domain.getAction(S.ACTION_BRING_OINTMENT).performAction(sTick, new String[]{});
			actionList.add(S.ACTION_BRING_OINTMENT);
		}
		else if (mentalState1.equals(S.MS_TYPE_NO_RASH)){
			sTick = rashBehaviour.domain.getAction(S.ACTION_BRING_DIAPER).performAction(sTick, new String[]{});
			actionList.add(S.ACTION_BRING_DIAPER);
		}
		System.out.println(sTick);
		mentalState1 = sTick.getObject(S.OBJ_HUMAN).getStringValForAttribute(S.ATTR_MENTAL_STATE);
		if (mentalState1.equals(S.MS_TYPE_NO_RASH)){
			sTick = rashBehaviour.domain.getAction(S.ACTION_BRING_DIAPER).performAction(sTick, new String[]{});
			actionList.add(S.ACTION_BRING_DIAPER);
			System.out.println(sTick);
		}
		}
		
		for(String tempAction : actionList){
			System.out.println(tempAction);
		}
	}
	}
}
