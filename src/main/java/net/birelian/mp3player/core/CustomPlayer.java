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

import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;

/**
 * Based in JavaZoom Player class. Just added some stuff.
 * 
 * Original class available at JavaZoom website.
 * 
 * @author birelian
 * 
 */
public class CustomPlayer {
    /** The current frame number */
    private long frame = 0;

    /** The MPEG audio bitstream */
    private Bitstream bitstream;

    /** The MPEG audio decoder */
    private Decoder decoder;

    /** The AudioDevice the audio samples are written to */
    private AudioDevice audio;

    /** Has the player been closed? */
    private boolean closed = false;

    /** Has the player played back all frames from the stream? */
    private boolean complete = false;

    /** Last known position */
    private int lastPosition = 0;

    /**
     * Creates a new Player instance.
     * 
     * @param stream
     *            Stream to be played
     * @throws JavaLayerException
     */
    public CustomPlayer(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    /**
     * Creates a new Player instance.
     * 
     * @param stream
     *            Stream to be played
     * @param device
     *            Audio device
     * @throws JavaLayerException
     */
    public CustomPlayer(InputStream stream, AudioDevice device)
            throws JavaLayerException {
        bitstream = new Bitstream(stream);
        decoder = new Decoder();

        if (device != null)
            audio = device;
        else {
            FactoryRegistry r = FactoryRegistry.systemRegistry();
            audio = r.createAudioDevice();
        }
        audio.open(decoder);
    }

    /**
     * Get current frame
     * 
     * @return Current frame
     */
    public long getFrame() {
        return frame;
    }

    /**
     * Play
     * 
     * @throws JavaLayerException
     */
    public void play() throws JavaLayerException {
        play(Integer.MAX_VALUE);
    }

    /**
     * Plays a number of MPEG audio frames.
     * 
     * @param frames
     *            The number of frames to play.
     * @return true if the last frame was played, or false if there are more
     *         frames.
     */
    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;

        while (frames-- > 0 && ret) {
            ret = decodeFrame();
            // Update current frame
            frame++;
        }
        if (!ret) {
            // Last frame, ensure all data flushed to the audio device.
            AudioDevice out = audio;
            if (out != null) {
                out.flush();
                synchronized (this) {
                    complete = (!closed);
                    close();
                }
            }
        }
        return ret;
    }

    /**
     * Closes this player. Any audio currently playing is stopped immediately.
     */
    public synchronized void close() {
        AudioDevice out = audio;
        if (out != null) {
            closed = true;
            audio = null;
            // This may fail, so ensure object state is set up before
            // calling this method.
            out.close();
            lastPosition = out.getPosition();
            try {
                bitstream.close();
            } catch (BitstreamException ex) {
            }
        }
    }

    /**
     * Returns the completed status of this player.
     * 
     * @return true if all available MPEG audio frames have been decoded, or
     *         false otherwise.
     */
    public synchronized boolean isComplete() {
        return complete;
    }

    /**
     * Retrieves the position in milliseconds of the current audio sample being
     * played. This method delegates to the AudioDevice that is used by this
     * player to sound the decoded audio samples.
     */
    public int getPosition() {
        int position = lastPosition;
        AudioDevice out = audio;

        if (out != null)
            position = out.getPosition();
        return position;
    }

    /**
     * Decodes a single frame.
     * 
     * @return true if there are no more frames to decode, false otherwise.
     */
    protected boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice out = audio;
            if (out == null)
                return false;
            Header h = bitstream.readFrame();
            if (h == null)
                return false;

            // Sample buffer set when decoder constructed
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h,
                    bitstream);

            synchronized (this) {
                out = audio;
                if (out != null)
                    out.write(output.getBuffer(), 0, output.getBufferLength());
            }
            bitstream.closeFrame();
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }

    /**
     * Skip a single frame. I just brought this method from JavaZoom
     * AdvancedPlayer
     * 
     * @return true if frame has been skipped
     * @throws JavaLayerException
     */
    protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if (h == null)
            return false;
        bitstream.closeFrame();
        return true;
    }

    /**
     * Go to a particular frame
     * 
     * @author birelian
     * @param frameNumber
     *            Frame number
     */
    public void goToFrame(long frameNumber) {
        boolean ret = true;
        long offset = frameNumber;
        while (offset-- > 0 && ret)
            try {
                ret = skipFrame();
                frame++;
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    /**
     * Set frame number
     * 
     * @author birelian
     * @param frame
     *            Number of frame
     */
    public void setFrame(long frame) {
        this.frame = frame;
    }
}