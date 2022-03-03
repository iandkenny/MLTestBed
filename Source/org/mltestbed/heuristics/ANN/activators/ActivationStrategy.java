package org.mltestbed.heuristics.ANN.activators;

public abstract class ActivationStrategy {
    abstract public double activate(double weightedSum);
    abstract public double derivative(double weightedSum);
    abstract public ActivationStrategy copy();
}
