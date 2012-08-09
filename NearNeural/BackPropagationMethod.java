package NearNeural;
/**Allows formally-specified backpropagation methods to be defined in order to modify the training of the net.

  <p>The net backpropagates in a slightly esoteric fashion: Delta and old weight in each edge allow for the spoofing of a 'back-calculate, forward adjust' style of backpropagation, whilst allowing the net to backpropagate faster (without considering each neuron twice).  Whilst this offers advantages in efficiency (much needed for complex nets) it does lend much complexity to the backprop process.  Instead of, as with a 'normal' algorithm, using the current weight to calculate adjustments this method requires that the weight is adjusted immediately and the old weight stored in oldweight.  This old weight variable must then be used in the hidden layers instead of the current weight.  This method avoids rifling through the net twice during backpropagation, but does require finding a neuron using a sequential search, which runs in linear time (although monitored by a sentinel).  This is still faster than the classic method in practice (honestly!)</p>

  <p>The ThresholdingAlgorithm interface includes the differential algorithm of the threshold function.  If you are to use gradient descent methods then use this differential as the base - numerical differentiation is very inaccurate at the tiny descent level due to variable inaccuracy and any thresholding algorithm may be used, so <strong>do not assume a particular threshold is used</strong></p>

  <p>My best advice would be to read the original algorithm, UnweightedBackPropagationMethod, which applies a descent down an error gradient by way of differentiation.  The ThresholdingAlgorithm interface provides both the algorithm and its differential, so gradient descent is possible easily without numerical methods.</p>

  @author Stephen Wattam
  @version 0.3
*/  
public interface BackPropagationMethod{

	/**Backpropagates the last layer directly based on the desirable set given. This is run once before all hidden layers and must adjust real values whilst storing the current ones in old weight.
	  @param desirable The set of desirable inputs.
	  @param neurons The neurons to backpropagate
	  @return The mean squared error from desired values
	*/  
	public double backPropagateOutputs(double[] desirable, ReceptiveNeuron[] neurons);

	/**Backpropagates a Hidden layer in the net. This is called once for every hidden layer in an upstream order.
	  @param downstream The neurons that are downstream from the ones which are to be adjusted.  These have already been adjusted, their old weight and the last edit can be retreived by calling getOldWeight and getDelta
	  @param neurons All neurons to backpropagate
	*/
	public void backPropagateHidden(NeuronLayer downstream, ReceptiveNeuron[] neurons);
	
}
