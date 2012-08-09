package NearNeural;
/** A receptive Neuron is a form of Neuron that maintains Edges, which themselves point to other Neurons, be they static or themselves a form of Receptive Neuron.  Receptive neurons can calculate their value through any means.

Because the only common code is Edge management, this is all the Nodes do.

	@author Stephen Wattam
	@version 0.2.0a
*/
public class ReceptiveNeuron implements Neuron{
	/**Stores the thresholding algorithm to use for this neuron*/
	private ThresholdingAlgorithm thresholdModel;

	/**Stores the value of this neuron to prevent unnecessary recalculation.  This square roots complexity when running the net and massively reduces execution time*/
	private double value = -14.34;
	
	/**Retains all Edges for this node.*/
	protected Edge[] edges = new Edge[0];//I want this not to be shared in package, but there is no keyword for it	

	/**Stores the last value correction as per the backpropagation algorithm*/
	protected double delta = 0;	

	/**Constructs randomly weighted edges to all neurons in the array provided.
	  @param p_neuronList An array of neurons to create edges to
	*/  
	public void addEdges(Neuron[] p_neuronList){
		Edge[] tempEdges = calculateEdges(p_neuronList);
		for(int i=0;i<tempEdges.length;i++)
			this.addEdge(tempEdges[i]);
	}

	/**Creates a new neuron with no edges.
	 	@param p_thresholdModel The thresholding algorithm to use 
	 */
	public ReceptiveNeuron(ThresholdingAlgorithm p_thresholdModel){
		thresholdModel = p_thresholdModel;
	}

	/**Creates a Neuron with Edges from an array.  Generally speaking this will remain unused as all Neurons are supposed to link to all of the previous NeuronLayer's Neurons.
		@param p_inputEdges An array of edges to attach to the Neuron
		@param p_thresholdModel The thresholding algorithm model to use
	*/
	public ReceptiveNeuron(Edge[] p_inputEdges, ThresholdingAlgorithm p_thresholdModel){	//this is for use with input nodes
		System.out.println("-");
		edges = p_inputEdges;
		thresholdModel = p_thresholdModel;
	}

	/**Returns the last error value from backpropagation.  This is used in the backpropagation process and can be used to indicate the success of the last training cycle, but is not stored across save/load events.
	  @return The last target value adjustment which caused edges to be edited
	*/  
	public double getDelta(){
		return delta;
	}

	/**Sets the difference between current value and desired value during the backpropagation process.  This can be edited manually but will be lost during save/load events and must be used in a specific order to make any difference to edge weights.  This method does not edit edge weights directly, but can influence the backpropagation process of all layers prior to the current one.
	  @param p_delta The delta value chosen for this neuron
	*/  
	public void setDelta(double p_delta){
		delta=p_delta;
	}
	
	/**Sets the weight of an edge, as indexed by the connection to the current Neuron.

	  @param p_index The index of the Edge of which to edit the weight.
	  @param p_weight The weight to set the Edge to.
	  @throws IndexOutOfBoundsException in the event that the index given is not linked to an edge.
	*/
	public void setEdgeWeight(int p_index, double p_weight)throws IndexOutOfBoundsException{
		if(p_index >= edges.length || p_index < 0)
			throw new IndexOutOfBoundsException("Edge index out of bounds");

		edges[p_index].setWeight(p_weight);
	}


	/**Returns a list of edges connecting the current Neuron to all Neurons in the array provided.
		@param p_neurons The list of neurons to connect to.
	*/
	private Edge[] calculateEdges(Neuron[] p_neurons){
		Edge[] edgeList = new Edge[p_neurons.length];

		for(int i=0;i<edgeList.length;i++)
			edgeList[i] = new Edge(p_neurons[i],((Math.random()*2)-1));	//create a link to this neuron from every single one in the last layer
		
		return edgeList;
	}

	/**Adds a single edge to the neuron.
	  @param p_edge The edge to add.
	*/
	public void addEdge(Edge p_edge){
		//This may seem the daftest way of doing things, but I simply need the speed offered by an array
		//This operation copies all references between arrays, which is fucking insane
		Edge tempEdges[] = new Edge[(edges.length+1)];	//create an array with one more slot
		for(int i=0;i<edges.length;i++)			//copy everything
			tempEdges[i] = edges[i];
		tempEdges[(tempEdges.length-1)] = p_edge;		//assign and append
		edges = tempEdges;
	}
	
	/** Removes an edge from this neuron.
	  @param p_edge The edge to remove.
	*/
	public void removeEdge(Edge p_edge) throws ItemNotFoundException{
		if(!isPresent(p_edge))
			throw new ItemNotFoundException();

		//see notes on freakiness in the addNeuron method
		Edge tempEdge[] = new Edge[(edges.length-1)];
		int j=0;
		for(int i=0;i<edges.length;i++){
			if(edges[i] != p_edge){
				tempEdge[j] = edges[j];
				j++;
			}	
		}
		edges = tempEdge;
	}
	
	/**Removes Edges with an absolute weight under the threshold given.  This is usually used after training for the final time and can be used to increase the speed of the net whilst sacrificing little in the way of accuracy.

	  @param p_threshold The weighting under which to remove an Edge
	*/
	public void prune(double p_threshold){
		for(int i=0;i<edges.length;i++){
			if(Math.abs(edges[i].getWeight()) < p_threshold){
				try{
					removeEdge(i);
				}catch(IndexOutOfBoundsException IOOBe){
					//it is probably best to fail silently.
					//IOOBe.printStackTrace();
				}
			}
		}
	}

	/** Removes an edge from the layer by index.

	 	@param p_index The index of the edge to remove
		@throws IndexOutOfBoundsException in the event that the index given is out of range.
	*/
	public void removeEdge(int p_index) throws IndexOutOfBoundsException{
		if(p_index >= edges.length || p_index < 0)
			throw new IndexOutOfBoundsException("Index is not within valid range");
		
		try{
			removeEdge(edges[p_index]);
		}catch(ItemNotFoundException INFe){
			//See notes in NeuronLayer.java as to why this is not actually 'wrong'.  It's odd but not really hacky
			throw new IndexOutOfBoundsException("Index apparently within valid range but no edge found");
		}
	}

	/** Returns the number of edges currently in this neuron.
	  @return The number of edges currently in the neuron
	*/
	public int countEdges(){
		return edges.length;
	}

	/**Returns a list of all edges handled by this Neuron*/
	public Edge[] getInputEdges(){		//get all edges that point here from edges[n].source
		return edges;
	}

	/**Returns the sum of all Edge values, multiplied by their weight. 
	 	@return The sum of all edges, which is the value for this Neuron.
	 */
	public double value(){
		return value;

		//this is now in LinearThresholdModel.java
		//double result = 0;

		//for (int i=0;i<edges.length;i++)		//sum all weights times their node's value
		//	result += (edges[i].getWeight()*edges[i].source.value());	
		//return result;	
	}
	
	/**Returns the differential of the function that generates value at this point.
	  @return The differential of the fucntion generating the valuye of this neuron
	*/
	public double differentialValue(){
		return thresholdModel.differentialValue(edges);
	}

	/**pre-calculates the value of this neuron - this method requires that all previous layers have been run first in a feed-forward manner.*/
	public void calculateValue(){
		value = thresholdModel.value(edges);
	}
	
	/**Returns an edge at the index given.

	  @param p_index the index of the Edge to return
	  @return The edge at the index given
	  @throws IndexOutOfBoundsException in the event that the index given is invalid.
	  */
	public Edge getEdge(int p_index) throws IndexOutOfBoundsException{
		if(p_index >= edges.length || p_index < 0)
			throw new IndexOutOfBoundsException("Value provided outside of permissable range");
		return edges[p_index];
	}
	
	
	/** checks if an Edge is held in this layer.

	  @param p_edge The edge to check for
	  @return true if the edge is present, false if it is not
	  */
	public boolean isPresent(Edge p_edge){
		for(int i=0;i<edges.length;i++){
			if(p_edge == edges[i])
				return true;
		}
		return false;	
	}

}
