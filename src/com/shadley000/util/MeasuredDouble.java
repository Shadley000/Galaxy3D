/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shadley000.util;
/**
 *
 * @author shadl
 */
public class MeasuredDouble {

    double average;
    double standardDeviation;

    public MeasuredDouble(double average, double standardDeviation) {
        this.average = average;
        this.standardDeviation = standardDeviation;
    }

    public double getValue() {
        return MathTool.getRandomGausian(average, standardDeviation);

    }

    public double getAbsoluteValue() {
        return Math.abs(getValue());
    }
}
