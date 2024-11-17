package entities;

import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private int statusCode;
    private Map<String, String> headers;
    private String body;

    public HttpResponse(CloseableHttpResponse response) throws IOException {
        this.statusCode = response.getCode();
        this.headers = new HashMap<>();
        for (int i = 0; i < response.getHeaders().length; i++) {
            this.headers.put(response.getHeaders()[i].getName(), response.getHeaders()[i].getValue());
        }
//        response.getHeaders().forEach(header -> headers.put(header.getName(), header.getValue()));

        if (response.getEntity() != null) {
            this.body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            this.body = "";
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String formatAsJson() {
        if (body.trim().startsWith("{")) {
            return body; // Retorna sin cambios si ya es JSON
        }
        return "{\"response\": \"" + body.replace("\"", "\\\"") + "\"}";
    }

    public String formatAsXml() {
        return "<response>" + body.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;") + "</response>";
    }
}