package NearOCR;
import java.awt.image.BufferedImage;
import java.awt.*;
import NearNeural.*;
import java.util.*;
import java.io.*;
/**The RunManager is responsible for all OCR running operations which actually recognise characters rather than merely adjusting weight values.  

  @author Stephen Wattam
  @version 0.1
*/  
public abstract class RunManager{
	/**The target 'true' value for neurons, ie the maximum allowable output number for normalisation purposes.*/
	static private final double TRUE_VAL = 0.9999;
	/**The minimum possible output number ofr normalisation.*/
	static private final double FALSE_VAL = -0.9999;//0.0001;
	
	/**Loops through all of the nets, runs them and add it to a list of doubles, then returns the best fit symbol for those outputs.

	  @param nets The neural net stack to run through, ordered
	  @param image The CharacterImage to run through this recognition algorithm
	  @param symbols The symbol table with which to compare the image given
	  @param listener A ProgressListener to monitor this operation
	  @return The symbols member best correlating with the image given
	*/
	public static Symbol runOneImg(NeuralNet[] nets, CharacterImage image, SymbolTable symbols, ProgressListener listener,double covarianceWeight, double subtractionWeight, ReceptorManager receptorManager){
		int outputCount = 0;
		for(int i=0;i<nets.length;i++)
			outputCount += nets[i].getLayerStructure()[nets[i].getLayerStructure().length-1];

		double[] outputs = new double[outputCount];
		
		int offset = 0;
		for(int i=0;i<nets.length;i++){
			double[] tempOutputs = run(nets[i], image, listener, receptorManager);

			for(int j=0;j<tempOutputs.length;j++){
				outputs[j+offset] = tempOutputs[j];
			}
			
			offset += tempOutputs.length;
		}
		
		Symbol bestSymbol = findBestChar(symbols, image, outputs, covarianceWeight, subtractionWeight, listener);
		image.setValue(bestSymbol.getValue());


		return bestSymbol;
	}

	/**Subtracts all image values from each other and returns the absolute mean deviation between the two.
	   @param i1 The image from which i2 is subtracted
	   @param i2 The image to subtract from i1
	   @return The mean deviation, from 0 to 1 inclusive
	 */
	private static double subtractImages(BufferedImage i1, BufferedImage i2){
		//make them the best fit for sizes.
		int commonWidth = ((i1.getWidth() + i2.getWidth()) / 2);
		int commonHeight = ((i1.getHeight() + i2.getHeight()) / 2);

		//ensure they are the same size
		i1 = scale(commonWidth, commonHeight, i1);
		i2 = scale(commonWidth, commonHeight, i2);

		double[] i1Data = parseImageRaw(i1);
		double[] i2Data = parseImageRaw(i2);

		if(i1Data.length != i2Data.length){
			System.out.println("DEBUG: lengths in subtract");
			return 0;
		}
		
		double sum = 0;
		double max = 0;
		for(int i=0;i<i1Data.length;i++){
			double val = Math.abs(i1Data[i] - i2Data[i]);
			sum += val;
			if(max < val)
				max = val;
		}
		

		return (sum/i1Data.length)/max;
	}

	/**Calculates the correlation coefficient for two CharacterImages.
	  @param x1 The CharacterImage to compare to x2 
	  @param x2 The CharacterImage to compare to x1
	  @return The correlation coeffieicnt, from -1 to 1.
	*/
	private static double calculateCorrelationCoefficient(BufferedImage x1, BufferedImage x2){
		//make them the best fit for sizes.
		int commonWidth = ((x1.getWidth() + x2.getWidth()) / 2);
		int commonHeight = ((x1.getHeight() + x2.getHeight()) / 2);

		//ensure they are the same size
		x1 = scale(commonWidth, commonHeight, x1);
		x2 = scale(commonWidth, commonHeight, x2);

		double[] x1Data = parseImageRaw(x1);
		double[] x2Data = parseImageRaw(x2);
		
		double x1Mean = getMean(x1Data);
		double x2Mean = getMean(x2Data);

		double x1StandardDeviation = getStandardDeviation(x1Mean, x1Data);
		double x2StandardDeviation = getStandardDeviation(x2Mean, x2Data);

		return (getMeanCovariance(x1Mean, x1Data, x2Mean, x2Data)) / (x1StandardDeviation * x2StandardDeviation);
	}

	/**Scales an image using a rather circuitous scaling algorithm that is designed primarily to retain pixel counts and emulate easily-generated training data.  
	  
	  <p>It is imperative that letters are scaled to set their pixel counts as the input neuron count does not change.  The specific way in which training data is serialised is clearly a factor when it comes to recognition, as unlike a receptor-based net a shift in character position will cause a dramatic loss in quality.</p>

	  <p>The scaling algorithm in this method scales the original image whilst retaining aspect ratio.  The length of the side deemed closest in proportions to its corresponding desired value is the one which is used as a guage to scale the rest, meaning that the image always fits within the chosen area.  The scaled image is then stuffed uncerimoniously into the top corner of the chosen image, matching the training data that I have been using (and enforcing a convention others must train using).</p>

	  Output is placed in the scaledImage object for later access without recalculation.

	  @param p_width The desired width of the output
	  @param p_height The desired height of the output
	*/
	private static BufferedImage scale(int p_width, int p_height, BufferedImage originalImage){
		//calculate longest side, adjust with same aspect ratio
		double scaleFactor;
		if(((double)p_width/(double)originalImage.getWidth()) > ((double)p_height/(double)originalImage.getHeight())){
			//resize by ratio of widths
			scaleFactor = ((double)p_height/(double)originalImage.getHeight());
		}else{
			//resize by ratio of heights
			scaleFactor = ((double)p_width/(double)originalImage.getWidth());
		}
		//System.out.println("W:" + originalImage.getWidth() + "\t H:" + originalImage.getHeight() + "\t p_w:" + p_width + "\t p_h:" + p_height + "\t SF:" + scaleFactor);

		Image prelimScaledImage = originalImage.getScaledInstance(	(int)Math.ceil(((double)originalImage.getWidth()*scaleFactor)),
										(int)Math.ceil(((double)originalImage.getHeight()*scaleFactor)),
										Image.SCALE_SMOOTH);
										//Image.SCALE_FAST
										//Image.SCALE_DEFAULT
										//Image.SCALE_AREA_AVERAGING
			
			//new BufferedImage(p_width, p_height, originalImage.TYPE_INT_RGB);
		BufferedImage scaledImage = new BufferedImage(p_width, p_height, originalImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D)scaledImage.createGraphics();

		//set bg colour
		g2d.setColor(Color.WHITE);	//this colour is irrelevant now, as it is the same on both
		g2d.fillRect(0,0,p_width, p_height);

		g2d.drawImage(prelimScaledImage,0,0,null);

		//draw image onto image
		//g2d.scale((double)originalImage.getWidth()*scaleFactor,(double)originalImage.getHeight()*scaleFactor);
		
		return scaledImage;
	}

	/**Returns an image as a one-dimensional array of doubles without normalisation.  This is used in covariant and subtractive comparison.
 		@param sourceImage The image to parse
      		@return An array of doubles, sourceImage.getWidth() * sourceImage.getHeight() in length.
	*/		
	private static double[] parseImageRaw(BufferedImage sourceImage){
		//get attributes	
		int sourceHeight = sourceImage.getHeight();
		int sourceWidth = sourceImage.getWidth();

		double[] imageSourceData = new double[(sourceHeight*sourceWidth)];
		double maxValue = 0;
		for(int i=0; i<sourceHeight ; i++){
			for(int j=0;j<sourceWidth;j++){
				imageSourceData[(i*sourceWidth)+j] = -sourceImage.getRGB(j,i);	//assign RGB vals to the array, keep track of max number
		//		if(imageSourceData[(i*sourceWidth)+j] > maxValue)
		//			maxValue = imageSourceData[(i*sourceWidth)+j]; 	
			}
		}

		//normalise
		//for(int i=0;i<imageSourceData.length;i++){
		//	imageSourceData[i] = ((imageSourceData[i]/maxValue)*(TRUE_VAL-FALSE_VAL))+FALSE_VAL;
		//	System.out.println(imageSourceData[i]);
		//}

		return imageSourceData;
	}
	

	/**Calculates mean value from a covariance matrix.
	  @param mean1 The mean of the first set of data
	  @param inputs1 The first set of data of which mean1 is mean
	  @param mean2 The mean of the second set
	  @param inputs2 The second set of data
	  @return The mean covariance
	*/  
	private static double getMeanCovariance(double mean1, double[] inputs1, double mean2, double[] inputs2){
		if(inputs1.length != inputs2.length)
			System.out.println("DEBUG: Lengths do not match");

		double sum = 0;
		for(int i=0;i<inputs1.length;i++)
			sum += (inputs1[i] - mean1) * (inputs2[i] - mean2);
		
		return sum/inputs1.length;
	}

	/**Returns the standard deviation of a set of data.
	  @param mean The mean value of the data being passed
	  @param inputs The data to calculate the standard deviation of
	  @return The standard deviation of the set 
	 */
	private static double getStandardDeviation(double mean, double[] inputs){
		double standardDeviation = 0;
		double sum = 0;

		for(int i=0;i<inputs.length;i++)
			sum += Math.pow((inputs[i] - mean), 2);
		
		sum /= inputs.length;	
		//sum == variance here

		return Math.sqrt(sum);
	}

	/**Returns the mean of a set of inputs.
	  @param inputs The input values
	  @return The mean of all input numbers
	*/
	private static double getMean(double[] inputs){
		double sum = 0;

		for(int i=0;i<inputs.length;i++)
 			sum += inputs[i];
		
		return	sum/inputs.length;
	}

	/**Returns the best fit of characters from within a given symbol table when provided with raw net outputs.

	  @param table The symbol table containing all possible symbols
	  @param outputs The outputs of the whole net stack, must match in length to the number of weighted nodes per symbol
	  @param listener The ProgressListener which is to monitor this operation
	  @return The table member that best fits the outputs given
	*/
	private static Symbol findBestChar(SymbolTable table, CharacterImage image, double[] outputs, double covarianceWeight, double subtractionWeight, ProgressListener listener){
		Symbol[] symbols = table.getSymbols();


		
		//find best char from net output
		int maxIndex = 0;
		double currentDev = 0;
		double[] tempOutputs = new double[table.getSymbols().length];
		for(int i=0;i<symbols.length;i++){
			//deviation from net output
			tempOutputs[i] = symbols[i].getMeanDeviation(outputs);	//add error
			//deviation from subtractive mixing
			tempOutputs[i] += (subtractionWeight * subtractImages(symbols[i].getImage(), image.getImage())); // weight and add error
			//deviation from covariance
			tempOutputs[i] += (covarianceWeight * -(calculateCorrelationCoefficient(symbols[i].getImage(), image.getImage()) - 1));	//adjust for 0 to 2 output, 0 being bad, then weight and add

			//find lowest deviation
			if(tempOutputs[i] < tempOutputs[maxIndex])	//find lowest error
				maxIndex = i;
		}

		listener.eventOccurred("Best is deviant by " + tempOutputs[maxIndex] + "/" + (4-(4-(subtractionWeight + covarianceWeight))) + ", value :" + symbols[maxIndex].getValue());
		
		return symbols[maxIndex];
	}

	/**Runs a single image through all nets, returning the raw net output.

	  @param nets The whole net stack, ordered
	  @param image The CharacterImage to run through all nets
	  @param listener The ProgressListener that is to monitor the operation
	  @return All net outputs, in order of the nets originally passed to the routine
	*/
	public static double[] run(NeuralNet[] nets, CharacterImage image, ProgressListener listener, ReceptorManager receptorManager){
		int outputCount = 0;
		for(int i=0;i<nets.length;i++)
			outputCount += nets[i].getLayerStructure()[nets[i].getLayerStructure().length-1];

		
		double[] outputs = new double[outputCount];
		int offset = 0;
		for(NeuralNet net: nets){
			double[] tempOutputs = run(net, image, listener, receptorManager);

			for(int j=0;j<tempOutputs.length;j++)
				outputs[j+offset] = tempOutputs[j];	
			
			offset += tempOutputs.length;
		}
		
		return outputs;
	}

	/**Runs a symbol through a single net, returning raw net output as an array of doubles.

		@param net The neural net to run
		@param image The CharacterImage that is to be run through the given net
		@param listener The ProgressListener That is to monitor this operation
		@return An array of doubles representing the exact raw output of the given net
	*/
	private static double[] run(NeuralNet net, CharacterImage image, ProgressListener listener, ReceptorManager receptorManager){
		double[] imageSourceData = parseImage(image.getImage(), receptorManager.getReceptors());
	
		try{	
			net.setInputValues(imageSourceData);
			return net.getOutput();
		}catch(Exception e){
		}
		return net.getOutput();
	}
	

	/**Converts a BufferedImage into an array of doubles, normalised to fit within TRUE_VAL and FALSE_VAL.
	  @param sourceImage The image to parse.
	  @return An array of doubles representing the normalised raw RGB values of sourceImage
	*/
	static private double[] parseImage(BufferedImage sourceImage, Receptor[] receptors){
		//get attributes	
		int sourceHeight = sourceImage.getHeight();
		int sourceWidth = sourceImage.getWidth();

		double[] receptorValueData = new double[receptors.length];
		double maxValue = 0;

		
		for(int k=0;k<receptors.length;k++){
			double a_X1 = receptors[k].getX1()*sourceWidth;
			double a_X2 = receptors[k].getX2()*sourceWidth;
			double a_Y1 = receptors[k].getY1()*sourceHeight;
			double a_Y2 = receptors[k].getY2()*sourceHeight;
			//System.out.println(a_X1 + "--" + a_Y1 + "--" + a_X2 + "--" + a_Y2);
			
			//if step size is 1 then they will hit most pixels, but more is needed for greatest accuracy.
			int steps = (int)(2.0*(Math.hypot((a_X2-a_X1),(a_Y2-a_Y1))))+1;	//double resolution
			//System.out.println(steps);
			
			//left, right, up and down must be done.  Fuckbadgers.
			
			for(int i=0;i<steps;i++){
				receptorValueData[k] += -sourceImage.getRGB(	(int)Math.floor((a_X1 + i*((a_X2-a_X1)/steps))), 
										(int)Math.floor((a_Y1 + i*((a_Y2-a_Y1)/steps))));
			}

			//average hit for the whole thing
			//System.out.println(receptorValueData[k] + "~~~" + (receptorValueData[k] / steps));
			if(receptorValueData[k] != 0)
				receptorValueData[k] = receptorValueData[k] / steps;	

			//System.out.println(receptorValueData[k]);

			if(receptorValueData[k] > maxValue)
				maxValue = receptorValueData[k]; 	
		}
	
		//normalise
		for(int i=0;i<receptorValueData.length;i++){
			receptorValueData[i] = ((receptorValueData[i]/maxValue)*(TRUE_VAL-FALSE_VAL))+FALSE_VAL;
		//	System.out.println(imageSourceData[i]);
		}
		
		return receptorValueData;
		
	/*	//get attributes	
		int sourceHeight = sourceImage.getHeight();
		int sourceWidth = sourceImage.getWidth();

		double[] imageSourceData = new double[(sourceHeight*sourceWidth)];
		double maxValue = 0;
		for(int i=0; i<sourceHeight ; i++){
			for(int j=0;j<sourceWidth;j++){
				imageSourceData[(i*sourceWidth)+j] = -sourceImage.getRGB(j,i);	//assign RGB vals to the array, keep track of max number
				if(imageSourceData[(i*sourceWidth)+j] > maxValue)
					maxValue = imageSourceData[(i*sourceWidth)+j]; 	
			}
		}

		//normalise
		for(int i=0;i<imageSourceData.length;i++){
			imageSourceData[i] = ((imageSourceData[i]/maxValue)*(TRUE_VAL-FALSE_VAL))+FALSE_VAL;
		//	System.out.println(imageSourceData[i]);
		}

		return imageSourceData;
	*/
	}
}
