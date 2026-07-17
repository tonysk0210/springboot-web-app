# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案結構

本倉庫根目錄下有 **三個獨立** 的 Spring Boot Maven 專案並列存放 — **沒有** 父層聚合 POM。每個專案都有自己的 `pom.xml` 與 Maven Wrapper（`mvnw` / `mvnw.cmd`），必須進入各自的目錄才能建置或執行。

| 模組 | Port | 用途 |
|------|------|------|
| `MyWeb` | 8081 | 主要 Web 應用：Thymeleaf 前端 + REST API + Spring Data REST + Actuator client |
| `AdminActuator` | 8083 | Spring Boot Admin **伺服器**（Codecentric），用於監控 `MyWeb` |
| `ConsumingRestService` | 8082 | 獨立 client 端，透過 OpenFeign / RestTemplate / WebClient 呼叫 `MyWeb` 的 `/api/contact` |

`MyWeb` 與 `AdminActuator` 綁定 Spring Boot **3.3.12**；`ConsumingRestService` 使用 Spring Boot **3.4.6** + Spring Cloud **2024.0.1**。三者皆使用 Java 17。

## 常用指令

以下指令請在對應模組目錄下執行（`MyWeb/`、`AdminActuator/` 或 `ConsumingRestService/`）：

```powershell
# 啟動應用程式（開發模式 — 部分模組含 spring-boot-devtools 支援熱重載）
.\mvnw.cmd spring-boot:run

# 建置
.\mvnw.cmd clean package

# 執行模組內所有測試
.\mvnw.cmd test

# 執行單一測試類別
.\mvnw.cmd test "-Dtest=SomeTestClass"

# 執行單一測試方法
.\mvnw.cmd test "-Dtest=SomeTestClass#someMethod"
```

本機執行整套服務的典型順序：先啟動 `AdminActuator`（讓 `MyWeb` 能註冊上去），再啟動 `MyWeb`，若要測試 Feign client 才啟動 `ConsumingRestService`。三個 JVM 各佔一個 port，請在不同終端機分別啟動。

各模組 `src/test/java` 目錄雖已建立但目前為空 — 尚未定義測試框架慣例。

## MyWeb 架構（主要應用）

### 進入點與關鍵註解
`MyWebApplication.java` 透過註解組合了多個橫切關注點：
- `@EnableAspectJAutoProxy` — 啟用 `aspect/LoggerAspect`，以 `@Around` 為 `com.company.MyWeb..*` 底下 **所有** 方法加上執行時間 log，並以 `@AfterThrowing` 記錄例外
- `@EnableJpaRepositories("com.company.MyWeb.repository")` + `@EntityScan("com.company.MyWeb.model")` — 使用非預設套件，新增 Repository / Entity 時務必放在這兩個套件底下
- `@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")` — `auditor/AuditAwareImpl` 從 `SecurityContextHolder` 取得目前使用者名稱作為 `@CreatedBy` / `@LastModifiedBy` 欄位值；若無驗證則退回為 `"anonymousUser"`（**未登入註冊** 時仍能寫入 `person.created_by` 的關鍵）

### 資料層
- 使用內嵌 **H2** 記憶體資料庫（`jdbc:h2:mem:testdb`），Console 位於 `http://localhost:8081/h2-console`
- Schema **同時定義於兩處**：`src/main/resources/schema.sql` 的 DDL 以及 `model/` 底下的 JPA Entity。Hibernate DDL 模式為 `update`，兩者都會生效 — 新增欄位時 **兩邊都要改**
- 初始資料（含兩個 BCrypt 加密的預設帳號：`admin@gmail.com` / `admin` 與 `student@gmail.com` / `123`）放在 `data.sql`
- JPA Entity：`Person`、`Roles`、`Address`、`Plan`、`Courses`、`Contact`。`Person` 擁有到 `Roles`、`Address`、`Plan` 的外鍵，並透過 `person_courses` 中介表對 `Courses` 做多對多 — 全部為 `FetchType.EAGER`
- 稽核欄位來自 `model/BaseEntity`；`Person` **刻意覆蓋** `createdBy` 欄位，以避免未登入註冊時 insert 失敗
- `model/` 底下有 **兩個非 JPA 類別**，勿誤加 `@Entity`：
  - `News` — 純 POJO，透過 `NewsRepository` 用 **`JdbcTemplate` + `BeanPropertyRowMapper`** 讀取；`news` 表只存在於 `schema.sql`
  - `Profile` — 表單/傳輸用 DTO（更新個人資料流程使用），不入庫，帶自己的 Bean Validation 註解

### 安全性模型（`config/security/`）
- 自訂 `UsernamePwdAuthenticationProvider`：以 email 從 `person` 表查詢使用者，並用 `BCryptPasswordEncoder` 驗證密碼
- 表單登入：`/login` → 成功導向 `/dashboard`，失敗導向 `/login?error=true`。登出是 `LoginController` 內的 **GET** handler（繞過預設 CSRF），內部手動呼叫 `SecurityContextLogoutHandler`
- `SpringSecurityConfig` 內的路由規則：
  - `/student/**` → `ROLE_STUDENT`
  - `/admin/**`、`/api/**`、`/spring-data-api/**`、`/myWeb/actuator/**` → `ROLE_ADMIN`
  - `/dashboard`、`/profilePage`、`/updateProfile` → 任何已登入使用者
  - 其他一律 `permitAll`（包含 H2 console）
- 以下路徑 **關閉 CSRF**：H2 console、`/api/**`、`/spring-data-api/**`、actuator base path
- `hasRole("X")` 對應的權限字串為 `ROLE_X` — 前綴 `ROLE_` 由 provider 的 `getGrantedAuthorities` 加上

### Controller 與 Service 慣例
- 平行的兩套 controller 樹：
  - `controller/` — 使用 Thymeleaf 渲染的 `@Controller`。回傳字串會對應到 `src/main/resources/templates/<name>.html`（**不需** 寫 `.html` 副檔名）
  - `controller/authenticated/` — 同上，但用於登入後頁面（dashboard、profile、admin、student）
  - `rest/` — `@RestController`，路徑前綴 `/api/**`，同時支援 JSON 與 XML 回應（classpath 已包含 Jackson XML dataformat），透過 `Accept` header 進行內容協商
- 沒有邏輯、只回傳 view 的路由可註冊在 `config/MyWebConfig#addViewControllers`（例：`/about`）
- `service/` 承載「寫入 + 業務邏輯」：`PersonService.savePerson`（重複 email 檢查、密碼加密、角色指派、手動 `createdBy`）、`ContactService`（狀態流轉、分頁查詢）。**新增寫入或跨 repository 的邏輯請放 service**；controller 只在單純讀取時直接呼叫 repository
- 自訂 Bean Validation 放在 `myValidation/`：`@PasswordValidator`（單欄位）、`@FieldValueMatchValidator`（class 層級，用於 `Person` 檢查 `password`/`confirmPassword` 與 `email`/`confirmEmail` 是否一致）
- 例外處理：MVC 由 `config/GlobalExceptionHandler` 處理；REST 由 `rest/GlobalExceptionRestController` 處理

### MyWeb 對外提供的端點
- 網頁 UI：`/`、`/home`、`/about`、`/contact`、`/news`、`/login`、`/register`
- 自訂 REST：`/api/contact/*`（見 `ContactRestController`）
- Spring Data REST 自動端點：`/spring-data-api/**`（HAL Explorer 位於 `/spring-data-api/`）
- Actuator：`/myWeb/actuator/**`（所有端點皆開啟 — `management.endpoints.web.exposure.include=*`）
- Boot Admin client 以 `admin@gmail.com` / `admin`（放在 instance metadata）向 `http://localhost:8083` 註冊

### 自訂應用程式屬性
前綴為 `myweb.*`，於 `config/MyWebProperties` 以 `@Validated` 綁定。目前只有 `myweb.paginationPageSize`（範圍 5–10）。新增可調參數請集中在此處，不要用 `@Value` 到處散落。

### Log 顏色
`constant/ProjectConstant` 定義了 ANSI 顏色碼，供 `LoggerAspect` 與部分 controller 使用。`application.properties` 內的 console log pattern 使用了 Logback 顏色轉換器 — 終端機會有彩色輸出。

### Lombok 慣例
專案 pom 內含 **Lombok**（`optional=true`），全 codebase 已廣泛使用，請 **遵循既有寫法**，勿手寫 constructor / getter / setter / logger：
- `@Slf4j` 取代手寫 `LoggerFactory.getLogger(...)`
- `@RequiredArgsConstructor` 產生 final 欄位的 constructor injection（相容 Spring 4.3+ 單一 constructor 自動注入，`@Autowired` 可省）
- Entity / DTO 常用 `@Data` + `@NoArgsConstructor`；繼承 `BaseEntity` 時搭配 `@ToString(callSuper = true)`

## ConsumingRestService 架構

- `@EnableFeignClients(basePackages = "com.company.ConsumingRestService.proxy")` 啟用 `ContactProxy` Feign 介面
- `ContactProxy` 目標網址為 `http://localhost:8081/api/contact`（**寫死** — 必須先啟動 MyWeb）
- `config/ProjectConfiguration` 定義了三個平行的 HTTP client（Feign、`RestTemplate`、`WebClient`），全部預先設定好 Basic Auth `admin@gmail.com` / `admin` — 呼叫端可依需求選擇對應的 client
