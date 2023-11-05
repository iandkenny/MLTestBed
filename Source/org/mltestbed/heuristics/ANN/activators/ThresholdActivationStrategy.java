package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

/**
 * NB: This class should NEVER be used in the backpropagation algorithm! This function is not differentiable at the
 * threshold. This class can be used to construct AND and OR networks when starting to learn about neural nets.
 * These networks don't actually end up getting trained.
 * 
 *  Indeed predicting this class of function is known to be NP-hard:
 *  S. Ben-David, N. Eiron, and P. M. Long.
 *  “On the difficulty of approximately maximizing
 *  agreements”. In: J. Comput. System Sci.
 *  66.3 (2003), pp. 496–514.
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
