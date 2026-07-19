package com.company.myweb.controller;

import com.company.myweb.model.Contact;
import com.company.myweb.service.ContactService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static com.company.myweb.constant.ProjectConstant.ANSI_GREEN;
import static com.company.myweb.constant.ProjectConstant.ANSI_RESET;

@Slf4j
@Controller
public class ContactController {

    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {

        // 1) 送一個空 Contact 給前端表單綁定
        model.addAttribute("contact", new Contact());

        // 從 POST /saveMessage redirect 過來時，接收 flash attribute（成功訊息）
        if (model.containsAttribute("successfullySavedMessage")) {
            String successfullySavedMessage = (String) model.getAttribute("successfullySavedMessage");
            log.info(ANSI_GREEN + "從 redirect 收到 flash attribute 'successfullySavedMessage'：{}", successfullySavedMessage + ANSI_RESET);
        }
        return "nav/contact";
    }

    @PostMapping("/saveMessage")
    public String saveMessage(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, RedirectAttributes redirectAttributes) {

        // 1) 驗證失敗回原表單（BindingResult 帶錯誤訊息渲染）
        if (result.hasErrors()) return "nav/contact";

        // 2) 執行 save
        Contact savedContact = contactService.saveContact(contact);
        // Flash attribute：Spring 會在 redirect 後把它搬到新 request 的 model，只存活一次
        if (savedContact != null) redirectAttributes.addFlashAttribute("successfullySavedMessage", "訊息已成功送出");
        return "redirect:/contact";
//        return "nav/contact";
//        return "redirect:/contact?success=true";
    }
}
