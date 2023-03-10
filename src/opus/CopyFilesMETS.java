package opus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Copy files from BagIt directory to METS directory
 * 
 * @author ogan
 *
 */
public class CopyFilesMETS {
	
	
	// Setting attribute to element
	public static void copyFiles(String dir, String id) throws IOException {
		
		// dirName
		String dirName = "\\content\\streams\\";			
		String sourceDirectory;
        String targetDirectory;
        String parentDirectory;
    
        if (id != null) {
    		sourceDirectory = "opus_resources\\" + dir + "\\bagits\\opus_" + id  + "\\data\\";
            targetDirectory = "opus_resources\\" + dir + "\\mets\\opus_" + id + dirName;
        	
	        Path path = Paths.get(sourceDirectory);
	        sourceDirectory = path.toAbsolutePath().toString();
	
	        File src[] = new File(sourceDirectory).listFiles();
	        
	        for (int i = 0; i < src.length; i++){                       
	        	
	            File dest = new File(targetDirectory + src[i].getName());  
	            dest.mkdirs();
	            try {
	                Files.copy(src[i].toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }                   
	        }
        }
        else {
        	parentDirectory = "opus_resources\\" + dir + "\\bagits";
	        Path path = Paths.get(parentDirectory);
	        parentDirectory = path.toAbsolutePath().toString();
	        
			int numberOfSubfolders = 0;
			File parentSrc[] = new File(parentDirectory).listFiles();
			
			for (int i = 0; i < parentSrc.length; i++) {
			    if (parentSrc[i].isDirectory()) {
			            numberOfSubfolders++;
			    }
			}		
			
			for (int k = 0; k < numberOfSubfolders; k++) {
				String opusId = parentSrc[k].getName();
				
				sourceDirectory = "opus_resources\\" + dir + "\\bagits\\" + opusId  + "\\data\\" ;
	            targetDirectory = "opus_resources\\" + dir + "\\mets\\" + opusId + dirName;
	            
	            Path sourcePath = Paths.get(sourceDirectory);
		        sourceDirectory = sourcePath.toAbsolutePath().toString();
		        
		        File src[] = new File(sourceDirectory).listFiles();
		        
		        for (int i = 0; i < src.length; i++){
		            
		        	File dest = new File(targetDirectory + src[i].getName());  
		            dest.mkdirs();
		            
		            try {
		                Files.copy(src[i].toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		            } catch (IOException e) {
		                e.printStackTrace();
		            }	        	
		        }            
			}
        	
        }
	}

}
