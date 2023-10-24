package com.mudiocean.playmdapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mudiocean.playmdapi.externalapi.Agent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProxyPlayMD {

    private final Agent agent;

    @Autowired
    public ProxyPlayMD(Agent agent) {
        this.agent = agent;
    }

    @GetMapping("/playmd")
    public String getPlayMD(@RequestParam(value = "storecode", defaultValue = "02") String storecode,
                            @RequestParam(value = "keyword", defaultValue = "") String keyword
    ) {
        // TODO : validate storecode, keyword
        try {
            JsonNode data = agent.getItem(storecode, keyword);
            return data.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
