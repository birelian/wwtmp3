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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javazoom.jl.decoder.JavaLayerException;
import net.birelian.mp3player.ui.MainWindow;

/**
 * Player manager. Uses a CustomPlayer object for playing mp3 files.
 * 
 * @author birelian
 *
 */
public class PlayerManager {
	/** Logger */
	private final Logger logger = LogManager.getLogger(PlayerManager.class);
    
	/** Actual player */
    private CustomPlayer player;
    
    /** Player status */
    private PlayerStatus playerStatus = PlayerStatus.NOTSTARTED;
    
    /** Locking object used to communicate with player thread */
	private final Object playerLock = new Object();

	/** Song manager */
    private SongManager songManager;
    
    /**
     *  Default constructor
     */
    public PlayerManager(){
		songManager = new SongManager();
		logger.info("Creating PausablePlayer instance");
		System.out.println("Creating PausablePlayer instance");
    }   
    
    /**
     * Get the player 
     * 
     * @return Player
     */
    public CustomPlayer getPlayer() {
		return player;
	}

    /**
     * Get the song manager
     * 
     * @return Song manager
     */
	public SongManager getSongManager() {
		return songManager;
	}
    
    /**
     * Get the player status
     * 
     * @return Player status
     */
    public PlayerStatus getPlayerStatus() {
		return playerStatus;
	}
	
    /**
     * Set the player status
     * 
     * @param playerStatus New player status
     */
    public void setPlayerStatus(PlayerStatus playerStatus) {
		this.playerStatus = playerStatus;
		System.out.println("Player status set to: " + playerStatus);
		logger.info("Player status set to: " + playerStatus);
	}
	
    /**
     * Start the playback or resumes if paused.
     * 
     * @param startFrame Frame from where start playing
     * @throws JavaLayerException
     */
    public void play(long startFrame) throws JavaLayerException {
    	// If there is a current song
    	if (songManager.getCurrentSong() != null){
	    	try {
				FileInputStream fileStream = new FileInputStream(songManager.getCurrentSong().getFilePath());
				player = new CustomPlayer(fileStream);
				// If not starting from the beginning
				if (startFrame > 0)
					player.goToFrame(startFrame);
				// startedPlaying Event
				MainWindow.getInstance().getEventManager().startedPlaying();
				synchronized (playerLock) {
		            switch (playerStatus) {
		            	case FINISHED: 
		            	case NOTSTARTED:
		                    // Playback process
		            		final Runnable playingRunnable = new Runnable() {
		                        public void run() {
									playInternal();
		                        }
		                    };
		                    final Thread playingThread = new Thread(playingRunnable);
		                    playingThread.setDaemon(true);
		                    playingThread.setPriority(Thread.MAX_PRIORITY);
		                    
		                    // Dynamic display updating process
		            		final Runnable displayUpdatingRunnable = new Runnable() {
		                        public void run() {
		                            updateDisplay();
		                        }
		                    };
		                    final Thread updateDisplayThread = new Thread(displayUpdatingRunnable);
		                    updateDisplayThread.setDaemon(true);
		                    updateDisplayThread.setPriority(Thread.MAX_PRIORITY);	                    
		                    
		                    // Run processes
		                    setPlayerStatus(PlayerStatus.PLAYING);
		                    playingThread.start();
		                    updateDisplayThread.start();
		                    break;
		                case PAUSED:
		                    resume();
		                    break;
		                default:
		                    break;
		            }
		        }
	    	} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
    	}
    }

    /**
     * Stop playing. If already stopped, does nothing
     */
    public void stop() {
	    synchronized (playerLock) {
	        setPlayerStatus(PlayerStatus.FINISHED);
	        playerLock.notifyAll();
	        // stoppedPlaying event
	        MainWindow.getInstance().getEventManager().stoppedPlaying();
	    }
	}

	/**
	 * Pause playing. 
	 * 
	 * @return true if new state is PAUSED.
	 */
    public boolean pause() {
        synchronized (playerLock) {
            if (playerStatus == PlayerStatus.PLAYING) {
                setPlayerStatus(PlayerStatus.PAUSED);
            }
            return playerStatus == PlayerStatus.PAUSED;
        }
    }

    /**
     *  Resume playing. 
     *  
     *  @return true if the new state is PLAYING.
     */
    public boolean resume() {
        synchronized (playerLock) {
            if (playerStatus == PlayerStatus.PAUSED) {
                setPlayerStatus(PlayerStatus.PLAYING);
                playerLock.notifyAll();
            }
            return playerStatus == PlayerStatus.PLAYING;
        }
    }

    /**
     * Play next song
     */
	public void next(){
		try{
			// Prepare next song
			if (songManager.next() != null){
				// Next song is now the new currentSong
				logger.info("Playing " + songManager.getCurrentSong().getFilePath());
				System.out.println("Playing " + songManager.getCurrentSong().getFilePath());
				// songChanged event
				MainWindow.getInstance().getEventManager().songChanged();
				play(0);
			}
			else 
				// No more songs. Stop.
				stop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Play previous song
	 */
	public void previous(){
		try{
			// Prepare previous song
			if (songManager.previous() != null){
				// Previous song is now the new currentSong
				logger.info("Playing " + songManager.getCurrentSong().getFilePath());
				System.out.println("Playing " + songManager.getCurrentSong().getFilePath());
				// songChanged event
				MainWindow.getInstance().getEventManager().songChanged();
				play(0);
			}else
				// No previous song. Stop
				stop();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Jump to a certain position.
	 *
	 * @param percent Percent that we want to skip
	 */
	public void jump(int percent){
		long frame = (percent * songManager.getCurrentSong().getFrames()) / 100;
		try {
			play(frame);
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add a song to the playlist
	 * 
	 * @param file File to be added
	 */
	public void add(File file){
		Song song = new Song (file);
		songManager.add(song);
		MainWindow.getInstance().getEventManager().songAdded(song);
	}

	/**
	 * Remove a song 
	 * 
	 * @param song Song to be removed
	 */
	public void remove(Song song){
		// If removing current song, first remove it and then play next song.
		if (song.equals(songManager.getCurrentSong())){
			songManager.remove(song);
			// If it wasn't the last song, next song is already loaded. Play it.
			if (songManager.getCurrentSong() != null && playerStatus == PlayerStatus.PLAYING)
				try {
					play(1);
				} catch (JavaLayerException e) {
					e.printStackTrace();
				}
			else
				// No more songs, or player is stopped
				stop();
		}else{
			// Otherwise, just remove the song
			songManager.remove(song);
		}
	}

	/**
	 *  Closes the player, regardless of current state.
	 */
	public void close() {
	    synchronized (playerLock) {
	        setPlayerStatus(PlayerStatus.FINISHED);
	    }
	    try {
	        player.close();
	        logger.info("Player closed");
	        System.out.println("Player closed");
	    } catch (final Exception e) {
	    }
	}

	/**
	 * Core of the playback process
	 */
	private void playInternal(){
		System.out.println("Playing " + songManager.getCurrentSong().getFilePath());
		logger.info("Playing " + songManager.getCurrentSong().getFilePath());
		// playingStarted event
		MainWindow.getInstance().getEventManager().startedPlaying();
		while (playerStatus != PlayerStatus.FINISHED) {
            try {
            	// When song is over, automatically load next song
            	if (!player.play(1)) {
            		// Try to load next song
            		if (songManager.next() != null){
	            		FileInputStream fileStream = new FileInputStream(songManager.getCurrentSong().getFilePath());
	        			// songChanged event
	            		MainWindow.getInstance().getEventManager().songChanged();
	            		player = new CustomPlayer(fileStream);
	            		logger.info("Playing " + songManager.getCurrentSong().getFilePath());
	            		System.out.println("Playing " + songManager.getCurrentSong().getFilePath());
            		}else{
            			// No more songs.
            			break;
            		}
            	}
            } catch (final JavaLayerException e) {
                break;
            } catch (FileNotFoundException e) {
				e.printStackTrace();
			}
            // Check if paused
            synchronized (playerLock) {
                while (playerStatus == PlayerStatus.PAUSED) {
                    try {
                        playerLock.wait();
                    } catch (final InterruptedException e) {
                        // Terminate player
                        break;
                    }
                }
            }
		}
        //Close player and stoppedEvent
		MainWindow.getInstance().getEventManager().stoppedPlaying();
		close();
    }    

	/**
	 * Update Display using a DisplayUpdater
	 */
	private void updateDisplay() {
		// Dynamic information. Update constantly during playing
		while (playerStatus != PlayerStatus.FINISHED){ 
			MainWindow.getInstance().getDisplayUpdater().updateDynamicDisplayInformation(this);
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
			// Check if paused or terminated
	        synchronized (playerLock) {
	        	while (playerStatus == PlayerStatus.PAUSED) {
	        		try {
	        			playerLock.wait();
	                }catch (final InterruptedException e) {
	                	break;
	                }
	            }
	        }
		}
    }    
}