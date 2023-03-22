package com.synchrony.project.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;

public class ImageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -3703555023272008812L;

    private Image data;

    private Integer status;

    private Boolean success;

    public Image getData() {
        return data;
    }

    public void setData(Image data) {
        this.data = data;
    }

    @JsonIgnore
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonIgnore
    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
