package com.directconnect.mobilesdk.transaction;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


//import javax.xml.soap.MessageFactory;
//import javax.xml.soap.MimeHeaders;
//import javax.xml.soap.SOAPBody;
//import javax.xml.soap.SOAPConnection;
//import javax.xml.soap.SOAPConnectionFactory;
//import javax.xml.soap.SOAPEnvelope;
//import javax.xml.soap.SOAPException;
//import javax.xml.soap.SOAPMessage;
//import javax.xml.soap.SOAPPart;

/**
 * Request abstract class
 * 
 * This class derives from the generic DataCollection class and defines an abstract request
 * class to be derived into method-specific request classes. The base class contains methods
 * to assign values to data elements along with XML and NVP serialization and request-
 * processing methods.
 * 
 * @author Francois Bergeon - Oakwell Consulting LLC for Direct Connect LLC
 *
 */
public abstract class Request extends DataCollection {
	/**
	 * Set String value to collection
	 * 
	 * @param name	Element name
	 * @param value	String value
	 */
	public void setValue(String name, String value) {
		// Reject if not defined
		if (!getElements().containsKey(name))
			throw new IllegalArgumentException(name);
		values.put(name, value);
	}
	
	/**
	 * Set int value to collection
	 * 
	 * @param name	Element name
	 * @param value	int value
	 */
	public void setValue(String name, int value) {
		setValue(name, String.valueOf(value));
	}
	
	/**
	 * Set boolean value to collection
	 * 
	 * @param name	Element name
	 * @param value	boolean value
	 */
	public void setValue(String name, boolean value) {
		setValue(name, (value ? "T": "F"));
	}
	
	/**
	 * Serialize request to NVP string - unused
	 * 
	 * @return NVP String
	 */
	public String serializeNvp() {
		Iterator<Map.Entry<String,DataElement>> it = getElements().entrySet().iterator();
		StringWriter sw = new StringWriter();
		boolean append = false;

		// Iterate through elements
		while (it.hasNext()) {
			// Get map entry
			Entry<String, DataElement> e = it.next();
			// Get DataElement name
			String name = e.getKey();
			// Get DataElement value
			Object v = values.get(name);

			if (append)
				sw.write('&');
			
			// Serialize element - even if empty
			serializeElementToNVP(sw, name, getElements().get(name), v);
			append = true;
		}
		return sw.toString();
	}
	
	/**
	 * Serialize DataElement to NVP pair
	 * 
	 * @param dataElement DataElement object
	 * @param value DataElement value
	 */
	private void serializeElementToNVP(StringWriter sw, String name, DataElement dataElement, Object value) {
		Request rc = null;
		sw.write(name);
		sw.write('=');
		// Attempt to cast object to a DataCollection if TYPE_UNDEF
		if (dataElement.type == DataElement.TYPE_UNDEF && value != null) {
			rc = (Request)value;
		}
		
		try {
			if (rc != null) {
				// Serialize sub class
				value = rc.serializeToXmlString();
			}			
			if (value != null) {
				// Serialize DataElement value
				sw.write(URLEncoder.encode(value.toString(), "UTF-8"));
			}
		} catch (Exception e) {
		}
	}
	
	
	/**
	 * Create XML document containing serialized DataCollection object
     *
	 * @param namespaceURI Namespace
	 * @return XML document
	 * @throws ParserConfigurationException
	 * @throws DOMException
	 */
	protected Document createDocument(String namespaceURI) throws ParserConfigurationException, DOMException {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		
		Element root;
		if (namespaceURI != null)
			root = doc.createElementNS(namespaceURI, getName());
		else
			root = doc.createElement(getName());
		populateRootElement(root);
		doc.appendChild(root);
		return doc;
	}

	/**
	 * Populate XML element containing serialized DataCollection
	 * 
	 * @param parent Parent XML element
	 */
	protected boolean populateRootElement(Element parent) {
		Iterator<Map.Entry<String,Object>> it = values.entrySet().iterator();
		boolean notEmpty = false;

		// Iterate through elements
		while (it.hasNext()) {
			// Get map entry
			Map.Entry<String,Object> e = it.next();
			String name = e.getKey();
			// Get DataElement value
			Object v = e.getValue();
			// Serialize value if not null
			if (v != null) {
				// Create XML element
				Element element = parent.getOwnerDocument().createElementNS(parent.getNamespaceURI(), name);
				element.setPrefix(parent.getPrefix());
				// Serialize value as text body
				if (serializeElement(element, getElements().get(name), v)) {
					parent.appendChild(element);
					notEmpty = true;
				}
			}
		}
		return notEmpty;
	}
	
	/**
	 * Serialize DataElement to XML node
	 * 
	 * @param xmlElement Parent XML element
	 * @param dataElement DataElement object
	 * @param value DataElement value
	 */
	private boolean serializeElement(Element xmlElement, DataElement dataElement, Object value) {
		Request rc = null;
		// Attempt to cast object to a DataCollection if TYPE_UNDEF
		if (dataElement.type == DataElement.TYPE_UNDEF)
			rc = (Request)value;
		
		if (rc != null) {
			if (rc.serializeToParent(xmlElement))
				return true;
		}			
		else if (value != null) {
			// Serialize DataElement value
			String s = value.toString();
			if (s.length() > 0) {
				xmlElement.setTextContent(s);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Serialize data collection into XML parent element
	 * This method is overridden for objects that need to be serialized
	 * as rootless XML inside CDATA
	 * 
	 * @param parent Parent XML element
	 */
	protected boolean serializeToParent(Element parent) {
		return populateRootElement(parent);
	}

	/**
	 * Serialize collection as rootless XML string in parent's CDATA
	 * 
	 * @param parent Parent element
	 * @return true if serialization successful
	 */
	protected boolean populateCData(Element parent) {
		String xml = serializeToXmlString();
		if (xml.length() > 0) {
			CDATASection cdata = parent.getOwnerDocument().createCDATASection(xml);
			parent.appendChild(cdata);
			return true;
		}
		return false;
	}
	
	/**
	 * Serialize DataCollection to rootless XML string
	 * 
	 * @return XML string
	 */
	private String serializeToXmlString() {
		StringWriter sw = new StringWriter();
		serializeToXmlString(sw);
		return sw.toString();
	}
	
	/**
	 * Manually serialize DataCollection to StringWriter (ugly)
	 * 
	 * @param sw StringWriter object
	 */
	private void serializeToXmlString(StringWriter sw) {
		// Iterate through elements
		for (Map.Entry<String,Object> e : values.entrySet()) {
			String name = e.getKey();
			sw.write('<');
			sw.write(name);
			sw.write('>');
			// Get DataElement value
			Object value = e.getValue();
			DataElement dataElement = getElements().get(name);
			Request rc = null;
			// Attempt to cast object to a DataCollection if TYPE_UNDEF
			if (dataElement.type == DataElement.TYPE_UNDEF)
				rc = (Request)value;
			
			if (rc != null) {
				// Serialize subclass
				rc.serializeToXmlString(sw);
			}			
			else if (value != null) {
				// Serialize DataElement value
				sw.write(value.toString());
			}
			sw.write("</");
			sw.write(name);
			sw.write('>');
		}
	}
}
