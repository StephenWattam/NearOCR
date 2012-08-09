package NearOCR;

import java.awt.image.BufferedImage;
import java.awt.*;

/**A bitmap representation of a character.  This has a value and a BufferedImage.

  @author Stephen Wattam
  @version 0.2
*/  
public class CharacterImage{
	/**The original image, in its original size*/
	protected BufferedImage image;
	/**Stores this symbol's value*/
	protected String value = "_";

	/**Creates a new CharacterImage from a BufferedImage.
	  @param p_image The image from which to create a new CharacterImage
	*/  
	public CharacterImage(BufferedImage p_image){
		setImage(p_image);
	}
	
	/**Sets the original image of this Symbol from which the scaled one is calculated.  After setting a new image one needs to reset the scaled image size using scale.
	 * @param p_image The new image
	 */ 
	public void setImage(BufferedImage p_image){
		image = p_image;
	}

	
	/**Sets the string value of this Symbol.
	 * @param p_value The value to set this symbol to.
	 */ 
	public void setValue(String p_value){
		value = p_value;
	}


	/**Returns the value of this Symbol
	 * @return The value of this Symbol
	 */ 
	public String getValue(){
		return value;
	}

	/**Returns the original, unscaled image which represents this symbol.  This is useless as net input as it is unadjusted, but can be used for reference or size judgement purposes.
	 * @return The original image from which the Symbol was created.
	 */ 
	public BufferedImage getImage(){
		return image;
	}
}
