package NearOCR;
import NearNeural.*;
import java.util.Vector;
import java.util.Iterator;
import java.io.*;
/**The net stack holds all nets that are to be run durin the OCR process.  All of the nets are run from the same input, but produce outputs which are serialised into a large array before analysis.  This means that the symbols must hold enough weights not just to parse with one net but for all that are loaded.
  @author Stephen Wattam
  @version 0.1
*/  
public class NetStack{
	/**Holds references to the nets to be used.*/
	private Vector<NeuralNet> nets = new Vector<NeuralNet>();
	/**Holds the number of input neurons.  This is of particular importance as all nets must agree on this value*/
	private int inputNeurons = 100;

	/**Creates an empty net stack.*/	
	public NetStack(){
	}


	/**Appends a net to the end of the stack.
	  @param tempNet The net to append.
	  @throws ValueOutOfBoundsException in the event that the net given has the wrong amount of input neurons.
	*/
	public void appendNet(NeuralNet tempNet) throws ValueOutOfBoundsException{
		//if vector is empty or if the new net is of the correct dimensions then...
		Logger.logInfo("Net pending with inputs: " + tempNet.getLayerStructure()[0] + " where OCR inputs require " + inputNeurons);  
		if(nets.size() == 0 || tempNet.getLayerStructure()[0] == inputNeurons){
			nets.add(tempNet);
		}else{
			throw new ValueOutOfBoundsException();
		}
		
		//if this is the only net then adapt the size
		if(nets.size() == 1)
			setInput(nets.get(0).getLayerStructure()[0]);
		//netFile = f;	//keep a reference for save operations

	}
	
	/**Attempts to adjust the number of input neurons.  This number is fixed across all currently loaded nets and regulated by the OCR manager.
	 * @param p_number The number of inputs to attempt to set
	 * @throws ValueOutOfBoundsException in the event that the desired number of inputs is incompatible with currently loaded nets.
	 */ 
	public void setInput(int p_number) throws ValueOutOfBoundsException{
		Iterator<NeuralNet> iter = nets.iterator();
		while(iter.hasNext()){
			if(iter.next().getLayerStructure()[0] != p_number)
				throw new ValueOutOfBoundsException();
		}

		//if all nets comply then
		inputNeurons = p_number;
		Logger.logInfo("Number of input neurons changed to " + inputNeurons);
	}

	
	
	
	
	/**Returns the number of input neurons that this OCR manager is currently set to using.  This is fixed and based on all nets that are currently loaded.  It can be set but must equal the same number of input neurons on every single loaded net.
	 * @return The number of input neurons.  All nets loaded must match this.
	 */ 
	public int getInputCount(){
		return inputNeurons;
	}

	
	
	
	
	
	
	
	/**Moves a net in the list of nets.  
	 *
	 * <p>The order in which nets are loaded is vital, as all nets' outputs are serialised before poat-parsing and symbol comparison.  Note that the removal of the net from the vector shuffles everything up - indexTo will have to be one less than one would normally assume.</p>
	 * @param p_indexFrom The index to request a net from
	 * @param p_indexTo The index to move the requested net to
	 * @throws java.lang.IndexOutOfBoundsException in the event that either index does not exist
	 */ 
	public void moveNet(int p_indexFrom, int p_indexTo) throws java.lang.IndexOutOfBoundsException{
		NeuralNet tempNet = nets.remove(p_indexFrom);
		if(tempNet == null)
			throw new java.lang.IndexOutOfBoundsException();
		
		nets.insertElementAt(tempNet, p_indexTo);

		/*if(p_indexTo == (nets.size()))
			nets.add(tempNet);
		else if(p_indexTo >= 0 && p_indexTo < nets.size())
			nets.insertElementAt(tempNet, p_indexTo);
		else
			throw new java.lang.IndexOutOfBoundsException();
			*/
	}

	
	
	
	
	
	
	/**Returns a net by index from this OCR manager.
	 *
	 * @param p_index The index of the net to return
	 * @throws java.lang.IndexOutOfBoundsException Thrown in the event that the index requested contains no net or does not exist
	 */ 
	public NeuralNet getNet(int p_index) throws java.lang.IndexOutOfBoundsException{
		if(p_index >= 0 && p_index < nets.size())
			return nets.get(p_index);
		else
			throw new java.lang.IndexOutOfBoundsException();
	}

	
	
	
	
	
	
	/**Removes a net from the internal list.  This allows for removal of nets without knowing index numbers.
	 * @param p_net the net to remove
	 * @throws ItemNotFoundException In the event that the net is not in the list
	 */ 
	public void removeNet(NeuralNet p_net) throws ItemNotFoundException{
		if(!nets.remove(p_net))
			throw new ItemNotFoundException();
	}

	
	
	
	
	/**Returns an array of all nets held in this OCR manager, ordered as they are to have their outputs serialised.  From this one can get enough data to prune, re-order, manipulate or replace any nets from outside the confines of the OCR manager.  Severe net restructuring may break the OCR process, and it is generally safer to use this for visualisation and edge operations only.
	 * @return An array of all neural nets held in this OCR manager.
	 */ 
	public NeuralNet[] getNets(){
		return (NeuralNet[])nets.toArray(new NeuralNet[nets.size()]);
	}
	

	/**Returns the number of nets that are currently loaded.
	  @return The number of nets currently in the stack.
	*/  
	public int countNets(){
		return nets.size();
	}	
	
	
	
	/**Returns the number of outputs this OCR system will generate.  This is equal to the sum of all outputs from all loaded nets and must be equal to the number of weighted nodes in each symbol of the symbol table.
	 *
	 * @return The number of outputs
	 */ 
	public int countOutput(){
		int outputCount = 0;
		
		Iterator<NeuralNet> iter = nets.iterator();
		while(iter.hasNext()){
			int[] layers = iter.next().getLayerStructure();
			outputCount += layers[layers.length-1];
		}
		
		return outputCount;
	}	
}
