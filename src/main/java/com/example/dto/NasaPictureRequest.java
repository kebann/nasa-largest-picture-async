package com.example.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NasaPictureRequest {
    private Integer sol;
    private String camera;

    @JsonProperty
    public Optional<String> getCamera() {
        return Optional.ofNullable(camera);
    }
}
