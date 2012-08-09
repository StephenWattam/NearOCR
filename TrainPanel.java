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
import javax.swing.*;
//import java.awt.image.RescaleOp;
/**A panel that allows the user to train nets in the net stack.
  @author Stephen Wattam
  @version 0.1
*/
public class TrainPanel extends JPanel implements ActionListener, ListSelectionListener, ProgressListener, ItemListener{
	/**The OCRManager to be used for training data and nets*/
	private OCRManager OCR;
	/**The progress bar to monitor training progress*/
	private JProgressBar progress = new JProgressBar(SwingConstants.HORIZONTAL);	

	/**Stores a list of nets*/
	private JList netList;
	/**Allows the list of nets to scroll*/
	private JScrollPane listPanel;

	/**Shows a log of all training*/
	private JTextArea log = new JTextArea();
	/**Allows the output to scroll*/
	private JScrollPane logPanel;

	/**A spinner to select the amount of noise to add to images*/
	private JSpinner entropy = new JSpinner(new SpinnerNumberModel(0,0,1,0.001));
	/**A lebel to show what the entropy spinner does*/
	private JLabel entropyLabel = new JLabel("Entropy");
	/**A spinner to allow the user to select the learning rate*/
	private JSpinner learningRate = new JSpinner(new SpinnerNumberModel(0,0,1,0.001));
	/**A label to indicate the learning rate spinner to the user*/
	private JLabel learningRateLabel = new JLabel("Learning Rate");
	/**A spinner to allow the user to select the number of cycles during training*/
	private JSpinner cycles = new JSpinner(new SpinnerNumberModel(0,0,1000000,1));
	/**A label to indicate the cycle spinner*/
	private JLabel cyclesLabel = new JLabel("Training cycles");
	/**A spinner to allow the user to select loops*/
	private JSpinner loops = new JSpinner(new SpinnerNumberModel(0,0,100,1));
	/**A label to indicate the loops selector to users*/
	private JLabel loopsLabel = new JLabel("Iterations/symbol");
	/**A spinner to select the target MSE*/
	private JSpinner MSEMin = new JSpinner(new SpinnerNumberModel(0,0,1,0.001));
	/**A label to indicate he MSE selector spinner*/
	private JLabel MSEMinLabel = new JLabel("Target MSE");
	/**Lets the user select whether or not to update the log*/
	private JCheckBox logUpdate = new JCheckBox("Update log");
	/**Holds if the log updates or not*/
	private boolean updateLog = true;


	/**A button to apply the learning rate*/
	private JButton applyLR = new JButton("Apply");
	/**A button to refresh training data form the selected net*/
	private JButton resetLR = new JButton("Reset");
	/**A button to train the net*/
	private JButton train = new JButton("Train");
	/**A button to clear the log output*/
	private JButton clear = new JButton("Clear");
	/**A panel in which all training stats are placed*/
	private JPanel trainingStats = new JPanel(new GridLayout(9,1,5,5));
	/*The tedium is setting in, it really is quite bad, I'm rather hoping javadoc, is just a silly fad.*/	
	/**Creates a new panel which displays net information.

	  @param p_OCR The OCR manager which controls the OCR process.
	*/
	public TrainPanel(OCRManager p_OCR){
		OCR=p_OCR;
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		train.addActionListener(this);
		applyLR.addActionListener(this);
		resetLR.addActionListener(this);
		clear.addActionListener(this);

		progress.setPreferredSize(new Dimension(750,20));
		progress.setStringPainted(true);

		logUpdate.setSelected(true);
		logUpdate.addItemListener(this);
		
		netList = new JList();
		NetCell netRenderer = new NetCell();
		netRenderer.setPreferredSize(new Dimension(250,60));
		netList.setCellRenderer(netRenderer);
		netList.addListSelectionListener(this);
		repopulateList();
		
		listPanel = new JScrollPane(netList);
		listPanel.setPreferredSize(new Dimension(250,500));

		logPanel = new JScrollPane(log);
		//log.setEditable(false);
		logPanel.setPreferredSize(new Dimension(220,500));

		//trainingStats.setPreferredSize(new Dimension(200,500));
		learningRateLabel.setLabelFor(learningRate);
		trainingStats.add(learningRateLabel);
		trainingStats.add(learningRate);
		trainingStats.add(resetLR);
		trainingStats.add(applyLR);
		resetLR.setEnabled(false);
		applyLR.setEnabled(false);

		cyclesLabel.setLabelFor(cycles);
		trainingStats.add(cyclesLabel);
		trainingStats.add(cycles);

		entropyLabel.setLabelFor(entropy);
		trainingStats.add(entropyLabel);
		trainingStats.add(entropy);

		loopsLabel.setLabelFor(loops);
		trainingStats.add(loopsLabel);
		trainingStats.add(loops);

		MSEMinLabel.setLabelFor(MSEMin);
		trainingStats.add(MSEMinLabel);
		trainingStats.add(MSEMin);

		trainingStats.add(train);
		train.setEnabled(false);
		trainingStats.add(clear);

		trainingStats.add(logUpdate);

		gbc.fill = GridBagConstraints.CENTER;

		gbc.gridx=0;
		gbc.gridy=0;
		//gbc.gridheight = 1;
		//gbc.gridwidth = 3;
		gbc.ipadx = 250;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		this.add(listPanel,gbc);


		gbc.gridx=1;
		gbc.gridy=0;
		//gbc.gridheight = 1;
		//gbc.gridwidth = 3;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.weightx = 0.2;
		this.add(trainingStats,gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.ipadx = 220;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		this.add(logPanel, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.ipadx = 780;
		gbc.ipady = 0;
		gbc.gridwidth = 3;
		this.add(progress, gbc);
		
		setVisible(true);
	}

	/**Fired when an itemevent firing component changes.
	  @param Ie The itemevent sent
	*/
	public void itemStateChanged(ItemEvent Ie){
		if(Ie.getSource() == logUpdate){
			updateLog = !updateLog;
		}
	}
	
	/**Repopulates the list of neural nets*/
	public void repopulateList(){
		netList.setListData(OCR.getNets());
	}

	/**Trains the net using the values as in the ui*/
	public void train(){
			//work out the offset of this net in the whole set of outputs.
			int netIndex = netList.getSelectedIndex();
			int nodeOffset = 0;
			for(int i=netIndex;i>0;i--)	//count offset
				nodeOffset += OCR.getNet(i).getLayerStructure()[OCR.getNet(i).getLayerStructure().length-1];		

			TrainingDispatcher trainingTask = new TrainingDispatcher(OCR.getNet(netList.getSelectedIndex()), OCR.getSymbolTable().getSymbols(), ((Number)loops.getModel().getValue()).intValue(), ((Number)cycles.getModel().getValue()).intValue(), this, ((Number)MSEMin.getModel().getValue()).doubleValue(), nodeOffset, ((Number)entropy.getModel().getValue()).doubleValue(), OCR);
			
			trainingTask.start();


	}

	/**Fired when a component performs an action whilst this class is listening to it.
	  @param Ae The action even generated
	*/
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == resetLR){
			resetLR();
		}else if(Ae.getSource() == applyLR){
			((UnweightedBackPropagationMethod)((NeuralNet)netList.getSelectedValue()).getBackPropagationMethod()).setLearningRate(((Number)learningRate.getModel().getValue()).doubleValue());
			repopulateList();
		}else if(Ae.getSource() == train){
			train();

			//public double trainAllSymbols(NeuralNet net, Symbol[] symbols, int iterationsPerSymbol, int iterations, ProgressListener listener, double minMSE
		}else if(Ae.getSource() == clear){
			log.setText("");
		}
	}

	/**Reloads the learning rate*/
	private void resetLR(){
		if(netList.getSelectedIndex() != -1){	//one last check, don't trust input yadda yadda...
			//System.out.println("WWW");
			learningRate.getModel().setValue(new Double(((UnweightedBackPropagationMethod)((NeuralNet)netList.getSelectedValue()).getBackPropagationMethod()).getLearningRate()));
		}
	}

	/**Changed when the list selection changes.
	  @param LSe The list event as fired when a list selection changes
	*/
	public void valueChanged(ListSelectionEvent LSe){
		if(LSe.getSource() == netList && netList.getSelectedIndex() != -1){
			resetLR();
			resetLR.setEnabled(true);
			applyLR.setEnabled(true);
			train.setEnabled(true);
		}else{
			resetLR.setEnabled(false);
			applyLR.setEnabled(false);
			train.setEnabled(false);
		}
	}

	/**Fired when a value is changed in the training process.
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
		if(updateLog){
			log.append(action);
			logPanel.getVerticalScrollBar().setValue(logPanel.getVerticalScrollBar().getMaximum());
		}
		progress.setString(action);
	}

	/**Fired when an error occurs during the analysis process.
	  @param error The error description
	*/
	public void errorOccurred(String error){
		if(updateLog){
			log.append("\n-->>" + error);
		}
	}

}
