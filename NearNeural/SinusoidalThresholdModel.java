package NearNeural;
/**Creates a new sinusoidal thresholding algorithm.
  @author Stephen Wattam
  @version 0.2
*/
public class SinusoidalThresholdModel extends WeightedThresholdModel implements ThresholdingAlgorithm{
	/**Creates a new sinusoidal function with a given steepness.
	  @param p_k The steepness value
	 */
	public SinusoidalThresholdModel(double p_k){
		super(p_k);
	}
	
	/**Calculates Neuron value from all Edges and their source Neurons.
	  	@param edges All of this neuron's edges
		@return This Neuron's value
	*/
	public double value(Edge[] edges){
		double result = 0;
		int limit = edges.length-1;	//ugly but quick versino of the loop

		for (int i=limit;i>=0;i--)		//sum all weights times their node's value
			result += (edges[i].getWeight()*edges[i].getSource().value());//more recursive than recursion itself, but the neurons are cleverer than that, muhahaha!!!	
		
		return (Math.sin(k*result)*.5)+.5;	//take sin of that to smoothly combine
	}

	/**Validates input against the applicable range for this thresholding algorithm.
	 	@param input The value to check
		@return True if the value is applicable to run though without error
	 */
	public boolean validateInput(double input){
		//from trough to peak only
		double peakDisplacement = (Math.PI/2) * (1/k);

		if(input >= -peakDisplacement && input <= peakDisplacement)
		       return true;
		return false;	
	}

	/**Returns the differential of the thresholding function.
	  @param edges The edges that comprise the links this neuron has.
	  @return The differential of the value
	*/
	public double differentialValue(Edge[] edges){
		//(sin(x))'  = (cos(x))
		//(sin(kx))' = (kcos(kx))
		//(.5sin(kx)) + .5 = ((.5sin(kx))' + (.5)' = product rule:
		//
		//	.5(kcos(kx)) + sin(kx)*0 = .5(kcos(kx)) + (.5)'
		//				 = .5(kcos(kx))
		//
		//

		double result = 0;
		int limit = edges.length-1;	//ugly but quick versino of the loop

		for (int i=limit;i>=0;i--)		//sum all weights times their node's value
			result += (edges[i].getWeight()*edges[i].getSource().value());//more recursive than recursion itself, but the neurons are cleverer than that, muhahaha!!!	

		//result == x;
		return .5 * (k * Math.cos(k * result));
	}
}
