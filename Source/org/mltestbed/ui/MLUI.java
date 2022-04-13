/** Created on 14-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.mltestbed.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.mltestbed.data.Experiment;
import org.mltestbed.heuristics.PSO.BaseSwarm;
import org.mltestbed.heuristics.PSO.ClassicPSO;
import org.mltestbed.heuristics.PSO.Heirarchy.Heirarchy;
import org.mltestbed.testFunctions.HeirarchyTestBase;
import org.mltestbed.testFunctions.TestBase;
import org.mltestbed.topologies.Topology;
import org.mltestbed.util.Boundary;
import org.mltestbed.util.Log;
import org.mltestbed.util.Particle;
import org.mltestbed.util.RandGen;
import org.mltestbed.util.RunExperiments;
import org.mltestbed.util.Util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ian Kenny
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class MLUI extends JPanel
		implements
			PropertyChangeListener,
			ActionListener,
			ListSelectionListener,
			TreeSelectionListener
{
	private static final String ABOUT_VERSION = "Machine Learning Test Bed\n Copyright Ian Kenny (2006-2022)\n Version 2.5.0";

	private static final String ADD = "Add>>";

	// Strings for the labels
	private static final String APPLY = "Apply";

	private static final String CANCEL_EXPERIMENT = "Cancel Experiment";

	private static final String CHOOSE_A_NEIGHBOURHOOD = "Choose a Neighbourhood:";

	private static final String CONNECTION_STRING = "Connection String:";

	private static final String DATA_SOURCE = "Data Source";

	private static final String DB_CONNECTION = "DB Connection";

	private static final String DEFAULT_CONNECTION_STRING = "SwarmExperiments"; // old name kept for compatibility

	private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

	private static final String DEFAULT_URL = "jdbc:odbc:";

	private static final String DRIVER = "Driver:";

	private static final String EDIT = "Edit ";

	private static final String ENABLE_HEIRARCHY = "Enable Heirarchy:";

	private static final String ENTER_SOME_EXPERIMENT_NOTES = "Enter some experiment notes";

	private static final String evaluationsString = "Evaluations: ";

	private static final String EXIT = "Exit";

	private static final String EXPERIMENT_TREE = "Available Experiments";

	private static final String EXPERIMENTAL_RUNS = "Experimental Runs";

	private static HashMap<String, Experiment> experiments = new HashMap<String, Experiment>();

	private static final String EXPERIMENTS = "Experiments";

	private static final String EXPERIMENTS_COMPLETED = "Experiments Completed";

	private static final String EXPERIMENTS_TO_RUN = "Experiments to Run";

	private static JTree experimentTree;

	private static final String functString = "Objective Function: ";

	private static final String HEIRARCHY_METHOD_LIST = "Heirarchy Method List: ";

	private static final String HEIRARCHY_SETTINGS = "Heirarchy Settings: ";

	private static final String HEIRARCHY_TAB = "Heirarchy";

	private static final String HEURISTIC_METHOD_LIST = "Heuristic Method List: ";

	private static final String HEURISTIC_SETTINGS = "Heuristic Settings: ";

	private static final String HTAB = "Heuristics";

	private static final String HTTAB = "Heirarchy Test Functions";

	private static final String INITIALISING_EXPERIMENTS = "Initialising Experiments...";

	private static final String iterationsString = "Swarm iterations: ";

	private static final String LINESEPARATOR = java.lang.System
			.getProperty("line.separator");

	private static final String LOAD_EXPERIMENT_DATA = "Load Experiment Data";

	private static final String LOADED_PARAMETERS_FOR_EXPERIMENT = "Loaded parameters for experiment ";

	private static final String LOG = "Log Progress";

	private static final String MASTER_SWARM_ITERATIONS = "Master Swarm Iterations";

	private static final String NEIGHBOURHOOD = "Neighbourhood";

	private static final String NEIGHBOURHOOD_PARAMETER_LIST = "Neighbourhood Settings:";

	private static final String NO_EXPERIMENTS_ARE_RUNNING = "No Experiments Are Running";

	private static final String NO_RESULTS_ARE_SELECTED_FOR_DISPLAY = "No results are Selected for display";

	private static final String NUMBER_OF_EXPERIMENTAL_RUNS = "Number of Experimental Runs:";

	private static final String NUMBER_OF_SWARMS = "Number of Swarms";

	private static final String ONLY_STORE_BEST_SCORES = "Only Store Best Scores:";

	private static final String OVERALL_PROGRESS = "Overall Progress";

	private static String particlesString = "Particles: ";

	private static final String PASS_SWARMS_BEST_TO_CHILD_SWARMS = "Pass Swarms' Best to Child Swarms";

	private static final String PASSWORD = "Password:";

	private static final String PLEASE_SELECT_AN_EXPERIMENT_TO_REMOVE = "Please select an experiment to remove";

	private static final String PROGRESS_LOG = "Progress Log:";

	private static final String RANDOM_NUMBER_GENERATOR = "Random Number Generator:";

	private static final String REMOVE = "Remove";

	private static final String RESULTS_LOG = "Results Log:";

	private static final String RUNTIME_PROPERTIES = "MLTestBedRuntime.properties";

	private static final long serialVersionUID = 1L;

	private static final String START_EXPERIMENT = "Start Experiment";

	private static final String STARTING_EXPERIMENTS = "Starting Experiments...";

	private static final String STORE_RESULTS_DIRECT_TO_DATABASE = "Store Results Direct to Database";

	private static final String SWITCH_OPERATORS_IN_VELOCITY = "Switch Operators in Velocity:";

	private static String Test_Function_Settings = "Objective Function Settings: ";

	private static final String TTAB = "Test Functions";

	private static final String TYPE_OF_BOUNDARY_HANDLING = "Type of Boundary Handling:";

	private static final String TYPE_OF_VELOCITY_INITIALISATION = "Type of Velocity Initialisation:";
	private static final String URL = "URL:";

	private static final String USE_FIPS_NEIGHBOURHOOD = "Use FIPS Neighbourhood";

	private static final String USE_MEMORY_BUFFERS = "Use Memory Buffers:";

	private static final String USE_PARALLEL_PROCESSING_TO_EVALUATE_PARTICLES = "Use Parallel Processing to Evaluate Particles:";

	private static final String USER_ID = "User ID:";

	private JMenuItem aboutMenuItem;

	private JButton addButton;

	private JButton applyButton;

	private JComboBox<?> boundaryHandlingCombo;

	private JLabel boundaryHandlingLabel;

	private JTabbedPane cardPane;

	private JFormattedTextField connectionBox;

	private JFormattedTextField driverBox;

	private JFormattedTextField editFuncParamsBox;

	private JLabel editFuncParamsLabel;

	private JFormattedTextField editFuncSourceBox;

	private JFormattedTextField editHeirarchyBox;

	private JLabel editHeirarchyLabel;

	private JFormattedTextField editHeirFuncParamsBox;

	private JLabel editHeirFuncParamsLabel;

	private JFormattedTextField editHeuristicBox;

	private JLabel editHeuristicLabel;

	private JFormattedTextField editTopologyBox;

	private JLabel editTopologyLabel;

	private JLabel enableHeirarchy;

	private long evaluations;

	private JFormattedTextField evaluationsField;

	private JLabel evaluationsLabel;

	private JButton exitButton;

	private DefaultMutableTreeNode expNode;

	private DefaultMutableTreeNode expTreeRoot = new DefaultMutableTreeNode(
			EXPERIMENTS);

	private JMenuItem FDMMenuItem;

	private JFrame frame;

	private JLabel funcLabel;

	private JMenuItem GasSpecMenuItem;

	private JCheckBox heirarchyCheckBox;

	private JLabel heirarchydimensionsLabel;

	private JComboBox<String> heirarchyFuncCombo;

	private JLabel heirarchyFuncLabel;

	private JLabel heirarchyLabel;

	private Properties heirarchyParams;

	private JLabel heirarchyParamsLabel;

	private JLabel heirarchyPassGBLabel;

	private JLabel heirarchySwitchOpsLabel;

	private JComboBox<String> heirarchyTestFuncCombo;

	private Properties heirarchyTestFuncParams;

	private JLabel heirarchyUseFIPSLabel;

	private JComboBox<String> heuristicFuncCombo;

	private JLabel heuristicLabel;

	private Properties heuristicParams;

	private JLabel heuristicParamsLabel;

	// heuristic classes
	private Map<String, String> heuristics = new HashMap<String, String>();

	private JMenuItem ingberMenuItem;

	private boolean isLogging = false;

	// Values for the fields
	private long iterations = 3000;

	// Fields for org.mltestbed.data entry
	private JFormattedTextField iterationsField;

	// Formats to format and parse numbers
	private NumberFormat iterationsFormat;

	// Labels to identify the fields
	private JLabel iterationsLabel;

	private JButton loadButton;

	private JCheckBox logCheck;

	private JLabel loggingLabel;

	private JComboBox<String> neighbourhoodCombo;

	private JCheckBox paraCheck;

	private JLabel paraLabel;

	private JList<String> paramHeirarchyLBox;

	private JScrollPane paramHeirarchyScroll;

	private JList<String> paramHeirarchyTestFuncLBox;

	private JScrollPane paramHeirarchyTestScroll;

	private JList<String> paramHeuristicLBox;

	private JScrollPane paramHeuristicScroll;

	private JList<String> paramTestFuncLBox;

	private JScrollPane paramTestScroll;

	private JList<String> paramTopologyLBox;

	private JScrollPane paramTopologyScroll;

	private long particles = 40;

	private JFormattedTextField particlesField;

	private JLabel particlesLabel;

	private JCheckBox passGBCheckBox;

	// function classes
	private JFormattedTextField passwordField;

	private NumberFormat percentFormat;

	private JProgressBar progressBar;

	private JLabel progressLabel;

	private JTextArea progressLog;

	private JCheckBox progressLoggable;

	private JComboBox<?> randCombo;

	private JLabel randLabel;

	private JButton removeButton;

	private DefaultMutableTreeNode resNode;

	private JTable resultTable;

	private ResultTableModel resultTableModal;

	private JTree resultTree;

	private DefaultMutableTreeNode resultTreeRoot;

	private RunExperiments runExperiments;

	private boolean runningExperiment = false;

	private Properties runparams = new Properties();

	private JFormattedTextField runsField;

	private JLabel runsLabel;

	private JMenuItem SEMGMenuItem;

	private JButton startButton;

	private JCheckBox storeCheck;

	private JFormattedTextField swarmsField;

	private JFormattedTextField swarmsIterationsField;

	private JLabel swarmsIterationsLabel;

	private JLabel swarmsLabel;

	private JCheckBox switchOpsCheckBox;

	private JComboBox<String> testFuncCombo;

	private Properties testFuncParams;

	private JLabel testFuncParamsLabel;

	private Map<String, String> testfunctions = new HashMap<String, String>();

	private Map<String, String> topologies = new HashMap<String, String>();

	private Properties topologyParams;

	private JLabel treeLabel;

	private ExpData uet;

	private JFormattedTextField urlBox;

	private JCheckBox useFIPSCheckBox;

	private JCheckBox useMemBuffersCheck;

	private JLabel useMemBuffersLabel;

	private JFormattedTextField userIdField;

	private JComboBox<String> velInitCombo;

	private JLabel velocityInitLabel;

	private JMenuItem MinasPassageMenuItem;

	/**
	 * @param frame
	 */
	public MLUI(JFrame frame)
	{
		super(new BorderLayout());
		Main.createLog();

		new Util(this);
		try
		{
			runparams.load(new FileInputStream(RUNTIME_PROPERTIES));
		} catch (FileNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (IOException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		}
		this.frame = frame;
		JMenuBar menuBar = new JMenuBar();
		// Build the first menu.
		JMenu menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.addActionListener(this);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");

		// JMenuItems
		ingberMenuItem = new JMenuItem("Ingber Data Import", KeyEvent.VK_I);
		ingberMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		ingberMenuItem.addActionListener(this);
		ingberMenuItem.getAccessibleContext()
				.setAccessibleDescription("Imports Ingber Data");
		menu.add(ingberMenuItem);
		FDMMenuItem = new JMenuItem("FDM Data Import", KeyEvent.VK_F);
		FDMMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));
		FDMMenuItem.addActionListener(this);
		FDMMenuItem.getAccessibleContext()
				.setAccessibleDescription("Imports FDM Data");
		menu.add(FDMMenuItem);

		SEMGMenuItem = new JMenuItem("SEMG Data Import", KeyEvent.VK_S);
		SEMGMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		SEMGMenuItem.addActionListener(this);
		SEMGMenuItem.getAccessibleContext()
				.setAccessibleDescription("Imports SEMG Data");
		menu.add(SEMGMenuItem);

		GasSpecMenuItem = new JMenuItem("Gas Spectrometry Data Import",
				KeyEvent.VK_G);
		GasSpecMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.ALT_MASK));
		GasSpecMenuItem.addActionListener(this);
		GasSpecMenuItem.getAccessibleContext()
				.setAccessibleDescription("Imports Gas Spectrometry Data");
		menu.add(GasSpecMenuItem);
		
		MinasPassageMenuItem = new JMenuItem("Minas Passage Data Import",
				KeyEvent.VK_M);
		MinasPassageMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.ALT_MASK));
		MinasPassageMenuItem.addActionListener(this);
		MinasPassageMenuItem.getAccessibleContext()
				.setAccessibleDescription("Minas Passage Data");
		menu.add(MinasPassageMenuItem);


		menu.add(new JSeparator());
		aboutMenuItem = new JMenuItem("About Box", KeyEvent.VK_A);
		aboutMenuItem.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		aboutMenuItem.addActionListener(this);
		aboutMenuItem.getAccessibleContext()
				.setAccessibleDescription("Displays the About Box");
		menu.add(aboutMenuItem);
		menuBar.add(menu);

		frame.setJMenuBar(menuBar);
		// org.mltestbed.heuristics = new HashMap<String, String>();
		// topologies = new HashMap<String, String>();
		setUpFormats();
		evaluations = computeEvals(iterations, particles, evaluations);
		paramHeuristicLBox = new JList<String>(new DefaultListModel<String>());
		paramHeuristicLBox.setVisibleRowCount(3);
		paramHeuristicLBox.setBorder(BorderFactory
				.createLineBorder(paramHeuristicLBox.getForeground()));
		paramHeuristicLBox.addListSelectionListener(this);
		paramHeuristicScroll = new JScrollPane(paramHeuristicLBox);
		paramHeuristicScroll.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paramHeuristicScroll.setPreferredSize(new Dimension(300, 100));

		paramHeirarchyLBox = new JList<String>(new DefaultListModel<String>());
		paramHeirarchyLBox.setVisibleRowCount(3);
		paramHeirarchyLBox.setBorder(BorderFactory
				.createLineBorder(paramHeirarchyLBox.getForeground()));
		paramHeirarchyLBox.addListSelectionListener(this);
		paramHeirarchyScroll = new JScrollPane(paramHeirarchyLBox);
		paramHeirarchyScroll.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		paramHeirarchyScroll.setPreferredSize(new Dimension(300, 100));
		editHeuristicLabel = new JLabel(EDIT);

		editHeuristicBox = new JFormattedTextField();
		editHeuristicBox.setValue("");
		// editHeuristicBox.setColumns(10);
		editHeuristicBox.addPropertyChangeListener("value", this);
		// editHeuristicBox.addKeyListener(this);

		new JLabel(DATA_SOURCE);

		editFuncSourceBox = new JFormattedTextField();
		editFuncSourceBox.setValue("");
		// editHeuristicBox.setColumns(10);
		editFuncSourceBox.addPropertyChangeListener("value", this);

		editFuncParamsLabel = new JLabel(EDIT);

		editFuncParamsBox = new JFormattedTextField();
		editFuncParamsBox.setValue("");
		// editHeuristicBox.setColumns(10);
		editFuncParamsBox.addPropertyChangeListener("value", this);

		editHeirFuncParamsLabel = new JLabel(EDIT);

		editHeirFuncParamsBox = new JFormattedTextField();
		editHeirFuncParamsBox.setValue("");
		// editHeuristicBox.setColumns(10);
		editHeirFuncParamsBox.addPropertyChangeListener("value", this);

		editHeirarchyLabel = new JLabel(EDIT);
		editHeirarchyBox = new JFormattedTextField();
		editHeirarchyBox.setValue("");
		// editHeirarchyBox.setColumns(10);
		editHeirarchyBox.addPropertyChangeListener("value", this);
		// editHeirarchyBox.addKeyListener(this);

		paramTopologyLBox = new JList<String>(new DefaultListModel<String>());
		paramTopologyLBox.setVisibleRowCount(3);
		paramTopologyLBox.setBorder(BorderFactory
				.createLineBorder(paramTopologyLBox.getForeground()));
		paramTopologyLBox.addListSelectionListener(this);
		paramTopologyScroll = new JScrollPane(paramTopologyLBox);

		paramTopologyScroll.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		editTopologyLabel = new JLabel(EDIT);
		editTopologyBox = new JFormattedTextField();
		editTopologyBox.setValue("");
		// editTopologyBox.setColumns(10);
		editTopologyBox.addPropertyChangeListener("value", this);
		// editTopologyBox.addKeyListener(this);

		// Create the labels.
		iterationsLabel = new JLabel(iterationsString);
		particlesLabel = new JLabel(particlesString);
		evaluationsLabel = new JLabel(evaluationsString);
		testFuncParamsLabel = new JLabel(Test_Function_Settings);
		heirarchydimensionsLabel = new JLabel(Test_Function_Settings);
		funcLabel = new JLabel(functString);
		heirarchyFuncLabel = new JLabel(functString);
		velocityInitLabel = new JLabel(TYPE_OF_VELOCITY_INITIALISATION);
		boundaryHandlingLabel = new JLabel(TYPE_OF_BOUNDARY_HANDLING);
		runsLabel = new JLabel(NUMBER_OF_EXPERIMENTAL_RUNS);
		randLabel = new JLabel(RANDOM_NUMBER_GENERATOR);
		paraLabel = new JLabel(USE_PARALLEL_PROCESSING_TO_EVALUATE_PARTICLES);
		useMemBuffersLabel = new JLabel(USE_MEMORY_BUFFERS);

		heuristicLabel = new JLabel(HEURISTIC_METHOD_LIST);
		heuristicParamsLabel = new JLabel(HEURISTIC_SETTINGS);

		enableHeirarchy = new JLabel(ENABLE_HEIRARCHY);
		swarmsLabel = new JLabel(NUMBER_OF_SWARMS);
		swarmsIterationsLabel = new JLabel(MASTER_SWARM_ITERATIONS);
		heirarchyLabel = new JLabel(HEIRARCHY_METHOD_LIST);
		heirarchyParamsLabel = new JLabel(HEIRARCHY_SETTINGS);
		heirarchySwitchOpsLabel = new JLabel(SWITCH_OPERATORS_IN_VELOCITY);
		heirarchyPassGBLabel = new JLabel(PASS_SWARMS_BEST_TO_CHILD_SWARMS);
		heirarchyUseFIPSLabel = new JLabel(USE_FIPS_NEIGHBOURHOOD);

		// Create the text fields and set them up.
		iterationsField = new JFormattedTextField(iterationsFormat);
		iterationsField.setValue(iterations);
		iterationsField.setColumns(10);
		iterationsField.addPropertyChangeListener("value", this);

		particlesField = new JFormattedTextField(iterationsFormat);
		particlesField.setValue(particles);
		particlesField.setColumns(10);
		particlesField.addPropertyChangeListener("value", this);

		evaluationsField = new JFormattedTextField();
		evaluationsField.setValue(evaluations);
		evaluationsField.setColumns(10);
		evaluationsField.setEditable(false);
		evaluationsField.addPropertyChangeListener("value", this);

		velInitCombo = new JComboBox<String>(
				Particle.getSupportedVelocityInit());
		boundaryHandlingCombo = new JComboBox<String>(
				Boundary.getSupportedBoundaries());

		runsField = new JFormattedTextField(iterationsFormat);
		runsField.setValue(Long.valueOf(runparams.getProperty("runs", "1")));
		runsField.setColumns(10);
		runsField.addPropertyChangeListener("value", this);

		randCombo = new JComboBox<Object>(RandGen.getSupportedText());
		randCombo.setEditable(false);
		randCombo.addPropertyChangeListener("value", this);

		useMemBuffersCheck = new JCheckBox();
		useMemBuffersCheck.setSelected(false);
		useMemBuffersCheck.addPropertyChangeListener("value", this);

		paraCheck = new JCheckBox();
		paraCheck.setSelected(false);
		paraCheck.addPropertyChangeListener("value", this);

		paramTestFuncLBox = new JList<String>(new DefaultListModel<String>());
		paramTestFuncLBox.setVisibleRowCount(3);
		paramTestFuncLBox.setBorder(BorderFactory
				.createLineBorder(paramTestFuncLBox.getForeground()));
		paramTestFuncLBox.addListSelectionListener(this);
		paramTestFuncLBox.addPropertyChangeListener("value", this);

		paramHeirarchyTestFuncLBox = new JList<String>(
				new DefaultListModel<String>());
		paramHeirarchyTestFuncLBox.setVisibleRowCount(3);
		paramHeirarchyTestFuncLBox.setBorder(BorderFactory
				.createLineBorder(paramHeirarchyTestFuncLBox.getForeground()));
		paramHeirarchyTestFuncLBox.addListSelectionListener(this);
		paramHeirarchyTestFuncLBox.addPropertyChangeListener("value", this);

		testFuncCombo = populateTestCombo("testFuncCombo");
		testFuncCombo.setEditable(false);
		testFuncCombo.setForeground(Color.red);
		testFuncCombo.addActionListener(this);
		// populateTestFunctParams(testFuncCombo);

		heirarchyTestFuncCombo = populateTestCombo("heirarchytestFuncCombo");
		heirarchyTestFuncCombo.setEditable(false);
		heirarchyTestFuncCombo.setForeground(Color.orange);
		heirarchyTestFuncCombo.addActionListener(this);
		// populateTestFunctParams(heirarchytestFuncCombo);

		heuristicFuncCombo = populateHeuristicCombo();
		heuristicFuncCombo.setEditable(false);
		heuristicFuncCombo.setForeground(Color.blue);
		heuristicFuncCombo.addActionListener(this);

		heirarchyFuncCombo = populateHeuristicCombo();
		heirarchyFuncCombo.setEditable(false);
		heirarchyFuncCombo.setForeground(Color.blue);
		heirarchyFuncCombo.addActionListener(this);

		heirarchyCheckBox = new JCheckBox();
		heirarchyCheckBox.setMnemonic(KeyEvent.VK_H);
		heirarchyCheckBox.addActionListener(this);
		heirarchyCheckBox.setSelected(false);

		switchOpsCheckBox = new JCheckBox();
		switchOpsCheckBox.setMnemonic(KeyEvent.VK_S);
		switchOpsCheckBox.addActionListener(this);
		switchOpsCheckBox.setSelected(true);

		passGBCheckBox = new JCheckBox();
		passGBCheckBox.setMnemonic(KeyEvent.VK_P);
		passGBCheckBox.addActionListener(this);
		passGBCheckBox.setSelected(true);

		useFIPSCheckBox = new JCheckBox();
		useFIPSCheckBox.setMnemonic(KeyEvent.VK_F);
		useFIPSCheckBox.addActionListener(this);
		useFIPSCheckBox.setSelected(true);

		swarmsField = new JFormattedTextField();
		swarmsField.setValue("auto");
		swarmsField.setColumns(10);
		swarmsField.setEditable(true);
		swarmsField.addPropertyChangeListener("value", this);

		swarmsIterationsField = new JFormattedTextField();
		swarmsIterationsField.setValue(1000);
		swarmsIterationsField.setColumns(10);
		swarmsIterationsField.setEditable(true);
		swarmsIterationsField.addPropertyChangeListener("value", this);

		neighbourhoodCombo = populateNeighbourhoodCombo();
		neighbourhoodCombo.setEditable(false);
		neighbourhoodCombo.setForeground(Color.magenta);
		neighbourhoodCombo.addActionListener(this);

		// Tell accessibility tools about label/textfield pairs.
		iterationsLabel.setLabelFor(iterationsField);
		particlesLabel.setLabelFor(particlesField);
		evaluationsLabel.setLabelFor(evaluationsField);
		testFuncParamsLabel.setLabelFor(paramTestFuncLBox);
		heirarchydimensionsLabel.setLabelFor(paramHeirarchyTestFuncLBox);
		funcLabel.setLabelFor(testFuncCombo);
		heirarchyFuncLabel.setLabelFor(heirarchyTestFuncCombo);
		heuristicLabel.setLabelFor(heuristicFuncCombo);
		heirarchyLabel.setLabelFor(heirarchyFuncCombo);
		editHeuristicLabel.setLabelFor(editHeuristicBox);
		editTopologyLabel.setLabelFor(editTopologyBox);
		heuristicParamsLabel.setLabelFor(paramHeuristicScroll);
		velocityInitLabel.setLabelFor(velInitCombo);
		boundaryHandlingLabel.setLabelFor(boundaryHandlingCombo);
		runsLabel.setLabelFor(runsField);
		randLabel.setLabelFor(randCombo);
		paraLabel.setLabelFor(paraCheck);
		useMemBuffersLabel.setLabelFor(useMemBuffersCheck);
		enableHeirarchy.setLabelFor(heirarchyCheckBox);
		heirarchySwitchOpsLabel.setLabelFor(switchOpsCheckBox);
		heirarchyPassGBLabel.setLabelFor(passGBCheckBox);

		// Set Mnemonics for labels
		heuristicLabel.setDisplayedMnemonic(KeyEvent.VK_H);
		editHeuristicLabel.setDisplayedMnemonic(KeyEvent.VK_E);
		editTopologyLabel.setDisplayedMnemonic(KeyEvent.VK_E);
		heuristicParamsLabel.setDisplayedMnemonic(KeyEvent.VK_S);
		enableHeirarchy.setDisplayedMnemonic(KeyEvent.VK_H);
		heirarchySwitchOpsLabel.setDisplayedMnemonic(KeyEvent.VK_S);
		heirarchyPassGBLabel.setDisplayedMnemonic(KeyEvent.VK_P);

		// Lay out the labels in a panel.
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;

		JPanel functionPane = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		functionPane.add(funcLabel, c);
		c.gridy++;
		functionPane.add(testFuncParamsLabel, c);
		c.gridy++;
		functionPane.add(editFuncParamsLabel, c);
		// c.gridy++;
		// functionPane.add(editFuncSourceLabel, c);
		c.gridx++;
		c.gridy = 0;

		// Layout the text fields in a panel.

		functionPane.add(testFuncCombo, c);
		c.gridy++;
		paramTestScroll = new JScrollPane(paramTestFuncLBox);
		paramTestScroll = populateTestFunctParams(testFuncCombo);
		paramTestScroll.setPreferredSize(new Dimension(200, 100));
		// c.gridx++;
		// heuristicPane.add(paramHeuristicScroll, c);

		functionPane.add(paramTestScroll, c);
		c.gridy++;
		functionPane.add(editFuncParamsBox, c);
		// c.gridy++;
		// functionPane.add(editFuncSourceBox, c);

		// fieldPaneOne.add(new JSeparator());
		JPanel heuristicPane = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		heuristicPane.add(heuristicLabel, c);
		c.gridx++;
		heuristicPane.add(heuristicFuncCombo, c);

		c.gridx = 0;
		c.gridy++;

		heuristicPane.add(heuristicParamsLabel, c);
		paramHeuristicScroll = populateHeuristicParams();
		paramHeuristicScroll.setPreferredSize(new Dimension(200, 100));
		c.gridx++;
		heuristicPane.add(paramHeuristicScroll, c);

		c.gridx = 0;
		c.gridy++;
		heuristicPane.add(editHeuristicLabel, c);
		c.gridx++;
		heuristicPane.add(editHeuristicBox, c);
		heuristicPane.setPreferredSize(new Dimension(500, 100));

		JPanel heirarchyPane = new JPanel(new GridBagLayout());

		c.gridx = 0;
		c.gridy = 0;
		heirarchyPane.add(enableHeirarchy, c);
		c.gridx++;
		heirarchyPane.add(heirarchyCheckBox, c);
		c.gridy++;
		c.gridx = 0;
		heirarchyPane.add(swarmsLabel, c);
		c.gridx++;
		heirarchyPane.add(swarmsField, c);
		c.gridy++;
		c.gridx = 0;

		heirarchyPane.add(swarmsIterationsLabel, c);
		c.gridx++;
		heirarchyPane.add(swarmsIterationsField, c);
		c.gridy++;
		c.gridx = 0;

		heirarchyPane.add(heirarchySwitchOpsLabel, c);
		c.gridx++;
		heirarchyPane.add(switchOpsCheckBox, c);

		c.gridy++;
		c.gridx = 0;

		heirarchyPane.add(heirarchyPassGBLabel, c);
		c.gridx++;
		heirarchyPane.add(passGBCheckBox, c);
		c.gridy++;
		c.gridx = 0;

		heirarchyPane.add(heirarchyUseFIPSLabel, c);
		c.gridx++;
		heirarchyPane.add(useFIPSCheckBox, c);

		c.gridy++;
		c.gridx = 0;
		heirarchyPane.add(heirarchyLabel, c);
		c.gridx++;
		heirarchyPane.add(heirarchyFuncCombo, c);

		c.gridx = 0;
		c.gridy++;

		heirarchyPane.add(heirarchyParamsLabel, c);
		paramHeirarchyScroll = populateHeirarchyParams();

		c.gridx++;
		heirarchyPane.add(paramHeirarchyScroll, c);
		c.gridx = 0;
		c.gridy++;
		heirarchyPane.add(editHeirarchyLabel, c);
		c.gridx++;
		heirarchyPane.add(editHeirarchyBox, c);

		c.fill = GridBagConstraints.HORIZONTAL;

		JPanel heirarchyfunctionPane = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;
		heirarchyfunctionPane.add(heirarchyFuncLabel, c);
		c.gridy++;
		heirarchyfunctionPane.add(heirarchydimensionsLabel, c);
		c.gridy++;
		heirarchyfunctionPane.add(editHeirFuncParamsLabel, c);
		c.gridx++;
		c.gridy = 0;

		// Layout the text fields in a panel.

		heirarchyfunctionPane.add(heirarchyTestFuncCombo, c);
		c.gridy++;
		// paramHeirarchyFuncLBox = new JList(new DefaultListModel());
		paramHeirarchyTestScroll = new JScrollPane(paramHeirarchyTestFuncLBox);
		paramHeirarchyTestScroll = populateTestFunctParams(
				heirarchyTestFuncCombo);
		paramHeirarchyTestScroll.setPreferredSize(new Dimension(200, 100));
		heirarchyfunctionPane.add(paramHeirarchyTestScroll, c);
		c.gridy++;
		heirarchyfunctionPane.add(editHeirFuncParamsBox, c);

		// setup experiment pane

		JPanel experimentalPane = new JPanel(new GridBagLayout());

		c.gridx = 0;
		c.gridy = 0;
		experimentalPane.add(iterationsLabel, c);
		c.gridy++;
		experimentalPane.add(particlesLabel, c);
		c.gridy++;
		experimentalPane.add(evaluationsLabel, c);
		c.gridy++;
		experimentalPane.add(velocityInitLabel, c);
		c.gridy++;
		experimentalPane.add(boundaryHandlingLabel, c);
		c.gridy++;
		experimentalPane.add(runsLabel, c);
		c.gridy++;
		experimentalPane.add(randLabel, c);
		c.gridy++;
		experimentalPane.add(paraLabel, c);
		c.gridy++;
		experimentalPane.add(useMemBuffersLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		experimentalPane.add(iterationsField, c);
		c.gridy++;
		experimentalPane.add(particlesField, c);
		c.gridy++;
		experimentalPane.add(evaluationsField, c);
		c.gridy++;
		experimentalPane.add(velInitCombo, c);
		c.gridy++;
		experimentalPane.add(boundaryHandlingCombo, c);
		c.gridy++;
		experimentalPane.add(runsField, c);
		c.gridy++;
		experimentalPane.add(randCombo, c);
		c.gridy++;
		experimentalPane.add(paraCheck, c);
		c.gridy++;
		experimentalPane.add(useMemBuffersCheck, c);

		// setup neighbourhood pane
		JPanel neighbourhoodPane = new JPanel(new GridBagLayout());
		JLabel label = new JLabel(CHOOSE_A_NEIGHBOURHOOD);

		// neighbourhoodCombo.setPreferredSize(new Dimension(200, 100));
		label.setLabelFor(neighbourhoodCombo);
		label.setDisplayedMnemonic(KeyEvent.VK_N);
		c.gridx = 0;
		c.gridy = 0;
		neighbourhoodPane.add(label, c);
		c.gridx++;
		neighbourhoodPane.add(neighbourhoodCombo, c);
		c.gridy++;
		c.gridx--;
		label = new JLabel(NEIGHBOURHOOD_PARAMETER_LIST);
		label.setLabelFor(paramTopologyScroll);
		label.setDisplayedMnemonic(KeyEvent.VK_N);
		neighbourhoodPane.add(label, c);
		c.gridx++;
		paramTopologyScroll = populateTopologyParams();
		paramTopologyScroll.setPreferredSize(new Dimension(200, 100));
		neighbourhoodPane.add(paramTopologyScroll, c);
		c.gridx--;
		c.gridy++;

		neighbourhoodPane.add(editTopologyLabel, c);
		c.gridx++;

		neighbourhoodPane.add(editTopologyBox, c);
		// neighbourhoodPane.setPreferredSize(new Dimension(200, 200));

		JPanel runPane = new JPanel(new GridLayout(0, 1));
		runPane.add(experimentalPane);

		// setup output pane
		JPanel DBConnectionPane = new JPanel(new GridBagLayout());
		c.gridx = 0;
		c.gridy = 0;

		DBConnectionPane.add(new JLabel(DRIVER), c);
		c.gridy = 1;
		DBConnectionPane.add(new JLabel(URL), c);
		c.gridy = 2;
		DBConnectionPane.add(new JLabel(CONNECTION_STRING), c);
		c.gridy = 3;
		DBConnectionPane.add(new JLabel(USER_ID), c);
		c.gridy = 4;
		DBConnectionPane.add(new JLabel(PASSWORD), c);
		c.gridy = 5;
		DBConnectionPane.add(new JLabel(ONLY_STORE_BEST_SCORES), c);
		c.gridy = 6;
		DBConnectionPane.add(new JLabel(STORE_RESULTS_DIRECT_TO_DATABASE), c);

		c.gridy = 0;
		c.gridx = 1;
		driverBox = new JFormattedTextField();
		String driver = runparams.getProperty("driver", DEFAULT_DRIVER);
		runparams.setProperty("driver", driver);
		driverBox.setText(driver);
		driverBox.setEditable(true);
		driverBox.addPropertyChangeListener("value", this);
		DBConnectionPane.add(driverBox, c);
		urlBox = new JFormattedTextField();
		String url = runparams.getProperty("url", DEFAULT_URL);
		runparams.setProperty("url", url);
		urlBox.addPropertyChangeListener("value", this);
		urlBox.setText(url);
		urlBox.setEditable(true);
		c.gridy = 1;
		DBConnectionPane.add(urlBox, c);
		connectionBox = new JFormattedTextField();
		String connection = runparams.getProperty("connection",
				DEFAULT_CONNECTION_STRING);
		runparams.setProperty("connection", connection);
		connectionBox.addPropertyChangeListener("value", this);
		connectionBox.setText(connection);
		connectionBox.setEditable(true);
		c.gridy = 2;
		DBConnectionPane.add(connectionBox, c);
		userIdField = new JFormattedTextField();
		userIdField.addPropertyChangeListener("value", this);
		userIdField.setText(runparams.getProperty("userid", ""));
		userIdField.setEditable(true);
		c.gridy = 3;
		DBConnectionPane.add(userIdField, c);
		passwordField = new JFormattedTextField();
		passwordField.addPropertyChangeListener("value", this);
		passwordField.setText(runparams.getProperty("password", ""));
		passwordField.setEditable(true);
		c.gridy = 4;
		DBConnectionPane.add(passwordField, c);
		logCheck = new JCheckBox();
		logCheck.addPropertyChangeListener("value", this);
		logCheck.setSelected(true);
		c.gridy = 5;
		DBConnectionPane.add(logCheck, c);
		storeCheck = new JCheckBox();
		storeCheck.addPropertyChangeListener("value", this);
		storeCheck.setSelected(false);
		c.gridy = 6;
		DBConnectionPane.add(storeCheck, c);

		cardPane = new JTabbedPane()
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Make the panel wider than it really needs, so
			// the window's wide enough for the tabs to stay
			// in one row.
			public Dimension getPreferredSize()
			{
				Dimension size = super.getPreferredSize();
				size.width += 50;
				// size.height += 100;
				return size;
			}
		};
		// cardPane.setPreferredSize(new Dimension(500, 400));

		cardPane.addTab(HTAB, heuristicPane);
		cardPane.addTab(TTAB, functionPane);
		cardPane.addTab(HEIRARCHY_TAB, heirarchyPane);
		cardPane.addTab(HTTAB, heirarchyfunctionPane);
		cardPane.addTab(NEIGHBOURHOOD, neighbourhoodPane);
		cardPane.addTab(EXPERIMENTAL_RUNS, runPane);
		cardPane.addTab(DB_CONNECTION, DBConnectionPane);

		enableHeirarchy(false);

		JPanel mainPane = new JPanel(new GridBagLayout())
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Make the panel wider than it really needs, so
			// the window's wide enough for the tabs to stay
			// in one row.
			public Dimension getPreferredSize()
			{
				Dimension size = super.getPreferredSize();
				// size.width += 100;
				// size.height -= 50;
				return size;
			}
		};
		// add left pane
		c.gridx = 0;
		c.gridy = 0;
		mainPane.add(cardPane, c);
		// Create and add middle pane
		JPanel midPanel = new JPanel(new GridBagLayout())
		{
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			// Make the panel wider than it really needs, so
			// the window's wide enough for the tabs to stay
			// in one row.
			public Dimension getPreferredSize()
			{
				Dimension size = super.getPreferredSize();
				// size.width += 100;
				// size.height -= 50;
				return size;
			}
		};
		c.gridy = 1;
		JPanel gridPane = new JPanel(new GridLayout(0, 1));

		addButton = new JButton(ADD);
		addButton.setMnemonic(KeyEvent.VK_D);
		addButton.addActionListener(this);
		gridPane.add(addButton);
		removeButton = new JButton(REMOVE);
		removeButton.setMnemonic(KeyEvent.VK_R);
		removeButton.addActionListener(this);
		gridPane.add(removeButton);
		midPanel.add(gridPane, c);
		// c.gridy = 2;

		c.gridy = 0;
		c.gridx = 1;
		midPanel.add(new JLabel(EXPERIMENTS_TO_RUN), c);

		experimentTree = new JTree(expTreeRoot);
		experimentTree.getSelectionModel()
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		experimentTree.addTreeSelectionListener(this);
		JScrollPane midScrollPane = new JScrollPane(experimentTree);
		midScrollPane.setPreferredSize(new Dimension(200, 150));
		c.gridy = 1;
		midPanel.add(midScrollPane, c);
		c.gridy = 0;

		mainPane.add(midPanel, c);
		c.gridy = 0;
		// c.gridx++;
		// Create and add right pane
		treeLabel = new JLabel(EXPERIMENT_TREE);
		resultTree = refreshExpTree();
		expandAPath(resultTree.getPathForRow(0));
		resultTree.getSelectionModel()
				.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		resultTree.addTreeSelectionListener(this);
		new JLabel(NO_RESULTS_ARE_SELECTED_FOR_DISPLAY);
		resultTableModal = new ResultTableModel(runparams);
		resultTable = new JTable(resultTableModal);

		progressLabel = new JLabel(PROGRESS_LOG);
		loggingLabel = new JLabel(RESULTS_LOG);
		progressLoggable = new JCheckBox(LOG, false);
		progressLoggable.addActionListener(this);
		progressLog = new JTextArea(5, 50);
		progressLog.setEditable(false);

		progressBar = new JProgressBar();
		progressBar.setString(OVERALL_PROGRESS);
		progressBar.setStringPainted(true);

		JScrollPane resultTableScrollPane = new JScrollPane(resultTable);
		resultTableScrollPane.setPreferredSize(new Dimension(700, 200));
		resultTableScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollPane resultTreeScrollPane = new JScrollPane(resultTree);
		resultTreeScrollPane.setPreferredSize(new Dimension(700, 100));
		resultTreeScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollPane progressScrollPane = new JScrollPane(progressLog);
		progressScrollPane.setPreferredSize(new Dimension(700, 100));
		progressScrollPane.setVerticalScrollBarPolicy(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel rightPanel = new JPanel(new GridBagLayout());
		rightPanel.add(new JSeparator(SwingConstants.VERTICAL), c);
		c.gridx++;
		rightPanel.add(treeLabel, c);
		c.gridy++;
		rightPanel.add(resultTreeScrollPane, c);
		// c.gridy++;
		// rightPanel.add(tableLabel, c);
		// c.gridy++;
		// rightPanel.add(resultTableScrollPane, c);
		c.gridy++;
		rightPanel.add(loggingLabel, c);
		c.gridy++;
		rightPanel.add(progressLabel, c);
		c.gridx++;
		rightPanel.add(progressLoggable, c);
		c.gridx--;
		c.gridy++;
		rightPanel.add(progressScrollPane, c);
		c.gridy++;
		rightPanel.add(progressBar, c);

		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		mainPane.add(rightPanel, c);

		JPanel buttonPane = new JPanel(new FlowLayout());
		applyButton = new JButton(APPLY);
		startButton = new JButton(START_EXPERIMENT);
		loadButton = new JButton(LOAD_EXPERIMENT_DATA);
		exitButton = new JButton(EXIT);
		applyButton.setMnemonic(KeyEvent.VK_A);
		startButton.setMnemonic(KeyEvent.VK_X);
		loadButton.setMnemonic(KeyEvent.VK_L);
		exitButton.setMnemonic(KeyEvent.VK_E);
		startButton.addActionListener(this);
		applyButton.addActionListener(this);
		loadButton.addActionListener(this);
		exitButton.addActionListener(this);
		buttonPane.add(applyButton);
		buttonPane.add(startButton);
		buttonPane.add(loadButton);
		buttonPane.add(exitButton);

		startButton.setEnabled(false);
		applyButton.setEnabled(false);
		// Put the panels in this panel, labels on left,
		// text fields on right.
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(mainPane, BorderLayout.CENTER);
		add(buttonPane, BorderLayout.SOUTH);
		// add(fieldPane, BorderLayout.LINE_END);
		loadExp(null);
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = (Object) e.getSource();
		if (source == aboutMenuItem)
		{

			String s1 = "OK";
			Object[] options =
			{s1};
			JOptionPane.showOptionDialog(frame, ABOUT_VERSION, "About",
					JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE,
					null, options, s1);

		} else if (source == ingberMenuItem)
		{

			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Properties prop = null;
					ImportIngberUI ing = new ImportIngberUI(prop);
					ing.pack();
					// Show it.
					ing.setSize(new Dimension(300, 150));
					ing.setLocationRelativeTo(frame);
					ing.setVisible(true);

				}
			});

			// JPanel contentPane = new JPanel(new BorderLayout());
			// contentPane.add(label, BorderLayout.CENTER);
			// contentPane.add(closePanel, BorderLayout.PAGE_END);
			// contentPane.setOpaque(true);
			// ing.setContentPane(contentPane);
		} else if (source == FDMMenuItem)
		{

			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Properties prop = null;
					ImportFDMUI ing = new ImportFDMUI(prop);
					ing.pack();
					// Show it.
					ing.setSize(new Dimension(300, 150));
					ing.setLocationRelativeTo(frame);
					ing.setVisible(true);

				}
			});

			// JPanel contentPane = new JPanel(new BorderLayout());
			// contentPane.add(label, BorderLayout.CENTER);
			// contentPane.add(closePanel, BorderLayout.PAGE_END);
			// contentPane.setOpaque(true);
			// ing.setContentPane(contentPane);

		} else if (source == SEMGMenuItem)
		{

			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Properties prop = null;
					ImportSEMGUI ing = new ImportSEMGUI(prop);
					ing.pack();
					// Show it.
					ing.setSize(new Dimension(300, 150));
					ing.setLocationRelativeTo(frame);
					ing.setVisible(true);

				}
			});
		} else if (source == GasSpecMenuItem)
		{

			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Properties prop = null;
					ImportGasGUI ing = new ImportGasGUI(prop);
					ing.pack();
					// Show it.
					ing.setSize(new Dimension(300, 150));
					ing.setLocationRelativeTo(frame);
					ing.setVisible(true);

				}
			});

		} 
		else if (source == MinasPassageMenuItem)
		{

			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					Properties prop = null;
					ImportMinasGUI ing = new ImportMinasGUI(prop);
					ing.pack();
					// Show it.
					ing.setSize(new Dimension(300, 150));
					ing.setLocationRelativeTo(frame);
					ing.setVisible(true);

				}
			});
		}
		else
			if (source == progressLoggable)
			isLogging = progressLoggable.isSelected();
		else if (source == heuristicFuncCombo)
		{
			paramHeuristicScroll = populateHeuristicParams();
			paramHeuristicScroll.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		} else if (source == heirarchyFuncCombo)
		{
			paramHeirarchyScroll = populateHeirarchyParams();
			paramHeirarchyScroll.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		} else if (source == neighbourhoodCombo)
		{
			paramTopologyScroll = populateTopologyParams();
			paramTopologyScroll.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		} else if (source == testFuncCombo)
			populateTestFunctParams(testFuncCombo);
		else if (source == heirarchyTestFuncCombo)
			populateTestFunctParams(heirarchyTestFuncCombo);
		else if (source == exitButton)
			quit(this.frame);
		else if (source == loadButton)
		{
			if (resNode != null)
			{
				String strNode = resNode.getUserObject().toString();
				long key = Long.parseLong(
						strNode.substring(strNode.indexOf(" ")).trim());
				loadExp(key);
			}
		} else if (source == applyButton)
		{
			applySettings();
			applyButton.setEnabled(false);
		} else if (source == addButton)
		{
			// startButton.setText(CANCEL_EXPERIMENT);
			// progressLog.append("test"+LINESEPARATOR);
			applySettings();
			// collapseAPath(experimentTree.getPathForRow(0));
			experiments = prepareExperiment();
			startButton.setEnabled(!experiments.isEmpty());
		} else if (source == removeButton)
		{
			// String s1 = "OK";
			// Object[] options =
			// {s1};
			// int n = JOptionPane.showOptionDialog(frame,
			// "Sorry, this function is not implemented yet.",
			// "Not Implemented", JOptionPane.OK_OPTION,
			// JOptionPane.INFORMATION_MESSAGE, null, options, s1);
			if (expNode == null)
				JOptionPane.showMessageDialog(frame,
						PLEASE_SELECT_AN_EXPERIMENT_TO_REMOVE);
			else
			{
				removeExperiment();
				startButton.setEnabled(!experiments.isEmpty());
			}

		} else if (source == startButton)
		{
			if (!runningExperiment)
			{
				fireExperimentsStarted();
				applySettings();
				runExperiments();
				updateLog(INITIALISING_EXPERIMENTS);
			} else
			{
				if (confirm(frame, false))
				{
					runExperiments.stopExperiments();
					runExperiments = null;
					fireExperimentsComplete();
					progressLabel.setText(NO_EXPERIMENTS_ARE_RUNNING);
					Log.push();
				}
			}
		} else if (source == heirarchyCheckBox)
			enableHeirarchy(heirarchyCheckBox.isSelected());
		else if (source == useFIPSCheckBox)
		{
			if (runparams != null)
			{
				runparams.setProperty("heirarchyusefips",
						Boolean.toString(useFIPSCheckBox.isSelected()));
				String buf = "";
				if (useFIPSCheckBox.isSelected())
				{

					Set<String> keys = topologies.keySet();
					for (Iterator<String> iterator = keys.iterator(); iterator
							.hasNext();)
					{
						buf = (String) iterator.next();
						if (buf.contains("FIPS"))
							break;
					}
					buf = topologies.get(buf) + "";
				} else
					buf = topologies.get(neighbourhoodCombo.getSelectedItem());
				runparams.setProperty("heirarchytopology", buf);
			}
		} else if (source == resultTree)
		{
			TreePath resultPath = resultTree.getSelectionPath();
			DefaultMutableTreeNode resultNode = (DefaultMutableTreeNode) resultPath
					.getPathComponent(1);
			String strNode = (String) resultNode.getUserObject();
			resultTableModal.fireTableDataChanged(
					Long.valueOf(strNode.lastIndexOf(" ") + 1));

		}
	}

	/**
	 * 
	 */
	public void fireExperimentsStarted()
	{
		progressLabel.setText(STARTING_EXPERIMENTS);
		startButton.setText(CANCEL_EXPERIMENT);
		runningExperiment = true;
		addButton.setEnabled(false);
		removeButton.setEnabled(false);
		loadButton.setEnabled(false);
		applyButton.setEnabled(false);
	}

	public void addExperiment(Experiment exp)
	{
		int index = experiments.size();
		String strId = "New Experiment " + ++index;

		DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(strId);
		expTreeRoot.add(node1);

		DefaultMutableTreeNode node2 = new DefaultMutableTreeNode(
				"Apply " + exp.getSwarm().getDescription());
		node1.add(node2);

		DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("using "
				+ exp.getNeighbourhood().getDescription() + " neighbourhood");
		node2.add(node3);

		DefaultMutableTreeNode node4 = new DefaultMutableTreeNode(
				"to " + exp.getObjective().getDescription());
		node3.add(node4);
		DefaultMutableTreeNode node5 = new DefaultMutableTreeNode(
				"run " + exp.getRuns() + " times");
		if (heirarchyCheckBox.isSelected())
		{
			DefaultMutableTreeNode node41 = new DefaultMutableTreeNode(
					"with heirarchy and " + exp.getHeir().getMasterSwarm()
							.getTestFunction().getDescription());
			node3.add(node41);
			node41.add(node5);
		} else
		{
			node4.add(node5);
		}
		DefaultMutableTreeNode node6 = new DefaultMutableTreeNode(
				"Notes: " + exp.getNotes());
		node5.add(node6);
		experimentTree.updateUI();
		expandAPath(experimentTree.getPathForRow(0));
		experiments.put((String) node1.getUserObject(), exp);

	}
	/**
	 * applies the settings; storing them in to Property variables
	 */
	private void applySettings()
	{
		/*
		 * setup heuristicParams Properties
		 */
		// I assume that the setup params are
		// already updated
		String buf = heuristics.get(heuristicFuncCombo.getSelectedItem());
		heuristicParams.setProperty("swarmclass", buf);
		heuristicParams.setProperty("particles",
				particlesField.getValue().toString());
		heirarchyParams.setProperty("iterations",
				iterationsField.getValue().toString());
		buf = topologies.get(neighbourhoodCombo.getSelectedItem());
		heuristicParams.setProperty("topologyclass", buf);
		heuristicParams.setProperty("objectiveclass",
				testfunctions.get(testFuncCombo.getSelectedItem()));
		if (testFuncParams.containsKey("dimensions"))
			heuristicParams.setProperty("dimensions",
					testFuncParams.getProperty("dimensions"));
		populateRunParams(heirarchyCheckBox.isSelected());

		resultTableModal.setRunparams(runparams);
		resultTable.updateUI();

	}

	@SuppressWarnings("unused")
	private void collapseAPath(TreePath p)
	{

		experimentTree.collapsePath(p);
		TreeNode currNode = (TreeNode) p.getLastPathComponent();
		int numChildren = currNode.getChildCount();
		for (int i = 0; i < numChildren; ++i)
		{

			TreePath newPath = p.pathByAddingChild(currNode.getChildAt(i));
			collapseAPath(newPath);
		}
	}

	private long computeEvals(long iterations, long particles, long evaluations)
	{

		evaluations = iterations * particles;
		return evaluations;
	}

	private boolean confirm(JFrame frame, boolean quit)
	{
		String s1;
		String s2;
		String question;
		String title;
		s1 = "Yes";
		s2 = "No";
		if (quit)
		{
			question = "Do you really want to quit?";
			title = "Quit Confirmation";
		} else
		{
			question = "Do you really want to cancel?";
			title = "Cancel Confirmation";
		}
		Object[] options =
		{s1, s2};

		int n = JOptionPane.showOptionDialog(frame, question, title,
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
				options, s1);
		if (n == JOptionPane.YES_OPTION)
		{
			return true;
		} else
		{
			return false;
		}
	}

	private Properties editHeirarchyParams()
	{
		String key;
		String[] pairs;
		String value;
		String oldValue = "";
		String oldKey = "";

		if (!paramHeirarchyLBox.isSelectionEmpty())
		{
			pairs = editHeirarchyLabel.getText().split(":");
			if (pairs.length > 1)
			{
				oldKey = pairs[1].trim();

				oldValue = editHeirarchyBox.getText();
				heirarchyParams.setProperty(oldKey, oldValue);
				DefaultListModel<String> defaultListModel = ((DefaultListModel<String>) paramHeirarchyLBox
						.getModel());
				replaceKey(defaultListModel, oldKey, oldValue);

			}
			key = paramHeirarchyLBox.getSelectedValue();
			if (key != null)
			{
				pairs = key.split("=");
				key = pairs[0].trim();
				value = pairs[1].trim();
				editHeirarchyLabel.setText(EDIT + key.trim() + ":");
				editHeirarchyBox.setText(value);
			}
			// setHeirarchyParamsList(heirarchyParams);
		}
		return heirarchyParams;
	}

	private Properties editHeirTestFuncParams()
	{
		String key;
		String[] pairs;
		String value;
		String oldValue = "";
		String oldKey = "";

		if (!paramHeirarchyTestFuncLBox.isSelectionEmpty())
		{
			pairs = editHeirFuncParamsLabel.getText().split(":");
			if (pairs.length > 1)
			{
				oldKey = pairs[1].trim();

				oldValue = editHeirFuncParamsBox.getText();
				heirarchyTestFuncParams.setProperty(oldKey, oldValue);
				DefaultListModel<String> defaultListModel = ((DefaultListModel<String>) paramHeirarchyTestFuncLBox
						.getModel());
				replaceKey(defaultListModel, oldKey, oldValue);
			}

			key = paramHeirarchyTestFuncLBox.getSelectedValue();
			if (key != null)
			{
				pairs = key.split("=");

				key = pairs[0].trim();
				value = pairs[1].trim();
				editHeirFuncParamsLabel.setText(EDIT + key.trim() + ":");
				editHeirFuncParamsBox.setText(value);
			}
			// setHeuristicParamsList(heuristicParams);
		}

		return heirarchyTestFuncParams;
	}

	private Properties editHeuristicParams()
	{
		String key;
		String[] pairs;
		String value;
		String oldValue = "";
		String oldKey = "";

		if (!paramHeuristicLBox.isSelectionEmpty())
		{
			pairs = editHeuristicLabel.getText().split(":");
			if (pairs.length > 1)
			{
				oldKey = pairs[1].trim();

				oldValue = editHeuristicBox.getText();
				heuristicParams.setProperty(oldKey, oldValue);
				DefaultListModel<String> defaultListModel = ((DefaultListModel<String>) paramHeuristicLBox
						.getModel());
				replaceKey(defaultListModel, oldKey, oldValue);
			}

			key = paramHeuristicLBox.getSelectedValue();
			if (key != null)
			{
				pairs = key.split("=");

				key = pairs[0].trim();
				value = pairs[1].trim();
				editHeuristicLabel.setText(EDIT + key.trim() + ":");
				editHeuristicBox.setText(value);
			}
			// setHeuristicParamsList(heuristicParams);
		}

		return heuristicParams;
	}

	private Properties editTestFuncParams()
	{
		String key;
		String[] pairs;
		String value;
		String oldValue = "";
		String oldKey = "";

		if (!paramTestFuncLBox.isSelectionEmpty())
		{
			pairs = editFuncParamsLabel.getText().split(":");
			if (pairs.length > 1)
			{
				oldKey = pairs[1].trim();

				oldValue = editFuncParamsBox.getText();
				testFuncParams.setProperty(oldKey, oldValue);
				DefaultListModel<String> defaultListModel = ((DefaultListModel<String>) paramTestFuncLBox
						.getModel());
				replaceKey(defaultListModel, oldKey, oldValue);
			}

			key = paramTestFuncLBox.getSelectedValue();
			if (key != null)
			{
				pairs = key.split("=");

				key = pairs[0].trim();
				value = pairs[1].trim();
				editFuncParamsLabel.setText(EDIT + key.trim() + ":");
				editFuncParamsBox.setText(value);
			}
			// setHeuristicParamsList(heuristicParams);
		}

		return testFuncParams;
	}

	private Properties editTopologyParams()
	{
		String key;
		String[] pairs;
		String value;
		String oldValue = "";
		String oldKey = "";

		if (!paramTopologyLBox.isSelectionEmpty())
		{
			pairs = editTopologyLabel.getText().split(":");
			if (pairs.length > 1)
			{
				oldKey = pairs[1].trim();

				oldValue = editTopologyBox.getText();
				topologyParams.setProperty(oldKey, oldValue);
				DefaultListModel<String> defaultListModel = ((DefaultListModel<String>) paramTopologyLBox
						.getModel());
				replaceKey(defaultListModel, oldKey, oldValue);
			}
			key = paramTopologyLBox.getSelectedValue();
			if (key != null)
			{
				pairs = key.split("=");
				key = pairs[0].trim();
				value = pairs[1].trim();
				editTopologyLabel.setText(EDIT + key.trim() + ":");
				editTopologyBox.setText(value);
			}
			// setTopologyParamsList(topologyParams);
		}
		return topologyParams;
	}

	private void enableHeirarchy(boolean bEnabled)
	{
		swarmsField.setEnabled(bEnabled);
		swarmsLabel.setEnabled(bEnabled);
		swarmsIterationsField.setEnabled(bEnabled);
		swarmsIterationsLabel.setEnabled(bEnabled);
		heirarchyFuncCombo.setEnabled(bEnabled);
		heirarchyLabel.setEnabled(bEnabled);
		heirarchyParamsLabel.setEnabled(bEnabled);
		paramHeirarchyLBox.setEnabled(bEnabled);
		paramHeirarchyScroll.setEnabled(bEnabled);
		editHeirarchyLabel.setEnabled(bEnabled);
		editHeirarchyBox.setEnabled(bEnabled);
		switchOpsCheckBox.setEnabled(bEnabled);
		passGBCheckBox.setEnabled(bEnabled);
		useFIPSCheckBox.setEnabled(bEnabled);
		heirarchySwitchOpsLabel.setEnabled(bEnabled);
		heirarchyPassGBLabel.setEnabled(bEnabled);
		heirarchyUseFIPSLabel.setEnabled(bEnabled);
		cardPane.setEnabledAt(cardPane.indexOfTab(HTTAB), bEnabled);
	}

	private void expandAPath(TreePath p)
	{

		experimentTree.expandPath(p);
		if (p != null)
		{
			TreeNode currNode = (TreeNode) p.getLastPathComponent();
			int numChildren = currNode.getChildCount();
			for (int i = 0; i < numChildren; ++i)
			{

				TreePath newPath = p.pathByAddingChild(currNode.getChildAt(i));
				expandAPath(newPath);
			}
		}
	}

	public void fireExperimentsComplete()
	{
		try
		{
			runningExperiment = false;
			progressLabel.setText(EXPERIMENTS_COMPLETED);
			loggingLabel.setText(RESULTS_LOG);
			startButton.setText(START_EXPERIMENT);
			startButton.setEnabled(false);
			addButton.setEnabled(true);
			removeButton.setEnabled(true);
			loadButton.setEnabled(true);
			// applyButton.setEnabled(true);
			startButton.repaint();
			if (expTreeRoot != null)
				expTreeRoot.removeAllChildren();
			if (experimentTree != null)
			{
				experimentTree.removeAll();
				experimentTree.updateUI();
			}
			progressBar.setValue(0);
			refreshExpTree();
			// expandAPath(resultTree.getPathForRow(0));
			experiments.clear();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the progressBar
	 */
	public JProgressBar getProgressBar()
	{
		return progressBar;
	}

	/**
	 * @return the runparams
	 */
	public Properties getRunparams()
	{
		return runparams;
	}

	/**
	 * @return the useMemBuffersCheck
	 */
	public JCheckBox getUseMemBuffersCheck()
	{
		return useMemBuffersCheck;
	}

	private synchronized void loadExp(Long expNo)
	{
		Experiment exp;

		populateRunParams(true);

		ExpData expData = new ExpData(runparams);
		exp = expData.load(expNo, 1, false);
		if (exp != null)
		{
			String userid = runparams.getProperty("userid");
			String password = runparams.getProperty("password");
			String driver = runparams.getProperty("driver");
			String connect = runparams.getProperty("connection");
			String url = runparams.getProperty("url");
			runparams.clear();
			runparams.putAll(exp.getRunParams());
			runparams.setProperty("driver", driver);
			runparams.setProperty("connection", connect);
			runparams.setProperty("url", url);
			runparams.setProperty("userid", userid);
			runparams.setProperty("password", password);
			long runs = Long.parseLong((String) runparams.get("runs"));
			exp.setRuns(runs);
			// Heuristic tab
			BaseSwarm swarm = exp.getSwarm();
			heuristicFuncCombo.setSelectedItem(swarm.getDescription());
			heuristicParams.clear();
			heuristicParams.putAll(swarm.getParams());
			setHeuristicParamsList(heuristicParams);

			// Topology tab
			Topology top = swarm.getNeighbourhood();
			neighbourhoodCombo.setSelectedItem(top.getDescription());
			topologyParams.clear();
			topologyParams.putAll(top.getParams());
			setTopologyParamsList(topologyParams);

			// Test Functions tab
			TestBase test = swarm.getTestFunction();
			testFuncCombo.setSelectedItem(test.getDescription());
			testFuncParams.clear();
			testFuncParams.putAll(test.getParams());
			setTestFuncParamsList(testFuncParams, paramTestFuncLBox);

			// Experimental Runs tab
			iterationsField.setValue(Long.parseLong(
					runparams.getProperty("iterations", "1000").toString()));
			particlesField.setValue(Long.parseLong(
					runparams.getProperty("particles", "40").toString()));
			velInitCombo.setSelectedItem(
					runparams.getProperty("initveltype").toString());
			boundaryHandlingCombo.setSelectedItem(
					runparams.getProperty("boundary").toString());
			runsField.setValue(runs);
			randCombo.setSelectedItem(runparams.getProperty("random"));
			paraCheck.setSelected(Boolean
					.parseBoolean(runparams.getProperty("paraeval", "false")));
			useMemBuffersCheck.setSelected(Boolean.parseBoolean(
					runparams.getProperty("usemembuffers", "false")));

			// Output tab
			driverBox.setText(runparams.getProperty("driver", driver));
			urlBox.setText(runparams.getProperty("url", url));
			connectionBox.setText(runparams.getProperty("connection", connect));
			userIdField.setText(runparams.getProperty("userid", userid));
			passwordField.setText(runparams.getProperty("password", password));
			logCheck.setSelected(!Boolean
					.parseBoolean(runparams.getProperty("logAll", "true")));
			storeCheck.setSelected(Boolean.parseBoolean(
					runparams.getProperty("storedirect", "true")));

			// Heirarchy tab

			Heirarchy heir = exp.getHeir();
			if (heir != null)
			{
				heirarchyCheckBox.setSelected(true);
				enableHeirarchy(true);
				BaseSwarm masterSwarm = heir.getMasterSwarm();
				heirarchyFuncCombo
						.setSelectedItem(masterSwarm.getDescription());
				heirarchyParams.clear();
				heirarchyParams.putAll(masterSwarm.getParams());
				setHeuristicParamsList(heuristicParams);

				swarmsField.setValue(
						runparams.getProperty("heirarchyswarms").toString());
				swarmsIterationsField.setValue(Long.parseLong(
						runparams.getProperty("heirarchyiterations", "1000")
								.toString()));
				switchOpsCheckBox
						.setSelected(
								Boolean.parseBoolean(runparams
										.getProperty("heirarchyswitchops",
												Boolean.toString(true))
										.toString()));
				passGBCheckBox.setSelected(Boolean.parseBoolean(runparams
						.getProperty("heirarchypassgb", Boolean.toString(true))
						.toString()));
				useFIPSCheckBox.setSelected(Boolean.parseBoolean(runparams
						.getProperty("heirarchyusefips", Boolean.toString(true))
						.toString()));

				setHeirarchyParamsList(masterSwarm.getParams());

				// Heirarchy Test Functions tab
				TestBase test1 = masterSwarm.getTestFunction();
				heirarchyTestFuncCombo.setSelectedItem(test1.getDescription());
				heirarchyTestFuncParams.clear();
				heirarchyTestFuncParams.putAll(test1.getParams());
				setTestFuncParamsList(heirarchyTestFuncParams,
						paramHeirarchyTestFuncLBox);

			} else
			{
				heirarchyCheckBox.setSelected(false);
				enableHeirarchy(false);
			}
			progressBar.setString(LOADED_PARAMETERS_FOR_EXPERIMENT
					+ (expNo == null ? "" : expNo.toString().trim()));

		}
	}

	/**
	 * @return JScrollPane containing the current Parameter settings
	 */
	private JScrollPane populateHeirarchyParams()
	{
		String base = "";

		editHeirarchyLabel.setText(EDIT);
		editHeirarchyBox.setText("");

		try
		{
			// Set the heuristicParams for the previous selection

			Class<?> class1;
			BaseSwarm swarm;
			base = heuristics.get(heirarchyFuncCombo.getSelectedItem());
			if (base != null && base != "")
			{
				class1 = (Class<?>) Class.forName(base);

				swarm = (BaseSwarm) class1.getDeclaredConstructor()
						.newInstance();
				heirarchyParams = swarm.getParams();
				setHeirarchyParamsList(heirarchyParams);
				paramHeirarchyLBox.setVisibleRowCount(4);

				paramHeirarchyLBox.setSelectedIndex(0);
				heirarchyParams = editHeirarchyParams();

			}
		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (InstantiationException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (IllegalAccessException e)
		{
			Log.log(Level.SEVERE, e);
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
		return paramHeirarchyScroll;
	}

	/**
	 * @return JComboBox containing heuristic method classes
	 */
	private JComboBox<String> populateHeuristicCombo()
	{
		JComboBox<String> bufList = new JComboBox<String>();
		ArrayList<Class<Object>> classes;
		BaseSwarm baseSwarm = null;
		String description;

		try
		{
			String pckgname = "org.mltestbed.heuristics.PSO";
			classes = Main.getClasses(pckgname);
			for (int i = 0; i < classes.size(); i++)
			{
				Class<Object> class1 = classes.get(i);
				Log.getLogger().info(class1.getName());
				String name = class1.getPackage().getName();
				if (name.equals(pckgname)
						&& !Modifier.isAbstract(class1.getModifiers())
						&& !class1.isMemberClass()
						&& !class1.isAnonymousClass())
				{
					try
					{
						baseSwarm = (BaseSwarm) class1.getDeclaredConstructor()
								.newInstance();
						description = baseSwarm.getDescription();
						bufList.addItem(description);
						heuristics.put(description, class1.getName());
						Log.getLogger().info(description);
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
			}
		}

		catch (ClassNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bufList;

	}

	/**
	 * @return JScrollPane containing the current Parameter settings
	 */
	private JScrollPane populateHeuristicParams()
	{

		String base = "";
		editHeuristicLabel.setText(EDIT);
		editHeuristicBox.setText("");
		try
		{
			// Set the heuristicParams for the previous selection

			Class<?> class1;
			BaseSwarm swarm;
			base = heuristics.get(heuristicFuncCombo.getSelectedItem());
			if (base != null && base != "")
			{
				class1 = (Class<?>) Class.forName(base);

				swarm = (BaseSwarm) class1.getDeclaredConstructor()
						.newInstance();
				heuristicParams = swarm.getParams();
				setHeuristicParamsList(heuristicParams);
				paramHeuristicLBox.setVisibleRowCount(4);
				paramHeuristicLBox.setSelectedIndex(0);

				heuristicParams = editHeuristicParams();
			}
		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (InstantiationException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (IllegalAccessException e)
		{
			Log.log(Level.SEVERE, e);
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

		return paramHeuristicScroll;
	}

	/**
	 * @return JComboBox containing neighbourhood classes
	 */
	private JComboBox<String> populateNeighbourhoodCombo()
	{
		JComboBox<String> bufList = new JComboBox<String>();
		ArrayList<Class<Object>> classes;
		Topology topology;

		try
		{
			topologies.clear();
			classes = Main.getClasses("org.mltestbed.topologies");
			for (int i = 0; i < classes.size(); i++)
			{
				Class<Object> class1 = classes.get(i);

				Log.getLogger().info(class1.getName());
				if (!Modifier.isAbstract(class1.getModifiers())
						&& !Modifier.isPrivate(class1.getModifiers())
						&& !class1.isAnonymousClass()
						&& !class1.isMemberClass())
				{
					try
					{
						Constructor<Object> con = class1
								.getConstructor(new Class[]
								{BaseSwarm.class});
						topology = (Topology) con.newInstance(new Object[]
						{new ClassicPSO()});
						bufList.addItem(topology.getDescription());
						topologies.put(topology.getDescription(),
								class1.getName());
					} catch (InstantiationException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e)
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
					}
				}
			}

		} catch (ClassNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bufList;

	}

	/**
	 * 
	 */
	private void populateRunParams(boolean incHeirarchy)
	{
		/*
		 * setup runparams Properties
		 */

		runparams = new Properties();
		// some parameters are repeated for convenience
		String buf = heuristics.get(heuristicFuncCombo.getSelectedItem());
		heuristicParams.setProperty("swarmclass", buf);
		runparams.setProperty("particles",
				particlesField.getValue().toString());
		runparams.setProperty("iterations",
				iterationsField.getValue().toString());
		runparams.setProperty("objective",
				testfunctions.get(testFuncCombo.getSelectedItem()));
		runparams.setProperty("initveltype",
				velInitCombo.getSelectedItem().toString());
		if (testFuncParams.containsKey("dimensions"))
			runparams.setProperty("dimensions",
					testFuncParams.getProperty("dimensions"));
		runparams.setProperty("driver", driverBox.getText().toString());
		runparams.setProperty("url", urlBox.getText().toString());

		runparams.setProperty("connection", connectionBox.getText().toString());
		runparams.setProperty("userid", userIdField.getText().toString());
		runparams.setProperty("password", passwordField.getText().toString());
		runparams.setProperty("runs", runsField.getValue().toString());
		runparams.setProperty("boundary",
				boundaryHandlingCombo.getSelectedItem().toString());
		runparams.setProperty("random", randCombo.getSelectedItem().toString());
		runparams.setProperty("paraeval",
				Boolean.toString(paraCheck.isSelected()));
		runparams.setProperty("usemembuffers",
				Boolean.toString(useMemBuffersCheck.isSelected()));

		runparams.setProperty("topologyclass",
				topologies.get(neighbourhoodCombo.getSelectedItem()));
		runparams.setProperty("logAll",
				Boolean.toString(!logCheck.isSelected()));
		runparams.setProperty("storedirect",
				Boolean.toString(storeCheck.isSelected()));

		if (incHeirarchy)
		{
			buf = heuristics.get(heirarchyFuncCombo.getSelectedItem());
			runparams.setProperty("heirarchyclass", buf);
			runparams.setProperty("heirarchyobjective", testfunctions
					.get(heirarchyTestFuncCombo.getSelectedItem()));
			runparams.setProperty("heirarchydimensions", heirarchyTestFuncParams
					.getProperty("dimensions", "0").toString());
			runparams.setProperty("heirarchyswarms",
					swarmsField.getText().toString());
			runparams.setProperty("heirarchyiterations",
					swarmsIterationsField.getValue().toString());
			runparams.setProperty("heirarchyswitchops",
					Boolean.toString(switchOpsCheckBox.isSelected()));
			runparams.setProperty("heirarchypassgb",
					Boolean.toString(passGBCheckBox.isSelected()));
			runparams.setProperty("heirarchyusefips",
					Boolean.toString(useFIPSCheckBox.isSelected()));
		}
		try
		{
			runparams.store(new FileOutputStream(RUNTIME_PROPERTIES),
					"ML Runtime properties - Automatically saved");
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
	 * @return JComboBox containing test function classes
	 */
	private JComboBox<String> populateTestCombo(String strList)
	{

		ArrayList<Class<Object>> classes;
		TestBase testFunct;

		JComboBox<String> bufList = new JComboBox<String>();
		try
		{

			testfunctions.clear();
			classes = (ArrayList<Class<Object>>) Main
					.getClasses("org.mltestbed.testFunctions.uniModal");
			classes.addAll(Main
					.getClasses("org.mltestbed.testFunctions.multiModal"));
			classes.addAll(Main
					.getClasses("org.mltestbed.testFunctions.heirarchy"));
			for (int i = 0; i < classes.size(); i++)
			{
				Class<Object> class1 = classes.get(i);

				Log.getLogger().info(class1.getName());
				if (!Modifier.isAbstract(class1.getModifiers())
						&& !Modifier.isPrivate(class1.getModifiers())
						&& !class1.isAnonymousClass()
						&& !class1.isMemberClass())
					try
					{
						testFunct = (TestBase) class1.getDeclaredConstructor()
								.newInstance();

						if (((strList.equalsIgnoreCase("testFuncCombo"))
								&& (!testFunct.getClass().getPackage().getName()
										.endsWith("heirarchy")))
								|| ((strList.equalsIgnoreCase(
										"heirarchytestFuncCombo"))
										&& (!testFunct.getClass().getPackage()
												.getName().endsWith("uniModal")
												&& (!testFunct.getClass()
														.getPackage().getName()
														.endsWith(
																"multiModal")))))
							bufList.addItem(testFunct.getDescription());
						testfunctions.put(testFunct.getDescription(),
								class1.getName().toString());
					} catch (InstantiationException e)
					{
						Log.log(Level.SEVERE, e);
						// e.printStackTrace();
					} catch (IllegalAccessException e)
					{
						Log.log(Level.SEVERE, e);
						// e.printStackTrace();
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
			bufList.setSelectedIndex(0);
		} catch (ClassNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return bufList;

	}

	private JScrollPane populateTestFunctParams(JComboBox<String> testFuncCombo)
	{

		try
		{
			// Set the heuristicParams for the previous selection

			Class<?> class1;
			TestBase test = null;

			String base = testfunctions.get(testFuncCombo.getSelectedItem());
			if (base != null && base != "")
			{
				class1 = (Class<?>) Class.forName(base);

				test = (TestBase) class1.getDeclaredConstructor().newInstance();
			}
			if (testFuncCombo.equals(this.testFuncCombo))
			{
				editFuncParamsLabel.setText(EDIT);
				editFuncParamsBox.setText("");
				if (test != null)
					testFuncParams = test.getParams();
				setTestFuncParamsList(testFuncParams, paramTestFuncLBox);
				paramTestFuncLBox.setVisibleRowCount(4);
				// paramTestFuncLBox.setSelectedIndex(0);

				testFuncParams = editTestFuncParams();

			}
			// Heirarchy
			else
			{
				editHeirFuncParamsLabel.setText(EDIT);
				editHeirFuncParamsBox.setText("");
				if (test != null)
					heirarchyTestFuncParams = test.getParams();
				setTestFuncParamsList(heirarchyTestFuncParams,
						paramHeirarchyTestFuncLBox);
				paramHeirarchyTestFuncLBox.setVisibleRowCount(4);
				paramHeirarchyTestFuncLBox.setSelectedIndex(0);

				heirarchyTestFuncParams = editHeirTestFuncParams();

			}
		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, e);
			// e.printStackTrace();
		} catch (InstantiationException e)
		{
			Log.log(Level.SEVERE, e);
		} catch (IllegalAccessException e)
		{
			Log.log(Level.SEVERE, e);
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

		if (testFuncCombo.equals(this.testFuncCombo))
			return paramTestScroll;
		else
			return paramHeirarchyTestScroll;
	}
	/**
	 * 
	 * @return JList containing the current Parameter settings
	 */
	private JScrollPane populateTopologyParams()
	{
		String base = "";
		editTopologyLabel.setText(EDIT);
		editTopologyBox.setText("");
		try
		{
			// Set the heuristicParams for the previous selection

			Class<?> class1;
			base = topologies.get(neighbourhoodCombo.getSelectedItem());
			if (base != null && base != "")
			{
				class1 = (Class<?>) Class.forName(base);
				Constructor<?> con = class1.getConstructor(new Class[]
				{BaseSwarm.class});
				Topology topology = (Topology) con.newInstance(new Object[]
				{new ClassicPSO()});
				topologyParams = topology.getParams();
				setTopologyParamsList(topologyParams);
				paramTopologyLBox.setSelectedIndex(0);
				paramTopologyLBox.setVisibleRowCount(4);
				topologyParams = editTopologyParams();

			}
		} catch (ClassNotFoundException e)
		{
			Log.getLogger().info(e.getMessage());
			// e.printStackTrace();
		} catch (InstantiationException e)
		{
			Log.getLogger().info(e.getMessage());
		} catch (IllegalAccessException e)
		{
			Log.getLogger().info(e.getMessage());
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paramTopologyScroll;
	}

	private HashMap<String, Experiment> prepareExperiment()
	{
		String buf = "";
		BaseSwarm swarm;
		Experiment exp = new Experiment();

		if (experiments == null)
			experiments = new HashMap<String, Experiment>();

		Class<?> class1;
		try
		{
			buf = heuristicParams.getProperty("swarmclass");
			class1 = (Class<?>) Class.forName(buf);
			swarm = (BaseSwarm) class1.getDeclaredConstructor().newInstance();
			swarm.setParams(heuristicParams);
			swarm.setTestFunction(
					heuristicParams.getProperty("objectiveclass"));
			iterations = Long.parseLong(runparams.getProperty("iterations",
					Long.toString(iterations)));
			swarm.setMaxIterations(iterations);
			swarm.setRunParams(runparams);
			if (heirarchyCheckBox.isSelected())
				swarm.setSwitchOps(Boolean.parseBoolean(runparams.getProperty(
						"heirarchyswitchops", Boolean.toString(true))));
			exp.setSwarm(swarm);
			buf = runparams.getProperty("topologyclass");
			class1 = (Class<?>) Class.forName(buf);
			Constructor<?> con = class1.getConstructor(new Class[]
			{BaseSwarm.class});
			Topology topology = (Topology) con.newInstance(new Object[]
			{swarm});
			topology.setParams(topologyParams);
			exp.setNeighbourhood(topology);

			buf = heuristicParams.getProperty("objectiveclass");
			class1 = (Class<?>) Class.forName(buf);
			TestBase testFunct = (TestBase) class1.getDeclaredConstructor()
					.newInstance();
			testFunct.setParams(testFuncParams);
			exp.setTestParams(testFuncParams);
			exp.setObjective(testFunct);

			if (heirarchyCheckBox.isSelected())
			{
				buf = runparams.getProperty("heirarchyclass");
				class1 = (Class<?>) Class.forName(buf);
				BaseSwarm master = (BaseSwarm) class1.getDeclaredConstructor()
						.newInstance();
				master.setParams(heirarchyParams);
				master.setMaster(true);
				master.setMaxIterations(Long.parseLong(runparams.getProperty(
						"heirarchyiterations", Long.toString(iterations))));
				master.setRunParams(runparams);
				swarm.setMaster(false);
				buf = runparams.getProperty("heirarchyobjective");
				class1 = (Class<?>) Class.forName(buf);
				con = class1.getConstructor(new Class[]
				{TestBase.class, BaseSwarm.class});
				HeirarchyTestBase heirTestFunct = (HeirarchyTestBase) con
						.newInstance(new Object[]
						{testFunct, master});
				heirTestFunct.setParams(heirarchyTestFuncParams);
				master.setTestFunction(heirTestFunct);
				if (Boolean.parseBoolean(
						runparams.getProperty("heirarchyusefips", "true")))
				{

					Set<String> keys = topologies.keySet();
					for (Iterator<String> iterator = keys.iterator(); iterator
							.hasNext();)
					{
						buf = (String) iterator.next();
						if (buf.contains("FIPS"))
							break;
					}
					buf = topologies.get(buf) + "";
					class1 = (Class<?>) Class.forName(buf);
					con = class1.getConstructor(new Class[]
					{BaseSwarm.class});
					Topology mastertopology = (Topology) con
							.newInstance(new Object[]
							{swarm});
					master.setNeighbourhood(mastertopology);
				} else
					master.setNeighbourhood(topology);
				Heirarchy heir = new Heirarchy(master, swarm, heirarchyParams,
						runparams);
				master.setHeir(heir);
				swarm.setHeir(heir);
				exp.setHeir(heir);
			} else
				exp.setHeir(null);

			exp.setRunParams(runparams);
			exp.setNotes(JOptionPane.showInputDialog(frame,
					ENTER_SOME_EXPERIMENT_NOTES));
			addExperiment(exp);
		} catch (ClassNotFoundException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (InstantiationException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (SecurityException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (IllegalArgumentException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, e);
			e.printStackTrace();
		}
		return experiments;
	}

	/** Called when a field's "value" property changes. */
	public void propertyChange(PropertyChangeEvent e)
	{
		Object source = e.getSource();
		if (source == editHeuristicBox)
			updateParam(editHeuristicLabel, editHeuristicBox, heuristicParams,
					paramHeuristicLBox);
		else if (source == editHeirarchyBox)
			updateParam(editHeirarchyLabel, editHeirarchyBox, heirarchyParams,
					paramHeirarchyLBox);
		else if (source == editHeirFuncParamsBox)
			updateParam(editHeirFuncParamsLabel, editHeirFuncParamsBox,
					heirarchyTestFuncParams, paramHeirarchyTestFuncLBox);
		else if (source == editTopologyBox)
			updateParam(editTopologyLabel, editTopologyBox, topologyParams,
					paramTopologyLBox);
		else if (source == editFuncParamsBox)
			updateParam(editFuncParamsLabel, editFuncParamsBox, testFuncParams,
					paramTestFuncLBox);
		else if (source == driverBox)
			runparams.setProperty("driver", driverBox.getText());
		else if (source == connectionBox)
			runparams.setProperty("connection", connectionBox.getText());
		else if (source == userIdField)
			runparams.setProperty("userid", userIdField.getText());
		else if (source == passwordField)
			runparams.setProperty("password", passwordField.getText());
		else if (source == randCombo)
			runparams.setProperty("randgen",
					((Integer) randCombo.getSelectedIndex()).toString());
		else if (source == iterationsField)
		{
			iterations = Long.parseLong(iterationsField.getValue().toString());
			evaluations = computeEvals(iterations, particles, evaluations);
			evaluationsField.setValue(Long.valueOf(evaluations));
			runparams.setProperty("iterations", Long.toString(iterations));

		} else if (source == swarmsIterationsField)
			runparams.setProperty("heirarchyiterations",
					swarmsIterationsField.getValue().toString());
		else if (source == particlesField)
		{
			particles = Long.parseLong(particlesField.getValue().toString());
			evaluations = computeEvals(iterations, particles, evaluations);
			evaluationsField.setValue(Long.valueOf(evaluations));

		} else if (source == evaluationsField)
		{
			evaluations = Long
					.parseLong(evaluationsField.getValue().toString());
			evaluations = computeEvals(iterations, particles, evaluations);
			evaluationsField.setValue(Long.valueOf(evaluations));

		} else if (source == runsField)
			runparams.setProperty("runs", runsField.getValue().toString());
		else if (source == paraCheck)
			runparams.setProperty("paraeval",
					Boolean.toString(paraCheck.isSelected()));
		else if (source == useMemBuffersCheck)
		{
			runparams.setProperty("usemembuffers",
					Boolean.toString(useMemBuffersCheck.isSelected()));
			Util.setUseMem(useMemBuffersCheck.isSelected());
		} else if (source == swarmsField)
			heirarchyParams.setProperty("noswarms",
					swarmsField.getValue().toString());
		applyButton.setEnabled(true);
	}

	// This method must be evoked from the event-dispatching thread.
	public void quit(JFrame frame)
	{
		if (confirm(frame, true))
		{
			Logger logger2 = Log.getLogger();
			if (logger2 != null)
				logger2.info("Quitting.");
			System.exit(0);
		}
	}

	private JTree refreshExpTree()
	{
		if (uet == null)
			try
			{
				uet = new ExpData(runparams, resultTree);
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, e);
				e.printStackTrace();
			}
		resultTree = uet.refresh();
		if (resultTree != null)
		{
			resultTreeRoot = (DefaultMutableTreeNode) resultTree.getModel()
					.getRoot();
			expandAPath(resultTree.getPathForRow(0));
		}
		return resultTree;
	}

	private void removeExperiment()
	{
		if (expNode != null)
		{
			experiments.remove((String) expNode.getUserObject());
			expNode.removeFromParent();
			experimentTree.updateUI();
			expNode = null;
		}

	}

	private void replaceKey(DefaultListModel<String> defaultListModel,
			String key, String value)
	{
		Object obj = null;
		if (!key.equals("") && !value.equals(""))
			for (int i = 0; i < defaultListModel.getSize(); i++)
			{
				obj = defaultListModel.getElementAt(i);

				String str = ((String) obj).trim();
				if (str.startsWith(key.trim()))
				{
					if (!str.endsWith(value.trim()))
					{
						String kv = key + " = " + value;
						defaultListModel.setElementAt(kv, i);
					}
					break;
				}
			}

	}
	private void runExperiments()
	{
		runExperiments = new RunExperiments(experiments);
		runExperiments.start();

	}
	private void setHeirarchyParamsList(Properties params)
	{
		Enumeration<Object> keys = params.keys();
		DefaultListModel<String> listModel = new DefaultListModel<String>();

		// listModel.removeAllElements();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			listModel.addElement(key + " = " + params.getProperty(key));
		}

		paramHeirarchyLBox.setModel(listModel);

	}

	private void setHeuristicParamsList(Properties params)
	{
		Enumeration<Object> keys = params.keys();
		DefaultListModel<String> listModel = new DefaultListModel<String>();

		// listModel.removeAllElements();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			listModel.addElement(key + " = " + params.getProperty(key));
		}
		paramHeuristicLBox.setModel(listModel);

	}

	private void setTestFuncParamsList(Properties params, JList<String> lbox)
	{
		Enumeration<Object> keys = params.keys();
		DefaultListModel<String> listModel = new DefaultListModel<String>();

		// listModel.removeAllElements();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			listModel.addElement(key + " = " + params.getProperty(key));
		}

		lbox.setModel(listModel);
		lbox.setSelectedIndex(0);
	}
	private void setTopologyParamsList(Properties params)
	{
		Enumeration<Object> keys = params.keys();
		DefaultListModel<String> listModel = new DefaultListModel<String>();

		// listModel.removeAllElements();
		while (keys.hasMoreElements())
		{
			String key = (String) keys.nextElement();
			listModel.addElement(key + " = " + params.getProperty(key));
		}

		paramTopologyLBox.setModel(listModel);

	}

	// Create and set up number formats. These objects also
	// parse numbers input by user.
	private void setUpFormats()
	{
		iterationsFormat = NumberFormat.getNumberInstance();

		percentFormat = NumberFormat.getNumberInstance();
		percentFormat.setMinimumFractionDigits(3);

	}

	public synchronized void updateLog(String text)
	{

		if (text.toLowerCase().indexOf("iteration") != -1
				|| text.toLowerCase().indexOf("terminated") != -1
				|| text.toLowerCase().indexOf("complete") != -1
				|| text.toLowerCase().indexOf("initialising") != -1)
			progressLabel.setText(PROGRESS_LOG + " " + text);
		else if (text.toLowerCase().indexOf("results") != -1)
			loggingLabel.setText(RESULTS_LOG + " " + text);
		if (isLogging)
		{
			Main.getLog().info(text);
			if (progressLog.getLineCount() > 50)
			{

				try
				{
					progressLog.getDocument().remove(0,
							progressLog.getText().indexOf(LINESEPARATOR));
				} catch (BadLocationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			progressLog.append(text + LINESEPARATOR);
			progressLog.setCaretPosition(progressLog.getDocument().getLength());
		}
	}

	/**
	 * 
	 */
	private void updateParam(JLabel label, JFormattedTextField edit,
			Properties params, JList<String> lbox)
	{
		String[] kv = new String[2];
		kv[1] = label.getText().replaceFirst(EDIT, "").replaceFirst(":", "");
		kv[1] = kv[1].trim();
		kv[0] = edit.getText().trim();
		if (!kv[1].equals(""))
			params.setProperty(kv[1], kv[0]);
		DefaultListModel<String> defaultListModel = ((DefaultListModel<String>) lbox
				.getModel());
		replaceKey(defaultListModel, kv[1], kv[0]);
	}

	public void valueChanged(ListSelectionEvent e)
	{
		Object source = e.getSource();

		if (source == paramHeuristicLBox && !e.getValueIsAdjusting())
		{
			heuristicParams = editHeuristicParams();
			// setHeuristicParamsList(heuristicParams);
		} else if (source == paramHeirarchyLBox && !e.getValueIsAdjusting())
		{
			heirarchyParams = editHeirarchyParams();
			// setHeirarchyParamsList(heirarchyParams);
		} else if (source == paramTopologyLBox && !e.getValueIsAdjusting())
		{
			topologyParams = editTopologyParams();
			// setTopologyParamsList(topologyParams);
		} else if (source == paramTestFuncLBox && !e.getValueIsAdjusting())
		{
			testFuncParams = editTestFuncParams();
		} else if (source == paramHeirarchyTestFuncLBox
				&& !e.getValueIsAdjusting())
		{
			heirarchyTestFuncParams = editHeirTestFuncParams();
		}

	}

	public void valueChanged(TreeSelectionEvent e)
	{
		JTree source = (JTree) e.getSource();
		DefaultMutableTreeNode newNode = (DefaultMutableTreeNode) source
				.getLastSelectedPathComponent();

		if (newNode.getRoot() == expTreeRoot)
		{
			if (newNode == null || newNode == expTreeRoot)
			{
				expNode = null;
				return;
			}
			expNode = newNode;
			while (expNode.getParent() != expTreeRoot)
				expNode = (DefaultMutableTreeNode) expNode.getParent();
		} else if (newNode.getRoot() == resultTreeRoot)
		{
			if (newNode == null || newNode == resultTreeRoot)
			{
				resNode = null;
				return;
			}
			resNode = newNode;
			while (resNode.getParent() != resultTreeRoot)
				resNode = (DefaultMutableTreeNode) resNode.getParent();
		}
	}

}
