package com.synchrony.project.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "IMAGE")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Image implements Serializable {

    @Serial
    private static final long serialVersionUID = 4139547463525719937L;

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonAlias("id")
    @Column(name = "imageHash", nullable = false)
    private String imageHash;

    @JsonAlias("deletehash")
    @Column(name = "image_delete_hash", nullable = false)
    private String imageDeleteHash;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "user_name")
    @JsonIgnore
    private String userName;

    public String getImageDeleteHash() {
        return imageDeleteHash;
    }

    public void setImageDeleteHash(String imageDeleteHash) {
        this.imageDeleteHash = imageDeleteHash;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageHash() {
        return imageHash;
    }

    public void setImageHash(String imageHash) {
        this.imageHash = imageHash;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "Image{" +
                "imageDeleteHash='" + imageDeleteHash + '\'' +
                ", link='" + link + '\'' +
                ", imageHash='" + imageHash + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
