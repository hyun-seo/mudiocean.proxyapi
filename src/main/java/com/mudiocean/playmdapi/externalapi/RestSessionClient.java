package com.mudiocean.playmdapi.externalapi;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class RestSessionClient {

    RestTemplate rt = new RestTemplate();
    Map<String, String> cookieMap = new HashMap<>();
    ArrayList<String> targetCookieName = new ArrayList<>();

    public ResponseEntity<String> getMethod(String url, MultiValueMap<String, String> params, HttpHeaders headers) {
        HttpEntity<String> request = new HttpEntity<>(headers);
        String uriWithPrams;

        uriWithPrams = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParams(params)
                .build()
                .toString();

        request = interceptCookie(request);
        ResponseEntity<String> response = rt.exchange(uriWithPrams, HttpMethod.GET, request, String.class);
        response = (ResponseEntity<String>) interceptCookie(response);

        return response;
    }

    public ResponseEntity<String> postMethod(String url, HashMap<String, String> params, HttpHeaders headers) {

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        request = interceptCookie(request);
        ResponseEntity<String> response = rt.exchange(url, HttpMethod.POST, request, String.class);
        response = (ResponseEntity<String>) interceptCookie(response);
        return response;
    }

    // header -> cookieMap, cookieMap -> header
    private <T> HttpEntity<T> interceptCookie(HttpEntity<T> httpEntity) {
        // header -> cookieMap
        HttpHeaders headers = httpEntity.getHeaders();
        if (headers.containsKey("Set-Cookie")) {
            String cookieString = headers.getFirst("Set-Cookie");

            if (cookieString != null) {
                String[] cookieArray = cookieString.split(";");
                Map<String, String> _cookieMap = new HashMap<>();
                for (String cookieItem : cookieArray) {
                    String[] cookieItemArray = cookieItem.split("=");
                    if (targetCookieName.contains(cookieItemArray[0]) || targetCookieName.isEmpty()) {
                        _cookieMap.put(cookieItemArray[0], cookieItemArray[1]);
                    }
                }
                cookieMap = _cookieMap; // for atomic operation
            }
        }

        // cookieMap -> header
        StringBuilder cookieString = new StringBuilder();
        for (String key : cookieMap.keySet()) {
            cookieString.append(key).append("=").append(cookieMap.get(key)).append(";");
        }
        headers.set("Cookie", cookieString.toString());


        return new HttpEntity<>(httpEntity.getBody(), headers);
    }

}
