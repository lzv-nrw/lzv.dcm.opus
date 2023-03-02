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
 * Write Metadata in File "bag-info.txt"
 * 
 * @author ogan
 *
 */
public class BagInfo {
	
	public static void writeBagInfo(String opusId, int fileCount, String xmlPath, String bagitPath) throws Exception {
		
		File inputFile = new File(xmlPath + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
	    
		String id = null;
	    
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
	        	Element eElement = (Element) nNode;
	        	
	        	// Call method writeInFile for one BagIt or all
	        	if (opusId != null && id.equals(opusId)) { 
	        		BagInfo.writeInFile(urlElement, eElement, id, bagitPath);
	        	}
	        	else if (opusId == null) {
	        		BagInfo.writeInFile(urlElement, eElement, id, bagitPath);
	        	}
		    }
	    }
	}	
	
	// Get content and write in file
	public static void writeInFile(Element urlElement, Element eElement, String id, String bagitPath) throws IOException {
		
		// DC element-tags
		String contributor, coverage, creator, date, description, format, identifier, language, publisher, relation, rights, source, subject, title, type;
    	
    	if (urlElement.getElementsByTagName("dc:identifier").item(1) != null) {
    		FileWriter fileWriter = new FileWriter(bagitPath + "\\opus_" + id + "\\bag-info.txt");
            PrintWriter printWriter = new PrintWriter(fileWriter);
		    int l = 0;
        	while (eElement.getElementsByTagName("dc:title").item(l) != null){
        		title = eElement.getElementsByTagName("dc:title").item(l).getTextContent();
        		printWriter.println("Title: " + title);
        		l++; 
        	} l=0;
        	while (eElement.getElementsByTagName("dc:creator").item(l) != null){
        		creator = eElement.getElementsByTagName("dc:creator").item(l).getTextContent();
                printWriter.println("Creator: " + creator);
                l++;
        	} l=0;
        	while (eElement.getElementsByTagName("dc:subject").item(l) != null){
        		subject = eElement.getElementsByTagName("dc:subject").item(l).getTextContent();
                printWriter.println("Subject: " + subject);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:description").item(l) != null){
        		description = eElement.getElementsByTagName("dc:description").item(l).getTextContent();
                printWriter.println("Description: " + description);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:contributor").item(l) != null){
        		contributor = eElement.getElementsByTagName("dc:contributor").item(l).getTextContent();
                printWriter.println("Contributor: " + contributor);
                l++;
        	} l=0;
        	while (eElement.getElementsByTagName("dc:publisher").item(l) != null){
        		publisher = eElement.getElementsByTagName("dc:publisher").item(l).getTextContent();
                printWriter.println("Publisher: " + publisher);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:date").item(l) != null){
        		date = eElement.getElementsByTagName("dc:date").item(l).getTextContent();
                printWriter.println("Date: " + date);
                l++; 
        	} l=0;
        	while (eElement.getElementsByTagName("dc:type").item(l) != null){
        		type = eElement.getElementsByTagName("dc:type").item(l).getTextContent();
                printWriter.println("Type: " + type);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:format").item(l) != null){
        		format = eElement.getElementsByTagName("dc:format").item(l).getTextContent();
                printWriter.println("Format: " + format);
                l++; 
        	} l=0;
        	while (eElement.getElementsByTagName("dc:identifier").item(l) != null){
        		identifier = eElement.getElementsByTagName("dc:identifier").item(l).getTextContent().replace("%20", " ");;
                printWriter.println("Identifier: " + identifier);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:language").item(l) != null){
        		language = eElement.getElementsByTagName("dc:language").item(l).getTextContent();
                printWriter.println("Language: " + language);
                l++; 
        	} l=0;
        	while (eElement.getElementsByTagName("dc:rights").item(l) != null){
        		rights = eElement.getElementsByTagName("dc:rights").item(l).getTextContent();
                printWriter.println("Rights: " + rights);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:relation").item(l) != null){
        		relation = eElement.getElementsByTagName("dc:relation").item(l).getTextContent();
                printWriter.println("Relation: " + relation);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:source").item(l) != null){
        		source = eElement.getElementsByTagName("dc:source").item(l).getTextContent();
                printWriter.println("Source: " + source);
                l++;  
        	} l=0;
        	while (eElement.getElementsByTagName("dc:coverage").item(l) != null){
        		coverage = eElement.getElementsByTagName("dc:coverage").item(l).getTextContent();
                printWriter.println("Coverage: " + coverage);
                l++;  
        	} l=0;
            printWriter.close();
    	}
	}
}
