package rosa.isabelle.inventorycontrol.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorMessage {
    INVALID_ENTRY("Invalid input", HttpStatus.BAD_REQUEST),
    DUPLICATED_DATA("Duplicated result", HttpStatus.CONFLICT),
    NO_DATA_FOUND("No data found", HttpStatus.NOT_FOUND),
    DEFAULT_ERROR("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

    private HttpStatus statusCode;
    private String message;

    ErrorMessage(String message, HttpStatus statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }
}
