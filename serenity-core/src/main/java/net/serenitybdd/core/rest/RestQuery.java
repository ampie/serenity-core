package net.serenitybdd.core.rest;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;

public class RestQuery {

    private RestMethod method;
    private String path;
    private String content;
    private String contentType;
    private String requestHeaders;
    private String requestCookies;
    private String responseHeaders;
    private String responseCookies;

    private Optional<? extends List<Object>> parameters=Optional.absent();
    private Optional<? extends Map<String, ?>> parameterMap=Optional.absent();
    private String responseBody;
    private Integer statusCode;
    public RestQuery(){
    }
    private RestQuery(RestMethod method, String path, List<Object> parameters, Map<String, ?> parameterMap,
                      String content, String contentType, String responseBody, Integer statusCode,
                      String requestHeaders, String requestCookies, String responseHeaders, String responseCookies) {
        this();
        this.method = method;
        this.path = path;
        this.parameters = Optional.fromNullable(parameters);
        this.parameterMap = Optional.fromNullable(parameterMap);
        this.content = content;
        this.contentType = contentType;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
        this.requestHeaders = requestHeaders;
        this.requestCookies = requestCookies;
        this.responseHeaders = responseHeaders;
        this.responseCookies = responseCookies;

    }

    public RestQuery(RestMethod method, String path) {
        this(method, path, null, null, null, null, null, null, null, null, null, null);
    }


    public RestQuery withParameters(List<Object> parameters) {
        return new RestQuery(method, path, parameters, null, content, contentType, responseBody, statusCode,
                requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withParameters(Map<String, ?> parameterMap) {
        return new RestQuery(method, path, null, parameterMap, content, contentType, responseBody, statusCode,
                requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withResponse(String responseBody) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withStatusCode(Integer statusCode) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withContent(String content) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withContentType(String contentType) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withResponseCookies(String responseCookies) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withResponseHeaders(String responseHeaders) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }
    public RestQuery withRequestCookies(String requestCookies) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestQuery withRequestHeaders(String requestHeaders) {
        return new RestQuery(method, path, parameters.orNull(), parameterMap.orNull(), content, contentType,
                responseBody, statusCode, requestHeaders, requestCookies, responseHeaders, responseCookies);
    }

    public RestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Optional<? extends List<Object>> getParameters() {
        return parameters;
    }

    public Optional<? extends Map<String, ?>> getParameterMap() {
        return parameterMap;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getRequestHeaders() {
        return requestHeaders;
    }

    public String getRequestCookies() {
        return requestCookies;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public String getResponseCookies() {
        return responseCookies;
    }

    public boolean hasParameters() {
        return parameters.isPresent() || parameterMap.isPresent();
    }

    public String toString() {
        return getFormattedQuery();
    }

    public String getFormattedQuery() { return getMethod() + " " + injectParametersInto(getPath()); }

    private String injectParametersInto(String path) {
        if (parameters.isPresent()) {
            return injectParameterValuesInto(path, parameters.get());
        } else if (parameterMap.isPresent()) {
            return injectParameterMapInto(path, parameterMap.get());
        } else {
            return path;
        }
    }

    private String injectParameterMapInto(String path, Map<String, ?> parameterMap) {
        for(String parameterName : parameterMap.keySet()) {
            String parameterValue = parameterMap.get(parameterName).toString();
            path = path.replaceAll("\\{" + parameterName + "\\}", parameterValue);
        }
        return path;
    }

    private String injectParameterValuesInto(String path, List<Object> parameters) {

        for (Object parameterValue : parameters) {
            int variableStart = path.indexOf("{");
            int variableEnd = path.indexOf("}");
            if (variableStart >= 0 && variableEnd > variableStart) {
                path = path.substring(0, variableStart) + parameterValue + path.substring(variableEnd + 1);
            }
        }
        return path;
    }

    public static RestQueryBuilder withMethod(RestMethod method) {
        return new RestQueryBuilder(method);
    }

    public static class RestQueryBuilder {

        private RestMethod method;

        public RestQueryBuilder(RestMethod method) {
            this.method = method;
        }

        public RestQuery andPath(String path) {
            return new RestQuery(method, path);
        }
    }
}
