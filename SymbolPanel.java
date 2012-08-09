import NearOCR.*;
import NearNeural.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.Vector;
//import java.awt.image.RescaleOp;
/**
  @author Stephen Wattam
  @version 0.1
*/
public class SymbolPanel extends JPanel implements ListSelectionListener, ActionListener, ProgressListener{
	/**Stores a reference to the OCR manager*/	
	private OCRManager OCR;

	/**A progress bar to monitor symbol analysis progress*/
	private JProgressBar progress = new JProgressBar(SwingConstants.HORIZONTAL);	
	
	/**A list of all symbols loaded*/
	private JList symbolList;
	/**Allows the list of symbols to scroll*/
	private JScrollPane symbolPanel;
	/**A list of all output neuron weights*/
	private JList neuronList;
	/**Allows the list of outputs to scroll*/
	private JScrollPane neuronPanel;

	/**A list of symbols, as taken from the OCRManager, which are currently loaded*/
	private Symbol[] symbols = new Symbol[0];
	/**A panel on which to draw the images of the symbols*/
	private JPanel img = new JPanel();

	/**A button to add symbols*/
	private JButton add = new JButton("Add");
	/**A button to remove symbols*/
	private JButton remove = new JButton("Remove");
	/**A text field to select the value of the current symbol*/
	private JTextField value = new JTextField("Value");
	/**A button to apply the typed value*/
	private JButton apply = new JButton("Apply");
	/**A button to analyse symbol weights against all current nets*/
	private JButton analyse = new JButton("Analyse");
	/**A button to reset the weights*/
	private JButton reset = new JButton("Reset");
	/**A panel on which to place all of the symbol related command components*/
	private JPanel symbolEditor = new JPanel(new GridLayout(8,1,5,5));

	/**A panel on which to place all weighted output editing components*/
	private JPanel weightEditor = new JPanel(new GridLayout(3,2,5,5));
	/**A spinner to select the weight of the current node*/
	private JSpinner weight = new JSpinner(new SpinnerNumberModel(0, -200, 200, 0.01));
	/**A spinner to select the target value of the current node*/
	private JSpinner target = new JSpinner(new SpinnerNumberModel(0,0,1,0.01));
	/**A label to indicate the weight spinner*/
	private JLabel weightLabel = new JLabel("Weight");
	/**A label to indicate the targhet spinner*/
	private JLabel targetLabel = new JLabel("Desired");
	/**A button to apply weights*/
	private JButton applyWeight = new JButton("Apply");
	/**A button to refresh the current values from the selected node*/
	private JButton refreshWeight = new JButton("Refresh");
	
	/**Creates a new symbol editing panel linked to the given OCRManager.
	  @param p_OCR The OCRManager to query as to symbols.
	*/
	public SymbolPanel(OCRManager p_OCR){
		OCR=p_OCR;
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		progress.setPreferredSize(new Dimension(750,20));
		progress.setStringPainted(true);


		symbolEditor.add(img);
		img.setPreferredSize(new Dimension(50,50));
		symbolEditor.add(add);
		remove.setEnabled(false);
		symbolEditor.add(remove);
		value.setEnabled(false);
		symbolEditor.add(value);
		apply.setEnabled(false);
		symbolEditor.add(apply);
		//analyse.setEnabled(false);
		symbolEditor.add(analyse);
		symbolEditor.add(reset);

		add.addActionListener(this);
		remove.addActionListener(this);
		apply.addActionListener(this);
		applyWeight.addActionListener(this);
		analyse.addActionListener(this);
		refreshWeight.addActionListener(this);
		reset.addActionListener(this);

		weightLabel.setLabelFor(weight);
		weightEditor.add(weightLabel);
		weight.setEnabled(false);
		weightEditor.add(weight);
		
		
		targetLabel.setLabelFor(target);
		weightEditor.add(targetLabel);
		weightEditor.add(target);
		target.setEnabled(false);
		weightEditor.add(refreshWeight);
		refreshWeight.setEnabled(false);
		weightEditor.add(applyWeight);
		applyWeight.setEnabled(false);

		//symbol list
		symbolList = new JList(symbols);
		symbolList.addListSelectionListener(this);
		SymbolCell symbolRenderer = new SymbolCell();
		symbolRenderer.setPreferredSize(new Dimension(200,20));
		symbolList.setCellRenderer(symbolRenderer);
		symbolPanel = new JScrollPane(symbolList);
		symbolPanel.setPreferredSize(new Dimension(200,500));
		
		neuronList = new JList(new WeightedNode[0]);
		neuronList.addListSelectionListener(this);
		NeuronCell neuronRenderer = new NeuronCell();
		neuronRenderer.setPreferredSize(new Dimension(230,20));
		neuronList.setCellRenderer(neuronRenderer);
		neuronPanel = new JScrollPane(neuronList);
		neuronPanel.setPreferredSize(new Dimension(250,500));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 200;
		gbc.ipady = 500;
		gbc.fill = GridBagConstraints.CENTER;
		gbc.weightx = 0.5;
		this.add(symbolPanel,gbc);


		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0.5;
		this.add(symbolEditor,gbc);

		
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.ipadx = 250;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		this.add(neuronPanel,gbc);
	
		gbc.gridx = 3;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		this.add(weightEditor,gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 780;
		gbc.ipady = 0;
		gbc.gridwidth = 4;
		this.add(progress, gbc);


		setVisible(true);
	}

	/**Reloads and repopulates the list of symbols from the internally stored OCRManager.*/
	public void repopulateSymbolList(){
		symbolList.setListData(OCR.getSymbolTable().getSymbols());

		//System.out.println(symbolList.getMaxSelectionIndex());

	/*	if(symbolList.getMaxSelectionIndex() != -1)
			analyse.setEnabled(true);
		else
			analyse.setEnabled(false);
	*/
	}
	
	/**Resets all weights to 1*/
	public void resetWeights(){
		OCR.setAllWeights(1.0);
	}

	/**Repopulates all weighted nodes form the current symbol*/
	public void repopulateOutputsList(){
		if(symbolList.getSelectedIndex() != -1){
			if(OCR.countOutput() != OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).getWeights().length)
				OCR.getSymbolTable().setLength(OCR.countOutput());

			neuronList.setListData(OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).getWeights());

			/*MOVE THIS INTO THE INDIVIDUAL LIST CELLS FOR SYMBOLS!!! 

			  PS. this cannot be done, blame swing and ignore the below comment ;)*/			
			Graphics2D g2d = (Graphics2D)img.getGraphics();
			if(g2d != null){
				//	System.out.println("Yey!");
				g2d.setColor(new Color(128,128,196));
				g2d.fillRect(0,0,img.getWidth(), img.getHeight());
				g2d.drawImage(OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).getImage(), null, 0,0);
			}
			/*GET THAT? GOOD! DO IT SOON*/



			/*outputs = new WeightedNode[outputCount];
			
			for(int i=0;i<outputs.length;i++)
				outputs[i] = new WeightedNode(i,0);
			
		

			WeightedNode[] realWeights = OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).getWeights();
			for(int i=0;i<realWeights.length;i++)
				outputs[realWeights[i].getIndex()] = realWeights[i];
			*/
		}else{
			neuronList.setListData(new WeightedNode[0]);
		}
	}

	/**Allows the user to load a set of symbols from files.	*/
	private void addSymbol(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setFileFilter(new ImageFileFilter());
		if(fileChooser.showOpenDialog(this) == 0){
			try{
				File[] files = fileChooser.getSelectedFiles();
				for(File f:files){
					int i=f.getName().lastIndexOf(".");
					String filename = f.getName().substring(0, i);
					OCR.addSymbol(new Symbol(filename,f.getPath()));
				}

			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error loading document");
			}
		}

	}
	
	/**Reloads the spinner values for weight and desired value from the selected weighted node*/
	private void resetWeightAndTarget(){
		if(neuronList.getSelectedIndex() != -1){	//one last check, don't trust input yadda yadda...
			//System.out.println("WWW");
			target.getModel().setValue(new Double(((WeightedNode)neuronList.getSelectedValue()).getTarget()));
			weight.getModel().setValue(new Double(((WeightedNode)neuronList.getSelectedValue()).getWeight()));
		}
	}

	/**Fires when a component to which this object is attached fires an actionEvent.
	  @param Ae The actionevent fired.
	*/  
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == add){
			addSymbol();
			repopulateSymbolList();
			repopulateOutputsList();
		}else if(Ae.getSource() == remove){
			if(symbolList.getSelectedIndex() != -1){
				OCR.getSymbolTable().remove(symbolList.getSelectedIndex());
				repopulateSymbolList();
				repopulateOutputsList();
			}
		}else if(Ae.getSource() == apply){
			if(symbolList.getSelectedIndex() != -1){
				OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).setValue(value.getText());
				repopulateSymbolList();
				repopulateOutputsList();
			}
		}else if(Ae.getSource() == applyWeight){
			WeightedNode tempWeight = (WeightedNode)neuronList.getSelectedValue();
			tempWeight.setWeight(((SpinnerNumberModel)weight.getModel()).getNumber().doubleValue());
			tempWeight.setTarget(((SpinnerNumberModel)target.getModel()).getNumber().doubleValue());
			repopulateOutputsList();

			//OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).getWeight(neuronList.getSelectedIndex()).setWeight(((SpinnerNumberModel)weight.getModel()).getNumber().doubleValue());
			//OCR.getSymbolTable().getSymbol(symbolList.getSelectedIndex()).getWeight(neuronList.getSelectedIndex()).setWeight(((SpinnerNumberModel)target.getModel()).getNumber().doubleValue());
		}else if(Ae.getSource() == analyse){
			WeightAnalysisDispatcher analysisThread = new WeightAnalysisDispatcher(OCR, this);
			analysisThread.start();
		}else if(Ae.getSource() == refreshWeight){
			resetWeightAndTarget();
		}else if(Ae.getSource() == reset){
			resetWeights();
		}
	}

	/**Fires when someone makes a selection on either list.
	  @param LSe The list selection event
	*/
	public void valueChanged(ListSelectionEvent LSe){
		if(LSe.getSource() == symbolList ){
			if(symbolList.getSelectedIndex() != -1){
				remove.setEnabled(true);
				apply.setEnabled(true);
				value.setEnabled(true);
				refreshWeight.setEnabled(true);

				repopulateOutputsList();
			}else{
				remove.setEnabled(false);
				apply.setEnabled(false);
				value.setEnabled(false);
				refreshWeight.setEnabled(false);
				repopulateOutputsList();
			}
		}else if(LSe.getSource() == neuronList){
			if(neuronList.getSelectedIndex() != -1){
				target.setEnabled(true);
				weight.setEnabled(true);
				refreshWeight.setEnabled(true);
				applyWeight.setEnabled(true);	
				resetWeightAndTarget();
				//weight.setValue(OCR.getSymbolTable().getSymbol(neuronList.getSelectedIndex()).getWeight());
				//target.setValue(OCR.getSymbolTable().getSymbol(neuronList.getSelectedIndex()).getWeight());
			}else{
				target.setEnabled(false);
				weight.setEnabled(false);
				refreshWeight.setEnabled(false);
				applyWeight.setEnabled(false);
			}
		}
	}

	/**Fired when a value is changed in the analysis process.
	  @param progress The progress made thus far
	  @param limit The maxmimum possible value of progress.
	 */
	public void valueChanged(double progress, double limit){
		if(this.progress.getMaximum() != (int)limit)
			this.progress.setMaximum((int)limit);

		this.progress.setValue((int)progress);
	}

	/**Fired when any event occurs in the analysis cycle.
	  @param action The event description
	*/
	public void eventOccurred(String action){
	}

	/**Fired when an error occurs during the analysis process.
	  @param error The error description
	*/
	public void errorOccurred(String error){
	}
}
