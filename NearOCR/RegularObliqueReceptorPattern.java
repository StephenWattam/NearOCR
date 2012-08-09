package NearOCR;

/**A receptor pattern that is essentially single-hatching.  This is designed to closely emulate the detail-recognition of a pixel based system along with the flexibility of a vector based receptor.  Each receptor is of fixed length and arranged diagonally through a square pixel, meaning that if the provided number is not square there will be some 'spare' slots in the return array.  These are filled by random placement of the last few receptors.
  @author Stephen Wattam
  @version 0.1
*/  
public class RegularObliqueReceptorPattern implements ReceptorPattern{
	/**An implementation of ReceptorPattern.generateReceptors(int).
	  @see ReceptorPattern#generateReceptors(int)
	*/
	public Receptor[] generateReceptors(int number){
		Receptor[] output = new Receptor[number];
		
		int segments = (int)Math.floor(Math.sqrt(number));

		int count = 0;
		for(int i=0;i<segments;i++){
			for(int j=0;j<segments;j++){
				try{
					output[count] = new Receptor(	(double)(i*(1.0/(double)segments)),
									(double)(j*(1.0/(double)segments)),
									(double)(i*(1.0/(double)segments) + (1.0/(double)segments)),
									(double)(j*(1.0/(double)segments) + (1.0/(double)segments)));

				}catch(NearNeural.ValueOutOfBoundsException VOOBe){
					count--;
				}
				count++;
			}
		}

		for(int i=count;i<output.length;i++){
			try{
				output[i] = new Receptor(Math.random(),Math.random(),Math.random(),Math.random());
			}catch(NearNeural.ValueOutOfBoundsException VOOBe){
				//ensure that we output number Receptors.
				i--;	
			}
		}
		
		return output;
	}
}
