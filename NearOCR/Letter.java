package NearOCR;
import java.awt.image.BufferedImage;
//import java.awt.Graphics2D;
import java.awt.*;
/**Stores a single character and forms the bulk of a Document.  Letters store many things which allow them to be placed in documents:

A value: The value of this letter (e.g. A, L, ;, etc)
An original image: the BufferedImage which represents the original, unscaled cut from the original document (can represent arbitrary data but the OCRManager takes care of slicing in most cases)
A scaled image: An image that has been scaled in line with the aspect ratio of the original, then pasted into an area which is a size as exactly specified in the scale method.  This is used to ensure that net inputs always have the correct amount of pixels.
The position and dimensions of the unscaled image - this is used to place the final output into a document and also to calculate some scaling intricacies.

<p>The scaled image is always calculated from the original, retaining quality across any number of resize operations</p>

  @author Stephen Wattam
  @version 0.1
*/
public class Letter extends CharacterImage{
	/**X co-ordinate of place where originalImage comes from in source Document*/
	private int x;
	/**Y-coordinate of place where originalImage comes from in the source Document */
	private int y;


	/**Creates a new Letter from a given image, assuming scaled is identical.
	  @param p_image The image slice to aply as a letter.
	*/
	public Letter(BufferedImage p_image){
		super(p_image);
	}

	/**Returns the current width of this letter.
	  @return The current width of this letter
	*/  
	public int getWidth(){
		return image.getWidth();	
	}

	/**Returns the current height of the letter.
	  @return The current height of the letter
	*/  
	public int getHeight(){
		return image.getHeight();
	}

	/**Sets the X co-ordinate of the letter on the page.
	  @param  p_x The X-coordinate, ignored if < 0
	*/
	public void setX(int p_x){
		if(p_x > 0)
			x=p_x;
	}

	/**Returns the current x-coordinate of the letter as placed on the document from which it was sliced.
	  @return The x-coordinate of the letter
	*/  
	public int getX(){
		return x;
	}

	/**Sets the Y coordinate of the letter as placed in the source document.
	  @param p_y The new Y coordinate, ignored is under 0
	*/  
	public void setY(int p_y){
		if(p_y > 0)
			y=p_y;
	}

	/**Returns the current Y coordinate of this letter as it was once placed in the source document.
	  @return The Y coordinate of this letter's position in the original document.
	*/  
	public int getY(){
		return y;
	}
}
