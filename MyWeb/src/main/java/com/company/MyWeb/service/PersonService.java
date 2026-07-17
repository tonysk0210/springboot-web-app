package com.company.MyWeb.service;

import com.company.MyWeb.constant.ProjectConstant;
import com.company.MyWeb.exception.EmailAlreadyExistsException;
import com.company.MyWeb.model.Person;
import com.company.MyWeb.model.Roles;
import com.company.MyWeb.repository.PersonRepository;
import com.company.MyWeb.repository.RolesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.company.MyWeb.constant.ProjectConstant.ANSI_GREEN;
import static com.company.MyWeb.constant.ProjectConstant.ANSI_RESET;

@Slf4j
@Service
public class PersonService {
    private PersonRepository personRepository;
    private RolesRepository rolesRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public PersonService(PersonRepository personRepository, RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Person savePerson(Person person) {

        //1) check if entered email already exists, and throw EmailAlreadyExistsException if it does.
        if (personRepository.existsByEmail(person.getEmail())) {
            throw new EmailAlreadyExistsException("⚠\uFE0F An account with email " + person.getEmail() + " already exists.");
        }

        /* At this point, name, mobile, email, password are already set by user via the registration form*/
        Roles roles = rolesRepository.getByRoleName(ProjectConstant.STUDENT_ROLE);
        person.setRoles(roles); //2) assign role.
        person.setPassword(passwordEncoder.encode(person.getPassword())); //3) set encoded password.

        /*At this point, new user has no authenticationToken established, and we do not want AuditorAware to intercept and assign anonymousUser to createdBy field.
         Instead, we intercept AuditorAware by hiding createdBy field in BaseEntity and manually set createdBy with user email
        */
        person.setCreatedBy(person.getEmail()); //4) set createdBy manually with user email

        log.info(ANSI_GREEN + "Current method: " + Thread.currentThread().getStackTrace()[1].getMethodName() + "| Person before saved: " + person + ANSI_RESET);
        //Person(super=BaseEntity(createdAt=null, createdBy=a@a, updatedAt=null, updatedBy=null), personId=0, name=aaa, mobile=1234567890, email=a@a, confirmEmail=a@a, password=$2a$10$rSQ9YKSi0986SWHh7Ok9MOZv9rK2KJoKBgqCDlYW6nGfzQ2u1BWnC, confirmPassword=1234567890, createdBy=a@a, roles=Roles(roleId=2, roleName=STUDENT), address=null, plan=null, courses=[])
        Person savedPerson = personRepository.save(person); //5) AuditorAware handles createdAt, person_id when saved
        log.info(ANSI_GREEN + "Current method: " + Thread.currentThread().getStackTrace()[1].getMethodName() + "| Person after saved: " + person + ANSI_RESET);
        //Person(super=BaseEntity(createdAt=2025-06-13T01:18:52.026037300, createdBy=a@a, updatedAt=2025-06-13T01:18:52.026037300, updatedBy=anonymousUser), personId=4, name=aaa, mobile=1234567890, email=a@a, confirmEmail=a@a, password=$2a$10$rSQ9YKSi0986SWHh7Ok9MOZv9rK2KJoKBgqCDlYW6nGfzQ2u1BWnC, confirmPassword=1234567890, createdBy=a@a, roles=Roles(roleId=2, roleName=STUDENT), address=null, plan=null, courses=[])

        return savedPerson;
    }
}
