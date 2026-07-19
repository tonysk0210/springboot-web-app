package com.company.myweb.controller;

import com.company.myweb.exception.EmailAlreadyExistsException;
import com.company.myweb.model.Person;
import com.company.myweb.service.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/public")
public class PublicController {

    private final PersonService personService;

    @Autowired
    public PublicController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("person", new Person());
        return "register";
    }

    @PostMapping("/createUser")
    public String createUser(@Valid @ModelAttribute("person") Person person, BindingResult result) {
        if (result.hasErrors()) return "register"; //1) validation check via MVC.

        try {
            personService.savePerson(person); // 2) Attempt to save; PersonService throws EmailAlreadyExistsException if found duplicate.
        } catch (EmailAlreadyExistsException ex) {
            // 3) Bind a field‐error on "email" with the exception message.
            result.rejectValue(
                    "email",            // the field in the Person object
                    null,                    // a message code (It can be defined in messages.properties if desired)
                    ex.getMessage()          // default message ("An account with email " + person.getEmail() + " already exists.")
            );
            // 4) Return to the registration page. The 'person' instance and BindingResult are still in the model.
            return "register";
        }

        return "redirect:/login?register=true";
    }
}
