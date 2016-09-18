package com.github.crazymax.crossfitreader.view;

import java.awt.Image;
import java.util.List;

import javax.swing.JFrame;

/**
 * Dummy frame for dialog
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public class DummyFrame extends JFrame {

    private static final long serialVersionUID = 6260525660242578861L;

    public DummyFrame(String title, List<? extends Image> iconImages) {
        super(title);
        setUndecorated(true);
        setVisible(true);
        setIconImages(iconImages);
        pack();
        setLocationRelativeTo(null);
    }
    
}
