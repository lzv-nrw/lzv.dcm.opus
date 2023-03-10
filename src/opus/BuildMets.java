package opus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Generate METS
 * 
 * @author ogan
 *
 */
public class BuildMets {

	public static void generateMets(String dir, String id) throws Exception {
		
		// dirName
		String dirName = "\\content";
		String sourceDirectory;
		String targetDirectory;
		String parentDirectory;
				
		if (id != null) {				
			sourceDirectory = "opus_resources\\" + dir + "\\bagits\\opus_" + id + "\\bag-info.txt";
			targetDirectory = "opus_resources\\" + dir + "\\mets\\opus_" + id + dirName;
						
	        Path path = Paths.get(sourceDirectory);
	        sourceDirectory = path.toAbsolutePath().toString();        
	        BuildMets.writeInFile(sourceDirectory, targetDirectory);
		}
		else {
			parentDirectory = "opus_resources\\" + dir + "\\bagits";
	        Path path = Paths.get(parentDirectory);
	        parentDirectory = path.toAbsolutePath().toString();
	        
			int numberOfSubfolders = 0;
			File src[] = new File(parentDirectory).listFiles();
			
			for (int i = 0; i < src.length; i++) {
			    if (src[i].isDirectory()) {
			            numberOfSubfolders++;
			    }
			}
			
			for (int i = 0; i < numberOfSubfolders; i++) {
				String opusId = src[i].getName();
				sourceDirectory = "opus_resources\\" + dir + "\\bagits\\" + opusId + "\\bag-info.txt";
				targetDirectory = "opus_resources\\" + dir + "\\mets\\" + opusId + dirName;
				      
		        BuildMets.writeInFile(sourceDirectory, targetDirectory);
			}
		}
	}
	
	// Get content and write in file
	public static void writeInFile(String sourceDirectory, String targetDirectory) throws Exception {
		

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.newDocument();
        doc.setXmlStandalone(true);
        
        // root element
        Element rootElement = doc.createElement("mets:mets");
        doc.appendChild(rootElement);
        BuildMetsOld.setAttr(doc, rootElement, "xmlns:mets", "http://www.loc.gov/METS/");
        
        // dmdSec element
        Element dmdSec = doc.createElement("mets:dmdSec");
        rootElement.appendChild(dmdSec);
        BuildMetsOld.setAttr(doc, dmdSec, "ID", "ie-dmd");

        // mdWrap element
        Element mdWrap = doc.createElement("mets:mdWrap");
        dmdSec.appendChild(mdWrap);
        BuildMetsOld.setAttr(doc, mdWrap, "MDTYPE", "DC");
         
        // xmlData element
        Element xmlData = doc.createElement("mets:xmlData");
        mdWrap.appendChild(xmlData);
        
        // record element
        Element record = doc.createElement("record");
        BuildMetsOld.setAttr(doc, record, "xmlns:dc", "http://purl.org/dc/elements/1.1/");
        BuildMetsOld.setAttr(doc, record, "xmlns:dcterms", "http://purl.org/dc/terms/");
        BuildMetsOld.setAttr(doc, record, "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        BuildMetsOld.setAttr(doc, record, "xmlns:mods", "http://www.loc.gov/standards/mods/v3/mods-3-0.xsd");
        xmlData.appendChild(record);
        
        // DC element-tags
		BufferedReader reader;
		String elemTag;
		String elemValue;
		try {
			reader = new BufferedReader(new FileReader(sourceDirectory));
			String line = reader.readLine();
			
			while (line != null) {
				elemTag = BuildMets.dcTagMapper(line);
				Element element = doc.createElement(elemTag);
				elemValue = StringCutter.cutFront(line, " ", 1);
				element.appendChild(doc.createTextNode(elemValue));
				record.appendChild(element);				
				// read next line
				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	// amdSec element
        Element amdSec = doc.createElement("mets:amdSec");
        rootElement.appendChild(amdSec);
        BuildMetsOld.setAttr(doc, amdSec, "ID", "ie-amd");
        
        // techMD element
        Element techMD = doc.createElement("mets:techMD");
        amdSec.appendChild(techMD);
        BuildMetsOld.setAttr(doc, techMD, "ID", "ie-amd-tech");
        
        // mdWrap element
        mdWrap = doc.createElement("mets:mdWrap");
        techMD.appendChild(mdWrap);
        BuildMetsOld.setAttr(doc, mdWrap, "MDTYPE", "OTHER");
        BuildMetsOld.setAttr(doc, mdWrap, "OTHERMDTYPE", "dnx");
    	
        // xmlData element
        xmlData = doc.createElement("mets:xmlData");
        mdWrap.appendChild(xmlData);
        
        // dnx element
        Element dnx = doc.createElement("dnx");
        xmlData.appendChild(dnx);
        BuildMetsOld.setAttr(doc, dnx, "xmlns", "http://www.exlibrisgroup.com/dps/dnx");
        
        // section element
        Element section = doc.createElement("section");
        dnx.appendChild(section);
        BuildMetsOld.setAttr(doc, section, "id", "generalIECharacteristics");
        
    	// record element
        record = doc.createElement("record");
        section.appendChild(record);
        
    	// key element
        Element key = doc.createElement("key");
        record.appendChild(key);
        key.appendChild(doc.createTextNode("Other"));
        BuildMetsOld.setAttr(doc, key, "id", "IEEntityType");
        
        // create the xml file
        // transform the DOM Object to an XML File
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        DOMSource domSource = new DOMSource(doc);
        
        File test = new File(targetDirectory);
        if ( test.exists()) {
	        StreamResult streamResult = new StreamResult(new File(targetDirectory + "\\ie.xml"));
	        transformer.transform(domSource, streamResult);
        }
        
	}
	
	
	public static String dcTagMapper(String line){
		
		for (int i = 0; i < 1; i++) {
			line = "dc:" + line.substring(0, line.indexOf(":")).toLowerCase();
        }
		return line;
	}
}


