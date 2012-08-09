package NearNeural;
/**A threshold which is linear.<strong>This method may not work at all with some backpropagation methods</strong>

  <p>Where x is k*sum(edge_weight*neuron_value) for all input edges.  Differential is, of course, k</p>
  @author Stephen Wattam
  @version 0.3
*/  
public class LinearThresholdModel extends WeightedThresholdModel implements ThresholdingAlgorithm{
	/**Creates a new thresholding function with the given 'harshness'.
	  @param p_k the steepness of the dropoff
	*/
	public LinearThresholdModel(double p_k){
		super(p_k);
	}

	/**Calculates Neuron value from all Edges and their source Neurons.
		@return This Neuron's value
	*/
	public double value(Edge[] edges){
		double result = 0;
		int limit = edges.length-1;	//ugly but quick versino of the loop

		for (int i=limit;i>=0;i--)		//sum all weights times their node's value
			result += (edges[i].getWeight()*edges[i].getSource().value());	
		
		return (k*result);	//take sigmoid of that to smoothly combine
	}

	/**Validates input against the applicable range for this thresholding algorithm.
	 	@param input The value to check
		@return True if the value is applicable to run though without error
	 */
	public boolean validateInput(double input){
	       return true;
	}

	/**Returns the differential of the thresholding function.
	  @param edges The edges that comprise the links this neuron has.
	  @return The differential of the value
	*/
	public double differentialValue(Edge[] edges){
		return k;	//ahh the beauty of linear functions!
	}
}
