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
	
	public static void download(String dir, String opusId, String fileName, boolean greater) throws IOException, Exception {

		String sourceDirectory = "opus_resources\\" + dir + "\\metadata\\";
		File inputFile = new File(sourceDirectory + fileName);		
		
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
	    		// Check if dir exist
	    		File bagitTest = new File("opus_resources\\" + dir + "\\bagits\\opus_" + id);
	        	// Call method downloadFiles for one BagIt or all
	        	if (opusId != null && id.equals(opusId) && greater == false) { 
	        		System.out.println("Check OPUS-ID " + id + " for Download");
	        		DownloadFile.downloadFiles(urlElement, id, dir);
		    	    // Compare downloaded Files
		    		if (bagitTest.exists()) {	    				    		
		    			String fileDirectory = "opus_resources\\" + dir + "\\bagits\\opus_" + id + "\\data\\";
		    			DownloadFile.compareFiles(fileDirectory);
		    		}
			    } 
	        	else if (opusId != null && Integer.parseInt(id) >= Integer.parseInt(opusId) && greater == true ) {
	        		System.out.println("Check OPUS-ID " + id + " for Download");
	        		DownloadFile.downloadFiles(urlElement, id, dir);
		    	    // Compare downloaded Files
		    		if (bagitTest.exists()) {	    				    		
		    			String fileDirectory = "opus_resources\\" + dir + "\\bagits\\opus_" + id + "\\data\\";
		    			DownloadFile.compareFiles(fileDirectory);
		    		}
			    } 
	        	else if (opusId == null) {
	        		System.out.println("Check OPUS-ID " + id + " for Download");
	        		DownloadFile.downloadFiles(urlElement, id, dir);
		    	    // Compare downloaded Files
		    		if (bagitTest.exists()) {	    				    		
		    			String fileDirectory = "opus_resources\\" + dir + "\\bagits\\opus_" + id + "\\data\\";
		    	    	DownloadFile.compareFiles(fileDirectory);
		    		}
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
		//int k = urlElement.getElementsByTagName("dc:format").getLength();
    	int n = urlElement.getElementsByTagName("dc:format").getLength();
//    	while (urlElement.getElementsByTagName("dc:format").item(n) != null){
//    		n++;
//    	}
    	int m = urlElement.getElementsByTagName("dc:identifier").getLength();
//    	while (urlElement.getElementsByTagName("dc:identifier").item(m) != null){
//    		m++;
//    	}

    	// Get number of urls to download content
    	int k=m-n;
    	System.out.println("Download " + n + " Dateien(en)" );
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
	
	// Compare downloaded Files
	public static void compareFiles(String filePath) throws Exception{
		ArchiveExtractor compare = new ArchiveExtractor();
		compare.extractor(filePath);
	}
}
