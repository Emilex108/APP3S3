package exceptions;

public class TransmissionErrorException extends Exception{
    public TransmissionErrorException(String errorMessage) {
        super(errorMessage);
    }
}
