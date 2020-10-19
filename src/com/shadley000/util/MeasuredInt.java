package com.shadley000.util;

public class MeasuredInt {

	
	int average;
    int standardDeviation;

    public MeasuredInt(int average, int standardDeviation) {
        this.average = average;
        this.standardDeviation = standardDeviation;
    }

    public int getValue() {
        return (int) MathTool.getRandomGausian(average, standardDeviation);

    }

    public int getAbsoluteValue() {
        return Math.abs(getValue());
    }
}
