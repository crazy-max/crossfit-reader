package com.github.crazymax.crossfitreader.exception;

/**
 * Exception for scanning card
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class ScanCardException
        extends Exception {

    private static final long serialVersionUID = -6883959201674173008L;

    public ScanCardException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public ScanCardException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public ScanCardException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public ScanCardException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public ScanCardException(final Throwable cause) {
        super(cause);
    }

}
