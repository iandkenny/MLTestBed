/**
 * 
 */
package org.mltestbed.testFunctions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.util.HausdorffCalculator;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.RandGen;
import org.mltestbed.util.SimpleRegression;
import org.mltestbed.util.Util;

/**
 * @author ian
 * 
 */
public class IFS extends TestBase
{
	private static final String FACTOR = "factor";
	private static final String NUMBER_OF_FUNCTIONS = "NumberOfFunctions";
	private static final String NUMBER_OF_TRIALS = "NumberOfTrials";
	private static final String SKIP_FIRST = "skipFirst";

	private static final String USE_LOG_VECTOR = "useLogVector";
	private static final String USE_OFFSET_VECTOR = "useOffsetVector";
	private static final String USE_POWER_VECTOR = "usePowerVector";
	private static final String VALIDATE = "Validate";

	protected int factor = 2;
	private int funcLen = 0;
	private HashMap<Integer, Vector<Double>> functions = new HashMap<Integer, Vector<Double>>();
	private HausdorffCalculator hausdorff = new HausdorffCalculator();
	protected int inputs = 0;
	private boolean log2n = false;
	private boolean logVec = false;
	private double[] logvector = null;
	private double[][] matrix = null;
	private int noTrials = 1;
	private int numFuncs = 4;
	private boolean powVec = false;
	private double[] powvector = null;
	private Random rand;
	private int skipFirst = 1000;
	private boolean subVec = false;
	protected boolean useSum = true;
	private double[] vector = null;

	/**
	 * 
	 */
	public IFS()
	{
		super();
		description = "IFS support";
		info = "Functions for IFS analysis";
	}
	protected ArrayList<Double> calc(Vector<Double> v, ArrayList<Double> input)
			throws Exception
	{
		ArrayList<Double> result = new ArrayList<Double>();
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> subV = new Vector<Double>();
		Vector<Double> powV = new Vector<Double>();
		Vector<Double> logV = new Vector<Double>();

		double A[][] = null;
		double B[][] = null;
		double C[][] = null;

		int sizeA = (vec.size() - inputs) / factor;
		// A = new double[factor][sizeA];
		//
		int index = 0;
		int sizeInput = input.size();
		// if (sizeInput != sizeA)
		// throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) Math.ceil(sizeInput / sizeA);
		// B = new double[factor][dim];
		// B = new double[sizeA][dim];
		// C = new double[factor][dim];
		try
		{
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
			A = new double[factor][sizeA];
			// A = new double[sizeA][factor];

			index = 0;
			for (int j = 0; j < factor; j++)
			{
				for (int i = 0; i < sizeA; i++)
				{
					A[j][i] = vec.get(index++);

				}
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array A: " + e.getMessage(), e));
			// e.printStackTrace();
		}
		index = 0;
		try
		{
			B = new double[sizeA][dim];

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
		// C = new double[factor][dim];
		C = new double[sizeA][dim];

		try
		{
			for (int i = 0; i < factor; i++)
				for (int k = 0; k < sizeA; k++)
					for (int j = 0; j < dim; j++)
						C[i][j] += A[i][k] * B[k][j];
			// for (int i = 0; i < sizeA; i++)
			// for (int k = 0; k < factor; k++)
			// for (int j = 0; j < dim; j++)
			// C[i][j] += A[i][k] * B[i][j];

		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Array C: " + e.getMessage(), e));
			e.printStackTrace();
		}
		matrix = A;
		try
		{

			index = 0;
			for (int j = 0; j < sizeA; j++)
			{
				for (int i = 0; i < dim; i++)
				{
					result.add(index++, C[j][i]);

				}
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

		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#createParams()
	 */
	@Override
	protected void createParams()
	{
		numFuncs = 4;
		noTrials = 1;
		skipFirst = 1000;
		factor = 2;
		params.setProperty(FACTOR, Integer.toString(factor));
		params.setProperty(NUMBER_OF_FUNCTIONS, "auto");
		params.setProperty(NUMBER_OF_TRIALS, Integer.toString(noTrials));
		params.setProperty(SKIP_FIRST, Integer.toString(skipFirst));

		params.setProperty(USE_OFFSET_VECTOR, "false");
		setSubVec(false);
		params.setProperty(USE_POWER_VECTOR, "false");
		setPowVec(false);
		params.setProperty(USE_LOG_VECTOR, "false");
		setLogVec(false);
		params.setProperty(VALIDATE, "false");
		setValidate(false);

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

	/*
	 * This function will return the determinant of any two dimensional matrix.
	 * For this particular function a two dimensional double matrix needs to be
	 * passed as arguments - Avishek Ghosh
	 */
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

	protected String functionsToXML(double[][] A)
	{
		String xml = "<Functions>";
		for (int i = 0; i < functions.size(); i++)
		{
			xml += "<Probability>" + A[i][0] + "</Probability>";
			xml += "<Function>" + functions.get(i).toString() + "</Function>"; // TODO
																				// format
																				// output
																				// better
		}
		xml += "</Functions>";
		return xml;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#init()
	 */
	@Override
	public void init()
	{
		performTest();
		String buf = params.getProperty(FACTOR, Integer.toString(factor));
		factor = Integer.parseInt(buf);
		// make sure its persisted
		params.setProperty(FACTOR, Integer.toString(factor));

		rand = RandGen.getLastCreated();
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
	 * @return the subVec
	 */
	public boolean isSubVec()
	{
		return subVec;
	}
	/*
	 * Mean Absolute Error
	 */
	protected double MAE(Vector<Double> expected, Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			result += Math.abs(expect - pred);
		}
		if (size > 0)
			return ((double) 1 / size) * result;
		else
			return Double.NaN;
	}

	/*
	 * Mean Error
	 */
	protected double ME(Vector<Double> expected, Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			result += (expected.get(i) - predicted.get(i));
		}
		if (size > 0)
			return ((double) 1 / size) * result;
		else
			return Double.NaN;
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
	protected double Objective(Particle p, LinkedList<ArrayList<Double>> data)
	{
		Vector<Double> v = p.getPosition();
		Vector<Double> vec = new Vector<Double>(v);
		double[][] A = new double[numFuncs][funcLen];
		int index = 0;
		double requiredValue;
		double sum = 0;
		SimpleRegression sr = new SimpleRegression();
		double reqSum = 0;
		double r2 = 0;
		long count = 0;
		double biggest = 0;
		Vector<Double> expected = new Vector<Double>();
		Vector<Double> predicted = new Vector<Double>();

		functions = new HashMap<Integer, Vector<Double>>();
		mResult = 0;
		try
		{
			double total = 0;
			for (int i = 0; i < numFuncs; i++)
			{
				for (int j = 0; j < funcLen; j++)
				{
					A[i][j] = vec.get(index++);
				}
				total += A[i][0];
			}

			// Scale the probabilities to cumulative values
			sum = 0;
//			total = 0;
//			for (int i = 0; i < numFuncs; i++)
//				total += A[i][0];
			for (int i = 0; i < numFuncs; i++)
			{
				sum += A[i][0];
				A[i][0] = scaleRange(sum, 0, total, 0, 1);

			}
			ArrayList<Double> predictedRow = new ArrayList<Double>();
			Vector<Double> selFunction = new Vector<Double>();

			LinkedList<ArrayList<Double>> predictedData = new LinkedList<ArrayList<Double>>();

			ArrayList<Double> expectedRow;
			for (int i = 0; i < noTrials; i++)
			{
				predictedData.clear();
				sum = 0;
				reqSum = 0;
				predictedRow = skip(A, skipFirst);
				sr.clear();
				expected.clear();
				predicted.clear();
				for (Iterator<ArrayList<Double>> iterator = data
						.iterator(); iterator.hasNext();)
				{
					expectedRow = iterator.next();

					selFunction = pickFunction(A);
					predictedRow = calc(selFunction, predictedRow);
					predictedData.add(predictedRow);
					for (int j = 0; j < expectedRow.size(); j++)
					{
						requiredValue = expectedRow.get(j);
						expected.add(requiredValue);
						reqSum += requiredValue;
						double predictedValue = predictedRow.get(j);
						predicted.add(predictedValue);
						sum += Math.sqrt(
								Math.pow((requiredValue - predictedValue), 2));
						sr.addData(requiredValue, predictedValue);
					}

				}
//				mResult += sum;
			}
			matrix = A; // not required, but leave for now
			p.setFuncSpecific(functionsToXML(A), true);
			for (int i = 0; i < data.size(); i++)
			{
				p.setFuncSpecific("<Output><Predicted>"
						+ predictedData.get(i).toString()
						+ "</Predicted><RequiredValue>" + data.get(i).toString()
						+ "</RequiredValue></Output>", false);

			}
			double hausdorffDist = hausdorff.hausdorffDist(data, predictedData);
			mResult = Math.sqrt(Math.pow(hausdorffDist, 2));
			p.setFuncSpecific("<HausdorffDistance>" + hausdorffDist
					+ "</HausdorffDistance><Sum>" + sum + "</Sum><ReqSum>"
					+ reqSum + "</ReqSum><AvgSum>" + (sum / count)
					+ "</AvgSum><AvgRequiredSum>" + (reqSum / count)
					+ "</AvgRequiredSum><AME description = \"Absolute Maximum Error\">"
					+ biggest
					+ "</AME><MAE description = \"Mean Absolute Error\">"
					+ MAE(expected, predicted)
					+ "</MAE><ME description = \"Mean Error\">"
					+ ME(expected, predicted)
					+ "</ME><RAE description = \"Relative Absolute Error\">"
					+ RAE(expected, predicted)
					+ "</RAE><RMSE description = \"Root Mean Squared Error\">"
					+ RMSE(expected, predicted)
					+ "</RMSE><R2 description = \"R Squared\">" + r2
					+ "</R2><Result>" + mResult + "</Result>", false);

		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

		return super.Objective(p);
	}

	protected Vector<Double> oldcalc(Vector<Double> v, Vector<Double> input)
			throws Exception
	{
		Vector<Double> res = new Vector<Double>();
		Vector<Double> vec = new Vector<Double>(v);
		Vector<Double> subV = new Vector<Double>();
		double A[][];
		double B[][];
		double C[][];
		int sizeA = (vec.size() - inputs) / factor;
		A = new double[factor][sizeA];

		int index = 0;
		int sizeInput = input.size();
		if (sizeInput != sizeA)
			throw new Exception("Vector sizes don't match for multiplication");
		int dim = (int) Math.ceil(sizeInput / sizeA);
		// B = new double[factor][dim];
		B = new double[sizeA][dim];
		C = new double[factor][dim];
		try
		{
			int reduceBy = inputs;
			while (reduceBy != 0)
			{
				Double lastElement = vec.lastElement();
				subV.add(0, lastElement);
				vec.remove(lastElement);
				reduceBy--;
			}
			for (int j = 0; j < factor; j++)
			{
				for (int i = 0; i < sizeA; i++)
				{
					A[j][i] = vec.get(index++);

				}
			}
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
		int size = subV.size();
		vector = new double[size];
		index = 0;
		for (int j = 0; j < factor; j++)
		{
			for (int i = 0; i < dim; i++)
				res.add(index++, C[j][i]);
		}
		for (int i = 0; i < size; i++)
		{
			double vecEle = subV.get(i);
			res.set(i, res.get(i) - vecEle);
			vector[i] = vecEle;
		}

		return res;
	}
	/**
	 * 
	 */
	private void performTest()
	{
		try
		{
			String funcsstr = params.getProperty(NUMBER_OF_FUNCTIONS, "auto");
			log2n = false;
			if (Util.isNumeric(funcsstr))
				numFuncs = Integer.parseInt(funcsstr);
			else if (funcsstr.equalsIgnoreCase("log2n"))
				log2n = true;

			else
				numFuncs = (int) (inputs + Math.floor(inputs / 3));
			noTrials = Integer.parseInt(params.getProperty(NUMBER_OF_TRIALS,
					Integer.toString(noTrials)));
			skipFirst = Integer.parseInt(params.getProperty(SKIP_FIRST,
					Integer.toString(skipFirst)));

			String buf = params.getProperty(USE_OFFSET_VECTOR, "true");
			setSubVec(buf.equalsIgnoreCase("true"));
			buf = params.getProperty(USE_POWER_VECTOR, "false");
			setPowVec(buf.equalsIgnoreCase("true"));
			buf = params.getProperty(USE_LOG_VECTOR, "false");
			setLogVec(buf.equalsIgnoreCase("true"));

		} catch (NumberFormatException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
	}
	// very poor; done for backwards compatibility
	protected Vector<Double> pickFunction(double[][] A)
	{
		Vector<Double> selFunction;
		double r = rand.nextDouble();
		int index = -1;
		double total = 0.0;
		do
		{
			index++;
			total += A[index][0];
		} while (r > total && index < numFuncs - 1);

		if (functions.containsKey(index))
			// selFunction.clear();
			selFunction = functions.get(index);
		else
		{
			selFunction = new Vector<Double>();
			for (int j = 1; j < A[index].length; j++)
			{
				selFunction.add(A[index][j]);
			}
			functions.put(index, selFunction);
		}
		return selFunction;
	}
	/**
	 * Relative Absolute Error
	 */
	protected double RAE(Vector<Double> expected, Vector<Double> predicted)
	{
		double sum1 = 0;
		double sum2 = 0;
		int size = expected.size();
		double avg = sum(expected) / (size - 1);
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			sum1 += Math.abs(expect - pred);
			sum2 += Math.abs(expect - avg);
		}
		if (sum1 != 0 && sum2 != 0)
			return (sum1 / sum2);
		else
			return Double.NaN;
	}
	/**
	 * Root mean square error
	 */
	protected double RMSE(Vector<Double> expected, Vector<Double> predicted)
	{
		double result = 0;
		int size = expected.size();
		if (size != predicted.size())
			return Double.NaN;
		for (int i = 0; i < size; i++)
		{
			double expect = (Double) expected.get(i);
			double pred = (Double) predicted.get(i);
			result += Math.pow(expect - pred, 2);
		}
		if (size > 0)
			return Math.sqrt(result / (size - 1));
		else
			return Double.NaN;
	}
	/**
	 * @param value
	 * @param oldMin
	 * @param oldMax
	 * @param newMin
	 * @param newMax
	 * @return scaled value
	 */
	protected double scaleRange(double value, double oldMin, double oldMax,
			double newMin, double newMax)
	{
		return (value / ((oldMax - oldMin) / (newMax - newMin))) + newMin;
	}

	// SSE=sum((y-ypred).^2);
	// SST=sum((y-mean(y)).^2)
	// 1-SSE/SST

	/**
	 * @param logVec
	 *            the logVec to set
	 */
	public void setLogVec(boolean logVec)
	{
		this.logVec = logVec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#setMDimension(int)
	 */
	@Override
	public void setMDimension(int dimension) throws Exception
	{
		// +1 because db.getDim() returns inputs -1 because it assumes that the
		// last column is the value to be predicted
		inputs = dimension + 1;
		if (log2n && db != null)
			numFuncs = (int) Util.log2(db.getRecordCount());
		int add = (subVec) ? inputs : 0;
		add += (powVec) ? inputs : 0;
		add += (logVec) ? inputs : 0;

		// +1 to allow for the probability value assigned to each function
		funcLen = (inputs * factor) + add + 1;
		super.setMDimension(numFuncs * funcLen);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#setParams(java.util.Properties)
	 */
	@Override
	public void setParams(Properties params)
	{
		super.setParams(params);
		performTest();
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
		// assumes this has been set by inheritor before call
//		for (int i = 0; i < mDimension; i++)
//		{
//			mMax[i] = 10;
//			mMin[i] = -10;
//		}

		for (int i = 0; i < numFuncs; i++)
		{
			mMax[i * funcLen] = 1;
			mMin[i * funcLen] = 0;
		}

	}
	/**
	 * @param subVec
	 *            the subVec to set
	 */
	public void setSubVec(boolean subVec)
	{
		this.subVec = subVec;
	}

	/**
	 * @param A
	 * @param skip
	 */
	private ArrayList<Double> skip(double[][] A, int skip)
	{

		ArrayList<Double> predictedRow = new ArrayList<Double>();
		Vector<Double> selFunction = new Vector<Double>();
		try
		{
			for (int i = 0; i < inputs; i++)
				predictedRow.add(i, 1.0);
			for (int i = 0; i < skip; i++)
			{

				selFunction = pickFunction(A);
				predictedRow = calc(selFunction, predictedRow);
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return predictedRow;
	}

	protected double StdDev(Vector<Double> elements)
	{
		double deviation = 0;
		double sum = 0;
		for (Iterator<Double> iterator = elements.iterator(); iterator
				.hasNext();)
		{
			double element = (Double) iterator.next();
			sum += element;
		}

		double average = sum / elements.size();
		sum = 0;
		for (Iterator<Double> iterator = elements.iterator(); iterator
				.hasNext();)
		{
			double element = (Double) iterator.next();
			sum += Math.pow((element - average), 2);

		}

		deviation = Math.sqrt(sum / (elements.size()));

		return deviation;
	}
	/*
	 * Vector sum
	 */
	protected double sum(Vector<Double> x)
	{
		double sum = 0;
		if (x != null)
			for (int i = 0; i < x.size(); i++)
			{
				sum += (double) x.get(i);
			}
		return sum;
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
