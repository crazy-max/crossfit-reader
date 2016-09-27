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

import com.github.crazymax.crossfitreader.Main;
import com.github.crazymax.crossfitreader.booking.User;

/**
 * Booking WS client proc
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class BookingProc {
    
    private static final Logger LOGGER = Logger.getLogger(BookingProc.class);
    
    private String baseUrl;
    private String apiKey;
    private String userProfilePath;
    private String userListPath;
    private String scanCardPath;
    private String associateCardPath;
    private String removeCardPath;
    
    private final Client client;
    
    private static class BookingProcessusHandler {
        private final static BookingProc instance = new BookingProc();
    }
    
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
            LOGGER.error("HTTP error code : " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
            return null;
        }
        
        if (Main.envDev) {
            LOGGER.debug(getBuilder(userListPath).get(String.class));
        }
        
        final List<User> userList = getBuilder(userListPath).get(new GenericType<List<User>>() {});
        LOGGER.info(userList.size() + " users found");
        LOGGER.debug(Arrays.toString(userList.toArray(new User[userList.size()])));
        
        return userList;
    }
    
    public User scanCard(final String cardUid) {
        final String adr = String.format(scanCardPath, cardUid);
        final Response response = getBuilder(adr).get();
        if (response.getStatus() != 200) {
            LOGGER.warn("scanCard error : HTTP " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
            return null;
        }
        
        if (Main.envDev) {
            LOGGER.debug(getBuilder(adr).get(String.class));
        }
        
        final User user = getBuilder(adr).get(new GenericType<User>() {});
        LOGGER.info(user.getFirstName() + " " + user.getLastName() + " scanned his card!");
        LOGGER.debug(user);
        
        return user;
    }
    
    public boolean associateCard(final String userId, final String cardUid) {
        final Response response = getBuilder(String.format(associateCardPath, userId))
                .put(Entity.text(cardUid), Response.class);
        
        if (response.getStatus() != 200) {
            LOGGER.error("HTTP error code : " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
            return false;
        }
        
        return true;
    }
    
    public boolean removeCard(final String userId) {
        final Response response = getBuilder(String.format(removeCardPath, userId))
                .put(Entity.text(""), Response.class);
        
        if (response.getStatus() != 200) {
            LOGGER.error("HTTP error code : " + response.getStatus() + " " + response.getStatusInfo().getReasonPhrase());
            return false;
        }
        
        return true;
    }
}
