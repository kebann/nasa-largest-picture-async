package com.example.exception;

import com.example.dto.NasaPictureRequest;

public class PictureNotFoundException extends RuntimeException {

    public PictureNotFoundException(String message) {
        super(message);
    }

    public PictureNotFoundException(NasaPictureRequest nasaPictureRequest) {
        super("No picture found for sol=%s".formatted(nasaPictureRequest.getSol()
                + nasaPictureRequest.getCamera().map(val -> "and camera=" + val).orElse("")));
    }
}