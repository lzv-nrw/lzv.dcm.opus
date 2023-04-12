package opus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Generate checksums for the downloaded files in /data
 * 
 * @author ogan
 *
 */
public class FileChecksum {
	
	public static void generateFileChecksum(String dir, String opusId, int fileCount, String selectedChecksum) throws Exception {
		
		String sourceDirectory = "opus_resources\\" + dir + "\\metadata";
		
		File inputFile = new File(sourceDirectory + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
	    
		String id = null;
				
		// Write content in File
	    for (int temp = 0; temp < nList.getLength(); temp++) {
	    	
	    	Node nNode = nList.item(temp);
		    Element idElement = (Element) nNode;
		    Element urlElement = (Element) nNode;
		    
	    	// Get OPUS identifier content
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {     	
	        	if (idElement.getElementsByTagName("identifier").item(0) != null){
	        		id = idElement.getElementsByTagName("identifier").item(0).getTextContent();
	        		id = StringCutter.cutFront(id, ":", 2);
	        	}
	        	// Call method writeCsInFile for one BagIt or all
	        	if (opusId != null && id.equals(opusId)) { 
	        		FileChecksum.writeCsInFile(urlElement, selectedChecksum, id, dir);
	        	} 
	        	else if (opusId == null) {
	        		FileChecksum.writeCsInFile(urlElement, selectedChecksum, id, dir);		        	
	        	}
	        }

	    }	
	}
	
	// Writer
	public static void writeCsInFile(Element urlElement, String selectedChecksum, String id, String dir) throws IOException, Exception {
		String filename = null;
		String selectedChecksumLow = selectedChecksum.toLowerCase();
		String bagitPath = "opus_resources\\" + dir + "\\bagits\\opus_" + id;
		    	
		// Check if dir exist
		File bagitTest = new File(bagitPath);
		if (bagitTest.exists()) {
			
			// Get number of files
			ArrayList<String> dataList = new ArrayList<String>();
			File dataDir[] = new File(bagitPath + "\\data\\").listFiles();
			
			// Write in File
	        FileWriter fileWriter = new FileWriter(bagitPath  + "\\manifest-" + selectedChecksumLow +".txt");
	        PrintWriter printWriter = new PrintWriter(fileWriter);
	
			for (int i = 0; i < dataDir.length; i++) {
				dataList.add(dataDir[i].getName());
				filename = dataDir[i].getName();
				String filePath = "\\opus_" + id + "\\data\\" + filename;
				
				//Create checksum for this file
				File file = new File(bagitPath + "\\data\\" + filename);
				 
				//Use selected algorithm
				MessageDigest sumDigest = MessageDigest.getInstance(selectedChecksum);
				 
				//Get the checksum
				String checksum = getFileChecksum(sumDigest, file);
				 		    			
		        // Write content in File
				String maniFestUrl = StringCutter.cutFront(filePath, "\\", 3);
		        printWriter.println(checksum + "  data/" + maniFestUrl);
			}
	        printWriter.close();
		}
	}


	// Generate file-checksum
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException{
		
	// Get file input stream for reading the file content
	FileInputStream fis = new FileInputStream(file);
	   
	// Create byte array to read data in chunks
	byte[] byteArray = new byte[1024];
	int bytesCount = 0; 
	    
	// Read file data and update in message digest
	while ((bytesCount = fis.read(byteArray)) != -1) {
		digest.update(byteArray, 0, bytesCount);
	};
	   
	fis.close();
	   
	// Get the hash's bytes
	byte[] bytes = digest.digest();
	   
	// Convert bytes[] from decimal to hexadecimal format
	StringBuilder sb = new StringBuilder();
	for(int i=0; i< bytes.length ;i++) {
		sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	}
	   
	//return complete hash
	return sb.toString();
	}
}
