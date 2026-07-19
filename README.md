# springboot-web-app

一個以 **Spring Boot 4.1 + Java 25** 打造的多模組練習專案，涵蓋 Web MVC、REST、微服務通訊、與監控四個面向。

倉庫下並列 **三個獨立** 的 Spring Boot 應用（沒有父層聚合 POM），可分別啟動，透過各自的 port 互相溝通。

| 模組 | Port | 定位 |
|---|---|---|
| [`MyWeb`](./MyWeb) | **8081** | 主應用：Thymeleaf 前端 + 表單登入 + 自訂 REST API + Spring Data REST + Actuator client |
| [`AdminActuator`](./AdminActuator) | **8083** | Codecentric **Spring Boot Admin 伺服器**，用於監控 `MyWeb` |
| [`ConsumingRestService`](./ConsumingRestService) | **8082** | REST client 練習：以 **OpenFeign / RestTemplate / WebClient** 三種方式呼叫 `MyWeb` 的 `/api/contact` |

---

## 目錄

- [架構總覽](#架構總覽)
- [技術棧](#技術棧)
- [環境需求](#環境需求)
- [快速開始](#快速開始)
- [模組詳細說明](#模組詳細說明)
  - [MyWeb](#myweb)
  - [AdminActuator](#adminactuator)
  - [ConsumingRestService](#consumingrestservice)
- [常用指令](#常用指令)
- [預設帳號](#預設帳號)
- [設定檔要點](#設定檔要點)
- [測試](#測試)
- [Boot 4 / Java 25 升級筆記](#boot-4--java-25-升級筆記)
- [Troubleshooting](#troubleshooting)
- [授權](#授權)

---

## 架構總覽

```
┌─────────────────────────┐       HTTP (Basic Auth)        	┌─────────────────────────┐
│ ConsumingRestService    │  ───────────────────────────►  	│ MyWeb                   │
│ :8082                   │   /api/contact/**               │ :8081                   │
│                         │                                 │                         │
│ - Feign (ContactProxy)  │                                 │ - Thymeleaf UI          │
│ - RestTemplate          │                                 │ - REST API (/api/**)    │
│ - WebClient             │                                 │ - Spring Data REST      │
└─────────────────────────┘                                 │ - H2 (in-memory)        │
                                                            │ - Spring Security       │
                                                            └───────────┬─────────────┘
                                                                        │
                                                                        │ Actuator 註冊
                                                                        │ (預設關閉)
                                                                        ▼
                                                            ┌─────────────────────────┐
                                                            │ AdminActuator           │
                                                            │ :8083                   │
                                                            │                         │
                                                            │ - Spring Boot Admin     │
                                                            │   Server (Codecentric)  │
                                                            └─────────────────────────┘
```

---

## 技術棧

**共通**
- Java **25**
- Spring Boot **4.1.0**（`spring-boot-starter-parent`；三個模組各自宣告）
- Lombok（`optional=true`）— 全 codebase 廣泛使用 `@Slf4j` / `@RequiredArgsConstructor` / `@Data`
- Maven Wrapper（`mvnw` / `mvnw.cmd`）— 無需另外安裝 Maven

**MyWeb**
- `spring-boot-starter-webmvc`（Boot 4 建議命名，取代已 deprecated 的 `spring-boot-starter-web`）
- Spring Security（自訂 `UsernamePwdAuthenticationProvider` + BCrypt）
- Spring Data JPA + Hibernate 7（透過 Boot BOM）
- H2 in-memory database + `spring-boot-h2console`（Boot 4 拆出的獨立模組）
- Thymeleaf + `thymeleaf-extras-springsecurity6`
- Spring Data REST + HAL Explorer
- Actuator + `hibernate-micrometer`（groupId 為 `org.hibernate.orm`）
- `jackson-dataformat-xml`（讓 `@RestController` 依 Accept header 回 JSON 或 XML）
- `spring-boot-admin-starter-client` **4.1.2**
- `spring-boot-devtools`

**AdminActuator**
- `spring-boot-starter-webmvc`
- `spring-boot-admin-starter-server` **4.1.2**

**ConsumingRestService**
- Spring Cloud **2025.1.2 (Oakwood release train)**（對應 Spring Boot 4.1）
- `spring-cloud-starter-openfeign` + `spring-cloud-starter-loadbalancer`
- `spring-boot-starter-restclient`（Boot 4 拆分出來，`RestTemplateBuilder` 位於此模組）
- `spring-boot-starter-webflux`（提供 `WebClient`）

---

## 環境需求

- **JDK 25**（三個模組 `pom.xml` 皆宣告 `<java.version>25</java.version>`）
- **PowerShell** 或任何 shell（本 README 範例使用 Windows PowerShell）
- Port `8081`、`8082`、`8083` 皆未被佔用
- 網路連線（首次執行 Maven Wrapper 會下載依賴）

> ⚠️ **Java 23+ 停用了預設的 classpath 隱式 annotation processing** — `MyWeb/pom.xml` 與 `ConsumingRestService/pom.xml` 已顯式在 `maven-compiler-plugin` 加上 Lombok 的 `<annotationProcessorPaths>`。若你新增第四個模組並使用 Lombok，記得複製這段設定。

---

## 快速開始

Clone 後，在**三個獨立終端機**分別啟動：

```powershell
# 終端機 1：主應用（8081）— 一定要有
cd MyWeb
.\mvnw.cmd spring-boot:run

# 終端機 2：Boot Admin 伺服器（8083）— 想使用 UI 監控時再啟動
cd AdminActuator
.\mvnw.cmd spring-boot:run

# 終端機 3：REST client（8082）— 想測試 Feign/RestTemplate/WebClient 時再啟動
cd ConsumingRestService
.\mvnw.cmd spring-boot:run
```

啟動後可造訪：

- 前端首頁：<http://localhost:8081/>
- 登入頁：<http://localhost:8081/login>（帳號見 [預設帳號](#預設帳號)）
- H2 console：<http://localhost:8081/h2-console>（JDBC URL 見下方）
- HAL Explorer：<http://localhost:8081/spring-data-api/>
- Actuator：<http://localhost:8081/myWeb/actuator>
- Boot Admin UI：<http://localhost:8083>（需先把 `MyWeb` 的 `spring.boot.admin.client.enabled` 改為 `true` 並重啟）
- ConsumingRestService：<http://localhost:8082/getMessages?status=OPEN>

---

## 模組詳細說明

### MyWeb

主 Web 應用，包辦「前端頁面 + REST API + 資料庫 + 安全性 + 監控 client」五件事。

#### 進入點

`com.company.myweb.MyWebApplication` 只帶三個 class-level 註解：

- `@SpringBootApplication`（Entity/Repository/AOP 都在預設 scan 範圍內，不需要 `@EnableAspectJAutoProxy` / `@EntityScan` / `@EnableJpaRepositories`）
- `@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")` — 由 `auditor/AuditAwareImpl` 從 `SecurityContextHolder` 取得目前使用者名稱給 `@CreatedBy` / `@LastModifiedBy`；未登入時退回為 `"anonymousUser"`
- `@EnableConfigurationProperties(MyWebProperties.class)` — 登記 `myweb.*` 屬性 bean

#### 資料層

- **H2 in-memory database**：`jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`（`DB_CLOSE_DELAY=-1` 讓 devtools restart 與 HikariCP 回收連線時 DB 不被銷毀）
- H2 Console：<http://localhost:8081/h2-console>
  - JDBC URL：`jdbc:h2:mem:mydb`
  - User：`sa`
  - Password：（空）
- **`spring.jpa.hibernate.ddl-auto=validate`** — `src/main/resources/sql/schema.sql` 是 schema 的**唯一真相**；Hibernate 只在啟動時驗證 Entity 對應現有 schema，型別／欄位不符會直接啟動失敗。**新增或變更欄位時，`schema.sql` 與 `model/` 底下 Entity 都要改。**
- 初始資料：`src/main/resources/sql/data.sql`（含兩個 BCrypt 加密的預設帳號）

##### Entity 與非 Entity 類別

| 類別 | 類型 | 說明 |
|---|---|---|
| `Person` | JPA Entity | 使用者主表；到 `Roles`/`Address`/`Plan` 有外鍵；透過 `person_courses` 中介表對 `Courses` 多對多；**刻意覆蓋** `BaseEntity` 的 `createdBy`，讓未登入註冊也能寫入 |
| `Roles`, `Address`, `Plan`, `Courses`, `Contact` | JPA Entity | 一般 Entity；`Person` 上的關聯全為 `FetchType.EAGER` |
| `BaseEntity` | `@MappedSuperclass` | 稽核欄位 `createdBy` / `createdAt` / `updatedBy` / `updatedAt` |
| `News` | 純 POJO ⚠️ | 透過 `NewsRepository` 用 **`JdbcTemplate` + `BeanPropertyRowMapper`** 讀取；`news` 表只存在於 `schema.sql`。**勿誤加 `@Entity`** |
| `Profile` | DTO ⚠️ | 更新個人資料表單專用；帶自己的 Bean Validation，不入庫 |

#### 安全性模型（`config/security/`）

自訂 `UsernamePwdAuthenticationProvider`：以 email 從 `person` 表查詢，用 `BCryptPasswordEncoder` 驗密碼。

| 路徑 | 需要權限 |
|---|---|
| `/student/**` | `ROLE_STUDENT` |
| `/admin/**`、`/api/**`、`/spring-data-api/**`、`/myWeb/actuator/**` | `ROLE_ADMIN` |
| `/dashboard`、`/profilePage`、`/updateProfile` | 任何已登入使用者 |
| 其他（含 `/`、`/login`、`/register`、H2 console 等） | `permitAll` |

- 表單登入：`/login` 成功 → `/dashboard`；失敗 → `/login?error=true`
- 登出是 `LoginController` 內的 **GET** handler（繞過預設 CSRF），內部手動呼叫 `SecurityContextLogoutHandler`
- **關閉 CSRF 的路徑**：H2 console、`/api/**`、`/spring-data-api/**`、actuator base path
- `hasRole("X")` 對應的權限字串為 `ROLE_X`（前綴由 provider 的 `getGrantedAuthorities` 加上）

#### 對外 endpoint

| 類型 | 路徑 | 說明 |
|---|---|---|
| 網頁 UI | `/`、`/home`、`/about`、`/contact`、`/news`、`/login`、`/register` | Thymeleaf 渲染 |
| 網頁 UI（需登入） | `/dashboard`、`/profilePage`、`/updateProfile`、`/admin/**`、`/student/**` | `controller/authenticated/` |
| 自訂 REST | `/api/contact/**` | `ContactRestController`；同時支援 JSON 與 XML（依 Accept header）|
| Spring Data REST | `/spring-data-api/**` | 自動產生的 CRUD 端點 |
| HAL Explorer | `/spring-data-api/` | Spring Data REST 的互動式瀏覽器 |
| Actuator | `/myWeb/actuator/**` | 所有端點皆開啟（`management.endpoints.web.exposure.include=*`）|

##### `/api/contact/**` 端點清單

| Method | 路徑 | 說明 |
|---|---|---|
| `GET` | `/api/contact/getContactMessageByStatus?status=OPEN` | 依 status query param 取清單 |
| `GET` | `/api/contact/getContactMessageByStatusRequestBody` | 依 request body 內的 status 取清單 |
| `POST` | `/api/contact/saveContactMessage` | 儲存訊息；必填 header `invocationFrom` |
| `DELETE` | `/api/contact/deleteContactMessage` | 依 body 的 `contactId` 刪除 |
| `PATCH` | `/api/contact/updateContactMessageStatus` | 依 body 的 `contactId` 把狀態改為 `CLOSED` |

#### 前端 CSS / JS 分層

`templates/` 只 link `static/css/app.css`；`app.css` 以 `@import` 串接三個 layer（順序即優先序）：

1. `foundation.css` — 設計 token（顏色、字級、間距、breakpoint 等）
2. `components.css` — 跨頁重用的 BEM 元件（navbar、卡片、表單元件⋯）
3. `pages.css` — 只針對單一頁面的樣式覆寫

新增樣式時按語意選層。共用 JS 行為統一放 `static/js/app.js`。

#### 自訂應用程式屬性

前綴為 `myweb.*`，於 `config/MyWebProperties` 以 `@Validated` 綁定。目前只有 `myweb.paginationPageSize`（範圍 5–10）。**新增可調參數請集中在此處**，不要用 `@Value` 到處散落。

---

### AdminActuator

**Spring Boot Admin Server**（Codecentric 4.1.2）— 提供 Web UI 監控其他 Spring Boot 應用（本專案僅供 `MyWeb` 註冊）。

- 進入點：`com.company.AdminActuator.AdminActuatorApplication`（帶 `@EnableAdminServer`）
- 訪問：<http://localhost:8083>
- 依賴極簡：只有 `spring-boot-starter-webmvc` + `spring-boot-admin-starter-server`

> `MyWeb` 的 SBA client 預設為關閉，避免 `AdminActuator` 未啟動時 log 洗版。**要看監控 UI 需先把 `MyWeb/src/main/resources/application.properties` 內的 `spring.boot.admin.client.enabled` 改為 `true` 並重啟 `MyWeb`。**

**版本鏈鎖定**：SBA 4.1.x 對應 Boot 4.1；`MyWeb` 與 `AdminActuator` 兩邊 `spring-boot-admin.version` 必須一致。

---

### ConsumingRestService

REST client 練習，以三種 HTTP client 呼叫 `MyWeb` 的 `/api/contact` — 全部預先配置好 Basic Auth（`admin@gmail.com` / `admin`）。

- 進入點：`com.company.ConsumingRestService.ConsumingRestServiceApplication`（帶 `@EnableFeignClients(basePackages = "com.company.ConsumingRestService.proxy")`）
- `config/ProjectConfiguration` 定義三個平行 bean：Feign default config、`RestTemplate`、`WebClient`
- `proxy/ContactProxy`（Feign）目標 URL **寫死** 為 `http://localhost:8081/api/contact` — 必須先啟動 `MyWeb`

#### 對外 endpoint

| Method | 路徑 | 使用的 client | 說明 |
|---|---|---|---|
| `GET` | `/getMessages?status=OPEN` | **Feign** (`ContactProxy`) | 依 status 取聯絡訊息 |
| `POST` | `/saveMessages` | **RestTemplate** | 儲存訊息（`invocationFrom: RestTemplate`）|
| `POST` | `/saveMessagesWebClient` | **WebClient** | 儲存訊息（`invocationFrom: WebClient`，回傳 `Mono<Response>`）|

範例 body（用於 `POST /saveMessages` 或 `POST /saveMessagesWebClient`）：

```json
{
  "name": "RestTemplate",
  "mobile": "1234567890",
  "email": "resttemplate@gmail.com",
  "subject": "resttemplate",
  "message": "resttemplate message",
  "status": "OPEN"
}
```

---

## 常用指令

以下所有指令請 `cd` 到對應模組目錄（`MyWeb/`、`AdminActuator/` 或 `ConsumingRestService/`）後執行。

```powershell
# 啟動應用程式（開發模式；MyWeb 與 ConsumingRestService 含 spring-boot-devtools）
.\mvnw.cmd spring-boot:run

# 建置 jar（產物在 target/）
.\mvnw.cmd clean package

# 執行模組內所有測試
.\mvnw.cmd test

# 執行單一測試類別
.\mvnw.cmd test "-Dtest=SomeTestClass"

# 執行單一測試方法
.\mvnw.cmd test "-Dtest=SomeTestClass#someMethod"

# 只更新執行中應用的 static 資源與 templates（免重啟；配合 devtools 效果更好）
.\mvnw.cmd resources:resources

# 執行打包好的 jar
java -jar target\MyWeb-0.0.1-SNAPSHOT.jar
```

> **PowerShell 引號**：`-Dtest=...` 必須用雙引號包起來，否則 PowerShell 會把 `#` 當成註解起點。

---

## 預設帳號

`MyWeb/src/main/resources/sql/data.sql` 插入了兩個 BCrypt 加密的帳號：

| Email | Password | Role | 用途 |
|---|---|---|---|
| `admin@gmail.com` | `admin` | `ROLE_ADMIN` | 存取 `/admin/**`、`/api/**`、`/spring-data-api/**`、Actuator |
| `student@gmail.com` | `123` | `ROLE_STUDENT` | 存取 `/student/**` |

> `ConsumingRestService` 內建的三個 HTTP client 都預先配置 `admin@gmail.com` / `admin` 做 Basic Auth，可直接呼叫 `MyWeb` 需要 `ROLE_ADMIN` 的 `/api/**` 端點。

---

## 設定檔要點

### `MyWeb/src/main/resources/application.properties`

| Property | 值 | 說明 |
|---|---|---|
| `server.port` | `8081` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:mem:mydb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` | H2 in-memory DB |
| `spring.jpa.hibernate.ddl-auto` | `validate` | Hibernate 只驗證、不動 DB — 以 `schema.sql` 為 schema 唯一真相 |
| `spring.sql.init.schema-locations` | `classpath:sql/schema.sql` | DDL 位置 |
| `spring.sql.init.data-locations` | `classpath:sql/data.sql` | 初始資料 |
| `spring.h2.console.enabled` | `true` | 啟用 `/h2-console`（需 `spring-boot-h2console` 依賴）|
| `spring.data.rest.basePath` | `/spring-data-api` | Spring Data REST 端點與 HAL Explorer 前綴 |
| `management.endpoints.web.base-path` | `/myWeb/actuator` | Actuator base path |
| `management.endpoints.web.exposure.include` | `*` | 開所有 Actuator endpoint（**生產請改白名單**）|
| `spring.boot.admin.client.enabled` | `false` | Boot Admin client 預設關閉 |
| `myweb.paginationPageSize` | `5` | 後台聯絡訊息分頁每頁筆數（5–10）|

`AdminActuator` 與 `ConsumingRestService` 的 `application.properties` 只設定 `spring.application.name` 與 `server.port`，其餘皆採預設值。

---

## 測試

- **框架**：JUnit 5 + Spring Boot Test + Spring Security Test（`MyWeb` 已引入相關 dependencies）
- **命名慣例**：測試類 `*Tests`；測試方法以行為描述（例：`registerRejectsDuplicateEmail()`）
- **現況**：三個模組的 `src/test/java` 除了空的 `MyWebApplicationTests` 外都尚無內容 — 是接下來要補上的區塊

---

## Boot 4 / Java 25 升級筆記

以下是升級到 Spring Boot 4.1 + Java 25 過程中踩過、非 obvious 的點：

- **`spring-boot-starter-web` 已 deprecated** — 三個模組都改用 **`spring-boot-starter-webmvc`**（舊名仍可 delegate，但新程式碼與升級請用新名字讓 MVC vs WebFlux 意圖明確）
- **Boot 4 拆分了 autoconfigure 模組**，多個常用類別被搬到 feature-specific 套件：
  - `@EntityScan`：`org.springframework.boot.autoconfigure.domain` → **`org.springframework.boot.persistence.autoconfigure`**（本專案 Entity 已在預設 scan 範圍所以未用到；若日後把 model 移出 root package 需要顯式匯入時要走新路徑）
  - `PathRequest`（servlet）：`org.springframework.boot.autoconfigure.security.servlet` → **`org.springframework.boot.security.autoconfigure.web.servlet`**（`SpringSecurityConfig` 使用）
  - `RestTemplateBuilder`：`org.springframework.boot.web.client` → **`org.springframework.boot.restclient`**（`ConsumingRestService` 需明確引入 `spring-boot-starter-restclient`）
  - **H2 Console** 被拆為獨立模組 **`spring-boot-h2console`** — `MyWeb/pom.xml` 已引入，才能讓 `spring.h2.console.enabled=true` 生效
- **Hibernate groupId 變更**：`hibernate-micrometer` 的 groupId 是 **`org.hibernate.orm`**（Hibernate 6+ 命名）；版本交由 Spring Boot BOM 管理，勿硬編碼
- **Lombok annotation processor 必須顯式宣告** — 見前文「[環境需求](#環境需求)」的 warning
- **Spring Boot Admin 版本鏈**：SBA 4.1.x 對應 Boot 4.1；client（`MyWeb`）與 server（`AdminActuator`）兩邊 `spring-boot-admin.version` 必須一致

---

## Troubleshooting

### 啟動時 `Schema-validation: missing table/column`
`ddl-auto=validate` 抓到 `sql/schema.sql` 與 JPA Entity 不一致。同步兩邊即可。

### 401 呼叫 `/api/**`
- 沒帶 Basic Auth，或帳號權限不足（`/api/**` 需要 `ROLE_ADMIN`）
- 從 `ConsumingRestService` 呼叫時，`ProjectConfiguration` 應已預配置好 `admin@gmail.com` / `admin`；請確認 `MyWeb` 已啟動

### Feign client `ConnectException: Connection refused`
`ContactProxy` 目標 URL 寫死為 `http://localhost:8081/api/contact` — 先啟動 `MyWeb`。

### Boot Admin UI 看不到 `MyWeb`
`MyWeb` 的 `spring.boot.admin.client.enabled` 預設為 `false`。改為 `true` 並重啟 `MyWeb`；同時確認 `AdminActuator` 已在 8083 啟動。

### `@Slf4j` / `@Data` 未生效（`log` 變數找不到、getter/setter 不存在）
Java 23+ 停用了預設 annotation processing。確認 `pom.xml` 的 `maven-compiler-plugin` 有顯式宣告 Lombok 為 annotation processor。

### H2 console 開不起來（`/h2-console` 404）
確認 `MyWeb/pom.xml` 有 **`spring-boot-h2console`** 依賴（Boot 4 拆出獨立模組），且 `spring.h2.console.enabled=true`。

### 中文亂碼（Windows PowerShell）
在啟動前執行 `chcp 65001` 切到 UTF-8 代碼頁；或改用 Windows Terminal。

---

## 授權

本專案為個人學習用途，未附加授權條款。

