import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import java.awt.Window;
import java.awt.Dimension;
import java.awt.*;

/**Displays a simple 'about' dialog referencing the author.

  @author Stephen Wattam
  @version 0.1
*/  
public class AboutFrame extends JDialog{
	/**Stores a path to the extreme tomato logo*/
	private final String LOGO_PATH = "data/sourceImages/logo.png";
	/**Holds the title label*/
	private JLabel title;
	/**Is drawn on by the above two*/
	private JPanel container = new JPanel();
	
	/**Creates a new about frame.
	  @param owner The frame's owner, used for modality
	*/  
	public AboutFrame(Frame owner){
		super(owner,"About NearOCR",true);
		//setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(new Dimension(300,150));
		setResizable(false);
		title = new JLabel("Designed and written by Stephen Wattam", new ImageIcon(LOGO_PATH), JLabel.CENTER);		//how does the imageicon constructor not throw ioexceptions? pah...
		title.setVerticalTextPosition(JLabel.BOTTOM);
		title.setHorizontalTextPosition(JLabel.CENTER);

		container.add(title);
		this.add(container);
		setVisible(true);
	}
}	
