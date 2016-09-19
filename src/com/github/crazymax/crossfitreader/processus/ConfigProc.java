package com.github.crazymax.crossfitreader.processus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.github.crazymax.crossfitreader.Main;
import com.github.crazymax.crossfitreader.model.Configuration;
import com.github.crazymax.crossfitreader.util.Util;

/**
 * Config proc
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class ConfigProc {
    
    private static final Logger LOGGER = Logger.getLogger(ConfigProc.class);
    
    private static final String PROP_BOOKING_BASE_URL = "bookingBaseUrl";
    private static final String PROP_BOOKING_API_KEY = "bookingApiKey";
    private static final String PROP_BOOKING_USER_PROFILE_PATH = "bookingUserProfilePath";
    private static final String PROP_BOOKING_USER_LIST_PATH = "bookingUserListPath";
    private static final String PROP_BOOKING_SCAN_CARD_PATH = "bookingScanCardPath";
    private static final String PROP_BOOKING_ASSOCIATE_CARD_PATH = "bookingAssociateCardPath";
    private static final String PROP_BOOKING_REMOVE_CARD_PATH = "bookingRemoveCardPath";
    
    private Path configPath;
    private Configuration config = new Configuration();
    
    private static class ConfigProcHandler {
        private final static ConfigProc instance = new ConfigProc();
    }
    
    public static ConfigProc getInstance() {
        return ConfigProcHandler.instance;
    }
    
    private ConfigProc() {
        super();
    }
    
    private void loadFile() {
        if (configPath == null) {
            configPath = Main.appPath.resolve(Main.appId + ".conf");
        }
    }
    
    public synchronized void loadConfig() {
        loadFile();
        
        Configuration result = new Configuration();
        result.setBookingBaseUrl("http://localhost/crossfit-reader");
        result.setBookingApiKey("rLhsoB0AwtUVFJ0dE7Z06R5CmgXYt8ZL");
        result.setBookingUserProfilePath("/?userprofile&id=%s");
        result.setBookingUserListPath("/?userlist");
        result.setBookingScanCardPath("/?scancard");
        result.setBookingAssociateCardPath("/?associatecard");
        result.setBookingRemoveCardPath("/?removecard");
        
        Properties propConfig = new Properties();
        InputStream inputConfigProperties = null;
        OutputStream outputConfigProperties = null;
        if (Files.exists(configPath)) {
            LOGGER.info("Loading configuration file: " + configPath);
            try {
                inputConfigProperties = new FileInputStream(configPath.toFile());
                propConfig.load(inputConfigProperties);
                
                result.setBookingBaseUrl(propConfig.getProperty(PROP_BOOKING_BASE_URL).trim());
                result.setBookingApiKey(propConfig.getProperty(PROP_BOOKING_API_KEY).trim());
                result.setBookingUserProfilePath(propConfig.getProperty(PROP_BOOKING_USER_PROFILE_PATH).trim());
                result.setBookingUserListPath(propConfig.getProperty(PROP_BOOKING_USER_LIST_PATH).trim());
                result.setBookingScanCardPath(propConfig.getProperty(PROP_BOOKING_SCAN_CARD_PATH).trim());
                result.setBookingAssociateCardPath(propConfig.getProperty(PROP_BOOKING_ASSOCIATE_CARD_PATH).trim());
                result.setBookingRemoveCardPath(propConfig.getProperty(PROP_BOOKING_REMOVE_CARD_PATH).trim());
            } catch (IOException e) {
                Util.logErrorExit(Util.i18n("processus.error.loading.config.properties"), e);
            } finally {
                if (inputConfigProperties != null) {
                    try {
                        inputConfigProperties.close();
                    } catch (IOException e) {
                        Util.logErrorExit(Util.i18n("processus.error.close.config.properties"));
                    }
                }
            }
        } else {
            LOGGER.info("Init configuration file: " + configPath);
            try {
                outputConfigProperties = new FileOutputStream(configPath.toFile());
                propConfig.setProperty(PROP_BOOKING_BASE_URL, result.getBookingBaseUrl());
                propConfig.setProperty(PROP_BOOKING_API_KEY, result.getBookingApiKey());
                propConfig.setProperty(PROP_BOOKING_USER_PROFILE_PATH, result.getBookingUserProfilePath());
                propConfig.setProperty(PROP_BOOKING_USER_LIST_PATH, result.getBookingUserListPath());
                propConfig.setProperty(PROP_BOOKING_SCAN_CARD_PATH, result.getBookingScanCardPath());
                propConfig.setProperty(PROP_BOOKING_ASSOCIATE_CARD_PATH, result.getBookingAssociateCardPath());
                propConfig.setProperty(PROP_BOOKING_REMOVE_CARD_PATH, result.getBookingRemoveCardPath());
                propConfig.store(outputConfigProperties, null);
            } catch (IOException e) {
                Util.logErrorExit(Util.i18n("common.error.loading.config.properties"), e);
            } finally {
                if (outputConfigProperties != null) {
                    try {
                        outputConfigProperties.close();
                    } catch (IOException e) {
                        Util.logErrorExit(Util.i18n("processus.error.close.config.properties"));
                    }
                }
            }
        }
        
        setConfig(result);
        LOGGER.info(config);
    }
    
    public Configuration getConfig() {
        synchronized (config) {
            return config;
        }
    }
    
    private void setConfig(final Configuration config) {
        synchronized (this.config) {
            this.config = config;
        }
    }
}
