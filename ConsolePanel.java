import NearOCR.Logger;
import NearOCR.MessageListener;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import java.awt.Dimension;

/**A panel that dsplays the logger results for NearOCR.  All messages received are merely appended onto the contents of a large text box.
 
  @author Stephen Wattam
  @version 0.1
*/
public class ConsolePanel extends JPanel implements MessageListener{
	/**Stores the logged messages, line by line.*/
	private JTextArea log = new JTextArea();
	/**Allows the log to scroll.*/
	private JScrollPane logPanel;

	/**Creates a new console panel, attaching itself to the logger and placing the log panel. */
	public ConsolePanel(){
		Logger.addMessageListener(this);
		
		logPanel = new JScrollPane(log);
		logPanel.setPreferredSize(new Dimension(750,520));

		this.add(logPanel);
		setVisible(true);
	}

	/**Adds a new line to the log window when a logger event is fired. 
	   @param message The message received
	   @see Logger
	 */
	public void messageReceived(String message){
		log.append("\n" + message);
	}
}
