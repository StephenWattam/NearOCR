import NearOCR.*;
import NearNeural.*;

/**Dispatches a run operation in a separate thread such that the UI remains responsive.  The events to update progress are handled elsewhere.
  @author Stephen Wattam
  @version 0.1
*/
public class RunDispatcher extends Thread{
	/**The object to notify*/
	private ProgressListener listener;
	/**The OCRManager to run data from and through*/
	private OCRManager OCR;
	/**The covariance weight*/
	private double covarianceWeight;
	/**The subtraction weight*/
	private double subtractionWeight;

	//public static double[] run(NeuralNet[] nets, ScalableImage image, ProgressListener listener){
	/**Creates a new rundispatcher and waits for the run() method to be called.
	  @param p_OCR The OCRManager to run data through and from
	  @param p_listener The ProgressListener to update the progress of
	  @see ProgressListener 
	*/
	public RunDispatcher(OCRManager p_OCR, ProgressListener p_listener, double p_subtractionWeight, double p_covarianceWeight){
		listener = p_listener;
		OCR = p_OCR;
		covarianceWeight = p_covarianceWeight;
		subtractionWeight = p_subtractionWeight;
	}

	/**Runs the analysis*/
	public void run(){
		OCR.analyse(covarianceWeight, subtractionWeight, listener);
	}
}
