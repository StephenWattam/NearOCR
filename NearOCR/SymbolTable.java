package NearOCR;

import java.util.Vector;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.*;
import javax.imageio.*;
/**Stores a table of all outputs correlating to their ideal output as a character and the filepath of the original image which was used to train the net, along with an average 'off' weighting.

  @see Symbol
  @author Stephen Wattam
  @version 0.1

*/
public class SymbolTable{
	/**Stores all symbols in this symbol table*/
	private Vector<Symbol> table = new Vector<Symbol>();

	/**Creates a blank symbol table*/
	public SymbolTable(){
	}

	/**Adds a symbol to the end of the symbol table.
	 
	  	@param p_symbol The symbol to append
	*/
	public void add(Symbol p_symbol){
		table.add(p_symbol);
		Logger.logInfo("Symbol added, table now containing " + table.size());
	}

	/**Creates a new symbol table from a saved symbol table file.
	 
	  @param p_file The file from which to load
	  @throws IOException if any load aspect fails
	 */
	public SymbolTable(FileReader p_file) throws IOException{
		try{
			BufferedReader fin = new BufferedReader(p_file);
			String inputLine;
			while((inputLine = fin.readLine()) != null){
				StringTokenizer symbolTokens = new StringTokenizer(inputLine, "|");
				Symbol tempSymbol = new Symbol(symbolTokens.nextToken(), symbolTokens.nextToken());

				StringTokenizer weightTokens = new StringTokenizer(symbolTokens.nextToken(), "~");
				tempSymbol.setLength(weightTokens.countTokens());

				int i=0;
				while(weightTokens.hasMoreTokens()){
					StringTokenizer weightValueTokens = new StringTokenizer(weightTokens.nextToken(), ",");
					WeightedNode tempNode = new WeightedNode(Integer.parseInt(weightValueTokens.nextToken()),
										new Double(weightValueTokens.nextToken()).doubleValue(),
										new Double(weightValueTokens.nextToken()).doubleValue());
					tempSymbol.setWeight(tempNode, i);
					i++;
				}
				add(tempSymbol);
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new IOException();
		}
	}

	//FIX THIS EXCEPTION, IT'S A HACK
	/**Removes a symbol from the table by index.
	 * @param p_index The index of the symbol one wishes to remove
	 * @throws IndexOutOfBoundsException in the event that the index number is not pointing to a symbol
	 */ 
	public void remove(int p_index) throws IndexOutOfBoundsException{
		if(!table.remove(table.get(p_index)))
			throw new IndexOutOfBoundsException();
		Logger.logInfo("Symbol removed from table, now containing " + table.size());
	}

	/**Saves the suymbol table to the file provided.
	 *
	 * @param fout The file to write to.
	 * @throws IOException in the event that any save aspect fails.
	 */
	public void save(FileWriter fout)throws IOException{
		Iterator<Symbol> iter = table.iterator();
	
		while(iter.hasNext())
			fout.write(iter.next() + "\n");
		
		fout.flush();
		fout.close();
	}

	/**Sets the number of weighted nodes every symbol in the table has.
	 * @param p_length The number of weighted nodes.  Ought to be equal to the number of total output neurons in the OCR manager
	 */
	public void setLength(int p_length){
		Iterator<Symbol> iter = table.iterator();
	
		while(iter.hasNext())
			iter.next().setLength(p_length);

	}

	/**Returns a symbol by index.
	 *
	 * @param p_index The index of the symbol to return
	 * @return The desired symbol, if possible
	 * @throws java.lang.IndexOutOfBoundsException in the event that no symbol is found at the index provided.
	 */ 
	public Symbol getSymbol(int p_index)throws java.lang.IndexOutOfBoundsException{
		if(p_index < 0 || p_index > (table.size()-1))
			throw new java.lang.IndexOutOfBoundsException();

		return table.get(p_index);
	}

	/**Returns an array with all symbols from this table in.
	 * @return All symbols currently stored in the table, in array form
	 */ 
	public Symbol[] getSymbols(){
		return (Symbol[])table.toArray(new Symbol[table.size()]);
	}

	/**Returns the length of the table.
	 *
	 * @return The length of the table, ie how many symbols are in it.
	 */ 
	public int getLength(){
		return table.size();
	}


}
