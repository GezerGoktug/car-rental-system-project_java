package exceptions;

public class RentalException extends Exception {
    private ErrorCode errorCode;

    public enum ErrorCode {
        CAR_NOT_AVAILABLE,
        INVALID_RENTAL_PERIOD
    }

    public RentalException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}