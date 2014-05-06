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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JButton;

@SuppressWarnings("serial")
/**
 * Circular button with no borders
 * 
 * @author birelian
 *
 */
public class CircleButton extends JButton {

    /**
     * Constructor
     * 
     * @param title
     *            Button text
     */
    public CircleButton(String title) {
        super(title);
        // Set width and height to same value
        Dimension size = getPreferredSize();
        size.width = size.height = Math.max(size.width, size.height);
        setPreferredSize(size);
        setContentAreaFilled(false);
    }

    /**
     * Paint button
     */
    @Override
    protected void paintComponent(Graphics g) {
        // On button click
        if (getModel().isArmed())
            g.setColor(Color.lightGray);
        // When not clicked
        else
            g.setColor(getBackground());
        // Fill circle
        g.fillOval(0, 0, getSize().width - 1, getSize().height - 1);
        // Paint
        super.paintComponent(g);
    }

    /**
     * Paint border
     */
    @Override
    protected void paintBorder(Graphics g) {
        // No border
    }
}
