package opus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DownloadFile {
	
	public static void downloadOne(String opusId, int fileCount, String xmlPath, String bagitPath) throws IOException, Exception {
		
		String url = null;
		String id = null;
		String filename = null;
		List<String> filePath = new ArrayList<String>();
				
	
		File inputFile = new File(xmlPath + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
	    		
	    for (int temp = 0; temp < nList.getLength(); temp++) {

	    	Node nNode = nList.item(temp);
	    	Element urlElement = (Element) nNode;
	    	Element idElement = (Element) nNode;
	    	
	    	// Get OPUS identifier content
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {	  
	        	if (idElement.getElementsByTagName("identifier").item(0) != null){
	        		id = idElement.getElementsByTagName("identifier").item(0).getTextContent();
	        		id = DownloadFile.cutFront(id, ":", 2);
	        	}            
	        	
	        	if (id.equals(opusId)) {    		
		        	// Get number of files
		        	int n = 0;
		        	while (urlElement.getElementsByTagName("dc:format").item(n) != null){
		        		n++;
		        	}
		        	int m = 0;
		        	while (urlElement.getElementsByTagName("dc:identifier").item(m) != null){
		        		m++;
		        	}
		        	
		        	// Get url content
		        	int k=m-n;
		        	while (urlElement.getElementsByTagName("dc:identifier").item(k) != null){
		        		url = urlElement.getElementsByTagName("dc:identifier").item(k).getTextContent();
		        	            
					    URL website = new URL(url);
						ReadableByteChannel rbc = Channels.newChannel(website.openStream());
						
						String pathString = bagitPath + "\\opus_" + id + "\\data\\";
						
						File f1 = new File(pathString);  
						f1.mkdirs();  
						filename = DownloadFile.cutFront(url, "/", 5);
		
						filePath.add(pathString + filename);
						
						FileOutputStream fos = new FileOutputStream(pathString + filename);
						fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
						fos.close();
						k++;
		        	}
			    }	        	
		    }
	    }
	}
	

	public static void downloadAll(int fileCount, String xmlPath, String bagitPath) throws IOException, Exception {
		
		String url = null;
		String id = null;
		String filename = null;
		List<String> filePath = new ArrayList<String>();
				
	
		File inputFile = new File(xmlPath + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
		
	    for (int temp = 0; temp < nList.getLength(); temp++) {

	    	Node nNode = nList.item(temp);
	    	Element urlElement = (Element) nNode;
	    	Element idElement = (Element) nNode;
	    	
	    	// Get OPUS identifier content
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        	
	        	if (idElement.getElementsByTagName("identifier").item(0) != null){
	        		id = idElement.getElementsByTagName("identifier").item(0).getTextContent();
	        		id = DownloadFile.cutFront(id, ":", 2);
	        	}            
	        	
	        	// Get number of files
	        	int n = 0;
	        	while (urlElement.getElementsByTagName("dc:format").item(n) != null){
	        		n++;
	        	}
	        	int m = 0;
	        	while (urlElement.getElementsByTagName("dc:identifier").item(m) != null){
	        		m++;
	        	}
	        	
	        	// Get url content
	        	int k=m-n;
	        	while (urlElement.getElementsByTagName("dc:identifier").item(k) != null){
	        		url = urlElement.getElementsByTagName("dc:identifier").item(k).getTextContent();
	        	            
				    URL website = new URL(url);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					
					String pathString = bagitPath + "\\opus_" + id + "\\data\\";
					
					File f1 = new File(pathString);  
					f1.mkdirs();  
					filename = DownloadFile.cutFront(url, "/", 5);
	
					filePath.add(pathString + filename);
					
					FileOutputStream fos = new FileOutputStream(pathString + filename);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);	
					fos.close();
					k++;
	        	}
		    }

	    }
	}
	
	// The method cut a given String returns a partial string starting from a certain position of a separator sign.
	public static String cutFront(String text, String sign, int number) {
        for (int i = 0; i < number; i++) {
            text = text.substring(text.indexOf(sign) + 1, text.length());
        }
        return text;
    }
}
