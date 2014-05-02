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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

/**
 * Stores information about a song.
 * 
 * @author birelian
 *
 */
public class Song {
	/** Logger */
	private final Logger logger = LogManager.getLogger(Song.class);
	
	/** Absolute path */
	private String filePath;
	
	/** Song title */
	private String title;
	
	/** Song artist */
	private String artist;
	
	/** Song album */
	private String album;
	
	/** Song bit rate */
	private String bitRate;
	
	/** Song sample rate */
	private String sampleRate;
	
	/** Song channels */
	private String channels;
	
	/** Song channels */
	private String format;
	
	/** Number of frames */
	private long frames;
	
	/** Song length measured in seconds */
	private int length;

	/**
	 * Default constructor
	 */
	public Song(){}
	
	/**
	 * Constructs a Song object extracting information from a given file 
	 * 
	 * @param file Song file
	 */
	public Song(File file){
		filePath = file.getAbsolutePath();
		try {
			// Used to read mp3 header
			MP3AudioHeader audioHeader = new MP3AudioHeader(file);
			
			// Used to read mp3 tags
			MP3File mp3File = (MP3File) AudioFileIO.read(file);
			Tag tag = mp3File.getTag();

			// mp3 header information
			length = audioHeader.getTrackLength();
			bitRate = audioHeader.getBitRate() + "kbps";
			sampleRate = audioHeader.getSampleRate() + "Hz" ;
			channels = audioHeader.getChannels();
			format = audioHeader.getFormat();
			frames = audioHeader.getNumberOfFrames();			
			
			// mp3 Tag information
			title = tag.getFirst(FieldKey.TITLE);
			if (title.length() == 0) title = "Unknown title";
			artist = tag.getFirst(FieldKey.ARTIST);
			if (artist.length() == 0) artist = "Unknown artist";
			
			logger.info( "Created song: " + title);
			System.out.println("Created song: " + title);
			
		} catch (Exception e) {
			logger.error("Error creating song from " + file.getAbsolutePath() );
			e.printStackTrace();
		}
	}
	
	/** 
	 * Get file path 
	 * 
	 * @return File absolute path 
	 */
	public String getFilePath() {
		return filePath;
	}
	
	/**
	 * Set file path
	 * 
	 * @param filePath File absolute path
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 *  Get song title
	 * 
	 * @return Song title
	 */
	public String getTitle() {
		return title;
	}
	
	/** 
	 * Set song title
	 * 
	 * @param title Song title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Get song artist
	 * 
	 * @return Song artist
	 * */
	public String getArtist() {
		return artist;
	}
	
	/**
	 * Set song artist
	 * 
	 * @param artist Song artist
	 *
	 */
	public void setArtist(String artist) {
		this.artist = artist;
	}
	
	/**
	 * Get song album
	 * 
	 * @return Song album
	 */
	public String getAlbum() {
		return album;
	}
	
	/**
	 * Set song album
	 * 
	 * @param album Song album
	 */
	public void setAlbum(String album) {
		this.album = album;
	}
	
	/**
	 * Get song length
	 * 
	 * @return Song length in seconds
	 */
	public int getLength() {
		return length;
	}
	
	/** 
	 * Set song length
	 * 
	 * @param length Song length in seconds
	 */
	public void setLength(int length) {
		this.length = length;
	}
	
	/** 
	 * Get song bit rate
	 * 
	 * @return Song bit rate
	 */
	public String getBitRate() {
		return bitRate;
	}
	
	/** 
	 * Set song bit rate
	 * 
	 * @param bitRate Song bit rate
	 */
	public void setBitRate(String bitRate) {
		this.bitRate = bitRate;
	}
	
	/** 
	 * Get song sample rate
	 * 
	 * @return Song sample rate
	 */
	public String getSampleRate() {
		return sampleRate;
	}
	
	/**
	 * Set song sample rate
	 * 
	 * @param sampleRate Song sample rate
	 */
	public void setSampleRate(String sampleRate) {
		this.sampleRate = sampleRate;
	}
	
	/**
	 * Get song channels
	 * 
	 * @return Song channels
	 */
	public String getChannels() {
		return channels;
	}
	
	/**
	 * Set song channels
	 * 
	 * @param channels Song channels
	 */
	public void setChannels(String channels) {
		this.channels = channels;
	}
	
	/**
	 * Get song format
	 * 
	 * @return Song format
	 */
	public String getFormat() {
		return format;
	}
	
	/**
	 * Set song format
	 * 
	 * @param format Song format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	
	/**
	 * Get number of frames
	 * 
	 * @return Number of frames
	 */
	public long getFrames() {
		return frames;
	}
	
	/**
	 * Set song number of frames
	 * 
	 * @param frames Song number of frames
	 */
	public void setFrames (long frames){
		this.frames = frames;
	}
}
