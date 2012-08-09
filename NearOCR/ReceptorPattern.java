package NearOCR;

/**An interface to allow creation of arbitrary receptor generating algorithms.

  <p>Receptors define inputs to the net and can be arranged arbitrarily on an image.  The value along each receptor, which constitutes a vector across a theoretical 1x1 image, is the mean pixel value of every single pixel intersected on a transformation of the letter that is being analysed.</p>
  @author Stephen Wattam
  @version 0.1
*/ 
public interface ReceptorPattern{

	//density from 0 to 1
	/**Returns an array of Receptors number in length.  It is particularly important that the returned array is of number elements as this correlates to the number of input neurons in the neural net.
	  @param number The number of receptors
	  @return An array of number Receptors in an arbitrary arrangement as determined by the specific implementation
	*/
	public Receptor[] generateReceptors(int number);
}
