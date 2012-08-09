package NearOCR;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Vector;
import java.util.Iterator;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**The logger is a client-agnostic way of reporting events and progress from the OCR manager to the interface.  It notifies whatever is listening of time, severity and custom details of each event, which may be a success, failure or information.

  <p>Listeners must implement NearOCR.MessageListener and add themselves as message listeners in order to receive logger events</p>

  @author Stephen Wattam
  @version 0.1
*/  
public abstract class Logger{
	/**Stores all messages that have been logged and sent on.*/
	private static ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();
	/**Stores refernces to all listeners.*/
	private static Vector<MessageListener> messageListeners = new Vector<MessageListener>();
	/**The prefix used to denote a warning message.*/
	private static final String WARNING_PREFIX = "(WW) ";
	/**The prefix used to denote an error message.*/
	private static final String ERROR_PREFIX = "(EE) ";
	/**The prefix used to denote an item of information.*/
	private static final String INFO_PREFIX = "(II) ";

	/**Returns a string representing the curent time and date, such that every message can be branded with it.
	  @return A string representing current time and date
	*/  
	private static String getDateString(){
		DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return (formatter.format(new Date()) + " ");
	}

	/**Logs a message by adding it to the internal queue and sending it to all listeners.
	  @param message The full message to log
	*/
	private static void log(String message){
		messages.add(message);

		Iterator<MessageListener> iter = messageListeners.iterator();
		while(iter.hasNext())
			iter.next().messageReceived(message);
	}

	/**Logs a warning, sending it to all listeners and storing it internally.
	  @param message The warning to log
	*/
	public static void logWarning(String message){
		log(getDateString() + WARNING_PREFIX + message);
	}

	/**Logs an error, sending it to all listeners and storing it internally.
	  @param message The error to log
	*/  
	public static void logError(String message){
		log(getDateString() + ERROR_PREFIX + message);
	}

	/**Logs an information message, sending it to alll listeners and storing it internally.
	  @param message The message to log
	*/  
	public static void logInfo(String message){
		log(getDateString() + INFO_PREFIX + message);
	}

	/**Adds a message listener, which will from that moment on receive all logged events.
	  @param messageListener The MessageListener to send events to
	*/  
	public static void addMessageListener(MessageListener messageListener){
		messageListeners.add(messageListener);
	}

	/**Removes a message listener such that it will no longer be sent events.
	  @param messageListener The MessageListener to remove
	*/  
	public static void removeMessageListener(MessageListener messageListener){
		messageListeners.remove(messageListeners);
	}


}
