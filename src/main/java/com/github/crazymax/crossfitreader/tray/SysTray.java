package com.github.crazymax.crossfitreader.tray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.smartcardio.Card;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;

import com.github.crazymax.crossfitreader.Main;
import com.github.crazymax.crossfitreader.booking.User;
import com.github.crazymax.crossfitreader.device.Device;
import com.github.crazymax.crossfitreader.device.DeviceListener;
import com.github.crazymax.crossfitreader.exception.FindDeviceException;
import com.github.crazymax.crossfitreader.processus.BookingProc;
import com.github.crazymax.crossfitreader.tray.menu.TrayMenuCardManager;
import com.github.crazymax.crossfitreader.tray.menu.TrayMenuCardUid;
import com.github.crazymax.crossfitreader.tray.menu.TrayMenuExit;
import com.github.crazymax.crossfitreader.util.Resources;
import com.github.crazymax.crossfitreader.util.Util;
import com.google.common.base.Strings;

/**
 * System Tray icon notification
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class SysTray implements DeviceListener {
    
    private static final Logger LOGGER = Logger.getLogger(SysTray.class);
    
    private static SysTray instance = null;
    private Device device = null;
    
    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private JPopupMenu popupMenu;
    private JDialog hiddenDialog;
    
    private SysTray() {
        super();
    }
    
    public static SysTray getInstance() {
        if (instance == null) {
            instance = new SysTray();
        }
        return instance;
    }
    
    public void init() {
        systemTray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(Resources.ICON_BLUE_32.getImage(), Main.appName, null);
        trayIcon.setImageAutoSize(true);
        
        final TrayMenuExit trayMenuExit = new TrayMenuExit(instance);
        final TrayMenuCardManager trayMenuCardManager = new TrayMenuCardManager(instance);
        final TrayMenuCardUid trayMenuCardUid = new TrayMenuCardUid(instance);
        
        popupMenu = new JPopupMenu();
        popupMenu.add(trayMenuCardManager);
        popupMenu.add(trayMenuCardUid);
        popupMenu.addSeparator();
        popupMenu.add(trayMenuExit);
        
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popupMenu.setLocation(e.getX(), e.getY());
                    hiddenDialog.setLocation(e.getX(), e.getY());
                    popupMenu.setInvoker(popupMenu);
                    hiddenDialog.setVisible(true);
                    popupMenu.setVisible(true);
                }
            }
        });
        
        try {
            systemTray.add(trayIcon);
            Util.createPidFile();
            
            // Init ACR122
            try {
                LOGGER.info("Init ACR122");
                device = new Device(Main.noReader ? null : Util.getTerminal());
                addCardListener();
                showInfoTooltip(Util.i18n("systray.device.found"));
                Util.playSound(Resources.SOUND_SUCCESS);
            } catch (FindDeviceException e1) {
                showErrorTooltip(Util.i18n("systray.device.error"));
                Util.playSound(Resources.SOUND_KO);
                LOGGER.error(e1.getMessage(), e1);
                removeTray();
                System.exit(0);
            }
            
            // Check device connected
            final ScheduledExecutorService scheduledExecutorDevice = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorDevice.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (Util.isTerminalPlugged()) {
                        setImage(Resources.ICON_BLUE_32.getImage());
                    } else {
                        setImage(Resources.ICON_RED_32.getImage());
                    }
                }
            }, 0, 500, TimeUnit.MILLISECONDS);
            
            // Check app exited outside
            final ScheduledExecutorService scheduledExecutorExit = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorExit.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    if (!Files.exists(Util.getPidFile().toPath())) {
                        removeTray();
                        System.exit(0);
                    }
                }
            }, 0, 100, TimeUnit.MILLISECONDS);
            
        } catch (AWTException e) {
            Util.logErrorExit(Util.i18n("systray.error.load"), e);
        }
        
        hiddenDialog = new JDialog();
        hiddenDialog.setSize(10, 10);
        hiddenDialog.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowLostFocus(final WindowEvent we) {
                hiddenDialog.setVisible(false);
                popupMenu.setVisible(false);
                popupMenu.setInvoker(null);
            }
            
            @Override
            public void windowGainedFocus(final WindowEvent we) {
                
            }
        });
    }
    
    public Device getDevice() {
        return device;
    }
    
    @Override
    public void cardInserted(final Card card, final String cardUid) {
        String errorMsg = null;
        final User userScan = BookingProc.getInstance().scanCard(cardUid);
        if (userScan == null) {
            errorMsg = String.format(Util.i18n("systray.scan.unknowncard"), cardUid);
            LOGGER.info(String.format("The card %s is assigned to any member", cardUid));
        } else if (userScan.getBookings() == null || userScan.getBookings().size() <= 0) {
            errorMsg = String.format(Util.i18n("systray.scan.noresa"), userScan.getFirstName(), userScan.getLastName());
            LOGGER.info(String.format("%s %s has not made any reservations", userScan.getFirstName(), userScan.getLastName()));
        }
        
        if (!Strings.isNullOrEmpty(errorMsg)) {
            showErrorTooltip(errorMsg);
            Util.playSound(Resources.SOUND_MIRROR_SHATTERING);
            return;
        }
        
        showInfoTooltip(String.format(Util.i18n("systray.scan.welcome"), userScan.getFirstName(), userScan.getLastName()));
        LOGGER.info(String.format("Good CrossFit workout %s %s !", userScan.getFirstName(), userScan.getLastName()));
        Util.playSound(Resources.SOUND_CASH_REGISTER);
    }
    
    @Override
    public void cardRemoved() {
        // N/A
    }
    
    public void addCardListener() {
        device.addCardListener(this);
    }
    
    public void removeCardListener() {
        device.removeCardListener(this);
    }

    public void removeTray() {
        systemTray.remove(trayIcon);
        trayIcon = null;
    }
    
    public void setImage(final Image image) {
        trayIcon.setImage(image);
    }
    
    private void showTooltip(final String message, final MessageType type) {
        trayIcon.displayMessage(Main.appName, message, type);
    }
    
    public void showInfoTooltip(final String message) {
        showTooltip(message, MessageType.INFO);
    }
    
    public void showWarningTooltip(final String message) {
        showTooltip(message, MessageType.WARNING);
    }
    
    public void showErrorTooltip(final String message) {
        showTooltip(message, MessageType.ERROR);
    }
}
