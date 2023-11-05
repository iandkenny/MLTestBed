package org.mltestbed.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class CosineSimilarity
{
	public static ArrayList<Double> cosineSimilarity(int rowIndex, double[][] D)
	{
		ArrayList<Double> similarRows = new ArrayList<>();

		for (int row = 0; row < D.length; row++)
		{
			double dotProduct = 0.0, firstNorm = 0.0, secondNorm = 0.0;
			for (int column = 0; column < D[0].length; column++)
			{
				dotProduct += (D[rowIndex][column] * D[row][column]);
				firstNorm += Math.pow(D[rowIndex][column], 2);
				secondNorm += Math.pow(D[row][column], 2);
				// Matrix f = D.getMatrix(row, column);
			}
			double cosinSimilarity = (dotProduct
					/ (Math.sqrt(firstNorm) * Math.sqrt(secondNorm)));
			similarRows.add(row, cosinSimilarity);
		}
		return similarRows;
	}

	public static double vectorCosineSimilarity(Vector<Double> v1,
			Vector<Double> v2) throws Exception
	{
		double sclar = 0, norm1 = 0, norm2 = 0;
		if (v1.size() != v2.size())
			throw new Exception("Vectors must be the same size");
		for (int k = 0; k < v1.size(); k++)
		{
			sclar += v1.get(k) * v2.get(k);
			norm1 += v1.get(k) * v1.get(k);
			norm2 += v2.get(k) * v2.get(k);
		}
		return sclar / Math.sqrt(norm1 * norm2);
	}

	static double textCosineSimilarity(Map<String, Double> v1,
			Map<String, Double> v2)
	{
		Set<String> both = new HashSet<String>(v1.keySet());
		both.retainAll(v2.keySet());
		double sclar = 0, norm1 = 0, norm2 = 0;
		for (String k : both)
			sclar += v1.get(k) * v2.get(k);
		for (String k : v1.keySet())
			norm1 += v1.get(k) * v1.get(k);
		for (String k : v2.keySet())
			norm2 += v2.get(k) * v2.get(k);
		return sclar / Math.sqrt(norm1 * norm2);
	}

}