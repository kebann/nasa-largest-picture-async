package com.example.listener;

import com.example.dto.NasaPictureCommandDetails;
import com.example.dto.NasaPictureRequest;
import com.example.exception.NasaServiceException;
import com.example.exception.PictureNotFoundException;
import com.example.service.NasaPictureService;
import com.example.storage.ContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import static com.example.config.RabbitMQConfig.PICTURES_QUEUE_NAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class NasaPictureListener {

    private final NasaPictureService nasaPictureService;
    private final ContentStorage contentStorage;

    @RabbitListener(queues = PICTURES_QUEUE_NAME)
    public void processPictureRequest(NasaPictureCommandDetails commandDetails) {
        try {
            log.info("Processing command: {}", commandDetails);

            NasaPictureRequest nasaPictureRequest = commandDetails.commandPayload();
            byte[] imageContent = nasaPictureService.findLargestPicture(
                            nasaPictureRequest.getSol(),
                            nasaPictureRequest.getCamera()
                    )
                    .orElseThrow(() -> new PictureNotFoundException(nasaPictureRequest));

            contentStorage.put(commandDetails.commandId(), imageContent);
            log.info("Successfully processed command: {}", commandDetails);
        } catch (PictureNotFoundException e) {
//          exception type isn't retryable and indicates that there are no picture found for given params
            log.error("Failed to process command", e);
        } catch (NasaServiceException e) {
            log.error("Failed to process command", e);
//            trigger retries
            throw new RuntimeException(e);
        }
    }
}
