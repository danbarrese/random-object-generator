package net.pladform.random;

/**
 * @author Dan Barrese
 */
public class FailedRandomObjectGenerationException extends RuntimeException {

    public FailedRandomObjectGenerationException(Exception cause) {
        super(cause);
    }

    public FailedRandomObjectGenerationException(String msg, Exception cause) {
        super(msg, cause);
    }

}
