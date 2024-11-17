package services;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import entities.HttpRequest;
import entities.HttpResponse;

import java.io.IOException;
import java.net.URISyntaxException;

public class RestClient {
    private HttpRequest request;

    public RestClient(String baseUrl) {
        this.request = new HttpRequest(baseUrl, "GET");
    }

    public void agregarHeader(String key, String value) {
        request.addHeader(key, value);
    }

    public void agregarParametro(String key, String value) {
        request.addQueryParam(key, value);
    }

    public void definirCuerpo(String body) {
        request.setBody(body);
    }

    public HttpResponse ejecutarRequest(String metodo) throws IOException {
        try {
            request.setUrl(request.toHttpUriRequest().getUri().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try (CloseableHttpClient client = HttpClients.createDefault();
             CloseableHttpResponse response = client.execute(request.toHttpUriRequest())) {
            return new HttpResponse(response);
        }
    }

    public HttpRequest getRequest() {
        return request;
    }
}
