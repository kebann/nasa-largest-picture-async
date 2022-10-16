package com.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record NasaPictureCommandDetails(String commandId, NasaPictureRequest commandPayload) {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public NasaPictureCommandDetails(String commandId, @JsonUnwrapped NasaPictureRequest commandPayload) {
        this.commandId = commandId;
        this.commandPayload = commandPayload;
    }
}
