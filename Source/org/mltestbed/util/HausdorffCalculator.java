package org.mltestbed.util;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;

/*
 * This calculates the Hausdorff distances between any two sets of points, passed to the class
 * as ArrayList<ArrayList<Double>>.  
 * 
 *  
 */


public class HausdorffCalculator {
	private double hausdorffDist;
	private double hausGenToInput;
	private double hausInputToGen;


	/*
	 * constructor - default
	 */ 
	public HausdorffCalculator ()		
	{		
	}

	/*
	 * constructor - accepts two ArrayLists of ArrayLists of type Double
	 */ 
	public HausdorffCalculator (LinkedList<ArrayList<Double>> a1,
								LinkedList<ArrayList<Double>> a2){
		
		hausdorffDist(a1, a2);
		
		
	}


	/**
	 * @param data
	 * @param predictedData
	 * @return 
	 */
	public double  hausdorffDist(LinkedList<ArrayList<Double>> data,
			LinkedList<ArrayList<Double>> predictedData)
	{
		// Loop through all points in theInputVertices, and calculate distance to each point in 
		// theSimplifiedVertices.
		// Keep a running variable (initialised to zero) that is the greatest distance yet seen.
		// This calculates directional Hausdorff distance from input to generalised.
			
		hausInputToGen = 0.0;	
		
		for(int i=0; i < data.size(); i++){
			
			double measure = Double.MAX_VALUE;					
			for(int j=0; j < predictedData.size(); j++){
				
				double measurePrime = 0.0;
				
				try
				{
					measurePrime = d(data.get(i), predictedData.get(j));
				} catch (Exception e)
				{
					Log.getLogger().log(Level.SEVERE, e.getMessage());
					e.printStackTrace();
				}
				
				if(measurePrime <= measure){
					measure = measurePrime;	
				}

			}
			
			if(measure >= hausInputToGen){
				hausInputToGen = measure;
			}
			
		}
		
		// loop through all points in generalized line, and calculate distance to each point in original line
		// ... as per loop above
		// This calculates directional Hausdorff distance from generalized line to input line
		
		hausGenToInput = 0.0;
		
		for(int i=0; i < predictedData.size(); i++){
			
			double measure = Double.MAX_VALUE;	
			
			for(int j=0; j < data.size(); j++){
				
				double measurePrime = 0.0;
				
				try
				{
					measurePrime = d(predictedData.get(i), data.get(j));
				} catch (Exception e)
				{
					Log.getLogger().log(Level.SEVERE, e.getMessage());
					e.printStackTrace();
				}
				
				if(measurePrime <= measure){
					measure = measurePrime;
				}
				
			}
			
			if(measure >= hausGenToInput){
				hausGenToInput = measure;
			}
			
		}
		
		// Compare two directional Hausdorff distances and take the greatest for absolute
		hausdorffDist = 0.0;
		hausdorffDist = Math.max(hausInputToGen, hausGenToInput);
		
		
		// report 
		Log.getLogger().info("\n~~ Hausdorff Report ~~~~~~~~~~~~~~~\n");
		Log.getLogger().info("h(v1 to v2) = " + roundTwoDecimals(hausInputToGen) + " m \n");
		Log.getLogger().info("h(v2 to v1) = " + roundTwoDecimals(hausGenToInput) + " m \n");
		Log.getLogger().info("* H(v1, v2) = " + roundTwoDecimals(hausdorffDist) + " m \n");
		Log.getLogger().info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
		return hausdorffDist;
	}


	private double d(ArrayList<Double> x, ArrayList<Double> y) throws Exception
	{
		if (x.size() != y.size())
			throw new Exception("Vectors are not the same size");
		double distance = 0;
		double sum = 0;
		for (int i = 0; i < y.size(); i++)
		{
			double sqr = (x.get(i) - y.get(i)) * (x.get(i) - y.get(i));
			sum += sqr;
		}
		distance = Math.sqrt(sum);
		return distance;
	}

	
	/**
	 * @return the hausdorffDist
	 */
	public double getHausdorffDist()
	{
		return hausdorffDist;
	}


	/**
	 * @return the hausGenToInput
	 */
	public double getHausGenToInput()
	{
		return hausGenToInput;
	}


	/**
	 * @return the hausInputToGen
	 */
	public double getHausInputToGen()
	{
		return hausInputToGen;
	}
	
	double roundTwoDecimals(double d) {
		// some borrowed code for rounding two decimals
    	DecimalFormat twoDForm = new DecimalFormat("#.##");
    	return Double.valueOf(twoDForm.format(d));
	}
	
}
