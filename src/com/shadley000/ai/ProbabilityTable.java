package com.shadley000.ai;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.shadley000.util.NameValuePair;

public class ProbabilityTable {

	// this design will not work duplicate key problem
	List<NameValuePair> stateList = new ArrayList<NameValuePair>();
	double total = 0;

	public void addState(String stateName, double part) {
		stateList.add(new NameValuePair(stateName,part));
		part += part;
	}

	public State getNextState() {

		double runningTotal = 0;
		for (Iterator<NameValuePair> it = stateList.iterator(); it.hasNext();) {
			NameValuePair pair = it.next();
			runningTotal += pair.getValue();
			
		}
		// TODO Auto-generated method stub
		return null;
	}

}
