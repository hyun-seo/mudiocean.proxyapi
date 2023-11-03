package com.mudiocean.playmdapi.externalapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Component
public class PlayMDRestSessionClientImp extends RestSessionClientImpl {

    @Value(value = "${CMEMCD}")
    private String CMEMCD;
    @Value(value = "${CMEMPWD}")
    private String CMEMPWD;
    @Value(value = "${CUSRID}")
    private String CUSRID;
    @Value(value = "${CUSRPWD}")
    private String CUSRPWD;

    boolean ingLogin = false;
    int MAX_RETRY = 2;

    public PlayMDRestSessionClientImp() {
        super();
        targetCookieName = new ArrayList<>(Arrays.asList("USERINFO", "xmd_session", "XDLSK", "XDTSK"));
    }


    public void login() throws Exception {
        if (ingLogin) {
            throw new RuntimeException("Already login process is");
        }
        ingLogin = true;
        String url = "https://playmd.xmd.co.kr/api/member/do_login";
        HashMap<String, String> params = new HashMap<>();
        params.put("CMEMCD", CMEMCD);
        params.put("CMEMPWD", CMEMPWD);
        params.put("CUSRID", CUSRID);
        params.put("CUSRPWD", CUSRPWD);
        super.callPost(url, params, null);
        ingLogin = false;
    }

    //    @RetryLogin
    @Override
    public HttpEntity<String> callGet(String url, MultiValueMap<String, String> params, HttpHeaders headers) throws Exception {
        int retry = 0;
        while (retry < MAX_RETRY) {
            try {
                return super.callGet(url, params, headers);
            } catch (Exception e) {
                login();
                retry++;
            }
        }
        throw new RuntimeException("login fail");
    }

    //    @RetryLogin
    @Override
    public HttpEntity<String> callPost(String url, HashMap<String, String> params, HttpHeaders headers) throws Exception {
        int retry = 0;
        while (retry < MAX_RETRY) {
            try {
                return super.callPost(url, params, headers);
            } catch (Exception e) {
                login();
                retry++;
            }
        }
        throw new RuntimeException("login fail");
    }


    @Override
    protected <T> HttpEntity<T> interceptCookie(HttpEntity<T> httpEntity) throws Exception {
        if (cookieMap.containsKey("xmd_session")) {
            HttpHeaders.writableHttpHeaders(httpEntity.getHeaders()).add("Xmd-Session", cookieMap.get("xmd_session"));
            HttpHeaders.writableHttpHeaders(httpEntity.getHeaders()).add("Referer", cookieMap.get("https://playmd.xmd.co.kr/"));
        }
        return super.interceptCookie(httpEntity);
    }


}
