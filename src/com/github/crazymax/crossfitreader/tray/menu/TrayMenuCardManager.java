package com.github.crazymax.crossfitreader.tray.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.github.crazymax.crossfitreader.Main;
import com.github.crazymax.crossfitreader.tray.SysTray;
import com.github.crazymax.crossfitreader.util.Util;
import com.github.crazymax.crossfitreader.view.CardManagerDialog;

/**
 * Associate menu on system tray
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class TrayMenuCardManager
        extends JMenuItem {
    
    private static final long serialVersionUID = 3844467863834228080L;
    
    public static final ImageIcon ICON_ASSOCIATE = new ImageIcon(
            Main.class.getResource("rss/img/menu_cardmanager.png"));
    
    public TrayMenuCardManager(final SysTray sysTray) {
        super();
        
        setText(Util.i18n("traymenu.cardmanager.text"));
        setIcon(ICON_ASSOCIATE);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final CardManagerDialog dlg = new CardManagerDialog(sysTray, getText());
                dlg.setVisible(true);
            }
        });
    }
}
