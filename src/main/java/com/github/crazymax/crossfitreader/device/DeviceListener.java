package com.github.crazymax.crossfitreader.device;

import javax.smartcardio.Card;

/**
 * The ACR122 device listener
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public interface DeviceListener {

    /**
     * Card is inserted into the reader terminal.
     * Use Card object to read data from the eID card.
     * @param card Card object
     * @param cardUid string
     */
    public void cardInserted(final Card card, final String cardUid);

    /** Card is removed from the reader terminal */
    public void cardRemoved();
}
