package com.mudiocean.playmdapi.externalapi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class NewAgent {

    private final RestSessionClient restSessionClient;
    @Value("${CMEMCD}")
    private String CMEMCD;
    @Value("${CMEMPWD}")
    private String CMEMPWD;
    @Value("${CUSRID}")
    private String CUSRID;
    @Value("${CUSRPWD}")
    private String CUSRPWD;

    boolean ingLogin = false;
    Integer MAX_RETRY = 1;

    public NewAgent(RestSessionClient restSessionClient) {
        this.restSessionClient = restSessionClient;
    }


    public void login() throws RuntimeException {
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
        restSessionClient.postMethod(url, params, null);
        ingLogin = false;
    }

    @RetryLogin
    public ResponseEntity<String> getItem(String I_AGTCD, String I_COND) throws Exception {
        String url = "https://playmd.xmd.co.kr/api/xcom/xcom_codbarpr";
        String I_DATE = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String I_TAG = "0"; // TODO : what is this?
        String I_USRGUBN = "1"; // TODO : what is this?

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("I_AGTCD", I_AGTCD);
        params.add("I_COND", I_COND);
        params.add("I_DATE", I_DATE);
        params.add("I_TAG", I_TAG);
        params.add("I_USRGUBN", I_USRGUBN);

        return restSessionClient.getMethod(url, params, null);
    }



}
