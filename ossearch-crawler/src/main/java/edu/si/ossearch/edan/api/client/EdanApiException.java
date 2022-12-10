package edu.si.ossearch.edan.api.client;

/**
 * @author jbirkhimer
 */
/**
 * Custom exception class for EDAN.  This class will be used to handle exception
 * that are more business logic related to EDAN.
 *
 * @author jbirkhimer
 */
public class EdanApiException extends Exception {

    /**
     * Same behavior is as the super class
     */
    public EdanApiException() {
        super();
    }

    /**
     * Same behavior is as the super class
     *
     * @param message the detailed message; same as the super class
     */
    public EdanApiException(String message) {
        super(message);
    }

    /**
     * Same behavior is as the super class
     *
     * @param cause the cause of exception; same as the super class
     */
    public EdanApiException(Throwable cause) {
        super(cause);
    }

    /**
     * Same behavior is as the super class
     *
     * @param message the detailed message; same as the super class
     * @param cause the cause of exception; same as the super class
     */
    public EdanApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
