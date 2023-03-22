package com.synchrony.project.service;

import com.synchrony.project.entity.User;
import com.synchrony.project.repo.UserRepo;
import com.synchrony.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = UserService.class)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void userSave() {

        User user = new User();

        user.setName("name");
        user.setEmail("email");
        user.setUsername("test");
        user.setPassword("test");

        User savedUser = new User();
        savedUser.setName("name");
        savedUser.setEmail("email");
        savedUser.setUsername("test");
        savedUser.setPassword("encrypted");
        savedUser.setId(1L);

        when(userRepo.save(user)).thenReturn(savedUser);

        userService.saveUser(user);

    }

}