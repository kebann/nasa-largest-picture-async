package com.example.controller;


import com.example.dto.NasaPictureRequest;
import com.example.exception.PictureNotFoundException;
import com.example.service.NasaPictureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/mars/pictures")
@RequiredArgsConstructor
public class NasaPicturesController {

    private final NasaPictureService nasaPictureService;

    @PostMapping("/largest")
    public ResponseEntity<?> postFindLargestPictureCommand(@RequestBody NasaPictureRequest nasaPictureRequest) {
        String commandId = nasaPictureService.postFindLargestPictureCommand(nasaPictureRequest);

        return ResponseEntity.accepted()
                .location(buildShortURI(commandId))
                .build();
    }

    private URI buildShortURI(String shortUrlKey) {
        return ServletUriComponentsBuilder.fromCurrentRequestUri().pathSegment(String.valueOf(shortUrlKey)).build()
                .toUri();
    }

    @GetMapping(value = "/largest/{commandId}", produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] getLargestPicture(@PathVariable String commandId) {
        return nasaPictureService.getContentByKey(commandId)
                .orElseThrow(() -> new PictureNotFoundException("No picture found for key: " + commandId));
    }
}
