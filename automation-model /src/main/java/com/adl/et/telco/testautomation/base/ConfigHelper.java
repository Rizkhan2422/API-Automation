package com.adl.et.telco.testautomation.base;

import com.adl.et.telco.testautomation.dto.CommonAPIRequest;
import com.adl.et.telco.testautomation.serviceclient.RequestBuilder;
import com.adl.et.telco.testautomation.serviceclient.ServiceClient;
import com.adl.et.telco.testautomation.statics.ConfigConstants;
import com.adl.et.telco.testautomation.utils.FileHelper;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidationException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.DocumentCallbackHandler;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testng.asserts.SoftAssert;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.adl.et.telco.testautomation.statics.ConfigConstants.*;
import static io.restassured.RestAssured.requestSpecification;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public interface ConfigHelper {

    Logger logger = LoggerFactory.getLogger(ConfigHelper.class);

    JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.newBuilder()
            .setValidationConfiguration(
                    ValidationConfiguration.newBuilder()
                            .setDefaultVersion(SchemaVersion.DRAFTV4)
                            .freeze()).freeze();

    default void setQueryParams(RequestBuilder requestBuilder, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.QUERY_PARAMS))) {
            String[] params = testData.get(ConfigConstants.QUERY_PARAMS).split(",");
            String[] values = testData.get(ConfigConstants.QUERY_PARAM_VALUES).split(",");
            for (int i = 0; i < params.length; i++) {
                requestBuilder.addQueryParam(params[i], values[i]);
            }
        }
    }

    default void setQueryParams(CommonAPIRequest commonAPIRequest, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.QUERY_PARAMS_2))) {
            commonAPIRequest.setQueryParams(testData.get(ConfigConstants.QUERY_PARAMS_2).split(","));
            commonAPIRequest.setQueryParamValues(testData.get(ConfigConstants.QUERY_PARAM_VALUES_2).split(","));
        } else {
            commonAPIRequest.setQueryParams(FIELD_ONLY_QUALIFY.split(","));
            commonAPIRequest.setQueryParamValues("false".split(","));
        }
    }

    default void setQueryParams(RequestBuilder requestBuilder, CommonAPIRequest request) {
        if (Objects.nonNull(request.getQueryParams())) {
            String[] params = request.getQueryParams();
            String[] values = request.getQueryParamValues();
            for (int i = 0; i < params.length; i++) {
                requestBuilder.addQueryParam(params[i], values[i]);
            }
        }
    }

    default String setSqlParamArray(String sql, Object... sqlDynamicParam) {

        logger.debug("Query = {} " ,sql);

        String str = String.format(sql, sqlDynamicParam);

        logger.debug("formatted query = {}",str);

        return str;
    }

    default void setPathParams(RequestBuilder requestBuilder, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.PATH_PARAMS))) {
            String[] params = testData.get(ConfigConstants.PATH_PARAMS).split(",");
            String[] values = testData.get(ConfigConstants.PATH_PARAM_VALUES).split(",");
            for (int i = 0; i < params.length; i++) {
                requestBuilder.addPathParam(params[i], values[i]);
            }
        }
    }

    default void setPathParams(CommonAPIRequest commonAPIRequest, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(PATH_PARAMS_2))) {
            commonAPIRequest.setPathParams(testData.get(PATH_PARAMS_2).split(","));
            commonAPIRequest.setPathParamValues(testData.get(PATH_PARAM_VALUES_2).split(","));
        }
    }

    default void setRequestBodyPath(CommonAPIRequest commonAPIRequest, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(EXTERNAL_API_JSON_NAME))) {
            commonAPIRequest.setRequestBodyPath(testData.get(REQUEST_BODY_FILE_PATH) + testData.get(EXTERNAL_API_JSON_NAME));
        }
    }

    default void setPathParams(RequestBuilder requestBuilder, CommonAPIRequest request) {
        if (Objects.nonNull(request.getPathParams())) {
            String[] params = request.getPathParams();
            String[] values = request.getPathParamValues();
            for (int i = 0; i < params.length; i++) {
                requestBuilder.addPathParam(params[i], values[i]);
            }
        }
    }

    default void setHeaders(RequestBuilder requestBuilder, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.HEADERS))) {
            String[] headers = testData.get(ConfigConstants.HEADERS).split(",");
            String[] values = testData.get(ConfigConstants.HEADER_VALUES).split(",");
            for (int i = 0; i < headers.length; i++) {
                requestBuilder.addHeader(headers[i], values[i]);
            }
        }
    }

    default void setHeaders(RequestBuilder requestBuilder, CommonAPIRequest request) {
        if (Objects.nonNull(request.getHeaders())) {
            String[] headers = request.getHeaders();
            String[] values = request.getHeaderValues();
            for (int i = 0; i < headers.length; i++) {
                requestBuilder.addHeader(headers[i], values[i]);
            }
        }
    }

    default String getRequestBody(Map<String, String> testData) {
        if (Objects.isNull(testData.get(ConfigConstants.REQUEST_BODY_JSON_NAME))) return null;
        String requestBody = getJsonStringFromJsonFile(testData.get(ConfigConstants.REQUEST_BODY_FILE_PATH).concat(testData.get(ConfigConstants.REQUEST_BODY_JSON_NAME)));
        if (Objects.nonNull(testData.get(ConfigConstants.BODY_OVERRIDE_FIELDS))
                && Objects.nonNull(testData.get(ConfigConstants.BODY_OVERRIDE_VALUES))) {
            String[] fields = testData.get(ConfigConstants.BODY_OVERRIDE_FIELDS).split(",");
            String[] values = testData.get(ConfigConstants.BODY_OVERRIDE_VALUES).split(",");
            DocumentContext context = JsonPath.parse(requestBody);
            for (int i = 0; i < fields.length; i++) {
                context.set(fields[i], values[i]);
            }
            requestBody = context.jsonString();
        }
        return requestBody;
    }

    default String setRequestBody(RequestBuilder requestBuilder, Map<String, String> testData) {
        if (Objects.isNull(testData.get(ConfigConstants.REQUEST_BODY_JSON_NAME))) return null;
        String requestBody = getRequestBody(testData);
        requestBuilder.setRequestBody(requestBody);
        return requestBody;
    }

    default String setRequestBody(RequestBuilder requestBuilder, CommonAPIRequest request) {
        if (Objects.isNull(request.getRequestBodyPath())) return null;
        String requestBody = getJsonStringFromJsonFile(request.getRequestBodyPath());
        requestBuilder.setRequestBody(requestBody);
        return requestBody;
    }

    default void validateBody(Response response, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.VALIDATE_FIELDS))) {
            SoftAssert softAssert = new SoftAssert();
            String[] fields = testData.get(ConfigConstants.VALIDATE_FIELDS).split(",");
            List<String> list = Stream.of(testData.get(ConfigConstants.VALIDATE_VALUES).split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"))
                    .map(x -> x.replaceAll("\"", ""))
                    .collect(Collectors.toList());
            for (int i = 0; i < fields.length; i++) {
                if (ConfigConstants.NULL.equals(list.get(i))) {
                    assertNull(response.then().extract().jsonPath().get(fields[i]));
                } else {
                    softAssert.assertEquals(objectToString(response.then().extract().jsonPath().get(fields[i])), list.get(i));
                }
            }
            softAssert.assertAll();
        }
    }

    default void validateExternalBody(Response response, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.VALIDATE_FIELDS_1))) {
            SoftAssert softAssert = new SoftAssert();
            String[] fields = testData.get(ConfigConstants.VALIDATE_FIELDS_1).split(",");
            List<String> list = Stream.of(testData.get(ConfigConstants.VALIDATE_VALUES_1).split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"))
                    .map(x -> x.replaceAll("\"", ""))
                    .collect(Collectors.toList());
            for (int i = 0; i < fields.length; i++) {
                if (ConfigConstants.NULL.equals(list.get(i))) {
                    assertNull(response.then().extract().jsonPath().get(fields[i]));
                } else {
                    softAssert.assertEquals(objectToString(response.then().extract().jsonPath().get(fields[i])), list.get(i));
                }
            }
            softAssert.assertAll();
        }
    }

    default void externalTestDataCreator(Response response, Map<String, String> testData) {
        if (Objects.nonNull(testData.get(ConfigConstants.CREATED_DATA_OVERRIDE_FIELDS))
                && Objects.nonNull(testData.get(ConfigConstants.RESPONSE_FIELDS))
                && Objects.nonNull(testData.get(ConfigConstants.BODY_OVERRIDE_VALUES))) {
            Map<String, String> createdData = new HashMap<>();
            String[] fields = testData.get(CREATED_DATA_OVERRIDE_FIELDS).split(",");
            String[] responseFields = testData.get(RESPONSE_FIELDS).split(",");
            for (int i = 0; i < fields.length; i++) {
                createdData.put(fields[i], objectToString(response.then().extract().jsonPath().get(responseFields[i])));
            }
            testData.put(BODY_OVERRIDE_VALUES, createdData.get(testData.get(CREATED_DATA_OVERRIDE_FIELDS)));
        }
    }

    default void validateStatusCode(Response response, Map<String, String> testData) {
        if (testData.containsKey(ConfigConstants.STATUS_CODE)) {
            assertEquals(response.statusCode(), Integer.parseInt(testData.get(ConfigConstants.STATUS_CODE)));
        }
    }

    default void validateExternalStatusCode(Response response, Map<String, String> testData) {
        if (testData.containsKey(ConfigConstants.STATUS_CODE_2)) {
            assertEquals(response.statusCode(), Integer.parseInt(testData.get(ConfigConstants.STATUS_CODE_2)));
        }
    }

    default void validateMultipleSqlQueries(Response response, String requestBody,
                                            JdbcTemplate jdbcTemplate,
                                            Map<String, String> testData,
                                            List<Map<String, String>> queries) {
        for (Map<String, String> sqlTag : queries) {
            testData.put(SQL, sqlTag.get(SQL));
            testData.put(SQL_VALIDATE_COLUMNS, sqlTag.get(SQL_VALIDATE_COLUMNS));
            testData.put(SQL_VALIDATE_FIELDS, sqlTag.get(SQL_VALIDATE_FIELDS));
            testData.put(SQL_COMPARE_TYPE, sqlTag.get(SQL_COMPARE_TYPE));
            validateSQLQuery(response, requestBody, jdbcTemplate, testData);
        }
    }

    default void validateSQLQuery(Response response, String requestBody,
                                  JdbcTemplate jdbcTemplate,
                                  Map<String, String> testData) {
        if (Objects.nonNull(testData.get(SQL))
                && Objects.nonNull(testData.get(SQL_VALIDATE_FIELDS))
                && Objects.nonNull(testData.get(SQL_VALIDATE_COLUMNS))
                && ((Objects.nonNull(testData.get(RECORD_COUNT)) && !(Objects.toString(testData.get(RECORD_COUNT)).equals("0"))) || Objects.isNull(testData.get(RECORD_COUNT)))) {

            SqlCompareType validateType = SqlCompareType.valueOf(testData.getOrDefault(ConfigConstants.SQL_COMPARE_TYPE, "DEFAULT"));

            String[] fields = testData.get(SQL_VALIDATE_FIELDS).split(",");
            List<String> columns = Stream.of(testData.get(ConfigConstants.SQL_VALIDATE_COLUMNS).split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)"))
                    .map(x -> x.replaceAll("\"", ""))
                    .collect(Collectors.toList());
            Map<String, Object> result = jdbcTemplate.queryForMap(testData.get(SQL));

            if (Objects.nonNull(result)) {
                switch (validateType) {
                    case REQUEST:
                        String[] requestObjectAddresses = testData.get(ConfigConstants.REQUEST_VALIDATE_VALUES).split(",");
                        DocumentContext context = JsonPath.parse(requestBody);
                        for (int i = 0; i < requestObjectAddresses.length; i++) {
                            assertEquals(objectToString(result.getOrDefault(columns.get(i), null)), objectToString(context.read(requestObjectAddresses[i])));
                        }
                        break;
                    case RESPONSE:
                        for (int i = 0; i < fields.length; i++) {
                            if (fields[i].equals("timestamp") && testData.containsKey(TIMESTAMP_GRACE)){
                                if (validateTime(objectToString(response.then().extract().jsonPath().get(fields[i])), objectToString(result.getOrDefault(columns.get(i), null)),testData.get(TIMESTAMP_GRACE))){
                                    assertTrue(true);
                                }else {
                                    assertEquals(objectToString(response.then().extract().jsonPath().get(fields[i])), objectToString(result.getOrDefault(columns.get(i), null)));
                                }
                            }else{
                                assertEquals(objectToString(response.then().extract().jsonPath().get(fields[i])), objectToString(result.getOrDefault(columns.get(i), null)));
                            }
                        }
                        break;
                    case REQUEST_RESPONSE:
                        //get value required value from the
                        String[] requestObjectAddress = testData.get(ConfigConstants.REQUEST_VALIDATE_VALUES).split(",");
                        DocumentContext contexts = JsonPath.parse(requestBody);
                        for (int i = 0; i < requestObjectAddress.length; i++) {
                            assertEquals(objectToString(result.getOrDefault(columns.get(i), null)), objectToString(contexts.read(requestObjectAddress[i])));
                        }
                        for (int i = 0; i < fields.length; i++) {
                            assertEquals(objectToString(response.then().extract().jsonPath().get(fields[i])), objectToString(result.getOrDefault(columns.get(i), null)));
                        }
                        break;
                    case DEFAULT:
                    default:
                        for (int i = 0; i < fields.length; i++) {
                            if (ConfigConstants.NULL.equals(fields[i])) {
                                assertNull(result.getOrDefault(columns.get(i), null));
                            } else {
                                assertEquals(objectToString(result.getOrDefault(columns.get(i), null)), fields[i]);
                            }
                        }
                }
            } else {
                logger.error("result is null");
                assertNotNull(result, "SQL query result is null");

            }
        }
        logger.debug("validateSQLQuery ended");
    }

    default boolean validateTime(String responseTime, String sqlTime,String graceTime){
        LocalDateTime formatResponseTime = LocalDateTime.parse(responseTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime formatSqlTime = LocalDateTime.parse(sqlTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Duration duration = Duration.between(formatSqlTime, formatResponseTime);
        if (duration.getSeconds() <= Long.parseLong(graceTime)){
            return true;
        } else {
            return  false;
        }
    }

    default void mongoCIRCheck(Response response,MongoTemplate mongoTemplate, Map<String, String> testData) {
        if (testData.containsKey(MONGO)
                && testData.containsKey(MONGO_COLLECTION)
                && testData.containsKey(MONGO_CIR_FIELD)
                && testData.containsKey(RESPONSE_CIR_FIELD)) {
            List<Document> result = new ArrayList<>();
            DocumentCallbackHandler dch = result::add;
            mongoTemplate.executeQuery(new BasicQuery(testData.get(ConfigConstants.MONGO)), testData.get(ConfigConstants.MONGO_COLLECTION), dch);
            DocumentContext context = JsonPath.parse(result.get(0).toJson());
            String date = objectToString(context.read(testData.get(MONGO_CIR_FIELD)).toString());
            String subDate = date.substring(9,17);
            String modifiedSubDate = subDate.replaceAll("-","");
            String responseField = response.body().jsonPath().get(testData.get(RESPONSE_CIR_FIELD)).toString();
            responseField = responseField.substring(0,6);
            assertEquals(responseField,modifiedSubDate);
        }
    }

    default void validateMongoQuery(Response response, String requestBody, MongoTemplate mongoTemplate, Map<String, String> testData) {
        if (testData.containsKey(ConfigConstants.MONGO)
                && Objects.nonNull(mongoTemplate)
                && testData.containsKey(ConfigConstants.MONGO_VALIDATE_ATTRIBUTES)
                && Objects.nonNull(testData.get(ConfigConstants.MONGO_COLLECTION))) {
            MongoCompareType validateType = MongoCompareType.valueOf(testData.getOrDefault(ConfigConstants.MONGO_COMPARE_TYPE, "DEFAULT"));
            List<Document> result = new ArrayList<>();
            DocumentCallbackHandler dch = result::add;
            mongoTemplate.executeQuery(new BasicQuery(testData.get(ConfigConstants.MONGO)), testData.get(ConfigConstants.MONGO_COLLECTION), dch);
            // check whether given query respond with one result
            assertFalse(result.isEmpty(), "No result found");
            String[] fields = testData.get(ConfigConstants.MONGO_VALIDATE_FIELDS).split(",");
            String[] attributes = testData.get(ConfigConstants.MONGO_VALIDATE_ATTRIBUTES).split(",");
            DocumentContext context = JsonPath.parse(result.get(0).toJson());
            switch (validateType){
                case RESPONSE:
                    for (int i = 0; i < fields.length; i++) {
                        if (attributes[i].equals("_id")) {
                            assertEquals(objectToString(result.get(0).get("_id")), objectToString(response.then().extract().jsonPath().get(fields[i])));
                        } else {
                            assertEquals(objectToString(context.read(attributes[i])), objectToString(response.then().extract().jsonPath().get(fields[i])));
                        }
                    }
                    break;
                case REQUEST_RESPONSE:
                    //get value required value from the request
                    String[] requestObjectAddress = testData.get(ConfigConstants.REQUEST_VALIDATE_VALUES).split(",");
                    DocumentContext contexts = JsonPath.parse(requestBody);
                    for (int i = 0; i < requestObjectAddress.length; i++) {
                        assertEquals(objectToString(context.read(attributes[i])), objectToString(contexts.read(requestObjectAddress[i])));
                    }
                    for (int i = 0; i < fields.length; i++) {
                        assertEquals(objectToString(context.read(attributes[i])), objectToString(response.then().extract().jsonPath().get(fields[i])));
                    }
                    break;
                case DEFAULT:
                    for (int i = 0; i < fields.length; i++) {
                        assertEquals(objectToString(context.read(attributes[i])), fields[i]);
                    }
                    break;
                default:
            }
        }
        validateTypeFromStep();
    }


    default void validateTypeFromStep(){

    }

    default List<Document> mongoQuery(String query, String collection, MongoTemplate mongoTemplate) {
        List<Document> result = new ArrayList<>();
        DocumentCallbackHandler dch = result::add;
        mongoTemplate.executeQuery(new BasicQuery(query), collection, dch);
        return result;
    }

    default List<Map<String, Object>> queryMultipleRows(String query, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(query);
    }

    default void assertMultipleSQLRows(Response response, Map<String, String> testData, JdbcTemplate jdbcTemplate) {
        String[] responseFields = testData.get(SQL_VALIDATE_FIELDS).split(",");
        String[] queryKeys = testData.get(MULTIPLE_QUERY_VALUES).split(",");
        List<String> listOfFields = new ArrayList<>(Arrays.asList(responseFields));
        List<Map<String, Object>> queryList = queryMultipleRows(testData.get(SQL), jdbcTemplate);
        if (Objects.nonNull(queryList)) {
            for (int i = 0; i < queryList.size(); i++) {
                for (int j = 0; j < listOfFields.size(); j++) {
                    assertEquals(listOfFields.get(j), objectToString(queryList.get(i).get(queryKeys[j])));
                }
            }
        }
    }

    default Response externalApiCall(Map<String, String> testData, RequestType requestType, String string) {
        if (Objects.nonNull(testData.get(METHODBASEPATH_2)) &&
                Objects.nonNull(testData.get(BASE_URI_2))) {
            logger.info(string);
            CommonAPIRequest commonAPIRequest = new CommonAPIRequest();
            commonAPIRequest.setHeaders(testData.get(HEADERS).split(","));
            commonAPIRequest.setHeaderValues(testData.get(HEADER_VALUES).split(","));
            setPathParams(commonAPIRequest, testData);
            setQueryParams(commonAPIRequest, testData);
            setRequestBodyPath(commonAPIRequest, testData);
            commonAPIRequest.setRequestType(requestType);
            commonAPIRequest.setApiBasePath(testData.get(METHODBASEPATH_2));
            commonAPIRequest.setApiBaseUri(testData.get(BASE_URI_2));
            return performExternalApiCall(commonAPIRequest);
        }
        return null;
    }


    default Response performExternalApiCall(CommonAPIRequest request) {
        RequestBuilder requestBuilder = new RequestBuilder();
        //set Headers
        setHeaders(requestBuilder, request);
        setPathParams(requestBuilder, request);
        setQueryParams(requestBuilder, request);
        setRequestBody(requestBuilder, request);
        RequestSpecification requestSpecification;
        ServiceClient serviceClient = new ServiceClient(request.getApiBaseUri(), request.getApiBasePath(), true);
        if (RequestType.POST.equals(request.getRequestType())) {
            requestSpecification = requestBuilder
                    .addHeader("Accept", ContentType.JSON.toString())
                    .setContentType(ContentType.JSON)
                    .build();
            return serviceClient.post(requestSpecification);
        } else if (RequestType.PATCH.equals(request.getRequestType())) {
            requestSpecification = requestBuilder
                    .addHeader("Accept", ContentType.JSON.toString())
                    .setContentType(ContentType.JSON)
                    .build();
            return serviceClient.patch(requestSpecification, "");
        } else if (RequestType.PUT.equals(request.getRequestType())) {
            requestSpecification = requestBuilder
                    .addHeader("Accept", ContentType.JSON.toString())
                    .setContentType(ContentType.JSON)
                    .build();
            return serviceClient.put(requestSpecification, "");
        } else {
            requestSpecification = requestBuilder.build();
            return serviceClient.get(requestSpecification);
        }
    }


    default Response doApiCall(Map<String, String> testData, ConfigConstants.RequestType requestType) {
        RequestBuilder requestBuilder = new RequestBuilder();
        setHeaders(requestBuilder, testData);
        setPathParams(requestBuilder, testData);
        setQueryParams(requestBuilder, testData);
        String requestBody = setRequestBody(requestBuilder, testData);
        logger.debug(requestBody);
        RequestSpecification requestSpecification;
        ServiceClient serviceClient = new ServiceClient(testData.get("dialog-uat-base-uri"), testData.get("methodBasePath"), false);
        switch (requestType) {
            case GET:
                requestSpecification = requestBuilder.build();
                return serviceClient.get(requestSpecification);
            case DELETE:
                requestSpecification = requestBuilder
                        .addHeader("Accept", ContentType.JSON.toString())
                        .setContentType(ContentType.JSON)
                        .build();
                return serviceClient.delete(requestSpecification);
            case POST:
                requestSpecification = requestBuilder
                        .addHeader("Accept", ContentType.JSON.toString())
                        .setContentType(ContentType.JSON)
                        .build();
                return serviceClient.post(requestSpecification);
            case PATCH:
                requestSpecification = requestBuilder
                        .addHeader("Accept", ContentType.JSON.toString())
                        .setContentType(ContentType.JSON)
                        .build();
                return serviceClient.patch(requestSpecification, "");
            case PUT:
            default:
                requestSpecification = requestBuilder
                        .addHeader("Accept", ContentType.JSON.toString())
                        .setContentType(ContentType.JSON)
                        .build();
                return serviceClient.put(requestSpecification, "");

        }
    }

    default void doApiCallWithAfterValidations(Map<String, String> testData, RequestType requestType,
                                               JdbcTemplate jdbcTemplate,
                                               MongoTemplate mongoTemplate) {
        String requestBody = getRequestBody(testData);
        Response response = doApiCall(testData, requestType);
        validateStatusCode(response, testData);
        validateBody(response, testData);
        setFormattedSQLFromRequestToTestData(requestBody, testData);
        setFormattedSQLToTestData(response, testData);
        validateSQLQuery(response, requestBody, jdbcTemplate, testData);
        //validate mongo document
        setFormattedMongoToTestData(response, testData);
        validateMongoQuery(response, requestBody, mongoTemplate, testData);
        headerSQLValidation(response, testData, jdbcTemplate);
        validateRecordCount(testData, jdbcTemplate);
        validateLogs(testData);
    }

    default void validateLogs(Map<String, String> testData) {
        if (Objects.isNull(testData.get(LOG_ELK_QUERY)) || Objects.isNull(testData.get(ELK_LOG_VALUE))) return;
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.setRequestBody(testData.getOrDefault(LOG_ELK_QUERY, ""));
        RequestSpecification requestSpecification = requestBuilder
                .addHeader("Accept", ContentType.JSON.toString())
                .setContentType(ContentType.JSON)
                .build();
        ServiceClient serviceClient = new ServiceClient(testData.getOrDefault(LOG_ELK_API_URL, testData.get(BASE_URI)), "", true);
        Response response = serviceClient.get(requestSpecification);
        if (200 == response.statusCode()) {
            String[] fieldJsonPaths = testData.get(LOG_ELK_FIELD_PATH).split(",");
            String[] logValues = testData.get(ELK_LOG_VALUE).split(",");
            if (Objects.nonNull(testData.get(ELK_LOG_PATTERN)) && testData.get(ELK_LOG_PATTERN).equals("Pattern_2")){
                for (int i = 0; i < fieldJsonPaths.length; i++) {
                    basicLogPatternSplit2(objectToString(response.then().extract().jsonPath().get(fieldJsonPaths[i])), logValues[i], testData.getOrDefault("trace_id", "ABC123"));
                }
            }else {
                for (int i = 0; i < fieldJsonPaths.length; i++) {
                    basicLogPatternSplit(objectToString(response.then().extract().jsonPath().get(fieldJsonPaths[i])), logValues[i], testData.getOrDefault("trace_id", "ABC123"));
                }
            }

        } else {
            logger.error("Error Occurred in Elastic Search Query");
        }
    }


    default void basicLogPatternSplit(String logLine, String expected, String traceId) {
        String[] logs = logLine.split("\\|");
        assertTrue(logs[0].contains(traceId), traceId + " not found");
        assertTrue(logs[2].contains(expected), expected + " not found in log-line");
    }
    default void basicLogPatternSplit2(String logLine, String expected, String traceId) {
        String[] logs = logLine.split("\\|");
        assertTrue(logs[0].contains(traceId), traceId + " not found");
        assertTrue(logs[2].contains(expected), expected + " not found in log-line");
    }
    /*
    this will use for validate required/optional fields, field length, field dataTypes
     */
    default void performSchemaValidation(String json, String path) {
        File file = FileHelper.getFileFromPath(path);
        JsonSchemaValidator jsonSchemaValidator = (JsonSchemaValidator) matchesJsonSchema(file).using(jsonSchemaFactory);
        try {
            assertThat(json, jsonSchemaValidator);
        } catch (JsonSchemaValidationException e) {
            fail();
        }
    }

    default String getJsonStringFromJsonFile(String absolutePath) {
        return FileHelper.getStringFromFile(absolutePath);
    }

    default Object[] getFieldValuesFromResponse(Response response, String[] fieldList) {
        Object[] fieldValues = new Object[fieldList.length];
        try {
            for (int i = 0; i < fieldList.length; i++) {
                fieldValues[i] = response.then().extract().jsonPath().get(fieldList[i]);
            }
        } catch (Exception e) {
            logger.error("Field not found in the Json");
        }
        return fieldValues;
    }

    default Map<String, Object> querySingleRowAsMap(String sql, JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForMap(sql);
    }

    default String objectToString(Object object) {
        if (Objects.isNull(object)) return null;
        if (object instanceof Float || object instanceof Double) {
            DecimalFormat format = new DecimalFormat("0.#");
            return format.format(object);
        }
        return object.toString();
    }

    default void setFormattedSQLToTestData(Response response, Map<String, String> testData) {
        if (Objects.isNull(testData.get(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_RESPONSE))) return;
        String[] fieldList = testData.get(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_RESPONSE).split(",");
        String sql = setSqlParamArray(testData.get(SQL), getFieldValuesFromResponse(response, fieldList));
        logger.debug("Formatted SQL = {} ",sql);
        testData.put(SQL, sql);
    }

    default void setFormattedSQLFromRequestToTestData(String requestBody, Map<String, String> testData) {
        if (Objects.isNull(testData.get(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_REQUEST))) return;
        String[] requestObjectAddress = testData.get(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_REQUEST).split(",");
        DocumentContext contexts = JsonPath.parse(requestBody);
        Object[] fieldValues = new Object[requestObjectAddress.length];
        for (int i = 0; i < requestObjectAddress.length; i++) {
            fieldValues[i] = objectToString(contexts.read(requestObjectAddress[i]));
        }
        String sql = setSqlParamArray(testData.get(SQL), fieldValues);
        logger.debug("Formatted SQL = {} ",sql);
        testData.put(SQL, sql);
    }

    default void setFormattedMongoToTestData(Response response, Map<String, String> testData) {
        if (Objects.isNull(testData.get(MONGO_DYNAMIC_PARAM_FROM_RESPONSE))) return;
        String[] fieldList = testData.get(MONGO_DYNAMIC_PARAM_FROM_RESPONSE).split(",");
        String mongo = setSqlParamArray(testData.get(MONGO), getFieldValuesFromResponse(response, fieldList));
        logger.debug("Formatted MONGO:  = {}",mongo);
        testData.put(MONGO, mongo);
    }

    default void insertCreatedDataToTestModel(Map<String, String> testData, Map<String, String> createdData) {
        testData.forEach((k, v) -> {
            for (String key : createdData.keySet()) {
                testData.put(k, testData.get(k).replace(key, createdData.get(key)));
            }
        });
    }

    default String getUrlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception es) {
            logger.error("Error occurred while trying to encode given string");
            return null;
        }
    }

    default void validateRecordCount(Map<String, String> testData, JdbcTemplate jdbcTemplate) {
        if (Objects.nonNull(testData.get(RECORD_COUNT))) {
            List<Map<String, Object>> result = jdbcTemplate.queryForList(testData.get(SQL));
            assertEquals(Objects.toString(result.size()), testData.get(RECORD_COUNT));
        }
    }

    default void compareStringValues(String value1, String value2) {
        assertEquals(value1, value2);
    }

    default void headerSQLValidation(Response response, Map<String, String> testData, JdbcTemplate jdbcTemplate) {
        if (Objects.nonNull(testData.get(HEADER_VALIDATE_SQL))
                && Objects.nonNull(testData.get(HEADER_SQL_VALIDATE_FIELDS))
                && Objects.nonNull(testData.get(HEADER_SQL_VALIDATE_COLUMNS))) {
            String[] columns = testData.get(HEADER_SQL_VALIDATE_COLUMNS).split(",");
            String[] fields = testData.get(HEADER_SQL_VALIDATE_FIELDS).split(",");
            String[] headers = testData.get(HEADERS).split(",");
            String[] values = testData.get(HEADER_VALUES).split(",");
            for (int i = 0; i < fields.length; i++) {
                fields[i] = values[ArrayUtils.indexOf(headers, fields[i])];
            }
            if (Objects.nonNull(testData.get(ConfigConstants.HEADER_SQL_PARAM_FROM_RESPONSE))) {
                String[] params = testData.get(ConfigConstants.HEADER_SQL_PARAM_FROM_RESPONSE).split(",");
                String sql = setSqlParamArray(testData.get(HEADER_VALIDATE_SQL), getFieldValuesFromResponse(response, params));
                testData.put(HEADER_VALIDATE_SQL, sql);
            }
            Map<String, Object> result = jdbcTemplate.queryForMap(testData.get(HEADER_VALIDATE_SQL));
            if (Objects.nonNull(result)) {
                for (int i = 0; i < fields.length; i++) {
                    assertEquals(objectToString(result.getOrDefault(columns[i], null)), objectToString(fields[i]));
                }
            } else {
                assertNotNull(result, "HEADER SQL query result is null");
            }
        }
    }
    default void validateFieldLength(Response response, Map<String, String> testData){
        if(testData.containsKey(LENGTH_VALIDATE_FIELDS) && testData.containsKey(VALIDATE_LENGTHS)){
            String[] fieldList = testData.get(LENGTH_VALIDATE_FIELDS).split(",");
            String[] lengthList = testData.get(VALIDATE_LENGTHS).split(",");
            for (int i=0; i<fieldList.length;i++){
                String field = objectToString(response.then().extract().jsonPath().get(fieldList[i]));
                assertEquals(field.length(), Integer.parseInt(lengthList[i]));
            }
        }
    }

    default void validateRetryState(Response response, Map<String, String> testData){

    }

    default String getMifeAccessToken() {
        RestAssured.baseURI = "https://mife-stg.dialog.lk/apicall/token?grant_type=client_credentials";
        // Create a request specification
        requestSpecification = RestAssured.given()
                .with().header("Content-Type", "application/x-www-form-urlencoded; charset=utf-8")
                .header("Authorization", "Bearer U2ZyZlN3dmZPSkZnanBoRWxfNWpaZUhtS3dRYTptdEVmMzcxamY0TzIzb1ZiOENlMDdWRVVnR1Fh")
                .with().param("grant_type", "client_credentials");

        Response response = requestSpecification.post();
        return response.jsonPath().get("access_token");
    }
    default Response  createMifeToken(Map<String, String> testData) {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.addHeader("Content-Type","application/x-www-form-urlencoded");
        requestBuilder.addHeader("Authorization","Bearer U2ZyZlN3dmZPSkZnanBoRWxfNWpaZUhtS3dRYTptdEVmMzcxamY0TzIzb1ZiOENlMDdWRVVnR1Fh");
        requestBuilder.addHeader("Cookie","JSESSIONIDSSO=7E520D3D7DEB35A57688D6D652862BFE");
        requestBuilder.addQueryParam("grant_type","client_credentials");
        RequestSpecification requestSpecification;
        ServiceClient serviceClient = new ServiceClient(testData.get("dialog-uat-base-uri-2"), "/apicall/token", false);
        requestSpecification = requestBuilder
                .build();
        Response response = serviceClient.post(requestSpecification);
        String token =  response.jsonPath().get("access_token");


        RequestBuilder requestBuilder1 = new RequestBuilder();
        requestBuilder1.addHeader("Authorization","Bearer "+token);
        requestBuilder1.addHeader("Content-Type","application/json");
        String requestBody = setRequestBody(requestBuilder1, testData);
        logger.debug(requestBody);
        RequestSpecification requestSpecification1;
        ServiceClient serviceClient1 = new ServiceClient(testData.get("dialog-uat-base-uri-2"), "/apicall/crm/rbm/all/acc/sum/v1.0.0/rest/query-all-acc-installments-summary", false);
        requestSpecification1 = requestBuilder1
                .build();
        return serviceClient1.post(requestSpecification1);



    }

    default Response  createMifeTokenForDeposits(Map<String, String> testData) {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.addHeader("Content-Type","application/x-www-form-urlencoded");
        requestBuilder.addHeader("Authorization","Bearer U2ZyZlN3dmZPSkZnanBoRWxfNWpaZUhtS3dRYTptdEVmMzcxamY0TzIzb1ZiOENlMDdWRVVnR1Fh");
        requestBuilder.addHeader("Cookie","JSESSIONIDSSO=7E520D3D7DEB35A57688D6D652862BFE");
        requestBuilder.addQueryParam("grant_type","client_credentials");
        RequestSpecification requestSpecification;
        ServiceClient serviceClient = new ServiceClient(testData.get("dialog-uat-base-uri-2"), "/apicall/token", false);
        requestSpecification = requestBuilder
                .build();
        Response response = serviceClient.post(requestSpecification);
        String token =  response.jsonPath().get("access_token");


        RequestBuilder requestBuilder1 = new RequestBuilder();
        requestBuilder1.addHeader("Authorization","Bearer "+token);
        requestBuilder1.addHeader("Content-Type","application/json");
        String requestBody = setRequestBody(requestBuilder1, testData);
        logger.debug(requestBody);
        RequestSpecification requestSpecification1;
        ServiceClient serviceClient1 = new ServiceClient(testData.get("dialog-uat-base-uri-2"), "/apicall/crm/Rbm/QueryDeposit/v1.0.0/dbtQueryAllDeposits", false);
        requestSpecification1 = requestBuilder1
                .build();
        return serviceClient1.post(requestSpecification1);



    }

    default void sqlCountQueryCheck(Response response, Map<String, String> testData, JdbcTemplate jdbcTemplate) {
        if(testData.containsKey(SQL_COUNT_CHECK)
                && testData.containsKey(SQL_COUNT_COLUMN)
                && testData.containsKey(SQL_COUNT_VALUE)) {
            if (testData.containsKey(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_REQUEST)) {
                String requestBody = getRequestBody(testData);
                String[] requestObjectAddress = testData.get(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_REQUEST).split(",");
                DocumentContext contexts = JsonPath.parse(requestBody);
                Object[] fieldValues = new Object[requestObjectAddress.length];
                for (int i = 0; i < requestObjectAddress.length; i++) {
                    fieldValues[i] = objectToString(contexts.read(requestObjectAddress[i]));
                }
                String sql = setSqlParamArray(testData.get(SQL_COUNT_CHECK), fieldValues);
                logger.debug("Formatted SQL: ".concat(sql));
                testData.put(SQL_COUNT_CHECK, sql);
            }
            if (testData.containsKey(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_RESPONSE)) {
                String[] fieldList = testData.get(ConfigConstants.SQL_DYNAMIC_PARAM_FROM_RESPONSE).split(",");
                String sql = setSqlParamArray(testData.get(SQL_COUNT_CHECK), getFieldValuesFromResponse(response, fieldList));
                logger.debug("Formatted SQL: ".concat(sql));
                testData.put(SQL_COUNT_CHECK, sql);
            }

            Map<String, Object> result = jdbcTemplate.queryForMap(testData.get(SQL_COUNT_CHECK));
            Long countCheck = Long.parseLong(String.valueOf(result.get(testData.get(SQL_COUNT_COLUMN))));
            assertEquals(countCheck.toString(), testData.get(SQL_COUNT_VALUE));
        }
    }

    default void getValueForQuery(String query, JdbcTemplate jdbcTemplate,  Map<String, String> testData) {

        List<String> numberList  = jdbcTemplate.queryForList(query, String.class);

        if(CollectionUtils.isEmpty(numberList)) {
            assertTrue(CollectionUtils.isEmpty(numberList));
        } else {
            testData.put("body-override-values", numberList.get(0));
            testData.put("body-override-values", numberList.get(0));
        }

    }
}
