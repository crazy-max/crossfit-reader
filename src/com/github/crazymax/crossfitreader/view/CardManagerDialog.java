package com.github.crazymax.crossfitreader.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.smartcardio.Card;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.github.crazymax.crossfitreader.booking.User;
import com.github.crazymax.crossfitreader.booking.UserComparator;
import com.github.crazymax.crossfitreader.device.Device;
import com.github.crazymax.crossfitreader.device.DeviceListener;
import com.github.crazymax.crossfitreader.enums.CardManagerLayoutEnum;
import com.github.crazymax.crossfitreader.enums.CardScanTypeEnum;
import com.github.crazymax.crossfitreader.processus.BookingProc;
import com.github.crazymax.crossfitreader.tray.SysTray;
import com.github.crazymax.crossfitreader.util.Util;
import com.google.common.base.Strings;

/**
 * Card manager dialog
 * @author crazy-max
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public final class CardManagerDialog
        extends JDialog
        implements ActionListener, DeviceListener {
    
    private static final long serialVersionUID = -4658875207238213753L;
    
    private static final Logger LOGGER = Logger.getLogger(CardManagerDialog.class);
    
    private static final List<Image> ICONS = Arrays.asList(
            Util.ICON_BLUE_16.getImage(),
            Util.ICON_BLUE_32.getImage(),
            Util.ICON_BLUE_48.getImage()
    );
    
    final static int CONTENT_WIDTH = 500;
    final static int CONTENT_HEIGHT = 210;
    
    private SysTray systray;
    private Device device;
    private BookingProc bookingProc;
    
    private final JPanel cards;
    private DefaultComboBoxModel<User> userModel;
    
    private CardManagerLayoutEnum currentLayout = CardManagerLayoutEnum.INTRO;
    private CardScanTypeEnum currentScanType;
    private User selectedUser;
    private String currentUid;
    
    public CardManagerDialog(final SysTray systray, final String title) {
        super(new DummyFrame(title, ICONS));
        
        this.systray = systray;
        this.systray.removeCardListener();
        this.device = systray.getDevice();
        this.device.addCardListener(this);
        this.bookingProc = BookingProc.getInstance();
        
        cards = new JPanel(new CardLayout());
        cards.add(getPanelIntro(), CardManagerLayoutEnum.INTRO.getName());
        cards.add(getPanelSearchUsers(), CardManagerLayoutEnum.SEARCH_USERS.getName());
        cards.add(getPanelSelectUser(), CardManagerLayoutEnum.SELECT_USER.getName());
        cards.add(getPanelScanCard(), CardManagerLayoutEnum.SCAN_CARD.getName());
        
        JPanel panneauPrincipal = new JPanel(new BorderLayout());
        panneauPrincipal.setOpaque(true);
        panneauPrincipal.add(cards, BorderLayout.CENTER);
        
        ((java.awt.Frame)getOwner()).setIconImage(Util.ICON_BLUE_32.getImage());
        setTitle(title);
        setContentPane(panneauPrincipal);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                close();
            }
        });
        pack();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        close();
    }
    
    private void close() {
        device.removeCardListener(this);
        systray.addCardListener();
        ((DummyFrame)getParent()).dispose();
        dispose();
    }
    
    private void switchLayout(CardManagerLayoutEnum layout) {
        currentLayout = layout;
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, layout.getName());
    }
    
    private JPanel getPanelIntro() {
        // Text
        JLabel labelText = new JLabel(Util.i18n("cardmanager.intro"));
        labelText.setHorizontalAlignment(JLabel.CENTER);
        
        // Buttons
        JButton btnContinue = new JButton(Util.i18n("common.next"));
        btnContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                switchLayout(CardManagerLayoutEnum.SEARCH_USERS);
            }
        });
        
        JButton btnCancel = new JButton(Util.i18n("common.cancel"));
        btnCancel.addActionListener(this);
        
        JPanel panelBtn = new JPanel();
        panelBtn.add(btnContinue);
        panelBtn.add(btnCancel);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        panel.add(labelText, BorderLayout.NORTH);
        panel.add(panelBtn, BorderLayout.SOUTH);
        return panel;
    }
    
    private JPanel getPanelSearchUsers() {
        // Texte
        JLabel labelTexte = new JLabel(Util.i18n("cardmanager.search.users"));
        labelTexte.setHorizontalAlignment(JLabel.CENTER);
        
        // Icone chargement
        JLabel loader = new JLabel();
        loader.setIcon(Util.ICON_LOADER);
        loader.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        panel.add(labelTexte, BorderLayout.NORTH);
        panel.add(loader, BorderLayout.CENTER);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        try {
                            findUsers();
                        } catch (Throwable t) {
                            Util.logError(Util.i18n("cardmanager.error.search.users"), t);
                        }
                        return null;
                    }
                    
                    @Override
                    protected void done() {
                        switchLayout(CardManagerLayoutEnum.SELECT_USER);
                    }
                }.execute();
            }
        });
        
        return panel;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JPanel getPanelSelectUser() {
        // Text
        final JLabel labelText = new JLabel(Util.i18n("cardmanager.select.user"));
        final JPanel panelText = new JPanel();
        panelText.add(labelText);
        
        // Label user profile
        final JLabel labelUserProfile = new JLabel();
        labelUserProfile.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labelUserProfile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    final String url = bookingProc.getUserProfileUrl(String.valueOf(selectedUser.getId()));
                    Desktop.getDesktop().browse(new URI(url));
                } catch (URISyntaxException | IOException e1) {
                    Util.logError(Util.i18n("common.error.openbrowser"), e1);
                }
            }
        });
        final JPanel panelUserProfile = new JPanel();
        panelUserProfile.add(labelUserProfile);
        
        // Buttons actions
        final JButton btnAssociate = new JButton(Util.i18n("cardmanager.card.associate"));
        btnAssociate.setEnabled(false);
        btnAssociate.setIcon(Util.ICON_ADD_24);
        btnAssociate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                currentScanType = CardScanTypeEnum.ASSOCIATE;
                switchLayout(CardManagerLayoutEnum.SCAN_CARD);
            }
        });
        final JButton btnRemove = new JButton(Util.i18n("cardmanager.card.remove"));
        btnRemove.setEnabled(false);
        btnRemove.setIcon(Util.ICON_REMOVE_24);
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                currentScanType = CardScanTypeEnum.REMOVE;
                switchLayout(CardManagerLayoutEnum.SCAN_CARD);
            }
        });
        final JPanel panelBtnActions = new JPanel();
        panelBtnActions.add(btnAssociate);
        panelBtnActions.add(btnRemove);
        
        // Combo
        userModel = new DefaultComboBoxModel(new Vector());
        final JComboBox comboUser = new JComboBox(userModel);
        comboUser.insertItemAt("", 0);
        comboUser.setRenderer(new UserRenderer());
        comboUser.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                if (comboUser.getSelectedItem() instanceof User) {
                    selectedUser = (User) comboUser.getSelectedItem();
                    
                    // Change user profile link
                    labelUserProfile.setText(String.format(Util.i18n("cardmanager.userprofile"), getUserProfileLink()));
                    
                    // Change buttons actions
                    btnAssociate.setEnabled(true);
                    btnRemove.setEnabled(!Strings.isNullOrEmpty(selectedUser.getCardUuid()));
                } else {
                    selectedUser = null;
                    labelUserProfile.setText(null);
                    btnAssociate.setEnabled(false);
                    btnRemove.setEnabled(false);
                }
            }
        });
        JPanel panelCombo = new JPanel();
        panelCombo.add(comboUser);
        
        // Panel north
        JPanel panelNorth = new JPanel();
        panelNorth.setLayout(new BoxLayout(panelNorth, BoxLayout.Y_AXIS));
        panelNorth.add(panelText);
        panelNorth.add(comboUser);
        panelNorth.add(panelUserProfile);
        panelNorth.add(new JPanel());
        panelNorth.add(new JPanel());
        panelNorth.add(panelBtnActions);
        
        // Button cancel
        JButton btnCancel = new JButton(Util.i18n("common.cancel"));
        btnCancel.addActionListener(this);
        
        // Panel button cancel
        JPanel panelBtnCancel = new JPanel();
        panelBtnCancel.add(btnCancel);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        panel.add(panelNorth, BorderLayout.NORTH);
        panel.add(panelBtnCancel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel getPanelScanCard() {
        // Text
        final JLabel labelTextScanCard = new JLabel(Util.i18n("cardmanager.wait.card"));
        labelTextScanCard.setHorizontalAlignment(JLabel.CENTER);
        
        // Loading icon
        final JLabel labelResultScanCard = new JLabel();
        labelResultScanCard.setIcon(Util.ICON_LOADER);
        labelResultScanCard.setHorizontalAlignment(JLabel.CENTER);
        
        final JPanel panelScanCard = new JPanel(new BorderLayout());
        panelScanCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelScanCard.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        panelScanCard.add(labelTextScanCard, BorderLayout.NORTH);
        panelScanCard.add(labelResultScanCard, BorderLayout.CENTER);
        
        return panelScanCard;
    }
    
    private void findUsers() {
        final List<User> userList = bookingProc.getUserList();
        if (userList == null) {
            return;
        }
        
        Collections.sort(userList, new UserComparator());
        for (User user : userList) {
            userModel.addElement(user);
        }
    }
    
    private void associateCard() {
        final boolean result = bookingProc.associateCard(String.valueOf(selectedUser.getId()), currentUid);
        if (result) {
            Util.showInfoDialog(String.format(Util.i18n("cardmanager.assoc.success"),
                    currentUid,
                    selectedUser.getFirstName(),
                    selectedUser.getLastName()));
            close();
        } else {
            Util.showErrorDialog(Util.i18n("cardmanager.assoc.error"));
            switchLayout(CardManagerLayoutEnum.SELECT_USER);
        }
    }
    
    private void removeCard() {
        final boolean result = bookingProc.removeCard(String.valueOf(selectedUser.getId()), currentUid);
        if (result) {
            Util.showInfoDialog(String.format(Util.i18n("cardmanager.remove.success"),
                    currentUid,
                    selectedUser.getFirstName(),
                    selectedUser.getLastName()));
            close();
        } else {
            Util.showErrorDialog(Util.i18n("cardmanager.remove.error"));
            switchLayout(CardManagerLayoutEnum.SELECT_USER);
        }
    }
    
    private String getUserProfileLink() {
        if (selectedUser != null) {
            return bookingProc.getUserProfileUrl(String.valueOf(selectedUser.getId()));
        }
        
        return null;
    }
    
    @Override
    public void cardInserted(final Card card, final String cardUid) {
        currentUid = cardUid;
        if (currentLayout == CardManagerLayoutEnum.SCAN_CARD) {
            if (currentScanType == CardScanTypeEnum.ASSOCIATE) {
                associateCard();
            } else if (currentScanType == CardScanTypeEnum.ASSOCIATE) {
                removeCard();
            }
        }
    }
    
    @Override
    public void cardRemoved() {
    }
}
