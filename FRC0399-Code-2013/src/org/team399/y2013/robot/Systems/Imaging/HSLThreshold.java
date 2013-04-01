/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.team399.y2013.robot.Systems.Imaging;

/**
 * an object to represent an HSL threshold
 * @author Justin S.
 */
public class HSLThreshold {

    public int HueHigh;
    public int HueLow;
    public int SatHigh;
    public int SatLow;
    public int LumHigh;
    public int LumLow;

    /**
     * Constructor
     * @param hueL
     * @param hueH
     * @param satL
     * @param satH
     * @param lumL
     * @param lumH 
     */
    public HSLThreshold(int hueL, int hueH, int satL, int satH, int lumL, int lumH) {
        HueHigh = hueH;
        HueLow = hueL;
        SatHigh = satH;
        SatLow = satL;
        LumHigh = lumH;
        LumLow = lumL;
    }
}
