package NearOCR;

/**Although slightly more capable than the random sampling receptor pattern this is still severely imperfect.  Liken it, if you will, to selecting random pixels.
  @author Stephen Wattam
  @version 0.1
*/  
public class FixedLengthRandomReceptorPattern implements ReceptorPattern{
	
	
	/**An implementation of ReceptorPattern.generateReceptors(int).
	  @see ReceptorPattern#generateReceptors(int)
	*/
	public Receptor[] generateReceptors(int number){
		Receptor[] output = new Receptor[number];

		for(int i=0;i<output.length;i++){
			double x1 = limit(Math.random());
			double y1 = limit(Math.random());
			double x2 = limit(x1 + ((Math.random()*2.0)-1)/20);
			double y2 = limit(y1 + ((Math.random()*2.0)-1)/20);
			
			try{
				output[i] = new Receptor(x1,y1,x2,y2);
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
	private double limit(double x){//this is a double check
		if(x > 1)
			return 1.0;
		if(x < 0)
			return 0.0;
		return x;
	}
}
