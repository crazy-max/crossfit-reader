package com.github.crazymax.crossfitreader.exception;

/**
 * Exception for device searching
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class FindDeviceException
        extends Exception {
    
    private static final long serialVersionUID = 5315777055564060L;
    
    public FindDeviceException() {
        super();
    }
    
    /**
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public FindDeviceException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    /**
     * @param message
     * @param cause
     */
    public FindDeviceException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    /**
     * @param message
     */
    public FindDeviceException(final String message) {
        super(message);
    }
    
    /**
     * @param cause
     */
    public FindDeviceException(final Throwable cause) {
        super(cause);
    }
    
}
