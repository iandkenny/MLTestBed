/**
 * 
 */
package org.mltestbed.data;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.heuristics.PSO.Heirarchy.Heirarchy;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.topologies.Topology;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.net.SwarmNet;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Ian Kenny
 * 
 */
public class Experiment implements Serializable
{

	private static final String EXPERIMENT_HAS_NOT_BEEN_RUN_YET = "Experiment has not been runs yet";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param in
	 * @return Document
	 */
	public static Document createDoc(InputStream in)
	{
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try
		{
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try
		{
			if (in != null)
				doc = builder.parse(in);
			else
			{
				doc = builder.newDocument();
				doc.appendChild(doc.createElement("EXPERIMENT"));
			}
		} catch (SAXException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return doc;
	}
	private Document doc;
	private long ExpNo = Long.MIN_VALUE;
	private boolean hasRun = false;
	private Heirarchy heir = null;
	private Topology neighbourhood = null;
	private String notes;
	private TestBase objective = null;
	private boolean running;
	private Properties runParams;
	private long runs = 0;
	private SwarmNet sn = null;
	private OutputResults storeData;
	private BaseSwarm swarm = null;

	private Properties testFuncParams;
	private Node addNodeWithProperties(Node top, Node node, Properties props)
	{
		Node ret = null;
		String key = null;
		try
		{
			if (node != null)
			{
				Set<Object> keys = props.keySet();
				for (Iterator<Object> iterator = keys.iterator(); iterator
						.hasNext();)
				{
					key = ((String) iterator.next()).trim();
					if (!key.equals("") && key != null)
					{
						Node newNode = doc.createElement(key);
						newNode.appendChild(
								doc.createTextNode(props.getProperty(key)));
						node.appendChild(newNode);
					}
				}
				ret = top.appendChild(node);
			}
		} catch (DOMException e)
		{
			Log.log(Level.SEVERE,
					new Exception(e.getMessage() + " key =" + key, e));
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * @param node
	 * @param str
	 * @param text
	 * @return
	 */
	private Node addNodeWithText(Node node, String str, String text)
	{
		Node ret = null;
		if (node != null)
		{
			ret = node.appendChild(doc.createElement(str)
					.appendChild(doc.createTextNode(text)));
		}
		return ret;
	}
	/**
	 * @return the expNo
	 */
	public long getExpNo()
	{
		return ExpNo;
	}
	/**
	 * @return the heir
	 */
	public Heirarchy getHeir()
	{
		return heir;
	}
	/**
	 * @return Returns the neighbourhood.
	 */
	public Topology getNeighbourhood()
	{
		return neighbourhood;
	}
	/**
	 * @return the notes
	 */
	public String getNotes()
	{
		return notes;
	}
	/**
	 * @return Returns the problem.
	 */
	public TestBase getObjective()
	{
		return objective;
	}
	/**
	 * @return swarm results
	 * @throws Exception
	 *             if experiment has been runs
	 */
	public Results getResults() throws Exception
	{
		if (!hasRun)
			throw new Exception(EXPERIMENT_HAS_NOT_BEEN_RUN_YET);
		return swarm.getResults();
	}

	/**
	 * @return Returns the runParams.
	 */
	public Properties getRunParams()
	{
		return runParams;
	}
	/**
	 * @return the runs
	 */
	public long getRuns()
	{
		return runs;
	}
	/**
	 * @return the storeData
	 */
	public OutputResults getStoreData()
	{
		return storeData;
	}

	/**
	 * @return Returns the swarm.
	 */
	public BaseSwarm getSwarm()
	{
		return swarm;
	}

	/**
	 * @return Textual description of experiment
	 * @throws Exception
	 */
	public String getTextDescription() throws Exception
	{
		String description = "";
		if (swarm == null || neighbourhood == null || objective == null)
			throw new Exception(
					"Generating Text description: The Experiment object has not been setup properly");
		description = "Apply " + swarm.getDescription()
				+ " with the following parameter settings:\n";
		Properties swarmProps = swarm.getParams();
		Enumeration<Object> keys = swarmProps.keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			String property = swarmProps.getProperty(key);
			if (property != null)
				description += key + "=" + property + "\n";
		}

		description += " using " + neighbourhood.getDescription()
				+ " with the following parameter settings:\n";
		Properties neighbourhoodParams = neighbourhood.getParams();
		keys = neighbourhoodParams.keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			description += key + "=" + neighbourhoodParams.getProperty(key)
					+ "\n";
		}
		description += " to " + objective.getDescription();
		if (!objective.getParams().isEmpty())
		{
			description += " with the following parameter settings:\n";
			keys = objective.getParams().keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				String property = objective.getParams().getProperty(key);
				if (property != null)
					description += key + "=" + property + "\n";
			}

		}
		if (heir != null)
		{
			description += " using the heirarchy "
					+ heir.getMasterSwarm().getDescription();
			description += " with the following parameter settings:\n";
			keys = heir.getMasterSwarm().getParams().keys();
			while (keys.hasMoreElements())
			{
				String key = (String) keys.nextElement();
				String property = heir.getMasterSwarm().getParams()
						.getProperty(key);
				if (property != null)
					description += key + "=" + property + "\n";
			}

		}

		description += " with the following runtime parameter settings:\n";
		keys = runParams.keys();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			String property = runParams.getProperty(key);
			if (property != null)
				description += key + "=" + property + "\n";
		}
		return description;
	}
	/**
	 * @return XML String
	 * @throws Exception
	 */
	public String getXMLDescription() throws Exception
	{
		if (swarm == null || neighbourhood == null || objective == null)
			throw new Exception(
					"Generating XML description: The Experiment object has not been setup properly");
		doc = createDoc(null);
		Node node = doc.getDocumentElement();

		node.appendChild(doc.createComment(
				DateFormat.getDateInstance().format(new Date())));
		addNodeWithText(node, "swarm", swarm.getDescription());
		addNodeWithProperties(node, doc.createElement("swarmprops"),
				swarm.getParams());

		addNodeWithText(node, "neighbourhood", neighbourhood.getDescription());
		addNodeWithProperties(node, doc.createElement("neighbourhoodprops"),
				neighbourhood.getParams());

		addNodeWithText(node, "objective", objective.getDescription());
		addNodeWithProperties(node, doc.createElement("objprops"),
				objective.getParams());

		addNodeWithProperties(node, doc.createElement("runparams"), runParams);
		if (heir != null)
		{
			addNodeWithText(node, "heir",
					heir.getMasterSwarm().getDescription());
			// Properties heirProps = heir.getMasterSwarm().getParams();
			// out.reset();
			// heirProps.storeToXML(out, null);
			addNodeWithProperties(node, doc.createElement("heirprops"),
					heir.getMasterSwarm().getParams());
		}

		return XMLtoString(doc).toString();
	}
	/**
	 * @return XML parameter string
	 */
	public String getXMLParameters()
	{
		Properties buf = new Properties();
		String ret = "";
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (heir != null)
			{
				Properties heirProp = new Properties();
				Properties params = heir.getParams();
				if (params != null)
					heirProp.putAll(params);
				params = heir.getMasterSwarm().getTestFunction().getParams();
				if (params != null)
					heirProp.putAll(params);
				heirProp.storeToXML(baos, "heirarchy properties");
				buf.setProperty("heirarchyproperties",
						"\"" + baos.toString() + "\"");
			}
			baos.flush();
			baos.close();
			Properties params = getRunParams();
//			buf.putAll(params);
			Set<String> keys = params.stringPropertyNames();
			for (Iterator<String> iterator = keys.iterator(); iterator
					.hasNext();)
			{
				String key = (String) iterator.next();
				buf.setProperty(key, params.getProperty(key));

			}
			params = swarm.getParams();
			keys = params.stringPropertyNames();
			for (Iterator<String> iterator = keys.iterator(); iterator
					.hasNext();)
			{
				String key = (String) iterator.next();
				buf.setProperty(key, params.getProperty(key));
			}
			params = swarm.getNeighbourhood().getParams();
			keys = params.stringPropertyNames();
			for (Iterator<String> iterator = keys.iterator(); iterator
					.hasNext();)
			{
				String key = (String) iterator.next();
				buf.setProperty(key, params.getProperty(key));
			}
			buf.putAll(swarm.getTestFunction().getParams());
			keys = params.stringPropertyNames();
			for (Iterator<String> iterator = keys.iterator(); iterator
					.hasNext();)
			{
				String key = (String) iterator.next();
				buf.setProperty(key, params.getProperty(key));
			}

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			buf.storeToXML(output,
					DateFormat.getDateInstance().format(new Date()));
			output.flush();
			ret = output.toString();
			output.close();

		} catch (IOException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return ret;
	}

	private void init()
	{
		if (runParams != null)
		{
			runs = Integer.parseInt(runParams.getProperty("runs", "1"));
		}
	}
	/**
	 * @return the hasRun
	 */
	public boolean isHasRun()
	{
		return hasRun;
	}

	/**
	 * @return the running
	 */
	public boolean isRunning()
	{
		return running;
	}
	/**
	 * creates the notes table record for a new experiment
	 */
	private synchronized void newExp()
	{

		try
		{
			storeData = new OutputResults(this);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
	}
	/**
	 * @return swarm as arraylist
	 */
	public ArrayList<Particle> rerun()
	{
		ArrayList<Particle> localSwarm = new ArrayList<Particle>();
		//
		// Statement stmt = getStatement();
		// String sqlWhere;
		// if (expNo == null)
		// sqlWhere = " where Expnum IN (Select Max(Expnum) From notebook)";
		// else
		// sqlWhere = " where Expnum =" + expNo.toString();
		// ResultSet rs = stmt.executeQuery(SQLString + sqlWhere);
		//
		return localSwarm;
	}
	public void runExp()
	{
		init();
		swarm.setRunParams(runParams);
		try
		{

			objective.setParams(testFuncParams);
			// dont init here this needs to be done per run
			// objective.init();
			if (swarm.getRunParams().containsKey("dimensions"))
				objective.setMDimension(Integer.parseInt(
						swarm.getRunParams().getProperty("dimensions")));

		} catch (NumberFormatException e1)
		{
			Log.log(Level.SEVERE, e1);
			e1.printStackTrace();
		} catch (Exception e1)
		{
			Log.log(Level.SEVERE, e1);
			e1.printStackTrace();
		}
		swarm.setTestFunction(objective);
		// swarm.setParams(params)
		swarm.setMaxRuns(getRuns());
		swarm.setPriority(Thread.MAX_PRIORITY);
		running = true;
		if (heir == null)
		{
			swarm.setNeighbourhood(neighbourhood);
			do
			{
				newExp();
			} while (storeData == null);
			swarm.setOutput(storeData);
			if (!swarm.isAlive())
				swarm.start();
			try
			{
				swarm.join();

			} catch (InterruptedException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		} else
		{
			BaseSwarm masterSwarm = heir.getMasterSwarm();
			BaseSwarm baseChild = heir.getBaseChild();
			// masterSwarm.setTestFunction(objective);
			masterSwarm.setRunParams(runParams);
			masterSwarm.setPriority(Thread.MAX_PRIORITY);
			baseChild.setRunParams(runParams);
			baseChild.setPriority(Thread.MAX_PRIORITY);
			do
			{
				newExp();
			} while (storeData == null);

			masterSwarm.setOutput(storeData);
			baseChild.setOutput(new OutputResults(storeData));
			masterSwarm.setNeighbourhood(neighbourhood);
			baseChild.setNeighbourhood((Topology) neighbourhood.clone());
			masterSwarm.setMaxRuns(runs);
			baseChild.setMaxRuns(1); // if its hierarchy we only want the child
										// swarm to run once since the
										// subsequent runs need new child swarms

			if (!masterSwarm.isAlive())
				masterSwarm.start();
			try
			{
				masterSwarm.join();

			} catch (InterruptedException e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}

		}
		stopRunning();
		hasRun = true;

	}

	/**
	 * @param expNo the expNo to set
	 */
	public void setExpNo(long expNo)
	{
		ExpNo = expNo;
	}

	public void setHeir(Heirarchy heir)
	{
		this.heir = heir;
	}

	/**
	 * @param neighbourhood
	 *            The neighbourhood to set.
	 */
	public void setNeighbourhood(Topology neighbourhood)
	{
		this.neighbourhood = (Topology) neighbourhood;
	}

	/**
	 * @param notes
	 *            the notes to set
	 */
	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	/**
	 * @param objective
	 *            The problem to set.
	 */
	public void setObjective(TestBase objective)
	{
		this.objective = objective;
		if (swarm != null)
			swarm.setTestFunction(objective);
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
	}

	// private void test (){
	// try {
	//
	//
	// DocumentBuilderFactory docBuilderFactory =
	// DocumentBuilderFactory.getDeclaredConstructor().newInstance();
	// DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
	// Document doc = docBuilder.parse (new File("test.xml"));
	//
	// // normalize text representation
	// doc.getDocumentElement ().normalize ();
	// System.out.println ("Root element of the doc is " +
	// doc.getDocumentElement().getNodeName());
	//
	//
	// NodeList listOfPersons = doc.getElementsByTagName("channel");
	// int totalPersons = listOfPersons.getLength();
	// Log.getLogger().info("Total no of people : " + totalPersons);
	// for(int s=0; s<listOfPersons.getLength() ; s++){
	//
	//
	// Node firstPersonNode = listOfPersons.item(s);
	// if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){
	//
	//
	// Element firstPersonElement = (Element)firstPersonNode;
	//
	// //-------
	// NodeList firstNameList =
	// firstPersonElement.getElementsByTagName("first");
	// Element firstNameElement = (Element)firstNameList.item(0);
	//
	// NodeList textFNList = firstNameElement.getChildNodes();
	// Log.getLogger().info("First Name : " +
	// ((Node)textFNList.item(0)).getNodeValue().trim());
	//
	// //-------
	// NodeList lastNameList = firstPersonElement.getElementsByTagName("last");
	// Element lastNameElement = (Element)lastNameList.item(0);
	//
	// NodeList textLNList = lastNameElement.getChildNodes();
	// Log.getLogger().info("Last Name : " +
	// ((Node)textLNList.item(0)).getNodeValue().trim());
	//
	// //----
	// NodeList ageList = firstPersonElement.getElementsByTagName("age");
	// Element ageElement = (Element)ageList.item(0);
	//
	// NodeList textAgeList = ageElement.getChildNodes();
	// Log.getLogger().info("Age : " +
	// ((Node)textAgeList.item(0)).getNodeValue().trim());
	//
	// //------
	//
	//
	// }//end of if clause
	//
	//
	// }//end of for loop with s var
	//
	//
	// }catch (SAXParseException err) {
	// System.out.println ("** Parsing error" + ", line " + err.getLineNumber ()
	// + ", uri " + err.getSystemId ());
	// Log.getLogger().info(" " + err.getMessage ());
	//
	// }catch (SAXException e) {
	// Exception x = e.getException ();
	// ((x == null) ? e : x).printStackTrace ();
	//
	// }catch (Throwable t) {
	// t.printStackTrace ();
	// }
	// //System.exit (0);
	//
	// }//end of main

	/**
	 * @param runParams
	 *            The runParams to set.
	 */
	public void setRunParams(Properties runParams)
	{
		this.runParams = runParams;
		init();
	}
	/**
	 * @param maxruns
	 *            the runs to set
	 */
	public void setRuns(long run)
	{
		this.runs = run;
	}
	/**
	 * @param swarm
	 *            The swarm to set.
	 */
	public void setSwarm(BaseSwarm swarm)
	{
		this.swarm = (BaseSwarm) swarm;
	}
	public void setSwarmNet(SwarmNet sn)
	{
		this.sn = sn;

	}
	public void setTestParams(Properties testFuncParams)
	{
		this.testFuncParams = testFuncParams;

	}
	public void startRunning()
	{
		new Thread()
		{

			public void run()
			{
				if (!running)
					runExp();

			}
		}.start();

	}
	public void stopRunning()
	{
		if (heir != null)
		{
			BaseSwarm masterSwarm = heir.getMasterSwarm();
			masterSwarm.stopRunning();
			masterSwarm.interrupt();
			heir.setMasterSwarm(null);
		} else
		{
			if (swarm != null)
			{
				swarm.stopRunning();
				swarm.interrupt();
			}
		}
		heir = null;
		swarm = null;
		running = false;
		Runtime.getRuntime().gc();
	}

	@SuppressWarnings("unused")
	private Document StringtoXML(String xmlStr)
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
			Document doc = builder
					.parse(new InputSource(new StringReader(xmlStr)));
			return doc;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	private String XMLtoString(Document doc)
	{
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try
		{
			transformer = tf.newTransformer();
			// below code to remove XML declaration
			// transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
			// "yes");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			String output = writer.getBuffer().toString();
			return output;
		} catch (TransformerException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
