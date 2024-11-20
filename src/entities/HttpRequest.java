package entities;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private int id;
    private String url;
    private String method;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String body;
    private String name;

    public HttpRequest(String url, String method) {
        this.url = url;
        this.method = method.toUpperCase();
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    public HttpRequest(int id, String url, String method) {
        this.id = id;
        this.url = url;
        this.method = method.toUpperCase();
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    public HttpRequest(String url, String method, String[][]  headers, String[][] queryParams, String body) {
        this.url = url;
        this.method = method.toUpperCase();
        this.headers = new HashMap<>();
        this.queryParams = new HashMap<>();

        for (String[] param : queryParams) {
            if (!param[0].isBlank() && !param[1].isBlank()) {
                this.addQueryParam(param[0], param[1]);
            }
        }

        for (String[] header : headers) {
            if (!header[0].isBlank() && !header[1].isBlank()) {
                this.addHeader(header[0], header[1]);
            }
        }

        this.body = body;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public HttpUriRequestBase toHttpUriRequest() {
        String fullUrl = buildFullUrl();
        HttpUriRequestBase request;

        switch (method) {
            case "GET":
                request = new HttpGet(fullUrl);
                break;
            case "POST":
                HttpPost post = new HttpPost(fullUrl);
                if (body != null) post.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
                request = post;
                break;
            case "PUT":
                HttpPut put = new HttpPut(fullUrl);
                if (body != null) put.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
                request = put;
                break;
            case "DELETE":
                request = new HttpDelete(fullUrl);
                break;
            default:
                throw new IllegalArgumentException("Método HTTP no soportado: " + method);
        }

        headers.forEach(request::addHeader);
        return request;
    }

    public String buildFullUrl() {
        if (queryParams.isEmpty()) return url;

        StringBuilder fullUrl = new StringBuilder(url);
        fullUrl.append("?");
        queryParams.forEach((key, value) -> fullUrl.append(key).append("=").append(value).append("&"));
        fullUrl.deleteCharAt(fullUrl.length() - 1);
        return fullUrl.toString();
    }

    public int getId() {
        return this.id;
    }

    public String getMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, String> getQueryParams() {
        return this.queryParams;
    }

    public String getBody() {
        return body;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}