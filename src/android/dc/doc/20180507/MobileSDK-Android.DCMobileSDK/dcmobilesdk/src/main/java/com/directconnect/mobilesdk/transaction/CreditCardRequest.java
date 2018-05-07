package com.directconnect.mobilesdk.transaction;

import android.util.Log;

import org.w3c.dom.Element;

import java.net.URL;
import java.util.HashMap;

/**
 * Credit Card Request class
 * This class derives from the generic Request class and implement the ExtData subclass
 *
 * @author Francois Bergeon
 *
 */
public class CreditCardRequest extends SendableRequest<CreditCardResponse> {
	public interface Listener extends SendableRequest.Listener<CreditCardResponse> {}

	// Internal static map & constructor
    private static HashMap<String, DataElement> elements = new HashMap<>();
//	private static final String endPointNVP = "https://gateway.1directconnect.com/ws/transact.asmx/ProcessCreditCard"; // NVP
//  private static final String endPoint = "https://gateway.1directconnect.com/ws/transact.asmx";	// SOAP
//  private static final String endPoint = "https://gatewaytest.1directconnect.com/ws/transact.asmx";	// SOAP
    private static final String soapAction = "http://TPISoft.com/SmartPayments/ProcessCreditCard";
    private static final String className = "ProcessCreditCard";
	private String endPoint;

	@Override
	protected String getName() {
		return className;
	}

	public CreditCardRequest(String endPoint) {
		this.endPoint = endPoint;
	}

	/**
	 * Nested class CreditCardRequest.ExtData
	 *
	 * @author Francois Bergeon
	 */
    private static class ExtData extends Request {
		// Internal static map & constructor
        private static HashMap<String, DataElement> elements = new HashMap<>();

		// Build nested parameter name
		protected static String fqParam(String parameterName) {
			return fqParam(ExtData.class, parameterName);
		}

		// Special case: populate parent as rootless XML in CDATA (yikes!)
		protected boolean serializeToParent(Element parent) {
			return populateCData(parent);
		}

		/**
		 * Nested class CreditCardRequest.ExtData.CustomFields
		 *
		 * @author Francois Bergeon
		 */
		static class CustomFields extends Request {
			// Internal static map & constructor
			static HashMap<String, DataElement> elements = new HashMap<>();

			@Override
			protected HashMap<String, DataElement> getElements() {
				return elements;
			}

			// Accept anything
			public void setValue(String name, String value) {
				values.put(name, value);
			}
		}
		/**
		 * Nested class CreditCardRequest.ExtData.P2PE
		 *
		 * @author Francois Bergeon
		 */
		static class P2PE extends Request {
			// Internal static map & constructor
			static HashMap<String, DataElement> elements = new HashMap<>();

			// Build nested parameter name
			protected static String fqParam(String parameterName) {
				return fqParam(P2PE.class, parameterName);
			}

			// CreditCardRequest.ExtData.Presentation element names
			static final String HSMDevice = "HSMDevice";
			static final String TerminalType = "TerminalType";
			static final String EncryptionType = "EncryptionType";
			static final String KSN = "KSN";
			static final String DataBlock = "DataBlock";

			// CreditCardRequest.ExtData.Presentation element definitions
			static {
				elements.put(HSMDevice, new DataElement(DataElement.TYPE_STRING, 6));
				elements.put(TerminalType, new DataElement(DataElement.TYPE_STRING, 25));
				elements.put(EncryptionType, new DataElement(DataElement.TYPE_STRING, 5));
				elements.put(KSN, new DataElement(DataElement.TYPE_STRING, 20));
				elements.put(DataBlock, new DataElement(DataElement.TYPE_STRING));
			}
			
			@Override
			protected HashMap<String, DataElement> getElements() {
				return elements;
			}
		}


		/**
		 * Nested class CreditCardRequest.ExtData.Presentation
		 *
		 * @author Francois Bergeon
		 */
		static class Presentation extends Request {
			// Internal static map & constructor
			static HashMap<String, DataElement> elements = new HashMap<>();

			// Build nested parameter name
			protected static String fqParam(String parameterName) {
				return fqParam(Presentation.class, parameterName);
			}

			// CreditCardRequest.ExtData.Presentation element names
			static final String CardPresent = "CardPresent";

			// CreditCardRequest.ExtData.Presentation element definitions
			static {
				elements.put(CardPresent, new DataElement(DataElement.TYPE_BOOLEAN));
			}
			
			@Override
			protected HashMap<String, DataElement> getElements() {
				return elements;
			}
		}

		/**
		 * Sub-containers
		 */
		CustomFields customFields = null;
		P2PE p2pe = null;
		Presentation presentation = null;
		public void setValue(String name, String value) {
			if (name.startsWith(CustomFieldsClass)) {
				if (customFields == null) {
					customFields = new CustomFields();
					values.put(CustomFieldsClass, customFields);
				}
				customFields.setValue(name.substring(CustomFieldsClass.length()+1), value);
			} else if (name.startsWith(P2PEClass)) {
				if (p2pe == null) {
					p2pe = new P2PE();
					values.put(P2PEClass, p2pe);
				}
				p2pe.setValue(name.substring(P2PEClass.length()+1), value);
			} else if (name.startsWith(PresentationClass)) {
				if (presentation == null) {
					presentation = new Presentation();
					values.put(PresentationClass, presentation);
				}
				presentation.setValue(name.substring(PresentationClass.length()+1), value);
			} else {
				super.setValue(name, value);
			}
		}
		
		// CreditCardRequest.ExtData element names
		static final String AltMerchName = "AltMerchName";
		static final String AltMerchAddr = "AltMerchAddr";
		static final String AltMerchCity = "AltMerchCity";
		static final String AltMerchState = "AltMerchState";
		static final String AltMerchZip = "AltMerchZip";
		static final String AuthCode = "AuthCode";
		static final String Authentication = "Authentication";
		static final String BillPayment = "BillPayment";
		static final String BillToState = "BillToState";
		static final String BypassAvsCvv = "BypassAvsCvv";
		static final String City = "City";
		static final String Clinical_Amount = "Clinical_Amount";
		static final String ConvenienceAmt = "ConvenienceAmt";
		static final String CustCode = "CustCode";
		static final String CustomerID = "CustomerID";
		static final String CVPresence = "CVPresence";
		static final String Dental_Amount = "Dental_Amount";
		static final String EmvData = "EmvData";
		static final String EntryMode = "EntryMode";
		static final String ExternalIP = "ExternalIP";
		static final String Force = "Force";
		static final String IIAS_Indicator = "IIAS_Indicator";
		static final String Level3Amt = "Level3Amt";
		static final String PartialIndicator = "PartialIndicator";
		static final String PONum = "PONum";
		static final String QHP_Amount = "QHP_Amount";
		static final String RegisterNum = "RegisterNum";
		static final String RX_Amount = "RX_Amount";
		static final String SequenceNum = "SequenceNum";
		static final String SequenceCount = "SequenceCount";
		static final String ServerID = "ServerID";
		static final String Target = "Target";
		static final String TaxAmt = "TaxAmt";
		static final String Timeout = "Timeout";
		static final String TipAmt = "TipAmt";
		static final String TrainingMode = "TrainingMode";
		static final String TransactionID = "TransactionID";
		static final String Vision_Amount = "Vision_Amount";
		// Sub-containers
		static final String CustomFieldsClass = CustomFields.class.getSimpleName();
		//		protected static final String LineItemDetailClass = LinteItemDetail.class.getSimpleName();
		static final String P2PEClass = P2PE.class.getSimpleName();
		static final String PresentationClass = Presentation.class.getSimpleName();


		// CreditCardRequest.ExtDataExtData element definitions
		static {
			elements.put(AltMerchName, new DataElement(DataElement.TYPE_STRING, 50));
			elements.put(AltMerchAddr, new DataElement(DataElement.TYPE_STRING, 50));
			elements.put(AltMerchCity, new DataElement(DataElement.TYPE_STRING, 50));
			elements.put(AltMerchState, new DataElement(DataElement.TYPE_STRING, 2));
			elements.put(AltMerchZip, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(AuthCode, new DataElement(DataElement.TYPE_STRING, 20));
			elements.put(Authentication, new DataElement(DataElement.TYPE_STRING, 20));
			elements.put(BillPayment, new DataElement(DataElement.TYPE_BOOLEAN));
			elements.put(BillToState, new DataElement(DataElement.TYPE_STRING, 2));
			elements.put(BypassAvsCvv, new DataElement(DataElement.TYPE_STRING)); // enum
			elements.put(City, new DataElement(DataElement.TYPE_STRING, 25));
			elements.put(Clinical_Amount, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(ConvenienceAmt, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(CustCode, new DataElement(DataElement.TYPE_STRING, 20));
			elements.put(CustomerID, new DataElement(DataElement.TYPE_STRING, 15));
			elements.put(CVPresence, new DataElement(DataElement.TYPE_STRING, 1));
			elements.put(Dental_Amount, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(EmvData, new DataElement(DataElement.TYPE_STRING));
			elements.put(EntryMode, new DataElement(DataElement.TYPE_STRING));
			elements.put(ExternalIP, new DataElement(DataElement.TYPE_STRING, 15));
			elements.put(Force, new DataElement(DataElement.TYPE_BOOLEAN));
			elements.put(IIAS_Indicator, new DataElement(DataElement.TYPE_STRING, 1));
			elements.put(Level3Amt, new DataElement(DataElement.TYPE_STRING, 18));
//			elements.put(LineItemDetailClass, new DataElement());
			elements.put(PartialIndicator, new DataElement(DataElement.TYPE_BOOLEAN));
			elements.put(PONum, new DataElement(DataElement.TYPE_STRING, 20));
			elements.put(QHP_Amount, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(RegisterNum, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(RX_Amount, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(SequenceNum, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(SequenceCount, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(ServerID, new DataElement(DataElement.TYPE_STRING, 50));
			elements.put(Target, new DataElement(DataElement.TYPE_STRING, 50));
			elements.put(TaxAmt, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(Timeout, new DataElement(DataElement.TYPE_STRING, 10));
			elements.put(TipAmt, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(TrainingMode, new DataElement(DataElement.TYPE_BOOLEAN));
			elements.put(TransactionID, new DataElement(DataElement.TYPE_STRING, 50));
			elements.put(Vision_Amount, new DataElement(DataElement.TYPE_STRING, 18));
			elements.put(CustomFieldsClass, new DataElement());
			elements.put(P2PEClass, new DataElement());
			elements.put(PresentationClass, new DataElement());	// Nested class
		}

		@Override
		protected HashMap<String, DataElement> getElements() {
			return elements;
		}
	}

	/**
	 * Set element and filter subclasses
	 */
    private ExtData extData = null;
	public void setValue(String name, String value) {
		if (name.startsWith(ExtDataClass)) {
			if (extData == null) {
				extData = new ExtData();
				values.put(ExtDataClass, extData);
			}
			extData.setValue(name.substring(ExtDataClass.length()+1), value);
		} else {
			super.setValue(name, value);
		}
	}


	// CreditCardRequest element names
	public static final String UserName = "UserName";
	public static final String Password = "Password";
	public static final String TransType = "TransType";
	public static final String CardNum = "CardNum";
	public static final String ExpDate = "ExpDate";
	public static final String MagData = "MagData";
	public static final String AccessToken = "AccessToken";
	public static final String NameOnCard = "NameOnCard";
	public static final String Amount = "Amount";
	public static final String InvNum = "InvNum";
	public static final String PNRef = "PNRef";
	public static final String Zip = "Zip";
	public static final String Street = "Street";
	public static final String CVNum = "CVNum";

	// ExtData elements
	private static final String ExtDataClass = ExtData.class.getSimpleName();
	public static final String AltMerchName = ExtData.fqParam(ExtData.AltMerchName);
	public static final String AltMerchAddr = ExtData.fqParam(ExtData.AltMerchAddr);
	public static final String AltMerchCity = ExtData.fqParam(ExtData.AltMerchCity);
	public static final String AltMerchState = ExtData.fqParam(ExtData.AltMerchState);
	public static final String AltMerchZip = ExtData.fqParam(ExtData.AltMerchZip);
	public static final String AuthCode = ExtData.fqParam(ExtData.AuthCode);
	public static final String Authentication = ExtData.fqParam(ExtData.Authentication);
	public static final String BillPayment = ExtData.fqParam(ExtData.BillPayment);
	public static final String BillToState = ExtData.fqParam(ExtData.BillToState);
	public static final String BypassAvsCvv = ExtData.fqParam(ExtData.BypassAvsCvv);
	public static final String City = ExtData.fqParam(ExtData.City);
	public static final String Clinical_Amount = ExtData.fqParam(ExtData.Clinical_Amount);
	public static final String ConvenienceAmt = ExtData.fqParam(ExtData.ConvenienceAmt);
	public static final String CustCode = ExtData.fqParam(ExtData.CustCode);
	public static final String CustomerID = ExtData.fqParam(ExtData.CustomerID);
	public static final String CVPresence = ExtData.fqParam(ExtData.CVPresence);
	public static final String Dental_Amount = ExtData.fqParam(ExtData.Dental_Amount);
	public static final String EmvData = ExtData.fqParam(ExtData.EmvData);
	public static final String EntryMode = ExtData.fqParam(ExtData.EntryMode);
	public static final String ExternalIP = ExtData.fqParam(ExtData.ExternalIP);
	public static final String Force = ExtData.fqParam(ExtData.Force);
	public static final String IIAS_Indicator = ExtData.fqParam(ExtData.IIAS_Indicator);
	public static final String Level3Amt = ExtData.fqParam(ExtData.Level3Amt);
	public static final String PartialIndicator = ExtData.fqParam(ExtData.PartialIndicator);
	public static final String PONum = ExtData.fqParam(ExtData.PONum);
	public static final String QHP_Amount = ExtData.fqParam(ExtData.QHP_Amount);
	public static final String RegisterNum = ExtData.fqParam(ExtData.RegisterNum);
	public static final String RX_Amount = ExtData.fqParam(ExtData.RX_Amount);
	public static final String SequenceNum = ExtData.fqParam(ExtData.SequenceNum);
	public static final String SequenceCount = ExtData.fqParam(ExtData.SequenceCount);
	public static final String ServerID = ExtData.fqParam(ExtData.ServerID);
	public static final String Target = ExtData.fqParam(ExtData.Target);
	public static final String TaxAmt = ExtData.fqParam(ExtData.TaxAmt);
	public static final String Timeout = ExtData.fqParam(ExtData.Timeout);
	public static final String TipAmt = ExtData.fqParam(ExtData.TipAmt);
	public static final String TrainingMode = ExtData.fqParam(ExtData.TrainingMode);
	public static final String TransactionID = ExtData.fqParam(ExtData.TransactionID);
	public static final String Vision_Amount = ExtData.fqParam(ExtData.Vision_Amount);
	// ExtData.P2PE elements

	public static final String HSMDevice = ExtData.fqParam(ExtData.P2PE.fqParam(ExtData.P2PE.HSMDevice));
	public static final String TerminalType = ExtData.fqParam(ExtData.P2PE.fqParam(ExtData.P2PE.TerminalType));
	public static final String EncryptionType = ExtData.fqParam(ExtData.P2PE.fqParam(ExtData.P2PE.EncryptionType));
	public static final String KSN  = ExtData.fqParam(ExtData.P2PE.fqParam(ExtData.P2PE.KSN));
	public static final String DataBlock  = ExtData.fqParam(ExtData.P2PE.fqParam(ExtData.P2PE.DataBlock));
	// ExtData.Presentation elements
	public static final String CardPresent = ExtData.fqParam(ExtData.Presentation.fqParam(ExtData.Presentation.CardPresent));
	// ExtData.CustomFields elements
	public static final String CustomFields = ExtData.fqParam(ExtData.CustomFieldsClass);

	// CreditCardRequest element definitions
	static {
		elements.put(UserName, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(AccessToken, new DataElement(DataElement.TYPE_STRING, 1000000));
		elements.put(Password, new DataElement(DataElement.TYPE_STRING, 20));
		elements.put(TransType, new DataElement(DataElement.TYPE_STRING));
		elements.put(CardNum, new DataElement(DataElement.TYPE_STRING, 19));
		elements.put(ExpDate, new DataElement(DataElement.TYPE_STRING, 4));
		elements.put(MagData, new DataElement(DataElement.TYPE_STRING));
		elements.put(NameOnCard, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(Amount, new DataElement(DataElement.TYPE_STRING, 18));
		elements.put(InvNum, new DataElement(DataElement.TYPE_STRING, 20));
		elements.put(PNRef, new DataElement(DataElement.TYPE_STRING, 10));
		elements.put(Zip, new DataElement(DataElement.TYPE_STRING, 10));
		elements.put(Street, new DataElement(DataElement.TYPE_STRING, 25));
		elements.put(CVNum, new DataElement(DataElement.TYPE_STRING, 4));
		elements.put(ExtDataClass, new DataElement());	// Nested class
	}

	@Override
	protected HashMap<String, DataElement> getElements() {
		return elements;
	}
	
	/**
	 * Send request, populate response
	 * 
	 * @param response responseT object to populate
	 */
	@Override
	protected void send(CreditCardResponse response) {
		try {
			URL url = new URL(endPoint);
			send(url, soapAction, response);
			response.parseSubClasses();
		} catch (Exception e) {
			response.setException(e);
		}
	}

}
