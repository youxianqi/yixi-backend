package youxianqi.yixi.utils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestClient {
    private RestTemplate restTemplate;
    public RestClient() {
        HttpClient httpClient = HttpClientBuilder.create().setMaxConnPerRoute(1).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate = new RestTemplate(requestFactory);
    }
    public <T> T post(String url, String payload, Class<T> clazz) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON_UTF8);
        return restTemplate.postForObject(url, new HttpEntity<String>(payload, headers), clazz);
    }

    public <T> T get(String url, Class<T> clazz) throws IOException {
        return restTemplate.getForObject(url, clazz);
    }

    public String get(String url) throws IOException {
        return get(url, String.class);
    }

    public static void main(String[] args) {
        RestClient rc = new RestClient();
        try {
            String s = rc.get("http://www.baidu.com");
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
