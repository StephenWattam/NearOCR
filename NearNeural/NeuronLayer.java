package NearNeural;

/**
A NeuronLayer keeps track of an arbitrary selection of neurons, each of which may have arbitrary or even circular links.  It is not possible to run the back propogation algorithms without restricting this class' contents and fixing the structure of the net.  This class's subclasses enable this.

There is no reason why this class should not be used, but for executing nets it is useless.
	@author Stephen Wattam
	@version 0.2.0a
  */
public abstract class NeuronLayer{
	/**The thresholding model to use for back propagation*/
	protected ThresholdingAlgorithm thresholdModel;
	/**Stores all Neurons in this layer
		@see Neuron
	*/
	protected Neuron[] neurons = new Neuron[0];

	/**Adds a neuron to the layer.
	  @param p_neuron The neuron to add to this layer.
	*/
	public void addNeuron(Neuron p_neuron){
		//This may seem the daftest way of doing things, but I simply need the speed offered by an array
		//This operation copies all references between arrays, which is fucking insane
		Neuron tempNeurons[] = new Neuron[(neurons.length+1)];	//create an array with one more slot
		for(int i=0;i<neurons.length;i++)			//copy everything
			tempNeurons[i] = neurons[i];
		tempNeurons[(tempNeurons.length-1)] = p_neuron;		//assign and append
		neurons = tempNeurons;
	}

	/**Creates a blank neuron layer with the threshold model provided.
	  @param p_thresholdModel The thresholding algorithm chosen
	*/  
	public NeuronLayer(ThresholdingAlgorithm p_thresholdModel){
		thresholdModel = p_thresholdModel;
	}

	/** Removes a neuron from this layer.
	  @param p_neuron The neuron to remove.
	  @throws ItemNotFoundException if the neuron passed is not present.
	*/
	public void removeNeuron(Neuron p_neuron) throws ItemNotFoundException{
		if(!isPresent(p_neuron))
			throw new ItemNotFoundException();	//else prepare for the array crapping out

		//see notes on freakiness in the addNeuron method
		Neuron tempNeurons[] = new Neuron[(neurons.length-1)];
		int j=0;
		for(int i=0;i<neurons.length;i++){
			if(neurons[i] != p_neuron){
				tempNeurons[j] = neurons[j];
				j++;
			}	
		}
		neurons = tempNeurons;
	}

	/** Removes a neuron from the layer by index.

	 	@param p_index The index of the neuron to remove
		@throws IndexOutOfBoundsException in the event that the index given is out of range.
	*/
	public void removeNeuron(int p_index) throws IndexOutOfBoundsException{
		if(p_index <= neurons.length || p_index < 0)
			throw new IndexOutOfBoundsException("Index is not within valid range");

		try{
			removeNeuron(neurons[p_index]);
		}catch(ItemNotFoundException INFe){
			//This is a curious situatioon.  The index is in range but no neuron exists.
			//This technically means that the index must be wrong if the add/remove methods are not
			//assuming they are not this is then only going to happen if another thread edits the layer in between checks and accessing
			//if this is the case then the error is actually an index error, so throw one with explainatioon
			throw new IndexOutOfBoundsException("Index apparently within valid range but no neuron found");
		}
	}

	/** Returns the number of neurons currently in this layer.
	  @return The number of neurons currently in the layer
	*/
	public int countNeurons(){
		return neurons.length;
	}

	/** Returns a neuron with the index provided
	  @param p_index The index of the Neuron to return
	  @return The neuron at the index specified
	  @throws IndexOutOfBoundsException in the event that the index is out of range.
	*/
	public Neuron getNeuron(int p_index) throws IndexOutOfBoundsException{
		if(p_index >= neurons.length || p_index < 0)
			throw new IndexOutOfBoundsException("Index is not within valid range [neurons/layer]");

		return neurons[p_index];
	}
	
	/**Returns all Neurons in this layer.  This is used largely to ensure that the load and save routines can inject values directly from the tokenised file stream.
	  @return An array of all Neurons held
	*/	  
	public Neuron[] getNeurons(){
		return neurons;
	}

	/** checks if a Neuron is held in this layer.

	  @param p_neuron The neuron to check for
	  @return true if the neuron is present, false if it is not
	  */
	public boolean isPresent(Neuron p_neuron){
		boolean present = false;
		for(int i=0;i<neurons.length;i++){
			if(p_neuron == neurons[i]){
				present = true;
				break;
			}
		}
		return present;	
	}
}
