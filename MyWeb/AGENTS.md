# Repository Guidelines

## 專案結構與模組配置

本專案是 Java 17、Spring Boot 3.3 的單一 Maven 應用程式。主要程式位於 `src/main/java/com/company/MyWeb/`，依責任分為 `controller/`、`service/`、`repository/`、`model/`、`config/` 與 `rest/`。Thymeleaf 頁面放在 `src/main/resources/templates/`，CSS 與圖片放在 `static/`；資料庫初始化與應用設定分別位於 `schema.sql`、`data.sql`、`application.properties`。測試位於 `src/test/java/`，並應鏡像主程式的 package 結構。`target/` 是產物目錄，不應提交。

## 建置、測試與本機開發

在 Windows PowerShell 使用 Maven Wrapper，避免依賴全域 Maven 版本：

- `.\mvnw.cmd spring-boot:run`：啟動開發伺服器；預設網址為 `http://localhost:8081`。
- `.\mvnw.cmd test`：執行全部 JUnit 測試。
- `.\mvnw.cmd clean package`：清除舊產物、測試並建立可執行 JAR。
- `java -jar target\MyWeb-0.0.1-SNAPSHOT.jar`：執行已封裝的應用程式。

本機啟動前，確認需要的 Spring Boot Admin 服務是否在 `localhost:8083`；不需要管理功能時，相關連線失敗不應影響核心頁面驗證。

## 程式風格與命名

Java 使用 4 個空格縮排並遵循既有 package 分層。類別採 `PascalCase`，方法與欄位採 `camelCase`，常數採 `UPPER_SNAKE_CASE`。Controller、Service、Repository 分別使用 `*Controller`、`*Service`、`*Repository` 後綴。Thymeleaf 與 CSS 檔名延續現有 `camelCase` 慣例。專案未配置專用 formatter；提交前請使用 IDE 格式化，並避免順帶重排無關程式碼。

## 測試規範

測試使用 JUnit 5、Spring Boot Test 與 Spring Security Test。測試類命名為 `*Tests`，測試方法應描述行為，例如 `registerRejectsDuplicateEmail()`。新增 controller、security 或 persistence 行為時，應加入對應的成功與失敗案例；提交前至少執行 `.\mvnw.cmd test`。

## Commit 與 Pull Request

目前 Git 歷史僅有初始化提交，尚無既定格式。請使用簡短祈使句並標明範圍，例如 `Add contact validation tests`。Pull Request 應說明目的、主要變更、驗證指令及相關 issue；頁面或 CSS 變更需附前後截圖。保持每個 PR 聚焦單一目的，避免提交 `target/`、IDE 設定或無關格式變更。

## 設定與安全性

`application.properties` 包含資料庫及管理服務設定。不得提交真實密碼、API key 或環境專屬憑證；請以環境變數或未追蹤的本機設定覆寫。修改 security、H2 console 或 actuator 曝露範圍時，需在 PR 中明確記錄風險與測試方式。
