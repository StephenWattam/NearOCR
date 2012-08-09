import NearOCR.*;
import NearNeural.*;

/**Dispatches weight analysis to adjust all symbol weighted nodes ot the current net stack.
  @author Stephen Wattam
  @version 0.1
*/  
public class WeightAnalysisDispatcher extends Thread{
	/**The object to notify of progress*/
	private ProgressListener listener;
	/**The OCRManager to use for analysis*/
	private OCRManager OCR;

	//public static double[] run(NeuralNet[] nets, ScalableImage image, ProgressListener listener){
	/**Creates a new dispatcher with the given values, which then waits to be run().
	  @param p_OCR The OCRManager to use for analysis.
	  @param p_listener The listener to notify during the operation
	*/  
	public WeightAnalysisDispatcher(OCRManager p_OCR, ProgressListener p_listener){
		listener = p_listener;
		OCR = p_OCR;
	}

	/**Runs the analyis*/
	public void run(){
		OCR.analyseSymbolWeights(listener);
	}
}
