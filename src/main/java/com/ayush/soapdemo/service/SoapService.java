package com.ayush.soapdemo.service;

import com.ayush.soapdemo.util.SoapConnectionUtil;
import jakarta.xml.soap.SOAPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

import java.io.IOException;
import java.util.Map;

@Service
public class SoapService {
    @Autowired
    private SoapConnectionUtil soapConnectionUtil;



    public SoapService(SoapConnectionUtil soapConnectionUtil) {
        this.soapConnectionUtil = soapConnectionUtil;
    }

    public String sendSoapRequest(String endpoint, MultipartFile xmlFile) throws SOAPException, IOException {
        return soapConnectionUtil.sendSoapRequest(endpoint, xmlFile);
    }

    public Map<String,String> sendSoapRequestAndGetMap(String endpoint, MultipartFile xmlFile) throws SOAPException, IOException {
        return soapConnectionUtil.sendSoapRequestAndGetMap(endpoint,xmlFile);
    }
}
