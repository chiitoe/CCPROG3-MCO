/** DataPersistenceException
 * Purpose: User-defined CHECKED exception (extends Exception).
 * Wraps low-level IOExceptions so callers deal with one domain-level failure type
 * instead of raw java.io plumbing.
 */
public class DataIOException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Purpose: Builds the exception with a message and the underlying cause.
     * @param message human-readable description of what failed
     * @param cause the original exception (usually an IOException)
     */
    public DataIOException(String message, Throwable cause){
        super(message, cause);
    }
}