package NearNeural;

/** Represents an exception caused by attempting to access an absent Neuron or other Net element.

  @author Stephen Wattam
  @version 0.1.0a
  */
public class ItemNotFoundException extends NeuralNetException{

	/** Creates a new exception with the message given.
	  @param p_message The message to relay to the user
	  */
	public ItemNotFoundException(String p_message){
		super(p_message);
	}

	/** Creates a new exception with no message */
	public ItemNotFoundException(){

	}
}
