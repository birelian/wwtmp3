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

package net.birelian.mp3player.core;

import java.util.concurrent.TimeUnit;

import net.birelian.mp3player.ui.MainWindow;
import net.birelian.mp3player.ui.VisualPlayList;

/**
 * Updates displays for user information (artist, title, bitrate, etc)
 * 
 * @author birelian
 * 
 */
public class DisplayUpdater {

    /**
     * Update Static Display. Information that is updated once per song
     * 
     * @param playerManager
     *            Player manager
     */
    public void updateStaticDisplayInformation(PlayerManager playerManager) {

        int lenght = playerManager.getSongManager().getCurrentSong()
                .getLength();
        String artist = playerManager.getSongManager().getCurrentSong()
                .getArtist();
        String title = playerManager.getSongManager().getCurrentSong()
                .getTitle();
        String bitrate = playerManager.getSongManager().getCurrentSong()
                .getBitRate();
        String sampleRate = playerManager.getSongManager().getCurrentSong()
                .getSampleRate();
        String channels = playerManager.getSongManager().getCurrentSong()
                .getChannels();

        String totalTime = String.format(
                "%02d:%02d",
                TimeUnit.SECONDS.toMinutes(lenght),
                TimeUnit.SECONDS.toSeconds(lenght)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS
                                .toMinutes((lenght))));
        MainWindow.getInstance().getTxtTotalTime().setText(totalTime);
        MainWindow.getInstance().getTxtTitle()
                .setNewText(artist + " - " + title);
        MainWindow.getInstance().getTxtMetadata()
                .setText(bitrate + " - " + sampleRate + " - " + channels);
    }

    /**
     * Update Dynamic Display. Information that is updated constantly during
     * playback
     * 
     * @param playerManager
     *            Player manager
     */
    public void updateDynamicDisplayInformation(PlayerManager playerManager) {
        float max = playerManager.getSongManager().getCurrentSong().getFrames();
        float left = max - playerManager.getPlayer().getFrame();
        float done = 0;
        float lenght = playerManager.getSongManager().getCurrentSong()
                .getLength();

        done = ((max - left) / max) * 1000;
        // Elapsed time in seconds
        float elapsed = (done * lenght) / 1000;
        // Formatting elapsed time
        String timeElapsed = String.format(
                "%02d:%02d",
                TimeUnit.SECONDS.toMinutes((int) elapsed),
                TimeUnit.SECONDS.toSeconds((int) elapsed)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS
                                .toMinutes((int) elapsed)));
        // Set elapsed time
        MainWindow.getInstance().getTxtTimeElapsed().setText(timeElapsed);

        // If user is not adjusting progressSlider...
        if (!MainWindow.getInstance().getProgressSlider().getValueIsAdjusting())
            MainWindow.getInstance().getProgressSlider().setValue((int) done);

        // Update Artist - Title
        MainWindow.getInstance().getTxtTitle().update();
    }

    /**
     * Reset displays
     */
    public void clearDisplay() {
        MainWindow.getInstance().getTxtTotalTime().setText("00:00");
        MainWindow.getInstance().getTxtTitle().setText("Waiting");
        MainWindow.getInstance().getTxtMetadata()
                .setText("for user instructions");
        MainWindow.getInstance().getTxtTimeElapsed().setText("00:00");
        MainWindow.getInstance().getProgressSlider().setValue(0);
    }

    /**
     * Update current song in the visual playlist
     */
    public void updateCurrentSong(PlayerManager playerManager) {
        VisualPlayList.getInstance().highlightPlayingSong(
                playerManager.getSongManager().getCurrentSong());
    }
}
