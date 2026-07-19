# Repository Guidelines

## 專案結構與模組配置

本專案是 Java 25、Spring Boot 4.1 的單一 Maven MVC 應用程式。Java 原始碼位於 `src/main/java/com/company/myweb/`，依責任分為 `controller/`、`service/`、`repository/`、`model/`、`config/`、`rest/` 與 `myValidation/`。Thymeleaf 頁面位於 `src/main/resources/templates/`；`nav/` 放公開頁面，`authenticated/` 放角色頁面，`text/` 放教學說明 fragment。資料庫初始化檔位於 `src/main/resources/sql/`。測試放在 `src/test/java/`，`target/` 為產物，不得提交。

## 建置、測試與本機開發

在 Windows PowerShell 使用 Maven Wrapper：

- `.\mvnw.cmd spring-boot:run`：啟動網站，預設為 `http://localhost:8081`。
- `.\mvnw.cmd test`：執行所有 JUnit 測試。
- `.\mvnw.cmd clean test`：清除舊產物後重新編譯及測試。
- `.\mvnw.cmd clean package`：測試並建立可執行 JAR。
- `.\mvnw.cmd resources:resources`：僅更新執行中的模板、CSS、JavaScript 等資源。

## 程式風格與命名

Java 使用 4 個空格縮排；類別採 `PascalCase`，方法及欄位採 `camelCase`，常數採 `UPPER_SNAKE_CASE`。沿用 `*Controller`、`*Service`、`*Repository` 後綴及現有 package 分層。Controller 應負責 request/response flow，業務邏輯放入 service，資料存取交由 repository。

所有頁面以繁體中文呈現，route、model key、資料欄位及技術識別字保持原文。CSS 由 `static/css/app.css` 統一載入：基礎 token 放 `foundation.css`，共用 BEM 元件放 `components.css`，頁面特例放 `pages.css`。避免 inline style 與重複 selector；JavaScript 共用行為放 `static/js/app.js`。

## 測試規範

使用 JUnit 5、Spring Boot Test 與 Spring Security Test。測試類命名為 `*Tests`，方法描述行為，例如 `registerRejectsDuplicateEmail()`。修改 controller、security、validation 或 persistence 時，需涵蓋成功與失敗路徑。UI 修改至少檢查桌面與行動版，確認無文字重疊或水平頁面溢位。

## Commit 與 Pull Request

現有歷史多使用 `update`、`fix styling` 等短訊息；新提交應更具體並使用祈使句，例如 `Fix contact table overflow`。PR 需說明目的、主要變更、驗證指令及相關 issue；UI 變更附前後截圖。不要提交憑證、`target/`、IDE 設定或無關格式化變更。

## 設定與安全性

`application.properties` 包含 H2、Security、Actuator 與 Spring Boot Admin 設定。不得提交真實密碼、API key 或環境專屬憑證。修改權限規則、H2 Console 或 Actuator 曝露範圍時，應在 PR 中記錄風險與驗證方式。
