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

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages a list of Song objects. 
 * 
 * @author birelian
 *
 */
public class SongManager {
	/** Logger */
	private final Logger logger = LogManager.getLogger(SongManager.class);
	
	/** List of songs */
	private List<Song> songList;
	
	/** Current song */
	private Song currentSong;
	
	/**
	 * Default constructor. Creates an empty list of Song objects.
	 */
	public SongManager(){
		songList = new ArrayList<Song>();
	}	

	/**
	 * Get current song
	 * 
	 * @return Current song
	 */
	public Song getCurrentSong() {
		return currentSong;
	}
	
	/**
	 * Set current song
	 * 
	 * @param currentSong Current song
	 */
	public void setCurrentSong(Song currentSong) {
		this.currentSong = currentSong;
		logger.info("Current song set to: " + currentSong.getTitle());
		System.out.println("Current song set to: " + currentSong.getTitle());
	}
	
	/**
	 * Add a new song. When first song is added, it becomes the default song.
	 * 
	 * @param song Song to be added
	 */
	public void add(Song song){
		songList.add(song);
		// If there is just one song, set the currentSong
		if (songList.size() == 1){
			setCurrentSong(song);
		}
		logger.info("Added to list: " + song.getTitle());
		System.out.println("Added to list: " + song.getTitle());	
	}
	
	/**
	 * Removes a song.
	 * 
	 * @param song Song to be removed.
	 */
	public void remove(Song song){
		int songIndex = songList.indexOf(song);
		// Search 
		if (songList.size() > 0 && songIndex > -1){
			// If removing current song, set next song as new current song
			if (currentSong != null && currentSong.equals(song))
				next();
			// Remove the song
			songList.remove(song);
			logger.info("Removed from list: " + song.getTitle());
			System.out.println("Removed from list: " + song.getTitle());	
		}
	}
	
	/**
	 * Retrieve next song.
	 * 
	 * @return Next song. If there is no next song, returns null
	 */
	public Song next(){
		// In case there is more than one song
		if (songList.size() > 1){ 
			int currentSongIndex = songList.indexOf(currentSong);
			// If found and it's not the last song, return next song
			if (currentSongIndex > -1 && currentSongIndex < songList.size() - 1 ){
				setCurrentSong(songList.get(currentSongIndex + 1));
				return currentSong;
			}
			// Other cases, currentSong = null and return null
			else{
				currentSong = null;
				return null;
			}			
		}
		// There is just one song. No next song. Return null.
		currentSong = null;
		return null;
	}
	
	/**
	 * Retrieve previous song.
	 * 
	 * @return Previous song. If there is no previous song, returns null.
	 */
	public Song previous(){
		// In case there are more than one song		
		if (songList.size() > 1){ 
			int currentSongIndex = songList.indexOf(currentSong);
			// If found and it's not the first song, return previous
			if (currentSongIndex > 0){
				setCurrentSong(songList.get(currentSongIndex - 1));
				return currentSong;
			}
			// Other cases, return null
			else
				return null;
		}
		// There is just one song. No next song.
		return null;
	}	

	/**
	 * Retrieve the fist song
	 * 
	 * @return First song. If song list is empty, returns null.
	 */
	public Song first(){
		// If the list is not empty, set currentSong to first song.
		if(!songList.isEmpty()){
			setCurrentSong(songList.get(0));
			return currentSong;
		}
		return null;
	}
}
