package opus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Write information in bagit.txt
 * 
 * @author ogan
 *
 */
public class BagitTxt {
	
	public static void bagitText(String dir, String opusId, String fileName, boolean greater) throws IOException, Exception {
		
		String sourceDirectory = "opus_resources\\" + dir + "\\metadata\\";	
		File inputFile = new File(sourceDirectory + fileName);
		
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
	        
	        	// Call method writeInFile for one BagIt or all
	        	if (opusId != null && id.equals(opusId) && greater == false) { 
	        		System.out.println("Check OPUS-ID " + id + " for bagit.txt");
		        	BagitTxt.writeInFile(urlElement, id, dir);
	        	} 
	        	else if (opusId != null && Integer.parseInt(id) >= Integer.parseInt(opusId) && greater == true) {
	        		System.out.println("Check OPUS-ID " + id + " for bagit.txt");
		        	BagitTxt.writeInFile(urlElement, id, dir);
	        	} 
	        	else if (opusId == null) {
	        		System.out.println("Check OPUS-ID " + id + " for bagit.txt");
	        		BagitTxt.writeInFile(urlElement, id, dir);		        	
	        	}
	        }
	    }
	}
	
	// Writer
	public static void writeInFile(Element urlElement, String id, String dir) throws IOException {
		if (urlElement.getElementsByTagName("dc:format").getLength() > 0) {
			String bagitPath = "opus_resources\\" + dir + "\\bagits\\opus_" + id;
	        FileWriter fileWriter = new FileWriter(bagitPath + "\\bagit.txt");
	        PrintWriter printWriter = new PrintWriter(fileWriter);
	        printWriter.println("BagIt-Version: 1.0");
	        printWriter.println("Tag-File-Character-Encoding: UTF-8");
	        printWriter.close();
		}
	}
}
