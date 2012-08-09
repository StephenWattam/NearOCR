package NearOCR;

import NearNeural.*;
import java.util.Vector;
import java.util.Iterator;
import java.io.*;
import java.util.StringTokenizer;

/**Manages the currently loaded receptor net and its various properties.

  @author Stephen Wattam
  @version 0.1
*/  
public class ReceptorManager{
	/**Retains a list of all receptors in the current net.*/
	private Vector<Receptor> receptors = new Vector<Receptor>();

	/**Creates a new ReceptorManager with a given receptor pattern and number of receptors.  This merely calls repopulate(ReceptorPattern, int).
	  @param p_pattern The pattern to generate new receptors using.
	  @param p_number The number of receptors to generate, should generally be identical to the number of input neurons provided by the net stack
	  @throws ValueOutOfBoundsException in the event that number is not valid
	*/
	public ReceptorManager(ReceptorPattern p_pattern, int p_number) throws ValueOutOfBoundsException{
		repopulate(p_pattern, p_number);
	}

	/**Creates an empty set of receptors.*/
	public ReceptorManager(){
	}

	/**Regenerates receptors using a set pattern.
	  @param p_pattern The pattern to generate new receptors using.
	  @param p_number The number of receptors to generate, should generally be identical to the number of input neurons provided by the net stack
	  @throws ValueOutOfBoundsException in the event that number is not valid
	*/
	public void repopulate(ReceptorPattern p_pattern, int p_number) throws ValueOutOfBoundsException{
		if(p_number < 0)
			throw new ValueOutOfBoundsException();

		receptors = new Vector<Receptor>(p_number);
		Receptor[] newReceptors = p_pattern.generateReceptors(p_number);

		for(int i=0;i<newReceptors.length;i++)
			receptors.add(newReceptors[i]);

	}

	/**Loads a set of receptors from a file.  
	  @param p_file The FileReader to read the data from
	  @throws IOException in the event that any error prevents loading the file.
	*/
	public ReceptorManager(FileReader p_file) throws IOException{
		try{
			BufferedReader fin = new BufferedReader(p_file);
			String inputLine;
			while((inputLine = fin.readLine()) != null){
				StringTokenizer tokens = new StringTokenizer(inputLine, "|");

				add(new Receptor(	Double.parseDouble(tokens.nextToken()),
							Double.parseDouble(tokens.nextToken()),
							Double.parseDouble(tokens.nextToken()),
							Double.parseDouble(tokens.nextToken())));
			}
		}catch(Exception e){
			e.printStackTrace();
			throw new IOException();
		}
	}
	

	/**Saves the Receptor manager to the file provided.
	 *
	 * @param fout The file to write to.
	 * @throws IOException in the event that any save aspect fails.
	 */
	public void save(FileWriter fout)throws IOException{
		Iterator<Receptor> iter = receptors.iterator();
	
		while(iter.hasNext())
			fout.write(iter.next() + "\n");
		
		fout.flush();
		fout.close();
	}

	/**Adds a single receptor to the set.
	  @param p_receptor The receptor to add.
	*/  
	public void add(Receptor p_receptor){
		receptors.add(p_receptor);
	}

	/**Removes a receptor from the set.
	  @param p_receptor The receptor to attempt removal of
	  @throws ItemNotFoundException in the event that the given receptor is not present
	*/  
	public void remove(Receptor p_receptor) throws ItemNotFoundException{
		if(!receptors.remove(p_receptor))
			throw new ItemNotFoundException();
	}

	/**Returns the number of receptors in the set.
	  @return The number of receptors
	*/  
	public int count(){
		return receptors.size();
	}

	/**Returns an array with references to all receptors in the set, ordered by the order in which they were inserted.
	  @return An array of all receptors in the set.
	*/
	public Receptor[] getReceptors(){
		return (Receptor[]) receptors.toArray(new Receptor[receptors.size()]);
	}

	//public Receptor getReceptor(int p_index) throws java.lang.IndexOutOfBoundsException{
	//	return 
	//}

	//public void save(FineWriter fout){

	//}
}
