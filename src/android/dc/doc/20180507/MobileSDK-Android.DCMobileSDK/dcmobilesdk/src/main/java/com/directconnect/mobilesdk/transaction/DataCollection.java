package com.directconnect.mobilesdk.transaction;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * DataCollection abstract class
 * 
 * This class defines an abstract class to be derived into method-specific request
 * and response collection classes.
 * 
 * @author Francois Bergeon - Oakwell Consulting LLC for Direct Connect LLC
 *
 */
abstract class DataCollection {
	protected HashMap<String, Object> values = new HashMap<>();
	private String className = this.getClass().getSimpleName();

	/**
	 * Get class name
	 * 
	 * @return class name
	 */
	protected String getName() {
		return className;
	}
	
	/***
	 * Nested class DataElement
	 * Holds type and length of data element
	 * 
	 * @author Francois Bergeon
	 * 	
	 */
	protected static class DataElement {
		// Types enum
		public static final int TYPE_UNDEF = 0;
		public static final int TYPE_STRING = 1;
		public static final int TYPE_INT = 2;
		public static final int TYPE_BOOLEAN = 3;
		
		// DC Data Element fields
		protected int type = TYPE_UNDEF;
		protected int length = 0;
		
		/**
		 * Constructor with type and length
		 * 
		 * @param type - data element type
		 * @param length - data element name
		 */
		public DataElement(final int type, int length) {
			this.type = type;
			this.length = length;
		}
		
		/**
		 * Constructor with type only (length=0)
		 * 
		 * @param type - data element type
		 */
		public DataElement(final int type) {
			this.type = type;
		}

		/**
		 * Basic constructor (TYPE_UNDEF, length=0)
		 * 
		 */
		public DataElement() {
		}
		
		/**
		 * @return data element type
		 */
		public int getType() {
			return type;
		}
		
		/**
		 * @return data element length
		 */
		public int getLength() {
			return length;
		}
	}
	
	protected abstract HashMap<String, DataElement> getElements();
	
	/**
	 * Recursively serialize XML Node into a StringWriter
	 * This method is used both by Request and Response classes
	 * 
	 * @param sw StringWriter
	 * @param n XML Node
	 * @throws UnsupportedEncodingException
	 */
	static void nodeToString(StringWriter sw, Node n) throws UnsupportedEncodingException {
		if (n == null)
			return;
		
		String name = n.getNodeName();
		sw.write('<');
		sw.write(name);
		sw.write('>');
		if (n.getNodeType() == Node.ELEMENT_NODE) {
			NodeList nl = n.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++){
				Node child = nl.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					nodeToString(sw, child);
				} else {
					String elementValue = n.getTextContent();
					if (elementValue != null) {
						sw.write(URLEncoder.encode(elementValue, "UTF-8"));
					}
				}				
			}
		}
		sw.write("</");
		sw.write(name);
		sw.write('>');
	}
	
	/**
	 * Retrieve XML representation of property holder
	 * 
	 * @return XML string
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 */
	protected String getOuterXml(DOMSource source) throws TransformerFactoryConfigurationError, TransformerException
	{
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		//transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		StreamResult result = new StreamResult(new StringWriter());
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		return xmlString;
	}
	
	/**
	 * Build nested parameter name
	 * 
	 * @param parameterName Name of parameter
	 * @return prefix.value
	 */
	protected static String fqParam(Class<?> theClass, String parameterName) {
		return theClass.getSimpleName() + "." + parameterName;
	}
}
