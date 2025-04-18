package org.mltestbed.util.EMD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.mltestbed.data.readDB.ReadMinas;
import org.mltestbed.util.Util;

public class Emd
{

	public class EmdData
	{
		protected double[][] imfs;
		protected int iterations, order, locality;
		protected double[] min, max, residue;
		protected int[] minPoints, maxPoints;
		protected int size, minSize, maxSize;
	}
	private static Emd emd;

	private static int inputs;

	private static EmdData emdData;

	private static final String linefeed = System.getProperty("line.separator");
	private static final String EMD_XML_FILE_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><?xml-stylesheet type=\"text/xsl\" href=\"emd.xsl\"?>";
	private static final String MINAS_PASSAGE_EMD_XMl = "MinasPassageEMD.xml";
	
	private static final String MINAS_PASSAGE_EMD_CSV = "MinasPassageEMD.csv";
	private static final String MINAS_PASSAGE_EMD_FOR_STATISTICS_CSV = "MinasPassageEMDForStatistics.csv";

	public static void minasPassage()
	{
		/**
		 * This code implements empirical mode decomposition in Java, adapted
		 * from the original C++. Required parameters include: - order: the
		 * number of IMFs to return - iterations: the number of iterations per
		 * IMF - locality: in samples, the nearest two extrema may be If it is
		 * not specified, there is no limit (locality = 0).
		 * 
		 * Typical use consists of calling emdCreate(), followed by
		 * emdDecompose(), and then using the struct's "imfs" field to retrieve
		 * the baseData. Call emdClear() to deallocate memory inside the class.
		 */

		ArrayList<Double> data = new ArrayList<Double>();
		ReadMinas db = new ReadMinas();
		double[] array = null;
		StringBuilder sb = new StringBuilder();

		db.useMinasT1(); // first 2 months 2015
		db.useMinasT2(); // complete 2015
//		db.useMinasT3(); // first 6 months 2015
		LinkedList<ArrayList<Double>> dbdata = db.getData(null);;
		emd = new Emd();
		ArrayList<String> names = db.getColumnNames();
		inputs = names.size();
		int order = names.size() + (int) Math.ceil(names.size() / 3) + 1; // +1
																			// to
																			// allow
																			// for
																			// residual
//		int order = (int) Math.ceil(Util.log2(dbdata.size()));
		int k = 0;
		int cols = 0;
		for (Iterator<ArrayList<Double>> iterator = dbdata.iterator(); iterator
				.hasNext();)
		{
			data = (ArrayList<Double>) iterator.next();
			cols = data.size();
			if (array == null)
				array = new double[cols];
			else
				array = Arrays.copyOf(array, array.length + cols);
			for (int i = 0; i < cols; i++)
				array[k++] = data.get(i);
		}
		emdData = emd.processEMD(order, array);
		k = 0;
		sb = new StringBuilder();
		for (int l = 0; l < cols; l++)
			sb.append(names.get(l) + ((l != cols - 1) ? "," : linefeed));
		for (int i = 0; i < array.length; i++)
		{
			if (k == 0)
				sb.append("[");
			sb.append(array[i] + ":");
			for (int j = 0; j < order; j++)
				sb.append(emdData.imfs[j][i] + ((j == order - 1) ? "]" : ";"));
			if (k == cols - 1)
				sb.append(linefeed);
			else
				sb.append(",");
			k = (k + 1) % cols;
		}

		File file = new File(MINAS_PASSAGE_EMD_CSV);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			writer.append(sb);
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = new StringBuilder();
		ArrayList<Vector<Double>> vi = emd.getValuedimfs();
		for (int l = 0; l < names.size(); l++)
		{
			sb.append(names.get(l) + ",");
			for (int i = 0; i < order; i++) // need to include residual
				sb.append(((i == order - 1) ? "IMF Res" : "IMF" + (i + 1)) + " "
						+ names.get(l)
						+ ((i == order - 1 && l == names.size() - 1)
								? linefeed
								: ","));
		}

		k = 0;
		for (Iterator<Vector<Double>> iterator2 = vi.iterator(); iterator2
				.hasNext();)
		{
			Vector<Double> vector = (Vector<Double>) iterator2.next();
			for (int i = 0; i < vector.size(); i++)
				sb.append(vector.get(i) + ((i == order && k == names.size() - 1)
						? linefeed
						: ","));
			k = (k + 1) % names.size();
		}

		for (int i = 0; i < cols; i++)
		{
			ArrayList<Vector<Double>> tmpListIMFs = emd.getOrderedIMFsBySeries()
					.get(i);
			double[][] tmpIMFs = new double[tmpListIMFs.size()][tmpListIMFs
					.get(0).size()];
			for (int m = 0; m < tmpListIMFs.size(); m++)
			{

				double[] arr = tmpListIMFs.get(m).stream()
						.mapToDouble(Double::doubleValue).toArray().clone();
				tmpIMFs[m] = arr;
			}

			double[] meanf = (new Tdic()).meanf(tmpIMFs, 1);
			k = 0;
			for (int l = 0; l < meanf.length; l++)
			{
				if (k == 0)
					sb.append(names.get(i) + ",");
				sb.append(meanf[l] + ((i == cols - 1 && l == meanf.length - 1)
						? linefeed
						: ","));
				k = (k + 1) % order;
			}
		}
		file = new File(MINAS_PASSAGE_EMD_FOR_STATISTICS_CSV);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			writer.append(sb);
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sb = null;
		System.out.println("Completed CSV file write");

		String tdicstr = processTDIC(dbdata, names, cols);
		String str = "<emd>" + tdicstr + "</emd>";
		file = new File(MINAS_PASSAGE_EMD_XMl);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			writer.append(EMD_XML_FILE_HEADER+linefeed);
			writer.append(str);
			writer.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Completed: XML file write");

		System.out.println("All Completed");
	}

	/**
	 * @return the orderedIMFsBySeries
	 */
	public ArrayList<ArrayList<Vector<Double>>> getOrderedIMFsBySeries()
	{
		return orderedIMFsBySeries;
	}

	/**
	 * @param dbdata
	 * @param names
	 * @param cols
	 * @return tdic output as xml string 
	 */
	public static String processTDIC(
			LinkedList<ArrayList<Double>> dbdata, ArrayList<String> names,
			int cols)
	{
		StringBuilder tdicsb = new StringBuilder();
		Tdic tdic = new Tdic();
		tdicsb.append("<tdic>");
		for (int j = 1; j < cols; j++)
		{
//			double[][] =emdData.imfs[j][i];
//			tdic.tdic(x, y, ifz, ntime, nct, it);
			int size = dbdata.size();
			double[][] imf1 = new double[1][emdData.imfs[j-1].length];
			imf1[0] =emdData.imfs[j-1];
			double[][] imf2 = new double[1][emdData.imfs[j].length];
			imf2[0] =emdData.imfs[j];
			double[] pp = tdic.maxlocalperiod(imf1, imf2);
			tdicsb.append("<comparison dataseries1=\"" + names.get(j - 1)
					+ "\" dataseries2=\"" + names.get(j) + "\">");
			double[] series1=new double[dbdata.size()];
			double[] series2=new double[dbdata.size()];
			for (int i = 0; i < dbdata.size(); i++)
			{
				series1[i] =dbdata.get(i).get(j-1);
				series2[i] =dbdata.get(i).get(j);
			}
			tdic.tdic(series1, series2, pp, null, null, null);
			tdicsb.append(tdic.toXML("c", tdic.getCorrelationMatrix())
					+ linefeed);
			tdicsb.append(
					tdic.toXML("p", tdic.getStudentTest()) + linefeed);
			tdicsb.append(tdic.toXML("tx", tdic.getTimeAxis()) + linefeed);
			tdicsb.append(
					tdic.toXML("scale", tdic.getScaleAxis()) + linefeed);
			tdicsb.append("</comparison>");

		}
		tdicsb.append("</tdic>");
		return tdicsb.toString();
	}

	public static void main(String[] args)
	{
		if (false)
			test();
		else
			minasPassage();

	}
	public static void test()
	{
		/*
		 * This code implements empirical mode decomposition in C. Required
		 * paramters include: - order: the number of IMFs to return -
		 * iterations: the number of iterations per IMF - locality: in samples,
		 * the nearest two extrema may be If it is not specified, there is no
		 * limit (locality = 0).
		 * 
		 * Typical use consists of calling emdCreate(), followed by
		 * emdDecompose(), and then using the struct's "imfs" field to retrieve
		 * the baseData. Call emdClear() to deallocate memory inside the struct.
		 */
		double[] data = new double[]
		{229.49, 231.94, 232.97, 234, 233.36, 235.15, 235.64, 235.78, 238.95,
				242.09, 240.61, 240.29, 237.88, 252.11, 259.16, 263.4, 262.1,
				254.85, 254.42, 261.27, 253.92, 259.04, 251.58, 248.96, 239.49,
				229.39, 247.02, 249.48, 254.9, 251.27, 246.85, 245.43, 241.52,
				231.23, 235.67, 239.99, 238.49, 237.41, 246.4, 249.83, 253.67,
				256.71, 255.9, 248.93, 244.05, 242.49, 236.52, 243.63, 246.55,
				247.3, 252.56, 259.91, 264.41, 266.55, 262.75, 266.33, 263.53,
				261.62, 259.38, 260.94, 249.14, 244.63, 241.66, 240.16, 241.81,
				251.57, 251.01, 252.49, 250.23, 244.89, 245.79, 244.55, 243.04,
				238.84, 244.98, 247.26, 251.91, 252.81, 252.16, 256.83, 253.8,
				251.03, 250.19, 254.66, 254.74, 255.76, 254.52, 252.95, 254.57,
				252.29, 243.32, 244.88, 242.26, 240.84, 245.05, 246.12, 243.02,
				242.79, 239.05, 233.34, 236.22, 233.69, 234.99, 235.84, 236.43,
				243.46, 245.25, 251.67, 250.73, 255.7, 255.85, 256.18, 259.71,
				260.7, 262.8, 268.98, 267.81, 275.46, 275.98, 279.85, 280.99,
				284.3, 283.17, 278.99, 279.48, 275.96, 274.77, 270.99, 281.01,
				281.25, 281.28, 286, 287.25, 290.35, 291.9, 294.01, 306.1,
				309.27, 301, 302.01, 301.02, 299.03, 300.36, 299.59, 299.38,
				296.86, 292.72, 295.83, 300.87, 304.21, 309.53, 308.43, 309.87,
				307.4, 309.3, 307.96, 299.58, 298.61, 293.31, 292.25, 299.96,
				298.31, 304.76, 300.26, 306.16, 306.35, 308.17, 302.61, 307.72,
				309.42, 308.73, 311.36, 309.48, 312.2, 310.98, 311.76, 312.84,
				311.5, 311.57, 312.43, 311.81, 313.37, 315.3, 316.24, 314.72,
				315.77, 316.54, 316.36, 314.78, 313.71, 320.52, 322.2, 324.83,
				324.57, 326.89, 333.05, 332.26, 334.97, 336.19, 338.92, 331.3,
				329.54, 323.55, 317.75, 328.19, 332.03, 334.41, 333.79, 326.88,
				330.01, 335.56, 334.87, 334.01, 336.99, 342.22, 345.45, 348.33,
				344.81, 347.06, 349.32, 350.02, 353.16, 348.47, 340.94, 329.32,
				333.22, 333.47, 338.6, 343.52, 339.72, 342.46, 349.69, 350.12,
				345.61, 346, 342.8, 337.15, 342.33, 343.86, 335.95, 320.95,
				325.46, 321.59, 329.99, 331.84, 329.88, 335.5, 341.89, 340.82,
				341.33, 339.06, 338.94, 335.1, 331.83, 329.59, 328.76, 328.8,
				325.86, 321.72, 323.28, 326.9, 323.3, 318.47, 322.74, 328.59,
				333.01, 341.07, 343.32, 340.8, 340.54, 337.23, 340.52, 336.78,
				338.64, 339.98, 337.23, 337.15, 338.06, 339.86, 337.7, 337.06,
				331.15, 324.15, 326.91, 330.54, 331.18, 326.02, 325.22, 323.07,
				327.54, 325.81, 328.15, 338.28, 336.03, 336.6, 334.01, 328.76,
				322.93, 323.12, 322.39, 316.96, 317.64, 323.32, 317.78, 316.24,
				311.47, 306.67, 316.37, 313.76, 322.14, 317.39, 322.93, 326.06,
				324.87, 326.46, 333.84, 339.84, 342.11, 347.4, 349.84, 344.28,
				344.04, 348.19, 347.95, 354.9, 363.54, 366.51, 376.28, 376.66,
				382.51, 387.56, 392.34, 381.81, 381.07, 379.76, 385.86, 378.24,
				381.8, 367.01, 363.37, 343.52, 363.74, 353.71, 363.44, 366.64,
				372.89, 370.04, 370, 356, 346.26, 346.66, 363.35, 365.85,
				363.46, 373.05, 379.27, 379.29, 374.27, 370.57, 363.78, 369.32,
				373.39, 373.6, 367.12, 369.51, 374.06, 378.61, 382.17, 389.51,
				400.33, 402.1, 400.83, 390.79, 393.2, 392.1, 388.3, 386.11,
				379.85, 370.85, 364.32, 362.28, 367.87, 367.01, 359.65, 378.14,
				389.3, 391.15, 397.22, 410.42, 408.46, 410.65, 387.68, 384.46,
				382.09, 394.63, 386.85, 389.6, 393.58, 393.84, 393.67, 385.63,
				386.5, 392.01, 389.25, 388.76, 395.08, 384.43, 374.65, 374.06,
				368.85, 378.16, 374.21, 367.05, 364.65, 358.88, 366.18, 356.92,
				353.59, 365.8, 362.96, 371.71, 377.28, 379, 382.22, 380.22,
				378.41, 379.94, 382.82, 381.09, 378.14, 369.75, 368.54, 370.56,
				371.72, 385.08, 385.57, 387.61, 392.26, 395.37, 391.59, 394,
				393.88, 399.94, 402.09, 406.56, 410.81, 410.15, 411.62, 410.95,
				409.82, 408.29, 413.04, 417.33, 416.01, 408.76, 415.68, 408.87,
				434.4, 432.43, 435, 440.58, 443.95, 443.67, 442.63, 447.06,
				451.24, 455.96, 463.6, 479.63, 479.88, 488.81, 495.48, 484.01,
				488.43, 488.34, 500.72, 498.96, 502.22, 508.07, 511.33, 520.71,
				527.55, 529.53, 530.22, 518.53, 515.71, 516.12, 527.11, 530.21,
				536.85, 552.51, 573.4, 569.49, 569.5, 584.6, 589.33, 585.96,
				582.89, 579.69, 590.32, 597.61, 600.67, 593.12, 583.09, 601.65,
				612.05, 607.17, 616.29, 618.77, 611.19, 609.01, 605.68, 588.62,
				564.21, 592.97, 591.64, 571.32, 557.25, 556.01, 544.9, 593.26,
				591.02, 586.45, 567.95, 566.15, 569.9, 565.85, 549.74, 553.85,
				552.59, 553.56, 554.86, 551.16, 542.9, 537.99, 531.09, 515.57,
				515.82, 545.87, 541.68, 554.9, 549.8, 546.86, 556.56, 563.27,
				561.87, 545.59, 548.8, 547.38, 555.78, 556.03, 564.39, 555.49,
				560.35, 556.46, 555.84, 558.37, 569.7, 571.29, 569.66, 561.81,
				566.12, 555.1, 556.33, 558.73, 553.43, 567.97, 576.26, 582.96,
				593.2, 589.25, 597.04, 591.52, 587.84, 582.46, 588.37, 590.25,
				590.28, 589.62, 597.46, 587.71, 587.26, 584.43, 559.19, 559.1,
				569.1};
		emd = new Emd();
		int order = 4;
		EmdData emdData = emd.processEMD(order, data);
		for (int i = 0; i < data.length; i++)
		{
			System.out.print(data[i] + ":[");
			for (int j = 0; j < order; j++)
				System.out.print(emdData.imfs[j][i] + ";");
			System.out.println("]");
		}
	}

	private ArrayList<Vector<Double>> valuedimfs = new ArrayList<Vector<Double>>();
	private ArrayList<ArrayList<Vector<Double>>> orderedIMFsBySeries = new ArrayList<ArrayList<Vector<Double>>>();

	private void emdClear(EmdData emd)
	{
		emd.iterations = 0;
		emd.order = 0;
		emd.locality = 0;
		emd.size = 0;
		emd.imfs = null;
		emd.residue = null;
		emd.minPoints = null;
		emd.maxPoints = null;
		emd.min = null;
		emd.max = null;

	}
	private void emdCreate(EmdData emd, int size, int order, int iterations,
			int locality)
	{
		emdSetup(emd, order, iterations, locality);
		emdResize(emd, size);
	}

	private void emdDecompose(EmdData emd, double[] signal)
	{
		int i, j;
		System.arraycopy(signal, 0, emd.imfs[0], 0, emd.size); // memcpy(emd->imfs[0],
																// signal,
																// emd->size *
																// sizeof(double));
		System.arraycopy(signal, 0, emd.residue, 0, emd.size); // memcpy(emd->residue,
																// signal,
																// emd->size *
																// sizeof(double));

		for (i = 0; i < emd.order - 1; i++)
		{
			double[] curImf = emd.imfs[i]; // double* curImf = emd->imfs[i];
			for (j = 0; j < emd.iterations; j++)
			{
				emdMakeExtrema(emd, curImf);

				if (emd.minSize < 4 || emd.maxSize < 4)
					break; // can't fit splines

				emdInterpolate(emd, curImf, emd.min, emd.minPoints,
						emd.minSize);
				emdInterpolate(emd, curImf, emd.max, emd.maxPoints,
						emd.maxSize);
				emdUpdateImf(emd, curImf);
			}

			emdMakeResidue(emd, curImf);
			System.arraycopy(emd.residue, 0, emd.imfs[i + 1], 0, emd.size); // memcpy(emd->imfs[i
																			// +
																			// 1],
																			// emd->residue,
																			// emd->size
																			// *
																			// sizeof(double));
		}
	}

	// Currently, extrema within (locality) of the boundaries are not allowed.
	// A better algorithm might be to collect all the extrema, and then assume
	// that extrema near the boundaries are valid, working toward the center.

	private void emdInterpolate(EmdData emd, double[] in, double[] out,
			int[] points, int pointsSize)
	{
		int size = emd.size;
		int i, j, i0, i1, i2, i3, start, end;
		double a0, a1, a2, a3;
		double y0, y1, y2, y3, muScale, mu;
		for (i = -1; i < pointsSize; i++)
		{
			i0 = points[mirrorIndex(i - 1, pointsSize)];
			i1 = points[mirrorIndex(i, pointsSize)];
			i2 = points[mirrorIndex(i + 1, pointsSize)];
			i3 = points[mirrorIndex(i + 2, pointsSize)];

			y0 = in[i0];
			y1 = in[i1];
			y2 = in[i2];
			y3 = in[i3];

			a0 = y3 - y2 - y0 + y1;
			a1 = y0 - y1 - a0;
			a2 = y2 - y0;
			a3 = y1;

			// left boundary
			if (i == -1)
			{
				start = 0;
				i1 = -i1;
			} else
				start = i1;

			// right boundary
			if (i == pointsSize - 1)
			{
				end = size;
				i2 = size + size - i2;
			} else
				end = i2;

			muScale = 1.f / (i2 - i1);
			for (j = start; j < end; j++)
			{
				mu = (j - i1) * muScale;
				out[j] = ((a0 * mu + a1) * mu + a2) * mu + a3;
			}
		}
	}

	private void emdMakeExtrema(EmdData emd, double[] curImf)
	{
		int i, lastMin = 0, lastMax = 0;
		emd.minSize = 0;
		emd.maxSize = 0;

		for (i = 1; i < emd.size - 1; i++)
		{
			if (curImf[i - 1] < curImf[i])
			{
				if (curImf[i] > curImf[i + 1] && (i - lastMax) > emd.locality)
				{
					emd.maxPoints[emd.maxSize++] = i;
					lastMax = i;
				}
			} else
			{
				if (curImf[i] < curImf[i + 1] && (i - lastMin) > emd.locality)
				{
					emd.minPoints[emd.minSize++] = i;
					lastMin = i;
				}
			}
		}
	}

	private void emdMakeResidue(EmdData emd, double[] cur)
	{
		int i;
		for (i = 0; i < emd.size; i++)
			emd.residue[i] -= cur[i];
	}

	private void emdResize(EmdData emd, int size)
	{
		int i;
		// emdClear(emd);

		emd.size = size;

		emd.imfs = new double[emd.order][]; // cnew(double*, emd->order);
		for (i = 0; i < emd.order; i++)
			emd.imfs[i] = new double[size]; // cnew(double, size);
		emd.residue = new double[size]; // cnew(double, size);
		emd.minPoints = new int[size / 2]; // cnew(int, size / 2);
		emd.maxPoints = new int[size / 2]; // cnew(int, size / 2);
		emd.min = new double[size]; // cnew(double, size);
		emd.max = new double[size]; // cnew(double, size);
	}

	private void emdSetup(EmdData emd, int order, int iterations, int locality)
	{
		emd.iterations = iterations;
		emd.order = order;
		emd.locality = locality;
		emd.size = 0;
		emd.imfs = null;
		emd.residue = null;
		emd.minPoints = null;
		emd.maxPoints = null;
		emd.min = null;
		emd.max = null;
	}

	private void emdUpdateImf(EmdData emd, double[] imf)
	{
		int i;
		for (i = 0; i < emd.size; i++)
			imf[i] -= (emd.min[i] + emd.max[i]) * .5f;
	}

	/**
	 * @return the valuedimfs
	 */
	public ArrayList<Vector<Double>> getValuedimfs()
	{
		return valuedimfs;
	}
	private int mirrorIndex(int i, int size)
	{
		if (i < size)
		{
			if (i < 0)
				return -i - 1;
			return i;
		}

		return (size - 1) + (size - i);
	}
	private EmdData processEMD(int order, double[] data)
	{
		EmdData emdData = new EmdData();
		if (order < 4)
			order = 4;
		emd.emdCreate(emdData, data.length, order, 20, 0);// was 20
		emd.emdDecompose(emdData, data);

		valuedimfs = new ArrayList<Vector<Double>>();
		orderedIMFsBySeries = new ArrayList<ArrayList<Vector<Double>>>();
		for (int i = 0; i < inputs; i++)
			orderedIMFsBySeries.add(new ArrayList<Vector<Double>>());
		int k = 0;
		for (int i = 0; i < data.length; i++)
		{
			Vector<Double> valuedimf = new Vector<Double>();

			for (int j = 0; j < order; j++) // includes residual
				valuedimf.add(emdData.imfs[j][i]);
			orderedIMFsBySeries.get(k).add(new Vector<Double>(valuedimf));
			valuedimf.add(0, data[i]);
			valuedimfs.add(valuedimf);
			k = (k + 1) % inputs;
		}
		return emdData;
	}

}
