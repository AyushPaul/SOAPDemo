package com.ayush.soapdemo.controller;

import com.ayush.soapdemo.dto.SoapRequest;
import com.ayush.soapdemo.service.SoapService;
import com.ayush.soapdemo.util.SoapConnectionUtil;
import jakarta.xml.soap.SOAPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/soap")
public class SoapController {
    @Autowired
    private SoapService soapService;
    @Autowired
    private SoapConnectionUtil soapUtil;



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
}
