package rosa.isabelle.inventorycontrol.exception;

import lombok.Getter;

public class CustomException extends RuntimeException{
    @Getter
    private int statusCode;

    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
