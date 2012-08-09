package NearOCR;

/**A pattern of receptors with totally random position, orientation and length.  This is the simplest but also the least effective of the default receptor patterns because each single receptor may encompass huge swathes of the source image, effecitvely averaging the image to the net.
  @author Stephen Wattam
  @version 0.1
*/  
public class RandomReceptorPattern implements ReceptorPattern{
	/**An implementation of ReceptorPattern.generateReceptors(int).
	  @see ReceptorPattern#generateReceptors(int)
	*/
	public Receptor[] generateReceptors(int number){
		Receptor[] output = new Receptor[number];
		for(int i=0;i<number;i++){
			try{
				output[i] = new Receptor(limit(Math.random()),limit(Math.random()),limit(Math.random()),limit(Math.random()));
			}catch(NearNeural.ValueOutOfBoundsException VOOBe){
				//ensure that we output number Receptors.
				i--;	
			}
		}
		return output;
	}

	/**Ensures no values go beyond the bounds of the unit image used to represent Receptors.
  		@param x The variable to limit
	      @return 0 or 1 if the input exceeds bounds (lower and upper respectively) or x itself if not.		
	   */
	private double limit(double x){//this is a _double_ check, punny eh?
		if(x > 1)
			return 1.0;
		if(x < 0)
			return 0.0;
		return x;
	}
}
