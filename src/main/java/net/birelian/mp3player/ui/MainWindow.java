/**
 * Copyright 2014 Guillermo Bauzá (birelian) - birelianATgmailDOTcom 
 * 
 * 
 * This file is part of WWT-Mp3 player.
 *
 * WWT-Mp3 player is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * WWT-Mp3 player is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with WWT-Mp3 player.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package net.birelian.mp3player.ui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import javazoom.jl.decoder.JavaLayerException;
import net.birelian.mp3player.core.DisplayUpdater;
import net.birelian.mp3player.core.EventManager;
import net.birelian.mp3player.core.PlayerManager;
import net.birelian.mp3player.ui.component.CircleButton;
import net.birelian.mp3player.ui.component.DynamicJLabel;
import net.birelian.mp3player.ui.component.RoundedPanel;

/**
 * Main Window. The actual player front-end.
 * 
 * @author birelian
 * 
 */
public class MainWindow {

    /** Main window */
    private static MainWindow instance;

    /** Display updater reference */
    private final DisplayUpdater displayUpdater = new DisplayUpdater();

    /** Main JFrame containing everything */
    private final JFrame frmWwteamMpPlayer = new JFrame();

    /** Player manager for all the playing stuff */
    private final PlayerManager playerManager = new PlayerManager();

    /** Visual playList */
    private final VisualPlayList visualPlayList = new VisualPlayList();

    /** Event manager */
    private final EventManager eventManager = new EventManager(this,
            visualPlayList, playerManager, displayUpdater);

    /** JLabel used for painting the background image */
    private final JLabel background = new JLabel();

    /** Label that contains info about artist, title and album */
    private final DynamicJLabel txtTitle = new DynamicJLabel();

    /** Label that contains metadata info like bitrate, sample rate, etc */
    private final JLabel txtMetadata = new JLabel();

    /** Label that shows elapsed time */
    private final JLabel txtTimeElapsed = new JLabel();

    /** Label that shows song length */
    private final JLabel txtTotalTime = new JLabel();

    /** Progress slider */
    private final JSlider progressSlider = new JSlider();

    /** Play button */
    private final JButton btnPlay = new CircleButton(null);

    /** Stop button */
    private final JButton btnStop = new CircleButton(null);

    /** Next button */
    private final JButton btnNext = new CircleButton(null);

    /** Previous button */
    private final JButton btnPrevious = new CircleButton(null);

    /** Playlist button */
    private final JButton btnPlaylist = new CircleButton(null);

    /** Display background color */
    private final Color DISPLAY_BACKGROUND_COLOR = new Color(70, 70, 70);

    /** Display foreground color */
    private final Color DISPLAY_FONT_COLOR = new Color(0, 200, 0);

    /** Display font */
    private Font displayFont;

    /**
     * Get song title, artist, album label
     * 
     * @return Song title, artist, album label
     */
    public DynamicJLabel getTxtTitle() {
        return txtTitle;
    }

    /**
     * Get metadata label
     * 
     * @return Metadata label
     */
    public JLabel getTxtMetadata() {
        return txtMetadata;
    }

    /**
     * Get time elapsed label
     * 
     * @return Time elapsed label
     */
    public JLabel getTxtTimeElapsed() {
        return txtTimeElapsed;
    }

    /**
     * Get total time label
     * 
     * @return Total time label
     */
    public JLabel getTxtTotalTime() {
        return txtTotalTime;
    }

    /**
     * Get Play button
     * 
     * @return Play button
     */
    public JButton getBtnPlay() {
        return btnPlay;

    }

    /**
     * Get progress slider
     * 
     * @return Progress slider
     */
    public JSlider getProgressSlider() {
        return progressSlider;
    }

    /**
     * Get main window instance
     * 
     * @return reference to the Main Window instance
     */
    public static MainWindow getInstance() {
        return instance;
    }

    /**
     * Get event manager
     * 
     * @return Event manager
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Get display updater
     * 
     * @return Display Updater reference
     */
    public DisplayUpdater getDisplayUpdater() {
        return displayUpdater;
    }

    /**
     * Launch application
     * 
     * @param args
     *            Ignored arguments
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainWindow window = new MainWindow();
                    window.frmWwteamMpPlayer.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Constructor
     */
    public MainWindow() {
        instance = this;

        visualPlayList.setPlayerManager(playerManager);
        initialize();
    }

    /**
     * Initialize components
     */
    private void initialize() {
        // Frame configuration
        frmWwteamMpPlayer.setResizable(false);
        frmWwteamMpPlayer.setBackground(SystemColor.windowBorder);
        frmWwteamMpPlayer.getContentPane().setBackground(
                new Color(240, 240, 240));
        frmWwteamMpPlayer.setTitle("WWTeam MP3 Player");
        frmWwteamMpPlayer.setBounds(100, 100, 420, 205);
        frmWwteamMpPlayer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Font
        try {
            displayFont = Font.createFont(Font.TRUETYPE_FONT,
                    this.getClass().getResourceAsStream("/fonts/freak.ttf"))
                    .deriveFont(Font.PLAIN, 24);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Background
        BufferedImage img;
        try {
            img = ImageIO.read(getClass().getResource("/images/bg_metal.jpg"));
            background.setIcon(new ImageIcon(img));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        background.setBounds(0, 0, 420, 205);
        frmWwteamMpPlayer.getContentPane().add(background);

        // Play button
        btnPlay.setIcon(new ImageIcon(MainWindow.class
                .getResource("/icons/play35.png")));
        btnPlay.setBounds(159, 130, 35, 35);

        background.add(btnPlay);
        btnPlay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switch (playerManager.getPlayerStatus()) {
                case NOTSTARTED:
                case FINISHED:
                    try {
                        playerManager.play(0);
                        eventManager.startedPlaying();
                    } catch (JavaLayerException e1) {
                        e1.printStackTrace();
                    }
                    break;
                case PLAYING:
                    playerManager.pause();
                    eventManager.pausedPlaying();
                    break;
                case PAUSED:
                    playerManager.resume();
                    eventManager.resumedPlaying();
                default:
                    break;
                }
            }
        });

        // Stop button
        btnStop.setIcon(new ImageIcon(MainWindow.class
                .getResource("/icons/stop35.png")));
        btnStop.setBounds(207, 130, 35, 35);

        background.add(btnStop);
        btnStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerManager.stop();
            }
        });

        // Previous button
        btnPrevious.setIcon(new ImageIcon(MainWindow.class
                .getResource("/icons/previous35.png")));
        btnPrevious.setBounds(112, 130, 35, 35);

        background.add(btnPrevious);
        btnPrevious.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerManager.previous();
            }
        });

        // Next button
        btnNext.setIcon(new ImageIcon(MainWindow.class
                .getResource("/icons/next35.png")));
        btnNext.setBounds(254, 130, 35, 35);

        background.add(btnNext);
        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                playerManager.next();
            }
        });

        // Add button
        btnPlaylist.setIcon(new ImageIcon(MainWindow.class
                .getResource("/icons/list35.png")));
        btnPlaylist.setBounds(368, 130, 35, 35);
        background.add(btnPlaylist);

        btnPlaylist.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                visualPlayList.show();
            }
        });

        // Displays creation
        JPanel panelTimeElapsed = new RoundedPanel();
        JPanel panelTotalTime = new RoundedPanel();
        JPanel panelSongInfo = new RoundedPanel();

        // 1. Time elapsed
        panelTimeElapsed.setBounds(312, 12, 94, 36);
        panelTimeElapsed.setBackground(DISPLAY_BACKGROUND_COLOR);
        txtTimeElapsed.setText("00:00");
        txtTimeElapsed.setHorizontalAlignment(SwingConstants.CENTER);
        txtTimeElapsed.setFont(displayFont.deriveFont(24F));
        txtTimeElapsed.setForeground(DISPLAY_FONT_COLOR);
        panelTimeElapsed.add(txtTimeElapsed);
        background.add(panelTimeElapsed);
        // 2. Total time
        panelTotalTime.setBounds(312, 53, 94, 36);
        panelTotalTime.setBackground(DISPLAY_BACKGROUND_COLOR);
        txtTotalTime.setText("00:00");
        txtTotalTime.setHorizontalAlignment(SwingConstants.CENTER);
        txtTotalTime.setForeground(DISPLAY_FONT_COLOR);
        txtTotalTime.setFont(displayFont.deriveFont(24F));
        panelTotalTime.add(txtTotalTime);
        background.add(panelTotalTime);
        // 3. Song info
        panelSongInfo.setBorder(null);
        panelSongInfo.setBounds(12, 12, 288, 77);
        panelSongInfo.setBackground(DISPLAY_BACKGROUND_COLOR);
        background.add(panelSongInfo);
        panelSongInfo.setLayout(null);

        txtTitle.setHorizontalAlignment(SwingConstants.CENTER);
        txtTitle.setText("Artist - And the title");
        txtTitle.setFont(displayFont.deriveFont(36));
        txtTitle.setForeground(DISPLAY_FONT_COLOR);
        txtTitle.setBounds(12, 8, 264, 25);
        panelSongInfo.add(txtTitle);

        txtMetadata.setHorizontalAlignment(SwingConstants.CENTER);
        txtMetadata.setText("128 kbps - 441000 Hz - Stereo");
        txtMetadata.setFont(displayFont.deriveFont(16F));
        txtMetadata.setForeground(DISPLAY_FONT_COLOR);
        txtMetadata.setBounds(12, 42, 264, 15);
        panelSongInfo.add(txtMetadata);

        // Progress slider
        progressSlider.setBounds(12, 102, 394, 16);
        progressSlider.setMinimum(0);
        progressSlider.setMaximum(1000);
        progressSlider.setValue(0);
        progressSlider.setOpaque(false);
        background.add(progressSlider);

        progressSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                playerManager.jump(progressSlider.getValue() / 10);
                progressSlider.setValueIsAdjusting(true);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                playerManager.jump(progressSlider.getValue() / 10);
                progressSlider.setValueIsAdjusting(false);
            }
        });
    }
}
