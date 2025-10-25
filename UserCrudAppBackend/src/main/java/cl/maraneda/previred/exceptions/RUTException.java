package cl.maraneda.previred.exceptions;

/**
 * @author Tomas Barros <a href="mailto:bbarros@nic.cl">bbarros@nic.cl</a>
 * @version 1.0
 *
 */
public class RUTException extends java.lang.Exception {

    /**
     * Creates new <code>RUTException</code> without detail message.
     */
    public RUTException() {
        super("Hay problemas con el RUT");
    }


    /**
     * Constructs an <code>RUTException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public RUTException(String msg) {
        super(msg);
    }
}
