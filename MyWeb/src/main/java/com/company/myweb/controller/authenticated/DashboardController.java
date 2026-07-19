package com.company.myweb.controller.authenticated;

import com.company.myweb.model.Person;
import com.company.myweb.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class DashboardController {

    private final PersonRepository personRepository;

    @Autowired
    public DashboardController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication, HttpSession session) {
        //1) retrieve logged-in person via the Authentication object.
        Person person = personRepository.readByEmail(authentication.getName()); //email

        //2) display the logged-in person's name, role, major on the page
        model.addAttribute("username", person.getName());
        model.addAttribute("role", authentication.getAuthorities().toString());
        if (person.getPlan() != null) model.addAttribute("plan", person.getPlan().getName());
        //be careful with transactional context is closed when plan field of Person set LAZY resulting in LAZY exception

        //3) stores the login user detail in session with the person object for later use: Profile Controller
        session.setAttribute("loggedInPerson", person);

        return "authenticated/dashboard";
    }
}
