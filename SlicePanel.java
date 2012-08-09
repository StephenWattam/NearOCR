import NearOCR.*;
import NearNeural.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
//import java.awt.image.RescaleOp;
/**A panel that allows the user to choose how to slice images up.

  <p>The analysis is designed to fit with text aligned horizontally.  As of version 0.1 this algorithm finds lines by taking average values of all pixels per row, then does the same per column in order to find letters.  This works for ony a subset of possible images, but is quite ok for a preliminary algorithm.  If this is in the final version then it ought not to be, just blame overambition.</p>

	@author Stephen Wattam
	@version 0.1

*/
public class SlicePanel extends JPanel implements ChangeListener, AdjustmentListener{
	//private JPanel container = new JPanel(new GridBagLayout());
	/**Stores the panel on which the document is drawn*/
	private JPanel documentView = new JPanel();

	/**The scrollable pane where the document panel resides*/
	private JScrollPane documentScroller;

	/**A slider for the X threshold value*/
	private JSlider thresholdX;

	/**A slider for the Y threshold value*/
	private JSlider thresholdY;
	
	/**A reference to the OCR manager which controls OCR for this session*/
	OCRManager OCR;

	/**Creates a new slice panel using the OCR object in question.

	  @param p_OCR The OCR manager which controls logic for this document.
	*/
	public SlicePanel(OCRManager p_OCR){
		OCR=p_OCR;
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		documentView.setPreferredSize(new Dimension(300,300));

		thresholdX = new JSlider(JSlider.VERTICAL, 0, 200, 100);
		thresholdX.setPreferredSize(new Dimension(20,500));
		thresholdY = new JSlider(JSlider.HORIZONTAL, 0, 200, 100);
		thresholdY.setPreferredSize(new Dimension(500,20));
		thresholdX.addChangeListener(this);
		thresholdY.addChangeListener(this);

		documentScroller = new JScrollPane(documentView);
		documentScroller.setPreferredSize(new Dimension(700,500));
		documentScroller.getVerticalScrollBar().addAdjustmentListener(this);
		documentScroller.getHorizontalScrollBar().addAdjustmentListener(this);


		//listen


		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridheight = 1;
		gbc.ipadx = 700;
		gbc.ipady = 500;
		this.add(documentScroller,gbc);
	
		gbc.gridx=1;
		gbc.gridy=0;
		gbc.gridheight=1;
		gbc.ipadx = 20;
		gbc.ipady=500;
		this.add(thresholdX, gbc);

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridheight=0;
		gbc.gridwidth = 2;
		gbc.ipadx = 500;
		gbc.ipady=20;
		this.add(thresholdY, gbc);
		
		setVisible(true);
		slice();
		drawDocument();
	}

/*	public void paint(Graphics g){
		this.paintComponents(g);
		//drawDocument();
	}
*/
	/* http://www.java2s.com/Code/Java/2D-Graphics-GUI/ImageOperations.htm */
	/**Draws the document with highlighted slices.  Slices are outlined and this is called whenever someone drops a scrollbar or changes a threshold value. 
	 
	 */
	public void drawDocument(){
		BufferedImage image = OCR.getDocument().getImage();
		documentView.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));

		Graphics2D g2d = (Graphics2D)documentView.getGraphics();
		if(g2d != null){
			//g2d.setBackground(Color.BLACK);
			g2d.setColor(new Color(128,128,196));
			g2d.fillRect(0,0,documentView.getWidth(), documentView.getHeight());
			g2d.drawImage(image, null, 0,0);

			Letter[] letters = OCR.getDocument().getLetters();
			for(int i=0;i<letters.length;i++){
				//g2d.drawImage(letters[i].getImage(),null,letters[i].getX(),letters[i].getY());
				g2d.setColor(new Color(128, 128, 196, 128));
				g2d.fillRect(letters[i].getX(),letters[i].getY(),letters[i].getWidth(),letters[i].getHeight());
				g2d.setColor(new Color(255,0,0));
				g2d.drawRect(letters[i].getX(),letters[i].getY(),letters[i].getWidth(),letters[i].getHeight());
		       		//	This causes a crash of the VM
				//	Don't ask me why, it doesn't exactly provide much info, but it nee work anyway.
				//g2d.drawImage(letters[i].originalImage,new RescaleOp(-1.0f, 255f, null),letters[i].getX(),letters[i].getY()); 
			}
		}
		
	}

	/**Handles a change in the scrolling pane, redrawing the image each time the user drops the scrollbar.
	 	@param Ae The event passed to this method via the interface
	 */
	public void adjustmentValueChanged(AdjustmentEvent Ae){
		if(Ae.getSource() == documentScroller.getVerticalScrollBar()){
			JScrollBar tempScrollBar = (JScrollBar)documentScroller.getVerticalScrollBar();
			if(!tempScrollBar.getValueIsAdjusting())
				drawDocument();
		}else if(Ae.getSource() == documentScroller.getHorizontalScrollBar()){
			JScrollBar tempScrollBar = (JScrollBar)documentScroller.getHorizontalScrollBar();
			if(!tempScrollBar.getValueIsAdjusting())
				drawDocument();
		}
	}

	/**Handles a value change in the threshold sliders, recalculating slices each time the slider is dropped.

	  @param Ce The event as passed according to the interface
	*/
	public void stateChanged(ChangeEvent Ce){
		if(Ce.getSource() == thresholdX || Ce.getSource() == thresholdY ){
			JSlider tempSlider = (JSlider)Ce.getSource();
			if(!tempSlider.getValueIsAdjusting()){
				slice();
				drawDocument();
			}
		}
	}

	/**Reslices all images according to slider values, causing the OCR manager to update its own letters.
	  */
	public void slice(){
		OCR.slice(	(double)((double)thresholdY.getValue()/(double)thresholdY.getMaximum()),
				(double)((double)thresholdX.getValue()/(double)thresholdX.getMaximum()));
	}
}
