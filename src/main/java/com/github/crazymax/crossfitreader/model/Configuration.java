package com.github.crazymax.crossfitreader.model;

/**
 * Configuration model
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class Configuration {
    
    private String bookingBaseUrl;
    private String bookingApiKey;
    private int bookingTimeout;
    private String bookingUserProfilePath;
    private String bookingUserListPath;
    private String bookingScanCardPath;
    private String bookingAssociateCardPath;
    private String bookingRemoveCardPath;

    public String getBookingBaseUrl() {
        return bookingBaseUrl;
    }

    public void setBookingBaseUrl(String bookingBaseUrl) {
        this.bookingBaseUrl = bookingBaseUrl;
    }

    public String getBookingApiKey() {
        return bookingApiKey;
    }

    public void setBookingApiKey(String bookingApiKey) {
        this.bookingApiKey = bookingApiKey;
    }
    
    public int getBookingTimeout() {
        return bookingTimeout;
    }

    public void setBookingTimeout(int bookingTimeout) {
        this.bookingTimeout = bookingTimeout;
    }

    public String getBookingUserProfilePath() {
        return bookingUserProfilePath;
    }

    public void setBookingUserProfilePath(String bookingUserProfilePath) {
        this.bookingUserProfilePath = bookingUserProfilePath;
    }

    public String getBookingUserListPath() {
        return bookingUserListPath;
    }

    public void setBookingUserListPath(String bookingUserListPath) {
        this.bookingUserListPath = bookingUserListPath;
    }

    public String getBookingScanCardPath() {
        return bookingScanCardPath;
    }

    public void setBookingScanCardPath(String bookingScanCardPath) {
        this.bookingScanCardPath = bookingScanCardPath;
    }

    public String getBookingAssociateCardPath() {
        return bookingAssociateCardPath;
    }

    public void setBookingAssociateCardPath(String bookingAssociateCardPath) {
        this.bookingAssociateCardPath = bookingAssociateCardPath;
    }

    public String getBookingRemoveCardPath() {
        return bookingRemoveCardPath;
    }

    public void setBookingRemoveCardPath(String bookingRemoveCardPath) {
        this.bookingRemoveCardPath = bookingRemoveCardPath;
    }
    
    @Override
    public String toString() {
        return "Configuration:\nbookingBaseUrl=" + bookingBaseUrl + "\nbookingApiKey=" + bookingApiKey
                + "\nbookingTimeout=" + bookingTimeout
                + "\nbookingUserProfilePath=" + bookingUserProfilePath + "\nbookingUserListPath=" + bookingUserListPath
                + "\nbookingScanCardPath=" + bookingScanCardPath + "\nbookingAssociateCardPath="
                + bookingAssociateCardPath + "\nbookingRemoveCardPath=" + bookingRemoveCardPath;
    }
}
