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

/**Draws the results of running an OCR operation as text.
 
  @author Stephen Wattam
  @version 0.1
*/
public class ResultsPanel extends JPanel implements ActionListener{
	/**Holds a reference to the OCR object that is managing the OCR process*/
	private OCRManager OCR;

	/**A text area to hold all of the outputs*/
	private JTextArea results = new JTextArea();
	/**Allows the results panel to scroll*/
	private JScrollPane resultsPanel;

	/**Allows the user to save the output as a file*/
	private JButton save = new JButton("Save...");
	/**Allows the user to refresh the output from the OCR manager object*/
	private JButton refresh = new JButton("Read from document");

	/**Creates a new result reading panel linked to the given OCR manager.
	  @param p_OCR The OCR manager to link this panel to
	*/  
	public ResultsPanel(OCRManager p_OCR){
		OCR = p_OCR;
		GridBagConstraints gbc = new GridBagConstraints();
		setLayout(new GridBagLayout());
		
		resultsPanel = new JScrollPane(results);
		resultsPanel.setPreferredSize(new Dimension(700,500));

		save.addActionListener(this);
		refresh.addActionListener(this);


		gbc.gridx=0;
		gbc.gridy=0;
		gbc.gridwidth = 2;
		gbc.ipadx = 730;
		gbc.ipady = 500;
		gbc.weightx = 0.5;
		this.add(resultsPanel, gbc);
	
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
		gbc.ipadx = 0;
		gbc.ipady = 0;
		this.add(refresh, gbc);

		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridheight = 1;
		gbc.gridwidth = 1;
	//	gbc.ipadx = 50;
	//	gbc.ipady = 15;
		this.add(save, gbc);


		setVisible(true);
	}

	/**Starts a JFileChooser to save the output as stripped from the output pane.*/
	public void save(){
		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);

		if(fileChooser.showSaveDialog(this) == 0){
			try{
				File f = fileChooser.getSelectedFile();

				FileWriter fout = new FileWriter(f);
				fout.write(results.getText());
				fout.flush();
				fout.close();
			}catch(IOException IOe){
				JOptionPane.showMessageDialog(this, "Error saving.");
			}catch(Exception e){
				JOptionPane.showMessageDialog(this, "Error saving output.");
			}
		}

	}

	/**Redraws the output pane with data read from the OCRManager as passed in the constructor.*/
	public void redraw(){
		Letter[] letters = OCR.getDocument().getLetters();

		results.setText("");
		double meanWidth = 0;
		double meanHeight = 0;
		for(Letter l:letters){
			meanWidth += l.getWidth();
			meanHeight += l.getHeight();
		}
		meanWidth /= letters.length;
		meanHeight /= letters.length;

		Letter lastLetter = letters[0];
		for(Letter l:letters){
			if((lastLetter.getWidth() + lastLetter.getX() + meanWidth) < l.getX())
				results.append(" ");

			if((lastLetter.getHeight() + lastLetter.getY() + meanHeight) < l.getY() )
				results.append("\n");

			results.append(l.getValue());
			lastLetter = l;
		}
	}

	/**Fired when a component performs an action whilst this class is listening to it.
	  @param Ae The action even generated
	*/
	public void actionPerformed(ActionEvent Ae){
		if(Ae.getSource() == refresh){
			redraw();
		}else if(Ae.getSource() == save){
			save();
		}
	}
}
