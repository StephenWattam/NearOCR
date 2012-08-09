package NearNeural;
/**This class contains the necessary code to implement coefficient management in order to adjust a thresholding function.  Extend this class if you wish to use a coefficient in your thresholding algorithm, where k affects steepness.

  @author Stephen Wattam
  @version 0.3
*/  
public abstract class WeightedThresholdModel{
	/**The coefficient value (steepness).*/
	protected double k;

	/**Cannot be called directly: allows subclasses to create weighted copies.
	  @param p_k The coefficient requested.
	*/  
	public WeightedThresholdModel(double p_k){
		setK(p_k);
	}


	/**Returns the coefficient of the function.
	  @return K, the coefficient of the function (steepnes)
	*/  
	public double getK(){
		return k;
	}

	/**Sets the coefficient of the running function (steepness).
	  @param p_k The new coefficient to set
	*/  
	public void setK(double p_k){
		k = p_k;
	}
}
