package com.edu.tau.alo.tau_survey_system.controller;

import com.edu.tau.alo.tau_survey_system.dto.UserMeResponse;
import com.edu.tau.alo.tau_survey_system.model.User;
import com.edu.tau.alo.tau_survey_system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "https://apisurveys.vercel.app")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserMeResponse me(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();

        String microsoftOid = jwt.getClaimAsString("oid");

        System.out.println("MICROSOFT OID = " + microsoftOid);

        if (microsoftOid == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Microsoft OID not found in token"
            );
        }

        User user = userRepository.findByMicrosoftOid(microsoftOid)
                .orElseGet(() -> {

                    User newUser = new User();

                    String login = jwt.getClaimAsString("preferred_username");

                    if (login == null) {
                        login = "user_" + System.currentTimeMillis();
                    }

                    newUser.setLogin(login);
                    newUser.setPasswordHash("oauth2");
                    newUser.setMicrosoftOid(microsoftOid);

                    long adminCount = userRepository.countByRole(User.Role.ADMIN);


                    //!!!!!!!!!!!!!!!!!!!!!!!!!
                    //WAZNE
                    //TUTAJ CONFIGURUJEMY TWORZENIE ADMINOW TESTOWO
                    //POZNIEJ ZMIENIC
                    //!!!!!!!!!!!!!!!!!!!!!!!!
                    if (adminCount < 1) {
                        newUser.setRole(User.Role.ADMIN);
                    } else {
                        newUser.setRole(User.Role.STUDENT);
                    }

                    return userRepository.save(newUser);
                });

        return new UserMeResponse(
                user.getId(),
                user.getLogin(),
                user.getRole()
        );
    }
}
