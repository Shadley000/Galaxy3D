/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.shadley000.util;

import java.util.Random;

/**
 *
 * @author shadl
 */
public class MathTool
{   static public Random random = new Random(System.currentTimeMillis());

    static public double getRandom( double max)
    {
        return getRandom(0,  max);
    }
    
    static public double getRandom(double min, double max)
    {
         return min + (random.nextDouble() * (max - min) );
        
       // return min + ((random.nextDouble()+1)/2 * (max - min) );
    }
    
    static public double getRandomGausian(double average, double standardDeviation)
    {
        return average + (standardDeviation * random.nextGaussian());
    }
    
    static public int getRandomInt(int max) {return Math.abs(random.nextInt()%max);}
    static public long getRandomLong(long max) {return Math.abs(random.nextLong()%max);}
    
   
   
    
}
