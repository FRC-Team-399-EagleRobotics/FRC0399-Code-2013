/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.Utilities;

/**
 *
 * @author Jackie
 */
public class FIRFilter {

    private double[] filterCoefficients;
    private double[] filterCircularBuffer;
    private int taps;
    private int index = 0;
    // Low Pass filter
    // Lets frequencies up to about 15hz through
    // Sampling rate 100hz, Fpass 10hz, Fstop 20hz
    // Apass 1db, Astop -80db
    private static double[] lowPass = { // Generated with MATLAB
        0.0009219922137813, 0.003357027925715, 0.006668464110805, 0.007552863366429,
        0.001032780355376, -0.01547866562202, -0.03695775644686, -0.04899393947907,
        -0.03326134137541, 0.02089643775041, 0.105771877242, 0.1940809202414,
        0.2505506210949, 0.2505506210949, 0.1940809202414, 0.105771877242,
        0.02089643775041, -0.03326134137541, -0.04899393947907, -0.03695775644686,
        -0.01547866562202, 0.001032780355376, 0.007552863366429, 0.006668464110805,
        0.003357027925715, 0.0009219922137813
    };

    public FIRFilter() {
        this(lowPass);
    }

    public FIRFilter(double[] coefficients) {
        taps = coefficients.length;
        filterCoefficients = new double[taps];
        filterCircularBuffer = new double[taps];
        System.arraycopy(coefficients, 0, filterCoefficients, 0, taps);
        for (int i = 0; i < taps; i++) {
            filterCircularBuffer[i] = 0;
        }
    }

    private void incrementIndex() {
        index = getIndex(1);
    }

    private int getIndex(int offset) {
        int newIndex = index + offset;
        newIndex = newIndex % taps;
        if (newIndex < 0) // if the offset was negative, wrap around correctly.
        {
            newIndex += taps;
        }
        return newIndex;
    }

    public double filter(double sample) {
        // Filter using a FIR -- Finite Impulse Response Filter.
        // A fancy moving average, each sample is given a different weight, and then summed.
        filterCircularBuffer[index] = sample;
        double mac = 0;
        for (int ii = 0; ii < taps; ii++) {
            double mult = filterCircularBuffer[getIndex(ii)] * filterCoefficients[taps - ii - 1];
            mac += mult;
        }
        incrementIndex();
        return mac;
    }
}



