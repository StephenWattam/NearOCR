package NearNeural;
/**Creates a new thresholding algorithm based on the hyperbolic tangent function.  This approximates sigmoid closely only with a slightly smoother curve at k~1, and hence can offer slightly better training results.
  @author Stephen Wattam (differentiatioon by Craig Edwards)
  @version 0.3
*/
public class HyperbolicTangentThresholdModel extends WeightedThresholdModel implements ThresholdingAlgorithm{
	/**Creates a new tangental function with a given steepness.  K is used as a coefficient of x in the hyperbolic tangent function.
	  @param p_k The steepness value
	 */
	public HyperbolicTangentThresholdModel(double p_k){
		super(p_k);
	}
	
	/**Calculates Neuron value from all Edges and their source Neurons.
		@param edges All of this neuron's edges
		@return This Neuron's value
		@see ThresholdingAlgorithm#value(Edge[])
	*/
	public double value(Edge[] edges){
		double result = 0;
		int limit = edges.length-1;	//ugly but quick versino of the loop

		for (int i=limit;i>=0;i--)		//sum all weights times their node's value
			result += (edges[i].getWeight()*edges[i].getSource().value());	
		
		return hypTan(result);	//take hypTan of that to smoothly combine
	}

	/**Validates input against the applicable range for this thresholding algorithm.
	 	@param input The value to check
		@return True if the value is applicable to run though without error
		@see ThresholdingAlgorithm#validateInput(double)
	 */
	public boolean validateInput(double input){
		if(input >= -1 && input <= 1)
		       return true;
		return false;	
	}


	/**Executes (tanh(x)*.5) + 0.5 where x is double, returning a double.  This is for use in the backpropagation process.
		@param x The number of which to return tanh of
		@return The tanh function of input, x
	  */
	private double hypTan(double x){
		//System.out.print(".");
		return (Math.tanh(k*x)*0.5) + 0.5;	
	}

	/**The value as run through the differential of the current function.  This may not be used during backpropagation but is used in most forms of gradient-descent backpropagation.
	  @param edges All edges going into this neurons
	  @return The differential value
	*/  
	public double differentialValue(Edge[] edges){
		double result = 0;
		int limit = edges.length-1;	//ugly but quick versino of the loop

		for (int i=limit;i>=0;i--)		//sum all weights times their node's value
			result += (edges[i].getWeight()*edges[i].getSource().value());	
		
		return 1/Math.cosh(k * result) * k / 2;
	}

	/* It's late, I'm tired and have an exam tomorrow.

	   This might be one of the best thresholding functions arouund but for one thing:
	   Differentiating 0.5tanh(kx) is fucking hard. Fucking me hard.  In the arse, with a cactus.

	   Future me? listening? Use the quotient rule, then some other fancy-arse shit, and go to...
		http://www.ucl.ac.uk/Mathematics/geomath/level2/hyper/hy5.html

	   For that reason this class is extending my ThreePointRuleDifferentiation class in order to try to get
	   cheating differentiation.
	 */

	
}
