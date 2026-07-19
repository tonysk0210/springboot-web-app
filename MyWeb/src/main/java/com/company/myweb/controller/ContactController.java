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

        //1) send contact object to front-end binding with the form
        model.addAttribute("contact", new Contact());

        //testing: model received from redirect
        if (model.containsAttribute("successfullySavedMessage")) {
            String successfullySavedMessage = (String) model.getAttribute("successfullySavedMessage");
            log.info(ANSI_GREEN + "model('successfullySavedMessage') received from redirect, message: {}", successfullySavedMessage + ANSI_RESET);
        }
        return "nav/contact";
    }

    @PostMapping("/saveMessage")
    public String saveMessage(@Valid @ModelAttribute("contact") Contact contact, BindingResult result, RedirectAttributes redirectAttributes) {

        //1) if validation fails, show error msg
        if (result.hasErrors()) return "nav/contact";

        //2) perform save contact
        Contact savedContact = contactService.saveContact(contact);
        //add "successfullySavedMessage" to redirectAttribute which Spring copies it into the model after the new request
        if (savedContact != null) redirectAttributes.addFlashAttribute("successfullySavedMessage", "Message successfully submitted");
        return "redirect:/contact";
//        return "nav/contact";
//        return "redirect:/contact?success=true";
    }
}
