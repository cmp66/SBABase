package com.oreilly.javaxslt.util;


public class Stopwatch {
    private long startTime;
    private long stopTime;
    private boolean running;
    private int numLaps;

    public String toString() {
        return getElapsedTime() + "ms";
    }

    public void start() {
        this.running = true;
        this.numLaps = 0;
        this.startTime = System.currentTimeMillis();
    }

    public void stop() {
        this.stopTime = System.currentTimeMillis();
        this.running = false;
    }

    public void lap() {
        this.numLaps++;
    }

    public long getElapsedTime() {
        if (this.running) {
            return System.currentTimeMillis() - this.startTime;
        }
        return this.stopTime - this.startTime;
    }

    public double getAverageTime() {
        if (this.numLaps == 0) {
            return getElapsedTime();
        }
        return (double) getElapsedTime() / (double) numLaps;
    }
}