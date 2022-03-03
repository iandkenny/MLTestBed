package org.mltestbed.util;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;

/*
 * This calculates the Hausdorff distances between any two sets of points, passed to the class
 * as ArrayList<Vector<Double>>.  
 * 
 *  
 */


public class HausdorffCalculator {
	private double hausdorffDist;
	private double hausGenToInput;
	private double hausInputToGen;

	/*
	 * constructor - accepts two ArrayLists of Vectors of type Double
	 */ 
	public HausdorffCalculator (ArrayList<Vector<Double>> a1,
								ArrayList<Vector<Double>> a2){
		
		// Loop through all points in theInputVertices, and calculate distance to each point in 
		// theSimplifiedVertices.
		// Keep a running variable (initialised to zero) that is the greatest distance yet seen.
		// This calculates directional Hausdorff distance from input to generalised.
			
		hausInputToGen = 0.0;	
		
		for(int i=0; i < a1.size(); i++){
			
			double measure = Double.MAX_VALUE;					
			for(int j=0; j < a2.size(); j++){
				
				double measurePrime = 0.0;
				
				try
				{
					measurePrime = d(a1.get(i), a2.get(j));
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
		
		for(int i=0; i < a2.size(); i++){
			
			double measure = Double.MAX_VALUE;	
			
			for(int j=0; j < a1.size(); j++){
				
				double measurePrime = 0.0;
				
				try
				{
					measurePrime = d(a2.get(i), a1.get(j));
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
		
		
	}


	private double d(Vector<Double> x, Vector<Double> y) throws Exception
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
