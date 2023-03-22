package com.synchrony.project.api;

import com.synchrony.project.config.SecurityTestConfig;
import com.synchrony.project.entity.Image;
import com.synchrony.project.entity.User;
import com.synchrony.project.service.ImageService;
import com.synchrony.project.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
@Import(SecurityTestConfig.class)
class ImageControllerTest {

    private static final String IMAGE_ENDPOINT = "/image";
    MockMultipartFile file = new MockMultipartFile("file", "hello.jpeg", MediaType.IMAGE_JPEG_VALUE, "TestFile".getBytes());
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageService imageService;
    @MockBean
    private UserService userService;

    @Test
    @WithUserDetails(value = "test")
    void viewImageRedirectsToImageUrl() throws Exception {

        Image image = new Image();
        image.setImageHash("imageHash");
        image.setImageDeleteHash("imageDeleteHash");
        image.setLink("imageUrl");

        when(imageService.viewImage(any(), any())).thenReturn(image.getLink());

        this.mockMvc.perform(get(IMAGE_ENDPOINT + "/imageHash")).andExpect(status().isFound()).andExpect(header().string("location", image.getLink())).andExpect(redirectedUrl(image.getLink()));
    }

    @Test
    @WithUserDetails(value = "test")
    void viewImageNotFound() throws Exception {

        when(imageService.viewImage(any(), any())).thenThrow(new NoSuchElementException("No image found"));
        this.mockMvc.perform(get(IMAGE_ENDPOINT + "/imageHash")).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "test")
    void uploadImageReturnsImageMetadata() throws Exception {

        Image image = new Image();
        image.setImageHash("imageHash");
        image.setImageDeleteHash("imageDeleteHash");
        image.setLink("imageUrl");

        when(imageService.upload(any(), any())).thenReturn(image);

        mockMvc.perform(multipart(IMAGE_ENDPOINT).file(file)).andExpect(status().isOk()).andExpect(jsonPath("$.imageHash").value(image.getImageHash())).andExpect(jsonPath("$.imageDeleteHash").value(image.getImageDeleteHash())).andExpect(jsonPath("$.link").value(image.getLink()));
    }

    @Test
    @WithUserDetails(value = "test")
    void uploadImageNotFound() throws Exception {

        when(imageService.upload(any(), any())).thenThrow(new NoSuchElementException("Invalid response from API"));

        mockMvc.perform(multipart(IMAGE_ENDPOINT).file(file)).andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "test")
    void deleteImageReturnsReturnsNoContent() throws Exception {

        mockMvc.perform(delete(IMAGE_ENDPOINT + "/imageDeleteHash")).andExpect(status().isNoContent());
    }

    @Test
    @WithUserDetails(value = "test")
    void deleteImageReturnsNotFound() throws Exception {

        doThrow(NoSuchElementException.class).when(imageService).delete(any(), any());
        mockMvc.perform(delete(IMAGE_ENDPOINT + "/imageDeleteHash")).andExpect(status().isNotFound());
    }

    @Test
    void accessRestrictedForUnauthorizedUser() throws Exception {

        mockMvc.perform(delete(IMAGE_ENDPOINT + "/imageDeleteHash")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(value = "test")
    void otherImageEndpointAccessForbidden() throws Exception {

        mockMvc.perform(post(IMAGE_ENDPOINT + "/random")).andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "test")
    void viewImagesForUser() throws Exception {

        Image image = new Image();
        image.setImageHash("imageHash");
        image.setImageDeleteHash("imageDeleteHash");
        image.setLink("imageUrl");
        image.setUserName("test");

        User user = new User();
        user.setName("test");
        user.setEmail("test");

        when(userService.fetchUser("test")).thenReturn(user);

        when(imageService.getImagesForUser(any())).thenReturn(new Image[]{image});

        this.mockMvc.perform(get("/myImages")).andExpect(status().isOk()).andExpect(jsonPath("$.name").value(user.getName())).andExpect(jsonPath("$.email").value(user.getEmail())).andExpect(jsonPath("$.images.*imageHash").value(image.getImageHash())).andExpect(jsonPath("$.images.*imageDeleteHash").value(image.getImageDeleteHash())).andExpect(jsonPath("$.images.*link").value(image.getLink()));
    }

}