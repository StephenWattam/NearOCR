package NearNeural;
/**Represents a thresholding algorithm based on the sigmoid function.

  @author Stephen Wattam
  @version 0.2
*/  
public class SigmoidThresholdModel extends WeightedThresholdModel implements ThresholdingAlgorithm{
	/**Creates a new thresholding function with the given 'harshness'.
	  @param p_k the steepness of the dropoff
	*/
	public SigmoidThresholdModel(double p_k){
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
		
		return sigmoid(result);	//take sigmoid of that to smoothly combine
	}

	/**Validates input values.
	  @param input The input value to check.  This does not have to conform to strict asymptotic rules as edge weights are applied.
	  @return True if the input is valid, false if not.
	*/
	public boolean validateInput(double input){
		if(input >= -1 && input <= 1)
		       return true;
		return false;	
	}


	/**Executes 1/(1+e^x) where x is double, returning a double.  This is for use in the backpropagation process.
		@param x The number of which to return sigmoid of
		@return The sigmoid function of input, x
	  */
	private double sigmoid(double x){
		/*    1
		  ----------
		  (1+(e^-x))
		*/
		//System.out.print(".");
		return (1/(1+(Math.pow(Math.E,(-x*k)))));	
	}


	/**Defined in ThresholdingAlgorithm, returns differential of current value.
	  @param edges All edges connected to the neuron
	  @return The differential of the value
	*/  
	public double differentialValue(Edge[] edges){
		double tempValue = value(edges);	//half the time we spend calculating values slowly

		return tempValue * (1-tempValue);	//sig(x)(1-sig(x)) is  differential
	}	
}
