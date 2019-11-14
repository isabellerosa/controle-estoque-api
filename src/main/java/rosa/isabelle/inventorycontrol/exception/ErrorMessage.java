package rosa.isabelle.inventorycontrol.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    INVALID_ENTRY("Invalid input", HttpStatus.BAD_REQUEST),
    DUPLICATED_DATA("Duplication error", HttpStatus.CONFLICT),
    NO_DATA_FOUND("There is no such register", HttpStatus.NOT_FOUND),
    DEFAULT_MESSAGE("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private HttpStatus statusCode;
    private String message;

    ErrorMessage(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
