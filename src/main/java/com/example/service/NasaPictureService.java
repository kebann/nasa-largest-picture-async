package com.example.service;

import com.example.dto.NasaPictureCommandDetails;
import com.example.dto.NasaPictureRequest;
import com.example.dto.NasaResponse;
import com.example.dto.Photo;
import com.example.exception.NasaServiceException;
import com.example.storage.ContentStorage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.config.RabbitMQConfig.PICTURES_EXCHANGE_NAME;
import static com.example.config.RabbitMQConfig.PICTURES_QUEUE_NAME;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;

@Service
@RequiredArgsConstructor
@Slf4j
public class NasaPictureService {

    private final RestTemplate restTemplate;
    private final ContentStorage contentStorage;
    private final RabbitTemplate rabbitTemplate;

    @Value("${nasa.api.url}")
    private String nasaBaseUrl;
    @Value("${nasa.api.key}")
    private String nasaApiKey;

    @Cacheable("largestPicture")
    public Optional<byte[]> findLargestPicture(Integer sol, Optional<String> camera) {
        var roverPhotos = getCuriosityRoverPhotos(sol, camera);
        return roverPhotos
                .parallelStream()
                .map(this::enrichPhotoWithSize)
                .max(comparing(Photo::getSize))
                .map(photo -> getPictureContent(photo.getPhotoUrl()));
    }

    private byte[] getPictureContent(String pictureUri) {
        try {
            return restTemplate.getForObject(pictureUri, byte[].class);
        } catch (RestClientException e) {
            throw new NasaServiceException("Failed to download picture content " + e.getMessage());
        }
    }

    private Photo enrichPhotoWithSize(Photo photo) {
        var headers = restTemplate.headForHeaders(photo.getPhotoUrl());
        return photo.setSize(headers.getContentLength());
    }

    private List<Photo> getCuriosityRoverPhotos(Integer sol, Optional<String> camera) {
        try {
            String nasaUrl = buildNasaUrl(sol, camera);
            var response = restTemplate.getForObject(nasaUrl, NasaResponse.class);

            return Optional.ofNullable(response)
                    .map(NasaResponse::photos)
                    .orElseGet(ArrayList::new);
        } catch (RestClientException e) {
            throw new NasaServiceException("Call to NASA API failed with " + e.getMessage());
        }
    }

    private String buildNasaUrl(Integer sol, Optional<String> camera) {
        return UriComponentsBuilder.fromHttpUrl(nasaBaseUrl)
                .path("mars-photos/api/v1/rovers/curiosity/photos")
                .queryParam("api_key", nasaApiKey)
                .queryParam("sol", sol)
                .queryParamIfPresent("camera", camera)
                .toUriString();
    }

    public Optional<byte[]> getContentByKey(@NonNull String commandId) {
        return contentStorage.getByKey(commandId);
    }

    public String postFindLargestPictureCommand(NasaPictureRequest nasaPictureRequest) {
        String commandId = randomAlphanumeric(5);
        log.info("Generated id for the command: {} ", commandId);

        var commandDetails = new NasaPictureCommandDetails(commandId, nasaPictureRequest);
        log.info("Received find largest NASA picture command: {}", commandDetails);

        rabbitTemplate.convertAndSend(PICTURES_EXCHANGE_NAME, "", commandDetails);
        log.info("Pushed command details to the `{}` queue", PICTURES_QUEUE_NAME);

        return commandId;
    }
}
