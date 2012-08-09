import NearNeural.*;
import NearOCR.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import javax.imageio.*;
import java.util.*;

/**Creates a new frame which allows the user to create a net with the parameters chosen.  The net itself is not actually built until the dialog is closed by way of 'Save' or 'Create', only a list is built from wich neural layers are built.

  @author Stephen Wattam
  @version 0.1
*/  
public class NewNetFrame extends JDialog implements ActionListener{
	/**Stores a list of all layers*/
	private JList layerList;
	/**Allows te list of layers to scroll*/
	private JScrollPane layerPanel;
	/**Stores the number of neurons in each layer*/
	private Vector<Integer> layers = new Vector<Integer>();

	/**Allows the user to select how many neurons are to be in this layer*/
	private JSpinner numberOfNeurons = new JSpinner(new SpinnerNumberModel(1,1,1000,1));
	/**Allows the user to select what learning rate the whole net is to have*/
	private JSpinner learningRate = new JSpinner(new SpinnerNumberModel(.01,.01,2,.001));
	/**Allows the user to create a new layer*/
	private JButton createLayer = new JButton("New");
	/**Allow the user to remove a layer*/
	private JButton removeLayer = new JButton("Delete");
	/**Allow the user to move a layer up*/
	private JButton moveLayerUp = new JButton("Move up");
	/**Allow the user to move a layer down*/
	private JButton moveLayerDown = new JButton("Move down");
	/**Allow the user to create the net and insert it into the OCRManager*/
	private JButton create = new JButton("Create");
	/**Allow the user to save the net as a file*/
	private JButton save = new JButton("Save...");
	/**Allow the user to cancel the net creation process and close the dialog*/
	private JButton cancel = new JButton("Cancel");
	
	/**Stores the names of the backprop options*/
	private String[] backpropMethods = {"Threaded backprop", "Single thread backprop"};
	/**Lets the user select the backprop method to use.*/
	private JComboBox backprop = new JComboBox(backpropMethods);
	
	/**Stores the strings describing the thresholding algorithms available*/
	private String[] thresholdAlgorithms = {"Sigmoid","Hyperbolic tangent", "Sinusoidal"};
	/**Lets the user select a threshold*/
	private JComboBox threshold = new JComboBox(thresholdAlgorithms);
	
	/**Lets the user choose a 'k' value for the threshold*/
	private JSpinner k = new JSpinner(new SpinnerNumberModel(1,.01,5,.01));

	/**Stores a reference to the original OCR manager.*/
	private OCRManager OCR;

	/**Creates and shows a new net building dialog.
	  @param owner The Frame to exclude with modal nature
	  @param p_OCR The OCRManager to insert the net into
	*/
	public NewNetFrame(Frame owner, OCRManager p_OCR){
		super(owner,"New Neural Net",true);	//1.6 only
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		OCR = p_OCR;

		createLayer.addActionListener(this);
		removeLayer.addActionListener(this);
		moveLayerDown.addActionListener(this);
		moveLayerUp.addActionListener(this);
		create.addActionListener(this);
		cancel.addActionListener(this);
		save.addActionListener(this);


		setSize(new Dimension(300,300));
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		layerList = new JList(layers);
		layerPanel = new JScrollPane(layerList);
		layerPanel.setPreferredSize(new Dimension(50, 50));

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 50;
		gbc.ipady = 150;
		gbc.gridheight = 8;
		this.add(layerPanel,gbc);

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.gridheight = 1;
		this.add(numberOfNeurons,gbc);

		gbc.gridx = 2;
		gbc.gridy = 0;
		this.add(createLayer,gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		this.add(moveLayerUp,gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		this.add(moveLayerDown,gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		this.add(removeLayer,gbc);

		gbc.gridx = 1;
		gbc.gridy = 4;
		this.add(learningRate,gbc);

		gbc.gridy = 5;
		gbc.gridx = 1;
		this.add(backprop,gbc);

		gbc.gridy = 6;
		gbc.gridx = 1;
		this.add(threshold,gbc);

		gbc.gridy = 7;
		gbc.gridx = 1;
		this.add(k, gbc);

		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		this.add(cancel,gbc);

		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(save,gbc);
		

		gbc.gridx = 2;
		gbc.gridy = 8;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(create,gbc);

		
		setVisible(true);
	}

	/**Creates a new neural net accoring to the options chosen by the user.
	   @throws IndexOutOfBoundsException in the event that the creation of the net fails due to the same error
	   @throws ValueOutOfBoundsException if any of the neuron counts are inapplicable
	 */
	private NeuralNet createNet() throws NearNeural.IndexOutOfBoundsException, NearNeural.ValueOutOfBoundsException{
		Integer[] tempLayers = (Integer[])layers.toArray(new Integer[layers.size()]);
		int[] tempLayers2 = new int[tempLayers.length-1];

		for(int i=1;i<tempLayers.length;i++)
			tempLayers2[i-1] = tempLayers[i].intValue();

		double[] dummyInput = new double[tempLayers[0].intValue()];
		for(int i=0;i<dummyInput.length;i++)
			dummyInput[i] = 0;	//set up for sigmoid, hyptan

		BackPropagationMethod backpropToUse;
		double learningRateToUse = ((SpinnerNumberModel)learningRate.getModel()).getNumber().doubleValue();

		switch(backprop.getSelectedIndex()){
			case 0: backpropToUse = new ThreadedUnweightedBackPropagationMethod(learningRateToUse); break;
			case 1: backpropToUse = new UnweightedBackPropagationMethod(learningRateToUse); break;
			default: backpropToUse = new UnweightedBackPropagationMethod(learningRateToUse); break;
		}

		ThresholdingAlgorithm thresholdToUse;
		double kToUse = ((SpinnerNumberModel)k.getModel()).getNumber().doubleValue();


		switch(backprop.getSelectedIndex()){
			case 0: thresholdToUse = new SigmoidThresholdModel(kToUse); break;
			case 1: thresholdToUse = new HyperbolicTangentThresholdModel(kToUse); break;
			case 2: thresholdToUse = new SinusoidalThresholdModel(kToUse); break;
			default: thresholdToUse = new SigmoidThresholdModel(kToUse); break;
		}

		NeuralNet net = new NeuralNet(dummyInput, tempLayers2, thresholdToUse, backpropToUse);

		return net;
	}

	/**Saves a net to a file.
	  @param net The net to save
	*/
	private void saveNet(NeuralNet net){
		
		try{

			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new NetFileFilter());
			fileChooser.setMultiSelectionEnabled(false);

			if(fileChooser.showSaveDialog(this) == 0)
				net.save(new FileWriter(fileChooser.getSelectedFile()));
			
		}catch(IOException IOe){
			JOptionPane.showMessageDialog(this, "Error saving.");
		}
	}

	/**Creates a new layer and adds it to the JList, but not to the OCRManager or the provisional net.*/
	public void createNewLayer(){
		layers.add(new Integer(((SpinnerNumberModel)numberOfNeurons.getModel()).getNumber().intValue()));
		repopulateList();
	}

	/**Removes a provisional layer from the list*/
	public void removeLayer(){
		if(layerList.getSelectedIndex() != -1){
			layers.remove(layerList.getSelectedIndex());
			repopulateList();
		}
	}

	/**Repopulates and redraws the net layer list.*/
	public void repopulateList(){
		layerList.setListData(layers);
	}

	/**Is called whenever a component fires an actionEvent.
	   @param Ae The actionEvent as fired by the component.
	 */
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == cancel){
			dispose();
		}else if(Ae.getSource() == createLayer){
			createNewLayer();
		}else if(Ae.getSource() == removeLayer){
			removeLayer();
		}else if(Ae.getSource() == save){
			try{
				saveNet(createNet());
				dispose();
			}catch(NearNeural.IndexOutOfBoundsException IOOBe){
				JOptionPane.showMessageDialog(this, "Error creating net to save, check your parameters.");
			}catch(NearNeural.ValueOutOfBoundsException VOOBe){
				JOptionPane.showMessageDialog(this, "Error creating default net.");
			}
		}else if(Ae.getSource() == create){
			try{
				OCR.appendNet(createNet());
				dispose();
			}catch(NearNeural.IndexOutOfBoundsException IOOBe){
				JOptionPane.showMessageDialog(this, "Error creating net, check your parameters.");
			}catch(NearNeural.ValueOutOfBoundsException VOOBe){
				JOptionPane.showMessageDialog(this, "Error creating default net.");
			}
		}else if(Ae.getSource() == moveLayerUp){
			int index = layerList.getSelectedIndex();
			if(index > 0){
				Integer tempInt = layers.remove(index);
				layers.insertElementAt(tempInt, index-1);
			}
			repopulateList();
		}else if(Ae.getSource() == moveLayerDown){
			int index = layerList.getSelectedIndex();
			if(index >= 0 && index < layers.size()){
				Integer tempInt = layers.remove(index);
				layers.insertElementAt(tempInt, index + 1);
			}
			repopulateList();

		}
	}


}	
