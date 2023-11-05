package com.ayush.soapdemo.controller;

import com.ayush.soapdemo.dto.SoapRequest;
import com.ayush.soapdemo.service.SoapService;
import com.ayush.soapdemo.service.WsdlMockService;
import com.ayush.soapdemo.util.SoapConnectionUtil;
import jakarta.xml.soap.SOAPException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
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
}
