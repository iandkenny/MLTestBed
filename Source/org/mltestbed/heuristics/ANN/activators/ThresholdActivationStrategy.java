package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

/**
 * NOTE: This class should NEVER be used in the backpropagation algorithm! This function is not differentiable at the
 * threshold. I used this class to construct AND and OR networks when I was just starting to learn about neural nets.
 * These networks don't actually end up getting trained. 
 */

public class ThresholdActivationStrategy extends ActivationStrategy implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double threshold;

    public ThresholdActivationStrategy(double threshold) {
        this.threshold = threshold;
    }

    public double activate(double weightedSum) {
        return weightedSum > threshold ? 1 : 0;
    }

    public double derivative(double weightedSum) {
        return 0;
    }

    public ThresholdActivationStrategy copy() {
        return new ThresholdActivationStrategy(threshold);
    }
}
