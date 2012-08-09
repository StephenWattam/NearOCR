import NearOCR.*;
import NearNeural.*;
/**Dispatches training threads, allowing the UI to continue updating.

  @author Stephen Wattam
  @version 0.1
*/  
public class TrainingDispatcher extends Thread{
	/**The OCR manager that is being used*/
	private OCRManager OCR;
	/**Stores the net to be trained*/
	private NeuralNet net;
	/**Sores all symbols to be run*/
	private Symbol[] symbols;
	/**How many loops to train for*/
	private int loops;
	/**How many cycles per loop to train symbols for*/
	private int cycles;
	/**The parent to report ProgressListener events to*/
	private TrainPanel parent;
	/**The target MSE*/
	private double MSEMin;
	/**The offset to the beginning of the net's outputs in the output stack*/
	private int offset;
	/**The amount of entropy to train with*/
	private double entropy;

	/**Creates a new training dispatcher with the parmeters given.
	  @param p_net The neural net to train
	  @param p_symbols The symbols to train the given net wirh
	  @param p_loops The number of times to loop all training data with
	  @param p_cycles The number of cycles per character to train
	  @param p_parent The listener to alert to events
	  @param p_MSEMin The target MSE to hit during training
	  @param p_offset The offset until the beginning of the net's outputs in the output stack
	  @param p_entropy The amount of entropy, 0 to 1, to train with
	*/  
	public TrainingDispatcher(NeuralNet p_net, Symbol[] p_symbols, int p_loops, int p_cycles, TrainPanel p_parent, double p_MSEMin, int p_offset, double p_entropy, OCRManager p_OCR){
		net = p_net;
		symbols = p_symbols;
		loops = p_loops;
		cycles = p_cycles;
		parent = p_parent;
		MSEMin = p_MSEMin;
		offset = p_offset;
		entropy = p_entropy;
		OCR = p_OCR;
	}

	/**Runs the training analysis*/
	public void run(){
		TrainingManager.trainAllSymbols(net, symbols, loops, cycles, parent, MSEMin, offset, entropy, OCR.getReceptorManager());
	}
}
