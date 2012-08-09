package NearOCR;
/**An interface which allows people to receive global NearOCR events from the logger.

  @see NearOCR.Logger
  @author Stephen Wattam
  @version 0.1
*/  
public interface MessageListener{
	/**Called when any form of message is logged.
	  @param message The message which hahs just been logged
	*/  
	public void messageReceived(String message);
}
