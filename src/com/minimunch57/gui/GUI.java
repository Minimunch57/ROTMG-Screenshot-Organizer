package com.minimunch57.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

/**
 * 
 * @author Minimunch57
 *
 */
public class GUI extends JFrame {

	/** The serial ID for this <tt>GUI</tt>. */
	private static final long serialVersionUID = 2616296355176630665L;
	/** A <tt>JPanel</tt> used as the content pane. */
	private JPanel contentPane;
	/** A <tt>JTextField</tt> used to display text and receive file drag and drops. */
	private JTextField textField;
	
	/** A <tt>Timer</tt> responsible for resetting the display text to its default after being temporarily used for a status update. */
	private Timer timerTextReset = new Timer(2000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) {
			SwingUtilities.invokeLater(() -> {
				textField.setText("Drag and drop the screenshot here!");
			});
		}
	});

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//	Launch the application.
					new GUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * <ul>
	 * <p>	<b><i>GUI</i></b>
	 * <p>	<code>public GUI()</code>
	 * <p>	Creates a new <tt>GUI</tt>.
	 * </ul>
	 */
	public GUI() {
		//	Setup the JFrame
		setTitle("ROTMG Screenshot Organizer");
		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/com/minimunch57/images/icon.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		//	Setup the Content Pane
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		//	Setup the Text Field with Drag and Drop
		textField = new JTextField();
		textField.setDropTarget(new DropTarget() {
			/** The serial ID for the <tt>DropTarget</tt>. */
			private static final long serialVersionUID = -5846239365568158520L;

			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_REFERENCE);
					@SuppressWarnings("unchecked")
					List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					for (File file : droppedFiles) {
						final String fileExt = file.getName().substring(file.getName().lastIndexOf("."));
						final BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
						
						//	Get the created and last modified times and use the oldest one to determine date of screenshot.
						final ZonedDateTime creationTime = attr.creationTime().toInstant().atZone(ZoneId.systemDefault());
						final ZonedDateTime modifiedTime = attr.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault());
						String dropDate = "0000-00-00";
						if(creationTime.compareTo(modifiedTime)<=0) {
							dropDate = creationTime.toString().substring(0, creationTime.toString().indexOf("T"));
						}
						else {
							dropDate = modifiedTime.toString().substring(0, modifiedTime.toString().indexOf("T"));
						}
						System.out.println(creationTime.toString());
						System.out.println(modifiedTime.toString());
						System.out.println(dropDate);
						
						//	Prompt for the type of drop.
						final String[] dropOptions = new String[]{"White Bag", "Orange Bag", "Skin Drop", "Blueprint Drop"};
						int dropType = JOptionPane.showOptionDialog(null, "Which type of drop is it?", "Drop Type", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, dropOptions, dropOptions[0]);
						if(dropType != -1) {
							//	Prompt for the item name.
							final String itemName = JOptionPane.showInputDialog(null, "What is the name of item?", "Item Name", JOptionPane.QUESTION_MESSAGE);
							if(itemName!=null && !itemName.trim().isEmpty()) {
								//	Rename the file.
								boolean exists = true;
								File newFile = new File(file.getParent() + "//ROTMG " + dropOptions[dropType].toUpperCase() + " " + dropDate + " (" + itemName.trim() + ")" + fileExt);
								for(int i = 2; exists; i++) {
									if(newFile.exists() && !file.getPath().equals(newFile.getPath())) {
										newFile = new File(file.getParent() + "//ROTMG " + dropOptions[dropType].toUpperCase() + " " + dropDate + " (" + itemName.trim() + ") (#" + i + ")" + fileExt);
									}
									else {
										exists = false;
									}
								}
								file.renameTo(newFile);							
							}
							else {
								//	Operation was cancelled.
								dropType = -1;
							}
						}
						
						//	Confirm that the operation was cancelled by temporarily changing the display text.
						if(dropType == -1) {
							SwingUtilities.invokeLater(() -> {
								textField.setText("Cancelled...");
								timerTextReset.restart();
							});
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		textField.setHorizontalAlignment(JTextField.CENTER);
		textField.setEditable(false);
		textField.setBorder(null);
		textField.setFocusable(false);
		textField.setFont(new Font("Calibri", Font.PLAIN, 24));
		textField.setText("Drag and drop the screenshot here!");
		contentPane.add(textField);
		
		//	Setup Timer
		timerTextReset.setRepeats(false);
		
		//	Display the Application
		setVisible(true);
	}

}
