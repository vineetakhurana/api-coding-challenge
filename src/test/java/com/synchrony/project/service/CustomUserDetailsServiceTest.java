package com.synchrony.project.service;

import com.synchrony.project.entity.User;
import com.synchrony.project.service.CustomUserDetailsService;
import com.synchrony.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CustomUserDetailsService.class)
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private UserService userService;

    @Test
    void loadUserByUsername() {

        User user = new User();

        user.setName("name");
        user.setEmail("email");
        user.setUsername("test");
        user.setPassword("test");

        when(userService.fetchUser("test")).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test");

        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails.isEnabled()).isTrue();
    }

    @Test
    void loadUserByUsernameNotFound() {

        when(userService.fetchUser("username")).thenThrow(new UsernameNotFoundException("Message"));
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("username")).isInstanceOf(UsernameNotFoundException.class);
    }
}