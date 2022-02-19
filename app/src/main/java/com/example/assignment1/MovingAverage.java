package com.example.assignment1;

public class MovingAverage {
    private float circularBuffer[];
    private float avg;
    private int circularIndex;
    private int count;
    private boolean isFirstCall;


    public MovingAverage(int k) {
        circularBuffer = new float[k];
        count = 0;
        isFirstCall = true;
        circularIndex = 0;
        avg = 0;
    }

    /** Get the current moving average. */
    public float getValue() {
        return avg;
    }

    /** Calculate moving average and store last value in circular buffer. */
    public void pushValue(float x) {
        if (isFirstCall) {
            primeBuffer(x);
            isFirstCall = false;
        }
        count++;
        float lastValue = circularBuffer[circularIndex];
        avg = avg + (x - lastValue) / circularBuffer.length;
        circularBuffer[circularIndex] = x;
        circularIndex = nextIndex(circularIndex);
    }

    public long getCount() {
        return count;
    }
    private void primeBuffer(float val) {
        for (int i = 0; i < circularBuffer.length; ++i) {
            circularBuffer[i] = val;
        }
        avg = val;
    }
    private int nextIndex(int curIndex) {
        if (curIndex + 1 >= circularBuffer.length) {
            return 0;
        }
        return curIndex + 1;
    }
}
