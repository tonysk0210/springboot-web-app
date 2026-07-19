# Repository Guidelines

## 專案結構與模組配置

此 repository 包含三個獨立的 Java 25 / Spring Boot 4.1 Maven 專案，根目錄沒有聚合 `pom.xml`。`MyWeb/` 是主要 MVC 應用程式，含 Thymeleaf、Spring Security、JPA/H2、Actuator 與靜態資源；`AdminActuator/` 提供 Spring Boot Admin Server；`ConsumingRestService/` 示範以 RestClient、WebClient 與 OpenFeign 呼叫服務。各模組的 Java 程式、設定與測試分別位於 `src/main/java/`、`src/main/resources/`、`src/test/java/`。`MyWeb/AGENTS.md` 是該模組的補充規範，處理其中檔案時也必須遵守。

## 建置、測試與本機開發

所有命令都應在目標模組內執行；Windows PowerShell 範例：

- `cd MyWeb; .\mvnw.cmd spring-boot:run`：在 port `8081` 啟動主要網站。
- `cd AdminActuator; .\mvnw.cmd spring-boot:run`：在 port `8083` 啟動管理介面。
- `cd ConsumingRestService; .\mvnw.cmd spring-boot:run`：在 port `8082` 啟動 REST client 範例。
- `.\mvnw.cmd test`：編譯並執行目前模組的測試。
- `.\mvnw.cmd clean package`：清除舊產物、測試並建立 JAR。

需要驗證跨服務行為時，先啟動被呼叫端，再啟動 client。不要提交任何模組的 `target/`。

## 程式風格與命名

Java 使用 4 個空格縮排；類別使用 `PascalCase`，方法與欄位使用 `camelCase`，常數使用 `UPPER_SNAKE_CASE`。沿用既有 `*Controller`、`*Service`、`*Repository`、`*Configuration` 後綴及 package 分層。Controller 保持精簡，業務邏輯放入 service，外部 HTTP 整合集中於 proxy 或 config。專案未設定獨立 formatter，提交前應使用 IDE 格式化並避免無關的大範圍排版變更。

## 測試規範

測試使用 JUnit 5 與 Spring Boot Test；測試類命名為 `*Tests`，方法名稱應描述行為，例如 `returnsContactWhenIdExists()`。修改 controller、security、資料庫或外部服務整合時，應涵蓋成功與錯誤路徑。至少在受影響的每個模組執行 `.\mvnw.cmd test`。

## Commit 與 Pull Request

目前歷史多為 `update` 等短訊息；新提交請改用具體祈使句，例如 `Add contact client error handling`。PR 應說明目的、受影響模組、驗證命令及相關 issue；畫面變更附前後截圖，跨服務變更列出啟動順序與 ports。避免混入 IDE 設定或無關重構。

## 設定與安全性

設定集中於各模組的 `application.properties`。不得提交真實密碼、API key 或環境專屬端點；調整 Security、H2 Console、Actuator 暴露範圍或管理憑證時，須在 PR 說明風險與本機驗證方式。
