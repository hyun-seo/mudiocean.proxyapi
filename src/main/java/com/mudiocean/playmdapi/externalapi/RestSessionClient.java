package com.mudiocean.playmdapi.externalapi;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

@Component
public interface RestSessionClient {
    public HttpEntity<String> callGet(String url, MultiValueMap<String, String> params, HttpHeaders headers) throws Exception;
    public HttpEntity<String> callPost(String url, HashMap<String, String> params, HttpHeaders headers) throws Exception;

}
