package com.rogers.api.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

public class Http {

    public static void main(String args[]) throws URISyntaxException {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://idcs-2c3743324b734b66bc18be15688ac5c8.identity.oraclecloud.com/oauth2/v1/token";
        System.out.println("URL :: " + url);

        String clientId = "RGBU_XTROFFCS_DEV_SETUP_DATA_APPID";
        String clientSecret = "2ee75177-7075-4bdb-8dce-609ac68f4058";

        String contentType = "application/x-www-form-urlencoded;charset=UTF-8";

        String credentials = clientId + ":" + clientSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        //String encodedCredentials = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Utf8.parse(clientInfo));

        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("grant_type", "client_credentials");
        bodyMap.add("scope", "urn:opc:idm:__myscopes__");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + encodedCredentials);
        httpHeaders.add(HttpHeaders.CONTENT_TYPE, contentType);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(bodyMap, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(new URI(url), HttpMethod.POST, requestEntity, String.class);

        System.out.println("Response from {}: {}" + response.getBody());
    }
}
