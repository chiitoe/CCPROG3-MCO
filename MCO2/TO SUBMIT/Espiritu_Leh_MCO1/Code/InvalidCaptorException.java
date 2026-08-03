/** InvalidCaptorException
 * Purpose: User-defined CHECKED exception (extends Exception).
 * Thrown when a capture is attempted by an invalid captor, e.g. a Pirate.
 */
public class InvalidCaptorException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Purpose: Builds the exception with a descriptive message.
     * @param message passed up to the parent Exception class
     */
    public InvalidCaptorException(String message){
        super(message);
    }
}
 