package com.github.crazymax.crossfitreader.tray.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import com.github.crazymax.crossfitreader.tray.SysTray;
import com.github.crazymax.crossfitreader.util.Resources;
import com.github.crazymax.crossfitreader.util.Util;
import com.github.crazymax.crossfitreader.view.CardUidDialog;

/**
 * View card UID
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class TrayMenuCardUid
        extends JMenuItem {
    
    private static final long serialVersionUID = 3844467863834228080L;
    
    public TrayMenuCardUid(final SysTray sysTray) {
        super();
        
        setText(Util.i18n("traymenu.carduid.text"));
        setIcon(Resources.ICON_CARDUID);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final CardUidDialog dlg = new CardUidDialog(sysTray, getText());
                dlg.setVisible(true);
            }
        });
    }
}
