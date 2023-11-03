package com.mudiocean.playmdapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mudiocean.playmdapi.externalapi.PlayMDRestSessionClientImp;
import com.mudiocean.playmdapi.externalapi.RestSessionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class PlayMDController {

    @Autowired
    @Qualifier("playMDRestSessionClientImp")
    RestSessionClient restSessionClient;



    @GetMapping("/api/v2/playmd")
    public String getPlayMD(@RequestParam(value = "storecode", defaultValue = "02") String storecode,
                            @RequestParam(value = "keyword", defaultValue = "js") String keyword
    ) {
        try {
            String url = "https://playmd.xmd.co.kr/api/xcom/xcom_codbarpr";
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("I_DATE", new SimpleDateFormat("yyyyMMdd").format(new Date()));
            params.add("I_TAG", "0");
            params.add("I_USRGUBN", "1");
            params.add("I_AGTCD", storecode);
            params.add("I_COND", keyword);

            HttpEntity<String> response = restSessionClient.callGet(url, params, null);
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

}
