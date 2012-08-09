package NearNeural;
/**Backpropagates a layer without any kind of weighting to avoid local minima of error.

  @author Stephen Wattam
  @version 0.3
*/  
public class UnweightedBackPropagationMethod implements BackPropagationMethod{
	/**The multiplier for error gradient descent.*/
	protected double learningRate;

	/**Creates a new backpropagation object with the given learning rate.
	 	@param p_learningRate The learning rate, normally arouund 0.02 or below
	 */
	public UnweightedBackPropagationMethod(double p_learningRate){
		setLearningRate(p_learningRate);
	}

	/**Sets the learning rate of this backpropagation method.  This is a very useful method of increasing the speed and accuracy of trainig.
	  @param p_learningRate The learning rate
	  */
	public void setLearningRate(double p_learningRate){
		learningRate = p_learningRate;
	}

	/**Returns the current learning rate.
	  @return The current learning rate.
	*/  
	public double getLearningRate(){
		return learningRate;
	}


	/**Back propagates a hidden layer relative to the layer immediately following it.
	  @param downstream The layer downstream from this
	  @param neurons The neurons in the layer to backprop.
	*/
	public void backPropagateHidden(NeuronLayer downstream, ReceptiveNeuron[] neurons){
	//	thresholdModel.backPropagate(p_learningRate, p_downstream, convertNeurons());
		
		//http://www.speech.sri.com/people/anand/771/html/node37.html
		//for each output unit k
		
		for(int i=0;i<neurons.length;i++){
			ReceptiveNeuron tempNeuron = neurons[i];
			Edge[] tempEdges = tempNeuron.getInputEdges();
			
			
			double sumDownstream = 0;		//find the error on all weights
			int limit = downstream.countNeurons();//this is an expensive call
			for(int j=0;j<limit;j++){
				try{
					ReceptiveNeuron tempNeuron2 = (ReceptiveNeuron)downstream.getNeuron(j);
					Edge[] tempEdges2 = tempNeuron2.getInputEdges();
					for(int k=0;k<tempEdges2.length;k++){
						//System.out.println(k);
						//System.out.println((tempEdges2[k].getSource() == tempNeuron) + "---" +tempNeuron.isPresent(tempEdges2[k]));
						if(tempEdges2[k].getSource() == tempNeuron)
							sumDownstream += tempEdges2[k].getOldWeight() * tempNeuron2.getDelta();
						
						
					}
				}catch(IndexOutOfBoundsException IOOBe){
					IOOBe.printStackTrace();	
				}
			}
			
					//differential of value	
			//double delta = tempNeuron.value() * (1 - tempNeuron.value()) * sumDownstream;
			//System.out.println(sumDownstream);
			double delta = tempNeuron.differentialValue() * sumDownstream;

			tempNeuron.setDelta(delta);	//store for later

			//for each hidden unit h
			for(int j=0;j<tempEdges.length;j++){
				Edge tempEdge = tempEdges[j];
				double tempWeight = tempEdge.getWeight();

				double deltaWeight = learningRate * delta * tempEdge.getSource().value();
				//System.out.println(deltaWeight);
				tempEdge.setOldWeight(tempWeight);
				tempEdge.setWeight(tempWeight + deltaWeight);
				//System.out.println(deltaWeight);

			}
		}
	}


	/**This function back-propogates output nodes.  It is subtly different to other Neuron layers' backprop algorithm and is called first by NeuralNet.  It returns sum([delta]o/n), the sum of all error functions with respect to weightings on each output node.  This returned value must be passed to the other node layers during the second stage of backprop training.
	  @param desirable The desirable set of inputs to adapt to
	  @return The mean squared error for the whole net.
	  @throws IndexOutOfBoundsException in the event that the number of desirable outputs does not match that of the net.
	*/  
	public double backPropagateOutputs(double[] desirable, ReceptiveNeuron[] neurons){
		
		//return thresholdModel.backPropagate(p_learningRate, desirable, convertNeurons());

	//This finally works correctly!
	//*dances around*
		//for each output unit k
		double MeanSquaredError = 0;
	
		for(int i=0;i<neurons.length;i++){
			ReceptiveNeuron tempNeuron = neurons[i];
			Edge[] tempEdges = tempNeuron.getInputEdges();
			
			
			double error = (desirable[i] - tempNeuron.value());
			

					//differential
			//double delta = tempNeuron.value() * (1 - tempNeuron.value()) * error ;
			double delta = tempNeuron.differentialValue() * error;


			tempNeuron.setDelta(delta);
			MeanSquaredError += (error*error);	//way faster than math.pow
			//System.out.println(desirable[i]  + ":" + tempNeuron.value());
			//System.out.println("Delta: " + delta);


			//for each hidden unit h
			for(int j=0;j<tempEdges.length;j++){
				Edge tempEdge = tempEdges[j];
				double tempWeight = tempEdge.getWeight();

				tempEdge.setOldWeight(tempWeight);
				double deltaWeight = learningRate * delta * tempEdge.getSource().value();
				tempEdge.setWeight(tempWeight + deltaWeight);
				//System.out.println(deltaWeight);

			}
		}

		return (MeanSquaredError/neurons.length);
	}
}
