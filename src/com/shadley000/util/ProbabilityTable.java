/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shadley000.util;


import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shadl
 */
public class ProbabilityTable {

    private Map<String, Double> map = new HashMap<>();

    public ProbabilityTable() {

    }

    public void add(String outcome, double value) {
        map.put(outcome, value);
    }


    public String getRandomOutcome() {

        double sum = 0;
        for (Double value : map.values()) {
            sum += value;
        };

        double random = MathTool.getRandom(sum);
        //System.out.println("sum = " + sum + "   random:" + random);
        
        sum=0;
        for (String key : map.keySet()) {
            sum += map.get(key);
            if (random < sum) {
                return (key);
            }
        }
        return null;
    }
}
