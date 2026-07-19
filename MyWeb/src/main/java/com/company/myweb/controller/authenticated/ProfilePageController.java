package com.company.myweb.controller.authenticated;

import com.company.myweb.model.Address;
import com.company.myweb.model.Person;
import com.company.myweb.model.Profile;
import com.company.myweb.repository.PersonRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import static com.company.myweb.constant.ProjectConstant.ANSI_GREEN;
import static com.company.myweb.constant.ProjectConstant.ANSI_RESET;

@Slf4j
@Controller
public class ProfilePageController {

    private final PersonRepository personRepository;

    @Autowired
    public ProfilePageController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * This method is to load the authenticated user detail on the profile page before update
     */
    @GetMapping("/profilePage")
    public String displayProfile(Model model, HttpSession session) {
        //1) Fetch the person object from session and copy its corresponding fields to the profile object.
        Person person = (Person) session.getAttribute("loggedInPerson");
        Profile profile = new Profile();
        profile.setName(person.getName());
        profile.setMobile(person.getMobile());
        profile.setEmail(person.getEmail());

        //2) Copy all address fields to the profile object if it exists. (Initially Address in Person is null when a new user is first created)
        if (person.getAddress() != null) {
            profile.setAddress1(person.getAddress().getAddress1());
            profile.setAddress2(person.getAddress().getAddress2());
            profile.setCity(person.getAddress().getCity());
            profile.setZipcode(person.getAddress().getZipCode());
        }
        //3 Send profile object to the front-end for display and to perform later update
        model.addAttribute("profile", profile);
        return "authenticated/profile";
    }

    /**
     * This method is to update the authenticated user detail by copying the data from Profile object to the Person object and save it.
     */
    @PostMapping("/updateProfile")
    public String updateProfile(@Valid @ModelAttribute("profile") Profile profile, BindingResult result, HttpSession session) {
        //1) Validate the input based on the rules defined in Profile class.
        if (result.hasErrors()) return "authenticated/profile";

        //2) Fetch the person object from session.
        Person person = (Person) session.getAttribute("loggedInPerson");

        //3) Check if the email entered by user from the profile object already exists in the database, if true display error message.
        if (personRepository.existsByEmailAndPersonIdNot(profile.getEmail(), person.getPersonId())) {
            result.rejectValue(
                    "email",
                    "profile.email.exists",
                    "An account with email " + profile.getEmail() + " already exists."
            );
            return "authenticated/profile";
        }

        //Store old email for comparison
        String oldEmail = person.getEmail();

        //4) Update the person object field
        person.setName(profile.getName());
        person.setMobile(profile.getMobile());
        person.setEmail(profile.getEmail());

        //5) If this is the user's first time updating the address detail, initialize an Address object
        if (person.getAddress() == null) person.setAddress(new Address());

        //6) Update the person's address object field
        person.getAddress().setAddress1(profile.getAddress1());
        person.getAddress().setAddress2(profile.getAddress2());
        person.getAddress().setCity(profile.getCity());
        person.getAddress().setZipCode(profile.getZipcode());

        //7) If email changed, update authentication BEFORE saving, so the AuditorAware reflects to the new email when handling updatedBy
        if (!oldEmail.equals(profile.getEmail())) {
            updateAuthentication(person);
        } else {
            // Force dirty mark so Hibernate performs an update if Person field is no modified (this is solved)
//            person.setName(person.getName());
        }

        //AuditorAware will handle updatedBy and UpdatedAt when an UPDATE is performed
        Person savedPerson = personRepository.save(person);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " savedPerson: " + savedPerson + ANSI_RESET);
        log.info(ANSI_GREEN + Thread.currentThread().getStackTrace()[1].getMethodName() + " savedPerson.getAddress(): " + savedPerson.getAddress() + ANSI_RESET);

        //8) Update the session with the user's up-to-date information for later use
        session.setAttribute("loggedInPerson", savedPerson);
        return "redirect:/profilePage?updated=true";
    }

    private void updateAuthentication(Person person) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();

        //1) Create new UserDetail with new email
        UserDetails newUserDetails = User.builder()
                .username(person.getEmail())  // Use new email as username
                .password(person.getPassword()) //?
                .authorities(currentAuth.getAuthorities())
                .build();

        //2) Create new Authentication with new UserDetail
        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                newUserDetails,
                currentAuth.getCredentials(),
                currentAuth.getAuthorities()
        );

        //3) Set new Authentication
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
