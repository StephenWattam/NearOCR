package NearOCR;
import NearNeural.*;
/**Represents one single-unit receptor.

  @author Stephen Wattam
  @version 0.2
*/
public class Receptor{
	private double x1;
	private double y1;
	private double x2;
	private double y2;

	public Receptor(double p_x1, double p_y1, double p_x2, double p_y2) throws ValueOutOfBoundsException{
		setX1(p_x1);
		setX2(p_x2);
		setY1(p_y1);
		setY2(p_y2);
	}

	private void checkBounds(double x) throws ValueOutOfBoundsException{
		if(x < 0 && x > 1)
			throw new ValueOutOfBoundsException();
		
	}

	public void setX1(double p_x1) throws ValueOutOfBoundsException{
		checkBounds(p_x1);
		x1 = p_x1;		
	}
	
	public void setX2(double p_x2) throws ValueOutOfBoundsException{
		checkBounds(p_x2);
		x2 = p_x2;		
	}
	
	public void setY1(double p_y1) throws ValueOutOfBoundsException{
		checkBounds(p_y1);
		y1 = p_y1;		
	}
	
	public void setY2(double p_y2) throws ValueOutOfBoundsException{
		checkBounds(p_y2);
		y2 = p_y2;		
	}

	public String toString(){
		return x1 + "|" + y1 + "|" + x2 + "|" + y2;
	}

	public double getX1(){
		return x1;
	}

	public double getX2(){
		return x2;
	}

	public double getY1(){
		return y1;
	}

	public double getY2(){
		return y2;
	}
}
