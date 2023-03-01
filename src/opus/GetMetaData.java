package opus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class GetMetaData {

	public static void getRequest(String url, String dir) throws IOException, Exception {
		
		URL urlForGetRequest = new URL(url +"oai?verb=ListRecords&metadataPrefix=oai_dc");
	    String readLine = null;
	    HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
	    conection.setRequestMethod("GET");
	    int responseCode = conection.getResponseCode();

	    BufferedReader in;
	    StringBuffer response;
	    PrintWriter printWriter;
	    OutputStream os;
	    
	    String resumptionTokenContent = null;
	    
	    // Read and write first page
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        in = new BufferedReader(new InputStreamReader(conection.getInputStream(), "utf-8"));

	        response = new StringBuffer();
			String xmlPath = "opus_resources\\" + dir +"\\metadata\\";
			File xmlOutput = new File(xmlPath);
			xmlOutput.mkdirs();
			os = new FileOutputStream(xmlPath + "opusMetaData_0.xml");
	        printWriter = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
	        
	        while ((readLine = in.readLine()) != null) {
	        	printWriter.println(readLine);	
	            response.append(readLine);
	        }
	        in.close();
	        printWriter.close();
            
            File inputFile = new File(xmlPath + "opusMetaData_0.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            
            Element element = doc.getDocumentElement();
            NodeList nodeList = element.getElementsByTagName("ListRecords");
            Element listRecordsElement = (Element)nodeList.item(0);
            Element resumptionTokenElement = (Element)listRecordsElement.getElementsByTagName("resumptionToken").item(0);
            Text resumptionTokeContentNode = (Text)resumptionTokenElement.getFirstChild();
            resumptionTokenContent = resumptionTokeContentNode.getData();

            // Read and write next pages
            int i=1;
            while(resumptionTokenElement != null) {
            	
            	urlForGetRequest = new URL(url +"oai?verb=ListRecords&resumptionToken=" + resumptionTokenContent);
            	readLine = null;
    		    conection = (HttpURLConnection) urlForGetRequest.openConnection();
    		    conection.setRequestMethod("GET");
    		    responseCode = conection.getResponseCode();	
    		    
    		    in = new BufferedReader(new InputStreamReader(conection.getInputStream(), "utf-8"));
    	        response = new StringBuffer();
    	        os = new FileOutputStream("opus_resources\\" + dir + "\\metadata\\opusMetaData_" + i +".xml");
    	        printWriter = new PrintWriter(new OutputStreamWriter(os, "UTF-8")); 
    	        while ((readLine = in.readLine()) != null) {
    	        	printWriter.println(readLine);	
    	            response.append(readLine);
    	        }
    	        in.close();
    	        printWriter.close();
     
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
	            resumptionTokenContent = resumptionTokeContentNode.getData();
                i++;
	            }
            }
	    }
	}
}
