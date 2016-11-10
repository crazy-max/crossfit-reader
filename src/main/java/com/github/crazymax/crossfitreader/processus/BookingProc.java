package com.github.crazymax.crossfitreader.processus;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.ProcessingException;
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
import com.github.crazymax.crossfitreader.exception.BookingException;
import com.github.crazymax.crossfitreader.util.Util;

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
    private int timeout;
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
        timeout = ConfigProc.getInstance().getConfig().getBookingTimeout();
        userProfilePath = ConfigProc.getInstance().getConfig().getBookingUserProfilePath();
        userListPath = ConfigProc.getInstance().getConfig().getBookingUserListPath();
        scanCardPath = ConfigProc.getInstance().getConfig().getBookingScanCardPath();
        associateCardPath = ConfigProc.getInstance().getConfig().getBookingAssociateCardPath();
        removeCardPath = ConfigProc.getInstance().getConfig().getBookingRemoveCardPath();
        
        ClientConfig configuration = new ClientConfig();
        configuration = configuration.property(ClientProperties.CONNECT_TIMEOUT, timeout == 0 ? 2000 : timeout);
        configuration = configuration.property(ClientProperties.READ_TIMEOUT, timeout == 0 ? 2000 : timeout);
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
    
    public List<User> getUserList() throws BookingException {
        final Response response = getBuilder(userListPath).get();
        if (Util.startsWith(response.getStatus(), 5)) {
            throw new BookingException(Util.i18n("booking.error.offline"));
        }
        if (response.getStatus() != 200) {
            throw new BookingException(String.format(Util.i18n("booking.error.http"),
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase()));
        }
        final List<User> userList = response.readEntity(new GenericType<List<User>>() {});
        LOGGER.info(userList.size() + " users found");
        LOGGER.debug(Arrays.toString(userList.toArray(new User[userList.size()])));
        return userList;
    }
    
    public User scanCard(final String cardUid) throws BookingException, ProcessingException {
        final Response response = getBuilder(String.format(scanCardPath, cardUid)).get();
        if (Util.startsWith(response.getStatus(), 5)) {
            throw new BookingException(Util.i18n("booking.error.offline"));
        }
        if (response.getStatus() == 404) {
            return null;
        }
        if (response.getStatus() != 200) {
            throw new BookingException(String.format(Util.i18n("booking.error.http"),
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase()));
        }
        final User user = response.readEntity(new GenericType<User>() {});
        LOGGER.info(user.getFirstName() + " " + user.getLastName() + " scanned his card!");
        LOGGER.debug(user);
        return user;
    }
    
    public boolean associateCard(final String userId, final String cardUid) throws BookingException {
        final Response response = getBuilder(String.format(associateCardPath, userId))
                .put(Entity.text(cardUid), Response.class);
        if (Util.startsWith(response.getStatus(), 5)) {
            throw new BookingException(Util.i18n("booking.error.offline"));
        }
        if (response.getStatus() != 200) {
            throw new BookingException(String.format(Util.i18n("booking.error.http"),
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase()));
        }
        return true;
    }
    
    public boolean removeCard(final String userId) throws BookingException {
        final Response response = getBuilder(String.format(removeCardPath, userId))
                .put(Entity.text(""), Response.class);
        if (Util.startsWith(response.getStatus(), 5)) {
            throw new BookingException(Util.i18n("booking.error.offline"));
        }
        if (response.getStatus() != 200) {
            throw new BookingException(String.format(Util.i18n("booking.error.http"),
                    response.getStatus(),
                    response.getStatusInfo().getReasonPhrase()));
        }
        return true;
    }
}
