package NearNeural;
/**
	A throwable exception that is the parent of all other exceptions regarding nets.

  	@author Stephen Wattam
	@version 0.1.0a
  */
public class NeuralNetException extends Exception{

	/** Creates a new NeuralNetException with the message given.

	  @param p_message The message to relay to the user
	  */
	protected NeuralNetException(String p_message){
		super(p_message);
	}

	/** Creates a new exception with no message */
	protected NeuralNetException(){

	}	
}
