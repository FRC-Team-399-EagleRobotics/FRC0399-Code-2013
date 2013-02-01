/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.Utilities;

/**
 *
 * @author Jeremy
 */
public class Integrator {
    private double value = 0.0;
    private long dT = 0, prevT = 0;
    
    public Integrator(double initialValue) {
        value += initialValue;
    }
    
    public void update(double input) {
        long time = System.currentTimeMillis();
        dT = time - prevT;
        prevT = time;
        value += input*dT;
        
    }
    
    
    public double get() {
        return value;
    }
    public void reset() {
        value = 0;
    }
    
    public void add(double input) {
        value +=input;
    }
    
}
