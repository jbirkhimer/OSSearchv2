package edu.si.ossearch.edan.api.client;

import edu.si.ossearch.edan.EdanResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author jbirkhimer
 */
@Slf4j
@Component
public class EdanClient {
    @Value("${ossearch.edan-api.url}")
    String baseUrl;
    @Value("${ossearch.edan-api.path}")
    String path;
    @Value("${ossearch.edan-api.appId}")
    String appId;
    @Value("${ossearch.edan-api.appKey}")
    String appKey;

    /**
     * int indicates the authentication type/level 0 for unsigned/trusted/T1
     * requests; (currently unused) 1 for signed/T2 requests; 2 for password based
     * (unused)
     * auth_type is only for future, this code is only for type 1
     */
    private int auth_type = 1;

    // This method returns filter function which will log request data
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientRequest);
        });
    }

    // This method returns filter function which will log response data
    private static ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("Response status: {}", clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.info("{}={}", name, value)));
            return Mono.just(clientResponse);
        });
    }

    /**
     * Setting the required http request headers for EDAN Authentication
     * (See <a href="http://edandoc.si.edu/authentication">http://edandoc.si.edu/authentication</a> for more information)
     *
     * @param queryString the query string
     * @return
     */
//    @SneakyThrows
    public Map<String, String> getAuthContent(String queryString) throws UnsupportedEncodingException {
        Map<String, String> auth = new HashMap<>();

        auth.put("nonce", UUID.randomUUID().toString().replace("-", ""));

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        auth.put("sdfDate",df.format(new Date()));

        String authContent = Base64.getEncoder().encodeToString(DigestUtils.sha1Hex(auth.get("nonce") + "\n" + queryString + "\n" + auth.get("sdfDate") + "\n" + appKey).getBytes("UTF-8"));
        auth.put("authContent", authContent);

        log.debug("edan queryString: {}, auth: {}", queryString, auth);
        return auth;
    }

    public URI getURI(MultiValueMap<String, String> params) {
        UriBuilder builder = new DefaultUriBuilderFactory(baseUrl).builder();
        builder.path(path);
        builder.queryParams(params);
        return builder.build();
    }

    /**
     * Perform a EDAN http request
     *
     * @param uri
     * @return
     * @throws EdanApiException
     */
    public EdanResponse sendRequest(URI uri) throws UnsupportedEncodingException {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(logRequest())
                .filter(logResponse())
                .build();

        Map<String, String> auth = getAuthContent(uri.getRawQuery());

//        String result = webClient
        EdanResponse result = webClient
                .get()
                .uri(uri)
                .headers(httpHeaders -> {
                    httpHeaders.add("X-AppId", appId);
                    httpHeaders.add("X-Nonce", auth.get("nonce"));
                    httpHeaders.add("X-RequestDate", auth.get("sdfDate"));
                    httpHeaders.add("X-AuthContent", auth.get("authContent"));
                    httpHeaders.add("X-AppVersion", "EDANInterface-0.10.1");
                })
                .retrieve()
                .bodyToMono(EdanResponse.class)
//                .bodyToMono(String.class)
                .block();

//        log.info("edan response: {}", new JSONObject(result).toString(4));
//        return new EdanResponse();

        return result;
    }
}
