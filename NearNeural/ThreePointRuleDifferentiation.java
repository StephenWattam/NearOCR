package NearNeural;
/**Implements a numerical method for differentiation.  Extend this class if you cannot calculate a more accurate differentiation model for your thresholding function.  This method takes copies of all edges in order to create small variations in value, then runs the value function two times - it is thus way over a guaranteed half speed of a well designed differentiation algorithm.

	Use this class when testing or in emergencies only, it is not fast and probably never will be.

  @author Stephen Wattam
  @version 0.2
*/  
public abstract class ThreePointRuleDifferentiation implements ThresholdingAlgorithm{
	/**The difference in x between samples to calculate the differential.*/
	private double h;

	/**Creates a new differentiation constant necessary to approximate differential.  Generally the smaller h is the better the result.
	  @param p_h The difference between samples.
	*/  
	public ThreePointRuleDifferentiation(double p_h){
		h = p_h;
	}

	/**Calculates Neuron value from all Edges and their source Neurons.
	  	@param edges This neuron's edges
		@return This Neuron's value
	*/
	public abstract double value(Edge[] edges);

	/**Validates input against the applicable range for this thresholding algorithm.
	 	@param  value The value to check
		@return True if the value is applicable to run though without error
	 */
	public abstract boolean validateInput(double value);

	/**Attempts to find the differential of any function by way of numerical methods.
	  @param edges All edges leading to this neurons
	  @return The differential of the value of the arbitrary thresholding functioon
	*/  
	public double differentialValue(Edge[] edges){
		double f1 = value(edges);
		double f2 = value(getEdgeCopies(edges, h));
	
		return (f1-f2)/h;
	}

	/**Returns a set of edges with weights skewed by deviation.
	  @param edges All edges one wishes to edit
	  @param deviation The amount to skew edges by
	  @return An array of skewed copies of edges.
	*/  
	private Edge[] getEdgeCopies(Edge[] edges, double deviation){
		Edge[] output = new Edge[edges.length];
		for(int i=0;i<output.length;i++){
			output[i] = new Edge(edges[i].getSource(), edges[i].getWeight());
			output[i].setWeight(output[i].getWeight() + deviation);
		}
		return output;
	}
}
