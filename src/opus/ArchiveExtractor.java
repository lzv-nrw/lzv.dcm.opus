package opus;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * Extracts files from an archive (zip&tgz)
 * and compares the files with the existing ones.  
 * 
 * @author ogan
 *
 */
public class ArchiveExtractor {
	
	public void extractor(String parentPath ) throws Exception {
		// Create temporary folders for extracted files
		String destDirZip = parentPath + "tmp_zip/";
		String destDirTgz = parentPath + "tmp_tgz/";
		
		// List files in source folder
		ArrayList<String> sourceList = new ArrayList<String>();		
		File parentSrc[] = new File(parentPath).listFiles();		
		for (int i = 0; i < parentSrc.length; i++) {
			sourceList.add(parentSrc[i].getName());
		}
		
		// Extract file if it is a zip or tgz
		ArchiveExtractor ae = new ArchiveExtractor();	
		for (int i = 0; i < sourceList.size() ; i++) {
			String ext = FilenameUtils.getExtension(sourceList.get(i));
			String filePath = parentPath + sourceList.get(i);
			if (ext.equals("zip")) {
				ae.unzip(parentPath, filePath, destDirZip);
			}
			else if (ext.equals("tgz")) {
				ae.untar(parentPath, filePath, destDirTgz);
			}
		}
	}
	
    /**
     * Extract tgz files
     * 
     * @param parentPath
     * @param sourcePath
     * @param destDir
     * @throws Exception
     */
    public void untar(String parentPath, String sourcePath, String destDir) throws Exception {
    	final int BUFFER = 2048;
    	FileInputStream fis = new FileInputStream(sourcePath);
		BufferedInputStream in = new BufferedInputStream(fis);
		GzipCompressorInputStream gzIn = new GzipCompressorInputStream(in);
		TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);
		
		TarArchiveEntry entry = null;
		File extrFile = new File(destDir);
		extrFile.mkdirs();
		
		// Read the tar entries
		while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {		
			String name = entry.getName();
			File fileNew = new File(destDir + name);
			
			// Check if entry is a folder and create folder
			if (name.endsWith("/")) {
				fileNew.mkdirs();
				continue;
			}

			FileOutputStream fos = new FileOutputStream(fileNew);
			BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
			int length;
			byte bytes[] = new byte[BUFFER];
			while ((length = tarIn.read(bytes, 0, BUFFER)) != -1) {
				bos.write(bytes, 0, length);
			}
			bos.close();
			
			// Compare files
            File test = new File(parentPath + name);
            boolean exists = test.exists();
            if (exists) {
            	ArchiveExtractor fC = new ArchiveExtractor();
            	fC.fileComparator(test, fileNew);
            }
		}

		// Close the input stream
		tarIn.close();
		
		// Delete folders/files
        if(extrFile.listFiles().length == 0) {
            FileUtils.deleteDirectory(extrFile);
        	File tgz = new File(sourcePath);
        	tgz.delete();    	
        }
        else if (extrFile.listFiles().length != 0) {
            FileUtils.deleteDirectory(extrFile);
        }       
    }
	
	/**
	 * Extract zip files
	 * 
	 * @param parentPath
	 * @param sourcePath
	 * @param destDir
	 * @throws Exception
	 */
    public void unzip(String parentPath, String sourcePath, String destDir) throws Exception {
    	final int BUFFER = 2048;
    	File extrFile = new File(destDir);  	
    	extrFile.mkdirs();   	
    	ZipFile zipFile = new ZipFile(sourcePath);
    	Enumeration<?> enu = zipFile.entries();
    	
		// Read the zip entries
        while (enu.hasMoreElements()) {        	
        	ZipEntry zipEntry = (ZipEntry) enu.nextElement();
        	String name = zipEntry.getName();
        	
			// Check if entry is a folder and create folder
        	File fileNew = new File(destDir + name);
            if (name.endsWith("/")) {
            	fileNew.mkdirs();
                continue;
            }
                       
            InputStream is = zipFile.getInputStream(zipEntry);
            FileOutputStream fos = new FileOutputStream(fileNew);
            int length;
            byte[] bytes = new byte[BUFFER];
            while ((length = is.read(bytes)) >= 0) {
                fos.write(bytes, 0, length);
            }
            // Close streams
            is.close();
            fos.close();
            
            // Compare files
            File test = new File(parentPath + name);
            boolean exists = test.exists();
            if (exists) {
            	ArchiveExtractor fC = new ArchiveExtractor();
            	fC.fileComparator(test, fileNew);
            }
        }
		// Close the input stream
        zipFile.close();
        
		// Delete folders/files
        if(extrFile.listFiles().length == 0) {
            FileUtils.deleteDirectory(extrFile);
        	File zip = new File(sourcePath);
        	zip.delete();    	
        }
        else if (extrFile.listFiles().length != 0) {
            FileUtils.deleteDirectory(extrFile);
        }  
    }
	
    /**
     *  Compare two files
     * 
     * @param fileParent
     * @param fileNew
     * @throws Exception
     */
    public void fileComparator(File fileParent, File fileNew) throws Exception {
    	String fileName = fileParent.getName();
		boolean isTwoEqual = FileUtils.contentEquals(fileParent, fileNew);
		if (isTwoEqual) {
			fileNew.delete();
		}
		else {
			String ext = FilenameUtils.getExtension(fileName);
			String name = fileParent.getName().replaceAll("." + ext, "");
			
            try {	            
            	String newFilePath = fileParent.getAbsolutePath().replace(fileNew.getName(), name + "_extracted." + ext);
            	File newFile = new File(newFilePath);
            	if(!newFile.exists()) {
            	FileUtils.moveFile(fileNew, newFile);
            	}
            	else {
            		fileNew.delete();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
		}
    }
}
