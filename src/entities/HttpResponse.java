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

        if (response.getEntity() != null) {
            this.body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            this.body = "";
        }
    }

    public String getContentType() {
        return headers.get("Content-Type");
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

}