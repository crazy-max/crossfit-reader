package com.github.crazymax.crossfitreader.processus;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import com.github.crazymax.crossfitreader.booking.User;
import com.github.crazymax.crossfitreader.util.Util;

/**
 * Booking WS client proc
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class BookingProc {
    
    private static final Logger LOGGER = Logger.getLogger(BookingProc.class);
    
    private static class BookingProcessusHandler {
        private final static BookingProc instance = new BookingProc();
    }
    
    private String baseUrl;
    private String apiKey;
    private String userProfilePath;
    private String userListPath;
    private String scanCardPath;
    private String associateCardPath;
    private String removeCardPath;
    
    private final Client client;
    
    public static BookingProc getInstance() {
        return BookingProcessusHandler.instance;
    }
    
    private BookingProc() {
        baseUrl = ConfigProc.getInstance().getConfig().getBookingBaseUrl();
        apiKey = ConfigProc.getInstance().getConfig().getBookingApiKey();
        userProfilePath = ConfigProc.getInstance().getConfig().getBookingUserProfilePath();
        userListPath = ConfigProc.getInstance().getConfig().getBookingUserListPath();
        scanCardPath = ConfigProc.getInstance().getConfig().getBookingScanCardPath();
        associateCardPath = ConfigProc.getInstance().getConfig().getBookingAssociateCardPath();
        removeCardPath = ConfigProc.getInstance().getConfig().getBookingRemoveCardPath();
        
        ClientConfig configuration = new ClientConfig();
        configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        configuration = configuration.property(ClientProperties.READ_TIMEOUT, 1000);
        client = ClientBuilder.newClient(configuration);
    }
    
    private Builder getBuilder(final String adresseRelative) {
        return client.target(String.format(baseUrl) + adresseRelative)
                .request()
                .header("X-Access-Token", apiKey);
    }
    
    public String getUserProfileUrl(final String id) {
        return baseUrl + String.format(userProfilePath, id);
    }
    
    public List<User> getUserList() {
        final Response response = getBuilder(userListPath).get();
        if (response.getStatus() != 200) {
            Util.logError("HTTP error code : " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
            return null;
        }
        
        LOGGER.debug(getBuilder(userListPath).get(String.class));
        
        final List<User> userList = getBuilder(userListPath).get(new GenericType<List<User>>() {});
        LOGGER.info(userList.size() + " users found");
        LOGGER.debug(Arrays.toString(userList.toArray(new User[userList.size()])));
        
        return userList;
    }
    
    public boolean associateCard(final String userId, final String cardUid) {
        final Response response = getBuilder(String.format(associateCardPath, userId))
                .put(Entity.text(cardUid), Response.class);
        
        return response.getStatus() == 200;
    }
    
    public boolean removeCard(final String userId, final String cardUid) {
        final Response response = getBuilder(String.format(removeCardPath, userId))
                .put(Entity.text(cardUid), Response.class);
        
        return response.getStatus() == 200;
    }
    
    public boolean scanCard(final String userId, final String cardUid) {
        final Response response = getBuilder(String.format(scanCardPath))
                .put(Entity.text(cardUid), Response.class);
        
        return response.getStatus() == 200;
    }
}
