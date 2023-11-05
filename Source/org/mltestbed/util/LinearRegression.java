package org.mltestbed.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Vector;

import org.mltestbed.data.readDB.ReadIEA;

public class LinearRegression
{

	private double beta;
	private double oldbeta;

	private double[] weights;
	private double[] oldweights;

	private double learningRate = 0.001d;

	private int epochs;

	// private Function<T, R>

	public LinearRegression(int featuresCount, int epochs)
	{
		weights = new double[featuresCount];
		this.epochs = epochs;
	}

	public Optional<Double> predict(double[] inputs)
	{
		if (inputs == null || inputs.length <= 0)
		{
			return Optional.empty();
		}

		double result = 0d;
		for (int i = 0; i < inputs.length; i++)
		{
			result = inputs[i] * weights[i] + result;
		}

		result = result + beta;

		return Optional.of(result);
	}

	public void trainSGD(double[][] trainData, double[] result)
	{

		if (trainData == null || trainData.length <= 0)
		{
			throw new RuntimeException("Input baseData can not be null");
		}
		// Stochastic Gradient descent
		for (int e = 0; e < epochs; e++)
		{
			double mse = 0d;
			for (int i = 0; i < trainData.length; i++)
			{
				double[] tempInput = trainData[i];

				Optional<Double> predictedValueOptional = this
						.predict(tempInput);

				double predictedValue = predictedValueOptional.get();

				double error = predictedValue - result[i];
				mse = error * error + mse;

				for (int j = 0; j < weights.length; j++)
				{
					weights[j] = weights[j]
							- learningRate * error * tempInput[j];

				}
				beta = beta - learningRate * error;

			}

			mse = (Math.sqrt(mse)) / trainData.length;

			if (Double.isNaN(mse) || Double.isInfinite(mse))
			{
				weights = oldweights.clone();
				beta = oldbeta;
				break;
			}
			System.out.println("Epoch: " + e + " MSE " + mse + " Weights "
					+ Arrays.toString(weights) + " Beta " + beta);
			oldweights = weights.clone();
			oldbeta = beta;
		}

	}

	private static void trainModel()
	{
		double[][] trainSet =
		{
				{20},
				{16},
				{19.8},
				{18.4},
				{17.1},
				{15.5}};
		double[] result =
		{88.6, 71.6, 93.3, 84.3, 80.6, 75.2};
		LinearRegression linearRegression = new LinearRegression(
				trainSet[0].length, 1000);
		linearRegression.train(trainSet, result);

	}
	private static void trainModel1()
	{

		ReadIEA db = new ReadIEA();
		db.useTrainData();
		System.out.println("Train SQL= " + db.getSQLString());
		double[][] trainSet = new double[(int) db.getRecordCount()][db
				.getDim()];
		double[] result = new double[(int) db.getRecordCount()];
		ArrayList<Vector<Object>> data = db.getData();
		int i = 0;
		for (Iterator<Vector<Object>> iterator = data.iterator(); iterator
				.hasNext();)
		{
			Vector<Object> vector = (Vector<Object>) iterator.next();
			int j2 = vector.size() - 1;
			for (int j = 0; j < vector.size(); j++)
			{
				String string = vector.get(j).toString();
				if (j == j2)
					result[i] = Double.valueOf(string).doubleValue();
				else
					trainSet[i][j] = Double.valueOf(string).doubleValue();
			}
			i++;
		}

		LinearRegression linearRegression = new LinearRegression(
				trainSet[0].length, 1000);
		linearRegression.train(trainSet, result);
		SimpleRegression sr = new SimpleRegression();
		for (int j = 0; j < result.length; j++)
		{
			double expect = result[j];

			Optional<Double> predict = linearRegression.predict(trainSet[j]);
			sr.addData(expect, predict.get());
			System.out.println(expect + "," + predict.get());
		}
		System.out.println("Train Rsquard=" + sr.getRSquare());
		db.useTestData();
		System.out.println("Test SQL= " + db.getSQLString());

		db = new ReadIEA();
		double[][] testSet = new double[(int) db.getRecordCount()][db.getDim()];
		result = new double[(int) db.getRecordCount()];
		data = db.getData();
		i = 0;
		for (Iterator<Vector<Object>> iterator = data.iterator(); iterator
				.hasNext();)
		{
			Vector<Object> vector = (Vector<Object>) iterator.next();
			for (int j = 0; j < vector.size(); j++)
				if (j == vector.size() - 1)
					result[i] = Double.valueOf(vector.get(j).toString())
							.doubleValue();
				else
					testSet[i][j] = Double.valueOf(vector.get(j).toString())
							.doubleValue();
			i++;
		}

		sr = new SimpleRegression();
		System.out.println("Expect, Predict");
		for (int j = 0; j < result.length; j++)
		{
			double expect = result[j];

			Optional<Double> predict = linearRegression.predict(testSet[j]);
			sr.addData(expect, predict.get());
			System.out.println(expect + "," + predict.get());
		}
		System.out.println("Test Rsquard=" + sr.getRSquare());
	}

	private double RPredict(double[] input)
	{
		double ret = 0;
		double[] weights =
		{1.563, 0.098, -0.211, -0.028, -0.176, 0.217, -0.016, -0.109, -0.074,
				-0.115, 0.217, 0.087, 0.066, -0.055, -0.078, -0.19, 0.115,
				-0.02, 0.065, -0.257, -0.094, -0.142, -0.055, 0.031, 0.128,
				0.172, 0.128, -0.149, 0.004, 0.042, 0.084, 0.202, 0.025, 0.143,
				-0.051, -0.194, 0.032, -0.041, 0.071, 0.196, 0.023, 0.115,
				0.018, 0.171, -0.165, -0.04, 0.031, 0.251, 0.031, 0.081, 0.124,
				-0.243, -0.075, 0.002, 0.007, 0.001, 0.08, -0.029, -0.102,
				0.133, -0.172, 0.122, -0.112, -0.024, -0.152, -0.184, 0.056,
				0.008, 0.071, 0.056, -0.078, 0.078, -0.148, -0.007, 0.009,
				-0.148, 0.011, -0.187, -0.128, 0.053, -0.037, -0.033, 0.142,
				0.099, 0.005, 0.256, 0.16, -0.07, 0.108, -0.063, 0.131, -0.05,
				0.106, -0.194, -0.116, -0.117, -0.114, 0.179, -0.025, -0.027,
				-0.047, 0.023, 0.113, -0.093, 0.109, 0.115, 0.109, -0.165,
				0.026, 0.245, -0.465, 0.135, 0.147, -0.07, 0.289, -0.528, 0.204,
				0.141, 0.078, -0.125, 0.115, 0.387, 0.253, 0.135, -0.206,
				-0.217, 0.341, -0.38, 0.35};
		for (int i = 0; i < weights.length; i++)
		{
			ret += input[i] * weights[i];

		}
		return ret;
	}
	private void train(double[][] trainSet, double[] result)
	{
		trainSGD(trainSet, result);
	}

	public static void main(String[] args)
	{
//		trainModel();
		trainModel1();
		// testRandom();
	}
}