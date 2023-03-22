package com.synchrony.project.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synchrony.project.config.ImgurProps;
import com.synchrony.project.config.RestConfig;
import com.synchrony.project.entity.Image;
import com.synchrony.project.entity.ImageResponse;
import com.synchrony.project.repo.ImageRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest(classes = {ImageService.class, RestConfig.class})
class ImageServiceTest {

    private MockRestServiceServer mockRestServiceServer;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImgurProps imgurProps;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpHeaders headers;

    @MockBean
    private ImageRepo imageRepo;

    @BeforeEach
    public void setup() {
        mockRestServiceServer = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @AfterEach
    public void afterEach() {
        mockRestServiceServer.verify();
    }

    @Test
    void uploadReturnsImageMetadata() throws Exception {

        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setStatus(200);
        imageResponse.setSuccess(true);

        Image image = new Image();
        image.setImageHash("imageHash");
        image.setImageDeleteHash("imageDeleteHash");
        image.setLink("imageUrl");

        imageResponse.setData(image);

        MockMultipartFile file = new MockMultipartFile("file", "hello.jpeg", MediaType.IMAGE_JPEG_VALUE, "TestFile".getBytes());
        mockRestServiceServer.expect(requestTo(imgurProps.getUploadUrl()))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(imageResponse), MediaType.APPLICATION_JSON));

        Image actual = imageService.upload(file.getResource(), "");
        assertThat(actual.toString()).isEqualTo(image.toString());
    }

    @Test
    public void viewReturnsImageUrl() {
        Image image = new Image();
        image.setImageHash("viewHash");
        image.setImageDeleteHash("imageDeleteHash");
        image.setLink("imageUrl");

        when(imageRepo.findByImageHashAndUserName(any(), any())).thenReturn(image);
        String imageUrl = imageService.viewImage("viewHash", "test");

        assertThat(imageUrl).isEqualTo(image.getLink());

        imageService.viewImage("viewHash", "test");
        imageService.viewImage("viewHash", "test");

        verify(imageRepo, times(1)).findByImageHashAndUserName(any(), any());
    }

    @Test
    public void viewInvalidImageThrowsException() {

        when(imageRepo.findByImageHashAndUserName(any(), any())).thenReturn(null);
        assertThatThrownBy(() ->
                imageService.viewImage("invalid", "test")).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    public void deleteImageSuccess() {

        Image image = new Image();
        image.setImageHash("deleteImage");
        image.setImageDeleteHash("imageDeleteHash");
        image.setLink("imageUrl");

        when(imageRepo.findByImageDeleteHashAndUserName(any(), any())).thenReturn(image);

        mockRestServiceServer.expect(requestTo(imgurProps.getDeleteUrl("imageDeleteHash")))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("OK", MediaType.APPLICATION_JSON));

        imageService.delete("imageDeleteHash", "test");
    }

    @Test
    public void deleteImageThrowsException() {

        when(imageRepo.findByImageDeleteHashAndUserName(any(), any())).thenReturn(null);
        assertThatThrownBy(() ->
                imageService.delete("imageDeleteHash", "test")).isInstanceOf(NoSuchElementException.class);
    }

}