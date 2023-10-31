package com.ayush.soapdemo.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SoapRequest {
    private String serviceName;
}
