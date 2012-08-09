import NearNeural.*;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.GridLayout;
import java.awt.Color;
/**Shows a neural net as part of a JList.  This displays the number of inputs, outputs, neurons, edges and the learning rate of its net.

  @author Stephen Wattam
  @version 0.1
*/ 
//props to http://java.sun.com/javase/6/docs/api/javax/swing/ListCellRenderer.html
public class NetCell extends JPanel implements ListCellRenderer{
	/**A label that displays the number of neurons*/
	private JLabel neurons = new JLabel("Neurons: N/A");
	/**A label that displays the number of edges*/
	private JLabel edges = new JLabel("Edges: N/A");
	/**A label that displays the number of outputs*/
	private JLabel outputs = new JLabel("Outputs: N/A");
	/**A label that displays the number of inputs*/
	private JLabel inputs = new JLabel("Inputs: N/A");
	/**A label that displays the learning rate.*/
	private JLabel learningRate = new JLabel("Learning rate: N/A");

	/**Creates a new net cell. */
	public NetCell(){
		super(new GridLayout(3,2));

		this.add(neurons);
		this.add(edges);
		this.add(outputs);
		this.add(inputs);
		this.add(learningRate);

		setOpaque(true);
	}

	/**Redraws all statisitcs, reading the net and updating values.
	  @param p_net The neural net to read data from.
	*/
	private void redrawStats(NeuralNet p_net){
		int layers[] = p_net.getLayerStructure();
		int neuronCount = 0;
		int edgeCount = 0;
		for(int j=0;j<layers.length;j++){
			if(j > 0){
				try{
					Neuron[] neurons = p_net.getLayer(j).getNeurons();		
					for(Neuron n:neurons)
						edgeCount += ((ReceptiveNeuron)n).countEdges();
				}catch(NearNeural.IndexOutOfBoundsException IOOBe){}
			}


			neuronCount += layers[j];
		}
		

		neurons.setText("Neurons: " + neuronCount);
		edges.setText("Edges: " + edgeCount);
		inputs.setText("Inputs: " + layers[0]);
		outputs.setText("Outputs: " + layers[(layers.length-1)]);
		learningRate.setText("Learning rate: " + ((UnweightedBackPropagationMethod)p_net.getBackPropagationMethod()).getLearningRate());	
	}

	/**Provides a renderable component for the JList.
	  @param list The JList this is in.
	  @param value The Neural net that is the value (or any object, but undefined behaviour results in using this list cell with oher objects)
	  @param index The index number of this cell
	  @param isSelected Whether or not this list cell is selected.
	  @param CellHasFocus Whether or not this cell has focus.
	*/  
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean CellHasFocus){
		if(value!=null){
			NeuralNet net = (NeuralNet)value;
			redrawStats(net);
		}

		
		int colourIntensity = 205;
		int colourUntensity = 0;
		
		if(isSelected){
			colourIntensity = 205;
			colourUntensity = 180;
		}else{
			colourIntensity = 205;
			colourUntensity = 155;
		}

		if(index%2==0){
			colourIntensity+=20;
			colourUntensity+=20;
		}
	
		setBackground(new Color(colourUntensity,colourUntensity,colourIntensity));
		
		return this;
	}


}
