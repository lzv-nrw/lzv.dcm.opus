package opus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Get Metadata from OPUS and write in Files
 * 
 * @author ogan
 *
 */
public class GetMetaData {

	public static void getRequest(String url, String dir, String date) throws IOException, Exception {
		
		String xmlPath = "opus_resources\\" + dir +"\\metadata\\";
		File metaData = new File(xmlPath);
		if (!metaData.exists()) {		
			metaData.mkdirs();
		}
		
		int fileCount = 0;
		URL urlForGetRequest;
		if (date == null){
			FileUtils.cleanDirectory(new File(xmlPath));
			urlForGetRequest = new URL(url +"oai?verb=ListRecords&metadataPrefix=oai_dc");
		}
		else {
			urlForGetRequest = new URL(url +"oai?verb=ListRecords&metadataPrefix=oai_dc&from=" + date);
			File files = new File(xmlPath);
			fileCount = files.list().length;
		}
		
	    HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
	    conection.setRequestMethod("GET");
	    int responseCode = conection.getResponseCode();

	    ReadableByteChannel rbc;
	    FileOutputStream fos;
	    File inputFile;

	    String resumptionTokenContent = null;
	    
	    // Download first xml file
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	    	System.out.println(urlForGetRequest.toString());
	    	rbc = Channels.newChannel(urlForGetRequest.openStream());
	    	File xmlOutput = new File(xmlPath);
			xmlOutput.mkdirs();
			
			if (date == null) {
				fos = new FileOutputStream(xmlPath + "opusMetaData_0.xml");
			}
			else {
				fos = new FileOutputStream(xmlPath + "opusMetaData_" + fileCount + ".xml");
			}
			
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
            
			if (date == null) {
				inputFile = new File(xmlPath + "opusMetaData_0.xml");
			}
			else {
				inputFile = new File(xmlPath + "opusMetaData_" + fileCount + ".xml");
			}
			
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            
            Element element = doc.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("ListRecords");
            Element listRecordsElement = (Element)nodeList.item(0);
            Element resumptionTokenElement = (Element)listRecordsElement.getElementsByTagName("resumptionToken").item(0);
            Text resumptionTokeContentNode = null;
            if (resumptionTokenElement != null) {
            	resumptionTokeContentNode = (Text)resumptionTokenElement.getFirstChild();
            	if (resumptionTokeContentNode != null) {
            		resumptionTokenContent = resumptionTokeContentNode.getData();
            	}
            }

            // Download next pages
            int i=1;
            if (date != null) {
            	i = fileCount + 1;
            }
            	
            while(resumptionTokenElement != null && resumptionTokeContentNode != null) {          	
            	urlForGetRequest = new URL(url +"oai?verb=ListRecords&resumptionToken=" + resumptionTokenContent);
    	    	System.out.println(urlForGetRequest.toString());
            	rbc = Channels.newChannel(urlForGetRequest.openStream());        
            	fos = new FileOutputStream(xmlPath + "opusMetaData_" + i +".xml");
    			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    			fos.close();
 
                inputFile = new File("opus_resources\\" + dir + "\\metadata\\opusMetaData_" + i + ".xml");
                dbFactory = DocumentBuilderFactory.newInstance();
                dBuilder = dbFactory.newDocumentBuilder();
                doc = dBuilder.parse(inputFile);
                
                element = doc.getDocumentElement();
	            nodeList = element.getElementsByTagName("ListRecords");
	            listRecordsElement = (Element)nodeList.item(0);
	            resumptionTokenElement = (Element)listRecordsElement.getElementsByTagName("resumptionToken").item(0);
	            if(resumptionTokenElement != null) {
	            	resumptionTokeContentNode = (Text)resumptionTokenElement.getFirstChild();
	            	if (resumptionTokeContentNode != null) {
	            		resumptionTokenContent = resumptionTokeContentNode.getData();
	            		i++;
	            	}
	            }
            }
	    }
	}
}
