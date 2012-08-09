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
/**A panel, to be a tab, that represents net data to the user.
<pre>
  I sit here writing JavaDoc,
  It's what I have to do,
  So to break the tedium,
  I wrote this up for you.

  For prose is so archaeic,
  it's dull and rather dry,
  so to spice it up a bit,
  I'll give couplets a try.

  This class, extending JPanel,
  is really not that ace.
  It picks a load of data out,
  and shoves it in your face.
</pre>
  @author Stephen Wattam
  @version 0.1
*/
public class NetPanel extends JPanel implements ListSelectionListener, ActionListener{
	/**Stores a reference to the OCR manager*/	
	private OCRManager OCR;

	/**A list which contains all nets in the stack*/
	private JList netList;
	/**A scrolling pane which allows the net list to scroll*/
	private JScrollPane listPanel;

	/**A spinner that allows the user to select which values to prune using*/
	private JSpinner pruneVal = new JSpinner(new SpinnerNumberModel(.001,.001,10,.001));
	/**A button which allows the user to prune the net*/
	private JButton prune = new JButton("Prune");

	/**A panel containing global net info*/
	private JPanel globalInfo = new JPanel(new GridLayout(12,1,5,5));
	/**A label showing the number of global input neurons*/
	private JLabel globalInputNeuronCount = new JLabel();
	/**A label showing the number of total outputs*/
	private JLabel globalOutputNeuronCount = new JLabel();
	/**A label showing a global count of all neurons in the stack*/
	private JLabel globalNeuronCount = new JLabel();
	/**A label showing a global count of all edges*/
	private JLabel globalEdgeCount = new JLabel();

	/**A button to allow the user to redraw the neural visualiser*/
	private JButton redraw = new JButton("Details");
	/**Allows a user to move a net up in the stack*/
	private JButton up = new JButton("Move up");
	/**Allows a user to move a net down in the stack*/
	private JButton down = new JButton("Move down");
	/**Allows the user to remove a net from the stack*/
	private JButton remove = new JButton("Remove");
	/**Allows the user to forcibly update values for the detail view*/
	private JButton precalc = new JButton("Precalc");
	/**Allows the user to save a net as a file*/
	private JButton save = new JButton("Save...");
	
	/**A GridBagConstraints object to allow the visaliser to find its proper location during remove and add*/
	private GridBagConstraints gbc = new GridBagConstraints();

	/*The tedium is setting in, it really is quite bad, I'm rather hoping javadoc, is just a silly fad.*/	
	/**Creates a new panel which displays net information.

	  @param p_OCR The OCR manager which controls the OCR process.
	*/
	public NetPanel(OCRManager p_OCR){
		OCR=p_OCR;
		setLayout(new GridBagLayout());

		netList = new JList();
		NetCell netRenderer = new NetCell();
		netRenderer.setPreferredSize(new Dimension(240,60));
		netList.setCellRenderer(netRenderer);
		netList.addListSelectionListener(this);
		repopulateList();

		listPanel = new JScrollPane(netList);
		listPanel.setPreferredSize(new Dimension(250,500));
		//listPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//listPanel.getVerticalScrollBar().addAdjustmentListener(this);
		//listPanel.getHorizontalScrollBar().addAdjustmentListener(this);
		
		drawNetInfo();
		drawGlobalInfo();

		globalInfo.add(pruneVal);
		pruneVal.setEnabled(false);
		globalInfo.add(prune);
		prune.setEnabled(false);
		globalInfo.add(precalc);
		precalc.setEnabled(false);
		globalInfo.add(redraw);
		redraw.setEnabled(false);
		globalInfo.add(up);
		up.setEnabled(false);
		globalInfo.add(down);
		down.setEnabled(false);
		globalInfo.add(remove);
		remove.setEnabled(false);
		globalInfo.add(save);
		save.setEnabled(false);

		up.addActionListener(this);
		down.addActionListener(this);
		remove.addActionListener(this);
		redraw.addActionListener(this);
		save.addActionListener(this);
		prune.addActionListener(this);
		precalc.addActionListener(this);

		globalInfo.add(globalInputNeuronCount);
		globalInfo.add(globalOutputNeuronCount);
		globalInfo.add(globalNeuronCount);
		globalInfo.add(globalEdgeCount);


		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.ipadx = 250;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		this.add(listPanel,gbc);

		/*gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridheight = 1;
		this.add(netInfo,gbc);
		*/

		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		gbc.fill = GridBagConstraints.CENTER;
		this.add(globalInfo, gbc);

		//now set up
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.ipadx = 350;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		
		
		setVisible(true);
	}

	/**Rcalculates and draws information about the global net situation.  This is a very slow method as it must iterate through all nets and all edges in order to calculate counts.*/
	public void drawGlobalInfo(){
		NeuralNet tempNets[] = OCR.getNets();

		int outputCount = 0;
		if(tempNets.length != 0){
			int neuronCount = 0;
			int edgeCount = 0;
			for(int i=0;i<tempNets.length;i++){
				int layers[] = tempNets[i].getLayerStructure();
				outputCount += layers[(layers.length-1)];
				for(int j=0;j<layers.length;j++){
					if(j > 0){
						try{
							Neuron[] neurons = tempNets[i].getLayer(j).getNeurons();		
							for(Neuron n:neurons)
								edgeCount += ((ReceptiveNeuron)n).countEdges();
						}catch(NearNeural.IndexOutOfBoundsException IOOBe){}
					}
					neuronCount += layers[j];

				}
			}
			globalInputNeuronCount.setText("Inputs: " + OCR.countInputs());
			globalNeuronCount.setText("Neurons: " + neuronCount);
			globalEdgeCount.setText("Edges: " + edgeCount);
			globalOutputNeuronCount.setText("Outputs: " + outputCount);
		}else{
			globalInputNeuronCount.setText("Inputs: N/A");
			globalNeuronCount.setText("Neurons: N/A");
			globalEdgeCount.setText("Edges: N/A");
			globalOutputNeuronCount.setText("Outputs: N/A");
		}
	}
	
	/**Prompts the user to save a currently selected net, then actually saves the net! woo.*/
	private void saveNet(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new NetFileFilter());
		fileChooser.setMultiSelectionEnabled(false);

		if(fileChooser.showSaveDialog(this) == 0){
			try{
				OCR.getNet(netList.getSelectedIndex()).save(new FileWriter(fileChooser.getSelectedFile()));
			}catch(IOException IOe){
				JOptionPane.showMessageDialog(this, "Error saving.");
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error saving net.");
			}
		}
	}

	/**Recalculates then draws individual net information into the UI.*/
	public void drawNetInfo(){
		if(netList.getSelectedIndex() != -1){
			NeuralNet tempNet = OCR.getNet(netList.getSelectedIndex());

			new NeuralVisualisationFrame(tempNet, 320, 240, "Neural net details");
		/*	try{
				this.remove(netVis);
			}catch(NullPointerException NPe){}
			netVis = null;
			netVis = new NeuralVisualisation(tempNet);
			netVis.setPreferredSize(new Dimension(200,350));
			try{
				netVis.update();
			}catch (NearNeural.IndexOutOfBoundsException IOOe){}
			this.add(netVis,gbc);
		}else{
			try{
				this.remove(netVis);
			}catch(NullPointerException NPe){}
			netVis = null;
		}*/
		}
	}

	/**Repopulates the list from the OCR manager's net stack.  Call this after making any edits in the rest of the UI to ensure the panel stays up to date.*/
	public void repopulateList(){
		netList.setListData(OCR.getNets());
	}

	/* As I sit writing Javadoc, it is becoming clear, that I would rather shove - quite hard, a pinecone in my ear. */

	/**Is called whenever the value of a list changes, in this case this is the Net list only.
	  @param LSe The list event as fired by the JList containing all nets
	*/  
	public void valueChanged(ListSelectionEvent LSe){
		if(LSe.getSource() == netList && netList.getSelectedIndex() != -1){
			remove.setEnabled(true);
			redraw.setEnabled(true);
			save.setEnabled(true);
			prune.setEnabled(true);
			pruneVal.setEnabled(true);
			precalc.setEnabled(true);
			up.setEnabled(true);
			down.setEnabled(true);
		}else{
			remove.setEnabled(false);
			redraw.setEnabled(false);
			precalc.setEnabled(false);
			save.setEnabled(false);
			prune.setEnabled(false);
			pruneVal.setEnabled(false);
			up.setEnabled(false);
			down.setEnabled(false);
		}
		drawGlobalInfo();
	}

	/**Is called whenever an appropriate event is fired by a component on the panel, probably a button.
	   @param Ae The actionEvent that is fired by the respecitve component.
	 */
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource()==up){
			if(netList.getSelectedIndex() > 0){
				OCR.moveNet(netList.getSelectedIndex(), netList.getSelectedIndex() - 1);
				repopulateList();
			}

		}else if(Ae.getSource() == down){
			if(netList.getSelectedIndex() != -1){
				OCR.moveNet(netList.getSelectedIndex(), netList.getSelectedIndex() + 1);
				repopulateList();
			}
		}else if(Ae.getSource() == remove){
			if(netList.getSelectedIndex() != -1){
				try{
					OCR.removeNet(netList.getSelectedIndex());
					repopulateList();
				}catch(ItemNotFoundException INFe){
					JOptionPane.showMessageDialog(this, "Nothing to remove");
				}catch(ValueOutOfBoundsException VOOBe){
					JOptionPane.showMessageDialog(this, "Error removing net");
				}
			}
		}else if(Ae.getSource() == redraw){
			drawNetInfo();
		}else if(Ae.getSource() == save){
			if(netList.getSelectedIndex() != -1){
				saveNet();
			}	
		}else if(Ae.getSource() == prune){
			((NeuralNet)netList.getSelectedValue()).prune(((SpinnerNumberModel)pruneVal.getModel()).getNumber().doubleValue());
		}else if(Ae.getSource() == precalc){
			((NeuralNet)netList.getSelectedValue()).preCalculate();
		}
	}


}
