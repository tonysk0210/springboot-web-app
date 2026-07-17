package com.company.MyWeb.config.security;

import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

    /**
     * SecurityFilterChain: a core interface in Spring Security that represents the chain of filters applied to incoming HTTP requests.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http,
                                    AuthenticationProvider myCustomProvider) throws Exception {

        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console()) //disable csrf for h2 console
                .ignoringRequestMatchers("/api/**") //disable csrf for REST API
                .ignoringRequestMatchers("/spring-data-api/**") //disable csrf for Spring Data Rest
                .ignoringRequestMatchers("/myWeb/actuator/**"));

        http.authorizeHttpRequests(request -> request
                .requestMatchers("/dashboard").authenticated()
                .requestMatchers("/profilePage").authenticated()
                .requestMatchers("/updateProfile").authenticated()
                .requestMatchers("/student/**").hasRole("STUDENT")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").hasRole("ADMIN") //Rest API
                .requestMatchers("/spring-data-api/**").hasRole("ADMIN") //Spring Data Rest
                .requestMatchers("/myWeb/actuator/**").hasRole("ADMIN") //actuator
                .anyRequest().permitAll()); //all other requests are permitted, including h2 console

        http.formLogin(loginConfig -> loginConfig
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
                .failureUrl("/login?error=true"));

        /**
         * logout is instead handled by controller
         */
        /*http.logout(logoutConfig -> logoutConfig
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .permitAll());   //these wont get executed*/

        http.httpBasic(Customizer.withDefaults());

        http.authenticationProvider(myCustomProvider); //allow both providers to authenticate users

        //disable h2 console frame options
        http.headers(headersConfigurer -> headersConfigurer
                //Configures the X-Frame-Options HTTP header, which controls whether the page can be embedded in an <iframe>
                .frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));

        return http.build();
    }

    /**
     * establish BcryptHashing PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*@Bean
    public InMemoryUserDetailsManager userDetailsManager() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("123")
                .roles("ADMIN").build();
        UserDetails student = User.withDefaultPasswordEncoder()
                .username("student")
                .password("123")
                .roles("STUDENT").build();
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager(admin, student);
        return manager;
    }*/
}
