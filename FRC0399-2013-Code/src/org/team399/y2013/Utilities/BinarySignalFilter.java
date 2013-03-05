/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.Utilities;

import com.sun.squawk.util.MathUtils;

/**
 * Simple filter to debounce a boolean input. Set a buffer size. will only return true if 
 * @author Jeremy
 */
public class BinarySignalFilter {

    boolean[] dataPts;
    int length;
    int i = 0;
    
    final int INT_SIZE = 32;

    public BinarySignalFilter(int length) {
    	if(length >= INT_SIZE)  
        {
            length = INT_SIZE-1;
            System.out.println("Capped max filter length");
        }
        
        dataPts = new boolean[length];
        this.length = length;
    }

    public boolean calculate(boolean in) {
        dataPts[i] = in;
        i++;
        if (i >= length) {
            i = 0;
        }

        boolean out = false;
        int n = 0;
        for (int j = 0; j < length; j++) {
            n = (n << 1) + (dataPts[i] ? 1 : 0);
        }
        
        out = n == (MathUtils.pow(2, length) - 1);
        return out;
    }
}
