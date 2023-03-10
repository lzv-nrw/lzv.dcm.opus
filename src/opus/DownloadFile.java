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

/**
 * Download files from the OPUS-record
 * 
 * @author ogan
 *
 */
public class DownloadFile {
	
	public static void download(String dir, String opusId, int fileCount) throws IOException, Exception {

		String sourceDirectory = "opus_resources\\" + dir + "\\metadata";
		
		File inputFile = new File(sourceDirectory + "\\opusMetaData_" + fileCount + ".xml");
	    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    Document doc = dBuilder.parse(inputFile);
	    doc.getDocumentElement().normalize();
	    NodeList nList = doc.getElementsByTagName("record");
	    		
	    String id = null;
	    
	    for (int temp = 0; temp < nList.getLength(); temp++) {

	    	Node nNode = nList.item(temp);
	    	Element urlElement = (Element) nNode;
	    	Element idElement = (Element) nNode;
	    	
	    	// Get OPUS identifier content
		    if (nNode.getNodeType() == Node.ELEMENT_NODE) {	  
	        	if (idElement.getElementsByTagName("identifier").item(0) != null){
	        		id = idElement.getElementsByTagName("identifier").item(0).getTextContent();
	        		id = StringCutter.cutFront(id, ":", 2);
	        	}            
	        	
	        	// Call method downloadFiles for one BagIt or all
	        	if (opusId != null && id.equals(opusId)) {    		
	        		DownloadFile.downloadFiles(urlElement, id, dir);
			    } 
	        	else if (opusId == null) {
	        		DownloadFile.downloadFiles(urlElement, id, dir);
			    }
		    }
	    }
	}
	
	// Downloader
	public static void downloadFiles(Element urlElement, String id, String dir) throws Exception {
		String url = null;
		String filename = null;
		List<String> filePath = new ArrayList<String>();

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
			
			String pathString = "opus_resources\\" + dir + "\\bagits\\opus_" + id + "\\data\\";

			File f1 = new File(pathString);  
			f1.mkdirs();  
			filename = StringCutter.cutFront(url, "/", 5).replace("%20", " ");

			filePath.add(pathString + filename);
			
			FileOutputStream fos = new FileOutputStream(pathString + filename);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			k++;
    	}
    }
}
