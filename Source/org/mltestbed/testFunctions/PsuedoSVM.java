/**
 * 
 */
package org.mltestbed.testFunctions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

/**
 * @author ian
 * 
 */
public class PsuedoSVM extends TestBase
{

	private static final String USE_LOG_VECTOR = "useLogVector";
	private static final String USE_OFFSET_VECTOR = "useOffsetVector";
	private static final String USE_POWER_VECTOR = "usePowerVector";
	private static final String USE_REVELANCE_VECTOR = "useRelevanceVector";
	protected int factor = 3;
	protected int factorMin = 3;
	protected int inputs = 0;
	private boolean logVec = false;
	private double[] logvector = null;
	private double[][] matrix = null;
	private boolean powVec = false;
	private double[] powvector = null;
	private double predicted;
	private boolean relVec = false;
	private Vector<Double> result;
	private boolean subVec = false;
	protected boolean useSum = true;
	private double[] vector = null;
	/**
	 * 
	 */
	public PsuedoSVM()
	{
		super();
		description = "Support Vector Machine support";
		info = "Functions for Support Vector Machine analysis";
	}
	/**
	 * @param v
	 * @throws Exception
	 */
	protected Vector<Double> CosmologyCalc(Vector<Double> v,
			Vector<Double> input) throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = v.size() / factor;
		// A = new double[factor][sizeA];
		A = new double[sizeA][factor];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) (sizeInput / sizeA);

		B = new double[sizeA][dim];
		// C = new double[factor][dim];
		C = new double[sizeA][dim];
		try
		{
			// for (int j = 0; j < factor; j++)
			for (int j = 0; j < sizeA; j++)
			{
				// for (int i = 0; i < sizeA; i++)
				for (int i = 0; i < factor; i++)
				{
					A[j][i] = v.get(index++);

				}
			}
			relevanceVector(A);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			for (int i = 0; i < sizeA; i++)
			{
				for (int j = 0; j < dim; j++)
				{
					B[i][j] = input.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array B: " + e.getMessage(), e));
			e.printStackTrace();
		}

		try
		{
			// for (int j = 0; j < N; j++)
			// for (int k = 0; k < N; k++)
			// for (int i = 0; i < N; i++)
			// C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < N; i++)
			// for (int k = 0; k < N; k++)
			// for (int j = 0; j < N; j++)
			// C[i][j] += A[i][k] * B[k][j];

			// for (int i = 0; i < factor; i++)
			// for (int k = 0; k < sizeA; k++)
			// for (int j = 0; j < dim; j++)
			// C[i][j] += A[i][k] * B[k][j];

			for (int i = 0; i < sizeA; i++)
				for (int k = 0; k < factor; k++)
					for (int j = 0; j < dim; j++)
						C[i][j] += A[i][k] * B[i][j];
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		index = 0;
		for (int i = 0; i < factor; i++)
		{
			for (int j = 0; j < dim; j++)
			{
				res.add(index++, C[i][j]);

			}
		}

		return res;
	}
	protected double CosmologyObjective(Particle p, ArrayList<Double> row)
	{
		Vector<Double> v = p.getPosition();
		Vector<Double> inpVec = new Vector<Double>(row);
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> result = null;
		Vector<Double> subV = new Vector<Double>();
		Vector<Double> powV = new Vector<Double>();
		Vector<Double> logV = new Vector<Double>();

		Double required = inpVec.lastElement();
		inpVec.remove(required);
		if (subVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (powVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				powV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (logVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				logV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		try
		{
			result = CosmologyCalc(vec, inpVec);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		if (subVec)
		{
			// int size = subV.size();
			int size = result.size();
			vector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = subV.get(i);
				result.set(i, result.get(i) - vecEle);
				vector[i] = vecEle;
			}

		}
		if (powVec)
		{
			// int size = subV.size();
			int size = result.size();
			powvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = powV.get(i);
				result.set(i, Math.pow(result.get(i), vecEle));
				powvector[i] = vecEle;
			}

		}
		if (logVec)
		{
			// int size = subV.size();
			int size = result.size();
			logvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = logV.get(i);
				double a = vecEle * vecEle;
				result.set(i, result.get(i) * Math.log10(a));
				logvector[i] = a;
			}

		}

		// NERC no abs
		// double sum = Math.abs(sum(result));
		double sum;
		if (useSum)
			sum = sum(result);
		else
			sum = reduceVector(result);

		predicted = sum;
		// NERC returns this
		// Absolute is Equivalent??
		mResult = Math.sqrt(Math.pow((required - sum), 2));
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		// super.setFuncSpecific(null);
		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><RequiredValue>" + required
				+ "</RequiredValue><SumResult>" + sum
				+ "</SumResult><ResultVector>" + result.toString()
				+ "</ResultVector><Result>" + mResult + "</Result></Output>");
		return super.Objective(p);
	}
	/**
	 * @param p
	 * @param row
	 * @return
	 */
	protected double CosmologyObjective3(Particle p, ArrayList<Double> row)
	{
		double score;

		Vector<Double> inpVec = new Vector<Double>(row);
		Double required = inpVec.lastElement();
		inpVec.remove(required);

		score = 0;
		Vector<Double> vec = new Vector<Double>(p.getPosition());
		for (int i = 0; i < inpVec.size(); i++)
		{
			score += vec.get(i) * inpVec.get(i);
		}
		score -= vec.get(vec.size() - 1);
		predicted = score;
		matrix = new double[vec.size()][1];
		for (int i = 0; i < vec.size(); i++)
		{
			matrix[i][0] = vec.get(i);
		}
		mResult = Math.sqrt(Math.pow((required - score), 2));

		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><RequiredValue>" + required
				+ "</RequiredValue><SumResult>" + score + "</SumResult><Result>"
				+ mResult + "</Result></Output>");

		return super.Objective(p);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#createParams()
	 */
	@Override
	protected void createParams()
	{
		params.setProperty(USE_OFFSET_VECTOR, "false");
		setSubVec(false);
		params.setProperty(USE_POWER_VECTOR, "false");
		setPowVec(false);
		params.setProperty(USE_LOG_VECTOR, "false");
		setLogVec(false);

		params.setProperty(USE_REVELANCE_VECTOR, "false");
		setRelVec(false);
	}

	protected double d(Vector<Double> x, Vector<Double> y) throws Exception
	{
		int size = y.size();
		if (x.size() != size)
			throw new Exception("Vectors are not the same size");
		double distance = 0;
		double sum = 0;
		for (int i = 0; i < size; i++)
		{
			double d = (Double) x.get(i) - (Double) y.get(i);
			double sqr = d * d;
			sum += sqr;
		}
		distance = Math.sqrt(sum);
		return distance;
	}

	public double determinant(double[][] mat)
	{

		double result = 0;

		if (mat.length == 1)
		{
			result = mat[0][0];
			return result;
		}

		if (mat.length == 2)
		{
			result = mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
			return result;
		}

		for (int i = 0; i < mat[0].length; i++)
		{
			double temp[][] = new double[mat.length - 1][mat[0].length - 1];

			for (int j = 1; j < mat.length; j++)
			{
				for (int k = 0; k < mat[0].length; k++)
				{

					if (k < i)
					{
						temp[j - 1][k] = mat[j][k];
					} else if (k > i)
					{
						temp[j - 1][k - 1] = mat[j][k];
					}

				}
			}

			result += mat[0][i] * Math.pow(-1, (double) i) * determinant(temp);
		}

		return result;

	}

	/**
	 * @param v
	 * @throws Exception
	 */
	protected Vector<Double> GasCalc(Vector<Double> v, Vector<Double> input)
			throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = v.size() / factor;
		// A = new double[factor][sizeA];
		A = new double[sizeA][factor];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) (sizeInput / sizeA);

		B = new double[sizeA][dim];
		// C = new double[factor][dim];
		C = new double[sizeA][dim];
		try
		{
			// for (int j = 0; j < factor; j++)
			for (int j = 0; j < sizeA; j++)
			{
				// for (int i = 0; i < sizeA; i++)
				for (int i = 0; i < factor; i++)
				{
					A[j][i] = v.get(index++);

				}
			}
			relevanceVector(A);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			for (int i = 0; i < sizeA; i++)
			{
				for (int j = 0; j < dim; j++)
				{
					B[i][j] = input.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array B: " + e.getMessage(), e));
			e.printStackTrace();
		}

		try
		{
			// for (int j = 0; j < N; j++)
			// for (int k = 0; k < N; k++)
			// for (int i = 0; i < N; i++)
			// C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < N; i++)
			// for (int k = 0; k < N; k++)
			// for (int j = 0; j < N; j++)
			// C[i][j] += A[i][k] * B[k][j];

			// for (int i = 0; i < factor; i++)
			// for (int k = 0; k < sizeA; k++)
			// for (int j = 0; j < dim; j++)
			// C[i][j] += A[i][k] * B[k][j];

			for (int i = 0; i < sizeA; i++)
				for (int k = 0; k < factor; k++)
					for (int j = 0; j < dim; j++)
						C[i][j] += A[i][k] * B[i][j];
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		index = 0;
		for (int i = 0; i < sizeA; i++)
		{
			for (int j = 0; j < dim; j++)
			{
				res.add(index++, C[i][j]);

			}
		}

		return res;
	}
	protected double GasObjective2(Particle p, ArrayList<Double> row,
			Vector<Double> expected)
	{
		Vector<Double> v = p.getPosition();

		Vector<Double> inpVec;
		synchronized (row)
		{
			inpVec = new Vector<Double>(row);
		}

		Vector<Double> vec;
		synchronized (v)
		{
			vec = new Vector<Double>(v);
		}
		Vector<Double> subV = new Vector<Double>();

		result = new Vector<Double>();
		if (subVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		try
		{
			result = GasCalc(vec, inpVec);
			if (subVec)
			{
				// int size = subV.size();
				int size = result.size();
				vector = new double[size];
				for (int i = 0; i < size; i++)
				{
					double vecEle = subV.get(i);
					result.set(i, result.get(i) - vecEle);
					vector[i] = vecEle;
				}

			}

			mResult = 0;
			for (int i = 0; i < result.size(); i++)
			{
				result.set(i, Math.signum(result.get(i)));
				mResult += Math
						.sqrt(Math.pow((expected.get(i) - result.get(i)), 2));
			}
			predicted = mResult;
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		// super.setFuncSpecific(null);
		String funcSpecific = "<Output><Input>" + inpVec.toString()
				+ "</Input><!--[CO_n,CO_L,CO_M,CO_H,Me_n,Me_L,Me_M,Me_H,Et_n,Et_L,Et_M,Et_H]--><RequiredValue>"
				+ expected.toString() + "</RequiredValue><ResultVector>"
				+ result.toString() + "</ResultVector><Result>" + mResult
				+ "</Result></Output>";
		p.setFuncSpecific(funcSpecific);
		return super.Objective(p);
	}
	/**
	 * @return the predicted
	 */
	public double getPredicted()
	{
		return predicted;
	}
	/**
	 * @return the result
	 */
	public Vector<Double> getResult()
	{
		return result;
	}
	/**
	 * Heaviside Step Function
	 * 
	 * @param x
	 * @return
	 */
	private double H(double x)
	{
		return (.5 * (Math.signum(x) + 1));
	}
	protected double heartObjective(Particle p, ArrayList<Double> row)
	{
		Vector<Double> v = p.getPosition();
		Vector<Double> inpVec = new Vector<Double>(row);
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> result = null;
		Vector<Double> subV = new Vector<Double>();
		Vector<Double> powV = new Vector<Double>();
		Vector<Double> logV = new Vector<Double>();
		Double required = inpVec.lastElement();
		inpVec.remove(required);
		if (subVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (powVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				powV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (logVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				logV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}

		try
		{
			result = ThracicCalc(vec, inpVec);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		if (subVec)
		{
			int size = result.size();
			vector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = subV.get(i);
				result.set(i, result.get(i) - vecEle);
				vector[i] = vecEle;
			}

		}

		if (powVec)
		{
			int size = result.size();
			powvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = powV.get(i);
				result.set(i, Math.pow(result.get(i), vecEle));
				powvector[i] = vecEle;
			}

		}

		if (logVec)
		{
			int size = result.size();
			logvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = logV.get(i);
				double d = vecEle * vecEle;
				result.set(i, result.get(i) * Math.log10(d));
				logvector[i] = d;
			}

		}

		double sum;
		// double sum = Math.abs(sum(result));
		if (useSum)
			sum = Math.round(sum(result));
		else
			sum = Math.round(reduceVector(result));
		// sum = Math.signum(sum) + 2;
		predicted = sum;
		mResult = Math.sqrt(Math.pow((required - sum), 2));
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		// super.setFuncSpecific(null);
		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><RequiredValue>" + required
				+ "</RequiredValue><SumResult>" + Math.abs(sum)
				+ "</SumResult><Result>"
				+ Math.sqrt(Math.pow(required - sum, 2))
				+ "</Result></Output>");
		return super.Objective(p);
	}

	/**
	 * @param v
	 * @throws Exception
	 */
	protected Vector<Double> IngberCalc(Vector<Double> v, Vector<Double> input)
			throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = v.size() / factor;
		A = new double[sizeA][factor];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) Math.ceil(sizeInput / sizeA);
		// B = new double[factor][dim];
		B = new double[sizeA][dim];
		C = new double[sizeA][dim];
		try
		{
			for (int j = 0; j < sizeA; j++)
			{
				for (int i = 0; i < factor; i++)
				{
					A[j][i] = v.get(index++);

				}
			}
			relevanceVector(A);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			for (int i = 0; i < sizeA; i++)
			{
				for (int j = 0; j < dim; j++)
				{
					B[i][j] = input.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array B: " + e.getMessage(), e));
			e.printStackTrace();
		}

		try
		{
			// for (int j = 0; j < N; j++)
			// for (int k = 0; k < N; k++)
			// for (int i = 0; i < N; i++)
			// C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < N; i++)
			// for (int k = 0; k < N; k++)
			// for (int j = 0; j < N; j++)
			// C[i][j] += A[i][k] * B[k][j];

			for (int i = 0; i < sizeA; i++)
				for (int k = 0; k < factor; k++)
					for (int j = 0; j < dim; j++)
						C[i][j] += A[i][k] * B[k][j];
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		index = 0;
		for (int j = 0; j < sizeA; j++)
		{
			for (int i = 0; i < dim; i++)
			{
				res.add(index++, C[j][i]);

			}
		}

		return res;
	}

	protected double IngberObjective(Particle p, ArrayList<Double> row,
			ArrayList<Double> next)
	{
		Vector<Double> inpVec = new Vector<Double>(row);
		Vector<Double> vec = new Vector<Double>(p.getPosition());
		Vector<Double> subV = null;

		if (subVec)
		{
			int reduceBy = mDimension / (factor + 1);
			subV = new Vector<Double>();
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		try
		{
			result = IngberCalc(vec, inpVec);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		if (subVec)
		{
			int size = subV.size();
			vector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = subV.get(i);
				result.set(i, result.get(i) - vecEle);
				vector[i] = vecEle;
			}

		}
		double sum = 0;
		for (int i = 0; i < result.size(); i++)
		{
			sum += Math.sqrt(Math.pow(result.get(i) - next.get(i), 2));
		}
		// Absolute is Equivalent??
		// mResult = Math.sqrt(Math.pow((required - sum), 2));
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		mResult = sum;

		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><PredictedVector>" + result.toString()
				+ "</PredictedVector><Next>" + next.toString()
				+ "</Next><SumResult>" + sum + "</SumResult><Result>" + mResult
				+ "</Result></Output>");
		return super.Objective(null);
	}

	/*
	 * This function will return the determinant of any two dimensional matrix.
	 * For this particular function a two dimensional double matrix needs to be
	 * passed as arguments - Avishek Ghosh
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#init()
	 */
	@Override
	public void init()
	{
		String buf = params.getProperty(USE_OFFSET_VECTOR, "false");
		setSubVec(buf.equalsIgnoreCase("true"));
		buf = params.getProperty(USE_POWER_VECTOR, "false");
		setPowVec(buf.equalsIgnoreCase("true"));
		buf = params.getProperty(USE_LOG_VECTOR, "false");
		setLogVec(buf.equalsIgnoreCase("true"));
		buf = params.getProperty(USE_REVELANCE_VECTOR, "false");
		setRelVec(buf.equalsIgnoreCase("true"));
	}
	/**
	 * @return the logVec
	 */
	public boolean isLogVec()
	{
		return logVec;
	}

	/**
	 * @return the powVec
	 */
	public boolean isPowVec()
	{
		return powVec;
	}
	/**
	 * @return the relVec
	 */
	public boolean isRelVec()
	{
		return relVec;
	}
	/**
	 * @return the subVec
	 */
	public boolean isSubVec()
	{
		return subVec;
	}
	/**
	 * @param v
	 * @throws Exception
	 */
	protected Vector<Double> NERCCalc(Vector<Double> v, Vector<Double> input)
			throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = v.size() / factor;
		A = new double[factor][sizeA];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) (sizeInput / sizeA);
		// B = new double[factor][dim];
		B = new double[sizeA][dim];
		C = new double[factor][dim];
		try
		{
			for (int j = 0; j < factor; j++)
			{
				for (int i = 0; i < sizeA; i++)
				{
					A[j][i] = v.get(index++);

				}
			}
			relevanceVector(A);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			for (int i = 0; i < sizeA; i++)
			{
				for (int j = 0; j < dim; j++)
				{
					B[i][j] = input.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array B: " + e.getMessage(), e));
			e.printStackTrace();
		}

		try
		{
			// for (int j = 0; j < N; j++)
			// for (int k = 0; k < N; k++)
			// for (int i = 0; i < N; i++)
			// C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < N; i++)
			// for (int k = 0; k < N; k++)
			// for (int j = 0; j < N; j++)
			// C[i][j] += A[i][k] * B[k][j];

			for (int i = 0; i < factor; i++)
				for (int k = 0; k < sizeA; k++)
					for (int j = 0; j < dim; j++)
						C[i][j] += A[i][k] * B[k][j];
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		index = 0;
		for (int i = 0; i < factor; i++)
		{
			for (int j = 0; j < dim; j++)
			{
				res.add(index++, C[i][j]);

			}
		}

		return res;
	}

	protected double NERCObjective(Particle p, ArrayList<Double> row)
	{
		Vector<Double> v = p.getPosition();
		Vector<Double> inpVec = new Vector<Double>(row);
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> result = null;
		Vector<Double> subV = new Vector<Double>();
		Vector<Double> powV = new Vector<Double>();
		Vector<Double> logV = new Vector<Double>();

		Double required = inpVec.lastElement();
		inpVec.remove(required);
		if (subVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (powVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				powV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (logVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				logV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}

		try
		{
			result = NERCCalc(vec, inpVec);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		if (subVec)
		{
			// int size = subV.size();
			int size = result.size();
			vector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = subV.get(i);
				result.set(i, result.get(i) - vecEle);
				vector[i] = vecEle;
			}

		}
		if (powVec)
		{
			int size = result.size();
			powvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = powV.get(i);
				result.set(i, Math.pow(result.get(i), vecEle));
				powvector[i] = vecEle;
			}

		}

		if (logVec)
		{
			int size = result.size();
			logvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = logV.get(i);
				double a = vecEle * vecEle;
				result.set(i, result.get(i) * Math.log10(a));
				logvector[i] = a;
			}

		}

		// NERC no abs
		// double sum = Math.abs(sum(result));
		double sum;
		if (useSum)
			sum = sum(result);
		else
			sum = reduceVector(result);

		predicted = sum;
		// NERC returns this
		// Absolute is Equivalent??
		mResult = Math.sqrt(Math.pow((required - sum), 2));
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		// super.setFuncSpecific(null);
		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><RequiredValue>" + required
				+ "</RequiredValue><SumResult>" + sum + "</SumResult><Result>"
				+ mResult + "</Result></Output>");
		return super.Objective(p);
	}

	/*
	 * Vector norm
	 */
	protected double norm(Vector<Double> x)
	{
		double sum = 0;
		int size = x.size();
		for (int i = 0; i < size; i++)
		{
			double xv = (Double) x.get(i);
			double sqr = xv * xv;
			sum += sqr;
		}
		return Math.sqrt(sum);
	}
	/**
	 * @param v
	 * @throws Exception
	 */
	protected Vector<Double> PDCalc(Vector<Double> v, Vector<Double> input)
			throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = v.size() / factor;
		A = new double[sizeA][factor];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) (sizeInput / sizeA);
		// B = new double[factor][dim];
		B = new double[sizeA][dim];
		C = new double[sizeA][dim];
		try
		{
			for (int j = 0; j < sizeA; j++)
			{
				for (int i = 0; i < factor; i++)
				{
					A[j][i] = v.get(index++);

				}
			}
			relevanceVector(A);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			for (int i = 0; i < sizeA; i++)
			{
				for (int j = 0; j < dim; j++)
				{
					B[i][j] = input.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array B: " + e.getMessage(), e));
			e.printStackTrace();
		}

		try
		{
			// for (int j = 0; j < N; j++)
			// for (int k = 0; k < N; k++)
			// for (int i = 0; i < N; i++)
			// C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < N; i++)
			// for (int k = 0; k < N; k++)
			// for (int j = 0; j < N; j++)
			// C[i][j] += A[i][k] * B[k][j];

			for (int i = 0; i < factor; i++)
				for (int k = 0; k < sizeA; k++)
					for (int j = 0; j < dim; j++)
						// C[i][j] += A[i][k] * B[k][j];
						C[k][j] += B[k][j] * A[k][i];
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		index = 0;
		for (int i = 0; i < sizeA; i++)
		{
			for (int j = 0; j < dim; j++)
			{
				res.add(index++, C[i][j]);

			}
		}

		return res;
	}

	protected double PDObjective(Particle p, ArrayList<Double> row)
	{
		Vector<Double> v = p.getPosition();
		Vector<Double> inpVec = new Vector<Double>(row);
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> result = null;
		Vector<Double> subV = new Vector<Double>();
		Vector<Double> powV = new Vector<Double>();
		Vector<Double> logV = new Vector<Double>();

		Double required = inpVec.lastElement();
		inpVec.remove(required);
		if (subVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (powVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				powV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (logVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				logV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}

		try
		{
			result = PDCalc(vec, inpVec);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		if (subVec)
		{
			int size = result.size();
			vector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = subV.get(i);
				result.set(i, result.get(i) - vecEle);
				vector[i] = vecEle;
			}

		}
		if (powVec)
		{
			int size = result.size();
			powvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = powV.get(i);
				result.set(i, Math.pow(result.get(i), vecEle));
				powvector[i] = vecEle;
			}

		}

		if (logVec)
		{
			int size = result.size();
			logvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = logV.get(i);
				double d = vecEle * vecEle;
				result.set(i, result.get(i) * Math.log10(d));
				logvector[i] = d;
			}

		}

		double sum;
		// double sum = Math.abs(sum(result));
		if (useSum)
			sum = Math.round(sum(result));
		else
			sum = Math.round(reduceVector(result));
		// sum = Math.signum(sum) + 2;
		predicted = ((sum > 2.0) ? 2.0 : (sum < 1.0) ? 1.0 : sum);
		mResult = Math.sqrt(Math.pow((required - sum), 2));
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		// super.setFuncSpecific(null);
		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><RequiredValue>" + required
				+ "</RequiredValue><SumResult>"
				+ ((sum > 2.0) ? 2.0 : (sum < 1.0) ? 1.0 : sum)
				+ "</SumResult><Result>" + Math.sqrt(Math.pow((required
						- ((sum > 2.0) ? 2.0 : (sum < 1.0) ? 1.0 : sum)), 2))
				// + Math.sqrt(Math.pow((required - sum), 2))
				+ "</Result></Output>");
		return super.Objective(p);
	}
	protected double reduceVector(Vector<Double> v)
	{
		boolean flag = true;
		double sum = Math.sqrt(Math.abs(v.get(0)));
		for (int i = 1; i < v.size(); i++)
		{
			Double a = v.get(i);
			if (flag)
				sum += Math.cos(a);
			else
				sum += Math.sin(a);
			flag = !flag;

		}
		return sum;
	}

	/**
	 * @param A
	 */
	protected void relevanceVector(double[][] A)
	{
		if (relVec)
			for (int j = 0; j < A.length; j++)
				if (H(A[j][0]) == 0)
					for (int k = 0; k < A[j].length; k++)
						A[j][k] = 0;
	}

	/**
	 * @param logVec
	 *            the logVec to set
	 */
	public void setLogVec(boolean logVec)
	{
		this.logVec = logVec;
	}

	// SSE=sum((y-ypred).^2);
	// SST=sum((y-mean(y)).^2)
	// 1-SSE/SST
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#setMDimension(int)
	 */
	@Override
	public void setMDimension(int dimension) throws Exception
	{
		int add = (subVec) ? inputs : 0;
		add += (powVec) ? inputs : 0;
		add += (logVec) ? inputs : 0;

		super.setMDimension(dimension + add);
	}

	/**
	 * @param powVec
	 *            the powVec to set
	 */
	public void setPowVec(boolean powVec)
	{
		this.powVec = powVec;
	}
	@Override
	protected void setRange()
	{
		// TODO Auto-generated method stub

	}

	/**
	 * @param relVec
	 *            the relVec to set
	 */
	public void setRelVec(boolean relVec)
	{
		this.relVec = relVec;
	}

	/**
	 * @param subVec
	 *            the subVec to set
	 */
	public void setSubVec(boolean subVec)
	{
		this.subVec = subVec;
	}
	protected double ThoracicObjective(Particle p, ArrayList<Double> row)
	{
		Vector<Double> v = p.getPosition();
		Vector<Double> inpVec = new Vector<Double>(row);
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> result = null;
		Vector<Double> subV = new Vector<Double>();
		Vector<Double> powV = new Vector<Double>();
		Vector<Double> logV = new Vector<Double>();
		Double required = inpVec.lastElement();
		inpVec.remove(required);
		if (subVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (powVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				powV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		if (logVec)
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				logV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
		}
		try
		{
			result = ThracicCalc(vec, inpVec);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
		if (subVec)
		{
			int size = result.size();
			vector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = subV.get(i);
				result.set(i, result.get(i) - vecEle);
				vector[i] = vecEle;
			}

		}
		if (powVec)
		{
			int size = result.size();
			powvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = powV.get(i);
				result.set(i, Math.pow(result.get(i), vecEle));
				powvector[i] = vecEle;
			}

		}

		if (logVec)
		{
			int size = result.size();
			logvector = new double[size];
			for (int i = 0; i < size; i++)
			{
				double vecEle = logV.get(i);
				double d = vecEle * vecEle;
				result.set(i, result.get(i) * Math.log10(d));
				logvector[i] = d;
			}

		}
		double sum;
		// double sum = Math.abs(sum(result));
		if (useSum)
			sum = Math.round(sum(result));
		else
			sum = Math.round(reduceVector(result));
//		sum = Math.ceil(sigmod(sum) + 2);
		predicted = sum;
		mResult = Math.sqrt(Math.pow((required - sum), 2));
		// mResult = Math.abs(required - sum);
		// mResult = required - sum;
		// super.setFuncSpecific(null);
		p.setFuncSpecific("<Output><Input>" + inpVec.toString()
				+ "</Input><RequiredValue>" + required
				+ "</RequiredValue><SumResult>"
				+ ((sum > 2.0) ? 2.0 : (sum < 1.0) ? 1.0 : sum)
				+ "</SumResult><Result>"
				+ Math.sqrt(Math.pow((required
						- ((sum > 2.0) ? 2.0 : (sum < 1.0) ? 1.0 : sum)), 2))
				+ "</Result></Output>");
		return super.Objective(p);
	}

	/**
	 * @param v
	 * @throws Exception
	 */
	protected Vector<Double> ThracicCalc(Vector<Double> v, Vector<Double> input)
			throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = v.size() / factor;
		A = new double[sizeA][factor];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) (sizeInput / sizeA);
		// B = new double[factor][dim];
		B = new double[sizeA][dim];
		C = new double[sizeA][dim];
		try
		{
			for (int j = 0; j < sizeA; j++)
			{
				for (int i = 0; i < factor; i++)
				{
					A[j][i] = v.get(index++);

				}
			}
			relevanceVector(A);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			for (int i = 0; i < sizeA; i++)
			{
				for (int j = 0; j < dim; j++)
				{
					B[i][j] = input.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array B: " + e.getMessage(), e));
			e.printStackTrace();
		}

		try
		{
			// for (int j = 0; j < N; j++)
			// for (int k = 0; k < N; k++)
			// for (int i = 0; i < N; i++)
			// C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < N; i++)
			// for (int k = 0; k < N; k++)
			// for (int j = 0; j < N; j++)
			// C[i][j] += A[i][k] * B[k][j];

			for (int i = 0; i < factor; i++)
				for (int k = 0; k < sizeA; k++)
					for (int j = 0; j < dim; j++)
						// C[i][j] += A[i][k] * B[k][j];
						C[k][j] += B[k][j] * A[k][i];
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		index = 0;
		for (int i = 0; i < sizeA; i++)
		{
			for (int j = 0; j < dim; j++)
			{
				res.add(index++, C[i][j]);

			}
		}

		return res;
	}

	public String toXML()
	{
		String xml = toXML(matrix);
		return xml;
	}
	private String toXML(double[][] matrix)
	{
		String xml = "<Matrix>";
		if (matrix != null)
		{
			NumberFormat formatter = new DecimalFormat(
					"##################0.0##############################");

			int rows = matrix.length;
			int cols = matrix[0].length;
			for (int i = 0; i < rows; i++)
			{
				xml += "\n\t<Row>";
				for (int j = 0; j < cols; j++)
				{
					xml += "\n\t\t<Cell>" + formatter.format(matrix[i][j])
							+ "\n\t\t</Cell>";
				}
				xml += "\n\t</Row>";
			}
		}
		xml += "\n</Matrix>";
		if (subVec)
		{
			xml += "\n<Vector>";
			if (vector != null)
			{
				for (int i = 0; i < vector.length; i++)
				{
					xml += "\n\t<Cell>";
					xml += "\n\t\t" + vector[i];
					xml += "\n\t</Cell>";
				}
			}
			xml += "\n</Vector>";

		}
		if (powVec)
		{
			xml += "\n<PowerVector>";
			if (powvector != null)
			{
				for (int i = 0; i < powvector.length; i++)
				{
					xml += "\n\t<Cell>";
					xml += "\n\t\t" + powvector[i];
					xml += "\n\t</Cell>";
				}
			}
			xml += "\n</PowerVector>";

		}
		if (logVec)
		{
			xml += "\n<LogVector>";
			if (logvector != null)
			{
				for (int i = 0; i < logvector.length; i++)
				{
					xml += "\n\t<Cell>";
					xml += "\n\t\t" + logvector[i];
					xml += "\n\t</Cell>";
				}
			}
			xml += "\n</LogVector>";

		}
		return xml;
	}

}
