package org.mltestbed.testFunctions.multiModal;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;

import org.mltestbed.data.readDB.ReadIngber;
import org.mltestbed.testFunctions.PsuedoSVM;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;

public class Ingber extends PsuedoSVM
{

	private static final String PREDICT_DATA = "PredictData";
	private static final String FACTOR = "factor";
	private static ReadIngber ingber;
	private static final String PATIENT_KEY = "patientKey";
	private static final String RESETDB = "resetDB";
	private static final String TRIAL = "trial";
	private boolean bScoring = false;
	private LinkedList<ArrayList<Double>> data;
	private String patientKey = null;
	private HashMap<String, Long> ranges = null;
	private int trial = 0;
	private boolean useBiggest = false;
	private boolean predictData = false;
	

	/**
	 * 
	 */
	public Ingber()
	{
		super();
		description = "Ingber EEG Function";
		info = "Ingber EEG Objective Function: Alcoholic org.mltestbed.data";
		minimised = true;
		// minimised = false;
		createParams();
	}
	@Override
	protected void createParams()
	{
		super.createParams();
		factor = 3;

		params.setProperty(TRIAL, "random");
		// params.setProperty(USE_BIGGEST_DIFF, "false");
		// params.setProperty(RESETDB, "false");
		params.setProperty(FACTOR, Integer.toString(factor));
		params.setProperty(PREDICT_DATA, "true");
		predictData = params.getProperty(PREDICT_DATA).equalsIgnoreCase("true");

		if (predictData)
		{
			if (ingber == null)
				ingber = new ReadIngber();
			ingber.usePredictData();
			setDims();
		} else
			try
			{
				setMDimension(2); // the patient key is not included as a
				// dimension
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		params.setProperty(PATIENT_KEY, "random");
		params.setProperty(RESETDB, "false");

	}
	/**
	 * @param patientKey
	 * @throws Exception
	 */
	private void findKey(String patientKey) throws Exception
	{
		int i = 0;
		do
		{
			patientKey = ingber.selectRandomKey(patientKey);
			i++;
		} while (patientKey == null && i != 10);
		if (patientKey == null)
			throw new Exception("No keys available");
		params.put(PATIENT_KEY, patientKey);
		ingber.writeSelectedKey(patientKey);
		this.patientKey = patientKey;
	}

	/**
	 * @param patientKey
	 * @throws Exception
	 */
	private void findTrial(String patientKey) throws Exception
	{
		int trial;
		int i = 0;
		do
		{
			trial = ingber.selectRandomTrial(patientKey);
			i++;
		} while (patientKey == null && i != 10);
		if (patientKey == null)
			throw new Exception("No keys available");
		params.put(TRIAL, trial);
		ingber.writeSelectedTrial(patientKey, trial);
		this.trial = trial;
	}

	/**
	 * @return the patientKey
	 */
	public String getPatientKey()
	{
		return patientKey;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	// @Override
	// public double Objective(Vector<Double> v)
	// {
	// // MIN_DOUBLE = 1e-18
	// // MAX_DOUBLE = 1e+18
	// // EPS_DOUBLE = 1e-18
	//
	// // *number_parameters = 4
	// // index_v parameter_minimum parameter_maximum parameter_value
	// // parameter_type
	// // 0 -10000 10000 999 -1
	// // 1 -10000 10000 -1007 -1
	// // 2 -10000 10000 1001 -1
	// // 3 -10000 10000 -903 -1
	//
	// // #if ASA_TEMPLATE_SAMPLE
	// // for (index = 0; index < *parameter_dimension; ++index)
	// // parameter_lower_bound[index] = 0;
	// // for (index = 0; index < *parameter_dimension; ++index)
	// // parameter_upper_bound[index] = 2.0;
	// // for (index = 0; index < *parameter_dimension; ++index)
	// // parameter_int_real[index] = REAL_TYPE;
	// // for (index = 0; index < *parameter_dimension; ++index)
	// // cost_parameters[index] = 0.5;
	// // #endif
	//
	// // ingber = new ReadIngber();
	// double q_n, d_i, s_i, t_i, z_i, c_r;
	// long k_i;
	// int i = 0, j;
	//
	// long trial = Math.round(v.get(0));
	// long sample = Math.round(v.get(1));
	// Double score = null;
	// if (!bScoring)
	// score = ingber.getTrialScore(patientKey, trial, sample);
	// else
	// ingber.setScoring(bScoring);
	// if (score != null)
	// {
	// mResult = score;
	// setFuncSpecific("/*" + ingber.getBufSQL() + "*/");
	// } else
	// {
	// try
	// {
	// TreeMap<String, Double> posValue = ingber.getTrialData(
	// patientKey, trial, sample);
	// setFuncSpecific("/*" + ingber.getBufSQL().replaceAll("'", "''")
	// + "*/");
	// s_i = 0.2;
	// t_i = 0.05;
	// c_r = 0.15;
	// q_n = 0.0;
	// Set<String> keys = posValue.keySet();
	// for (Iterator<String> iterator = keys.iterator(); iterator
	// .hasNext(); i++)
	// {
	// String key = (String) iterator.next();
	//
	// // if (fabs (parameter_upper_bound[i] -
	// // parameter_lower_bound[i]) <
	// // (double) EPS_DOUBLE)
	// // continue;
	//
	// j = i % 4;
	// switch (j)
	// {
	// case 0 :
	// d_i = 1.0;
	// break;
	// case 1 :
	// d_i = 1000.0;
	// break;
	// case 2 :
	// d_i = 10.0;
	// break;
	// default :
	// d_i = 100.0;
	// }
	// double x = (Double) posValue.get(key);
	// if (x > 0.0)
	// {
	// k_i = (long) (x / s_i + 0.5);
	// } else if (x < 0.0)
	// {
	// k_i = (long) (x / s_i - 0.5);
	// } else
	// {
	// k_i = 0;
	// }
	//
	// if (Math.abs(k_i * s_i - x) < t_i)
	// {
	// if (k_i < 0)
	// {
	// z_i = k_i * s_i + t_i;
	// } else if (k_i > 0)
	// {
	// z_i = k_i * s_i - t_i;
	// } else
	// {
	// z_i = 0.0;
	// }
	// q_n += c_r * d_i * z_i * z_i;
	// } else
	// {
	// q_n += d_i * x * x;
	// }
	// }
	// mResult = q_n;
	// ingber.updateScore(patientKey, trial, sample, mResult);
	//
	// } catch (Exception e)
	// {
	// Log.log(Level.SEVERE, e);
	// e.printStackTrace();
	// }
	// }
	// return super.Objective(v);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.TestBase#Objective(java.util.Vector)
	 */
	// @Override
	// public double Objective(Vector<Double> v)
	// {
	// /*
	// * My Scoring function for Ingber
	// */
	// double min = Double.MAX_VALUE;
	// double max = Double.MIN_VALUE;
	// double result = 0;
	// float weights[] = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	// int i = 0;
	//
	//
	// long trial = Math.round(v.get(0));
	// long sample = Math.round(v.get(1));
	// Double score = null;
	//		
	// ingber.setScoring(bScoring);
	// if (!bScoring)
	// score = ingber.getTrialScore(patientKey, trial, sample);
	//		
	// if (score != null)
	// {
	// mResult = score;
	// setFuncSpecific("/*" + ingber.getBufSQL() + "*/");
	// } else
	// {
	// try
	// {
	// TreeMap<String, Double> posValue = ingber.getTrialData(
	// patientKey, trial, sample);
	// setFuncSpecific("/*" + ingber.getBufSQL().replaceAll("'", "''")
	// + "*/");
	//				
	// result = 0.0;
	// String lastKey = "";
	// Set<String> keys = posValue.keySet();
	// for (Iterator<String> iterator = keys.iterator(); iterator
	// .hasNext(); i++)
	// {
	//					
	// String key = (String) iterator.next();
	// if(lastKey.equals(""))
	// lastKey = key;
	// double value = posValue.get(key);
	// if(key.startsWith(lastKey.substring(0,lastKey.length()-1)))
	// {
	// if(value<min)
	// min = value;
	// if(value>max)
	// max= value;
	// }
	// else
	// {
	// result += (max-min)* weights[i];
	// min = max = value;
	// lastKey = key;
	// i++;
	// }
	// }
	// mResult = result;
	// ingber.updateScore(patientKey, trial, sample, mResult);
	//
	// } catch (Exception e)
	// {
	// Log.log(Level.SEVERE, e);
	// e.printStackTrace();
	// }
	// }
	//	
	// return super.Objective(v);
	// }
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#init()
	 */
	@Override
	public void init()
	{
		super.init();
		try
		{
			initProps();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
	/**
	 * @throws Exception
	 */
	protected void initProps() throws Exception
	{
		String key = params.getProperty(RESETDB);
		if (key.equalsIgnoreCase("true"))
			ingber.deleteSelected();
		key = params.getProperty(PREDICT_DATA);
		if (key.equalsIgnoreCase("true"))
		{
			predictData = true;
			ingber.usePredictData();
		}
		patientKey = params.getProperty(PATIENT_KEY);
		if (patientKey.equalsIgnoreCase("a")
				|| patientKey.equalsIgnoreCase("c"))
		{
			findKey(patientKey);

		} else if (patientKey.equalsIgnoreCase("random"))
		{
			findKey(null);
		}
		key = params.getProperty(TRIAL);
		if (key.equalsIgnoreCase("random"))
		{
			findTrial(patientKey);
		}
		//	
		// HashMap<String, Long> bufProp = null;
		// while (bufProp==null)
		// try
		// {
		// bufProp = ingber.getRange(key);
		// } catch (SQLException e)
		// {
		// Log.log(Level.SEVERE, new
		// Exception("Failed to retrieve range from database");
		// e.printStackTrace();
		// }
		// String buf = params.getProperty(USE_BIGGEST_DIFF);
		// useBiggest = (buf.equalsIgnoreCase("true")) ? true : false;

		// buf = params.getProperty(RESETDB);
		// if (buf.equalsIgnoreCase("true"))
		// db.deleteSelected();
		String buf = params.getProperty(FACTOR, Integer.toString(factor));
		factor = Integer.parseInt(buf);
		factor = (factor <= 0) ? factorMin : factor;
		params.setProperty(FACTOR, Integer.toString(factor));

		ingber.setProp(params);
		if (!bScoring && !predictData)
			setRange();
		HashMap<String, String> keys = new HashMap<String, String>();
		keys.put("id", patientKey);
		keys.put("trial", Integer.toString(trial));
		data = ingber.getData(keys);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.SVM#Objective(java.util.Vector)
	 */
	@Override
	public double Objective(Particle p)
	{
		double reqSum = 0;
		double sum = 0;
		long count = 0;
		double biggest = 0;
		appendLocalBuffer(null,true);
		for (int i = 1; i < data.size(); i++)
		{
			ArrayList<Double> row = data.get(i - 1);
			ArrayList<Double> next = data.get(i);
			double score = super.IngberObjective(p, row, next);
			double abs = Math.abs(score);
			if (abs > biggest)
				biggest = abs;
			// sum += abs;
			sum += score;
			count++;
			appendLocalBuffer(p.getFuncSpecific(),false);

		}
		if (useBiggest)
		{
			mResult = biggest;
		} else
		{
			// mResult = (reqSum / count) - (sum / count);
			// *** RMSE
			// mResult = Math.sqrt(Math.pow((reqSum / count) - (sum /
			// count),
			// 2));
			// mResult = Math.sqrt(Math.pow(reqSum - sum, 2));
			// *** Absolute Diff
			// mResult = Math.abs((reqSum / count) - (sum / count));
			// mResult = Math.abs(reqSum - sum);
			// *** Just sum
			mResult = sum / count;
			// RSME
			// mResult = sum / count;
		}
				
		p.setFuncSpecific("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"ingberresults.xsl\"?><IterResult><Key>"
				+ patientKey
				+ "</Key><Trial>"
				+ trial
				+ "</Trial>"
				+ toXML(),true);
		transBuf(p);
		p.setFuncSpecific("<Sum>"
				+ sum
				+ "</Sum><ReqSum>"
				+ reqSum
				+ "</ReqSum><AvgSum>"
				+ (sum / count)
				+ "</AvgSum><AvgRequiredSum>"
				+ (reqSum / count)
				+ "</AvgRequiredSum><AME description = \"Absolute Maximum Error\">"
				+ biggest + "</AME><Result>" + mResult
				+ "</Result></IterResult>",false
				);
		return super.Objective(p);
	}

	/**
	 * 
	 */
	private void setDims()
	{
		try
		{
			if (ingber == null)
				ingber = new ReadIngber();
			int dim = ingber.getDim() + 1;
			dim = (dim * factor);

			// dim forced inside function
			setMDimension(dim);

		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
	}
	/**
	 * @param patientKey
	 *            the patientKey to set
	 */
	public void setPatientKey(String patientKey)
	{
		this.patientKey = patientKey;
		params.setProperty(PATIENT_KEY, patientKey);
	}

	@Override
	protected void setRange()
	{
		if (predictData)
		{
			mMin = new double[mDimension];
			mMax = new double[mDimension];
			for (int i = 0; i < mDimension; i++)
			{
				mMin[i] = -10;
				mMax[i] = 10;
			}
		} else if (ingber != null && !patientKey.equalsIgnoreCase("a")
				&& !patientKey.equalsIgnoreCase("c"))
		{

			mMin = new double[mDimension];
			mMax = new double[mDimension];
			while (ranges == null)
				try
				{
					ranges = ingber.getRange(patientKey);
					if (ranges != null)
					{
						mMin[0] = ranges.get("mintrial");
						mMax[0] = ranges.get("maxtrial");
						mMin[1] = ranges.get("minsample");
						mMax[1] = ranges.get("maxsample");
					}
				} catch (SQLException e)
				{
					Log.log(Level.SEVERE, new Exception(
							"Failed to retrieve range from database", e));
					e.printStackTrace();
				}
		}
	}

	/**
	 * @param scoring
	 *            the bScoring to set
	 */
	public void setScoring(boolean scoring)
	{
		bScoring = scoring;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mltestbed.testFunctions.PsuedoSVM#setMDimension(int)
	 */
	@Override
	public void setMDimension(int dimension) throws Exception
	{
		int add = (isSubVec()) ? dimension / factor : 0;

		dimension += add;
		if (dimension < 1)
			throw new Exception("Dimensions must be >0");
		else
		{
			mDimension = dimension;
			params.setProperty("dimensions", Integer.toString(dimension));
			mMax = new double[mDimension];
			mMin = new double[mDimension];
			setRange();
		}
	}
	public double Objective(Vector<Double> v)
	{
		Particle p = new Particle(getParams(), this, new Random(), -1);
		return Objective(p);
	}
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
}
