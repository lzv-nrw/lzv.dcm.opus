package opus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;

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
	
	public static void generateFileChecksum(String opusId, int fileCount, String selectedChecksum, String xmlPath, String bagitPath) throws Exception {
		
		File inputFile = new File(xmlPath + "\\opusMetaData_" + fileCount + ".xml");
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
	        		id = FileChecksum.cutFront(id, ":", 2);
	        	}            
	        	// Call method writeCsInFile for one BagIt or all
	        	if (opusId != null && id.equals(opusId)) { 
	        		FileChecksum.writeCsInFile(urlElement, selectedChecksum, id, bagitPath);
	        	} 
	        	else if (opusId == null) {
	        		FileChecksum.writeCsInFile(urlElement, selectedChecksum, id, bagitPath);		        	
	        	}
	        }

	    }	
	}
	
	// Writer
	public static void writeCsInFile(Element urlElement, String selectedChecksum, String id, String bagitPath) throws IOException, Exception {
	    String url = null;
		String filename = null;
		String selectedChecksumLow = selectedChecksum.toLowerCase();
		
		// Get content and write in file
    	
		// Get number of files
    	int n = 0;
    	while (urlElement.getElementsByTagName("dc:format").item(n) != null){
    		n++;
    	}
    	int m = 0;
    	while (urlElement.getElementsByTagName("dc:identifier").item(m) != null){
    		m++;
    	}
    	int k=m-n;
    	
    	// Get url content
    	if (urlElement.getElementsByTagName("dc:identifier").item(k) != null) {
    		url = urlElement.getElementsByTagName("dc:identifier").item(k).getTextContent();
    		filename = FileChecksum.cutFront(url, "/", 5).replace("%20", " ");
    		String filePath = "\\opus_" + id + "\\data\\" + filename;
			
			//Create checksum for this file
			File file = new File(bagitPath + "\\opus_" + id + "\\data\\" + filename);
			 
			//Use selected algorithm
			MessageDigest sumDigest = MessageDigest.getInstance(selectedChecksum);
			 
			//Get the checksum
			String checksum = getFileChecksum(sumDigest, file);
			 		    			
	        // Write content in File
			String maniFestUrl = FileChecksum.cutFront(filePath, "\\", 3);

	        FileWriter fileWriter = new FileWriter(bagitPath + "\\opus_" + id +"\\manifest-" + selectedChecksumLow +".txt");
	        PrintWriter printWriter = new PrintWriter(fileWriter);
	        printWriter.println(checksum + "  data/" + maniFestUrl);
	        k++;
	        while (urlElement.getElementsByTagName("dc:identifier").item(k) != null) {
        		url = urlElement.getElementsByTagName("dc:identifier").item(k).getTextContent();
        		filename = FileChecksum.cutFront(url, "/", 5);
        		filePath = bagitPath + "\\opus_" + id + "\\data\\" + filename;
        		
    			//Create checksum for this file
    			file = new File(bagitPath + "\\opus_" + id + "\\data\\" + filename);
    			 
    			//Use selected algorithm
    			sumDigest = MessageDigest.getInstance(selectedChecksum);
    			 
    			//Get the checksum
    			checksum = getFileChecksum(sumDigest, file);
    			 			    			
    	        // Write content in File
    			maniFestUrl = FileChecksum.cutFront(filePath, "\\", 3);
	        	printWriter.println(checksum + "  data/" + maniFestUrl);
	        	k++;
	        }
	        printWriter.close();
    	}
	}

	// The method cut a given String returns a partial string starting from a certain position of a separator sign.
	public static String cutFront(String text, String sign, int number) {
        for (int i = 0; i < number; i++) {
        	text = text.substring(text.indexOf(sign) + 1, text.length());
        }
        return text;
    }

	// Generate file-checksum
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
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
	  for(int i=0; i< bytes.length ;i++)
	  {
	    sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	  }
	   
	  //return complete hash
	   return sb.toString();
	}

}
