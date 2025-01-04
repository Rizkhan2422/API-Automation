package com.adl.et.telco.testautomation.dto;

import com.adl.et.telco.testautomation.statics.ConfigConstants.RequestType;
import lombok.Data;

@Data
public class CommonAPIRequest {
    String[] headers;
    String[] headerValues;
    String[] pathParams;
    String[] pathParamValues;
    String[] queryParams;
    String[] queryParamValues;
    String requestBodyPath;
    RequestType requestType;
    String apiBasePath;
    String apiBaseUri;
}
