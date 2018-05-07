package com.directconnect.mobilesdk.transaction;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Response abstract class
 * 
 * This class derives from the generic DataCollection class and defines an abstract
 * response class to be derived into method-specific response classes. The base class
 * contains methods to getValue values from data elements along with XML and NVP parsing
 * methods.
 * 
 * @author Francois Bergeon - Oakwell Consulting LLC for Direct Connect LLC
 *
 */
public abstract class Response extends DataCollection {
    /**
     * Exception setter, tester and getter
     */
	private static final String TAG = "SendableRequest";
	private static final String exception = "**Exception**";
    protected void setException(Exception e) { values.put(exception, e); }
    public boolean hasException() { return values.containsKey(exception); }
    public Exception getException() { return (Exception)values.get(exception); }

    /**
	 * Get data element's string value and enforce type unless TYPE_UNDEF
	 * 
	 * @param name Element name
	 * @param type Element type
	 * @return Element's string value
	 */
	protected String getValue(String name, final int type) {
		DataElement el = getElements().get(name);
		if (el == null || (type != DataElement.TYPE_UNDEF && type != el.getType()))
			throw new IllegalArgumentException(name);
		return (String)values.get(name);
	}
	
	/**
	 * Get data element's string value
	 * 
	 * @param name Element name
	 * @return Element's string value
	 */
	public String getValue(String name) {
		return getValue(name, DataElement.TYPE_UNDEF);
	}
	
	/**
	 * Get data element's int value
	 * 
	 * @param name Element name
	 * @return Element's int value
	 */
	public int getInt(String name) {
		String value = getValue(name, DataElement.TYPE_INT);
		if (value == null)
			throw new IllegalArgumentException(name);
		
		return Integer.parseInt(value);		
	}
	
	/**
	 * Get data element's boolean value
	 * 
	 * @param name Element name
	 * @return Element's boolean value
	 */
	public boolean getBool(String name) {
		String value = getValue(name, DataElement.TYPE_BOOLEAN);
		return (value.equals("T"));
	}
	
	/**
	 * Parse XML string into DataCollection
	 * @param xml XML string
	 * @return true if parsing successful
	 */
	public boolean parse(String xml) {
        Log.d(TAG, "parse String");
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
		return parse(in);
	}

	
	/**
	 * Parse InputStream into DataCollection
	 * @param in InputStream
	 * @return true if parsing successful
	 */
	boolean parse(InputStream in) {
        Log.d(TAG, "parse InputStream");
        try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        SAXParser parser = spf.newSAXParser();
	        ResponseHandler handler = new ResponseHandler();
	        parser.parse(in, handler); 	
	        return true;
		} catch (Exception e) {
			Log.e(TAG, "parse exception: " + e);
			setException(e);
			return false;
		}
	}
	
	/**
	 * Manually parse NVP/XML string into DataCollection.
	 * Also parses embedded XML within the NVP string (ugly!)
	 * 
	 * @param value NVP/XML string
	 * @return true if parsing successful
	 */
	protected boolean parseNVP(String value) {
        Log.d(TAG, "parseNVP");
        byte[] chars = value.getBytes();
		int i, start, end = 0, xmlDepth;
		String elementName = null;
		
		for (i = 0, start = 0, xmlDepth = 0; i < chars.length; i++) {
			// Comma-delimited NVP
			if (chars[i] == '=') {
				// Save NVP name
				elementName = value.substring(start, i);
				start = i+1;
			} else if (chars[i] == ',') {
				// Save NVP value and setValue pair
				if (elementName != null) {
					values.put(elementName, value.substring(start, i));		
					elementName = null;
				}
				start = i+1;
			} else if (chars[i] == '<') {
				// Closing tag?
				boolean closing = (chars[i+1] == '/');
				
				// Opening tag
				if (!closing) {
					// First opening XML brace is also an end NVP delimiter (yikes!)
					if (++xmlDepth == 1) {
						// Parse non-delimited NVP if any
						if (i > start) {
							if (elementName != null) {
								values.put(elementName, value.substring(start, i));		
								elementName = null;
							}
							start = i;
						}
					}
				} else if (xmlDepth == 1) {
					// Start of top-level XML closing tag is end of value
					end = i;
				}

				// Skip tag name
				while (chars[i] != '>') {
					if (++i == chars.length)
						break;
				}
				
				// Check for empty node
				closing = (closing || (chars[i-1] == '/'));

				// Last closing XML is also a start NVP/XML delimiter (yikes!)						
				if (closing) {
					if (--xmlDepth == 0) {
						// XML ends here, save XML contents as string and setValue pair
						// Nested XML will be parsed by its own subclass
						if (elementName != null && end > start) {
							values.put(elementName, value.substring(start, end));	
							elementName = null;
							end = 0;
						}
						// Next NVP or XML section starts there
						start = i+1;
					}
				} else if (xmlDepth == 1) {
					// Save top-level XML tag
					elementName = value.substring(start+1, i);	
					start = i+1;
				}
			}
		}
		
		// Save trailing NVP if any (yikes!)
		if (i > start && xmlDepth == 0 && elementName != null) {
			values.put(elementName, value.substring(start, i));		
		}
		
		return true;
	}
	
	// To be derived
	public void parseSubClasses() {
		// DO NOTHING
	}
	
	// SAX handler
	private class ResponseHandler extends DefaultHandler {
		private int nLevel = 0;
		private String elementName = null;
		private StringWriter elementWriter = null;
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (nLevel == 0 && qName.equals(getName())) {
			    ++nLevel;
			} else if (nLevel == 1 && qName.endsWith("Result")) {
				++nLevel;
			} else if (nLevel == 2) {
			    ++nLevel;
			    elementName = qName;
			}
	   }

		@Override
		public void characters(char ch[], int start, int length) throws SAXException {
			if (elementName != null) {
				if (elementWriter == null)
					elementWriter = new StringWriter();
				elementWriter.write(ch, start, length);
			}
		}
   
	   @Override
	   public void endElement(String uri, String localName, String qName) throws SAXException {
		    if (qName != elementName)
		    {
		        // ** Throw exception        
		    }
		    if (--nLevel >= 2) {
		        if (elementWriter != null)
		        	values.put(elementName, elementWriter.toString());
		        elementName = null;
		        elementWriter = null;
		    }
	   }

	}
}
