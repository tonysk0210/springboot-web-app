# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 模組定位

`MyWeb` 是倉庫下三個獨立 Spring Boot 專案之一（其他兩個為 `AdminActuator`、`ConsumingRestService`，位於同層目錄）。三者 **沒有** 父層聚合 POM，各自建置與執行；MyWeb 的責任為：

- Thymeleaf 前端 + 表單登入
- 自訂 REST API（`/api/**`）與 Spring Data REST（`/spring-data-api/**`）
- Actuator client（向 `AdminActuator`（port 8083）註冊）

服務綁定 Spring Boot **4.1.0** + Java **25**，Port **8081**。

本機典型啟動順序：先啟動 `../AdminActuator`（8083）讓 MyWeb 能註冊上去，再啟動 MyWeb；若要測試 Feign client，才啟動 `../ConsumingRestService`（8082）。

## 常用指令

以下指令在 MyWeb 目錄下執行（此檔案所在目錄）：

```powershell
# 啟動應用程式（含 spring-boot-devtools 支援熱重載）
.\mvnw.cmd spring-boot:run

# 建置
.\mvnw.cmd clean package

# 執行全部測試
.\mvnw.cmd test

# 執行單一測試類別
.\mvnw.cmd test "-Dtest=SomeTestClass"

# 執行單一測試方法
.\mvnw.cmd test "-Dtest=SomeTestClass#someMethod"
```

`src/test/java` 目錄雖已建立但目前為空 — 尚未定義測試框架慣例。

## 架構

### 進入點與關鍵註解
`MyWebApplication.java` 透過註解組合了多個橫切關注點：
- `@EnableAspectJAutoProxy` — 啟用 `aspect/LoggerAspect`，以 `@Around` 為 `com.company.MyWeb..*` 底下 **所有** 方法加上執行時間 log，並以 `@AfterThrowing` 記錄例外
- `@EnableJpaRepositories("com.company.MyWeb.repository")` + `@EntityScan("com.company.MyWeb.model")` — 使用非預設套件，新增 Repository / Entity 時務必放在這兩個套件底下
- `@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")` — `auditor/AuditAwareImpl` 從 `SecurityContextHolder` 取得目前使用者名稱作為 `@CreatedBy` / `@LastModifiedBy` 欄位值；若無驗證則退回為 `"anonymousUser"`（**未登入註冊** 時仍能寫入 `person.created_by` 的關鍵）

### 資料層
- 使用內嵌 **H2** 記憶體資料庫（`jdbc:h2:mem:testdb`），Console 位於 `http://localhost:8081/h2-console`
- Schema **同時定義於兩處**：`src/main/resources/sql/schema.sql` 的 DDL 以及 `model/` 底下的 JPA Entity。Hibernate DDL 模式為 `update`，兩者都會生效 — 新增欄位時 **兩邊都要改**
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

### 對外提供的端點
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
- **Java 23+ 停用了預設的 classpath 隱式 annotation processing** — `pom.xml` 已顯式配置 `maven-compiler-plugin` 的 `<annotationProcessorPaths>` 宣告 Lombok；勿隨意移除，否則 `@Slf4j`、`@Data` 等不會生效

## Boot 4 / Java 25 建置注意事項

以下是升級到 Spring Boot 4.1 + Java 25 過程中踩過、非 obvious 的點：

- **`spring-boot-starter-web` 已 deprecated**，改用 **`spring-boot-starter-webmvc`**（舊名仍可 delegate，但新程式碼與升級請用新名字讓 MVC vs WebFlux 意圖明確）
- **Boot 4 拆分了 autoconfigure 模組**，多個常用類別被搬到 feature-specific 套件：
  - `@EntityScan`：舊 `org.springframework.boot.autoconfigure.domain` → 新 **`org.springframework.boot.persistence.autoconfigure`**（`MyWebApplication.java` 使用）
  - `PathRequest`（servlet）：舊 `org.springframework.boot.autoconfigure.security.servlet` → 新 **`org.springframework.boot.security.autoconfigure.web.servlet`**（`SpringSecurityConfig` 使用）
- **Hibernate groupId 變更**：`hibernate-micrometer` 的 groupId 是 **`org.hibernate.orm`**；版本交由 Spring Boot BOM 管理，勿硬編碼
- **Spring Boot Admin 版本鏈**：`spring-boot-admin.version` 屬性必須與 `AdminActuator/pom.xml` 內相同（client ↔ server）
