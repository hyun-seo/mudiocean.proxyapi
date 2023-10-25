package com.mudiocean.playmdapi.externalapi;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

@Component
public class Agent {
    // external api call to https://playmd.xmd.co.kr/
    // post method for login : https://playmd.xmd.co.kr/api/member/do_login
    // get method for get info : https://playmd.xmd.co.kr/api/xcom/xcom_codbarpr
    static final ArrayList<String> cookieNames = new ArrayList<>(Arrays.asList("USERINFO", "xmd_session", "XDLSK", "XDTSK"));
    //    Map<String, String> cookieMap = Collections.synchronizedMap(new HashMap<>());
    Map<String, String> cookieMap = new HashMap<>();


    RestTemplate rt = new RestTemplate();

    static final String url_login = "https://playmd.xmd.co.kr/api/member/do_login";
    static final String url_getitem = "https://playmd.xmd.co.kr/api/xcom/xcom_codbarpr";
    static final int MAX_RETRY = 1;
    @Autowired
    private ApplicationContext context;
    @Autowired
    private Environment env;

    private void login() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        Map<String, String> body = new HashMap<>();

        body.put("CMEMCD", env.getProperty("CMEMCD"));
        body.put("CMEMPWD", env.getProperty("CMEMPWD"));
        body.put("CUSRID", env.getProperty("CUSRID"));
        body.put("CUSRPWD", env.getProperty("CUSRPWD"));


        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
        HttpEntity<String> response = rt.exchange(url_login, HttpMethod.POST, request, String.class);
        HttpHeaders responseHeader = response.getHeaders();
        List<String> cookies = responseHeader.get("Set-Cookie");
        assert cookies != null;

        // cookiemap update with atomic operation
        Map<String, String> _cookieMap = new HashMap<>();

        for (String cookie : cookies) {
            String[] cookieValue = cookie.split(";")[0].split("=");

            if (cookieNames.contains(cookieValue[0])) {
                _cookieMap.put(cookieValue[0], cookieValue[1]);
            }
        }
        cookieMap = _cookieMap;
    }


    public JsonNode getItem(String I_AGTCD, String I_COND) throws Exception {
        // TODO : validate I_AGTCD, I_COND

        // today "20231023"
        String I_DATE = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String I_TAG = "0"; // TODO : what is this?
        String I_USRGUBN = "1"; // TODO : what is this?
        String url = "https://playmd.xmd.co.kr/api/xcom/xcom_codbarpr?I_AGTCD=" + I_AGTCD + "&I_COND=" + I_COND + "&I_DATE=" + I_DATE + "&I_TAG=" + I_TAG + "&I_USRGUBN=" + I_USRGUBN;

        // retry login TODO : make it better
        int retry = 0;
        while (retry < MAX_RETRY) {
            try {
                HttpHeaders headers = getCookieHeader();
                HttpEntity<String> request = new HttpEntity<>(headers);
                HttpEntity<String> response = rt.exchange(url, HttpMethod.GET, request, String.class);
                return parseListItem(response.getBody());
            } catch (Exception e) {
                retry++;
//                sleep(500);
                System.out.println(e);
            }
        }

        throw new Exception("failed");
    }

    private HttpHeaders getCookieHeader() throws Exception {
        if (!assertCookie()) {
            login();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        headers.add("Cookie", "USERINFO=" + cookieMap.get("USERINFO") + "; xmd_session=" + cookieMap.get("xmd_session") + "; XDLSK=" + cookieMap.get("XDLSK") + "; XDTSK=" + cookieMap.get("XDTSK"));
        headers.add("Xmd-Session", cookieMap.get("xmd_session"));
        headers.add("Referer", "https://playmd.xmd.co.kr/pos/viewWebPos");
        return headers;
    }

    private Boolean assertCookie() {
        if (cookieMap == null) {
            return Boolean.FALSE;
        }
        for (String cookieName : cookieNames) {
            if (!cookieMap.containsKey(cookieName)) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }


    private JsonNode parseListItem(String jsonstr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonstr);
    }
}
