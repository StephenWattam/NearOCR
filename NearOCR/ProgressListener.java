package NearOCR;
/**Designed to allow objects to listen in on the progress of long OCR-related operations.  Training, adapting symbols and running the OCR routine currently require a listener, which will be called when significant events happen to alert the user of progress.

  @author Stephen Wattam
  @version 0.1
*/  
public interface ProgressListener{

	/**Called when the progress is updated.  Both progress and limit may vary, and it is the implementing classes' job to keep track of this.
	  @param progress The progress made thus far
	  @param limit The target value of the progress.  This may change as the operation learns of things.
	*/  
	public void valueChanged(double progress, double limit);

	/**Called whenever a noteworthy event during the operation completes.
	  @param action A string representing what happend
	*/  
	public void eventOccurred(String action);

	/**called whenever an error, fatal or non-fatal, occurs during the operation.
	  @param error The string repsenting the error
	*/  
	public void errorOccurred(String error);

}
