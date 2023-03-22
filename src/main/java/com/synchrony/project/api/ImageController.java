package com.synchrony.project.api;

import com.synchrony.project.entity.Image;
import com.synchrony.project.entity.User;
import com.synchrony.project.entity.UserImage;
import com.synchrony.project.service.ImageService;
import com.synchrony.project.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.security.Principal;

@RestController
public class ImageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @PostMapping("/image")
    @ResponseBody
    public Image uploadImage(@RequestPart @NonNull MultipartFile file, Principal principal) {
        LOGGER.info("Received upload image request for image name: {}", file.getName());
        return imageService.upload(file.getResource(), principal.getName());
    }

    @GetMapping("/image/{imageHash}")
    @ResponseBody
    public ResponseEntity<Void> viewImage(@PathVariable String imageHash, Principal principal) {
        imageHash = HtmlUtils.htmlEscape(imageHash);
        LOGGER.info("Received view image request for image hash: {} and user: {}", imageHash, principal.getName());
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(imageService.viewImage(imageHash, principal.getName())))
                .build();
    }

    @DeleteMapping("/image/{imageDeleteHash}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable String imageDeleteHash, Principal principal) {
        imageDeleteHash = HtmlUtils.htmlEscape(imageDeleteHash);

        LOGGER.info("Received delete image request for delete hash: {}", imageDeleteHash);
        imageService.delete(imageDeleteHash, principal.getName());
    }

    @GetMapping("/myImages")
    @ResponseBody
    public UserImage getUserImages(Principal principal) {

        LOGGER.info("Received view associated images request for user {}", principal.getName());
        User user = userService.fetchUser(principal.getName());

        Image[] images = imageService.getImagesForUser(principal.getName());
        if (images.length == 0) {
            LOGGER.warn("No images associated with user");
        }

        UserImage userImage = new UserImage();
        userImage.setEmail(user.getEmail());
        userImage.setName(user.getName());
        userImage.setImages(images);

        return userImage;
    }
}
