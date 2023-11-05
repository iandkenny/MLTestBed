package org.mltestbed.util;

import java.util.Random;

/******************************************************************************
 * Compilation: javac Matrix.java Execution: java Matrix
 *
 * A bare-bones collection of static methods for manipulating matrices.
 *
 * 
 * 6 different ways to multiply two N-by-N matrices. Illustrates importance of
 * row-major vs. column-major ordering.
 *
 * % java Matrix multiplication 500 Generating input: 0.048 seconds Order ijk:
 * 3.562 seconds Order ikj: 1.348 seconds Order jik: 2.368 seconds Order jki:
 * 4.846 seconds Order kij: 1.407 seconds Order kji: 4.91 seconds Order jik JAMA
 * optimized: 0.571 seconds Order ikj pure row: 0.483 seconds
 *
 * These timings are on a SUN-FIRE-X4100 running Linux.
 *
 *************************************************************************/

public class Matrix
{

	// return c = a + b
	public static double[][] add(double[][] a, double[] b)
	{
		int m = a.length;
		int n = a[0].length;
		int m2 = b.length;
		if (m != m2)
			throw new RuntimeException("Illegal matrix dimensions.");
		double[][] c = new double[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				c[i][j] = a[i][j] + b[i];
		return c;
	}

	// return c = a + b
	public static double[][] add(double[][] a, double[][] b)
	{
		int m = a.length;
		int n = a[0].length;
		double[][] c = new double[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				c[i][j] = a[i][j] + b[i][j];
		return c;
	}

	// return x^T y
	public static double dot(double[] x, double[] y)
	{
		if (x.length != y.length)
			throw new RuntimeException("Illegal vector dimensions.");
		double sum = 0.0;
		for (int i = 0; i < x.length; i++)
			sum += x[i] * y[i];
		return sum;
	}

	// return n-by-n identity matrix I
	public static double[][] identity(int n)
	{
		double[][] a = new double[n][n];
		for (int i = 0; i < n; i++)
			a[i][i] = 1;
		return a;
	}
	
	// Method to carry out the partial-pivoting Gaussian
		// elimination. Here index[] stores pivoting order.
	public static void gaussian(double a[][], int index[])
	{
		int n = index.length;
		double c[] = new double[n];

		// Initialize the index
		for (int i = 0; i < n; ++i)
			index[i] = i;

		// Find the rescaling factors, one from each row
		for (int i = 0; i < n; ++i)
		{
			double c1 = 0;
			for (int j = 0; j < n; ++j)
			{
				double c0 = Math.abs(a[i][j]);
				if (c0 > c1)
					c1 = c0;
			}
			c[i] = c1;
		}

		// Search the pivoting element from each column
		int k = 0;
		for (int j = 0; j < n - 1; ++j)
		{
			double pi1 = 0;
			for (int i = j; i < n; ++i)
			{
				double pi0 = Math.abs(a[index[i]][j]);
				pi0 /= c[index[i]];
				if (pi0 > pi1)
				{
					pi1 = pi0;
					k = i;
				}
			}

			// Interchange rows according to the pivoting order
			int itmp = index[j];
			index[j] = index[k];
			index[k] = itmp;
			for (int i = j + 1; i < n; ++i)
			{
				double pj = a[index[i]][j] / a[index[j]][j];

				// Record pivoting ratios below the diagonal
				a[index[i]][j] = pj;

				// Modify other elements accordingly
				for (int l = j + 1; l < n; ++l)
					a[index[i]][l] -= pj * a[index[j]][l];
			}
		}
	}

	public static double[][] invert(double a[][])
	{
		int n = a.length;
		double x[][] = new double[n][n];
		double b[][] = new double[n][n];
		int index[] = new int[n];
		for (int i = 0; i < n; ++i)
			b[i][i] = 1;

		// Transform the matrix into an upper triangle
		gaussian(a, index);

		// Update the matrix b[i][j] with the ratios stored
		for (int i = 0; i < n - 1; ++i)
			for (int j = i + 1; j < n; ++j)
				for (int k = 0; k < n; ++k)
					b[index[j]][k] -= a[index[j]][i] * b[index[i]][k];

		// Perform backward substitutions
		for (int i = 0; i < n; ++i)
		{
			x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
			for (int j = n - 2; j >= 0; --j)
			{
				x[j][i] = b[index[j]][i];
				for (int k = j + 1; k < n; ++k)
				{
					x[j][i] -= a[index[j]][k] * x[k][i];
				}
				x[j][i] /= a[index[j]][j];
			}
		}
		return x;
	}
	

	public static void main(String[] args)
	{
		int N = Integer.parseInt(args[0]);
		long start, stop;
		double elapsed;

		// generate input
		start = System.currentTimeMillis();

		double[][] A = new double[N][N];
		double[][] B = new double[N][N];
		double[][] C;

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				A[i][j] = Math.random();

		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				B[i][j] = Math.random();

		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Generating input:  " + elapsed + " seconds");

		// order 1: ijk = dot product version
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				for (int k = 0; k < N; k++)
					C[i][j] += A[i][k] * B[k][j];
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order ijk:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 2: ikj
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int i = 0; i < N; i++)
			for (int k = 0; k < N; k++)
				for (int j = 0; j < N; j++)
					C[i][j] += A[i][k] * B[k][j];
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order ikj:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 3: jik
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int j = 0; j < N; j++)
			for (int i = 0; i < N; i++)
				for (int k = 0; k < N; k++)
					C[i][j] += A[i][k] * B[k][j];
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order jik:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 4: jki = GAXPY version
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int j = 0; j < N; j++)
			for (int k = 0; k < N; k++)
				for (int i = 0; i < N; i++)
					C[i][j] += A[i][k] * B[k][j];
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order jki:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 5: kij
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int k = 0; k < N; k++)
			for (int i = 0; i < N; i++)
				for (int j = 0; j < N; j++)
					C[i][j] += A[i][k] * B[k][j];
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order kij:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 6: kji = outer product version
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int k = 0; k < N; k++)
			for (int j = 0; j < N; j++)
				for (int i = 0; i < N; i++)
					C[i][j] += A[i][k] * B[k][j];
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order kji:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 7: jik optimized ala JAMA
		C = new double[N][N];
		start = System.currentTimeMillis();
		double[] bcolj = new double[N];
		for (int j = 0; j < N; j++)
		{
			for (int k = 0; k < N; k++)
				bcolj[k] = B[k][j];
			for (int i = 0; i < N; i++)
			{
				double[] arowi = A[i];
				double sum = 0.0;
				for (int k = 0; k < N; k++)
				{
					sum += arowi[k] * bcolj[k];
				}
				C[i][j] = sum;
			}
		}
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out
				.println("Order jik JAMA optimized:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		// order 8: ikj pure row
		C = new double[N][N];
		start = System.currentTimeMillis();
		for (int i = 0; i < N; i++)
		{
			double[] arowi = A[i];
			double[] crowi = C[i];
			for (int k = 0; k < N; k++)
			{
				double[] browk = B[k];
				double aik = arowi[k];
				for (int j = 0; j < N; j++)
				{
					crowi[j] += aik * browk[j];
				}
			}
		}
		stop = System.currentTimeMillis();
		elapsed = (stop - start) / 1000.0;
		System.out.println("Order ikj pure row:   " + elapsed + " seconds");
		if (N < 10)
			show(C);

		testMatrixAlgebra();
		
		double D[][] = invert(A);

		System.out.println("The inverse is: ");
		for (int i = 0; i < N; ++i)
		{
			for (int j = 0; j < N; ++j)
			{
				System.out.print(D[i][j] + "  ");
			}
			System.out.println();
		}

	}
	// vector-matrix multiplication (y = x^T A)
	public static double[] multiply(double[] x, double[][] a)
	{
		int m = a.length;
		int n = a[0].length;
		if (x.length != m)
			throw new RuntimeException("Illegal matrix dimensions.");
		double[] y = new double[n];
		for (int j = 0; j < n; j++)
			for (int i = 0; i < m; i++)
				y[j] += a[i][j] * x[i];
		return y;
	}

	// matrix-vector multiplication (y = A * x)
	public static double[] multiply(double[][] a, double[] x)
	{
		int m = a.length;
		int n = a[0].length;
		if (x.length != n)
			throw new RuntimeException("Illegal matrix dimensions.");
		double[] y = new double[m];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				y[i] += a[i][j] * x[j];
		return y;
	}
	// return c = a * b
	public static double[][] multiply(double[][] a, double[][] b)
	{
		int m1 = a.length;
		int n1 = a[0].length;
		int m2 = b.length;
		int n2 = b[0].length;
		if (n1 != m2)
			throw new RuntimeException("Illegal matrix dimensions.");
		double[][] c = new double[m1][n2];
		for (int i = 0; i < m1; i++)
			for (int j = 0; j < n2; j++)
				for (int k = 0; k < n1; k++)
					c[i][j] += a[i][k] * b[k][j];
		return c;
	}

	public static double[][] multiplyMatrices(double[][] firstMatrix,
			double[][] secondMatrix)
	{
		double[][] result = new double[firstMatrix.length][secondMatrix[0].length];

		for (int row = 0; row < result.length; row++)
		{
			for (int col = 0; col < result[row].length; col++)
			{
				result[row][col] = multiplyMatricesCell(firstMatrix,
						secondMatrix, row, col);
			}
		}

		return result;
	}

	private static double multiplyMatricesCell(double[][] firstMatrix,
			double[][] secondMatrix, int row, int col)
	{
		double cell = 0;
		for (int i = 0; i < secondMatrix.length; i++)
		{
			cell += firstMatrix[row][i] * secondMatrix[i][col];
		}
		return cell;
	}

	// return a random m-by-n matrix with values between 0 and 1
	public static double[][] random(int m, int n)
	{
		double[][] a = new double[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				a[i][j] = Math.random();
		return a;
	}

	public static void show(double[][] a)
	{
		int N = a.length;
		for (int i = 0; i < N; i++)
		{
			for (int j = 0; j < N; j++)
			{
				System.out.printf("%6.4f ", a[i][j]);
			}
			System.out.println();
		}
		System.out.println();
	}

	// return c = a - b
	public static double[][] subtract(double[][] a, double[] b)
	{
		int m = a.length;
		int n = a[0].length;
		int m2 = b.length;
		if (m != m2)
			throw new RuntimeException("Illegal matrix dimensions.");
		double[][] c = new double[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				c[i][j] = a[i][j] - b[i];
		return c;
	}

	// return c = a - b
	public static double[][] subtract(double[][] a, double[][] b)
	{
		int m = a.length;
		int n = a[0].length;
		double[][] c = new double[m][n];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				c[i][j] = a[i][j] - b[i][j];
		return c;
	}
	// test client
	public static void testMatrixAlgebra()
	{
		System.out.println("D");
		System.out.println("--------------------");
		double[][] d =
		{
				{1, 2, 3},
				{4, 5, 6},
				{9, 1, 3}};
		show(d);
		System.out.println();

		System.out.println("I");
		System.out.println("--------------------");
		double[][] c = Matrix.identity(5);
		show(c);
		System.out.println();

		System.out.println("A");
		System.out.println("--------------------");
		double[][] a = Matrix.random(5, 5);
		show(a);
		System.out.println();

		System.out.println("A^T");
		System.out.println("--------------------");
		double[][] b = Matrix.transpose(a);
		show(b);
		System.out.println();

		System.out.println("A + A^T");
		System.out.println("--------------------");
		double[][] e = Matrix.add(a, b);
		show(e);
		System.out.println();

		System.out.println("A * A^T");
		System.out.println("--------------------");
		double[][] f = Matrix.multiply(a, b);
		show(f);
		System.out.println();
	}
	// return B = A^T
	public static double[][] transpose(double[][] a)
	{
		int m = a.length;
		int n = a[0].length;
		double[][] b = new double[n][m];
		for (int i = 0; i < m; i++)
			for (int j = 0; j < n; j++)
				b[j][i] = a[i][j];
		return b;
	}
}
