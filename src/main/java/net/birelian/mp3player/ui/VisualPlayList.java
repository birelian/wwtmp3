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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import javazoom.jl.decoder.JavaLayerException;
import net.birelian.mp3player.core.PlayerManager;
import net.birelian.mp3player.core.Song;
import net.birelian.mp3player.ui.component.CircleButton;
import net.birelian.mp3player.ui.component.TableNonEditable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VisualPlayList {
	/** Logger */
	private final Logger logger = LogManager.getLogger(VisualPlayList.class);
	
	/** Instance */
	private static VisualPlayList instance;
	
	/** Main JFrame */
	private JFrame frmWwteamMpPlayer;
	
	/** Table representing the playlist */
	private JTable table;
	
	/** Play list table model */
	private DefaultTableModel tableModel;

	/** Player Manager */
	private PlayerManager playerManager;
	
	/** File chooser */
	private final JFileChooser fc = new JFileChooser();
	
	/** Background */
	private JLabel background;
	
	/**
	 * Get instance
	 * 
	 * @return Visual playlist instance
	 */
	public static VisualPlayList getInstance(){
		return instance;
	}
	
	/**
	 * Set the PlayerManager
	 * 
	 * @param playerManager Player manager
	 */
	public void setPlayerManager(PlayerManager playerManager) {
		this.playerManager = playerManager;
	}

	/**
	 * Constructor
	 */
	public VisualPlayList() {
		instance = this;
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// JFrame configuration
		frmWwteamMpPlayer = new JFrame();
		frmWwteamMpPlayer.setResizable(false);
		frmWwteamMpPlayer.setTitle("WWTeam MP3 Player - Playlist");
		frmWwteamMpPlayer.setBounds(520, 100, 450, 330);
		frmWwteamMpPlayer.setBackground(new Color(230,230,230));
		frmWwteamMpPlayer.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		// Background
		background = new JLabel("");
		BufferedImage img;
		try {
			img = ImageIO.read(getClass().getResource("/images/bg_metal.jpg"));
			background = new JLabel(new ImageIcon(img));
		} catch (IOException e1) {
				e1.printStackTrace();
		}
		background.setBounds(0, 0, 450, 303);
		frmWwteamMpPlayer.getContentPane().add(background);
		
		// TableModel configuration
		tableModel = new DefaultTableModel();
		tableModel.addColumn("songAddress");
		tableModel.addColumn("songInfo");
		
		// File chooser configuration. Multiple mp3 selection is allowed.
		FileNameExtensionFilter filter = new FileNameExtensionFilter("MP3 files", "mp3", "text");
		fc.setFileFilter(filter);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setMultiSelectionEnabled(true);

		// Table configuration
		table = new TableNonEditable();
		table.setFont(new Font("Dialog", Font.BOLD, 12));
		table.setTableHeader(null);
		JScrollPane tablePane = new JScrollPane(table);
		tablePane.setBounds(20, 20, 405, 220);
		background.add(tablePane);
		table.setName("Playlist");
		table.setModel(tableModel);
		table.setBackground(new Color(240,240,240));
		// First column is just for internal purposes. Not shown
		table.removeColumn(table.getColumnModel().getColumn(0));
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setBounds(12, 12, 422, 174);
		table.addMouseListener(new MouseAdapter() 
		   {
		      public void mouseClicked(MouseEvent e) 
		      {
		    	  playDoubleClickedSong(e);
		      }
		   });
		
		// Add songs Button
		JButton btnAdd = new CircleButton(null);
		btnAdd.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/add35.png")));
		
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnValue = fc.showOpenDialog(frmWwteamMpPlayer);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
		            File[] songs = fc.getSelectedFiles();
		            for (int i = 0; i < songs.length; i++)
		            	playerManager.add(songs[i]);
		        } else {
		        	System.out.println("Open command cancelled by user");
		        	logger.info("Open command cancelled by user");
		        }
			}
		});
		btnAdd.setBounds(160, 250, 35, 35);
		background.add(btnAdd);
		
		//Remove song Button
		JButton btnRemove = new CircleButton(null);
		btnRemove.setIcon(new ImageIcon(MainWindow.class.getResource("/icons/remove35.png")));
		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedSongs();
			}
		});
		btnRemove.setBounds(240, 250, 35, 35);
		background.add(btnRemove);
	}

	/**
	 * Set visible
	 */
	public void show(){
		this.frmWwteamMpPlayer.setVisible(true);
	}
	
	/**
	 * Enqueue a song
	 * 
	 * @param song Song to be added
	 */
	public void enqueue(Song song){
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addRow(new Object[]{song, song.getArtist() + " - " + song.getTitle()});
	}
	
	/**
	 * Highlights the current playing song in the list
	 * 
	 * @param song Playing song
	 */
	public void highlightPlayingSong(Song song){
		// Search the song in the list
		for (int i = 0; i < tableModel.getRowCount(); i++){
			if (tableModel.getValueAt(i, 0).equals(song)){
				// Found
				table.setRowSelectionInterval(i, i);
				return;
			}	
		}
	}

	/**
	 * Play double-clicked song
	 * 
	 * @param e Mouse Event to be processed
	 */
	private void playDoubleClickedSong(MouseEvent e){
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        // If a row is selected by double-click, play the song
        if ((row > -1) && (column > -1) && e.getClickCount() == 2){
       	 playerManager.getSongManager().setCurrentSong((Song) tableModel.getValueAt(row,column));
       	 try {
				playerManager.play(0);
				// startedPlaying event
				MainWindow.getInstance().getEventManager().startedPlaying();
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
        }		
	}
	
	/**
	 * Remove selected song
	 */
	private void removeSelectedSongs(){
		Song song;
		table.getSelectedRows();
		// Important: Delete backwards so we don't have problems with changed indexes caused
		// for previous row deletions. The safer way seems to create a descending sorted list
		// of indexes of the selected rows. This way, when we delete a row, it won't affect
		// next deletions.
		
		if (table.getSelectedRowCount() > 0){
		
			// Create the list of selected rows
			List<Integer> selectedRows = new ArrayList<Integer>();
			for (int i : table.getSelectedRows())
				selectedRows.add((Integer)i);
			// Sort the list
			Collections.sort(selectedRows,Collections.reverseOrder());
			
			// Proceed with deletions
        	for (Integer rowIndex : selectedRows){
        		song = (Song) tableModel.getValueAt(rowIndex, 0);
                playerManager.remove(song);
                tableModel.removeRow(rowIndex);
                // Check if there are more songs
                int rowCount = tableModel.getRowCount();
                if (rowCount == 0) return;
                // Select next row. Be careful with the last song
                if (rowIndex < rowCount)
                	table.setRowSelectionInterval(rowIndex, rowIndex);
                else 
                	table.setRowSelectionInterval(rowIndex -1 , rowIndex - 1);		
        	}
        }
	}
}
