import NearOCR.*;
import NearNeural.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.Vector;
//import java.awt.image.RescaleOp;

/**A panel that shows the progress of analysis.
 
  @author Stephen Wattam
  @version 0.1
*/
public class RunPanel extends JPanel implements ActionListener, ProgressListener{
	/**Stores a reference to the OCRManager to run data through*/
	private OCRManager OCR;

	/**A progress bar that updates as the analysis runs*/
	private JProgressBar progress = new JProgressBar(SwingConstants.VERTICAL);	

	/**Allows the user to select how important the covariance is in the final calculation*/
	private JSlider covarianceWeight;
	/**Allows the user to select how important the subtractive value is in the final calculation*/
	private JSlider subtractionWeight;
	/**A button that allows the user to run analysis*/
	private JButton run = new JButton("Run!");
	/**A button that allows the user to clear logs*/
	private JButton clear = new JButton("Clear");
	
	/**Stores a log of all events generated during the analysis.*/
	private JTextArea log = new JTextArea();
	/**Allows the log to scroll*/
	private JScrollPane logPanel;

	/**Creates a new run monitor panel, maintaining a reference to the OCR manager passed.
	  @param p_OCR The OCRManager to use when running anlysis
	*/
	public RunPanel(OCRManager p_OCR){
		OCR=p_OCR;
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		run.addActionListener(this);
		clear.addActionListener(this);

		covarianceWeight = new JSlider(JSlider.VERTICAL, 0, 200, 100);
		covarianceWeight.setPreferredSize(new Dimension(20,500));
		subtractionWeight = new JSlider(JSlider.VERTICAL, 0, 200, 100);
		subtractionWeight.setPreferredSize(new Dimension(20,500));
		
		
		logPanel = new JScrollPane(log);
		//log.setEditable(false);
		logPanel.setPreferredSize(new Dimension(730,500));

		progress.setPreferredSize(new Dimension(20,500));
		progress.setStringPainted(true);

		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth = 2;
		gbc.ipadx = 670;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		this.add(logPanel, gbc);

		gbc.gridx = 2;
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		this.add(subtractionWeight, gbc);
		
		gbc.gridx = 3;
		gbc.gridwidth = 1;
		gbc.gridy = 0;
		gbc.ipadx = 0;
		this.add(covarianceWeight, gbc);
		
		gbc.gridx=4;
		gbc.gridy=0;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.ipadx = 0;
		gbc.ipady = 500;
		gbc.weightx = 0.2;
		this.add(progress,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
	//	gbc.ipadx = 50;
		gbc.ipady = 0;
		this.add(run, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
	//	gbc.ipadx = 50;
	//	gbc.ipady = 15;
		this.add(clear,gbc);

		setVisible(true);
	}

	/**Fired when a component performs an action whilst this class is listening to it.
	  @param Ae The action even generated
	*/
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == run){
			RunDispatcher runThread = new RunDispatcher(OCR, this,  (double)((double)subtractionWeight.getValue()/(double)subtractionWeight.getMaximum()),
										(double)((double)covarianceWeight.getValue()/(double)covarianceWeight.getMaximum()));
			runThread.start();
		}else if(Ae.getSource() == clear){
			log.setText("");	
		}
	}

	/**Fired when a value is changed in the analysis process.
	  @param progress The progress made thus far
	  @param limit The maxmimum possible value of progress.
	 */
	public void valueChanged(double progress, double limit){
		if(this.progress.getMaximum() != (int)limit)
			this.progress.setMaximum((int)limit);

		this.progress.setValue((int)progress);
	}

	/**Fired when any event occurs in the analysis cycle.
	  @param action The event description
	*/
	public void eventOccurred(String action){
		log.append("\n--" + action);
		logPanel.getVerticalScrollBar().setValue(logPanel.getVerticalScrollBar().getMaximum());
	}

	/**Fired when an error occurs during the analysis process.
	  @param error The error description
	*/
	public void errorOccurred(String error){
		log.append("\n-->>" + error);
	}

	
	/**Handles a change in the scrolling pane, redrawing the image each time the user drops the scrollbar.
	 	@param Ae The event passed to this method via the interface
	 */
	/*public void adjustmentValueChanged(AdjustmentEvent Ae){
		if(Ae.getSource() == documentScroller.getVerticalScrollBar()){
			JScrollBar tempScrollBar = (JScrollBar)documentScroller.getVerticalScrollBar();
			if(!tempScrollBar.getValueIsAdjusting())
				drawDocument();
		}else if(Ae.getSource() == documentScroller.getHorizontalScrollBar()){
			JScrollBar tempScrollBar = (JScrollBar)documentScroller.getHorizontalScrollBar();
			if(!tempScrollBar.getValueIsAdjusting())
				drawDocument();
		}
	}*/
}
