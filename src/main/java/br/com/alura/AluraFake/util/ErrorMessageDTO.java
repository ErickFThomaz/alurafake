package br.com.alura.AluraFake.util;

import org.springframework.util.Assert;

public class ErrorMessageDTO {

    private final String message;

    public ErrorMessageDTO(String message) {
        Assert.notNull(message, "message description must not be null");
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
