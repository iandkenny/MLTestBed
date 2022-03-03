package org.mltestbed.heuristics.ANN.generator;

import java.util.ArrayList;
import java.util.Vector;

/**
 * @author ian
 *
 */
public class TrainingData {

    private ArrayList<Vector<Double>> inputs;
    private ArrayList<Vector<Double>> outputs;

    public TrainingData(ArrayList<Vector<Double>> inputs, ArrayList<Vector<Double>> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public ArrayList<Vector<Double>> getInputs() {
        return inputs;
    }

    public ArrayList<Vector<Double>> getOutputs() {
        return outputs;
    }
}
