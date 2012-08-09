package NearOCR;
import java.util.Vector;
import java.awt.image.BufferedImage;
/** Retains information specific to one document.  This can be saved and loaded to retain all information.

  <p>A document consists of an image and a Vector of letters, which represent position, value and the original image (both scaled and unscaled of each symbol in the file.</p>

  @see Letter
  @author Stephen Wattam
  @version 0.1
*/
public class Document{

	/**Retains the original image from which this document is generated*/
	private BufferedImage sourceImage;

	/**All symbols in the file*/
	private Vector<Letter> letters = new Vector<Letter>();
	//private String value;

	/**A simple constructor to instantiate the image on which the document is to be based.
	 	@param p_image The image on which to base the new Document
	*/
	public Document(BufferedImage p_image){
		setImage(p_image);
		//loadImage(p_image);
	}

	/**Sets the image that this document is based on.  Doing this will make the letters obselete, so also call clearLetters.
	  @param p_image The image to base this Document on.
	  @see #clearLetters()
	*/
	public void setImage(BufferedImage p_image){
		sourceImage = p_image;
		letters = new Vector<Letter>();
	}

	/**Returns the image that this document is based on.
	  @return The image on which this Document is based.  This can be obselete, say, if it has been changed by sloppy assignment and differs from its component Symbols.
	  @see #setImage(BufferedImage)
	*/
	public BufferedImage getImage(){
		return sourceImage;
	}

	/**Returns an array of all Letters contained within this document.
	  @return ALl letters of which this document comprises.
	  @see Letter
	*/
	public Letter[] getLetters(){
		return (Letter[])letters.toArray(new Letter[letters.size()]);
	}

	/**Adds a letter to the list of letters.  This will merely append it, meaning that any in-order traversal of the array returned from getLetters can be made to return a nicely-ordered string.
	  @param p_letter The letter to add to the Document
	*/
	public void addLetter(Letter p_letter){
		letters.add(p_letter);
	}

	/**Clears the document of letters.  Can be useful when re-analysing documents using the OCRManager.
	  @see OCRManager
	*/
	public void clearLetters(){
		letters = new Vector<Letter>();
	}
}
