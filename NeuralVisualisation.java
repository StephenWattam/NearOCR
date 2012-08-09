
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
public class NeuralVisualisation extends Canvas {
	/**The size of the neurons, in pixels, diameter.*/
	private final double NEURON_SIZE = 10;

	/**A reference to the net that is used to grab values from.*/
	NeuralNet net;

	/**Holds the width of the current drawing, is used during resizing.*/
	int width;
	/**Holds the height of the current drawing, is used during resizing and can tweak the output*/
	int height;

	/**Holds neurons as part of a parallel array*/
	private Ellipse2D.Double neurons[];
	/**Holds real references in a parallel array with the neurons[] item*/
	private Neuron realNeurons[];

	/**Holds various objects, the type of which is based on the exact index relative to the net structure*/
	Vector<Line2D.Double> edges = new Vector<Line2D.Double>();
	/**Holds edges of various types, based on precise net structure*/
	Vector<Edge> realEdges = new Vector<Edge>();

	/**Creates a new visualisation with the given net and dimension.

	  	@param p_net The net to visualise
	 */
	public NeuralVisualisation(NeuralNet p_net){
		net = p_net;

		repaint();	
		setVisible(true);
	}

	/**Reads all neurons from the net owned by the visualiser.
	  @throws NearNeural.IndexOutOfBoundsException In the event that the NeuralNet object misrepresents its neuron count, possible due to the editing of net lists
	*/
	private void populateNeurons() throws NearNeural.IndexOutOfBoundsException{		
		int[] layers = net.getLayerStructure();
		int neuronCount = 0;
		for(int i=0;i<layers.length;i++)
			neuronCount += layers[i];
		
		neurons = new Ellipse2D.Double[neuronCount];
		realNeurons = new Neuron[neuronCount];
		
		neuronCount = 0;
		for(int i=0;i<layers.length;i++){
			Neuron tempNeurons[] = net.getLayer(i).getNeurons();

			for(int j=0;j<tempNeurons.length;j++){
				neurons[neuronCount] = new Ellipse2D.Double((((((double)width/(double)layers.length))*i)+(((double)width/(double)layers.length)/2.0))-((double)NEURON_SIZE/2.0),
									      (((((double)height/(double)tempNeurons.length))*j)+(((double)height/(double)tempNeurons.length)/2.0))-((double)NEURON_SIZE/2.0),
									       (double)NEURON_SIZE,
									       (double)NEURON_SIZE);
				realNeurons[neuronCount] = tempNeurons[j];
				neuronCount++;
			}
		}
	}

	/**Reads all Edge references and stores them for later use by the visualiser.
		@throws NearNeural.IndexOutOfBoundsException In the event that the net misrepresents its Edges with reference to the original read of neurons.
	*/
	private void populateEdges() throws NearNeural.IndexOutOfBoundsException{
		int edgeCount = 0;
		for(int i=(net.getLayer(0).countNeurons());i<realNeurons.length;i++){
			ReceptiveNeuron tempNeuron = (ReceptiveNeuron)realNeurons[i];
			Edge tempEdges[] = tempNeuron.getInputEdges();
			

			for(int j=0;j<tempEdges.length;j++){
				Neuron tempNeuron2 = tempEdges[j].getSource();
				int neuronIndex = -1;
				for(int k=0;k<realNeurons.length;k++){
					if(tempNeuron2==realNeurons[k])
						neuronIndex = k;
				}
				if(neuronIndex!=-1)
					edges.add(new Line2D.Double(neurons[i].getX()+((double)NEURON_SIZE/2.0),
								neurons[i].getY()+((double)NEURON_SIZE/2.0),
								neurons[neuronIndex].getX()+((double)NEURON_SIZE/2.0),
								neurons[neuronIndex].getY()+((double)NEURON_SIZE/2.0)));
				realEdges.add(tempEdges[j]);
			}
		}		
	}

	/**Draws the net based on values from edges which are stored in the reference vectors built during populateEdges and populateNeurons methods.
	  @param g The graphics object on which to draw
	  @see #populateNeurons 
	  @see #populateEdges
	*/
	public void paint(Graphics g){
		
		if(this.getWidth() != width || this.getHeight()!=height){
			width = this.getWidth();
			height = this.getHeight();
			try{
				edges = new Vector<Line2D.Double>();
				realEdges = new Vector<Edge>();
				populateNeurons();
				populateEdges();
			}catch(Exception e){}
		}


		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

		g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g2d.setColor(Color.RED);
		double maxWeight = 0;
		for(int i=0;i<edges.size();i++){
			Edge tempEdge = realEdges.get(i);
			if(tempEdge.getWeight() > maxWeight)
				maxWeight = tempEdge.getWeight();
		}
		
		renderText("Max edge weight: " + new Double(maxWeight).toString(), g2d, 5, 10, 8);





		for(int i=0;i<edges.size();i++){
			Edge tempEdge = realEdges.get(i);
			Color edgeColour;
			try{
				if(tempEdge.getWeight()<0)
					edgeColour = new Color(255,0,0,(int)((Math.abs(tempEdge.getWeight()/maxWeight))*254));
				else
					edgeColour = new Color(0,255,0,(int)((Math.abs(tempEdge.getWeight()/maxWeight))*254));
			}catch (IllegalArgumentException IAe){//alpha has exceeded by a tiny amount because the edge may have changed since maxWeight was calculated. no biggie.
				if(tempEdge.getWeight()<0)
					edgeColour = new Color(255,0,0,255);
				else
					edgeColour = new Color(0,255,0,255);
					
			}	
			g2d.setColor(edgeColour);

			g2d.setStroke(new BasicStroke(1));
			g2d.draw(edges.get(i));
		}

		try{	
			for(int i=0;i<realNeurons.length;i++){
				double tempVal = realNeurons[i].value();
				if(tempVal > 0){
					g2d.setColor(new Color(0,128,255,(int)Math.abs((tempVal*254))));	//value ==0-1, so ideal for this
				}else if(tempVal < 0){
					g2d.setColor(new Color(0,255,128,(int)Math.abs((tempVal*254))));	//value ==0-1, so ideal for this
				}else{
					g2d.setColor(new Color(255,128,0,(int)Math.abs((tempVal*254))));	//value ==0-1, so ideal for this
				}
				g2d.fill(neurons[i]);
				renderText(new Double(tempVal).toString(), g2d, (int)(neurons[i].getX()+(NEURON_SIZE)), (int)(neurons[i].getY()+NEURON_SIZE), 8);
			}
		}catch(IllegalArgumentException IEe){}

	}

	/**Renders text to the x-y coordinate given, on the Graphics2D plane provided.
	  	@param p_text The text which is to be written
		@param g2d The graphics2D object on which to write
		@param p_x The x co-ordinate Of the bottoom left
		@param p_y The y co-ordinate of the bottom of the text
	*/
	private void renderText(String p_text, Graphics2D g2d, int p_x, int p_y, int fontsize){
		FontRenderContext frc = g2d.getFontRenderContext();
		Font f = new Font("Helvetica",Font.BOLD, fontsize);
		String s = new String(p_text);
		TextLayout tl = new TextLayout(s, f, frc);
		Dimension theSize=getSize();
		g2d.setColor(Color.WHITE);
		tl.draw(g2d, p_x,p_y+fontsize);
		
	}

	/**Recalculates edge and neurons lists from the net.
	  	@throws NearNeural.IndexOutOfBoundsException based on the conditions of the other two.
	*/
	public void update() throws NearNeural.IndexOutOfBoundsException{
		populateNeurons();
		populateEdges();
		repaint();
	}

}
