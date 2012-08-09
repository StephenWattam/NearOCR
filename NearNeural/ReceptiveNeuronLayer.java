package NearNeural;

/**
  A Receptive form of the Neuron Layer, this class allows Neurons to maintain Edges to other Neurons.  It has a backpropgation algorithm in it that is suitable for intermediate layers but not output.  The only difference between the OutputNeuronLayer and this one is the back propagation routine, and they are separated for that reason only.  

  <p>This Layer accepts any descendents of ReceptiveNeuron, but for stability reasons the Sigmoid form is coded in at the moment.  Since it's the only possible algorithm that isn't exactly an issue, but be aware if you wish to change the algorithm and source later.</p> 
  @author Stephen Wattam
  @version 0.2.0a
 */
public class ReceptiveNeuronLayer extends NeuronLayer{

	/** Links all neurons in this layer to all neurons in the layer provided.

	  @param p_neuronlayer The neuron layer to link all Neurons to.
	 */
	protected void linkTo(NeuronLayer p_neuronlayer){
		Neuron[] layerNeurons;
		ReceptiveNeuron currentNeuron;
		for (int i=0; i<neurons.length; i++){
			layerNeurons = p_neuronlayer.getNeurons();
			currentNeuron = (ReceptiveNeuron)neurons[i];
			currentNeuron.addEdges(layerNeurons);
		}
	}

	/**Creates a new receptive neuron layer with the desired thresholding algorithm.
	  @param p_thresholdModel The thresholding algorithm to use./
	*/
	public ReceptiveNeuronLayer(ThresholdingAlgorithm p_thresholdModel){
		super(p_thresholdModel);
	}

	/**Returns an edge from one of the neurons in this layer.

	  @param p_neuronIndex The index of the neron from which to return an edge.
	  @param p_edgeIndex The index of the edge from that neuron
	  @return The edge as selected by the above two indices
	  @throws IndexOutOfBoundsException in the event that either of the indices fall out of range
	 */
	public Edge getEdge(int p_neuronIndex, int p_edgeIndex) throws IndexOutOfBoundsException{
		ReceptiveNeuron tempNeuron =  (ReceptiveNeuron)getNeuron(p_neuronIndex);
		return tempNeuron.getEdge(p_edgeIndex);
	}

	/**Precalculates all neuron values based on their edges.  It is imperative that this is run in order, however, if you only change one layer it is quite possible to run only this method.
	*/
	public void preCalculate(){
		//System.out.print(neurons.length);
		for(int i=0;i<neurons.length;i++)
			((ReceptiveNeuron)neurons[i]).calculateValue();
	}
	
	public ReceptiveNeuron getNeuron(int p_index) throws IndexOutOfBoundsException{
		return (ReceptiveNeuron)super.getNeuron(p_index);
	}

	/**Returns all neurons as ReceptiveNeurons.  Since this is a receptive neuron layer we can cast down with relative safety.  This is used for back propagation.
	  @return All of the contained neurons, cast to ReceptiveNeurons
	*/
	public ReceptiveNeuron[] getNeurons(){
		ReceptiveNeuron[] tempNeurons = new ReceptiveNeuron[neurons.length];
		int limit = neurons.length;

		for(int i=0;i<limit;i++)
			tempNeurons[i] = (ReceptiveNeuron)neurons[i];
		return tempNeurons;	
	}	
}
