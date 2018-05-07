package com.directconnect.mobilesdk.transaction;

import android.util.Log;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.reflect.ParameterizedType;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * SendableRequest class - works with a generic Response class
 * 
 * @author Francois Bergeon
 */
public abstract class SendableRequest<responseT extends Response> extends Request {
    private static final String TAG = "SendableRequest";
    private static final String nameSpaceURI = "http://TPISoft.com/SmartPayments/";
	private int timeout;
	/**
	 * Send request, populate response - to be implemented by derived classes
	 * 
	 * @param response responseT object to populate
	 */
	protected abstract void send(responseT response);

	/*
	 *  Process request then send via derived class, invoke listener delegate to receive response.
	 *  If the listener delegate is null the request is processed but no response is returned.
	 *
	 *  @returns null
	 */
	public interface Listener<responseT extends Response> extends AsyncRequest.Listener<responseT> {}

	@SuppressWarnings("unchecked")
	public void process(Listener<responseT> listener) {
        if (listener == null)
            throw new IllegalArgumentException();
        AsyncRequest<SendableRequest<responseT>, responseT> req = new AsyncRequest<>(listener);
        req.execute(this);
 	}


	/*
	 *  Process request then send via derived class, return response as specified class
	 *  
	 *  @returns Response object
	 */
	public responseT process() {
		return process(0);
	}

	public responseT process(int timeout) {
		this.timeout = timeout;
		try {
		    // Instantiate response of specified parameter class
			ParameterizedType t = (ParameterizedType)this.getClass().getGenericSuperclass();
			Class<?> c = (Class<?>)t.getActualTypeArguments()[0];
			@SuppressWarnings("unchecked")
			responseT response = (responseT)c.newInstance();
	
			// Process request
		    {}
	
		    // Send request by calling derived class method
		    send(response);
		    return response;
		} catch (Exception e) {
//			e.printStackTrace();
			return null;		
		}
	}
	
	
	/**
	 * Send NVP - Process request in NVP format
	 * (not fully tested, switched to SOAP)
	 * 
	 * @param url URL
	 * @param response Response object
	 * @throws IOException
	 */
	/*
	protected void sendNVP(URL url, Response response) throws IOException  {
		String request = serializeNvp();
		System.out.write(request.getBytes());
		
	    // Send data
	    HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
	    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	    conn.setRequestProperty ("User-Agent", "DC Gateway Mobile SDK v1.0");
	    conn.setDoOutput(true);
	    OutputStream out = conn.getOutputStream();
	    out.write(request.getBytes());
	    out.flush();

	    // Get the response
	    InputStream in = conn.getInputStream();
	    response.parse(in);

        in.close();
        out.close();
	}
	*/
	
	/**
	 * Send request using SOAP client classes
	 * 
	 * @param url	Endpoint URL
	 * @param soapAction SOAPActon
	 * @param response Response object to populate
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException 
	 * @throws DOMException 
	 */
	/*
	 * 	protected void processSOAP(URL url, String soapAction, Response response) throws IOException, SOAPException, ParserConfigurationException, DOMException, TransformerException  {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();

        // Generate SOAP body from DataCollection
		Document doc = createDocument(nameSpaceURI);
        SOAPBody reqBody = envelope.getBody();
        reqBody.addDocument(doc);
        
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.setHeader("SOAPAction", soapAction);

        soapMessage.saveChanges();
        
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

        soapMessage.writeTo(System.out);
        System.out.write('\n');
       
        // Send SOAP Message to SOAP Server
        SOAPMessage soapResponse = soapConnection.call(soapMessage, url);
        soapConnection.close();

        // print SOAP Response
        soapResponse.writeTo(System.out);
        System.out.write('\n');

        // Parse response
        SOAPBody respBody = soapResponse.getSOAPBody();
        response.parse(respBody);
	}
	 */

	/**
	 * Send request in SOAP format with manual interaction
	 * 
	 * @param url URL of SOAP endpoint
	 * @param soapAction SOAP action
	 * @param response responseT object to populate
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws TransformerException 
	 * @throws DOMException 
	 */
	protected void send(URL url, String soapAction, responseT response) throws IOException, ParserConfigurationException, DOMException, TransformerException  {
        Log.d(TAG, "send");

/*
		IdentityApi apiInstance = new IdentityApi();
		Log.d(TAG, "Afteridentity");
		IdentityResponse results = new IdentityResponse();
		Log.d(TAG, "results");
		ApplicationUserModel applicationUser = new ApplicationUserModel(); // ApplicationUserModel | User Credentials to generate the token.
		applicationUser.setUsername("MobileSDK");
		applicationUser.setPassword("7TM5EAwC");
		applicationUser.setGatewayId("46");
		Log.d(TAG, "AFTER application: ");
		try {
			results = apiInstance.v1IdentityPost(applicationUser);
			System.out.println(results);
			Log.d(TAG, "BEFORE application: ");
		} catch (ApiException e) {
			System.err.println("Exception when calling IdentityApi#v1IdentityPost");
			e.printStackTrace();
		}
*/
        /*
        Request SOAP Message:
        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns="http://TPISoft.com/SmartPayments/">
        	<SOAP-ENV:Header/>
        	<SOAP-ENV:Body>
        		<ProcessCreditCard>
        			<Amount>100</Amount>
        			<CardNum>1234567890123456</CardNum>
        		</ProcessCreditCard>
        	</SOAP-ENV:Body>
        </SOAP-ENV:Envelope>
         */

        // Generate SOAP body from DataCollection
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		Element soapEnvelope = doc.createElementNS("http://schemas.xmlsoap.org/soap/envelope/", "soap:Envelope");
		Element soapBody = doc.createElement("soap:Body");
		Element root = doc.createElementNS(nameSpaceURI, getName());
		populateRootElement(root);

		// Build document
		soapBody.appendChild(root);
		soapEnvelope.appendChild(soapBody);
		doc.appendChild(soapEnvelope);
	    String xmlRequest = getOuterXml(new DOMSource(doc));
		
	    // Open connection
	    HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
	    conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
	    conn.setRequestProperty ("User-Agent", "DC Gateway MobileSDK Android v1.0");
	    conn.setRequestProperty ("SOAPAction", soapAction);
		conn.setConnectTimeout(timeout*1000);
	    conn.setDoOutput(true);
	    OutputStream out = conn.getOutputStream();
	    
	    // Send data
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
//		StringWriter writer = new StringWriter();
//		StreamResult result = new StreamResult(writer);
		StreamResult result = new StreamResult(out);
		transformer.transform(new DOMSource(doc), result);
	    out.flush();

		StringWriter writer = new StringWriter();
		result = new StreamResult(writer);
		transformer.transform(new DOMSource(doc), result);
		String xml = writer.toString();
        Log.d(TAG, "send: " + xml);

	    // Parse response
	    InputStream in = conn.getInputStream();
	    response.parse(in);

        in.close();
        out.close();
	}		
}
