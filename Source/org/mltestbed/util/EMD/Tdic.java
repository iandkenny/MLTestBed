package org.mltestbed.util.EMD;

import java.util.logging.Level;

import org.mltestbed.util.Log;

public class Tdic
{

	private double[][] c = null;// is the correlation matrix
	private double[][] p = null;// is the indicator for student-test
	private int[] scale = null;// is the time scale
	private double[] tx = null; // is the time axis

	public Tdic()
	{
	}
	private double betacf_sc(Double arg0, Double arg1, Double arg2)
	{
		/*
		 * Ported from the original C code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */

		/* declarations */
		double a, b, x, out1 = Double.NaN;
		int m, m2, Ny, i, j, maxit;
		double c, d, h, qab, qam, qap, stp, tmp, eps, fpmin, aa, del;
//		double[] coef;

		try
		{
			/* check input */
//if (nrhs!=3)     mexErrMsgTxt("Three input!");
			if (arg0 == null)
				throw new Exception("The first input is empty!");
			if (arg1 == null)
				throw new Exception("The second input is empty!");
			if (arg2 == null)
				throw new Exception("The thrid input is empty!");

			/* get input data */
			a = arg0.doubleValue();// mxGetPr(prhs[0]);
			b = arg1.doubleValue();// mxGetPr(prhs[1]);
			x = arg2.doubleValue();// mxGetPr(prhs[2]);

			maxit = 100;
			eps = 2.220446049250313e-016;
			fpmin = 2.225073858507201e-308 / eps;
			qab = a + b;

			qap = a + 1.0;
			qam = a - 1.0;
			c = 1.0;
			d = 1.0 - qab * x / qap;
			if (Math.abs(d) < fpmin)
				d = fpmin;

			d = 1.0 / d;

			h = d;

//plhs[0]=mxCreateDoubleMatrix(1,1,mxREAL);
//out1=mxGetPr(plhs[0]);

			for (m = 0; m < maxit; m++)
			{
				m2 = 2 * m;

				aa = m * (b - m) * x / ((qam + m2) * (a + m2));

				d = 1.0 + aa * d;
				if (Math.abs(d) < fpmin)
					d = fpmin;

				c = 1.0 + aa / c;

				if (Math.abs(c) < fpmin)
					c = fpmin;

				d = 1.0 / d;

				h = h * d * c;

				aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));

				d = 1.0 + aa * d;
				if (Math.abs(d) < fpmin)
					d = fpmin;

				c = 1.0 + aa / c;

				if (Math.abs(c) < fpmin)
					c = fpmin;
				d = 1.0 / d;

				del = d * c;
				h = h * del;

				// could be coded better for Java, as coded is consistent with
				// the original C
				if (Math.abs(del - 1.0) <= eps)
				{
					out1 = h;
					return out1;
				}

				out1 = h;
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
		}
		// must have default return in Java
		return out1;
	}
	private double betai_s(double a, double b, double x)
	{
		/*
		 * Ported from the original MATLAB code,written by Yongxiang HUANG
		 * 04-2012, by Ian Kenny 04-2022
		 */
//%use nrtype; use nrutil, only : assert
//%use nr, only : betacf,gammln
		double bt;
		if (x == 0.0 || x == 1.0)
			bt = 0.0;
		else // %factors in front of the continued fraction
			bt = btc(a, b, x);
		double prob;
		if (x < (a + 1.0d) / (a + b + 2.0d)) // %use continued fraction
												// directly.
			prob = bt * betacf_sc(a, b, x) / a;
		else // %use continued fraction after making the
			prob = 1.0d - bt * betacf_sc(b, a, 1.0d - x) / b; // %symmetry
																// transformation.
		return prob;
	}
	private double btc(Double arg0, Double arg1, Double arg2)
	{
		/*
		 * Ported from the original C code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */

		/* declarations */
		double x, a, b, out1 = Double.NaN;
//		int dof, Nx, Ny, i, j;
		double stp, tmp, xx, tmp2;
		double[] coef;

		try
		{
			/* check input */
//    if (nrhs!=3)     mexErrMsgTxt("Three input!");
			if (arg0 == null)
				throw new Exception("The first input is empty!");
			if (arg1 == null)
				throw new Exception("The second input is empty!");
			if (arg2 == null)
				throw new Exception("The thrid input is empty!");

			/* get input data */
			a = arg0.doubleValue();// mxGetPr(prhs[0]);
			b = arg1.doubleValue();// mxGetPr(prhs[1]);
			x = arg2.doubleValue();// mxGetPr(prhs[2]);

			stp = 2.5066282746310005;

			coef = new double[6];// (double *)malloc(6*sizeof(double));;
			coef[0] = 76.18009172947146;
			coef[1] = -86.50532032941677;
			coef[2] = 24.01409824083091;
			coef[3] = -1.231739572450155;
			coef[4] = 0.1208650973866179e-2;
			coef[5] = -0.5395239384953e-5;

//    plhs[0]=mxCreateDoubleMatrix(1,1,mxREAL);
//    out1=mxGetPr(plhs[0]);

			xx = a + b;
			tmp = xx + 5.5;
			tmp = (xx + 0.5) * Math.log(tmp) - tmp;

			tmp2 = tmp
					+ Math.log(stp
							* (1.000000000190015 + (coef[0] + coef[1] + coef[2]
									+ coef[3] + coef[4] + coef[5]) / (xx + 1.0))
							/ xx);

			xx = a;
			tmp = xx + 5.5;
			tmp = (xx + 0.5) * Math.log(tmp) - tmp;

			tmp = tmp
					+ Math.log(stp
							* (1.000000000190015 + (coef[0] + coef[1] + coef[2]
									+ coef[3] + coef[4] + coef[5]) / (xx + 1.0))
							/ xx);
			tmp2 = tmp2 - tmp;

			xx = b;
			tmp = xx + 5.5;
			tmp = (xx + 0.5) * Math.log(tmp) - tmp;

			tmp = tmp
					+ Math.log(stp
							* (1.000000000190015 + (coef[0] + coef[1] + coef[2]
									+ coef[3] + coef[4] + coef[5]) / (xx + 1.0))
							/ xx);
			tmp2 = tmp2 - tmp;

			tmp2 = tmp2 + a * Math.log(x) + b * Math.log(1.0 - x);
			out1 = Math.exp(tmp2);
			/* free allocated memory */

//    free(coef);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//	e.printStackTrace();
		}
		return out1;
	}

	public double[][] getCorrelationMatrix()
	{
		return c;
	}

	public int[] getScaleAxis()
	{
		return scale;
	}
	public double[][] getStudentTest()
	{
		return p;
	}

	public double[] getTimeAxis()
	{
		return tx;
	}

	private double[] myxcorrc(double[] x, double[] y, int df)
	{
		/*
		 * Ported from the original C code,written by Yongxiang HUANG 04-2012,
		 * by Ian Kenny 04-2022
		 */
		/* declarations */
		int pSizeX, pSizeY;

		double[] out1 = new double[3];
		int i;// ,j;
		double tmp, stdx, stdy, tiny;

		/* check input */
		try
		{
//		if (nrhs!=3)    throw new Exception("Three input!");
			if (x == null || x.length == 0)
				throw new Exception("The first is empty!");
			if (y == null || y.length == 0)
				throw new Exception("The second is empty!");
			if (df <= 0)
				throw new Exception("The third is not valid!");
			/* get input data */
//    x=mxGetPr(prhs[0]);
//    y=mxGetPr(prhs[1]);
//    df=mxGetPr(prhs[2]);

			tiny = 1.0e-20;

			pSizeX = x.length; // mxGetNumberOfElements(prhs[0]);
			pSizeY = y.length;// mxGetNumberOfElements(prhs[1]);
			if (pSizeX != pSizeY)
				throw new Exception("Two inputs should have the same length!");

//		plhs[0]=mxCreateDoubleMatrix(1,3,mxREAL);
			out1 = new double[pSizeX];// mxGetPr(plhs[0]);

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + x[i];
			tmp = tmp / pSizeX;
			for (i = 0; i < pSizeX; i++)
				x[i] = -tmp + x[i];

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + y[i];
			tmp = tmp / pSizeX;
			for (i = 0; i < pSizeX; i++)
				y[i] = -tmp + y[i];

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + x[i] * x[i];
			tmp = tmp / pSizeX;
			stdx = Math.sqrt(tmp) + tiny;

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + y[i] * y[i];
			tmp = tmp / pSizeX;
			stdy = Math.sqrt(tmp) + tiny;

			tmp = 0.0;
			for (i = 0; i < pSizeX; i++)
				tmp = tmp + x[i] * y[i];
			tmp = tmp / pSizeX;
			out1[0] = tmp / stdx / stdy;

			out1[1] = 0.5e0 * Math.log(
					((1.0e0 + out1[0]) + tiny) / ((1.0e0 - out1[0]) + tiny));

			out1[2] = out1[0] * Math.sqrt(df / (((1.0e0 - out1[0]) + tiny)
					* ((1.0e0 + out1[0]) + tiny)));
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//		e.printStackTrace();
		}
		return out1;
	}

	private double studentc(Double arg0, Integer arg1)
	{

		/* declarations */
//    int  pSizeI, pSizeB;
//    
//    int i,j,k,kk,i0,j0,k0,Np;
//    
		double[] a, b, c, d, e1, f, g, h, i1, j1;
		double t, out1 = Double.NaN;
		int df;
		double nonsense, v, pos, x;
		/*
		 * instantaneous frequency, instantaneous amplitude, resolution of
		 * amplitude resolution frequency
		 */

		try
		{
			/* check input */
//    if (nrhs!=2)     mexErrMsgTxt("You have to input three parameters!");
			if (arg0 == null)
				throw new Exception("t is empty!");
			if (arg1 == null)
				throw new Exception("df is empty!");

			/* get input data */
			t = arg0.doubleValue();// mxGetPr(prhs[0]);
			df = arg1.intValue();// mxGetPr(prhs[1]);

			nonsense = 99999.99;

			a = new double[5];// (double *)malloc(5*sizeof(double));;
			a[0] = 0.09979441;
			a[1] = -0.581821;
			a[2] = 1.390993;
			a[3] = -1.222452;
			a[4] = 2.151185;

			b = new double[2];// (double *)malloc(2*sizeof(double));;
			b[0] = 5.537409;
			b[1] = 11.42343;

			c = new double[5];// (double *)malloc(5*sizeof(double));;
			c[0] = 0.04431742;
			c[1] = -0.2206018;
			c[2] = -0.03317253;
			c[3] = 5.679969;
			c[4] = -12.96519;

			d = new double[2];// (double *)malloc(2*sizeof(double));;
			d[0] = 5.166733;
			d[1] = 13.49862;

			e1 = new double[5];// (double *)malloc(5*sizeof(double));;
			e1[0] = 0.009694901;
			e1[1] = -0.1408854;
			e1[2] = 1.88993;
			e1[3] = -12.75532;
			e1[4] = 25.77532;

			f = new double[2];// (double *)malloc(2*sizeof(double));;
			f[0] = 4.233736;
			f[1] = 14.3963;

			g = new double[5];// (double *)malloc(5*sizeof(double));;
			g[0] = -9.187228e-5;
			g[1] = 0.03789901;
			g[2] = -1.280346;
			g[3] = 9.249528;
			g[4] = -19.08115;

			h = new double[2];// (double *)malloc(2*sizeof(double));;
			h[0] = 2.777816;
			h[1] = 16.46132;

			i1 = new double[5];// (double *)malloc(5*sizeof(double));;
			i1[0] = 5.79602e-4;
			i1[1] = -0.02763334;
			i1[2] = 0.4517029;
			i1[3] = -2.657697;
			i1[4] = 5.127212;

			j1 = new double[2];// (double *)malloc(2*sizeof(double));;

			j1[0] = 0.5657187;
			j1[1] = 21.83269;

//		plhs[0]=mxCreateDoubleMatrix(1,1,mxREAL);
//		out1=new double[1];//mxGetPr(plhs[0]);

			if (df <= 4.0)
				out1 = nonsense;

			else
			{
				v = 1.0 / df;
				if (t >= 0)
					pos = 1.0;
				else
					pos = 0;

				if (t < 0)
					t = -t;
				x = (1.0 + t * (((a[0]
						+ v * (a[1] + v * (a[2] + v * (a[3] + v * a[4]))))
						/ (1.0 - v * (b[0] - v * b[1])))
						+ t * (((c[0] + v
								* (c[1] + v * (c[2] + v * (c[3] + v * c[4]))))
								/ (1.0 - v * (d[0] - v * d[1])))
								+ t * (((e1[0] + v * (e1[1] + v
										* (e1[2] + v * (e1[3] + v * e1[4]))))
										/ (1.0 - v * (f[0] - v * f[1])))
										+ t * (((g[0] + v * (g[1] + v * (g[2]
												+ v * (g[3] + v * g[4]))))
												/ (1.0 - v * (h[0] - v * h[1])))
												+ t * ((i1[0] + v * (i1[1] + v
														* (i1[2] + v * (i1[3]
																+ v * i1[4]))))
														/ (1.0 - v * (j1[0] - v
																* j1[1]))))))));
				x = 0.5 * Math.pow(x, -8);
				if (pos == 1.0)
					out1 = x;
				else
					out1 = 1.0 - x;
			}

//    free(a);
//    free(b);
//    free(c);
//    free(d);
//    free(e1);
//    free(g);
//    free(h);
//    free(i1);
//    free(j1);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
//		e.printStackTrace();
		}
		return out1;
	}
	public void tdic(double[] x, double[] y, int[] ifz, Integer ntime,
			Integer nct, Boolean it)
	{
		/*
		 * Ported from the MATLAB code by Ian Kenny 04-2022 Original comment
		 * below:
		 * 
		 * % [c,p,tx,scale]= tdic(x,y,ifz,ntime,nct,it) % Input % x is the time
		 * series of the first variable % y is the time series of the second
		 * variable % ifz is the instantaneous period provided by the
		 * zero-crossing method % it is the output of the function
		 * maxlocalperiod % ntime is the maximum window size, the default value
		 * is half of the length % of the data % nct is the maximum size of
		 * moving time window % - it: definition of degree of freedom % - 0:
		 * means default, i.e., d.o.f. = n-2 % - 1: means how many cycles
		 * included in the region % Output % c is the correlation matrix % p is
		 * the indicator for student-test % tx is the time axis % scale is the
		 * time scale % % [c,p,tx,scale]=tdicnew(x,y,pp);
		 *
		 * % To show the result: surf(tx,scale,(c.*p))
		 * 
		 * % Written by Yongxiang HUANG 04-2012
		 * 
		 */
		/*
		 * %check input
		 * 
		 */
		double[] out1;
		double r;
		double t;
		double z;

		int Nx = x.length;
		int Ny = y.length;
		try
		{
			if (Nx != Ny)
				throw new Exception(
						"Length of the first two inputs should be the same.");

			if (ntime == null && nct == null && it == null)
			{
				ntime = Nx;
				nct = (int) Math.floor(ntime / 2);
				it = false;
			}

			if (ntime > Nx)
				ntime = Nx;

			int ncct;
			if (nct > 0.5 * ntime)
				ncct = (int) Math.floor(0.5 * ntime);
			else
				ncct = nct;

			if (it == null)
				it = false;

			c = new double[ncct][ntime];// *nan;//%initial the output
			p = new double[ncct][ntime];// *nan;//%initial the output
			for (int j = 0; j < c.length; j++)
				for (int k = 0; k < c[0].length; k++)
				{
					c[j][k] = Double.NaN;
					p[j][k] = Double.NaN;
				}

//			p=c; // looking at the code below we need two arrays
			tx = new double[ntime];
			for (int k = 0; k < ntime; k++)
				tx[k] = Double.NaN;// zeros(1,ntime)*nan;

			int df = 0;
			for (int j = 1; j < ntime; j++)
			{
				int d = (int) Math.max(Math.floor(ifz[j] * 0.5e0), 1);
				if (d <= nct)
				{

					for (int i = d; i < ncct; i++)
					{
						if (j - i < 1 || j + i > ntime || j + i <= j - i)
							continue;

						int ndf = 0;
						if (it == false)
							df = ntime - 2;
						else
							ndf = 0;

						for (int ii = j - i + 1; ii < j + i - 1; ii++)
						{
							if ((x[ii - 1] <= x[ii]) && (x[ii] >= x[ii + 1]))
								ndf = ndf + 1;
						}
						if (ndf == 0)
							continue;

						df = ndf;

//			            %%%%%
						out1 = new double[3];
						int len = (j - 1) - (j + 1);
						double[] subx = new double[len];
						double[] suby = new double[len];
						System.arraycopy(x, 0, subx, 0, len);
						System.arraycopy(y, 0, suby, 0, len);

						out1 = myxcorrc(subx, suby, df);// %calculate the
														// local
														// correlation
						r = out1[1];
						z = out1[2];
						t = out1[3];

						double prob = betai_s(0.5e0 * df, 0.5e0,
								df / Math.pow(df + t, 22));// % student's t
															// probability.
//			            %%%%%

						c[i][j] = r;
						p[i][j] = prob;
						tx[j] = j;
						if (it) // % we do not use this part
							t = Math.sqrt(1.0 - r * r);
						if (t == 0.0)
							t = t + 0.0000010;
						t = r * Math.sqrt(df) / t;
						p[i][j] = studentc(t, df);
					}
				}
			}
			for (int j = 0; j < p.length; j++)
				for (int k = 0; k < p[0].length; k++)
					p[j][k] = (Math.abs(p[j][k]) > 0.5)
							? Double.NaN
							: (Math.abs(p[j][k]) < 0.5) ? 1 : p[j][k];
//			p(abs(p)>0.5)=Double.NaN; //% check the student test
//			p(abs(p)<0.5)=1;
			for (int j = 0; j < c.length; j++)
				for (int k = 0; k < c[0].length; k++)
					c[j][k] = (c[j][k] > 1 || c[j][k] == 0)
							? Double.NaN
							: c[j][k];
//			c(c>1)=nan; //%check the correlation matrix
//			c(c==0)=nan;
			scale = new int[p.length];
			for (int k = 0; k < c[0].length; k++)
				scale[k] = k;
//			scale=1:size(p,1);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}
	}

}