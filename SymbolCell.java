
import NearOCR.*;
import NearNeural.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;

/**Shows a symbol as part of a JList.  This shows the value.

  @author Stephen Wattam
  @version 0.1
*/ 
//props to http://java.sun.com/javase/6/docs/api/javax/swing/ListCellRenderer.html
public class SymbolCell extends JPanel implements ListCellRenderer{
	/**Shows the value of the symbol to the user*/
	private JLabel val = new JLabel("-");

	/**Creates a new symbol cell.*/
	public SymbolCell(){
		this.add(val);

		setOpaque(true);
	}

	
	/**Provides a renderable component for the JList.
	  @param list The JList this is in.
	  @param value The Symbol that is the value (or any object, but undefined behaviour results in using this list cell with oher objects)
	  @param index The index number of this cell
	  @param isSelected Whether or not this list cell is selected.
	  @param CellHasFocus Whether or not this cell has focus.
	*/  
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean CellHasFocus){
		Symbol tempSymbol = (Symbol)value;
		val.setText(tempSymbol.getValue());


		int colourIntensity = 205;
		int colourUntensity = 0;
		
		if(isSelected){
			colourIntensity = 205;
			colourUntensity = 180;
		}else{
			colourIntensity = 205;
			colourUntensity = 155;
		}

		if(index%2==0){
			colourIntensity+=20;
			colourUntensity+=20;
		}
	
		setBackground(new Color(colourUntensity,colourUntensity,colourIntensity));
		
		return this;
	}


}
