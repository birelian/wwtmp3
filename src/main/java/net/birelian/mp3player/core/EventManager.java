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

import javax.swing.ImageIcon;

import net.birelian.mp3player.ui.MainWindow;
import net.birelian.mp3player.ui.VisualPlayList;

/**
 * Manages events.
 * 
 * @author birelian
 */
public class EventManager {
	/** Main window */
	private MainWindow mainWindow;
	
	/** Visual playlist */
	private VisualPlayList visualPlayList;
	
	/** Player manager */
	private PlayerManager playerManager;
	
	/** Display updater */
	private DisplayUpdater displayUpdater;
	
	/**
	 * Constructor 
	 * 
	 * @param mainWindow Main window
	 * @param visualPlayList Visual playlist
	 * @param playerManager Player manager
	 * @param displayUpdater Display updater
	 */
	public EventManager(MainWindow mainWindow, VisualPlayList visualPlayList, PlayerManager playerManager, DisplayUpdater displayUpdater){
		this.mainWindow = mainWindow;
		this.visualPlayList = visualPlayList;
		this.playerManager = playerManager;
		this.displayUpdater = displayUpdater;
	}
	
	/**
	 * Actions that should be taken when starting playback
	 */
	public void startedPlaying(){
		if (playerManager.getSongManager().getCurrentSong() != null){
			displayUpdater.updateStaticDisplayInformation(playerManager);
			mainWindow.getBtnPlay().setIcon(new ImageIcon(MainWindow.class.getResource("/icons/pause35.png")));
		}
	}
	
	/**
	 * Actions that should be taken when pausing playback
	 */	
	public void pausedPlaying(){
		mainWindow.getBtnPlay().setIcon(new ImageIcon(MainWindow.class.getResource("/icons/play35.png")));
	}
	
	/**
	 * Actions that should be taken when resuming playback
	 */	
	public void resumedPlaying(){
		mainWindow.getBtnPlay().setIcon(new ImageIcon(MainWindow.class.getResource("/icons/pause35.png")));
	}	
	
	/**
	 * Actions that should be taken when stopping playback
	 */	
	public void stoppedPlaying(){
		// Set current song to the first song
		playerManager.getSongManager().first();
		// Set play/pause icon
		mainWindow.getBtnPlay().setIcon(new ImageIcon(MainWindow.class.getResource("/icons/play35.png")));
		// Clear display
		displayUpdater.clearDisplay();
		// Update visual play list highlighted element
		visualPlayList.highlightPlayingSong(playerManager.getSongManager().getCurrentSong());
	}
	
	/**
	 * Actions that should be taken when song is changed
	 */	
	public void songChanged(){
		// Update Display
		displayUpdater.updateStaticDisplayInformation(playerManager);
		// Update VisualPlayList
		visualPlayList.highlightPlayingSong(playerManager.getSongManager().getCurrentSong());	
	}
	
	/**
	 * Actions that should be taken when a new song is added
	 */	
	public void songAdded(Song song){
		visualPlayList.enqueue(song);
	}
}
