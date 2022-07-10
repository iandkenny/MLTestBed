/**
 * 
 */
package org.mltestbed.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.TimeZone;

import org.mltestbed.ui.MLUI;
import org.mltestbed.ui.Main;

/**
 * @author Ian Kenny
 * 
 */
public class Util
{
	private static long minMem;
	private static Runtime runtime = Runtime.getRuntime();
	private static final long MIN_MEM_CALC = Math.floorDiv(runtime.freeMemory(),
			20);
	private static MLUI swarmui;

	private static long limit = MIN_MEM_CALC;

	public static boolean checkMemFree()
	{
//		runtime.gc();
//		if (useMem)
//			System.out.println("Using Memory Buffers");
//		else
//			System.out.println("Not using Memory Buffers");
		return (Main.isUseMem()) ? runtime.freeMemory() > limit : false;

	}
	public static void copyFile(File sourceFile, File destFile)
			throws IOException
	{
		if (!destFile.exists())
		{
			destFile = (File.createTempFile("swarm", null));
			destFile.deleteOnExit();
		}

		FileChannel source = null;
		FileChannel destination = null;
		FileOutputStream fileOutputStream = null;
		FileInputStream fileInputStream = null;

		try
		{
			fileInputStream = new FileInputStream(sourceFile);
			source = fileInputStream.getChannel();
			fileOutputStream = new FileOutputStream(destFile);
			destination = fileOutputStream.getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally
		{
			if (source != null)
			{
				source.close();
			}
			if (destination != null)
			{
				destination.close();
			}
			if (fileOutputStream != null)
			{
				fileOutputStream.close();
			}
			if (fileInputStream != null)
			{
				fileInputStream.close();
			}
		}
	}
	public static File createTempFile() throws IOException
	{
		File file = null;
		try
		{
			(file = File.createTempFile("swarm", null)).deleteOnExit();
		} catch (IllegalStateException e)
		{
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}
	public static String formatRuntime(long l)
	{
		String format = "";
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(l);
		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
		try
		{
			NumberFormat numberInstance = NumberFormat.getNumberInstance();
			numberInstance.setMinimumIntegerDigits(2);
			Integer day = cal.get(Calendar.DAY_OF_YEAR) - 1;
			Integer hour = cal.get(Calendar.HOUR_OF_DAY);
			Integer minute = cal.get(Calendar.MINUTE);
			Integer sec = cal.get(Calendar.SECOND);
			format = ((day == null) ? "00" : numberInstance.format(day)) + ":"
					+ ((hour == null) ? "00" : numberInstance.format(hour))
					+ ":"
					+ ((minute == null) ? "00" : numberInstance.format(minute))
					+ ":" + ((sec == null) ? "00" : numberInstance.format(sec));
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return format;
	}

	/**
	 * @return the swarmui
	 */
	public static MLUI getSwarmui()
	{
		return swarmui;
	}
	public static boolean isNumeric(String str)
	{
		return str.trim().matches("-?\\d+(\\.\\d+)?");  // match a number with
														// optional
														// '-' and decimal.
	}

	public static double log2(long l)
	{
		return Math.log(l) / Math.log(2);
	}

	public static double[][] zeros(int x, int y)
	{
		double[][] p = new double[x][y];
		for (int i = 0; i < p.length; i++)
			for (int j = 0; j < p[0].length; j++)
				p[i][j] = 0;
		return p;
	}
	
	public static double[][] ones(int x, int y)
	{
		double[][] p = new double[x][y];
		for (int i = 0; i < p.length; i++)
			for (int j = 0; j < p[0].length; j++)
				p[i][j] = 1;
		return p;
	}
	public static double[][] transpose(double[][] arr)
	{
		double[][] ret = new double[arr[0].length][arr.length];
		// transpose matrix
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr[0].length; j++)
				ret [j][i] = arr[i][j];
		return ret;
	}
	/**
	 * @return
	 */
	public static double[][] inverse(double[][] arr)
	{
		double[][] p = new double[arr.length][arr[0].length];
		for (int i = 0; i < p.length; i++)
			for (int j = 0; j < p[0].length; j++)
				p[i][j] = 1 / arr[i][j];
		return p;
	}
	 
	public static double[] maxPerColumn(double[][] arr)
	{
		double[] maxm = new double[arr[0].length];
		for (int col = 0; col < arr[0].length; col++)
		{

			// Initialize max to 0 at beginning
			// of finding max element of each column
			maxm[col] = arr[0][col];
			for (int row = 1; row < arr.length; row++)
				if (arr[row][col] > maxm[col])
					maxm[col] = arr[row][col];

//			System.out.println(maxm[col]);
		}
		return maxm;
	}
	 
	public static double absmean(double[] a)
	{
		int n = a.length;
		double sum = 0;
		for (int i = 0; i < n; i++)
		{
			sum += Math.abs(a[i]);
		}
		return (sum != 0 && n != 0) ? (sum / (double) n) : Double.NaN;
	}
	public static boolean isnan(double n)
	{
		return Double.NaN ==n;
	}
	public Util()
	{
		minMem = MIN_MEM_CALC;
		limit = minMem * 3;
	}
	public Util(boolean useMem)
	{
		Main.setUseMem(useMem);
		minMem = MIN_MEM_CALC;
		limit = minMem * 3;
	}
	public Util(MLUI ui)
	{
		swarmui = ui;
		if (ui != null && ui.getUseMemBuffersCheck() != null)
			Main.setUseMem(ui.getUseMemBuffersCheck().isSelected());
		minMem = runtime.freeMemory() / 10;
		limit = minMem * 3;
	}
}
