import NearOCR.*;
import NearNeural.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import javax.imageio.*;
/**Displays the main window of the OCR program and contains all other panels.
  @author Stephen Wattam
  @version 0.1
*/
public class OCRSwing extends JFrame implements ActionListener{
	/**The OCRManager that will be passed to all other panels*/
	private OCRManager OCR = new OCRManager();

	/**A path to the icon to use for this program*/
	private final static String ICON_PATH = "data/sourceImages/icon.png";

	/**A panel where the document can be sliced into Letters*/
	private SlicePanel slicePanel;
	/**The receptor manager*/
	private ReceptorPanel receptorPanel;
	/**The resizing panel*/
	//private ResizePanel resizePanel;
	/**The net manager panel*/
	private NetPanel netPanel;
	/**The symol editor panel*/
	private SymbolPanel symbolPanel;
	/**The training manager panel*/
	private TrainPanel trainPanel;
	/**The run manager panel*/
	private RunPanel runPanel;
	/**The menu bar*/
	private JMenuBar menuBar;
	/**The console panel*/
	private ConsolePanel consolePanel;
	/**The p[anel which displays results*/
	private ResultsPanel resultsPanel; 

	/**The tab handler which holds all of the panels*/
	private JTabbedPane tabs = new JTabbedPane();
	
	/**A simple constructor to create the main window*/
	public OCRSwing(){
		super("NearNeural OCR");
		super.setIconImage(new ImageIcon(ICON_PATH).getImage());
		setSize(800, 630);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		menuBar = buildMenu();
		this.setJMenuBar(menuBar);

		slicePanel = new SlicePanel(OCR);
		tabs.add("Document Slicer", slicePanel);


		netPanel = new NetPanel(OCR);
		tabs.add("Net Manager",netPanel);
		
		symbolPanel = new SymbolPanel(OCR);
		tabs.add("Symbol Manager", symbolPanel);

		receptorPanel = new ReceptorPanel(OCR);
		tabs.add("Receptors", receptorPanel);

		trainPanel = new TrainPanel(OCR);
		tabs.add("Net Trainer", trainPanel);

		runPanel = new RunPanel(OCR);
		tabs.add("Run Analysis", runPanel);
	
		resultsPanel = new ResultsPanel(OCR);
		tabs.add("Results", resultsPanel);

		consolePanel = new ConsolePanel();
		tabs.add("Log", consolePanel);


		setContentPane(tabs);
		setVisible(true);
	}

	/**Creates a new menu item with the given string and command string.
	  @param label The string to be shown to the user
	  @param actionCommand The command to send through the actionEvent for this item
	*/  
	private JMenuItem buildMenuItem(String label, String actionCommand){
		JMenuItem menuItem = new JMenuItem(label);
		menuItem.setActionCommand(actionCommand);
		menuItem.addActionListener(this);
		return menuItem;
	}

	/**Builds and adds the whole menu to the frame*/
	private JMenuBar buildMenu(){
		JMenuBar menuBar = new JMenuBar();

		JMenu fileMenu = new JMenu("File");
		/*fileMenu.add(buildMenuItem("Analyse","analyse"));*/
		fileMenu.add(buildMenuItem("Exit","exit_all"));
		menuBar.add(fileMenu);
		
		
		JMenu netMenu = new JMenu("Net");
		netMenu.add(buildMenuItem("New...","new_net"));
		netMenu.add(buildMenuItem("Load...","load_net"));
		menuBar.add(netMenu);

		
		JMenu docMenu = new JMenu("Document");
		docMenu.add(buildMenuItem("Load...","load_doc"));
		docMenu.addSeparator();
		docMenu.add(buildMenuItem("Reslice","reslice_doc"));
		menuBar.add(docMenu);

		JMenu receptorMenu = new JMenu("Receptors");
		receptorMenu.add(buildMenuItem("Load...","load_receptors"));
		receptorMenu.add(buildMenuItem("Save...","save_receptors"));
		menuBar.add(receptorMenu);

		JMenu fontMenu = new JMenu("Symbol set");
		fontMenu.add(buildMenuItem("Load...","load_symbols"));
		fontMenu.add(buildMenuItem("Save...","save_symbols"));
		menuBar.add(fontMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(buildMenuItem("About","about"));
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(helpMenu);

		return menuBar;
	}

	/**Prompts a user to load a document image.*/
	private void loadDoc(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new ImageFileFilter());
		if(fileChooser.showOpenDialog(this) == 0){
			try{
				OCR.loadImage(fileChooser.getSelectedFile());
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error loading document");
			}
		}
	}

	/**Prompts the user to load a new net*/
	private void loadNet(){
		new LoadFrame(this, OCR);
	}

	/**Prompts the user to save all symbols*/
	private void saveSymbols(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new SymbolFileFilter());

		if(fileChooser.showSaveDialog(this) == 0){
			try{
				OCR.getSymbolTable().save(new FileWriter(fileChooser.getSelectedFile()));
			}catch(IOException IOe){
				JOptionPane.showMessageDialog(this, "Error saving.");
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error saving symbols.");
			}
		}
	}

	/**Prompts the user to load all symbols form a file*/
	private void loadSymbols(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileFilter(new SymbolFileFilter());
		if(fileChooser.showOpenDialog(this) == 0){
			try{
				OCR.loadSymbols(fileChooser.getSelectedFile());
				//netPanel.loadNet(OCR.getNet());
			}catch(IOException IOe){
				JOptionPane.showMessageDialog(this, "Error loading symbols.");
			}
		}
	}

	private void loadReceptors(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
	//	fileChooser.setFileFilter(new SymbolFileFilter());
		if(fileChooser.showOpenDialog(this) == 0){
			try{
				ReceptorManager newReceptorManager = new ReceptorManager(new FileReader(fileChooser.getSelectedFile()));
				if(newReceptorManager.count() != OCR.countInputs())
					JOptionPane.showMessageDialog(this, "Wrong number of receptors to fit the nets.");
				else
					OCR.setReceptorManager(newReceptorManager);


				//netPanel.loadNet(OCR.getNet());
			}catch(IOException IOe){
				JOptionPane.showMessageDialog(this, "Error loading Receptors.");
			}
		}
	}
	
	private void saveReceptors(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
	///	fileChooser.setFileFilter(new SymbolFileFilter());

		if(fileChooser.showSaveDialog(this) == 0){
			try{
				OCR.getReceptorManager().save(new FileWriter(fileChooser.getSelectedFile()));
			}catch(IOException IOe){
				JOptionPane.showMessageDialog(this, "Error saving.");
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error saving receptors.");
			}
		}
	}
	
	/**Fired when a component performs an action whilst this class is listening to it.
	  @param Ae The action even generated
	*/
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getActionCommand().equals("exit_all")){
			quit();
		}else if(Ae.getActionCommand().equals("load_doc")){
			loadDoc();
			slicePanel.drawDocument();
		}else if(Ae.getActionCommand().equals("reslice_doc")){
			slicePanel.slice();
			slicePanel.drawDocument();
		}else if(Ae.getActionCommand().equals("load_net")){
			loadNet();
			netPanel.repopulateList();
			trainPanel.repopulateList();
		}else if(Ae.getActionCommand().equals("save_symbols")){
			saveSymbols();
		}else if(Ae.getActionCommand().equals("load_symbols")){
			loadSymbols();
			symbolPanel.repopulateSymbolList();
		}else if(Ae.getActionCommand().equals("about")){
			new AboutFrame(this);
		}else if(Ae.getActionCommand().equals("new_net")){
			new NewNetFrame(this, OCR);
			netPanel.repopulateList();
			trainPanel.repopulateList();
		}else if(Ae.getActionCommand().equals("save_receptors")){
			saveReceptors();
		}else if(Ae.getActionCommand().equals("load_receptors")){
			loadReceptors();
		}
	}

	/*public void paint(Graphics g){
		this.paintComponents(g);
		//slicePanel.drawDocument();
		//resizePanel.drawDocument();
	}*/

	/**Exits the program entirely*/
	private void quit(){
		/*final JOptionPane optionPane = new JOptionPane("Are you sure you wish to quit?",
								    JOptionPane.QUESTION_MESSAGE,
								    JOptionPane.YES_NO_OPTION);

		System.out.println(optionPane.showConfirmDialog());*/
		System.exit(0);
	}

	/**Runs the whole shebang.
	   @param args Thw arguments from the command line.
	 */
	public static void main(String args[]){
		try{
		/*	UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.metal.MetalLookAndFeel");
		*/
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}catch(Exception e){}

		new OCRSwing();
	}
}
