package org.mltestbed.heuristics.transformers.generic;
import java.util.concurrent.ConcurrentHashMap;
public class Correlations
{
	public static void main(String[] args)
	{
		test();
	}

	private static void test()
	{
		Correlations embed = new Correlations();
		embed.add("the", "off", 2.0d);
		embed.add("the", "road", 1.0d);
		embed.add("the", "off", 4.0d);
	}
	private ConcurrentHashMap<Object, ConcurrentHashMap<Object, Double>> emb = new ConcurrentHashMap<Object, ConcurrentHashMap<Object, Double>>();

	public void add(Object k1, Object k2, Double v)
	{
		ConcurrentHashMap<Object, Double> e;
		double dist = 0.0d;

		v = Math.abs(v);

		if (emb.containsKey(k1) && emb.get(k1).containsKey(k2))
		{
			dist = emb.get(k1).get(k2).doubleValue();
		}
		if (emb.containsKey(k1))
			e = emb.get(k1);
		else
		{
			e = new ConcurrentHashMap<Object, Double>();
			emb.put(k1, e);
		}
		e.put(k2, matric(dist + v));
	}
	public void addBi(Object k1, Object k2, Double v)
	{
		add(k1, k2, v);
		add(k2, k1, v);
	}
	/**
	 * @return the emb
	 */
	public ConcurrentHashMap<Object, ConcurrentHashMap<Object, Double>> getEmb()
	{
		return emb;
	}
	private double matric(double v)
	{
		return (1 - (1 / (1 + v)));
	}
	public void reset()
	{
		if (emb != null)
			emb.clear();
		else
			emb = new ConcurrentHashMap<Object, ConcurrentHashMap<Object, Double>>();
	}
}
