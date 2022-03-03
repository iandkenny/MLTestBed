/*
 * Created on 15-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.heuristics.PSO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JProgressBar;

import org.mltestbed.data.OutputResults;
import org.mltestbed.data.Results;
import org.mltestbed.heuristics.PSO.Heirarchy.Heirarchy;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.topologies.Star;
import org.mltestbed.topologies.Topology;
import org.mltestbed.util.Boundary;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.RandGen;
import org.mltestbed.util.Util;
import org.mltestbed.util.net.SwarmNet;
import org.mltestbed.util.net.SwarmNet.SwarmServer;

/**
 * @author Ian Kenny
 * 
 */

public abstract class BaseSwarm extends Thread implements Cloneable
{
	public static class EvalParticle extends Thread
	{
		/**
		 * 
		 */

		private boolean complete;
		private boolean evaluate;
		private Particle particle;
		private int particleIndex;
		private HashMap<Integer, Particle> particles = null;
		private boolean running;
		private boolean singleParticle = true;
		private int swarmNo;
		private Object sync = new Object();

		public EvalParticle(int swarm, HashMap<Integer, Particle> particles)
		{
			super("Swarm " + swarm + " Evaluation Starting with Particle: "
					+ particles.keySet().iterator().next().toString());
			swarmNo = swarm;
			setParticles(particles);
			setEvaluate(true);
			running = true;
			complete = false;
			singleParticle = false;
		}
		public EvalParticle(int swarm, Particle particle, int index)
		{
			super("Swarm " + swarm + " Evaluation " + index);
			swarmNo = swarm;
			setParticle(particle, index);
			setEvaluate(true);
			running = true;
			complete = false;
			singleParticle = true;
		}
		public void destroy()
		{
			if (particle != null)
			{
				particle.destroy();
				particle = null;
			}
			if (particles != null)
			{
				for (Iterator<Integer> iterator = particles.keySet()
						.iterator(); iterator.hasNext();)
				{
					Integer key = (Integer) iterator.next();
					particles.get(key).destroy();
				}
				particles.clear();
				particles = null;
			}
		}
		private void evalParticle()
		{
			double result = Double.NaN;

			complete = false;
			evaluate = true;
			result = particle.eval();
			System.out.println("Swarm: " + swarmNo + " Key: "
					+ particle.getTestFunction().getKey() + " Particle: "
					+ particleIndex + " best: " + particle.getBestScore()
					+ " scored: " + result);
			Util.getSwarmui()
					.updateLog(" Processing... Swarm: "
							+ ((swarmNo < 0) ? "Master" : swarmNo)
							+ " Particle: " + particleIndex + " scored");

			synchronized (this)
			{
				complete = true;
				evaluate = false;

				this.notifyAll();
			}
		}
		private void evalParticles()
		{
			double result = Double.NaN;

			complete = false;
			evaluate = true;
			for (Iterator<Integer> iterator = particles.keySet()
					.iterator(); iterator.hasNext();)
			{
				Integer key = (Integer) iterator.next();
				result = particles.get(key).eval();
				System.out.println("Swarm: " + swarmNo + " Particle: " + key
						+ " scored: " + result);
				Util.getSwarmui()
						.updateLog(" Processing... Swarm: "
								+ ((swarmNo < 0) ? "Master" : swarmNo)
								+ " Particle: " + key + " scored");

			}

			synchronized (this)
			{
				complete = true;
				evaluate = false;
				this.notifyAll();
			}
		}
		public void evaluate()
		{
			setEvaluate(true);
		}
		/**
		 * @return the particles
		 */
		public HashMap<Integer, Particle> getParticles()
		{
			return particles;
		}

		/**
		 * @return the complete
		 */
		public boolean isComplete()
		{
			if (complete)
			{
				synchronized (this)
				{
					this.notifyAll();
				}
			}
			return complete;
		}
		/**
		 * @return the evaluate
		 */
		public boolean isEvaluate()
		{
			return evaluate;
		}
		/**
		 * @return the running
		 */
		public boolean isRunning()
		{
			return running;
		}
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			while (running)
			{
				if (evaluate)
				{
					// System.out.println("Starting eval: " + this.getName());

					complete = false;
					if (singleParticle)
						evalParticle();
					else
						evalParticles();
					// System.out.println("Completed eval: " + this.getName());
				}
				synchronized (sync)
				{
					try
					{
						if (!evaluate)
							sync.wait();
					} catch (InterruptedException e)
					{
						// don't report this
						// Log.log(Level.SEVERE, e);
						// e.printStackTrace();
					}
				}
			}
			if (particle != null)
			{
				particle.destroy();
				particle = null;
			}
			if (particles != null)
			{
				for (Iterator<Particle> iterator = particles.values()
						.iterator(); iterator.hasNext();)
				{
					Particle p = (Particle) iterator.next();
					p.destroy();
				}
				particles.clear();
			}

			super.run();

		}
		/**
		 * @param evaluate
		 *            the evaluate to set
		 */
		public synchronized void setEvaluate(boolean evaluate)
		{
			this.complete = !evaluate;
			this.evaluate = evaluate;
			synchronized (sync)
			{
				if (evaluate)
				{
					sync.notifyAll();
				}
			}
		}
		/**
		 * @param particle
		 *            the particle to set
		 * @param index
		 */
		public void setParticle(Particle particle, int index)
		{
			this.particle = particle;
			particleIndex = index;
		}
		/**
		 * @param particles
		 */
		public void setParticles(HashMap<Integer, Particle> particles)
		{
			this.particles = particles;
		}
		/**
		 * @param running
		 *            the running to set
		 */
		public void setRunning(boolean running)
		{
			this.running = running;
			synchronized (this)
			{
				notifyAll();
			}
		}

	}
	private static final String ELAPSED_TIME = " Elapsed Time: ";
	/**
	 * 
	 */
	private static final String EVAL_SB = "Eval SB";

	private static final String EXECUTION_STOPPED_BY_USER = "Execution stopped by user";

	private static final String EXPERIMENT_NO = "Experiment No: ";
	private static final String GENERAL_PSO_PROPERTIES_FILE = "General PSO properties file";
	private static final int LIMITED_PARA_EVAL = 2;
	private static final int PARA_EVAL = 1;
	private static final String PSOPROPS_PROPERTIES = "PSOProps.properties";
	private static final String RUN_COlON = " Run: ";
	private static final int SINGLE_EVAL = 0;
	protected boolean bFinished = false;
	protected boolean bHeirarchy = false;
	protected boolean bMaster = false;
	protected String boundary;
	protected boolean bReset = true;
	protected boolean bRunning = false;
	protected boolean bStop = false;
	protected boolean bStopExecution = false;
	private boolean bStoreDirect;
	protected boolean bSwitchOps = true;
	protected boolean bWait = false;
	protected HashMap<String, EvalParticle> evals = null;
	protected int evalType = SINGLE_EVAL;
	protected Particle gbestparticle = null;
	protected Heirarchy heir = null;
	private int iteration = Integer.MIN_VALUE;
	protected Object masterSync = new Object();
	protected long maxruns = 1;
	protected String mDescription;
	protected long mIterations = 1000;
	// protected Particle gbest;
	protected ArrayList<Particle> mSwarm = null;
	protected int mSwarmNo = 0;
	protected Topology neighbourhood = null;

	protected int noMembers = 10;

	protected OutputResults output;

	private boolean paraeval;

	protected Properties params = null;

	protected boolean passGB = true;

	protected Results results = null;

	protected Random rnd = null;

	protected int run;

	protected Properties runparams = new Properties();

	protected SwarmNet sn = null;
	protected long startTime;
	protected long stepSize;
	protected TestBase testFunction;
	protected Thread timer = null;
	protected double VMax = Double.NaN;

	public BaseSwarm()
	{
		super("Swarm");
		setPriority(MAX_PRIORITY);
		this.neighbourhood = new Star(this);

		params = new Properties();
		runparams = new Properties();
		createParams();
		loadProperties();

	}

	/**
	 * @param o
	 */
	public BaseSwarm(BaseSwarm o)
	{
		super();
		this.bFinished = o.bFinished;
		this.bHeirarchy = o.bHeirarchy;
		this.bMaster = o.bMaster;
		this.boundary = o.boundary;
		this.bStop = o.bStop;
		this.bStopExecution = o.bStopExecution;
		this.bSwitchOps = o.bSwitchOps;
		this.bWait = o.bWait;
		this.bRunning = o.bRunning;
		this.evals = o.evals;
		this.evalType = o.evalType;
		this.gbestparticle = o.gbestparticle;
		this.heir = o.heir;
		this.masterSync = o.masterSync;
		this.maxruns = o.maxruns;
		this.mDescription = o.mDescription;
		this.mIterations = o.mIterations;
		if (o.mSwarm != null)
		{
			this.mSwarm = new ArrayList<Particle>(o.mSwarm.size());
			for (int i = 0; i < this.mSwarm.size(); i++)
				mSwarm.add(i, new Particle(o.mSwarm.get(i)));
		} else
			this.mSwarm = null;
		this.mSwarmNo = o.mSwarmNo;
		if (o.neighbourhood != null)
			this.neighbourhood = (Topology) o.neighbourhood.clone();
		this.noMembers = o.noMembers;
		this.output = o.output;
		if (output != null)
			output = (OutputResults) output.clone();
		this.params = new Properties(o.params);
		this.passGB = o.passGB;
		this.results = o.results;
		if (results != null)
			results = (Results) results.clone();
		this.rnd = o.rnd;
		this.run = o.run;
		this.runparams = new Properties(o.runparams);
		this.startTime = o.startTime;
		this.stepSize = o.stepSize;
		this.testFunction = o.testFunction;
		if (testFunction != null)
			testFunction = (TestBase) testFunction.clone();
		this.timer = o.timer;
		this.VMax = o.VMax;
		this.bReset = o.bReset;
	}

	/**
	 * @param runparams
	 *            run time parameters to set
	 */
	public BaseSwarm(Topology neighbourhood, Properties params,
			Properties runparams)
	{
		super("Swarm");
		this.neighbourhood = neighbourhood;
		this.runparams = runparams;
		this.params = params;
		createParams();
		try
		{
			init();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		}

	}

	/**
	 * add function, implemented so master swarms repel each other
	 * 
	 * @param x
	 * @param y
	 */
	protected double add(double x, double y)
	{
		// this is an easy but potentially confusing fix
		// for the lack of java operator overloading
		double sum = 0;
		if (isMaster() && isSwitchOps())
			sum = x - y;
		else
			sum = x + y;
		return sum;
	}

	protected abstract void afterCalc(int index);

	protected abstract void afterIter(long iteration);

	protected abstract void beforeCalc(int index);

	protected abstract void beforeIter(long iteration);

	/**
	 * @param index
	 * @return Updated Particle
	 * @throws Exception
	 */
	protected abstract Particle calcNew(int index) throws Exception;

	private boolean canGo(long iteration)
	{
		try
		{
			if (bHeirarchy && !bMaster)
			{
				if (iteration != -1 && iteration % stepSize == 0)
				{
					synchronized (masterSync)
					{

						setWait(true);
						recalcGBest();
						heir.getMasterSwarm().setWait(false);
						masterSync.notifyAll();
					}
					// synchronized (childSync)
					synchronized (this)
					{
						try
						{
							while (bWait)
								// childSync.wait(10000);
								this.wait(10000);
						} catch (InterruptedException e)
						{
							// Log.log(Level.SEVERE, new
							// Exception(e.getMessage()
							// + " Iteration:" + iteration, e));
							// e.printStackTrace();
						}
						setWait(false);
						// recalc scores
						// eval();
					}
				}
				if (gbestparticle != null)
				{
					gbestparticle.eval();
					recalcGBest();
				}

				return true;
			} else if (bMaster)
			{
				boolean bFlag;
				setWait(true);
				Vector<BaseSwarm> childswarms = heir.getChildSwarms();

				for (Iterator<BaseSwarm> iterator = childswarms
						.iterator(); iterator.hasNext();)
				{
					BaseSwarm swarm = (BaseSwarm) iterator.next();
					synchronized (swarm)
					{
						if (swarm.isAlive())
						{
							if (passGB && gbestparticle != null)
								swarm.setSwarmsBest(gbestparticle);
							swarm.setWait(false);
							swarm.notifyAll();
						}
					}

				}

				try
				{

					synchronized (masterSync)
					{
						do
						{
							while (bWait)
								masterSync.wait(10000);
							bFlag = true;
							// check that ALL child swarms are waiting
							for (Iterator<BaseSwarm> iterator = heir
									.getChildSwarms().iterator(); iterator
											.hasNext();)
							{
								BaseSwarm swarm = (BaseSwarm) iterator.next();
								if (swarm.isAlive())
									bFlag &= swarm.isWait();
								if (!bFlag)
									break;
							}
						} while (!bFlag && bRunning);
						setWait(false);
					}

				} catch (InterruptedException e)
				{
					// Log.log(Level.SEVERE, new Exception(e.getMessage()
					// + " Iteration:" + iteration, e));
					// e.printStackTrace();
				}
				mSwarm = heir.getChildSwarmBests();
				// eval();
				// singleThreadEval();
				return true;
			} else
				return true;
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return true;
	}

	public Object clone()
	{
		try
		{
			BaseSwarm clone;

			BaseSwarm baseSwarm = this;
			Class<? extends BaseSwarm> class1 = baseSwarm.getClass();
			if (!Modifier.isAbstract(class1.getModifiers()))
			{
				// clone = (BaseSwarm) new BaseSwarm(this);
				Constructor<?> con = class1.getConstructor(new Class[]
				{BaseSwarm.class});
				clone = (BaseSwarm) con.newInstance(new Object[]
				{baseSwarm});
			} else
				throw new CloneNotSupportedException();
			// clone = (BaseSwarm) super.clone(); // jdk6
			// clone = Copy.deepCopy(this);
			clone.setOutput((OutputResults) clone.getOutput().clone());
			return clone;
		} catch (Exception e)
		{
			// This should never happen
			throw new InternalError(e.getCause().getMessage());
		}
	}

	/**
	 * Allows constraints to be specified
	 * 
	 * @throws Exception
	 */
	public abstract boolean constraints() throws Exception;

	protected abstract void copy(BaseSwarm o);

	/**
	 * 
	 */
	protected abstract void createParams();
	/**
	 * 
	 */
	private void destroyevals()
	{
		if (evals != null)
		{
			for (int k = 0; k < evals.size(); k++)
			{
				String key = String.valueOf(k);
				EvalParticle evalParticle = evals.get(key);
				if (evalParticle != null)
				{
					evalParticle.setRunning(false);
					evalParticle.evaluate();
					evalParticle.destroy();
					evals.remove(key);
				}
			}
			evals.clear();
			evals = null;
		}
	}
	/**
	 * 
	 */
	private void destroyswarm()
	{
		if (mSwarm != null)
		{
			for (Iterator<Particle> iterator = mSwarm.iterator(); iterator
					.hasNext();)
			{
				Particle particle = (Particle) iterator.next();
				if (particle != null)
				{
					particle.destroy();
					particle = null;
				}
			}
			mSwarm.clear();
			mSwarm = null;
		}
	}

	protected void eval()
	{
		System.out.println("Evalulating...");
		switch (evalType)
		{
			case PARA_EVAL :
				paraEvalOneParticlePerThread();
				break;
			case LIMITED_PARA_EVAL :
				paraThreadEval();
				break;

			default :
				singleThreadEval();
				break;
		}
		System.out.println("Completed Evalulation");
	}
	/**
	 * @return Returns the mDescription.
	 */
	public String getDescription()
	{
		return mDescription;
	}

	/**
	 * @return the evalType
	 */
	public int getEvalType()
	{
		return evalType;
	}
	/**
	 * @return Particle with gBest best score
	 */
	public Particle getGBest()
	{
		if (gbestparticle == null && mSwarm != null)
		{
			if (testFunction.isMinimised())
			{
				gbestparticle = testFunction.min(mSwarm);
			} else
			{
				gbestparticle = testFunction.max(mSwarm);
			}
		}
		// else if (mSwarm ==null)
		// System.err.println("mSmarm is null");
		return gbestparticle;

	}
	/**
	 * @return the heir
	 */
	public Heirarchy getHeir()
	{
		return heir;
	}

	/**
	 * @return the iteration
	 */
	public int getIteration()
	{
		return iteration;
	}

	/**
	 * @return Returns the current swarm scores, preceded by gbest
	 */
	public HashMap<Long, ArrayList<Double>> getIterSwarmScores()
	{
		HashMap<Long, ArrayList<Double>> scores = new HashMap<Long, ArrayList<Double>>();

		ArrayList<Double> pScore = new ArrayList<Double>();
		pScore.add(0, Double.valueOf(neighbourhood.getGBest().getBestScore()));
		pScore.add(1, Double.valueOf(neighbourhood.getGBest().getBestScore()));

		scores.put(-1L, pScore);
		for (int i = 0; i < mSwarm.size(); i++)
		{
			pScore = new ArrayList<Double>();
			pScore.add(0, Double.valueOf(mSwarm.get(i).getBestScore()));
			pScore.add(1, Double.valueOf(mSwarm.get(i).getCurrentScore()));
			scores.put((long) i, pScore);
		}
		return scores;

	}
	/**
	 * @return Returns the mIterations.
	 */
	public long getMaxIterations()
	{
		return mIterations;
	}
	/**
	 * @return the run
	 */
	public long getMaxRuns()
	{
		return maxruns;
	}
	/**
	 * @return Returns the nbest.
	 */
	public Particle getNbest(int index)
	{
		return neighbourhood.getNbest(index);
	}
	/**
	 * @return Returns the neighbourhood.
	 */
	public Topology getNeighbourhood()
	{
		return neighbourhood;
	}

	/**
	 * @return Returns the noMembers.
	 */
	public int getNoMembers()
	{
		return noMembers;
	}
	/**
	 * @return the output
	 */
	public OutputResults getOutput()
	{
		return output;
	}
	/**
	 * @param key
	 *            The parameter name
	 * @return the value of the parameter requested or NaN if the parameter does
	 *         not exist
	 */
	public double getParam(String key)
	{
		if (this.params.containsKey(key))
			return Double.valueOf(this.params.getProperty(key)).doubleValue();
		else
			return Double.NaN;

	}
	public Properties getParams()
	{
		return params;
	}
	/**
	 * @return Returns the results.
	 */
	public Results getResults()
	{
		return results;
	}
	/**
	 * @return the run
	 */
	public int getRun()
	{
		return run;
	}
	/**
	 * @return the run time parameters
	 */
	public Properties getRunParams()
	{
		return this.runparams;
	}

	/**
	 * @return the sn
	 */
	public SwarmNet getSn()
	{
		return sn;
	}

	/**
	 * @return Returns a clone of mSwarm.
	 */
	// public ArrayList<Particle> getSwarmMembers()
	// {
	// ArrayList<Particle> clone = new ArrayList<Particle>(mSwarm);
	// if (clone == null || clone.size() == 0)
	// try
	// {
	// throw new Exception(
	// "Failed to obtain clone of swarm: swarm is "
	// + mSwarm.size());
	// } catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return clone;
	// }
	/**
	 * @return the mSwarm
	 */
	public ArrayList<Particle> getSwarmMembers()
	{
		ArrayList<Particle> ret = null;
		if (mSwarm != null)
		{
			ret = mSwarm;
			if (ret == null || ret.size() == 0)
				try
				{
					throw new Exception(
							"Failed to obtain a copy of swarm: swarm is "
									+ mSwarm.size());
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return ret;

	}
	/**
	 * @return the mSwarmNo
	 */
	public long getSwarmNo()
	{
		return mSwarmNo;
	}
	/**
	 * @return Returns the testFunction.
	 */
	public TestBase getTestFunction()
	{
		return testFunction;
	}
	protected void init() throws Exception
	{
		// this.params = new Properties(params);
		if (runparams != null)
		{
			bStoreDirect = (runparams.getProperty("storedirect", "false")
					.equalsIgnoreCase("true"));
			rnd = RandGen.getRand(runparams.getProperty("random"));
			paraeval = runparams.getProperty("paraeval", "false")
					.equalsIgnoreCase("true");
			bHeirarchy = heir != null; // this shouldn't be necessary
			// if (bHeirarchy && bMaster)
			// if (bHeirarchy)
			// regardless of setting dont use parallel evaluations for the
			// master swarm
			if (!paraeval || bMaster)
				evalType = SINGLE_EVAL;
			else
				evalType = PARA_EVAL;
			String testFunctionString = null;
			if (runparams.containsKey("objective"))
				testFunctionString = (String) runparams.get("objective");

			long iter = Long.parseLong(runparams.getProperty("iterations",
					Long.toString(mIterations)));
			if (bMaster && runparams.containsKey("heirarchyiterations"))
			{
				mIterations = Long.parseLong(
						runparams.getProperty("heirarchyiterations"));
				if (mIterations > iter)
					mIterations = iter;
				stepSize = (long) Math.ceil(iter / mIterations);
			} else if (runparams != null && runparams.containsKey("iterations"))
				mIterations = iter;
			if (runparams != null && runparams.containsKey("swarmno"))
				mSwarmNo = Integer
						.parseInt(runparams.getProperty("swarmno", "0"));
			passGB = runparams.getProperty("heirarchypassgb", "true")
					.equalsIgnoreCase("true");
			boundary = runparams.getProperty("boundary", "wrap");

			if (testFunctionString == null && testFunction == null)
				throw new Exception("No objective function has been provided");
			else
			{
				if (testFunctionString != "" && testFunction == null)
				{
					try
					{
						testFunction = (TestBase) Class
								.forName(testFunctionString)
								.getDeclaredConstructor().newInstance();

					} catch (InstantiationException e)
					{
						Log.log(Level.SEVERE, e);
						e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						Log.log(Level.SEVERE, e);
						e.printStackTrace();
					} catch (ClassNotFoundException e)
					{
						Log.log(Level.SEVERE, e);
						e.printStackTrace();
					}
				}
				if (params.containsKey("dimensions"))
					testFunction.setMDimension(
							Integer.parseInt(params.getProperty("dimensions")));
				testFunction.init();
			}

			try
			{

				Random random = RandGen
						.getRand(runparams.getProperty("random"));
				if (!bMaster)
				{
					noMembers = Integer.parseInt(runparams
							.getProperty("particles", "40").toString());
					// make sure we have the number of particles
					runparams.setProperty("particles",
							Integer.toString(noMembers));
					int i = 0;
					if (bReset)
					{
						recalcGBest();
						destroyevals();
						destroyswarm();
						Runtime.getRuntime().gc();
						mSwarm = new ArrayList<Particle>(noMembers);
						i = 0;
						Properties p = neighbourhood.getParams();
						Constructor<?> con = neighbourhood.getClass()
								.getConstructor(new Class[]
								{BaseSwarm.class});
						Topology topology = (Topology) con
								.newInstance(new Object[]
								{this});
						topology.setParams(p);
						neighbourhood = (Topology) topology;
					} else
						i = mSwarm.size();
					for (; i < noMembers; i++)
						mSwarm.add(new Particle(runparams, testFunction, random,
								i));

				} else
				{
					heir.init();
					mSwarm = heir.getChildSwarmBests();
					noMembers = mSwarm.size();
				}
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
			if (results != null)
				results.clearQueue();
			results = new Results(this, output);
		}
	}
	/**
	 * @return the bFinished
	 */
	public boolean isFinished()
	{
		return bFinished;
	}
	/**
	 * @return the bHeirarchy
	 */
	public boolean isHeirarchy()
	{
		return bHeirarchy;
	}
	/**
	 * @return the bMaster
	 */
	public boolean isMaster()
	{
		return bMaster;
	}
	/**
	 * @return the bRunning
	 */
	public boolean isRunning()
	{
		return bRunning;
	}
	/**
	 * @return the bSwitchOps
	 */
	public boolean isSwitchOps()
	{
		return bSwitchOps;
	}
	/**
	 * @return the bWait
	 */
	public boolean isWait()
	{
		return bWait;
	}
	public void load(Properties props)
	{
		if (props != null)
		{
			params.clear();
			createParams();
			Enumeration<Object> keys = params.keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				if (props.containsKey(key))
					params.setProperty(key, props.getProperty(key));
			}
		}
	}

	public void loadProperties()
	{
		if (this.params == null)
			this.params = new Properties();
		try
		{
			this.params.load(new FileInputStream(PSOPROPS_PROPERTIES));
		} catch (FileNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
			try
			{
				this.params.store(new FileOutputStream(PSOPROPS_PROPERTIES),
						PSOPROPS_PROPERTIES + " - Automatically created");
			} catch (FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
	}

	public void loadProperties(String fname)
	{
		if (this.params == null)
			this.params = new Properties();
		try
		{
			this.params.load(new FileInputStream(fname));
		} catch (FileNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
			try
			{
				this.params.store(new FileOutputStream(PSOPROPS_PROPERTIES),
						PSOPROPS_PROPERTIES + " - Automatically created");
			} catch (FileNotFoundException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
	}

	protected void paraEvalOneParticlePerThread()
	{
		try
		{
			if (evals == null)
			{
				evals = new HashMap<String, EvalParticle>(mSwarm.size());

				for (int i = 0; i < mSwarm.size(); i++)
				{

					EvalParticle evalParticle = evals.get(String.valueOf(i));

					evalParticle = new EvalParticle(mSwarmNo, mSwarm.get(i), i);
					evals.put(String.valueOf(i), evalParticle);

					evalParticle.setPriority(MAX_PRIORITY);
					// evals[i].setParticle(mSwarm.get(i), i);
					// evals[i].evaluate();
					evalParticle.start();

				}
			}
			Set<String> set = evals.keySet();
			for (Iterator<String> iterator = set.iterator(); iterator
					.hasNext();)
			{
				String key = (String) iterator.next();
				if (evals.get(key).isAlive())
				{
					evals.get(key).evaluate();
				}

			}

			for (Iterator<String> iterator = set.iterator(); iterator
					.hasNext();)
			{
				String key = (String) iterator.next();
				EvalParticle evalParticle = evals.get(key);
				synchronized (evalParticle)
				{
					if (!evalParticle.isComplete() && evalParticle.isAlive())
						evalParticle.wait();
				}

			}
			recalcGBest();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Para Eval: " + e.getMessage(), e));
			e.printStackTrace();
		}
	}
	protected void paraThreadEval()
	{
		int threads = 0;
		try
		{
			if (evals == null)
			{
				evals = new HashMap<String, EvalParticle>();

				EvalParticle evalParticle;

				HashMap<Integer, Particle> particles = new HashMap<Integer, Particle>();
				for (int j = 0; j < mSwarm.size(); j++)
				{
					particles.put(threads, mSwarm.get(j));
					if (j % 10 == 0)
					{
						evalParticle = new EvalParticle(mSwarmNo, particles);
						evals.put(String.valueOf(threads), evalParticle);
						particles = new HashMap<Integer, Particle>();
						evalParticle.setPriority(MAX_PRIORITY);
						evalParticle.start();
						threads++;
					}
				}

			}

			for (Iterator<String> iterator = evals.keySet().iterator(); iterator
					.hasNext();)

			{
				String i = iterator.next();
				EvalParticle evalParticle = evals.get(i);
				if (evalParticle != null && evalParticle.isAlive())
				{
					evalParticle.evaluate();
				}

			}
			for (Iterator<String> iterator = evals.keySet().iterator(); iterator
					.hasNext();)
			{
				String particle = (String) iterator.next();
				EvalParticle evalParticle = evals.get(particle);
				synchronized (evalParticle)
				{
					if (!evalParticle.isComplete() && evalParticle.isAlive())
						evalParticle.wait();
				}

			}
			recalcGBest();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE,
					new Exception("Para Eval: " + e.getMessage(), e));
			e.printStackTrace();
		}
	}
	protected Particle posAdjust(Particle particle)
	{
		if (boundary.equalsIgnoreCase("wrap"))
			Boundary.wrap(particle, testFunction);
		else if (boundary.equalsIgnoreCase("stick"))
			Boundary.stick(particle, testFunction);
		else if (boundary.equalsIgnoreCase("bounce"))
			Boundary.bounce(particle, testFunction);
		return particle;
	}
	/**
	 * 
	 */
	public void recalcGBest()
	{
		gbestparticle = null;
		getGBest();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		bRunning = true;
		bFinished = false;
		bStopExecution = false;
		for (run = 0; run < maxruns && !bStopExecution; run++)
		{

			try
			{
				final JProgressBar progressBar = Util.getSwarmui()
						.getProgressBar();
				if (bMaster || !bHeirarchy)
				{
					progressBar.setValue(0);
					progressBar.setString("Initialising Run: " + run);
				}
				bStop = false;

				String msg = EXECUTION_STOPPED_BY_USER;
				try
				{
					if (constraints())
					{
						Util.getSwarmui()
								.updateLog(" Initialising swarm: " + mSwarmNo);
						init();
						results.setLog(!bStoreDirect);
						if (bMaster || !bHeirarchy)
						{
							results.startOutput();
							startTime = Calendar.getInstance()
									.getTimeInMillis();
							progressBar.setMinimum(0);
							progressBar.setMaximum((int) mIterations - 1);
							if (timer == null)
							{
								timer = new Thread("Timer")
								{
									public void run()
									{
										while (!bStop && !bFinished)
										{
											try
											{
												sleep(1000);
											} catch (InterruptedException e)
											{
												// TODO Auto-generated catch
												// block
												// e.printStackTrace();
											}

											progressBar.setString(EXPERIMENT_NO
													+ output.getExpNum()
													+ RUN_COlON + run
													+ ELAPSED_TIME
													+ Util.formatRuntime(
															Calendar.getInstance()
																	.getTimeInMillis()
																	- startTime));

										}
									}
								};
								timer.setPriority(MIN_PRIORITY);
								timer.start();
							}

						}
						Util.getSwarmui().updateLog(" Initialising swarm: "
								+ mSwarmNo
								+ " Evaluating Initial Positions and scores");
						if (bMaster)
						{
							heir.startChildren();
							bWait = true;
						} else if (bHeirarchy)
						{
							stepSize = mIterations / heir.getMasterIterations();
							stepSize = (stepSize <= 0) ? 1 : stepSize;
						}
						canGo(-1);
						eval();
						// store initial scores & positions with iteration index
						// -1
						Util.getSwarmui().updateLog(" Initialising swarm: "
								+ mSwarmNo
								+ " Storing Initial Positions and scores");
						results.store(run, -1);
						for (iteration = 0; iteration < mIterations && !bStop
								&& !bStopExecution; iteration++)
						{

							if (bMaster || !bHeirarchy)
								progressBar.setValue((int) iteration);

							int swarmSize = mSwarm.size();
							if ((noMembers != swarmSize)
									&& (noMembers + 1 != swarmSize))
								throw new Exception(
										"Swarm size does not match the number of members: noMembers="
												+ noMembers + " swarm size ="
												+ swarmSize);
							Util.getSwarmui()
									.updateLog(" Processing....Run: " + run
											+ " Iteration: " + iteration
											+ " Swarm: " + mSwarmNo);

							canGo(iteration);

							beforeIter(iteration);
							neighbourhood.setIteration(iteration);
							for (int j = 0; j < swarmSize && !bStop; j++)
							{
								mSwarm.get(j).setFuncSpecific("", true);
								beforeCalc(j);
								try
								{
									Util.getSwarmui()
											.updateLog(" Processing....Run: "
													+ run + " Iteration: "
													+ iteration + " Swarm: "
													+ mSwarmNo + " Particle: "
													+ j + " of "
													+ (swarmSize - 1));
									Particle particle = calcNew(j);
									particle = posAdjust(particle);
									// mSwarm.set(j, particle);
									if (particle
											.getBestScore() == Double.POSITIVE_INFINITY
											|| particle
													.getBestScore() == Double.NEGATIVE_INFINITY
											|| particle
													.getBestScore() == testFunction
															.getMGlobalOptima())
									{
										msg = "Execution Terminated: Iteration("
												+ iteration + ") swarm("
												+ mSwarmNo + ") run(" + run
												+ ") particle(" + j
												+ ") score = "
												+ Double.toString(
														particle.getBestScore())
												+ " Global Optima = "
												+ testFunction
														.getMGlobalOptima();
										bStop = true;
									}
									Util.getSwarmui().updateLog(
											"pos " + j + "=" + mSwarm.get(j));
									Util.getSwarmui().updateLog("Cur " + j + "="
											+ mSwarm.get(j).getCurrentScore());
									Util.getSwarmui().updateLog("Best " + j
											+ "="
											+ mSwarm.get(j).getBestScore());
								} catch (Exception e)
								{
									Log.log(Level.SEVERE, e);
									e.printStackTrace();
								}
								afterCalc(j);
							}
							Util.getSwarmui().updateLog(" Processing....Run: "
									+ run + " Iteration: " + iteration
									+ " Swarm: " + mSwarmNo + " Scoring...");

							eval();
							Util.getSwarmui().updateLog(" Processing....Run: "
									+ run + " Iteration: " + iteration
									+ " Swarm: " + mSwarmNo + " Scored");

							Particle pbest = neighbourhood.getGBest();
							Util.getSwarmui().updateLog(
									"gbest= " + pbest.getBestScore());
							Util.getSwarmui().updateLog(" Processing....Run: "
									+ run + " Iteration: " + iteration
									+ " Swarm: " + mSwarmNo + " Logging "
									+ (runparams.getProperty("logAll", "true")
											.equalsIgnoreCase("true")
													? "All"
													: "Only Best Particle")
									+ " Results");
							results.store(run, iteration);
							afterIter(iteration);
							Runtime.getRuntime().gc();
							// if ((testFunction.isMinimised() && pbest
							// .getBestScore() <= testFunction.threshold())
							// || (!testFunction.isMinimised() && pbest
							// .getBestScore() <= testFunction
							// .threshold()))
							if ((!bHeirarchy && !bMaster
									&& testFunction.isMinimised()
									&& pbest.getBestScore() <= testFunction
											.threshold()))
							{
								bStop = true;
								msg = "Swarm Best has reached termination threshold: Run ="
										+ run + " iteration =" + iteration
										+ " pBest Score ="
										+ pbest.getBestScore()
										+ " pBest Value ="
										+ pbest.getPosition().toString();
							}
							if (!bHeirarchy && !bMaster
									&& pbest.absAvgRateChange() <= testFunction
											.threshold())
							{
								bStop = true;
								msg = "Swarm Best has reached average rate of change threshold: Run ="
										+ run + " iteration =" + iteration
										+ " pBest Score ="
										+ pbest.getBestScore()
										+ " pBest Value ="
										+ pbest.getPosition().toString();

							}
						}
					}
					if (bMaster || !bHeirarchy)
					{
						results.updateNotes(Util.formatRuntime(
								Calendar.getInstance().getTimeInMillis()
										- startTime));
						if (timer != null)
						{
							timer.interrupt();
							timer = null;
						}
					}
					if (bStop)
					{
						Util.getSwarmui().updateLog("Cancelling Run...");
						results.updateNotes(msg);
					} else
						results.updateNotes("Swarm: " + mSwarmNo
								+ " completed successfully");

					results.setEndTime();
					if (bMaster)
						heir.stopChildren();

				} catch (Exception e1)
				{

					Log.log(Level.SEVERE, new Exception(
							"A constaints violation has occurred whilst running "
									+ this.getDescription() + " "
									+ e1.getMessage(),
							e1));
					e1.printStackTrace();
				}
			} catch (Exception e)
			{

				Log.log(Level.SEVERE,
						new Exception("An Exception has occured during run "
								+ run + " of " + maxruns + this.getDescription()
								+ " " + e.getMessage(), e));
			}
		}
		if (!bStop)
			bFinished = true;
		stopRunning();
		bRunning = false;
		super.run();
	}
	/**
	 * @param description
	 *            The mDescription to set.
	 */
	protected void setDescription(String description)
	{
		mDescription = description;
	}
	/**
	 * @param evalType
	 *            the evalType to set
	 */
	public void setEvalType(int evalType)
	{
		this.evalType = evalType;
	}
	/**
	 * @param heir
	 *            the heir to set
	 */
	public void setHeir(Heirarchy heir)
	{
		this.heir = heir;
		params.setProperty("heirarchy", heir.getClass().getName());
		bHeirarchy = true;
	}
	/**
	 * @param master
	 *            the bMaster to set
	 */
	public void setMaster(boolean master)
	{
		bMaster = master;
		params.setProperty("master", Boolean.toString(master));
	}

	/**
	 * @param iterations
	 *            The mIterations to set.
	 */
	public void setMaxIterations(long iterations)
	{
		mIterations = iterations;
		params.put("iterations", mIterations);
	}
	/**
	 * @param run
	 *            the run to set
	 */
	public void setMaxRuns(long runs)
	{
		this.maxruns = runs;
	}

	/**
	 * @param neighbourhood
	 *            The neighbourhood to set.
	 */
	public void setNeighbourhood(Topology neighbourhood)
	{
		this.neighbourhood = neighbourhood;
		this.neighbourhood.setSwarm(this);
	}

	/**
	 * @param noMembers
	 *            The noMembers to set.
	 */
	public void setNoMembers(int noMembers)
	{
		this.noMembers = noMembers;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(OutputResults output)
	{
		this.output = output;
		this.output.setSwarm(this);
	}

	/**
	 * @param key
	 *            The parameter name
	 * @param value
	 *            The value to set, must be a double
	 */
	public void setParam(String key, double value)
	{
		if (this.params.containsKey(key))
		{
			this.params.setProperty(key, String.valueOf(value));
		}
	}

	public synchronized void setParams(Properties params)
	{
		this.params = params;
	}

	public synchronized void setParticle(Particle particle)
	{
		int size = mSwarm.size();
		boolean bFlag = false;
		for (int i = 0; i < size; i++)
		{
			if (mSwarm.get(i).getIdentityNumber() == particle
					.getIdentityNumber())
			{
				mSwarm.set(i, particle);
				bFlag = true;
				break;
			}

		}
		if (!bFlag)
			mSwarm.add(particle);
	}

	/**
	 * @param bRunning
	 *            the bRunning to set
	 */
	public void setRunning(boolean bRunning)
	{
		this.bRunning = bRunning;
	}

	/**
	 * @param params
	 *            ; the run time parameters to be set
	 */
	public synchronized void setRunParams(Properties params)
	{
		this.runparams = params;

	}

	/**
	 * @param sn
	 *            the sn to set
	 */
	public void setSn(SwarmNet sn)
	{
		this.sn = sn;
	}

	/**
	 * @param swarm
	 *            The mSwarm to set.
	 */
	public synchronized void setSwarm(ArrayList<Particle> swarm)
	{
		mSwarm = swarm;
	}
	/**
	 * @param swarmNo
	 *            the mSwarmNo to set
	 */
	public void setSwarmNo(int swarmNo)
	{
		mSwarmNo = swarmNo;
		setName(getName() + ": " + swarmNo);
	}
	public synchronized void setSwarmsBest(Particle sb)
	{
		Particle sharedparticle = sb;
		int noMemsplusone = noMembers + 1;
		int sharedindex = Integer.MIN_VALUE;
		int size = mSwarm.size();
		if (size == noMemsplusone)
			// sharedparticle = mSwarm.get(noMembers);
			// cannot guarantee order is preserved
			for (int i = 0; i < size; i++)
			{
				if (mSwarm.get(i).getIdentityNumber() == noMembers)
				{
					sharedparticle = mSwarm.get(i);
					sharedindex = i;
					break;
				}

			}

		if (sharedparticle.equals(gbestparticle))
		{
			Particle clone;
			try
			{
				clone = (Particle) sb.clone();
				clone.setIdentityNumber(noMembers);
				clone.setTestFunction(testFunction);
				if (testFunction.isMinimised())
					clone.setPbest(Double.MAX_VALUE);
				else
					clone.setPbest(Double.MIN_VALUE);
				clone.eval();
				if (sharedindex != Integer.MIN_VALUE)
					mSwarm.set(sharedindex, clone);
				else
					mSwarm.add(clone); // need to create space
				if (evals != null)
				{
					EvalParticle evalParticle;
					if (evals.containsKey(EVAL_SB))
					{
						evalParticle = evals.get(EVAL_SB);
						evalParticle.setParticle(clone,
								clone.getIdentityNumber());
					} else
					{
						evalParticle = new EvalParticle(mSwarmNo, clone,
								clone.getIdentityNumber());
						evals.put(EVAL_SB, evalParticle);
						evalParticle.start();
					}
				}

			} catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * @param bSwitchOps
	 *            the bSwitchOps to set
	 */
	public void setSwitchOps(boolean bSwitchOps)
	{
		this.bSwitchOps = bSwitchOps;
	}
	/**
	 * @param testFunction
	 *            The testFunction to set.
	 */
	public void setTestFunction(String testFunctionName)
	{
		try
		{
			Class<?> class1 = Class.forName(testFunctionName);
			TestBase testFunction = (TestBase) class1.getDeclaredConstructor()
					.newInstance();
			this.testFunction = testFunction;
		} catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param testFunction
	 *            The testFunction to set.
	 */
	public void setTestFunction(TestBase testFunction)
	{
		this.testFunction = testFunction;
	}

	/**
	 * @param wait
	 *            the bWait to set
	 */
	public synchronized void setWait(boolean wait)
	{
		bWait = wait;
		if (!bWait)
			synchronized (this)
			{
				this.notifyAll();
			}
		else if (!bMaster && bHeirarchy)
			synchronized (masterSync)
			{
				heir.getMasterSwarm().setWait(false);
				masterSync.notifyAll();
			}
	}

	protected void singleThreadEval()
	{
		double result;
		int size = mSwarm.size();
		for (int i = 0; i < size; i++)
		{
			Particle particle = (Particle) mSwarm.get(i);
			Util.getSwarmui()
					.updateLog(" Processing... Swarm: "
							+ ((bMaster) ? "Master" : mSwarmNo) + " Particle: "
							+ i + " scored");
			result = particle.eval();

			// result = testFunction.Objective(particle);
			particle.setCurrentScore(result);
			if (testFunction.isMinimised())
			{
				if (result < particle.getBestScore())
				{
					particle.setPbest(result);
				}
			} else
			{
				if (result > particle.getBestScore())
				{
					particle.setPbest(result);
				}
			}
			// particle.setFuncSpecific(particle.getFuncSpecificReader());
			System.out.println("Swarm: " + getSwarmNo() + " Key: "
					+ particle.getTestFunction().getKey() + " Index: " + i
					+ " Particle: " + particle.getIdentityNumber() + " scored: "
					+ result);

			mSwarm.set(i, particle);

		}
		recalcGBest();
	}

	public void stopRunning()
	{
		bStop = bStopExecution = true;
		bRunning = false;
		if (bMaster)
		{
			if (heir != null)
				for (Iterator<BaseSwarm> iterator = heir.getChildSwarms()
						.iterator(); iterator.hasNext();)
				{
					BaseSwarm swarm = (BaseSwarm) iterator.next();
					if (swarm != null)
						synchronized (swarm)
						{

							if (passGB && gbestparticle != null)
								swarm.setSwarmsBest(gbestparticle);
							swarm.setWait(false);
							swarm.stopRunning();
							swarm.interrupt();
							swarm = null;
						}
				}
			heir.getChildSwarms().clear();
		} else
		{
			if (heir != null)
			{
				synchronized (masterSync)
				{
					heir.getMasterSwarm().setWait(false);
					heir.getMasterSwarm().setRunning(false);
					masterSync.notifyAll();
				}
			}
//			if(gbestparticle!= null)
//				gbestparticle.getTestFunction().
			destroyswarm();
		}
		destroyevals();
		if (bMaster || !bHeirarchy)
			results.stopQueue();
	}

	public void storeProperties()
	{

		try
		{
			this.params.store(new FileOutputStream(PSOPROPS_PROPERTIES),
					GENERAL_PSO_PROPERTIES_FILE);
		} catch (FileNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
	}

	public void storeProperties(String fname, String comments)
	{

		try
		{
			this.params.store(new FileOutputStream(fname), comments);
		} catch (FileNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
	}

	/**
	 * subtract function, implemented so master swarms repel each other
	 * 
	 * @param x
	 * @param y
	 */
	protected double subtract(double x, double y)
	{
		// this is an easy but potentially confusing fix
		// for the lack of java operator overloading
		double sum = 0;
		if (isMaster() && isSwitchOps())
			sum = x + y;
		else
			sum = x - y;
		return sum;
	}

	protected double vmaxAdjust(int i, double v)
	{
		if (VMax > 0)
		{
			v = v > VMax ? VMax : v;
			v = v < -VMax ? -VMax : v;
		} else if (params.getProperty("VMax").compareToIgnoreCase("xmax") == 0)
		{
			v = v > testFunction.getMax(i) ? testFunction.getMax(i) : v;
			v = v < testFunction.getMin(i) ? testFunction.getMin(i) : v;
		}
		return v;
	}

}