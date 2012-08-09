
import NearNeural.*;
import javax.swing.*; 
import javax.swing.JPanel;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.Vector;
import java.awt.font.*;
/** A panel that draws a visal representation of a neural net, displaying colour-coded edges and alpha-blended neurons.

<p>Edges display as green if their weighting is positive, or red if it is negative.  The darker an edge is the lower its abs(weight) is.  Edge weightings are displayed relatively, meaning that training cycles may not appear to adjust the net much as the weightings only accentuate their existing values and change little relative to one another.</p>

<p>Neurons are displayed as blue circles, alpha blended based on absolute values, 0 being totally transparent and 255 being a value of 1.</p>

<p>This class works by stripping references from the net and watching their values every time repaint is called.  This means that removing edges or neurons from a net with a visualiser attached is likely to cause problems - the garbage collector will not remove the objects, but internal references from the NeuralNet's component objects will no longer apply.  This means that the visualiser will retain references and throw many exceptions</p>

  @author Stephen Wattam
  @version 0.1
*/
public class NeuralVisualisationFrame extends JFrame{
	private NeuralVisualisation netVis;

	/**Creates a new visualisation with the given net and dimension.

	  	@param p_net The net to visualise
	 */
	public NeuralVisualisationFrame(NeuralNet p_net, int p_width, int p_height, String p_title){
		super(p_title);

		setSize(new Dimension(p_width,p_height));
		netVis = new NeuralVisualisation(p_net);
		netVis.setSize(new Dimension(p_width,p_height));
		this.add(netVis);

		repaint();
		setVisible(true);
	}

	public void update() throws NearNeural.IndexOutOfBoundsException{
		netVis.update();
	}

}
