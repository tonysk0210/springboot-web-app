package com.company.myweb.rest;

import com.company.myweb.constant.ProjectConstant;
import com.company.myweb.model.Contact;
import com.company.myweb.repository.ContactRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Contact 的 REST API（/api/contact/**）
 *
 * produces = { JSON, XML }：依 client 送來的 Accept header 決定回傳格式：
 *   - Accept: application/json → 回 JSON（Jackson）
 *   - Accept: application/xml  → 回 XML（jackson-dataformat-xml）
 */
@Slf4j
@RestController
@RequestMapping(path = "/api/contact", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
@CrossOrigin(origins = "*") // 允許所有 origin 存取（例如 http://localhost:3000）；正式環境應限縮白名單
public class ContactRestController {

    private final ContactRepository contactRepository;

    @Autowired
    public ContactRestController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * 依 status 取聯絡訊息清單（用 query parameter）
     */
    @GetMapping("/getContactMessageByStatus")
    public List<Contact> getContactMessageByStatus(@RequestParam String status) {
        return contactRepository.findByStatus(status);
    }

    /**
     * 依 status 取聯絡訊息清單（用 @RequestBody）
     * @RequestBody：把 HTTP body 的 JSON/XML 反序列化成 Contact 物件
     */
    @GetMapping("/getContactMessageByStatusRequestBody")
    public List<Contact> getContactMessageByStatusRequestBody(@RequestBody Contact contact) {
        return (contact != null && contact.getStatus() != null) ? contactRepository.findByStatus(contact.getStatus()) : List.of();
    }

    /**
     * 儲存聯絡訊息：接受一個必填 HTTP header（invocationFrom）+ 一個驗證過的 Contact JSON body
     * log 記錄請求 → 存 DB → 回 ResponseEntity 包 Response
     */
    @PostMapping("/saveContactMessage")
    public ResponseEntity<Response> saveContactMessage(@RequestHeader("invocationFrom") String invocationFrom, @Valid @RequestBody Contact contact) {
        log.info("invocationFrom: {}, contact: {}", invocationFrom, contact);
        contactRepository.save(contact);
        Response response = new Response();
        response.setStatusCode("201");
        response.setStatusMessage("聯絡訊息已儲存");
        return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
                .header("isSaved", "true").body(response);
    }

    /**
     * 刪除聯絡訊息：透過 RequestEntity<Contact> 拿到 header + body，記錄後刪除
     */
    @DeleteMapping("/deleteContactMessage")
    public ResponseEntity<Response> deleteContactMessage(RequestEntity<Contact> requestEntity) {
        // 1) 印出所有 request header
        HttpHeaders httpHeaders = requestEntity.getHeaders(); // HttpHeaders 實作 Map<String, List<String>>
        httpHeaders.forEach((key, value) -> {
            log.info("key={} : value={}", key, value);
        });

        // 2) 從 RequestEntity 取出 body 內的 Contact（Spring 用 Jackson 反序列化）
        Contact contact = requestEntity.getBody();

        // 3) 刪除聯絡訊息（等同 DELETE FROM contact WHERE contact_id = ?；找不到則靜默略過）
        contactRepository.delete(contact);
        // contactRepository.deleteById(contact.getContactId()) 也可以，且較常見

        // 4) 回傳刪除成功的 ResponseEntity
        Response response = new Response();
        response.setStatusCode("200");
        response.setStatusMessage("聯絡訊息已刪除");
        return ResponseEntity.status(HttpStatus.OK) // 200 OK
                .header("isDeleted", "true").body(response);
    }

    /**
     * 依 contactId 把狀態改為 CLOSED
     * 找到 → 更新並回 200；找不到 → 回 400
     */
    @PatchMapping("/updateContactMessageStatus")
    public ResponseEntity<Response> updateContactMessageStatus(@RequestBody Contact contact) {
        Response response = new Response();
        // 1) 依 contactId 取 Contact
        Optional<Contact> contactOptional = contactRepository.findById(contact.getContactId());
        // 2) 找到就改狀態並存回；找不到回 400
        if (contactOptional.isPresent()) {
            contactOptional.get().setStatus(ProjectConstant.STATUS_CLOSED);
            contactRepository.save(contactOptional.get());
        } else {
            response.setStatusCode("400");
            response.setStatusMessage("找不到對應的 contactId");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 400 Bad Request（無效輸入 / 欄位驗證失敗）
                    .header("isUpdated", "false").body(response);
        }
        // 3) 回傳成功
        response.setStatusCode("200");
        response.setStatusMessage("訊息狀態已更新為 CLOSED");
        return ResponseEntity.status(HttpStatus.OK).header("isUpdated", "true").body(response);
    }

    /**
     * Spring Data REST 自動端點（因 ContactRepository 是 JpaRepository）
     *   http://localhost:8081/spring-data-api/profile
     *   http://localhost:8081/spring-data-api/      ← HAL Explorer
     */

}
