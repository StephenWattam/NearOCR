package NearNeural;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

/**
A practical implementation of a feed-forward back propogating multi-layer perceptron network.  In this implementatiion each net is comprised of layers of Neurons, connected by edges.  Layers represent merely an ordered group of neurons which are guaranteed not to be connected either to themselves or to those in the layer downstream from themselves.  Any number of input and output nodes are supported, with as little as two layers and a sum of two Neurons in the whole net.

<p>The net can be trained by providing 'perfect' data in a given pattern using the train method.  Its memory can be saved to a file and later loaded to allow for persistent learning.  A custom backpropagation model defines what algorithm is used to train the net.</p>

<p>As of v0.2.0a Edges between nodes can be broken and moved manually.  This is extremely powerful but also extremely dangerous as the net does not check for circular links.  If you are to add entropy to link management it is probably wise to implement a checking algorithm.</p>

@author Stephen Wattam
@version 0.3.0b
*/
public class NeuralNet{
	/**The version of the net code.*/
	public final String version = "0.3.0b";

	/** The versions from which this version can load files. */
	public final String[] supportedVersions = {/*"0.1.0a", "0.1.1a", "0.1.2a", "0.2.0a" "0.2.1a"*/ "0.3.0b"};
	
	/**Holds the object responsible for back propagation.*/
	private BackPropagationMethod backprop;

	/**The input nodes are stored in this layer.*/
	private StaticNeuronLayer inputLayer;

	/**All hidden neurons are stored here, as any descendant of the ReceptiveNeuron class.*/
	private ReceptiveNeuronLayer[] hiddenLayers;

	/**Creates a new net with p_layers.length+1 layers, each with n nodes in them.  p_layers must be of length one or above, each layer containing at least one Neuron else an IndexOutOfBoundsException exception will be thrown.  The input data provided is taken as the input layer.
		@param p_inputs The array of inputs.  Length of the input layer is taken from these.
		@param p_layers An array holding how many Neurons per NeuronLayer to maintain.
		@param p_thresholdModel The thresholding model to use for all neurons
		@param p_backprop The back propagation system to use for the net's training
		@throws IndexOutOfBoundsException if p_layers[] has fewer than 2 aspects
		@throws ValueOutOfBoundsException if not enough Neurons exist in each layer to make the net functional (p_layers[i] < 1) 
	*/
	public NeuralNet(double[] p_inputs, int[] p_layers, ThresholdingAlgorithm p_thresholdModel, BackPropagationMethod p_backprop) throws IndexOutOfBoundsException, ValueOutOfBoundsException{
		if(p_layers.length < 1)
			throw new IndexOutOfBoundsException("At least an output layer are necessary to instantiate a net");
		for(int i=0;i<p_layers.length;i++)
			if(p_layers[i] < 1)
				throw new ValueOutOfBoundsException("At least one Neuron per layer is required", p_layers[i]);
		if(p_inputs.length < 1)
			throw new ValueOutOfBoundsException("At least one input is necessary to instantiate a net");

		setBackPropagationMethod(p_backprop);
		reconstructNet(p_inputs, p_layers, p_thresholdModel);
		preCalculate();	//make sure default values are precalculated
	}

	/**Returns the BackPropagationMethod used to train the net.
	  @return The BackPropagationMethod used when the net trains
	*/  
	public BackPropagationMethod getBackPropagationMethod(){
		return backprop;
	}

	/**Sets the BackPropagationMethod that willl be used to train the net.
	  @param p_backprop The BackPropagationMethod that will be used when train is called to adjust weights
	*/  
	public void setBackPropagationMethod(BackPropagationMethod p_backprop){
		backprop = p_backprop;
	}

	/**Rebuilds the net using input values provided and all Edge weights randomised.
	  @param p_inputs The inputs to apply to the net.
	  @param p_layers An array representing the number of Neurons in each layer
	  @param p_thresholdModel The threshold model to use
	*/
	private void reconstructNet(double[] p_inputs, int[] p_layers, ThresholdingAlgorithm p_thresholdModel) throws ValueOutOfBoundsException{

		hiddenLayers = new ReceptiveNeuronLayer[(p_layers.length)];	//create the array

		//create neurons to fill the layers
		inputLayer = new StaticNeuronLayer(p_thresholdModel);
		for(int i=0;i<p_inputs.length;i++)
			inputLayer.addNeuron(new StaticNeuron(p_inputs[i], p_thresholdModel));
		
		
		
		for(int i=0;i<(p_layers.length);i++){	//for each hidden layer
			hiddenLayers[i] = new ReceptiveNeuronLayer(p_thresholdModel);
			for(int j=0;j<p_layers[i];j++){	//for each neuron in this layer
				ReceptiveNeuron linkedNeuron = new ReceptiveNeuron(p_thresholdModel);
				if(i==0){ linkedNeuron.addEdges(inputLayer.getNeurons()); } else { linkedNeuron.addEdges(hiddenLayers[(i-1)].getNeurons()); }
				hiddenLayers[i].addNeuron(linkedNeuron);
			}
		}

	}

	/** Returns a layer such that one may edit its nodes and structure manually.  This is very dangerous and may cause the net to fail back propagation or not save/load.

	  <p>When requesting layers be aware of any polymorphism that may be all cunning and sneaky.</p>

	  @param p_index The index of the layer to acquire, with 0 being the input layer.
	  @return NeuronLayer The layer requested
	  @throws IndexOutOfBoundsException if the index provided is out of bounds.
	  @see #getNumberOfLayers
	  */
	public NeuronLayer getLayer(int p_index) throws IndexOutOfBoundsException{
		if(p_index < 0 || p_index > (getNumberOfLayers()))
			throw new IndexOutOfBoundsException("Invalid index given.  counts start from 0 and include input/output layers.");
		
		if(p_index == 0)
			return inputLayer;

		return hiddenLayers[(p_index-1)];
	}
	
	/**Returns a neuron from anywhere in the current net.
	  @param p_layerIndex The index number of the layer in which the Neuron resides
	  @param p_neuronIndex The index number of the neuron witin the specified layer
	  @return The neuron selected
	  @throws IndexOutOfBoundsException in the event that either of the indices is out of range
	  */
	public Neuron getNeuron(int p_layerIndex, int p_neuronIndex) throws IndexOutOfBoundsException{
		return getLayer(p_layerIndex).getNeuron(p_neuronIndex);
	}

	/** Precalculates all neuron values in order to speed up the net's calculation.  Values are precalculated in order away from the inputs (ripping down the layers).  This is called every time one sets inputs or back propagates to ensure that the net stays current.  It will not need to be called manually unless one edits neuron edges manually.
	*/
	public void preCalculate(){
		for(int i=0;i<hiddenLayers.length;i++)
			hiddenLayers[i].preCalculate();
	}

	/**Sets input values from an array of doubles.  This array should have a length equal to the number of input nodes else an IndexOutOfBoundsException will be thrown.  All values must be between -1 and 1 else a ValueOutOfBoundsException will be thrown.

	  <p>Note that this value automatically calls preCalculate and hence inherits some slowness.</p>
		@param p_values An array of doubles to set the input nodes as.
		@throws IndexOutOfBoundsException if the wrong amount of aspects are found in p_values, passed back from NeuronLayer.setInputValues
		@throws ValueOutOfBoundsException if the wrong values are contained in p_values, passed back from NeuronLayer.setInputValues
		@see NeuronLayer
		@see #setInputValue(double, int)
		@see #preCalculate()
	*/
	public void setInputValues(double[] p_values) throws IndexOutOfBoundsException, ValueOutOfBoundsException{
		inputLayer.setInputValues(p_values);
		preCalculate();
	}

	/**Allows the user to set the value of one input Neuron.  This must be a permissable input value according to the threshold model used.
		@param p_value The value, between 0 and 1, to set the Neuron to.
		@param p_index The index of the input node to set.
		@throws IndexOutOfBoundsException in the event that the index given is invalid
		@throws ValueOutOfBoundsException in the event that the given value is outside of the permissable input range of the function given
		@see #setInputValues(double[])
	*/
	public void setInputValue(double p_value, int p_index) throws IndexOutOfBoundsException, ValueOutOfBoundsException{
		inputLayer.setInputValue(p_value, p_index);
		preCalculate();
	}

	/**Returns the number of layers in the net, without any extra information. 
	     @return The number of layers, inclusive of inputs
	 */
	public int getNumberOfLayers(){
		return (hiddenLayers.length+1);
	}

	/**Returns an array of integers representing the structure of the net.  Array aspect 0 contains the number input nodes, Array aspect (length-1) contains the number of output nodes.
	  	@return The structure of the net, represented as an array of integers
		@see NeuronLayer
	*/
	public int[] getLayerStructure(){
		int[] layerStructure = new int[getNumberOfLayers()];
		layerStructure[0] = inputLayer.countNeurons();
		for(int i=0;i<hiddenLayers.length;i++)
			layerStructure[(i+1)] = hiddenLayers[i].countNeurons();
		return layerStructure;
	}

	/**Returns the output of the net as an array of doubles as long as the amount of output Neurons.  This output is precalculated when preCalculate is run, which is implicit when inputs are changed.
		@return All values from output Neurons represented as an array of doubles.
	*/
	public double[] getOutput(){
		Neuron[] resultNeurons = hiddenLayers[getNumberOfLayers()-2].getNeurons();
		double[] results = new double[resultNeurons.length];
		
		for(int i=0;i<results.length;i++)
			results[i] = resultNeurons[i].value();

		return results;
	}

	/**Returns the output layer of the net.  This is merely a convenience method with comparison to getLayer() and getLayerStructure().
	  @return ReceptiveNeuronLayer The output layer of the net
	  @see #getLayerStructure()
	  @see #getLayer(int)
	*/  
	private ReceptiveNeuronLayer getOutputLayer(){
		return hiddenLayers[hiddenLayers.length-1];//last hidden is output
	}

	/**Runs the back propogation algorithms on the whole net.  The backprop code works with the last set of input values, so the training cycle must consist of two calls: setInputValues and then train with correlating data.
		@param p_desirable An array of desirable output doubles, matched in length to the amount of output Neurons (those in the final layer).
		@return The mean squared error for this net, as returned by BackPropagationMethod.backPropagateOutputs
		@throws IndexOutOfBoundsException if the wrong amount of desirable outputs is passed
	*/
	public double train(double[] p_desirable) throws IndexOutOfBoundsException{
		if(p_desirable.length != getOutputLayer().countNeurons())
			throw new IndexOutOfBoundsException("Discrepancy between number of desirable data and number of outputs in current net");

		double MeanSquaredError = backprop.backPropagateOutputs(p_desirable, getOutputLayer().getNeurons());
		
		for(int i=(getLayerStructure().length-2);i>0;i--)	//walk backwards
				backprop.backPropagateHidden(getLayer(i+1), ((ReceptiveNeuronLayer)getLayer(i)).getNeurons());
				//((ReceptiveNeuronLayer)getLayer(i)).backPropagate(getLayer(i+1), ((ReceptiveNeuronLayer)getLayer(i)).convertNeurons());
		
		preCalculate();
		return MeanSquaredError;
	}

	/**Returns all neurons in this net. Note that any neurons with index 0 through to getLayer(0).length will be static.  Those afterwards will be some derivative of ReceptiveNeuron.
	 <p>This routine is used in saving and loading and can be used by the end user to perform pruning operations on Edges and neurons.  There is, however, a prune method.</p>
	  @return All neurons in the net
	*/
	private Neuron[] getAllNeurons(){		
		int[] layers = getLayerStructure();
		int neuronCount = 0;
		for(int i=0;i<layers.length;i++)
			neuronCount += layers[i];
		
		Neuron realNeurons[] = new Neuron[neuronCount];

		neuronCount = 0;
		for(int i=0;i<layers.length;i++){
			try{
				Neuron tempNeurons[] = getLayer(i).getNeurons();
				for(int j=0;j<tempNeurons.length;j++){
					realNeurons[neuronCount] = tempNeurons[j];
					neuronCount++;
				}
			}catch(IndexOutOfBoundsException IOOBe){
				//if finding neurons fails then they probably don't exist, so skip over and try the next.  The user need not know
			}
		}
		return realNeurons;
	}

	/**Removes a neuron and all edges that point to and from it.  Useful for pruning nets according to user-defined rules such as average edge weightings.  Be aware of the prune method, however, which will probably suffice for most simple prune operations.  This method runs the garbage collector, which is quite resource intensive.
	  @param p_neuron The neuron to remove from this net.  Various other methods allow for finding this.
	  @throws ItemNotFoundException if the neuron passed is not in the net.
	  */
	public void removeNeuron(Neuron p_neuron) throws ItemNotFoundException{
		try{
			Neuron tempNeurons[] = getAllNeurons();

			//remove edges to the neuron, including circular ones
			for(int i=inputLayer.countNeurons();i<tempNeurons.length;i++){
				ReceptiveNeuron currentNeuron = (ReceptiveNeuron)tempNeurons[i];
				Edge[] tempEdges = currentNeuron.getInputEdges();
				for(int j=0;j<tempEdges.length;j++){
					if(tempEdges[j].getSource() == p_neuron)
						currentNeuron.removeEdge(tempEdges[j]);
				}
			}

			//removing the neuron from its layer now orphans it and its edges.  I run the garbage collector manually to ensure it's all picked up in time for any training the user might do, which requires an imperial shitload of memory.

			int[] layerStructure = getLayerStructure();
			int layerIndex = 0;
			for(int i=0;i<layerStructure.length;i++){	//for each layer
					NeuronLayer currentLayer = getLayer(i);
					if(currentLayer.isPresent(p_neuron)){
						currentLayer.removeNeuron(p_neuron);
						layerIndex = i;
					}
			}

			//garbage collect yo' ass
			System.gc();


		}catch(IndexOutOfBoundsException IOOBe){
			throw new ItemNotFoundException("Error addressing net");
		}

	}

	/**Removes edges with absolute values below the threshold given.  This can be used to speed up the net without affecting output adversely.  The garbage collector is run once during this method, which is quite resource intensive.
	  @param p_threshold The value under which to remove an edge. (ie if (Math.abs(edge.getWeight()) < p_threshold))
	*/
	public void prune(double p_threshold){
		Neuron[] tempNeurons = getAllNeurons();
		for(int i=inputLayer.countNeurons();i<tempNeurons.length;i++){	//all receptive neurons
			ReceptiveNeuron tempNeuron = (ReceptiveNeuron)tempNeurons[i];
			tempNeuron.prune(p_threshold);
		}	

		//tidy up.  This may be run during training and all memory is precious.  We have also relied upon the garbage collection as a method of deletion during an expicit routine, so make sure it's tidy
		System.gc();
	}
	
	/**Saves all net memory to a file.
		@param p_file The file to save to
		@see #NeuralNet(FileReader, ThresholdingAlgorithm, BackPropagationMethod)
	*/
	public void save(FileWriter p_file) throws IOException{
		p_file.write(version + "\n");
	//	p_file.write(new Double(learningRate).toString() + "\n");
		p_file.write(new Integer(hiddenLayers.length+1).toString() + "\n");

		p_file.write(new Integer(inputLayer.countNeurons()).toString() + "\n");	//write length of layer
		for(int i=0;i<hiddenLayers.length;i++)
			p_file.write(new Integer(hiddenLayers[i].countNeurons()).toString() + "\n");	//write length of layer


		Neuron neurons[];			//loop through, input neuron values
		
		//input neuron values
		neurons = inputLayer.getNeurons();	
		for(int j=0;j<neurons.length;j++)
			p_file.write(neurons[j].value() + "\n");	//

		int offset = inputLayer.countNeurons();	//builds two arrays of neurons, one which has input neurons
		Neuron[] tempDestinationNeurons = getAllNeurons();
		ReceptiveNeuron[] tempSourceNeurons = new ReceptiveNeuron[tempDestinationNeurons.length-offset];
		for(int i=0;i<tempSourceNeurons.length;i++)	//fill the source neuron array
			tempSourceNeurons[i] = (ReceptiveNeuron)tempDestinationNeurons[i+offset];

		//ok, so now both sets of neurons are assigned.  Edges go from a source to a destination neuron
		for(int i=0;i<tempSourceNeurons.length;i++){	//for every source neuron
			Edge[] tempEdges = tempSourceNeurons[i].getInputEdges();	//get its edges
			for(int j=0;j<tempEdges.length;j++){				//for each edge
				Neuron tempNeuron = tempEdges[j].getSource();		//get its source neuron, source of data destination of edge
				for(int k=0;k<tempDestinationNeurons.length;k++){	//find the index value of destination
					if(tempNeuron == tempDestinationNeurons[k])	//if found then write to file
						p_file.write(i + "|" + tempEdges[j].getWeight() + "|" + k + "\n");	
				}
			}
		}
		

		p_file.flush();
		p_file.close();
	}

	/**Create a net from a saved net file.  This resumes the net structure, Neurons and Edges.  Nets must be loaded from files of their own version, else an IOException will be thrown.  In the event that any other error occurs with the file an IOException will also be thrown.
	 <p>Files are saved in a human-readable format which, as of version 0.1.0a has the following outline, with values being indexed in the [layer,neuron,edge weight] system</p>
<pre>
0.2.0a	//version
0.0040	//learning rate
3	//numver of layers

2	//nodes in layer 0, inputLayer
3	//nodes in hiden layers
4	//nodes in output layer

0.6	//input 0
0.8	//input 1

matrix data

</pre>	

	  <p>When loading a net you must use the same threshold model as it was built with.  Failure to do this results in very unpredictable behaviour.  Changing its backpropagation model might cause the net to 'un-learn' its previous method of achieving results and learn a new method.</p>
		@param p_file The file to load.
		@param p_thresholdModel The thresholding model to use for this reconstucted net
		@param p_backprop The backpropagation model to use.
		@throws IOException if the file is not valid and thus cannot be loaded
	*/	
	public NeuralNet(FileReader p_file, ThresholdingAlgorithm p_thresholdModel, BackPropagationMethod p_backprop) throws IOException{					//constructor throwing evil exception
		BufferedReader fin = new BufferedReader(p_file);
		try{
			boolean versionIsSupported = false;
			String fileVersion = fin.readLine();
			for(int i=0;i<supportedVersions.length;i++){
				if(fileVersion.equals(supportedVersions[i])){
					versionIsSupported = true;
					break;
				}
			}

			if(versionIsSupported){
				//load full net attributes

			//	learningRate = Double.parseDouble(fin.readLine());			//get the learning rate

				int[] numberOfLayers = new int[Integer.parseInt(fin.readLine())];	//get array holding how many Neurons are in each layer
				for(int i=0;i<numberOfLayers.length;i++)
					numberOfLayers[i] = Integer.parseInt(fin.readLine());

				//reconstructNet(numberOfLayers);

				//create input neurons, assign values to them
				inputLayer = new StaticNeuronLayer(p_thresholdModel);
				for(int i=0;i<numberOfLayers[0];i++)
					inputLayer.addNeuron(new StaticNeuron(Double.parseDouble(fin.readLine()), p_thresholdModel));

				//create unlinked neurons.
				hiddenLayers = new ReceptiveNeuronLayer[(numberOfLayers.length-1)];
				for(int i=0;i<(numberOfLayers.length-1);i++){	//loop through all hidden layers
					hiddenLayers[i] = new ReceptiveNeuronLayer(p_thresholdModel);
					for(int j=0;j<numberOfLayers[i+1];j++){
						hiddenLayers[i].addNeuron(new ReceptiveNeuron(p_thresholdModel));
					}
				}
				
				int offset = inputLayer.countNeurons();	//builds two arrays of neurons, one which has input neurons
				Neuron[] tempDestinationNeurons = getAllNeurons();
				ReceptiveNeuron[] tempSourceNeurons = new ReceptiveNeuron[tempDestinationNeurons.length-offset];
				for(int i=0;i<tempSourceNeurons.length;i++)	//fill the source neuron array
					tempSourceNeurons[i] = (ReceptiveNeuron)tempDestinationNeurons[i+offset];
			
				String inputLine;
				while((inputLine = fin.readLine()) != null){
					StringTokenizer tokens = new StringTokenizer(inputLine, "|");
					
					int sourceNeuronIndex = Integer.parseInt(tokens.nextToken());
					double tempEdgeWeight = Double.parseDouble(tokens.nextToken());
					int destinationNeuronIndex = Integer.parseInt(tokens.nextToken());

					tempSourceNeurons[sourceNeuronIndex].addEdge(new Edge(tempDestinationNeurons[destinationNeuronIndex], tempEdgeWeight));
				}


				backprop = p_backprop;


			}else{
				throw new IOException();	//I know, but it *is* an IO exception, sort of.
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new IOException();
		}
		p_file.close();
	}

	/** Returns an array of strings that represent (and correlate with) version ids from which this net can load files.
	  @return A list of supported version numbers.
	*/
	public String[] getSupportedFileVersions(){
		return supportedVersions;
	}

	/**Returns a string representing the object.
	  @return The string representing the current net.
	*/
	public String toString(){
		String tempString = new String(super.toString() + ", version: " + version + ",Structure:");
		
		int[] tempInts = getLayerStructure();
		for(int i=0;i<tempInts.length;i++)
			tempString.concat(tempInts[i] + ",");
		tempString.concat("Filename: " + "placeHolder");

		return tempString;
	}
}
