/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Gemstone2;

import org.apache.log4j.Logger;

/**
 *
 * @author jusjoken
 * - 04/04/2012 - updated for Gemstone
 */
public class StopWatch {
    
    static private final Logger LOG = Logger.getLogger(StopWatch.class);
    private long startTime = 0;
    private long stopTime = 0;
    private Boolean running = Boolean.FALSE;
    private String StopWatchName = "StopWatch";

    public StopWatch(String Name){
        this.StopWatchName = Name;
        //Start();
    }
    
    public void SetName(String Name){
        this.StopWatchName = Name;
    }
    
    public void Start() {
        this.startTime = System.currentTimeMillis();
        this.running = Boolean.TRUE;
    }
    
    public void Stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = Boolean.FALSE;
    }

    public void StopandLog() {
        this.stopTime = System.currentTimeMillis();
        this.running = Boolean.FALSE;
        LOG.debug(StopWatchName + ": completed in '" + getElapsedTime() + "' milliseconds");
    }
    
    public void LogCurrent(String LogText) {
        this.stopTime = System.currentTimeMillis();
        this.running = Boolean.FALSE;
        LOG.debug(StopWatchName + ": " + LogText + " in '" + getElapsedTime() + "' milliseconds");
        this.startTime = System.currentTimeMillis();
        this.running = Boolean.TRUE;
    }

    //elaspsed time in milliseconds
    public long getElapsedTime() {
        long elapsed;
        if (running) {
             elapsed = (System.currentTimeMillis() - startTime);
        }
        else {
            elapsed = (stopTime - startTime);
        }
        return elapsed;
    }
    
    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
        long elapsed;
        if (running) {
            elapsed = ((System.currentTimeMillis() - startTime) / 1000);
        }
        else {
            elapsed = ((stopTime - startTime) / 1000);
        }
        return elapsed;
    }
        
}
