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
import com.github.crazymax.crossfitreader.device.Device;
import com.github.crazymax.crossfitreader.device.DeviceListener;
import com.github.crazymax.crossfitreader.exception.FindDeviceException;
import com.github.crazymax.crossfitreader.tray.menu.TrayMenuCardManager;
import com.github.crazymax.crossfitreader.tray.menu.TrayMenuCardUid;
import com.github.crazymax.crossfitreader.tray.menu.TrayMenuExit;
import com.github.crazymax.crossfitreader.util.Util;

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
    
    private TrayMenuExit trayMenuExit;
    private TrayMenuCardManager trayMenuAssociate;
    private TrayMenuCardUid trayMenuCardUid;
    
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
        trayIcon = new TrayIcon(Util.ICON_BLUE_32.getImage(), Main.appName, null);
        trayIcon.setImageAutoSize(true);
        
        trayMenuExit = new TrayMenuExit(instance);
        trayMenuAssociate = new TrayMenuCardManager(instance);
        trayMenuCardUid = new TrayMenuCardUid(instance);
        
        popupMenu = new JPopupMenu();
        popupMenu.add(trayMenuAssociate);
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
                device = new Device(Util.getTerminal());
                addCardListener();
                showInfoTooltip(Util.i18n("systray.device.found"));
                Util.playSound(Util.getRssFile("sounds/success.wav"));
            } catch (FindDeviceException e1) {
                showErrorTooltip(Util.i18n("systray.device.error"));
                Util.playSound(Util.getRssFile("sounds/ko.wav"));
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
                        setImage(Util.ICON_BLUE_32.getImage());
                    } else {
                        setImage(Util.ICON_RED_32.getImage());
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
        Util.playSound(Util.getRssFile("sounds/cash-register.wav"));
    }
    
    @Override
    public void cardRemoved() {
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
