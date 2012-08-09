package NearNeural;
/**
	An exception that handles values which are not processable as to the user's requests.

  	@author Stephen Wattam
	@version 0.1.0a
  */
public class UnresolvedValueException extends NeuralNetException{

	/** Creates a new UnresolvedValueException with the message given.

	  @param p_message The message to relay to the user
	  */
	protected UnresolvedValueException(String p_message){
		super(p_message);
	}

	/** Creates a new exception with no message */
	protected UnresolvedValueException(){

	}	
}
