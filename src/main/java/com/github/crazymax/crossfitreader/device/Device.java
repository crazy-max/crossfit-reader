package com.github.crazymax.crossfitreader.device;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.smartcardio.Card;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;

import org.apache.log4j.Logger;

import com.github.crazymax.crossfitreader.exception.FindDeviceException;
import com.github.crazymax.crossfitreader.exception.ScanCardException;
import com.github.crazymax.crossfitreader.util.Util;
import com.google.common.base.Strings;

/**
 * The ACR122 device
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class Device {

    private static final Logger LOGGER = Logger.getLogger(Device.class);

    private CardTerminal terminal;
    private volatile Card card;
    private volatile String cardUid;
    private CopyOnWriteArrayList<DeviceListener> listeners;
    private Thread listenerThread;

    public Device(final CardTerminal terminal) {
        this.terminal = terminal;
        listeners = new CopyOnWriteArrayList<DeviceListener>();

        // Start new thread
        listenerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    // infinite timeout needed for waitForCard*
                    int timeoutMs = 0;

                    // main thread loop
                    while (true) {
                        boolean statusChanged = true;
                        try {
                            // wait for a status change
                            if (card == null) {
                                LOGGER.debug("Card is null, wait for insertion");
                                terminal.waitForCardPresent(timeoutMs);
                            } else {
                                LOGGER.debug("Card not null, wait for removal");
                                terminal.waitForCardAbsent(timeoutMs);
                            }

                            // change the status
                            if (card == null && isCardPresent()) {
                                connect();
                            } else if (card != null && !isCardPresent()) {
                                disconnect();
                            } else {
                                // Increase the timeout
                                timeoutMs = 3000;
                                statusChanged = false;
                            }
                        } catch (CardException | ScanCardException e1) {
                            // force disconnect
                            card = null;
                            cardUid = null;

                            // try to reconnect if card is present and continue the loop
                            if (isCardPresent()) {
                                LOGGER.debug("Try reconnect");
                                // will step out on repeated exception
                                connect();
                            }
                        }

                        if (statusChanged) {
                            notifyListeners();
                        }
                    }
                } catch (CardException e) {
                    LOGGER.debug("Terminal unplugged, wait for device");
                    while (true) {
                        try {
                            resetTerminal();
                            LOGGER.debug("Terminal plugged in!");
                            break;
                        } catch (FindDeviceException e1) {
                            // Retry
                        }
                    }
                } catch (ScanCardException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }

            private void notifyListeners() {
                for (DeviceListener listener : listeners) {
                    notifyCardListener(listener, false);
                }
            }
        });

        listenerThread.start();
    }

    private void resetTerminal() throws FindDeviceException {
        this.terminal = Util.getTerminal();
    }

    /**
     * Add new card listener to be notified on card insertion/removal. Listeners
     * should assume that the card is removed in default state.
     * @param listener Card listener object to be added
     */
    public void addCardListener(DeviceListener listener) {
        LOGGER.debug("Add listener " + listener.getClass().getName());
        listeners.add(listener);
        notifyCardListener(listener, true);
    }

    /**
     * Remove card listener from the list of listeners. Does nothing if the
     * listener is not present in the list.
     * @param listener Previously added card listener object to be removed
     * @return true if the removal succeeded; false otherwise
     */
    public boolean removeCardListener(DeviceListener listener) {
        LOGGER.debug("Remove listener " + listener.getClass().getName());
        return listeners.remove(listener);
    }

    /**
     * Notify the listener for action
     * @param listener
     * @param insertedOnly
     */
    private void notifyCardListener(DeviceListener listener, boolean insertedOnly) {
        if (card != null && !Strings.isNullOrEmpty(cardUid)) {
            final String tmpCardUid = cardUid;
            cardUid = null;
            listener.cardInserted(card, tmpCardUid);
        } else if (!insertedOnly) {
            listener.cardRemoved();
        }
    }

    public void connect() throws CardException, ScanCardException {
        LOGGER.debug("Connect card");
        card = terminal.connect("*");
        cardUid = Util.getCardUid(card);
    }

    public void disconnect() throws CardException {
        LOGGER.debug("Disconnect card");
        card.disconnect(false);
        card = null;
        cardUid = null;
    }

    private boolean isCardPresent() throws CardException {
        return terminal.isCardPresent();
    }
}
