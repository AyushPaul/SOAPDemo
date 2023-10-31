package com.ayush.soapdemo.util;


import jakarta.xml.soap.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Component
public class SoapConnectionUtil {

    private final SaajSoapMessageFactory messageFactory;

    public SoapConnectionUtil(SaajSoapMessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public String sendSoapRequest(String endpoint, MultipartFile xmlFile) throws SOAPException, IOException {

        String requestXml = new String(xmlFile.getBytes());
        String response = sendSoapRequestToService(requestXml,endpoint);
//        SOAPMessage soapRequest = createSoapMessage(requestXml);
//
//        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
//        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//        MimeHeaders headers = soapRequest.getMimeHeaders();
//        headers.addHeader("Content-Type", "text/xml");
//        SOAPMessage soapResponse = soapConnection.call(soapRequest, endpoint);
//
//        soapConnection.close();

//        return getSoapResponseAsString(soapResponse);
        return response;
    }

    public Map<String,String> sendSoapRequestAndGetMap(String endpoint, MultipartFile xmlFile){
        try {
            String requestXml = new String(xmlFile.getBytes());
            String response = sendSoapRequestToService(requestXml,endpoint);
            Map<String,String> map = extractResponseData(response);
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private SOAPMessage createSoapMessage(String requestXml) throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        SOAPPart soapPart = soapMessage.getSOAPPart();
        soapPart.setContent(new StreamSource(new java.io.StringReader(requestXml)));

        return soapMessage;
    }

    private String getSoapResponseAsString(SOAPMessage soapMessage) throws SOAPException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);

        return new String(outputStream.toByteArray());
    }

    private String sendSoapRequestToService(String requestXml,String endpointUrl) {
        try {
            // Create a SOAPMessage from the requestXml
            MessageFactory messageFactory = MessageFactory.newInstance();
            SOAPMessage soapRequest = messageFactory.createMessage(null, new ByteArrayInputStream(requestXml.getBytes()));

            // Create a SOAPConnectionFactory and SOAPConnection
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection soapConnection = soapConnectionFactory.createConnection();
//            MimeHeaders headers = soapRequest.getMimeHeaders();
//            headers.addHeader("Content-Type", "text/xml");
            // Define the SOAP endpoint URL
            //String endpointUrl = "http://example.com/your-soap-endpoint"; // Replace with the actual endpoint URL

            // Send the SOAP request and receive the response
            SOAPMessage soapResponse = soapConnection.call(soapRequest, endpointUrl);

            // Process the SOAP response
            String responseXml = getSoapResponseAsString(soapResponse);

            // Close the SOAP connection
            soapConnection.close();

            return responseXml;
        } catch (SOAPException | IOException e) {
            e.printStackTrace(); // Handle exceptions appropriately
            return "Error in sending SOAP request: " + e.getMessage();
        }
    }

    private Map<String, String> extractResponseData(String responseXml) {
        try {
            SOAPMessage soapResponse = MessageFactory.newInstance().createMessage(null, new ByteArrayInputStream(responseXml.getBytes()));
            SOAPBody soapBody = soapResponse.getSOAPBody();

            // Create a Map to store the response data
            Map<String, String> responseMap = new HashMap<>();

            // Iterate through child elements of the SOAPBody
            Iterator<?> childElements = soapBody.getChildElements();
            while (childElements.hasNext()) {
                Object element = childElements.next();
                if (element instanceof SOAPElement) {
                    SOAPElement soapElement = (SOAPElement) element;
                    String value = soapElement.getTextContent();
                    // Remove leading and trailing white spaces and unescape XML entities
                    value = StringEscapeUtils.unescapeXml(value.trim());
                    responseMap.put(soapElement.getElementName().getLocalName(), value);
                }
            }

            return responseMap;
        } catch (SOAPException | IOException e) {
            e.printStackTrace(); // Handle exceptions as needed
            return new HashMap<>();
        }
    }
}
