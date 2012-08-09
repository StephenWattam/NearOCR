package NearNeural;
import java.util.Vector;
import java.util.Iterator;
/**A threaded version of UnweightedBackPropagationMethod which uses a standard gradient-descent backpropagation method.  One thread per passed neuron is started, each of which recurses and follows all paths through the net.  This results in some large overheads for thread management, but allows for faster training if using a cluster or multi-core machine.  It is 100% algorithmically compatible with UnweightedBackPropagationMethod and can be swapped out without re-training destroying the net's abilities.

  <p>The only instance in which this is worth using instead of an unthreaded backpropagation method is where many threads may run in parallel (such as a quad-core chip) and where each output neuron is likely to have many edges leading into it.  The overheads involved in the creation of a thread per neuron are worthwhile only when many edges need to be processed per neuron.</p>

  @author Stephen Wattam
  @version 0.3
*/  
public class ThreadedUnweightedBackPropagationMethod extends UnweightedBackPropagationMethod implements BackPropagationMethod{
	/**Creates a new ThreadedUnweightedBackPropagationMethod with the given learning rate.
	  @param p_learningRate The learning rate to use.
	*/  
	public ThreadedUnweightedBackPropagationMethod(double p_learningRate){
		super(p_learningRate);
	}

	/**Back propagates a hidden layer relative to the layer immediately following it.
	  @param downstream The layer downstream from this
	  @param neurons The neurons in the layer to backprop.
	 */
	public void backPropagateHidden(NeuronLayer downstream, ReceptiveNeuron[] neurons){
		//http://www.speech.sri.com/people/anand/771/html/node37.html
		//for each output unit k
		ReceptiveLayerBackpropagationThread[] threads = new ReceptiveLayerBackpropagationThread[neurons.length];
		for(int i=0;i<neurons.length;i++){
			threads[i] = new ReceptiveLayerBackpropagationThread(learningRate, downstream, neurons[i]);
			threads[i].start();
		}

		for(int i=0;i<threads.length;i++){
			try{
				threads[i].join();
			}catch(InterruptedException Ie){
				System.out.println("Join with thread interrupted");
			}
		}
	}


	/**This function back-propogates output nodes.  It is subtly different to other Neuron layers' backprop algorithm and is called first by NeuralNet.  It returns sum([delta]o/n), the sum of all error functions with respect to weightings on each output node.  This returned value must be passed to the other node layers during the second stage of backprop training.
	  @param desirable The desirable set of inputs to adapt to
	  @return The mean squared error for the whole net.
	  @throws IndexOutOfBoundsException in the event that the number of desirable outputs does not match that of the net.
	 */  
	public double backPropagateOutputs(double[] desirable, ReceptiveNeuron[] neurons){
		//results come in here, a global object...
		Vector<Double> MSEvals = new Vector<Double>();



		OutputLayerBackpropagationThread[] threads = new OutputLayerBackpropagationThread[neurons.length];
		for(int i=0;i<neurons.length;i++){
			threads[i] = new OutputLayerBackpropagationThread(learningRate, desirable[i], neurons[i], MSEvals);
			threads[i].start();
		}

		for(int i=0;i<threads.length;i++){
			try{
				threads[i].join();
			}catch(InterruptedException IE){
				//this is an unimportant condition which happens very infrequently
			}
		}

		//MSEvals has at this point had all MSEs dumped into it by the other threads
		double MeanSquaredError = 0;
		Iterator<Double> iter = MSEvals.iterator();
		while(iter.hasNext())
			MeanSquaredError += iter.next(); 

		return (MeanSquaredError/neurons.length);
	}


	/**This thread backpropagates outputs for a single neuron.
	  @author Stephen Wattam
	  @version 0.3
	*/
	private class OutputLayerBackpropagationThread extends Thread{
		/**Holds the learning rate.*/
		private double learningRate;
		/**Holds the desirable output for this one neuron.*/
		private double desirable;
		/**Holds a reference to the neuron this will backpropagate.*/
		private ReceptiveNeuron neuron;
		/**Holds a reference to the global set of MSE values.*/
		private Vector<Double> MSEvals;

		/**Creates a new OutputLayerBackpropagationThread that will backpropagate a single neuron in the output layer.
		  @param p_learningRate The learning rate to use for this neuron
		  @param p_desirable The desirable value to use for this neuron.
		  @param p_neuron The neuron to backpropagate
		  @param p_MSEvals The vector in which to store MSE
		*/  
		public OutputLayerBackpropagationThread(double p_learningRate, double p_desirable, ReceptiveNeuron p_neuron, Vector<Double> p_MSEvals){
			neuron = p_neuron;
			learningRate = p_learningRate;
			desirable = p_desirable;
			MSEvals = p_MSEvals;
		}

		/**Starts the thread running.*/
		public void start(){
			Edge[] tempEdges = neuron.getInputEdges();
			double error = (desirable - neuron.value());
			double delta = neuron.differentialValue() * error;

			neuron.setDelta(delta);
			MSEvals.add((error*error));	//faster than math.pow

			//for each hidden unit h
			for(int j=0;j<tempEdges.length;j++){
				Edge tempEdge = tempEdges[j];
				double tempWeight = tempEdge.getWeight();

				tempEdge.setOldWeight(tempWeight);
				double deltaWeight = learningRate * delta * tempEdge.getSource().value();
				tempEdge.setWeight(tempWeight + deltaWeight);
			}

		}

	}

	/**This thread backpropagates a single neuron in any hidden layer.
	  @author Stephen Wattam
	  @version 0.3
	*/  
	private class ReceptiveLayerBackpropagationThread extends Thread{
		/**Holds the learning rate.*/
		private double learningRate;
		/**Holds a reference to the neuron layer that was backpropagated immediately before the one in which the current neuron resides.*/
		private NeuronLayer downstream;
		/**Holds a reference to the neuron to backpropagate.*/
		private ReceptiveNeuron neuron;

		/**Creates a new ReceptiveLayerBackpropagationThread that will backpropagate a single neuron in a hidden layer.
		  @param p_learningRate The learning rate to use for this neuron
		  @param p_downstream The downstream neuron layer.
		  @param p_neuron The neuron to backpropagate
		*/  
		public ReceptiveLayerBackpropagationThread(double p_learningRate, NeuronLayer p_downstream, ReceptiveNeuron p_neuron){
			neuron = p_neuron;
			learningRate = p_learningRate;
			downstream = p_downstream;
		}

		/**Starts the thread running.*/
		public void start(){
			Edge[] tempEdges = neuron.getInputEdges();
			double sumDownstream = 0;		//find the error on all weights
			int limit = downstream.countNeurons();//this is an expensive call
			
			for(int j=0;j<limit;j++){
				try{
					ReceptiveNeuron neuron2 = (ReceptiveNeuron)downstream.getNeuron(j);
					Edge[] tempEdges2 = neuron2.getInputEdges();
					for(int k=0;k<tempEdges2.length;k++){
						if(tempEdges2[k].getSource() == neuron)
							sumDownstream += tempEdges2[k].getOldWeight() * neuron2.getDelta();

					}
				}catch(IndexOutOfBoundsException IOOBe){
					IOOBe.printStackTrace();	
				}
			}

			double delta = neuron.differentialValue() * sumDownstream;
			neuron.setDelta(delta);	//store for later

			//for each hidden unit h
			for(int j=0;j<tempEdges.length;j++){
				Edge tempEdge = tempEdges[j];
				double tempWeight = tempEdge.getWeight();
				double deltaWeight = learningRate * delta * tempEdge.getSource().value();
				tempEdge.setOldWeight(tempWeight);
				tempEdge.setWeight(tempWeight + deltaWeight);
			}
		}

	}

}

