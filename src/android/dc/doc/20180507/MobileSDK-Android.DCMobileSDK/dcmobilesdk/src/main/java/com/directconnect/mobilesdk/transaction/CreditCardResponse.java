package com.directconnect.mobilesdk.transaction;

import java.util.HashMap;

/**
 * Credit Card Response data elements
 * 
 * @author Francois Bergeon
 *
 */
public class CreditCardResponse extends Response {
	// Internal static map & constructor
	private static HashMap<String, DataElement> elements = new HashMap<>();
	private static final String className = "Process" + CreditCardResponse.class.getSimpleName();
	
	@Override
	protected String getName() {
		return className;
	}
	
	/**
	 * Nested class CreditCardResponse.ExtData
	 *
	 * @author Francois Bergeon
	 */
	private static class ExtData extends Response {
		// Internal static map & constructor
		static HashMap<String, DataElement> elements = new HashMap<>();

		// Build nested parameter name
		protected static String nestedParam(String parameterName) {
			return fqParam(ExtData.class, parameterName);
		}
		
		
		/**
		 * Nested class CreditCardResponse.ExtData.ReceiptData
		 *
		 * @author Francois Bergeon
		 */
		static class ReceiptData extends Response {
			// Internal static map & constructor
			static HashMap<String, DataElement> elements = new HashMap<>();

			// Build nested parameter name
			protected static String nestedParam(String parameterName) {
				return fqParam(ReceiptData.class, parameterName);
			}
			
			// CCResponse.ExtData.ReceiptData element names
			static final String Requested_Amt = "Requested_Amt";
			static final String Approved_Amt = "Approved_Amt";
			
			// CCResponse.ExtData.ReceiptData element definitions
			static {		
				elements.put(Requested_Amt, new DataElement(DataElement.TYPE_STRING, 18));
				elements.put(Approved_Amt, new DataElement(DataElement.TYPE_STRING, 18));
			}
			
			@Override
			protected HashMap<String, DataElement> getElements() {
				return elements;
			}
		}
		
		// CCResponse.ExtData element names
		static final String BatchNum = "BatchNum"; // Nested class
		static final String CardType = "CardType"; // Nested class
		static final String ExpDate = "ExpDate"; // Nested class
		static final String EmvResponseData = "EmvResponseData";
		static final String ReceiptDataClass = ReceiptData.class.getSimpleName(); // Nested class
			
		// CreditCardResponse.ExtData element definitions
		static {
			elements.put(BatchNum, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(CardType, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(ExpDate, new DataElement(DataElement.TYPE_STRING, 4));
			elements.put(EmvResponseData, new DataElement(DataElement.TYPE_STRING));
			elements.put(ReceiptDataClass, new DataElement());
		}	
		
		@Override
		protected HashMap<String, DataElement> getElements() {
			return elements;
		}
		
		/**
		 * Parse ReceiptData (Rootless XML format)
		 */
		ReceiptData receiptData = null;
		public void parseSubClasses() {
			// Parse ExtData (already parsed as a string)
			String value = (String)values.get(ReceiptDataClass);
			if (value != null) {
				receiptData = new ReceiptData();
				// Rootless parsing
				if (receiptData.parseNVP(value)) {
					values.put(ReceiptDataClass,  receiptData);
					receiptData.parseSubClasses();
				}
			}
		}

		/**
		 * Get value including sub-containers
		 * 
		 * @param name Element name
		 * @param type Element type
		 * @return Element value
		 */
		protected String getValue(String name, final int type) {
			String value = null;
			if (name.startsWith(ReceiptDataClass)) {
				if (receiptData != null)
					value = receiptData.getValue(name.substring(ReceiptDataClass.length()+1), type);
			} else {
				value = super.getValue(name, type);
			}
			return value;
		}
		 
	}

	// CreditCardResponse element names
	public static final String AuthorizedAmount = "AuthorizedAmount";
	public static final String Balance = "Balance";
	public static final String Result = "Result";
	public static final String RespMSG = "RespMSG";
	public static final String Message = "Message";
	public static final String Message1 = "Message1";
	public static final String Message2 = "Message2";
	public static final String AuthCode = "AuthCode";
	public static final String PNRef = "PNRef";
	public static final String HostCode = "HostCode";
	public static final String HostURL = "HostURL";
	public static final String ReceiptURL = "ReceiptURL";
	public static final String GetAVSResult = "GetAVSResult";
	public static final String GetAVSResultTXT = "GetAVSResultTXT";
	public static final String GetAVSStreetMatchTXT = "GetAVSStreetMatchTXT";
	public static final String GetAVSZipMatchTXT = "GetAVSZipMatchTXT";
	public static final String GetCVResult = "GetAVSResult";
	public static final String GetCVResultTXT = "GetAVSResultTXT";
	public static final String GetOrigResult = "GetOrigResult";
	public static final String GetCommercialCard = "GetCommercialCard";
	public static final String WorkingKey = "WorkingKey";
	public static final String KeyPointer = "KeyPointer";

	// ExtData elements
	private static final String ExtDataClass = ExtData.class.getSimpleName();	// Nested class
	public static final String BatchNum = ExtData.nestedParam(ExtData.BatchNum);
	public static final String CardType = ExtData.nestedParam(ExtData.CardType);
	public static final String ExpDate = ExtData.nestedParam(ExtData.ExpDate);
	public static final String EmvResponseData = ExtData.nestedParam(ExtData.EmvResponseData);
	// ExtData.ReceiptData elements
	public static final String Requested_Amt = ExtData.nestedParam(ExtData.ReceiptData.nestedParam(ExtData.ReceiptData.Requested_Amt));
	public static final String Approved_Amt = ExtData.nestedParam(ExtData.ReceiptData.nestedParam(ExtData.ReceiptData.Approved_Amt));

	// CCResponse element definitions
	static {
		elements.put(AuthorizedAmount, new DataElement(DataElement.TYPE_STRING, 18));
		elements.put(Balance, new DataElement(DataElement.TYPE_STRING, 18));
		elements.put(Result, new DataElement(DataElement.TYPE_INT));
		elements.put(RespMSG, new DataElement(DataElement.TYPE_STRING, 150));
		elements.put(Message, new DataElement(DataElement.TYPE_STRING, 150));
		elements.put(Message1, new DataElement(DataElement.TYPE_STRING, 150));
		elements.put(Message2, new DataElement(DataElement.TYPE_STRING, 150));
		elements.put(AuthCode, new DataElement(DataElement.TYPE_STRING, 20));
		elements.put(PNRef, new DataElement(DataElement.TYPE_STRING, 10));
		elements.put(HostCode, new DataElement(DataElement.TYPE_STRING, 30));
		elements.put(HostURL, new DataElement(DataElement.TYPE_STRING));
		elements.put(ReceiptURL, new DataElement(DataElement.TYPE_STRING));
		elements.put(GetAVSResult, new DataElement(DataElement.TYPE_STRING, 10));
		elements.put(GetAVSResultTXT, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(GetAVSStreetMatchTXT, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(GetAVSZipMatchTXT, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(GetCVResult, new DataElement(DataElement.TYPE_STRING, 1));
		elements.put(GetCVResultTXT, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(GetOrigResult, new DataElement(DataElement.TYPE_STRING));
		elements.put(GetCommercialCard, new DataElement(DataElement.TYPE_BOOLEAN));
		elements.put(WorkingKey, new DataElement(DataElement.TYPE_STRING));
		elements.put(KeyPointer, new DataElement(DataElement.TYPE_STRING));
		elements.put(ExtDataClass, new DataElement());
	}	
	
	@Override
	protected HashMap<String, DataElement> getElements() {
		return elements;
	}
	
	/**
	 * Parse ExtData (Mixed NVP/XML format)
	 */
	private ExtData extData = null;
	public void parseSubClasses() {
		// Parse ExtData (already parsed as a string)
		String value = (String)values.get(ExtDataClass);
		if (value != null) {
			extData = new ExtData();
			// Rootless parsing
			if (extData.parseNVP(value)) {
				values.put(ExtDataClass,  extData);
				extData.parseSubClasses();
			}
		}
	}

	/**
	 * Get value including sub-containers
	 * 
	 * @param name Element name
	 * @param type Element type
	 * @return Element value
	 */
	protected String getValue(String name, final int type) {
		String value = null;
		if (name.startsWith(ExtDataClass)) {
			if (extData != null)
				value = extData.getValue(name.substring(ExtDataClass.length()+1), type);
		} else {
			value = super.getValue(name, type);
		}
		return value;
	}
}
