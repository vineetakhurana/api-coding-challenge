package com.synchrony.project.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

@ConfigurationProperties(prefix = "imgur")
public record ImgurProps(String uploadUrl, String deleteUrl, String bearer) {

    public String getUploadUrl() {
        return UriComponentsBuilder.fromHttpUrl(uploadUrl)
                .encode()
                .toUriString();
    }

    public String getDeleteUrl(String imageDeleteHash) {

        return UriComponentsBuilder.fromHttpUrl(deleteUrl)
                .buildAndExpand(imageDeleteHash)
                .encode()
                .toUriString();
    }

    public String getBearer() {
        return bearer;
    }
}
