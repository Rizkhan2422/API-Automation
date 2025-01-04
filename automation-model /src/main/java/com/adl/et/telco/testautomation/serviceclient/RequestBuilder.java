package com.adl.et.telco.testautomation.serviceclient;

import com.adl.et.telco.testautomation.utils.FileHelper;
import com.google.gson.JsonObject;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.internal.AuthenticationSpecificationImpl;
import io.restassured.specification.AuthenticationSpecification;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

public class RequestBuilder {
    private RequestSpecBuilder requestSpecBuilder;
    private AuthenticationSpecification authenticationSpecification;
    private RequestSpecification authRequestSpecification;

    public RequestBuilder(){
        requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.addFilter(new AllureRestAssured());
        authenticationSpecification = new AuthenticationSpecificationImpl(requestSpecBuilder.build());
    }

    public RequestBuilder setBasicAuth(String userName, String password){
        authRequestSpecification = authenticationSpecification.basic(userName,password);
        return this;
    }
    
    public RequestBuilder setAllureReport() {
    	requestSpecBuilder.addFilter(new AllureRestAssured());
    	return this;
    }

    public RequestBuilder setContentType(ContentType contentType){
        requestSpecBuilder.setContentType(contentType);
        return this;
    }

    public RequestBuilder setAccept(ContentType contentType){
        requestSpecBuilder.setAccept(contentType.getAcceptHeader());
        return this;
    }

    public RequestBuilder addHeader(String key, String value){
        requestSpecBuilder.addHeader(key,value);
        return  this;
    }

    public RequestBuilder addHeader(Map<String,String> headers){
        requestSpecBuilder.addHeaders(headers);
        return this;
    }

    public RequestBuilder addQueryParam(String paramName, Object object){
        requestSpecBuilder.addQueryParam(paramName,object);
        return this;
    }

    public RequestBuilder setRequestBody(String body){
        requestSpecBuilder.setBody(body);
        return this;
    }
    
    public RequestBuilder setRequestBody(JsonObject jsonObject){
        requestSpecBuilder.setBody(jsonObject);
        return this;
    }

    public RequestBuilder setRequestBodyFromFile(String filePath)  {
        String fileContent = FileHelper.getStringFromFile(filePath);
        requestSpecBuilder.setBody(fileContent);
        return this;
    }

    public RequestBuilder addQueryParams(Map<String,Object> params){
        requestSpecBuilder.addQueryParams(params);
        return this;
    }

    public RequestBuilder addPathParam(String paramName, String value){
        requestSpecBuilder.addPathParam(paramName, value);
        return this;
    }

    public RequestSpecification build(){
        if(authRequestSpecification != null){
            return requestSpecBuilder.build().spec(authRequestSpecification);
        }
        else{
            return requestSpecBuilder.build();
        }

    }
}
