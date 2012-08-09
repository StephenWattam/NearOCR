package NearOCR;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.util.Vector;
import java.io.*;
import javax.imageio.*;
import java.util.Iterator;
import java.awt.*;

/**A symbol is responsible for managing training and finding values from net output.  It comprises a very important part of the post parsing system.  Each symbol stores its character value (may be multiple characters, but is unlikely to be), the image from which the net was trained (used for on-the-fly adjustments to the nets, a reference to the original image location (used during save/load operations - symbols do not store images within their file formats) and an array of all weighted edges, the length of which must match the number of output neurons for the whole OCR manager.
 *
 * @author Stephen Wattam
 * @version 0.1
 */
public class Symbol extends CharacterImage{
	/**A relative link path to the training image*/
	private String imageRef; 
	/**All weights to apply to the final net output*/
	private WeightedNode[] weights = new WeightedNode[0];

	/**Creates a new symbol with a given value and from a given filename.
	 * @param p_value The initial value of this symbol
	 * @param p_imageFilename A path to the image to load.  The constructor loads the image itself
	 * @throws IOException in the event that any problem occurs when loading the image filepath
	 */ 
	public Symbol(String p_value, String p_imageFilename) throws IOException{
		super(ImageIO.read(new File(p_imageFilename)));
		value = p_value;
		imageRef = p_imageFilename;
	}


	/**Returns a list of all weighted nodes stored within this Symbol.  Its length should be equal to the number of output neurons in the entire OCR manager, but this is dependent upon when it was last updated using setLength.  There is no guarantee that getWeights().length == OCRManager.countOutputs().
	 * @return All weighted nodes, in order
	 * @see #setLength(int)
	 */ 
	public WeightedNode[] getWeights(){
		return weights;
	}

	/**Returns a range of weighted nodes from a given position.
	  @param p_offset The start position
	  @param p_length The number of items to return
	  @return An array of WeightedNodes with length of p_length
	*/  
	public WeightedNode[] getWeightsFromOffset(int p_offset, int p_length){
		WeightedNode[] tempNodes = new WeightedNode[p_length];
		
		for(int i=0; i< p_length;i++)
			tempNodes[i] = weights[i+p_offset];
		

		return tempNodes;
		//build and return array
	}

	/**Returns a range of desired values.
	  @param p_start The start position.
	  @param p_length The number of items to return
	  @return A list of all desired values requested in the form of an array of doubles
	*/  
	public double[] getDesiredValues(int p_start, int p_length){
		WeightedNode[] tempNodes = getWeightsFromOffset(p_start, p_length);
		double tempDes[] = new double[tempNodes.length];

		for(int i=0;i<tempNodes.length;i++)
			tempDes[i] = tempNodes[i].getTarget();
		
		
		return tempDes;
		//return segment of values from start index to end index	
	}


	/**Returns one weighted node, addressed by the index in the internal array - NOT the index stored within the weighted node!
	 * @param p_index The index number to return the node from
	 * @return The weighted node at the index given
	 */ 
	public WeightedNode getWeight(int p_index){
		return weights[p_index];
	}

	/**Sets the weight of one particular output node, indexed by value in internal array - NOT by the index as stored in the weighted node itself.
	 * @param p_weight The weight to set
	 * @param p_index The index into which to insert this weighted node.
	 */ 
	public void setWeight(WeightedNode p_weight, int p_index){
		weights[p_index] = p_weight;
	}


	/**Calculates a confidence value for this nsymbol based on net outputs.
	 * @param p_outputs An array of all net outputs from this OCRManager.
	 * @return The mean confidence for all outputs
	 */ 
	public double getMeanDeviation(double[] p_outputs){
		double total = 0;
		for(int i=0;i<p_outputs.length;i++){
			total += weights[i].getDeviation(p_outputs[i]);
		}

		return (total/p_outputs.length);
	}

	/**Returns this Symbol as represented by a string.  USed in the save/load process
	 * @return This object, as represented in a string form.
	 */ 
	public String toString(){
		String returnString = new String(value + "|" + imageRef + "|");
		for(int i=0;i<weights.length-1;i++)
			returnString += weights[i].toString() + "~";
		returnString += weights[weights.length-1].toString();

		return returnString;
	}

	/**Sets the number of output nodes, which ought to be equal to the number of total output nodes in the OCR manager.  This scales an array and hence is probably very slow.  Sizes which are smaller than current values will crop weights, those above will stuff the array with default values.
	 * @param p_length The number of nodes
	 */ 
	public void setLength(int p_length){
		WeightedNode[] tempWeights = new WeightedNode[p_length];
		for(int i=0;i<tempWeights.length;i++)
			tempWeights[i] = new WeightedNode(i,0,1);

		for(int i=0;i<tempWeights.length;i++){
			if(i<weights.length && weights[i] != null)
				tempWeights[i] = weights[i];
		}

		weights = tempWeights;
		Logger.logInfo("Number of weights changed to " + weights.length + " on Symbol with value " + value);
	}

	/**Returns the number of weighted nodes held by this symbol.
	 * @return The number of weighted nodex held by this symbol.
	 */ 
	public int getLength(){
		return weights.length;
	}	

	/**This returns a double array of all 'desirable' edges which should be firing in order to trigger the symbol.  It is one of the few methods that uses the indices stored within the nodes themselves.
	 * @return The desired values of all nodes.
	 */ 
	public double[] getDesiredValues(){
		double[] desVals = new double[weights.length];
		for(int i=0;i<weights.length;i++)
			desVals[weights[i].getIndex()] = weights[i].getTarget();
		
		return desVals;
	}


}
