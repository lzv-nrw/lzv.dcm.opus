package opus;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BagInfo {
	
	public static void writeBagInfoOne(String opusId, int fileCount, String xmlPath, String bagitPath) throws Exception {
		
		File inputFile = new File(xmlPath + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
	    
		String id = null;
	    String title, creator, description, date, format, subject, type, language, rights, identifier;
	    
	    for (int temp = 0; temp < nList.getLength(); temp++) {
	    	
	    	Node nNode = nList.item(temp);
		    Element idElement = (Element) nNode;
		    Element urlElement = (Element) nNode;
		    
		    int l = 0;		    
	    	
	    	// Get OPUS identifier content
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        	
	        	if (idElement.getElementsByTagName("identifier").item(0) != null){
	        		id = idElement.getElementsByTagName("identifier").item(0).getTextContent();
	        		id = DownloadFile.cutFront(id, ":", 2);
	        	}        
	        	
	        	if (id.equals(opusId)) { 
		        	
	        		// Get content and write in file
		        	Element eElement = (Element) nNode;
		        	
		        	if (urlElement.getElementsByTagName("dc:identifier").item(1) != null) {
		        		FileWriter fileWriter = new FileWriter(bagitPath + "\\opus_" + id + "\\bag-info.txt");
			            PrintWriter printWriter = new PrintWriter(fileWriter);
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
			        		identifier = eElement.getElementsByTagName("dc:identifier").item(l).getTextContent();
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
			            printWriter.close();
		        	}
	        	}
	        }
	    	
	    }

	}	
	
	
	public static void writeBagInfoAll(int fileCount, String xmlPath, String bagitPath) throws Exception {
		
		File inputFile = new File(xmlPath + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
	    
		String id = null;
		String title, creator, description, date, format, subject, type, language, rights, identifier;
	    
	    for (int temp = 0; temp < nList.getLength(); temp++) {
	    	
	    	Node nNode = nList.item(temp);
		    Element idElement = (Element) nNode;
		    Element urlElement = (Element) nNode;
		    
		    int l = 0;    
	    	
	    	// Get OPUS identifier content
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        	
	        	if (idElement.getElementsByTagName("identifier").item(0) != null){
	        		id = idElement.getElementsByTagName("identifier").item(0).getTextContent();
	        		id = DownloadFile.cutFront(id, ":", 2);
	        	}            
	        
	        	// Get content and write in file

	        	Element eElement = (Element) nNode;
	        	
	        	if (urlElement.getElementsByTagName("dc:identifier").item(1) != null) {
	        		FileWriter fileWriter = new FileWriter(bagitPath + "\\opus_" + id + "\\bag-info.txt");
		            PrintWriter printWriter = new PrintWriter(fileWriter);
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
		        		identifier = eElement.getElementsByTagName("dc:identifier").item(l).getTextContent();
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
		            printWriter.close();
	        	}
	        	
	        }
	    	
	    }

	}	
}
