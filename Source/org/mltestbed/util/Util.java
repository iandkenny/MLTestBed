/**
 * 
 */
package org.mltestbed.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.channels.FileChannel;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.TimeZone;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.mltestbed.ui.MLUI;
import org.mltestbed.ui.Main;

/**
 * @author Ian Kenny
 * 
 */
public class Util
{
	private static final long MIN_MEM_CALC = Math
			.floorDiv(Runtime.getRuntime().totalMemory(), 10);
	private static Runtime runtime = Runtime.getRuntime();

	private static MLUI swarmui;

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

	public static boolean checkMemFree()
	{
//		runtime.gc();
//		if (Main.isUseMem())
//			System.out.println("Using Memory Buffers");
//		else
//			System.out.println("Not using Memory Buffers");

		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long freeMemory = runtime.maxMemory() - usedMemory;
		return (Main.isUseMem()) ? freeMemory > MIN_MEM_CALC : false;

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

	public static double encodeString(StringReader sb)
	{
		BufferedReader br = new BufferedReader(sb);
		String line;
		double ret = Double.NaN;
		boolean flag = true;
		try
		{
			while ((line = br.readLine()) != null)
			{
				byte[] bytes = line.getBytes();
				for (int i = 0; i < bytes.length; i++)
				{

					double d = (double) bytes[i];
					if (ret == Double.NaN)
						ret = Math.sqrt(d);
					else
					{
						if (flag)
							ret += Math.cos(d);
						else
							ret += Math.sin(d);
						flag = !flag;
					}
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	public static Object eval(String Exp) throws ScriptException
	{
		ScriptEngineManager MyMgr = new ScriptEngineManager(); // Declaring a
																// "ScriptEngineManager"
		ScriptEngine MathEng = MyMgr.getEngineByName("JavaScript"); // Declaring
																	// a
																	// "ScriptEngine"
		return MathEng.eval(Exp);
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

	public static boolean isnan(double n)
	{
		return Double.NaN == n;
	}

	public static boolean isNumeric(String str)
	{
		return str.trim().matches("-?\\d+(\\.\\d+)?"); // match a number with
														// optional
														// '-' and decimal.
	}

	public static double log(int N, int b)
	{

		// calculate log N in base b indirectly
		// using log() method
		double result = (Math.log(N) / Math.log(b));

		return result;
	}
	public static double log2(long l)
	{
		return Math.log(l) / Math.log(2);
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

	public static double mean(double[] m)
	{
		double sum = 0;
		for (int i = 0; i < m.length; i++)
		{
			sum += m[i];
		}
		return sum / m.length;
	}

	public static double median(double m[])
	{
		Arrays.sort(m);
		int middle = m.length / 2;
		if (m.length % 2 == 1)
		{
			return m[middle];
		} else
		{
			return (m[middle - 1] + m[middle]) / 2.0;
		}
	}
	public static double mode(double a[])
	{
		double maxValue = 0;
		int maxCount = 0;

		for (int i = 0; i < a.length; ++i)
		{
			int count = 0;
			for (int j = 0; j < a.length; ++j)
			{
				if (a[j] == a[i])
					++count;
			}
			if (count > maxCount)
			{
				maxCount = count;
				maxValue = a[i];
			}
		}

		return maxValue;
	}
	public static double[][] ones(int x, int y)
	{
		double[][] p = new double[x][y];
		for (int i = 0; i < p.length; i++)
			for (int j = 0; j < p[0].length; j++)
				p[i][j] = 1;
		return p;
	}

	public static String spacePunct(String s)
	{
		return s.replaceAll("\\p{Punct}", " $0 ");
	}

	public static double standardDeviation(double s[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = s.length;

        for(double num : s) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: s) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }

	public static double[][] transpose(double[][] arr)
	{
		double[][] ret = new double[arr[0].length][arr.length];
		// transpose matrix
		for (int i = 0; i < arr.length; i++)
			for (int j = 0; j < arr[0].length; j++)
				ret[j][i] = arr[i][j];
		return ret;
	}
	public static double[][] zeros(int x, int y)
	{
		double[][] p = new double[x][y];
		for (int i = 0; i < p.length; i++)
			for (int j = 0; j < p[0].length; j++)
				p[i][j] = 0;
		return p;
	}


	public Util()
	{
	}
	public Util(boolean useMem)
	{
		Main.setUseMem(useMem);
	}
	public Util(MLUI ui)
	{
		swarmui = ui;
		if (ui != null && ui.getUseMemBuffersCheck() != null)
			Main.setUseMem(ui.getUseMemBuffersCheck().isSelected());
	}
}
