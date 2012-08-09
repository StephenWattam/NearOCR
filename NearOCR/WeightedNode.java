package NearOCR;
/**Holds an adjusted weight for one output.  A symbol contains many of these, one for each serialised net output node.  
  
  @author Stephen Wattam
  @version 0.1
 
*/
public class WeightedNode{
	/**Stores the index number of the total OCR system output*/
	private int index;
	/**Stores the weight by which the output will be multiplied*/
	private double weight;
	/**Stores the intended value, as set by the user.*/
	private double target;
	
	/**Creates a new weighted node with a set index, weight and target.
	 *
	 * @param p_index The index number of the node
	 * @param p_target The target value of the node, initially 0
	 * @param p_weight The weight value of the node, initially 1 to preserver net output
	 */ 
	public WeightedNode(int p_index, double p_target, double p_weight){
		//System.out.println("p_index:" + p_index + "\tp_target:" + p_target + "\tp_weight:" + p_weight);
		setIndex(p_index);
		setTarget(p_target);
		setWeight(p_weight);
	}

	/**Sets the target value, from which error is calculated
	 * @param p_target The value to set as a target, from 0 to 1 inclusive
	 * @throws ValueOutOfBoundsException Thrown in the event that the value given in outside of the permissable range
	 */ 
	public void setTarget(double p_target)/* throws ValueOutOfBoundsException*/{
	/*	if(p_target < 0 || p_target > 1)
			throw new ValueOutOfBoundsException();
	*/
		target = p_target;
	}

	/**Returns the current target value, between 0 and 1, of this weighted node.
	 * @return The target value
	 */ 
	public double getTarget(){
		return target;
	}

	/**Sets the index number of this node.
	 * @param p_index the index to set this node to.
	 */ 
	public void setIndex(int p_index){
		index = p_index;
	}

	//most useless javadoc alert
	/**Returns the current index of this node.
	 * @return The current index of this node.
	 */ 
	public int getIndex(){
		return index;
	}

	/**Sets the weight of this node, by which the output of the net will be multiplied in order to fin its true output.
	 * @param p_weight The weight to set
	 */ 
	public void setWeight(double p_weight){
		weight = p_weight;
		//Logger.logInfo("Weight on node " + index + " changed to " + weight);
	}

	/**Returns the weight this node currently holds.
	 * @return The current weight of the node
	 */ 
	public double getWeight(){
		return weight;
	}

	/**Returns the difference between the weight-adjusted output neuron value and the internally stored target.  This takes the form deviation == target-(weight*p_output)
	 * @param p_output The output neuron's value
	 * @return The deviation for this node only
	 */ 
	public double getDeviation(double p_output){
		return Math.abs((target-(weight*p_output)));	//assuming 1 is target value
	}

	/**Adjusts the weight of this node such that its weight multiplied by the given output is exactly equal to the target.  This is used to compensate for the unique fingerprints of each net, removing inaccuracy whilst still allowing for a weighting system to judge confidence values.
	  @param p_output The output from the net stack which matches this node's index number.
	*/  
	public void adjustWeightByDeviation(double p_output){
		weight = ((target)/p_output);
	}

	/**Returns this node represented as a string.  This is used in the save/load process.
	 * @return A string representing this object.
	 */ 
	public String toString(){
		return new String(index + "," + target + "," + weight);
	}
}
