package com.ayush.soapdemo.service;

import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.wsdl.*;
import javax.wsdl.factory.WSDLFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;


@Service
@Slf4j
public class WsdlMockService {

    public String generateMockResponse(String wsdlContent, String serviceName, String operationName) throws Exception {
        try {
            // Parse the WSDL
            log.info("Service (28) : " + "Service Started");
            Definition definition = WSDLFactory.newInstance().newWSDLReader().readWSDL(null, wsdlContent);
            log.info("Service (30) : " + definition);
            // Find the PortType by service name
            PortType portType = findPortType(definition, serviceName);
            log.info("Service (33) : " + portType);
            if (portType == null) {
                throw new Exception("Service not found in WSDL: " + serviceName);
            }

            // Find the binding for the PortType
            //Binding binding = portType.getBinding();
            Binding binding = findBindingForPortType(definition, portType);

            // Find the operation by its name
            //String operationName = "yourOperationName"; // Replace with the actual operation name
            Operation operation = findOperation(binding, operationName);

            if (operation == null) {
                throw new Exception("Operation not found in WSDL: " + operationName);
            }

            // Create a mock response message based on the operation
            Message responseMessage = operation.getOutput().getMessage();
            log.info("Service(54) : " + responseMessage);
            SOAPMessage mockResponse = createMockResponseMessage(responseMessage);
            // Convert the response to a String
            return soapMessageToString(mockResponse);
        } catch (WSDLException e) {
            throw new Exception("Error parsing WSDL: " + e.getMessage());
        }
    }

    public String generateMockResponse(URL wsdlUrl, String serviceName, String operationName) throws Exception {
        try {
            // Parse the WSDL
            log.info("Service (65) : " + "Service Started");
            WSDLFactory factory = WSDLFactory.newInstance();
            Definition definition = factory.newWSDLReader().readWSDL(wsdlUrl.toString());
            log.info("Service (68) : " + definition);
            // Find the PortType by service name
            PortType portType = findPortType(definition, serviceName);
            log.info("Service (71) : " + portType);
            if (portType == null) {
                throw new Exception("Service not found in WSDL: " + serviceName);
            }

            // Find the binding for the PortType
            //Binding binding = portType.getBinding();
            Binding binding = findBindingForPortType(definition, portType);

            // Find the operation by its name
            //String operationName = "yourOperationName"; // Replace with the actual operation name
            Operation operation = findOperation(binding, operationName);

            if (operation == null) {
                throw new Exception("Operation not found in WSDL: " + operationName);
            }
            log.info("Service(87) : " + operation);
            log.info("Service(88) : " + operation.getOutput());
            // Create a mock response message based on the operation
            Message responseMessage = operation.getOutput().getMessage();
            log.info("Service(91) : " + responseMessage);
            SOAPMessage mockResponse = createMockResponseMessage(responseMessage);

            // Convert the response to a String
            return soapMessageToString(mockResponse);
        } catch (WSDLException e) {
            throw new Exception("Error parsing WSDL: " + e.getMessage());
        }
    }

    private Binding findBindingForPortType(Definition definition, PortType portType) {
        Iterator<Binding> bindings = definition.getBindings().values().iterator();
        while (bindings.hasNext()) {
            Binding binding = bindings.next();
            QName qName = binding.getPortType().getQName();
            log.info("findBindingForPortType : " + qName);
            if (qName.equals(portType.getQName())) {
                return binding;
            }
        }
        return null;
    }

    private PortType findPortType(Definition definition, String serviceName) {
        Iterator<PortType> portTypes = definition.getAllPortTypes().values().iterator();
        while (portTypes.hasNext()) {
            PortType portType = portTypes.next();
            QName qName = portType.getQName();
            log.info("findPortType : " + qName);
            if (qName.getLocalPart().equals(serviceName)) {
                return portType;
            }
        }
        return null;
    }

    private Operation findOperation(Binding binding, String operationName) {
        for (Object bindingOperationObj : binding.getBindingOperations()) {
            BindingOperation bindingOperation = (BindingOperation) bindingOperationObj;
            if (bindingOperation.getName().equals(operationName)) {
                return bindingOperation.getOperation();
            }
        }
        return null;
    }

    private SOAPMessage createMockResponseMessage(Message message) throws Exception {
        // Create a mock response SOAP message
        SOAPMessage responseMessage = MessageFactory.newInstance().createMessage();
        log.info("Service(140) : " + message.getParts().values().iterator().next());
        Part part = (Part) message.getParts().values().iterator().next();
        String elementName = String.valueOf(part.getElementName());
        if(elementName.contains("}")){
            elementName = elementName.substring(elementName.indexOf('}')+1);
        }
        log.info("Service(146) : " + part.getElementName());
        log.info("Service(147) : " + elementName);
        String responseContent = "<" + elementName + ">MockResponse</" + elementName + ">";
        responseMessage.getSOAPBody().addDocument(parseXML(responseContent));
        return responseMessage;
    }

    private org.w3c.dom.Document parseXML(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
    }

    private String soapMessageToString(SOAPMessage soapMessage) throws Exception {
        StringWriter sw = new StringWriter();
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(soapMessage.getSOAPPart().getContent(), new StreamResult(sw));
        return sw.toString();
    }

}
