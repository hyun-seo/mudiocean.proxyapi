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
import java.util.List;
import java.util.Map;

@Component
public class RestSessionClientImpl implements RestSessionClient {

    RestTemplate rt = new RestTemplate();
    Map<String, String> cookieMap = new HashMap<>();
    ArrayList<String> targetCookieName = new ArrayList<>();

    public HttpEntity<String> callGet(String url, MultiValueMap<String, String> params, HttpHeaders headers) throws Exception {

        HttpEntity<String> request = new HttpEntity<>(headers);
        String uriWithPrams;

        uriWithPrams = UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParams(params)
                .build()
                .toString();

        request = interceptCookie(request);
        HttpEntity<String> response = rt.exchange(uriWithPrams, HttpMethod.GET, request, String.class);
        response = interceptCookie(response);
        return response;

    }

    public HttpEntity<String> callPost(String url, HashMap<String, String> params, HttpHeaders headers) throws Exception {

        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        request = interceptCookie(request);
        HttpEntity<String> response = rt.exchange(url, HttpMethod.POST, request, String.class);
        response =  interceptCookie(response);
        return response;
    }

    // header -> cookieMap, cookieMap -> header
    protected <T> HttpEntity<T> interceptCookie(HttpEntity<T> httpEntity) throws Exception {
        try {
            // header -> cookieMap
            HttpHeaders headers = HttpHeaders.writableHttpHeaders(httpEntity.getHeaders());
            if (headers.containsKey("Set-Cookie")) {
                List<String> cookies = headers.get("Set-Cookie");

                // cookiemap update with atomic operation
                Map<String, String> _cookieMap = new HashMap<>();

                for (String cookie : cookies) {
                    String[] cookieValue = cookie.split(";")[0].split("=");

                    if (targetCookieName.contains(cookieValue[0]) || targetCookieName.isEmpty()) {
                        _cookieMap.put(cookieValue[0], cookieValue[1]);
                    }
                }
                cookieMap = _cookieMap;
            }

            // cookieMap -> header
            StringBuilder cookieString = new StringBuilder();
            for (String key : cookieMap.keySet()) {
                cookieString.append(key).append("=").append(cookieMap.get(key)).append(";");
            }
            if (!cookieString.toString().equals("")){
                headers.add("Cookie", cookieString.toString());
            }


            return new HttpEntity<>(httpEntity.getBody(), headers);
        } catch (Exception e) {
            throw new Exception("interceptCookie error", e);
        }
    }

}
