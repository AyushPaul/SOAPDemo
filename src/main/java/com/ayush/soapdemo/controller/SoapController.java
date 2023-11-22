package com.ayush.soapdemo.controller;

import com.ayush.soapdemo.dto.SoapRequest;
import com.ayush.soapdemo.service.SoapService;
import com.ayush.soapdemo.service.WsdlMockService;
import com.ayush.soapdemo.util.SoapConnectionUtil;
import jakarta.xml.soap.SOAPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/soap")
@Slf4j
public class SoapController {
    @Autowired
    private SoapService soapService;
    @Autowired
    private SoapConnectionUtil soapUtil;

    @Autowired
    private WsdlMockService wsdlMockService;



    @PostMapping("/send")
    public String sendSoapRequest(@RequestParam("serviceName") String serviceName, @RequestParam("file")MultipartFile xmlFile) throws IOException, SOAPException {
        //String dynamicRequest = soapUtil.sendSoapRequest(request.getServiceName(), request.getRequestXml());
        return soapService.sendSoapRequest(serviceName, xmlFile);
    }

    @PostMapping("/sendMap")
    public Map<String,String> sendSoapRequestAndGetMap(@RequestParam("serviceName") String serviceName, @RequestParam("file")MultipartFile xmlFile) throws IOException, SOAPException {
        //String dynamicRequest = soapUtil.sendSoapRequest(request.getServiceName(), request.getRequestXml());
        return soapService.sendSoapRequestAndGetMap(serviceName,xmlFile);
    }

    @PostMapping("/mock")
    public String generateMockResponse(
            @RequestParam("serviceName") String serviceName,
            @RequestParam("operationName") String operationName,
            @RequestParam("file") MultipartFile wsdlFile) {
        log.info("Controller :" + "Process Started");

        try {
            String wsdlContent = new String(wsdlFile.getBytes());
            log.info("Controller (51) :" + wsdlContent);
            String mockResponse = wsdlMockService.generateMockResponse(wsdlContent, serviceName,operationName);
            log.info("Controller (53)" + mockResponse);
            return mockResponse;
        } catch (IOException e) {
            return "Error reading the uploaded WSDL file: " + e.getMessage();
        } catch (Exception e) {
            return "Error generating mock response: " + e.getMessage();
        }
    }

    @PostMapping("/mockUrl")
    public String generateMockResponseFromUrl(
            @RequestParam("serviceName") String serviceName,
            @RequestParam("operationName") String operationName,
            @RequestParam("file") String url) {
        log.info("Controller :" + "Process Started");

        try {
            URL wsdlUrl = new URL(url);
            //String wsdlContent = new String(wsdlFile.getBytes());
            log.info("Controller (73) :" + wsdlUrl);
            String mockResponse = wsdlMockService.generateMockResponse(wsdlUrl, serviceName,operationName);
            log.info("Controller (75)" + mockResponse);
            return mockResponse;
        } catch (IOException e) {
            return "Error reading the uploaded WSDL file: " + e.getMessage();
        } catch (Exception e) {
            return "Error generating mock response: " + e.getMessage();
        }
    }

    @PostMapping(value = "/parseXml")
    public Map<String, Object> parseXml(
            @RequestParam("xmlData") String xmlData,
            @RequestParam("parameters") List<String> parameters) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Create a DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML string
            Document document = builder.parse(new ByteArrayInputStream(xmlData.getBytes()));

            // Traverse the XML document and filter specified tags
            NodeList childNodes = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                traverseXml(childNodes.item(i), parameters, result);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Handle parsing errors appropriately
        }

        return result;
    }

    private void traverseXml(Node node, List<String> parameters, Map<String, Object> result) {
        // Process only elements (ignore text nodes, comments, etc.)
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            // If the tag is in the specified parameters or it is a direct child of one of the parameters,
            // add it to the result map
            if (parameters.contains(node.getNodeName()) || parameters.contains(node.getParentNode().getNodeName())) {
                // If the tag has child nodes, recursively process them
                NodeList childNodes = node.getChildNodes();
                log.info("line 127 : " + node.getNodeName() );
                log.info("line 128 : " + childNodes.getLength() );
                if (childNodes.getLength() > 1) {
                    Map<String, Object> nestedResult = new HashMap<>();
                    for (int i = 0; i < childNodes.getLength(); i++) {
                        traverseXml(childNodes.item(i), parameters, nestedResult);
                    }
                    result.put(node.getNodeName(), nestedResult);
                } else {
                    log.info("line 135 : " + node.getTextContent().trim());
                    // If the tag has no child nodes, add its text content to the result map
                    result.put(node.getNodeName(), node.getTextContent().trim());
                }
            } else {
                // If the tag is not in the specified parameters, recursively process child nodes
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    traverseXml(childNodes.item(i), parameters, result);
                }
            }
        }
    }
}
