package NearOCR;
import NearNeural.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.Vector;
import java.util.Iterator;
import java.awt.Color;
/**Manages all OCR function.  This is done in an interface-agnostic way.
 
  @author Stephen Wattam
  @version 0.1
*/
public class OCRManager{
	/**The symbol table that this OCR run will use */
	private SymbolTable symbols = new SymbolTable();
	/**Retains a reference to the original file for use in save operations*/
	private File symbolFile;
	/**Retains a reference ot the document that is to be processed.*/
	private Document doc;
	/**Holds the net stack.*/
	private NetStack nets = new NetStack();
	/**Holds receptor matrix*/
	private ReceptorManager receptors = new ReceptorManager();
	
	/**Creates a new OCR manager, with default original values.*/
	public OCRManager(){
		try{
			//appendNet(new File("data/nets/MS_Sans_Serif/mssansserif.net"));
			loadImage(new File("data/sourceImages/TPSreport.png"));
		}catch(IOException IOe){
			IOe.printStackTrace();
			Logger.logError("Error creating new OCR manager");
		}
		Logger.logInfo("New OCR manager created successfully");
	}

	/**Returns the number of outputs in the final layer of the entire net stack.  
	  @return The number of outputs in the final layer of the net stack
	*/  
	public int countOutput(){
		return nets.countOutput();
	}

	/**Sets the receptor manager.
	  @param p_manager The ReceptorManager to use for analysis.
	*/
	public void setReceptorManager(ReceptorManager p_manager){
		receptors = p_manager;
	}	

	/**Returns the current ReceptorManager that will be used for analysis.
	  @return The current ReceptorManager
	*/
	public ReceptorManager getReceptorManager(){
		return receptors;
	}

	/**Removes a net from the internal list.
	 *
	 * @param p_index The index number of the net to remove
	 * @throws IndexOutOfBoundsException In the event that no net exists
	 */ 
	public void removeNet(int p_index)throws java.lang.IndexOutOfBoundsException, ItemNotFoundException, ValueOutOfBoundsException{
		removeNet(nets.getNet(p_index));
	}

	/**Returns all nets from the net stack, is merely a wrapper for the Net stack object.
	  @return All nets from the stack, in order, as an array
	*/  
	public NeuralNet[] getNets(){
		return nets.getNets();
	}

	/**Returns a net from the given index number.
	  @return The net at the index given in the net stack
	*/
	public NeuralNet getNet(int p_index) throws java.lang.IndexOutOfBoundsException{
		return nets.getNet(p_index);
	}

	/**Returns the number of inputs as shared across the net stack.
	  @return The number of inputs
	*/  
	public int countInputs(){
		return nets.getInputCount();
	}

	/**Moves a net in the stack.
	  @param p_indexFrom The index of the net to move
	  @param p_indexTo The index where the net will be placed
	*/  
	public void moveNet(int p_indexFrom, int p_indexTo){
		nets.moveNet(p_indexFrom, p_indexTo);
	}

	/**Removes a net from the stack.
	  @param net The neural net to remove from the net stack.
	*/  
	public void removeNet(NeuralNet net) throws ItemNotFoundException, ValueOutOfBoundsException{
		nets.removeNet(net);
		symbols.setLength(countOutput());
		Logger.logInfo("Net removed. " + nets.countNets() + " currently loaded.");
	}

	/*Loads a net from a given net file.  Nets must be of the correct version.
	 
	  @param f The file from which to load the net
	  @throws IOException in the event that the net cannot be loaded
	  @throws ValueOutOfBoundsException if the number of input neurons is incompatible with the other nets
	*/
/*	public void appendNet(File f)throws IOException, ValueOutOfBoundsException{
		NeuralNet tempNet = new NeuralNet(new FileReader(f), new SigmoidThresholdModel(1), new ThreadedUnweightedBackPropagationMethod(0.0));
		appendNet(tempNet);
		Logger.logInfo("Filepath of net: " + f.getPath() + ".");
	}*/

	/**Appends a net to the stack.
	  @param net The net to append to the net stack.
	  @throws ValueOutOfBoundsException in the event that the number of inputs does not match that of the other loaded nets.
	*/  
	public void appendNet(NeuralNet net)throws ValueOutOfBoundsException{
		nets.appendNet(net);
		
		symbols.setLength(countOutput());
		
		Logger.logInfo("Net loaded, " + nets.countNets() + " now loaded.");
	}


	/**Adds a symbol to the symbol table.  The order of the symbol table is technically an irrelevance, as each symbol retains its own weighted edges which are automatically adjusted to fit the current net.
	 * @param p_symbol The symbol to add to the currently loaded symbol table
	 */ 
	public void addSymbol(Symbol p_symbol){
		symbols.add(p_symbol);
		p_symbol.setLength(countOutput());
	}

	/**Loads a symbol table from the given file.
	 *
	 * @param f The file to load a symbol table from.
	 * @throws IOException In the event that any error occurs during loading
	 */
	public void loadSymbols(File f) throws IOException{
		symbols = new SymbolTable(new FileReader(f));
		symbolFile = f;	//keep a reference for save ops
	}

	/**Returns the entire current sumbol table.
	 * @return The currently loaded symbol table.
	 */ 
	public SymbolTable getSymbolTable(){
		return symbols;
	}

	/**Returns the document that this OCRManager is using.
	 *
	 * @return The document that this OCR manager is based on.
	 */
	public Document getDocument(){
		return doc;
	}

	/**Sets the document to parse.
	 *
	 * @param p_doc The document to set as the current document in this OCR manager.
	 */
	public void setDocument(Document p_doc){
		doc=p_doc;
		Logger.logInfo("New document loaded");
	}
	
	/**Sets the current document's image source.
	 *
	 * @param p_file The image file to read into the document.
	 */
	public void loadImage(File p_file) throws IOException{
		doc = new Document(ImageIO.read(p_file));
		Logger.logInfo("New image loaded into current document, letters deleted.");
	}
	

	/**Analyses all letters by running them through all nets.  The return type is void because all output gets written back into the individual Letters' value fields.

		@param listener A ProgressListener that is to watch the progress of the operation, which could take some time.
	*/	
	public void analyse(double covarianceWeight, double subtractionWeight, ProgressListener listener){
		Letter[] letters = doc.getLetters();

		for(int i=0;i<letters.length;i++){
			listener.valueChanged(i, letters.length);
			RunManager.runOneImg(nets.getNets(), letters[i], symbols, listener, covarianceWeight, subtractionWeight, receptors); 
		}
		listener.valueChanged(0,1);
	}

	/**Resets all symbol weights to the given number.

	  @param p_weight The number to set all weights to
	*/
	public void setAllWeights(double p_weight){
		Symbol[] symbols = this.symbols.getSymbols();
		
		for(Symbol s:symbols){
			WeightedNode[] nodes = s.getWeights();
			for(WeightedNode w:nodes)
				w.setWeight(p_weight);
		}


	}

	/**Analyses and updates all symbol weights to compensate for individual net idiosynchrasies.
		@param listener A ProgressListener that is to watch the progress of the operation, which could take some time.
	*/  
	public void analyseSymbolWeights(ProgressListener listener){
		Symbol[] symbols = this.symbols.getSymbols();

		for(int i=0;i<symbols.length;i++){
			listener.valueChanged(i, symbols.length);
			TrainingManager.adjustSymbolWeights(nets.getNets(), symbols[i], listener, receptors);
		}

		listener.valueChanged(0,1);
	}

	/**Returns an array of doubles which represent the averages in the horizontal direction.  These values are normalised to be between 0(white) and 1(black)
	 *
	 * @param sourceImage The image to parse
	 * @return All averages of horizontal lines in the given source image
	 */ 
   	private double[] meanHorizLines(BufferedImage sourceImage){
		double averages[] = new double[sourceImage.getHeight()];
		
		int j;
		double max = 0;
		for(int i=0;i<averages.length;i++){
			averages[i] = 0;
			for(j=0;j<sourceImage.getWidth();j++)
				averages[i] -= sourceImage.getRGB(j,i);
			averages[i] /= j;
			if(averages[i] > max){max=averages[i];}
		}

		for(int i=0;i<averages.length;i++)
			averages[i] /= max;
		
		return averages;
	}
   
	/**Returns an array of doubles, from 0 to 1, representing average RGB values in the vertical direction.
	 * @param sourceImage The image to process
	 * @return The average column values
	 */
	private double[] meanVertLines(BufferedImage sourceImage){
		double averages[] = new double[sourceImage.getWidth()];
		
		int j;
		double max = 0;
		for(int i=0;i<averages.length;i++){
			averages[i] = 0;
			for(j=0;j<sourceImage.getHeight();j++)
				averages[i] -= sourceImage.getRGB(i,j);
			averages[i] /= j;
			if(averages[i] > max){max=averages[i];}
		}

		for(int i=0;i<averages.length;i++)
			averages[i] /= max;
		
		return averages;
	}

	/**Attempts to find letters by dividing an image up by lines and letters.  This populates letters in the current document, which can be retrieved using other methods or straight from the document.
	 * @param p_thresholdX The X-direction threshold for cutting up images (switches the tracer on/off based on averages values
	 * @param p_thresholdY The Y-direction threshold for cutting images up
	 */
	public void slice(double p_thresholdX, double p_thresholdY){
		BufferedImage sourceImage = doc.getImage();
		doc.clearLetters();		
		
		double[] averagesY = meanHorizLines(sourceImage);
		double[] averagesX;// = meanHorizLines(sourceImage);


		boolean toggleY = false;
		boolean toggleX = false;
		int lastY = 0;
		int lastX = 0;
		for(int i=0;i<averagesY.length;i++){
			if(toggleY && (i == averagesY.length || averagesY[i] < p_thresholdY) && (i - lastY) > 0){
				BufferedImage tempImage = sourceImage.getSubimage(0,lastY,sourceImage.getWidth(),i-lastY);
				averagesX = meanVertLines(tempImage);
				for(int j=0;j<averagesX.length;j++){
					//System.out.println("X: " + toggleX + " Y: " + toggleY + " avX: " + averagesX[j] + " thX: " + p_thresholdX + " avY: " + averagesY[i] + " thY: " + p_thresholdY);
					if(toggleX && (j == averagesX.length || averagesX[j] < p_thresholdX) && (j - lastX) > 0){
						//create a new letter and assign it default values for now
						Letter tempLetter = new Letter(tempImage.getSubimage(lastX,0,j-lastX, tempImage.getHeight()));
						tempLetter.setX(lastX);
						tempLetter.setY(lastY);

						//System.out.println(tempLetter.getX() + ":" + tempLetter.getY() + ":" + tempLetter.getWidth() + ":" + tempLetter.getHeight());
						doc.addLetter(tempLetter);

						//System.out.print("=");
						toggleX = false;
					}

					if(!toggleX && averagesX[j] > p_thresholdX){
						lastX = j;
						//System.out.print("_");
						toggleX = true;
					}
					
				}
				//System.out.print("-\n");
				toggleY = false;
			}
			toggleX = false;


			if(!toggleY && averagesY[i] > p_thresholdY){
				lastY = i;
				//System.out.print("+");
				toggleY = true;
			}
		}
		Logger.logInfo("Letters sliced out of document: " + doc.getLetters().length);
		//System.out.print("\n\n\n\n");
	}
/*	WORK ON THIS LAYER
	
	public void saveOCRSession(String filename) throws IOException{
		symbols.save(new FileWriter(new File(filename + "symbols.syb")));
		net.save(new FileWriter(new File(filename + "net.net")));

		fout = new FileWriter(new File(filename + "OCRsession.ocr"));
	
		
	}*/
}
