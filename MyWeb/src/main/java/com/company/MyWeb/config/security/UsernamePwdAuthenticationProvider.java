package com.company.MyWeb.config.security;

import com.company.MyWeb.model.Person;
import com.company.MyWeb.model.Roles;
import com.company.MyWeb.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.company.MyWeb.constant.ProjectConstant.ANSI_GREEN;

/**
 * After POST /login:
 * 1. Spring Security authenticates user using the AuthenticationProvider.
 * 2. It creates an Authentication object based on UsernamePasswordAuthenticationToken with username and roles (authorities).
 * 3. Stores Authentication object in the SecurityContext and session.
 * 4. Redirects to the success URL.
 * 5. Future requests use this context for authorization decisions.
 */

@Slf4j
@Component
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsernamePwdAuthenticationProvider(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Spring Security may have multiple AuthenticationProvider implementations registered. When a user submits a login form:
     * 1. The AuthenticationManager tries to authenticate the request.
     * 2. It iterates through each configured AuthenticationProvider.
     * 3. For each provider, it calls supports() with the authentication request’s class.
     * 4. If supports() returns true, it calls authenticate(authentication).
     * 5. The supports() method is used internally by Spring to choose the right provider when authenticating a user.
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    /**
     * This creates an instance of Authentication using UsernamePasswordAuthenticationToken
     * This token represents a successfully authenticated user. It is returned by your provider to tell Spring Security: "This user is authenticated."
     * In which the credential is no longer needed hence can be set null and for better security
     */
    @Override
    public Authentication authenticate(Authentication authentication) {
        //1) retrieve username and password from the login form when user tries to login
        String enteredEmail = authentication.getName();
        String enteredPassword = authentication.getCredentials().toString();
        //2) retrieve the person object based on the email entered and its role object since the fetch type is EAGER
        Person person = personRepository.readByEmail(enteredEmail);
        log.info(ANSI_GREEN + "Current method: " + Thread.currentThread().getStackTrace()[1].getMethodName() + " | Person: " + person);

        //3) Authentication logic starts here: if the person exists, and the enteredPassword (form input) matches the person's encodedPassword (database)
        if (person != null && passwordEncoder.matches(enteredPassword, person.getPassword()))
            //4) create the Authentication object for this user with principal and role after login
            return new UsernamePasswordAuthenticationToken(
                    enteredEmail,
                    null,
                    getGrantedAuthorities(person.getRoles()));

        else throw new BadCredentialsException("Invalid credentials");
        /*Spring Security’s AuthenticationFailureHandler catches BadCredentialsException, and redirects to /login?error=true (as configured in .failureUrl)*/
    }

    /**
     * This helper method converts a Roles object (representing a user’s role) into a list of Spring Security GrantedAuthority objects, which define the permissions for the user.
     * Spring Security uses GrantedAuthority to control access to resources.
     * Spring security supports multiple roles
     */
    private List<GrantedAuthority> getGrantedAuthorities(Roles roles) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        /*The .hasRole("ADMIN") method in security configuration checks if the List<GrantedAuthority> contains "ROLE_ADMIN".*/
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + roles.getRoleName()));
        return grantedAuthorities;
    }
}

