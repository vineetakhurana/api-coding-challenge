package com.synchrony.project.api;

import com.synchrony.project.entity.User;
import com.synchrony.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    public void userAdminSave(@RequestBody @NonNull User user) {
        user.setRole("ADMIN");
        userSave(user);
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    public void userSave(@RequestBody @NonNull User user) {
        LOGGER.info("Received user save request for username: {}", user.getUsername());
        userService.saveUser(user);
    }
}
