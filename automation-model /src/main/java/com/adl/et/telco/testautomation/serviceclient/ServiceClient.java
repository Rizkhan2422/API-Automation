package com.adl.et.telco.testautomation.serviceclient;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONArray;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;

public class ServiceClient {

    private final RequestSpecBuilder requestSpecBuilder;
    private String baseUri;
    private String basePath;


    private static final boolean IS_DEBUG_ENABLED = true;


    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public ServiceClient(boolean isLogEnable) {
        requestSpecBuilder = new RequestSpecBuilder();
        if (isLogEnable){
            // to be implemented
        }
    }

    public ServiceClient(String baseUri, String basePath, boolean isLogEnable) {
        this(isLogEnable);
        this.baseUri = baseUri;
        this.basePath = basePath;
    }
    
    public ServiceClient(String baseUri, String basePath) {
        this(false);
        this.baseUri = baseUri;
        this.basePath = basePath;
    }

    /**
     * This method is used to POST REST request to server
     *
     * @param response
     */

    public int getResponseCode(Response response) {
        return response.getStatusCode();
    }

    public Response get(RequestSpecification requestSpecification, String path) {
        Response response = requestSpec(requestSpecification)
                .get(path);
        logResponse(response);
        return response;
    }
    
    public Response get(RequestSpecification requestSpecification) {
        return get(requestSpecification,"");
    }

    public Response post(RequestSpecification requestSpecification) {
        Response response = requestSpec(requestSpecification)
                .post();
        logResponse(response);
        return response;
    }

    public Response delete(RequestSpecification requestSpecification) {
        Response response = requestSpec(requestSpecification)
                .delete();
        logResponse(response);
        return response;
    }

    public Response patch(RequestSpecification requestSpecification, String path) {
        Response response = requestSpec(requestSpecification)
                .patch(path);
        logResponse(response);
        return response;
    }

    public Response put(RequestSpecification requestSpecification, String path) {
        Response response = requestSpec(requestSpecification)
                .put(path);
        logResponse(response);
        return response;
    }

    private RequestSpecification requestSpec(RequestSpecification requestSpecification) {
        RequestSpecification tempRequestSpecification = requestSpecBuilder.build()
                .spec(requestSpecification)
                .baseUri(baseUri)
                .basePath(basePath);
        if(IS_DEBUG_ENABLED) {
        	tempRequestSpecification.when().log().all();
        }
        return given().spec(tempRequestSpecification);
    }

    public Response get(String path) {
        return given()
                .baseUri(baseUri).basePath(basePath)
                .when().get(path);
    }

    public static JSONObject getResponseBodyAsJsonObject(Response response) {
        return new JSONObject(response.body().asString());
    }

    public static JSONArray getResponseBodyAsJsonArray(Response response) {
        return new JSONArray(response.body().asString());
    }
    
    /**
     * Log the response
     * 
     * @param response
     */
    private void logResponse(Response response) {
    	if(IS_DEBUG_ENABLED) {
        	response.then().log().all();
        }
    }
}

