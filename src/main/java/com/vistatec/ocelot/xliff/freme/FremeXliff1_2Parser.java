package com.vistatec.ocelot.xliff.freme;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vistatec.ocelot.segment.model.OcelotSegment;
import com.vistatec.ocelot.services.SegmentService;

public class FremeXliff1_2Parser {

	public void parse(File file) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
			        .newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			System.out.println("Xliff version :" + doc.getDocumentElement().getAttribute("version"));
			NodeList transUnitNodes = doc.getElementsByTagName("trans-unit");
			if(transUnitNodes != null){
				Node currTransUnit = null;
				for(int i = 0; i<transUnitNodes.getLength(); i++){
					currTransUnit = transUnitNodes.item(i);
					if(currTransUnit.getNodeType() == Node.ELEMENT_NODE){
						Element transUnitElem = (Element) currTransUnit;
						System.out.println("Trans-unit with id = " + transUnitElem.getAttribute("id"));
					}
				}
			}
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }

	}
	
	public void saveAnnotations(final File xliffFile, SegmentService segService){
		
		
	}
	 
	public static void main(String[] args) {
	    
		FremeXliff1_2Parser parser = new FremeXliff1_2Parser();
		File file = new File("C:\\Users\\Martab\\Projects\\XLIFF Files", "AllFeaturesNotWorkingForPhil.xlf");
		parser.parse(file);
    }
}
