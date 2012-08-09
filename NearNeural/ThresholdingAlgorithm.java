package NearNeural;
/**A formal definition of an algorithm which adjusts input values.

  <p>Whilst a differential algorithm may not be used there is a very large chance of it happening - any capable backpropagation algorithm will attempt to minimise error by trying to locate the direction to move in which will reduce error gradient descent), and this is far easier if the differential is given more accurately, and this can be done best by hand.  Extend ThreePointRuleDifferentiation if you wish to use numerical methods, although at the tiny descent levels usually used this is very inaccurate.  <strong>If at all possible work out and code a differential algorithm manually, ThreePointRuleDifferentiation is very very bad by comparison.</strong></p>

  @see SinusoidalThresholdModel
  @see LinearThresholdModel
  @see HyperbolicTangentThresholdModel
  @see SigmoidThresholdModel
  @author Stephen Wattam
  @version 0.3
*/
public interface ThresholdingAlgorithm{
	/**Returns the value of a neuron.  This is called by ReceptiveNeuron when it precalculates, and the value is stored in the neuron for later use, meaning that processing overheads can be offloaded onto a spare thread or into an otherwise idle part of the algorithm which manages the net/nets.

		@param edges All of the edges leading into this neuron
		@return The value of this neuron.
	 */
	public double value(Edge[] edges);

	/**Returns true if the provided number is within reasonable input bounds for this algorithm.  This is to define input ranges which are acceptable for tha algorithm, but beware that edge values are randomly assigned from -1 to 1 during net initialisation: neurons may not take the exact value limits as typed, even on the first net.  This last point is extremely important: inputs are very rarely fed in with a weight of 1.
	  @param value The number to validate
	  @return true if value is valid, false if not.
	*/  
	public boolean validateInput(double value);

	/**Returns the first differential of the value.  This is used to work out the error gradient in a standard backkpropagation algorithm, although one may be implemented that omits this trait.
	   @param edges The edges from which the value will be calculated
	   @return The differential of that value
	 */
	public double differentialValue(Edge[] edges);
}
