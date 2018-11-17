package io.gameapis.gameapis.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String error;

    public static ErrorResponse error(String message) {
        return new ErrorResponse(message);
    }
}
