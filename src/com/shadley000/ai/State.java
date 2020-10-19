package com.shadley000.ai;

public class State {

	ProbabilityTable violationProbabilityTable;
	ProbabilityTable defaultNextProbabilityTable;
	ProbabilityTable defaultProbabilityTable;

	public State() {
	}

	public void update(DecisionEngine parent) {

		State nextState = null;
		State defaultNextState = null;

		boolean isStateComplete = false;

		if (stateConditionViolation()) {
			nextState = violationProbabilityTable.getNextState();

		} else if (timerExpiration()) {
			nextState = defaultNextProbabilityTable.getNextState();
		} else {
			parent.currentState = defaultProbabilityTable.getNextState();
		}

		if (nextState != null) {
			parent.currentState = nextState;
		}

	}

	private boolean timerExpiration() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean stateConditionViolation() {
		// TODO Auto-generated method stub
		return false;
	}

}
