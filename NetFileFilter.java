import javax.swing.filechooser.*;
import java.io.File;
/**Filters files for use with a FileChooser, in order to allow selection of neural nets only.

  @author Stephen Wattam
  @version 0.1
*/
public class NetFileFilter extends FileFilter{
	
	/**Returns true or false to indicate acceptance of the file.
		@param f The file to check
		@return true if the file given is acceptable input as a neural net, false if not
	*/
	public boolean accept(File f){
		if(f.isDirectory())
			return true;

		String ext = getExtension(f);

		if(ext!=null)
			if(ext.equals("net"))
				return true;
		
		return false;
	}

	/**Finds the extension of a file, is used in the accept method.
	  	@param f The file to return the extension of
		@return The extension of the file given
		@see #accept(File)
	*/
	private String getExtension(File f){
		String fileName = f.getName();

		int i=fileName.lastIndexOf(".");
		String extension = fileName.substring(i+1, fileName.length()).toLowerCase();
	
		//System.out.println("Extension is " + extension);
		return extension;
	}

	/**Returns the description that is displayed in the drop down type selector in the FileChooser prompt.
	 	@return The description, as penned by your esteemed javadoc author.
	*/
	public String getDescription(){
		return "Neural nets (.net)";
	}

}
