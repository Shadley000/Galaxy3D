package com.shadley000.ai;

import java.util.HashMap;
import java.util.Map;

public class DecisionEngine {

	
	State currentState = null;
	State defaultState = null;
	
	Map<String, State> stateMap;
	
	public DecisionEngine()
	{
		stateMap = new HashMap<String, State>();
	}
	
	
	public void update()
	{
		if(currentState == null)
			currentState = defaultState;
		if(currentState !=null)  currentState.update(this);
	}
}
