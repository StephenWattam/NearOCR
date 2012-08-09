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
/**A panel that allows the user to regenerate and load receptors.
	
	@author Stephen Wattam
	@version 0.1

*/
public class ReceptorPanel extends JPanel implements ActionListener{
	//private JPanel container = new JPanel(new GridBagLayout());
	/**Stores the panel on which the document is drawn*/
	private JPanel receptorView = new JPanel();

	/**Stores the names for possible receptor patterns.*/
	private String[] receptorPatterns = {"Fixed length random", "Random", "Double oblique", "Regular oblique"};
	/**Allows the user to select the receptor pattern*/
	private JComboBox pattern = new JComboBox(receptorPatterns);

	/**A spinner that allows the user to select which values to prune using*/
	//private JSpinner number = new JSpinner(new SpinnerNumberModel(100,0,10000,1));
	/**A button which allows the user to prune the net*/
	private JButton regenerate = new JButton("Regenerate");
	/**Allows the user to redraw without regenerating, to view the receptor net*/
	private JButton redraw = new JButton("Redraw");
	/**Allows the user to load a receptor net.*/
	private JButton load = new JButton("Load...");
	/**Allows the user to save a receptor net*/
	private JButton save = new JButton("Save...");
	
	/**A reference to the OCR manager which controls OCR for this session*/
	private OCRManager OCR;

	/**Creates a new slice panel using the OCR object in question.
	  @param p_OCR The OCR manager which controls logic for this document.
	*/
	public ReceptorPanel(OCRManager p_OCR){
		OCR=p_OCR;
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		receptorView.setPreferredSize(new Dimension(400,400));
		regenerate.addActionListener(this);
		redraw.addActionListener(this);
		pattern.setSelectedIndex(0);
		//pattern.addActionListener(this);
		//listen


		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridheight = 3;
		gbc.ipadx = 400;
		gbc.ipady = 400;
		this.add(receptorView,gbc);
	


		gbc.gridx=1;
		gbc.gridy=0;
		gbc.gridheight=1;
		gbc.ipadx =0;
		gbc.ipady=0;
		this.add(pattern, gbc);

		gbc.gridx=1;
		gbc.gridy=1;
		gbc.gridheight=1;
		gbc.ipadx =0;
		gbc.ipady=0;
		this.add(regenerate, gbc);
		
		gbc.gridx=1;
		gbc.gridy=2;
		gbc.gridheight=1;
		gbc.ipadx =0;
		gbc.ipady=0;
		this.add(redraw, gbc);
/*
		gbc.gridx=0;
		gbc.gridy=1;
		gbc.gridheight=0;
		gbc.gridwidth = 2;
		gbc.ipadx = 500;
		gbc.ipady=20;
		this.add(thresholdY, gbc);
*/		
		setVisible(true);
		drawReceptors();
	}

/*	public void paint(Graphics g){
		this.paintComponents(g);
		//drawDocument();
	}
*/
	/* http://www.java2s.com/Code/Java/2D-Graphics-GUI/ImageOperations.htm */
	/**Draws the document with highlighted slices.  Slices are outlined and this is called whenever someone drops a scrollbar or changes a threshold value. 
	 
	 */
	public void drawReceptors(){
		Graphics2D g2d = (Graphics2D)receptorView.getGraphics();
		if(g2d != null){
			//g2d.setBackground(Color.BLACK);
			g2d.setColor(new Color(128,128,196));
			g2d.fillRect(0,0,receptorView.getWidth(), receptorView.getHeight());
			g2d.setColor(new Color(255,0,0));

			Receptor[] receptors = OCR.getReceptorManager().getReceptors();
			for(int i=0;i<receptors.length;i++){
				g2d.drawLine(	(int)(receptors[i].getX1()*receptorView.getWidth()), 
						(int)(receptors[i].getY1()*receptorView.getHeight()), 
						(int)(receptors[i].getX2()*receptorView.getWidth()), 
						(int)(receptors[i].getY2()*receptorView.getHeight()));
			}
		       		//	This causes a crash of the VM
				//	Don't ask me why, it doesn't exactly provide much info, but it nee work anyway.
				//g2d.drawImage(letters[i].originalImage,new RescaleOp(-1.0f, 255f, null),letters[i].getX(),letters[i].getY()); 
		}
		
	}

	/**Regenerates the pattern as per the selected options and the number of net inputs.*/
	private void regenerate(){
		ReceptorPattern patternToUse;

		switch(pattern.getSelectedIndex()){
			case 0: patternToUse = new FixedLengthRandomReceptorPattern(); break;
			case 1: patternToUse = new RandomReceptorPattern(); break;
			case 2: patternToUse = new DoubleObliqueReceptorPattern(); break;
			case 3: patternToUse = new RegularObliqueReceptorPattern(); break;
			default: patternToUse = new RandomReceptorPattern(); break;
		}

		try{
			OCR.getReceptorManager().repopulate(patternToUse, OCR.countInputs());
			drawReceptors();
		}catch(ValueOutOfBoundsException VOOBe){}
	}

	/**Is called whenever an appropriate event is fired by a component on the panel, probably a button.
	   @param Ae The actionEvent that is fired by the respecitve component.
	 */
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource()==regenerate){
			regenerate();
		}else if(Ae.getSource() == redraw){
			drawReceptors();
		}else if(Ae.getSource() == load){
			System.out.println("load");
		}else if(Ae.getSource() == save){
			System.out.println("save");
		}/*else if(Ae.getSource() == pattern){
			
		}*/
	}
}
