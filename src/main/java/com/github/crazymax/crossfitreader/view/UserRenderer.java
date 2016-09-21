package com.github.crazymax.crossfitreader.view;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.github.crazymax.crossfitreader.booking.User;
import com.github.crazymax.crossfitreader.util.Resources;
import com.google.common.base.Strings;

/**
 * User renderer for comobo box
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class UserRenderer extends BasicComboBoxRenderer {
    
    private static final long serialVersionUID = 1885739695085981197L;

    @SuppressWarnings("rawtypes")
    public Component getListCellRendererComponent(final JList list, final Object value, final int index,
            final boolean isSelected, final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);
        
        if (value instanceof User) {
            User user = (User) value;
            setText(user.getLastName() + " " + user.getFirstName());
            if (Strings.isNullOrEmpty(user.getCardUuid())) {
                setIcon(Resources.ICON_BULLET_GREEN);
            } else {
                setIcon(Resources.ICON_BULLET_RED);
            }
        } else {
            setIcon(null);
            setText("");
        }
        
        return this;
    }
}
