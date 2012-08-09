package NearNeural;
/**An exception that is generally thrown when a value is out of acceptable bounds.  Examples include when a node value exceeds 1 or 0 as upper and lower bounds respectively.

  	@author Stephen Wattam
	@version 0.1.0a
*/
public class ValueOutOfBoundsException extends NeuralNetException{

	/** Holds the value that was beyond 0 or 1. */
	private double attemptedValue;

	/** Creates a new ValueOutOfBoundsException with the message and value given.

	  @param p_message The message to relay to the user
	  @param p_value The value which caused the exception
	*/
	public ValueOutOfBoundsException(String p_message, double p_value){
		super(p_message);
		attemptedValue = p_value;
	}
	
	/** Creates a new ValueOutOfBoundsException with the given message.

	  @param p_message The message to relay to the user
	*/
	public ValueOutOfBoundsException(String p_message){
		super(p_message);
	}

	/** Creates a new exception with no message */
	public ValueOutOfBoundsException(){

	}
}
