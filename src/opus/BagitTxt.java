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
	
	public static void bagitText(String opusId, int fileCount, String xmlPath, String bagitPath) throws IOException, Exception {
		
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
	        		id = DownloadFile.cutFront(id, ":", 2);
	        	}            
	        
	        	// Call method writeInFile for one BagIt or all
	        	if (opusId != null && id.equals(opusId)) { 
		        	BagitTxt.writeInFile(urlElement, id, bagitPath);
	        	} 
	        	else if (opusId == null) {
	        		BagitTxt.writeInFile(urlElement, id, bagitPath);		        	
	        	}
	        }
	    }
	}
	
	// Writer
	public static void writeInFile(Element urlElement, String id, String bagitPath) throws IOException {
		if (urlElement.getElementsByTagName("dc:identifier").item(1) != null) {
	        FileWriter fileWriter = new FileWriter(bagitPath + "\\opus_" + id + "\\bagit.txt");
	        PrintWriter printWriter = new PrintWriter(fileWriter);
	        printWriter.println("BagIt-Version: 1.0");
	        printWriter.println("Tag-File-Character-Encoding: UTF-8");
	        printWriter.close();
		}
	}
}
