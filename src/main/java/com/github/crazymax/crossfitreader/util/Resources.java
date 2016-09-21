package com.github.crazymax.crossfitreader.util;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import com.github.crazymax.crossfitreader.Main;

public class Resources {
    
    public static final ResourceBundle BUNDLE = ResourceBundle.getBundle(Main.class.getPackage().getName() + ".bundles.crossfitreader");
    
    public static final ImageIcon ICON_BLUE_16 = getImageIcon("icon_blue_16.png");
    public static final ImageIcon ICON_BLUE_32 = getImageIcon("icon_blue_32.png");
    public static final ImageIcon ICON_BLUE_48 = getImageIcon("icon_blue_48.png");
    public static final ImageIcon ICON_BLUE_128 = getImageIcon("icon_blue_128.png");
    public static final ImageIcon ICON_RED_16 = getImageIcon("icon_red_16.png");
    public static final ImageIcon ICON_RED_32 = getImageIcon("icon_red_32.png");
    public static final ImageIcon ICON_RED_48 = getImageIcon("icon_red_48.png");
    public static final ImageIcon ICON_RED_128 = getImageIcon("icon_red_128.png");
    
    public static final ImageIcon ICON_LOADER = getImageIcon("loader.gif");
    
    public static final ImageIcon ICON_BULLET_GREEN = getImageIcon("bullet_green.png");
    public static final ImageIcon ICON_BULLET_RED = getImageIcon("bullet_red.png");
    
    public static final ImageIcon ICON_ADD_24 = getImageIcon("add_24.png");
    public static final ImageIcon ICON_REMOVE_24 = getImageIcon("remove_24.png");
    
    public static final ImageIcon ICON_ASSOCIATE = getImageIcon("menu_cardmanager.png");
    public static final ImageIcon ICON_CARDUID = getImageIcon("menu_carduid.png");
    public static final ImageIcon ICON_EXIT = getImageIcon("menu_exit.png");
    
    public static final File SOUND_SUCCESS = getSound("success.wav");
    public static final File SOUND_KO = getSound("ko.wav");
    public static final File SOUND_MIRROR_SHATTERING = getSound("mirror-shattering.wav");
    public static final File SOUND_CASH_REGISTER = getSound("cash-register.wav");
    
    private Resources() {
        super();
    }
    
    public static URL getResource(final String resource) {
        return Main.class.getResource("ext/" + resource);
    }
    
    public static ImageIcon getImageIcon(final String img) {
        return new ImageIcon(getResource("img/" + img));
    }
    
    public static File getFile(final String filename) {
        return new File(Main.appRssPath.toFile(), filename);
    }
    
    public static File getSound(final String sound) {
        return new File(Main.appRssPath.resolve("sounds").toFile(), sound);
    }
}
