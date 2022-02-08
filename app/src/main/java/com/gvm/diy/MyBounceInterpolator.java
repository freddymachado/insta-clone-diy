package com.gvm.diy;

public class MyBounceInterpolator implements android.view.animation.Interpolator {
    private double mAmplitud = 1;
    private double mFrequency = 10;


    public MyBounceInterpolator(double mAmplitud, double mFrequency) {
        this.mAmplitud = mAmplitud;
        this.mFrequency = mFrequency;
    }

    public MyBounceInterpolator() {
    }

    @Override
    public float getInterpolation(float v) {
        return (float) (-1 * Math.pow(Math.E, -v/ mAmplitud) * Math.cos(mFrequency * v) + 1);
    }
}
