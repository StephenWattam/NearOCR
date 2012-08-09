
import NearOCR.*;
import NearNeural.*;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.GridLayout;
import java.awt.Color;
/**Displays the details of a single output neuron weight.
  @author Stephen Wattam
  @version 0.1
*/  
//props to http://java.sun.com/javase/6/docs/api/javax/swing/ListCellRenderer.html
public class NeuronCell extends JPanel implements ListCellRenderer{
	/**Shows the index of this weighted node.*/
	private JLabel index = new JLabel("-");
	/**Shows the target value of this weighted node.*/
	private JLabel target = new JLabel("D: N/A");
	/**Shows the weight value of this weighted node*/
	private JLabel weight = new JLabel("W: N/A");

	/**Creates a new neuron cell with blank values*/
	public NeuronCell(){
		super(new GridLayout(1,3));
		
		add(index);
		add(target);
		add(weight);

		setVisible(true);
		setOpaque(true);
	}

	
	/**Provides a renderable component for the JList.
	  @param list The JList this is in.
	  @param value The Weighted node that is the value (or any object, but undefined behaviour results in using this list cell with other objects)
	  @param index The index number of this cell
	  @param isSelected Whether or not this list cell is selected.
	  @param CellHasFocus Whether or not this cell has focus.
	*/  
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean CellHasFocus){
		WeightedNode tempNode = (WeightedNode)value;
		this.index.setText(tempNode.getIndex() + "");


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
	
		target.setText("D: " + tempNode.getTarget());
		weight.setText("W: " + tempNode.getWeight());
		
		if(tempNode.getWeight() < 1)
			setBackground(new Color(colourIntensity,colourUntensity,colourUntensity));
		else if(tempNode.getWeight() > 1)
			setBackground(new Color(colourUntensity,colourIntensity,colourUntensity));
		else	
			setBackground(new Color(colourUntensity,colourUntensity,colourIntensity));

		return this;
	}
}
