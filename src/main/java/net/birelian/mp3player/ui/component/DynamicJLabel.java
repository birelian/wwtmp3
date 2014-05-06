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

package net.birelian.mp3player.ui.component;

import javax.swing.JLabel;

/**
 * JLabel with moving text
 * 
 * @author birelian
 * 
 */

@SuppressWarnings("serial")
public class DynamicJLabel extends JLabel {

    /**
     * Separator between the end and the beginning of the text
     */
    private final String SEPARATOR = "    ";

    /**
     * Set a new text. Should be called when a new text is set. This method will
     * add a separator at the end of the text.
     * 
     * @param newText
     *            Text to be set
     */
    public void setNewText(String newText) {
        setText(newText + SEPARATOR);
    }

    /**
     * Update text.
     */
    public void update() {
        String originalText = this.getText();
        int length = originalText.length();

        char[] newText = new char[length];
        for (int i = 0; i < length; i++) {
            newText[i] = originalText.charAt((i + 1) % length);
        }
        this.setText(new String(newText));
    }
}
