package services;

import entities.History;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import entities.HttpRequest;
import entities.HttpResponse;
import views.RestClientUI;

import java.io.IOException;
import java.net.URISyntaxException;

public class RestClient {
    private RestClientUI view;
    private HttpRequest currentRequest;
    private History history;

    public RestClient() {
        this.history = new History();
        this.view = new views.RestClientUI(this);
    }

    public HttpResponse ejecutarRequest(HttpRequest request) throws IOException {
        this.currentRequest = request;
        try {
            currentRequest.setUrl(currentRequest.toHttpUriRequest().getUri().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        try {
            CloseableHttpResponse response;
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                response = client.execute(currentRequest.toHttpUriRequest());
            }
            history.saveRequest(currentRequest);
            return new HttpResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpRequest getRequest() {
        return currentRequest;
    }
}
