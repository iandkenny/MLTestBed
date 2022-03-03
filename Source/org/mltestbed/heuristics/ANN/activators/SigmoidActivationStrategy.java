package org.mltestbed.heuristics.ANN.activators;

import java.io.Serializable;

public class SigmoidActivationStrategy extends ActivationStrategy implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public double activate(double weightedSum) {
        return 1.0 / (1 + Math.exp(-1.0 * weightedSum));
    }

    public double derivative(double weightedSum) {
        return weightedSum * (1.0 - weightedSum);
    }

    public SigmoidActivationStrategy copy() {
        return new SigmoidActivationStrategy();
    }
}
