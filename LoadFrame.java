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
public class LoadFrame extends JDialog implements ActionListener{
	/**Allow the user to create the net and insert it into the OCRManager*/
	private JButton create = new JButton("Load");
	/**Allow the user to cancel the net creation process and close the dialog*/
	private JButton cancel = new JButton("Cancel");
	
	/**Allows the user to select what learning rate the whole net is to have*/
	private JSpinner learningRate = new JSpinner(new SpinnerNumberModel(.01,.01,2,.001));
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
	public LoadFrame(Frame owner, OCRManager p_OCR){
		super(owner,"Load Neural Net",true);	//1.6 only
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		
		OCR = p_OCR;

		create.addActionListener(this);
		cancel.addActionListener(this);


		setSize(new Dimension(300,150));
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());

		gbc.gridwidth = 2;
		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(learningRate, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		this.add(backprop, gbc);
		
		gbc.gridwidth = 1;
		gbc.gridx = 0;
		gbc.gridy = 2;
		this.add(threshold, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		this.add(k, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		this.add(cancel,gbc);

		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		this.add(create,gbc);

		
		setVisible(true);
	}

	/**Creates a new neural net accoring to the options chosen by the user.
	   @param fin The file to load from
	   @throws IndexOutOfBoundsException in the event that the creation of the net fails due to the same error
	   @throws ValueOutOfBoundsException if any of the neuron counts are inapplicable
	 */
	private NeuralNet createNet(File fin) throws FileNotFoundException, IOException{

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
		

		NeuralNet net = new NeuralNet(new FileReader(fin), thresholdToUse, backpropToUse);

		return net;
	}


	/**Is called whenever a component fires an actionEvent.
	   @param Ae The actionEvent as fired by the component.
	 */
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == cancel){
			dispose();
		}else if(Ae.getSource() == create){
			final JFileChooser fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(false);
			fileChooser.setFileFilter(new NetFileFilter());
			if(fileChooser.showOpenDialog(this) == 0){
				try{
					OCR.appendNet(createNet(fileChooser.getSelectedFile()));
					//netPanel.loadNet(OCR.getNet());
				}catch(FileNotFoundException FNFe){
					JOptionPane.showMessageDialog(this, "Error creating net, file not found.");
				}catch(IOException IOe){
					JOptionPane.showMessageDialog(this, "Error creating default net.");
				}catch(NearNeural.ValueOutOfBoundsException VOOBe){
					JOptionPane.showMessageDialog(this, "Error creating net: invalid file");
				}
			}
			dispose();
		}
	}


}	
