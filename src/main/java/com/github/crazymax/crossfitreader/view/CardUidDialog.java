package com.github.crazymax.crossfitreader.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;

import javax.smartcardio.Card;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultEditorKit;

import org.apache.log4j.Logger;

import com.github.crazymax.crossfitreader.booking.User;
import com.github.crazymax.crossfitreader.device.Device;
import com.github.crazymax.crossfitreader.device.DeviceListener;
import com.github.crazymax.crossfitreader.enums.CardUidLayoutEnum;
import com.github.crazymax.crossfitreader.exception.BookingException;
import com.github.crazymax.crossfitreader.processus.BookingProc;
import com.github.crazymax.crossfitreader.tray.SysTray;
import com.github.crazymax.crossfitreader.util.Resources;
import com.github.crazymax.crossfitreader.util.Util;

/**
 * Card UID dialog
 * @author CrazyMax
 * @license MIT License
 * @link https://github.com/crazy-max/crossfit-reader
 */
public final class CardUidDialog
        extends JDialog
        implements ActionListener, DeviceListener {

    private static final long serialVersionUID = -3969093045340980759L;

    private static final Logger LOGGER = Logger.getLogger(CardUidDialog.class);

    private static final List<Image> ICONS = Arrays.asList(
            Resources.ICON_BLUE_16.getImage(),
            Resources.ICON_BLUE_32.getImage(),
            Resources.ICON_BLUE_48.getImage()
    );

    private static final int CONTENT_WIDTH = 500;
    private static final int CONTENT_HEIGHT = 210;

    private SysTray systray;
    private Device device;

    private final JPanel cards;

    private String currentUid;
    private String infoUid;

    public CardUidDialog(final SysTray systray, final String title) {
        super(new DummyFrame(title, ICONS));

        this.systray = systray;
        this.systray.removeCardListener();
        this.device = systray.getDevice();
        this.device.addCardListener(this);

        cards = new JPanel(new CardLayout());
        cards.add(getPanelScanCard(), CardUidLayoutEnum.SCAN_CARD.getName());
        cards.add(getPanelResult(), CardUidLayoutEnum.RESULT.getName());

        JPanel panneauPrincipal = new JPanel(new BorderLayout());
        panneauPrincipal.setOpaque(true);
        panneauPrincipal.add(cards, BorderLayout.CENTER);

        ((java.awt.Frame)getOwner()).setIconImage(Resources.ICON_BLUE_32.getImage());
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

    private void switchLayout(CardUidLayoutEnum layout) {
        CardLayout cl = (CardLayout) (cards.getLayout());
        cl.show(cards, layout.getName());
    }

    private JPanel getPanelScanCard() {
        // Text
        final JLabel labelTextScanCard = new JLabel(Util.i18n("cardmanager.wait.card"));
        labelTextScanCard.setHorizontalAlignment(JLabel.CENTER);

        // Loading icon
        final JLabel labelResultScanCard = new JLabel();
        labelResultScanCard.setIcon(Resources.ICON_LOADER);
        labelResultScanCard.setHorizontalAlignment(JLabel.CENTER);

        final JPanel panelScanCard = new JPanel(new BorderLayout());
        panelScanCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelScanCard.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        panelScanCard.add(labelTextScanCard, BorderLayout.NORTH);
        panelScanCard.add(labelResultScanCard, BorderLayout.CENTER);

        return panelScanCard;
    }

    private JPanel getPanelResult() {
        // Popup UID field
        final JPopupMenu uidMenu = new JPopupMenu();
        Action copy = new DefaultEditorKit.CopyAction();
        copy.putValue(Action.NAME, "Copy");
        copy.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control C"));
        uidMenu.add(copy);

        // UID field
        final JTextField uidField = new JTextField();
        uidField.setComponentPopupMenu(uidMenu);
        uidField.setFont(new Font("Courier", Font.PLAIN, 20));
        uidField.setBackground(new Color(255, 255, 192));
        uidField.setHorizontalAlignment(JLabel.CENTER);
        uidField.setPreferredSize(new Dimension(150, 40));
        uidField.addFocusListener(new FocusListener() {
            @Override public void focusLost(final FocusEvent pE) {
                // N/A
            }
            @Override public void focusGained(final FocusEvent pE) {
                uidField.selectAll();
            }
        });

        // Info UID
        JLabel labelInfoUid = new JLabel(Util.i18n("cardmanager.intro"));
        labelInfoUid.setHorizontalAlignment(JLabel.CENTER);

        final JPanel panelInfoUid = new JPanel();
        panelInfoUid.add(labelInfoUid);
        panelInfoUid.setPreferredSize(new Dimension(CONTENT_WIDTH, 20));

        final JPanel panelBlank = new JPanel();
        panelBlank.setPreferredSize(new Dimension(CONTENT_WIDTH, 50));

        final JPanel panelUid = new JPanel();
        panelUid.setPreferredSize(new Dimension(CONTENT_WIDTH - 100, CONTENT_HEIGHT - 100));
        panelUid.add(panelBlank);
        panelUid.add(uidField);
        panelUid.add(panelInfoUid);

        // Buttons
        JButton btnCopy = new JButton(Util.i18n("common.copyuid"));
        btnCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Util.copyToClipboard(currentUid);
            }
        });

        JButton btnRescan = new JButton(Util.i18n("carduid.rescan"));
        btnRescan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                switchLayout(CardUidLayoutEnum.SCAN_CARD);
            }
        });

        JButton btnClose = new JButton(Util.i18n("common.close"));
        btnClose.addActionListener(this);

        JPanel panelBtn = new JPanel();
        panelBtn.add(btnCopy);
        panelBtn.add(btnRescan);
        panelBtn.add(btnClose);

        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(CONTENT_WIDTH, CONTENT_HEIGHT));
        panel.add(panelUid, BorderLayout.CENTER);
        panel.add(panelBtn, BorderLayout.SOUTH);
        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(final ComponentEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() {
                        uidField.setText(currentUid);
                        labelInfoUid.setText(infoUid);
                        return null;
                    }
                }.execute();
            }
        });

        return panel;
    }

    @Override
    public void cardInserted(final Card card, final String cardUid) {
        currentUid = cardUid;
        infoUid = null;
        try {
            final User userScan = BookingProc.getInstance().scanCard(cardUid);
            if (userScan == null) {
                LOGGER.warn(String.format("The card %s belongs to nobody", cardUid));
                infoUid = Util.i18n("carduid.nobody");
            } else {
                LOGGER.info(String.format("This card belongs to %s %s", userScan.getFirstName(), userScan.getLastName()));
                infoUid = String.format(Util.i18n("carduid.tomember"), userScan.getFirstName(), userScan.getLastName());
            }
        } catch (BookingException e) {
            LOGGER.error(e.getMessage(), e);
            infoUid = e.getMessage();
        } finally {
            switchLayout(CardUidLayoutEnum.RESULT);
        }
    }

    @Override
    public void cardRemoved() {
        // N/A
    }
}
