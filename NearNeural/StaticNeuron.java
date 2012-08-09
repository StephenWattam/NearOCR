package NearNeural;
/**Represents an input neuron, which has a static value, uninfluenced by any edges.

 <p>StaticNeuronLayer objects use these to form an input layer, the value of which can be changed without probing or manipulating Edge references from the first hidden layer.</p> 
 
	@author Stephen Wattam
	@version 0.2.0a
*/
public class StaticNeuron implements Neuron{
	/**The thresholding model to check values against.*/
	private ThresholdingAlgorithm thresholdModel;

	/** The static value of this Neuron. */
	private double value = 0;


	/**Creates a Neuron with the value given.

		@param p_value The value to set the Neuron to
		@throws ValueOutOfBoundsException in the event that the input is not suitable for the thresholding model chosen
	*/
	protected StaticNeuron(double p_value, ThresholdingAlgorithm p_thresholdModel)throws ValueOutOfBoundsException{		//for creating stupid neurons, which have no edges and thus no weighting
		thresholdModel = p_thresholdModel;
		setValue(p_value); //makes the node stupid implicitly
	}

	/** Sets the value of this neuron.  This requires that the neuron is stupid and thus the neuron is automatically set to stupid when this method is called.  
	
	  	@param p_value The value to set this neuron to.
		@throws ValueOutOfBoundsException in the event that the input is not suitable for the thresholding model chosen
	  */
	protected void setValue(double p_value) throws ValueOutOfBoundsException{
		if(!thresholdModel.validateInput(p_value))
			throw new ValueOutOfBoundsException();

		value = p_value;	//discard all weightings.
	}

	/** Returns this Static Neuron's value.
	  @return The value of this neuron
	  */
	public double value(){
		return value;
	}
}
