package NearNeural;
/**
The Static Neuron Layer holds only Static neurons, that is those without any Edges.  This allows this layer and its accompanying neurons to be used as an input layer.  It supports the changing of node values directly, so that input can quickly be changed without the overheads of clearing and recreating the Edges and array elements related to other layers which may depend on the members of this layer.

<p>Static layers cannot back-propogate</p>

	@author Stephen Wattam
	@version 0.2.0a
*/
public class StaticNeuronLayer extends NeuronLayer{

	/**Creates a new layer with a given number of Static Neurons.  Since all of these neurons will be without edges they are simply created apart from any other layers, and given a default values of 0.5, which will result in 0 on a sigmoid net.

	  @param p_numberOfNeurons The number of Static neurons to create in this layer
	  @see StaticNeuron
	*/
	protected StaticNeuronLayer(int p_numberOfNeurons, ThresholdingAlgorithm p_thresholdModel)throws ValueOutOfBoundsException{
		super(p_thresholdModel);
		if(p_numberOfNeurons <= 0)
			throw new ValueOutOfBoundsException("Number of neurons must exceed 0");

		thresholdModel = p_thresholdModel;

		for(int i=0;i<p_numberOfNeurons;i++)
			this.addNeuron(new StaticNeuron(0.5, thresholdModel));

	}

	protected StaticNeuronLayer(ThresholdingAlgorithm p_thresholdModel){
		super(p_thresholdModel);
	}

	/** Sets the input value of one static node.

		@param p_value The value to set a stupid Neuron to
		@param p_index The index of the Neuron of which to set the value
		@see Neuron

	*/
	protected void setInputValue(double p_value, int p_index) throws IndexOutOfBoundsException, ValueOutOfBoundsException{
		//if(p_value >= 1 || p_value <= -1)
		//	throw new ValueOutOfBoundsException("Values must be between -1 and 1 exclusive", p_value);
		if(p_index >= neurons.length || p_index < 0)
			throw new IndexOutOfBoundsException("Index is not within valid range: " + p_index);
	
		StaticNeuron tempNeuron = (StaticNeuron)neurons[p_index];
		tempNeuron.setValue(p_value);
	}

	/**Sets all nodes in this layer to the values given, in order.

		@param p_values The set of values to which to set the nodes
		@throws ValueOutOfBoundsException if provided values exceed the 0&lt;x&lt;1 bounds required of this net
		@throws IndexOutOfBoundsException if not enough or too few values are provided to set the input Neurons
	*/
	protected void setInputValues(double[] p_values) throws IndexOutOfBoundsException, ValueOutOfBoundsException{		//set input values of dummy layer
		if(p_values.length != neurons.length)
			throw new IndexOutOfBoundsException("Discrepancy between number of input nodes and values given:" + p_values.length + " vs " + neurons.length);

		for(int i=0; i<neurons.length;i++)
			setInputValue(p_values[i], i);
	}
	//cannot be backpropped

}
