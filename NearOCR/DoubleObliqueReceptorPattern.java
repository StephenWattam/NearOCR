package NearOCR;

/**Provides a double-hatching layout designed to emulate pixel layouts.  This is one of the better receptor layouts but has much redundancy and is, as of v0.1, quite buggy.
  @author Stephen Wattam
  @version 0.1
*/  
public class DoubleObliqueReceptorPattern implements ReceptorPattern{

	/**An implementation of ReceptorPattern.generateReceptors(int).
	  @see ReceptorPattern#generateReceptors(int)
	*/
	public Receptor[] generateReceptors(int number){
		Receptor[] output = new Receptor[number];
		
		int segments = (int)(Math.floor(Math.sqrt(number))/2.0);


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
				
				try{
					output[count] = new Receptor(	(double)(i*(1.0/(double)segments) + (1.0/(double)segments)),
									(double)(j*(1.0/(double)segments)),
									(double)(i*(1.0/(double)segments)),
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
