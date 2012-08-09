package NearNeural;

/**Neurons are the staple of the net.  They maintain Edges to the previous layer, linking to every single other Neuron.  Neurons can either be Static or Receptive to input:
	<table border="1" width="100%" cellpadding="3" cellspacing="0">
		<thead><tr class="TableHeadingColor">
			<th>Type</th>
			<th>Use</th>
			<th>Value</th>
			<th>Number of maintained Edges</th>
		</tr></thead>
		<tr class="TableRowColor">
			<td>Static</td>
			<td>input neurons</td>
			<td>fixed, defined using setValue()</td>
			<td>0</td>
		</tr>
		<tr class="TableRowColor">
			<td>Receptive</td>
			<td>hidden, output neurons</td>
			<td>variable, accessed via value() but defined by weighted input Edges</td>
			<td>variable, by default each neuron has an Edge to every neuron in the previous layer.</td>
		</tr>
	</table>

	@author Stephen Wattam
	@version 0.2.0a
*/
public interface Neuron{	

	/** Returns the value of this Neuron, calculated through any means. */
	public double value();

}
