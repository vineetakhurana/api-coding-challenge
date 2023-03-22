package com.synchrony.project.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.synchrony.project.config.ImgurProps;
import com.synchrony.project.entity.Image;
import com.synchrony.project.entity.ImageResponse;
import com.synchrony.project.repo.ImageRepo;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@EnableConfigurationProperties(ImgurProps.class)
public class ImageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageService.class);

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    HttpHeaders headers;

    @Autowired
    private ImgurProps imgurProps;

    @Autowired
    private ImageRepo imageRepo;

    private final LoadingCache<ImmutablePair<String, String>, Image> imageHashCache =
            CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.MINUTES).build(new CacheLoader<>() {
                @Override
                public Image load(final ImmutablePair<String, String> imageHashUserPair) throws Exception {
                    LOGGER.debug("Fetching image url from store for hash {}", imageHashUserPair.getKey());
                    return imageRepo.findByImageHashAndUserName(imageHashUserPair.getKey(), imageHashUserPair.getValue());
                }
            });

    /**
     * Uploads image and links its metadata to user
     *
     * @param image    file to upload
     * @param userName user uploading the file
     * @return image metadata
     */
    public Image upload(@NonNull Resource image, String userName) {

        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("image", image);
        map.add("name", image.getFilename());
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

        LOGGER.debug("Uploading image {} to {}", image.getFilename(), imgurProps.getUploadUrl());
        ResponseEntity<ImageResponse> responseEntity = restTemplate.exchange(imgurProps.getUploadUrl(), HttpMethod.POST, requestEntity, ImageResponse.class);

        if (responseEntity.getBody() == null || responseEntity.getBody().getData() == null) {
            LOGGER.error("No metadata of image received from Imgur API");
            throw new NoSuchElementException("Invalid response from Imgur API");
        }
        LOGGER.info("Uploaded image {} successfully", image.getFilename());

        responseEntity.getBody().getData().setUserName(userName);

        LOGGER.debug("Saving image {} metadata to store", image.getFilename());
        imageRepo.save(responseEntity.getBody().getData());

        return responseEntity.getBody().getData();
    }

    /**
     * Delete an image
     *
     * @param imageDeleteHash unique delete identifier
     * @param userName        user who has access to image
     */
    public void delete(String imageDeleteHash, String userName) {

        LOGGER.debug("Fetching image from store and deleting if present for hash {}", imageDeleteHash);
        Image image = imageRepo.findByImageDeleteHashAndUserName(imageDeleteHash, userName);

        if (image == null) {
            throw new NoSuchElementException(String.format("No image found with delete hash: %s for user: %s ", imageDeleteHash, userName));
        }
        imageHashCache.invalidate(new ImmutablePair<>(image.getImageHash(), image.getUserName()));
        imageRepo.delete(image);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        LOGGER.debug("Sending delete request to {}", imgurProps.getDeleteUrl(imageDeleteHash));
        restTemplate.exchange(imgurProps.getDeleteUrl(imageDeleteHash), HttpMethod.DELETE, requestEntity, String.class).getBody();

        LOGGER.debug("Successfully deleted image {} ", imageDeleteHash);
    }

    /**
     * Fetches image URL from cache or metadata
     *
     * @param imageHash unique image hash
     * @param userName  user who has access to image
     * @return URL to image
     */
    public String viewImage(String imageHash, String userName) {
        try {
            Image image = imageHashCache.get(new ImmutablePair<>(imageHash, userName));
            LOGGER.debug("Retrieved image successfully for image hash: {}", imageHash);
            return image.getLink();
        } catch (Exception exception) {
            LOGGER.warn("Error fetching image with hash {}", imageHash, exception);
            throw new NoSuchElementException(String.format("No image found with image hash: %s for user: %s ", imageHash, userName));
        }
    }

    /**
     * Retrieves all image metadata for the given user from cache or data store
     *
     * @param userName username associated with user
     * @return list of images associated
     */
    public Image[] getImagesForUser(String userName) {
        LOGGER.debug("Fetching images for user : {}", userName);
        return imageRepo.findAllByUserName(userName);
    }

}
