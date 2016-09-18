package com.github.crazymax.crossfitreader.tray.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import com.github.crazymax.crossfitreader.Main;
import com.github.crazymax.crossfitreader.tray.SysTray;
import com.github.crazymax.crossfitreader.util.Util;

/**
 * Exit menu on system tray
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class TrayMenuExit
        extends JMenuItem {
    
    private static final long serialVersionUID = 3844467863834228080L;
    
    public static final ImageIcon ICON_EXIT = new ImageIcon(
            Main.class.getResource("rss/img/menu_exit.png"));
    
    public TrayMenuExit(final SysTray sysTray) {
        super();
        
        setText(Util.i18n("traymenu.exit.text"));
        setIcon(ICON_EXIT);
        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                sysTray.removeTray();
                System.exit(0);
            }
        });
    }
}
