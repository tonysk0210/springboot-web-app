package com.company.MyWeb.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is to customize the /actuator/info endpoint
 */
@Component
public class MyWebActuatorInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, String> myWebMap = new HashMap<>();
        myWebMap.put("App Name", "MyWeb");
        myWebMap.put("App Description", "MyWeb Application for Students and Admin");
        myWebMap.put("App Version", "1.0.0");
        myWebMap.put("Contact Email", "anthonyshangkuan@gmail.com");
        builder.withDetail("myWeb-info", myWebMap);
    }
}
