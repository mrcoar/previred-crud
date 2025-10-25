package cl.maraneda.previred.exceptions;

public class RutRuntimeException extends RuntimeException{
    public RutRuntimeException(String message, RUTException cause){
        super(message, cause);
    }

    public RutRuntimeException(String message){
        this(message, new RUTException(message));
    }
}
