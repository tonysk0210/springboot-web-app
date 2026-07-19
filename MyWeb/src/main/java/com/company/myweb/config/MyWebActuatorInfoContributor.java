package com.company.myweb.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 自訂 /actuator/info 端點內容
 * 實作 InfoContributor → Spring Boot 自動偵測；資料以 "myWeb-info" 為頂層 key
 * 出現在 /myWeb/actuator/info 回應與 Spring Boot Admin dashboard 的 Info 分頁
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
