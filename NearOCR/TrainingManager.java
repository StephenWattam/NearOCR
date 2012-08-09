package NearOCR;
import NearNeural.*;
import java.awt.image.BufferedImage;

/**Handles all aspects relating to training techniques for both the encapsulated Neural Net and the symbol table.

  @author Stephen Wattam
  @version 0.1
*/  
public abstract class TrainingManager{
	/**The target 'true' value for neurons, ie the maximum allowable output number for normalisation purposes.*/
	static private final double TRUE_VAL = 0.9999;
	/**The minimum possible output number for normalisation.*/
	static private final double FALSE_VAL = -0.9999;//0.0001;

	/**Adjusts all symbol weights in a single symbol.  Used to adapt a symbol table to a net stack.
	  @param nets The whole net stack to run through, ordered.
	  @param symbol The symbol whose output node weights are to be edited
	  @param listener A ProgressListener which can watch the progress of the algorithm
	*/
	static public void adjustSymbolWeights(NeuralNet[] nets, Symbol symbol, ProgressListener listener, ReceptorManager receptorManager){
		WeightedNode[] nodes = symbol.getWeights();

		double[] outputs = RunManager.run(nets, symbol, listener, receptorManager);
		for(WeightedNode weight: nodes){
			weight.adjustWeightByDeviation(outputs[weight.getIndex()]);
		}

	}

	/**Trains the net in a cyclic fashion with all symbols given.  This takes only one net, not the entire stack, yet trains it with all chosen symbols.

	  @param net The neural net to train
	  @param symbols An unordered array of symbols
	  @param iterationsPerSymbol How many times to train the net with each symbol.  Best results are low, around 1 to 10.
	  @param iterations The number of times to train the whole symbol set, best results are large, depending on training rate and how complex the symbol set is
	  @param listener A ProgressListener that can watch the operation
	  @param minMSE The mean standard error value at which to abort training.  Set to 0 in order to guarantee completion of a full cycle.
	  @param netOffset The offset in the output stack of neurons that point to the current net being run, for example, if net 0 on the stack had 23 outputs and we were training net 1 then this would be 23 - pointing to the first element which is owned by the current net.  Getting this wrong can totally destroy the training process or shift symbols around.
	  @return The average mean standard error for the training
	*/  
	static public double trainAllSymbols(NeuralNet net, Symbol[] symbols, int iterationsPerSymbol, int iterations, ProgressListener listener, double minMSE, int netOffset, double entropy, ReceptorManager receptorManager){
		listener.eventOccurred("\nStarting training of "+symbols.length+" Symbols, seeking MSE of "+minMSE+" or better through "+iterations+" iterations of "+iterationsPerSymbol+" cycles each.");

		double MSE = 0;
		for(int i=0;i<iterations;i++){
			listener.eventOccurred("\nStarting training run " + i + " of " + iterations + ", using all symbols(" + symbols.length  + ")");

			for(int j=0;j<symbols.length;j++){
				listener.valueChanged((i*symbols.length)+j,iterations*symbols.length);
				MSE += runSingleTrainingCycle(net, symbols[j], iterationsPerSymbol, listener, netOffset, entropy, receptorManager);
			}
			
			listener.eventOccurred("\nAverage MSE: " + (MSE/(i+1)));

			if((MSE/(i+1)) < minMSE){
				listener.eventOccurred("\nTarget MSE " + minMSE + " hit at iteration " + i + ", exiting training procedure");
				return (MSE/i);
			}

		}
		listener.valueChanged(0, 1);
		listener.eventOccurred("\nDone training net: average MSE across training schedule: " + (MSE/iterations));
		return (MSE/iterations);
	}

	/**Runs a single training cycle for a single net and a single symbol.
	  @param net The neural net to train
	  @param target The symbol to train with
	  @param iterations The number of times to train
	  @param listener A ProgressListener that can watch the operation
	  @param netOffset The offset in the output stack of neurons that point to the current net being run, for example, if net 0 on the stack had 23 outputs and we were training net 1 then this would be 23 - pointing to the first element which is owned by the current net.  Getting this wrong can totally destroy the training process or shift symbols around.
	  @return The average mean standard error for the training
	*/
	static public double runSingleTrainingCycle(NeuralNet net, Symbol target, int iterations, ProgressListener listener, int netOffset, double entropy, ReceptorManager receptorManager){
		listener.eventOccurred(target.getValue());
		double[] receptorValueData = parseImage(target.getImage(), entropy, receptorManager.getReceptors());

		double MSE = 0;
		if(receptorValueData.length != net.getLayerStructure()[0]){
			listener.eventOccurred("\nInputs differ in number to pixels, cannot train symbol with value: " + target.getValue());
		}else{
			try{
				MSE = train(net, receptorValueData, target.getDesiredValues(netOffset, net.getLayerStructure()[net.getLayerStructure().length-1]), iterations);
			}catch(Exception e){
				e.printStackTrace();
				listener.errorOccurred("Attmept to train with symbol '" + target.getValue() + "' failed.");
			}
		}
		return MSE;
	}

	/**Converts a BufferedImage into an array of doubles, normalised to fit within TRUE_VAL and FALSE_VAL.
	  @param sourceImage The image to parse.
	  @param entropy The amount of noise to add to the image before returning values.
	  @return An array of doubles representing the normalised raw RGB values of sourceImage
	*/
	static private double[] parseImage(BufferedImage sourceImage, double entropy, Receptor[] receptors){
		//get attributes	
		int sourceHeight = sourceImage.getHeight();
		int sourceWidth = sourceImage.getWidth();

		double[] receptorValueData = new double[receptors.length];
		double maxValue = 0;

		
		for(int k=0;k<receptors.length;k++){
			double a_X1 = receptors[k].getX1()*sourceWidth;
			double a_X2 = receptors[k].getX2()*sourceWidth;
			double a_Y1 = receptors[k].getY1()*sourceHeight;
			double a_Y2 = receptors[k].getY2()*sourceHeight;
				
			//if step size is 1 then they will hit most pixels, but more is needed for greatest accuracy.
			int steps = (int)(2.0*(Math.hypot((a_X2-a_X1),(a_Y2-a_Y1))))+1;	//double resolution

			
			//left, right, up and down must be done.  Fuckbadgers.
			
			for(int i=0;i<steps;i++){
				receptorValueData[k] += -sourceImage.getRGB(	(int)Math.floor((a_X1 + i*((a_X2-a_X1)/steps))), 
										(int)Math.floor((a_Y1 + i*((a_Y2-a_Y1)/steps))));
			}

			//average hit for the whole thing
			if(receptorValueData[k] != 0)
				receptorValueData[k] = receptorValueData[k] / steps;	

			if(receptorValueData[k] > maxValue)
				maxValue = receptorValueData[k]; 	
		}

		/*for(int i=0; i<sourceHeight ; i++){
			for(int j=0;j<sourceWidth;j++){
				receptorValueData[(i*sourceWidth)+j] = -sourceImage.getRGB(j,i);	//assign RGB vals to the array, keep track of max number
				if(receptorValueData[(i*sourceWidth)+j] > maxValue)
					maxValue = receptorValueData[(i*sourceWidth)+j]; 	
			}
		}
		*/


		//normalise
		double entropyRatio = entropy*maxValue;	//get a true value back from the entropy ratio
		double halfEntropyRatio = entropyRatio/2; //for speed
		for(int i=0;i<receptorValueData.length;i++){
			double entropyAdjustment = (Math.random() *entropyRatio) - halfEntropyRatio;
			receptorValueData[i] = (((receptorValueData[i] + entropyAdjustment)/maxValue)*(TRUE_VAL-FALSE_VAL))+FALSE_VAL;
			
		//	System.out.print(entropyAdjustment + "--");
		//	System.out.print(receptorValueData[i]);
			if(receptorValueData[i] > TRUE_VAL){
		//		System.out.print("+");
				receptorValueData[i] = TRUE_VAL;
			}
			if(receptorValueData[i] < FALSE_VAL){
		//		System.out.print("-");
				receptorValueData[i] = FALSE_VAL;
			}
		//	System.out.print("\n");
		//	System.out.println(receptorValueData[i]);
		}

		return receptorValueData;
	}

	/**Trains the net with entirely artificial targets and inputs.  Can be used to arbitrarily train nets.
	  @param net The net to train
	  @param inputs An array of doubles between 0 and 1 exclusive that are to be used as input values for the net.
	  @param desiredOutputs The outputs from which the backprop system will adjust the edge weights
	  @param iterations The number of times to repeat training
	  @return The mean standard error for this training cycle
	*/
	static private double train(NeuralNet net, double[] inputs, double[] desiredOutputs, int iterations) throws NearNeural.IndexOutOfBoundsException, NearNeural.ValueOutOfBoundsException{
		double netOutput[] = new double[net.getLayerStructure()[net.getLayerStructure().length-1]];
		double MSE = 0;
		for(int i=0;i<iterations;i++){
			net.setInputValues(inputs);		//set input values
			netOutput = net.getOutput();		//run net
			MSE += net.train(desiredOutputs);	//train net using last run values
		}

		return (MSE/iterations);
	}

	

}
