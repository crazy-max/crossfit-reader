package com.github.crazymax.crossfitreader.exception;

/**
 * Exception for booking
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class BookingException
        extends Exception {

    private static final long serialVersionUID = 4795476845779724713L;

    public BookingException() {
        super();
    }

    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public BookingException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * @param message
     * @param cause
     */
    public BookingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public BookingException(final String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public BookingException(final Throwable cause) {
        super(cause);
    }

}
