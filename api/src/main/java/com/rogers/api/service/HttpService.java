package com.rogers.api.service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.*;

import com.jcraft.jsch.SftpException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@lombok.Value
@Component
public class HttpService {

    RestTemplate restTemplate;
    String clientId;
    String clientSecret;
    String oauth2URL;
    String xcenterURL;


    public HttpService(RestTemplateBuilder builder, @Value("${application.rest.clientId}") final String clientId, @Value("${application.rest.clientSecret}") final String clientSecret,
                       @Value("${application.rest.oauth2URL}") final String oauth2URL, @Value("${application.rest.xcenterURL}") final String xcenterURL) {
        this.restTemplate = builder.setConnectTimeout(Duration.ofMillis(30000))
                            .setReadTimeout(Duration.ofMillis(30000))
                            .build();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.oauth2URL = oauth2URL;
        this.xcenterURL = xcenterURL;
    }

    public String getAccessToken() throws URISyntaxException {

        String url = oauth2URL + "/oauth2/v1/token";
        log.info("Sending POST request to: {}", url);

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("grant_type", "client_credentials");
        bodyMap.add("scope", "urn:opc:idm:__myscopes__");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, httpHeaders);

        ResponseEntity<HashMap> response = restTemplate.exchange(new URI(url), HttpMethod.POST, requestEntity, HashMap.class);

        log.info("Response from {}: {}", url, response.getBody().get("access_token"));

        return (String) response.getBody().get("access_token");
    }


    public void sendPutRequest(File file, byte[] bytes) throws URISyntaxException, SftpException, IOException {

        String url =  xcenterURL + "/xcenter/rest/DEFAULT/19/file/autodeploy/1/" + file.getName();
        log.info("Sending PUT request to: {}", url);

        String accessToken = getAccessToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/vnd.businessobjects");
        httpHeaders.add(HttpHeaders.ACCEPT, "*/*");
        httpHeaders.add(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate, br");

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(bytes, httpHeaders);

        try {
            ResponseEntity<String> response = restTemplate.exchange(new URI(url), HttpMethod.PUT, requestEntity, String.class);
            log.info("Response from {}: {}", url, response.getBody());
            log.info(" Response Status code :" + response.getStatusCode());
        } catch (Exception e) {
            log.warn("File name :: " + file.getName());
            log.warn("An error has occurred while sending file via PUT method", e);
        }

    }

    public int getRequestStatusCode() throws URISyntaxException {

        String url =  xcenterURL + "/xcenter/rest/DEFAULT/19/file/autodeploy/1/";
        log.info("Sending GET request to: {}", url);

        String accessToken = getAccessToken();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(new URI(url), HttpMethod.GET, httpEntity, String.class);

        log.info("Response from {}: {}", url, response.getBody());
        log.info(" Response Status code :" + response.getStatusCodeValue());

        return response.getStatusCodeValue();
    }

}
