package com.company.myweb.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 自訂 /actuator/info 端點的內容
 *
 * 實作 InfoContributor 介面 → Spring Boot 會自動偵測並注入到 /actuator/info 回應內
 * 呼叫 /myWeb/actuator/info 時可看到這裡放的 key/value（會以 "myWeb-info" 作為 JSON 的頂層 key）
 *
 * 也會被 Spring Boot Admin server 定期 poll，顯示在 dashboard 的 Info 分頁
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
