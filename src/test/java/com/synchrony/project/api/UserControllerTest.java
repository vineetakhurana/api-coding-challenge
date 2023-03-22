package com.synchrony.project.api;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.project.config.RestConfig;
import com.synchrony.project.config.SecurityTestConfig;
import com.synchrony.project.entity.User;
import com.synchrony.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityTestConfig.class, RestConfig.class})
class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_ENDPOINT = "/user";

    @Test
    @WithUserDetails("test")
    public void createUserReturns201() throws Exception {

        User user = new User();

        user.setName("name");
        user.setEmail("email");
        user.setUsername("test");
        user.setPassword("test");

        this.mockMvc.perform(post(USER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithUserDetails("test")
    public void createUserReturns400() throws Exception {
        User user = new User();

        user.setName("name");
        user.setEmail("email");
        user.setUsername("another");

        doThrow(new DataIntegrityViolationException("something")).when(userService).saveUser(any());

        this.mockMvc.perform(post(USER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails("test")
    public void otherUserEndpointAccessForbidden() throws Exception {

        this.mockMvc.perform(put(USER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}