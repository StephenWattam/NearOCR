package NearNeural;
/**
This represents an edge in a NearNeural net.  It contains a reference to its source neuron and a weight.  Thresholding algorithms may use these two in an arbitrary manner, but the usual method is to multiply an edge's source neuron value by the edge weight and sum all edges contained within the neuron, then pass it through a soft limiter function such as a sigmoid function.

<p>Edges point from a downstream neuron upstream.  This has been done to optimise calculation of values, but results in a slightly more ineficient backpropagation method where gradient descent is involved.  Other backpropagation methods may not be hit severely by this, but it is an important consideration when designing the algorithm.</p>

<p>The edge's old weight is used when backpropagating</p>

	@see Neuron
	@author Stephen Wattam
	@version 0.1.0a
*/

public class Edge{
	/** Stores a reference to the source neuron.  This is the neuron pointed at by the edge from a neuron downtsream from it.  Edges may appear to point backwards, but this is in fact the manner in which they are most often used.  Storing only a single-way link prevents much costly reference management code.
		@see #setSource(Neuron)
	 */
	protected Neuron source;	//store the node this links _FROM_
	
	/** Stores the weight of this edge.
	 	@see #setWeight(double)
		@see #getWeight()
	*/
	private double weight;	//store this link's weighting

	/**Stores the last weight prior to weight edits.  Used during backpropagation to prevent the need for many passes of the net.  Initially this value is 1, which is the multiplicatory identity.
	 @see BackPropagationMethod
	*/
	private double oldWeight = 1;

	/**Set the old weight of this Edge.  This is used during backpropagation to simulate a double-pass method, as backpropagation calculations ought to use this to simulate a lack of change in a downstream neuron.
	  @param p_weight The weight to set
	  @see BackPropagationMethod
	*/  
	public void setOldWeight(double p_weight){
		oldWeight = p_weight;
	}
	
	/**Returns the old weight, as per backpropagation.
	  @return The old weight of this edge
	*/
	public double getOldWeight(){
		return oldWeight;
	}

	/** Retrns the target of this edge, ie the source (the neuron from which data flows unto the one which owns this Edge), but not the source of data.
	  @return the Neuron this edge moves data from.
	*/
	public Neuron getSource(){
		return source;
	}

	/**Creates a new edge with neuron and weight.  The neuron given is the source of data, which has a target of whatever neuron to which this new edge is added.
		@param p_source The source neuron
		@param p_weight The weight of this edge
		@see Neuron
	*/
	protected Edge(Neuron p_source, double p_weight){
		setSource(p_source);
		setWeight(p_weight);
	}

	/**Sets the source neuron reference to the given neuron.
		@param p_source the neuron to set as source neuron
		@see Neuron
	*/
	private void setSource(Neuron p_source){
		source = p_source;
	}

	/**Sets the weight of this edge
		@param p_weight The weight to set this edge to
	  */
	protected void setWeight(double p_weight){
		weight = p_weight;
	}
	

	/**Returns the weight of the Edge. 	
		@return The weight of this Edge
	 */
	public double getWeight(){
		return weight;
	}
}
