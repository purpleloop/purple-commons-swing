package io.github.purpleloop.commons.swing.sprites.tools;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.SwingUtils;

/** Utility tool for sprites. */
public class SpriteTools extends JFrame {

	/** Logger of the class. */
	private static final Log LOG = LogFactory.getLog(SpriteTools.class);

	/** File chooser. */
	private JFileChooser fileChooser = new JFileChooser(".");

	/** Serialization tag. */
	private static final long serialVersionUID = -1224826635940209274L;

	/** Open a sprite set file. */
	private Action openSpriteSetAction = new AbstractAction("Open a sprite set") {

		/** Serialization tag. */
		private static final long serialVersionUID = -2884771883670344782L;

		@Override
		public void actionPerformed(ActionEvent e) {
			openSpriteSet();
		}
	};

	/** Open an image file. */
	private Action openImageAction = new AbstractAction("Open an sprite image") {

		/** Serialization tag. */
		private static final long serialVersionUID = -8321657149929106948L;

		@Override
		public void actionPerformed(ActionEvent e) {
			openImage();
		}
	};

	/** Action to choose a color for sprite bounds. */
	private Action chooseSpriteDefColor = new AbstractAction("Color of sprite bounds") {

		/** Serialization tag. */
		private static final long serialVersionUID = 4077533179715589424L;

		@Override
		public void actionPerformed(ActionEvent e) {
			chooseSpriteBoundsColor();
		}
	};

	/** The sprite set manager. */
	private SpriteSetManager spriteSetManager;

	/** The sprite panel. */
	private SpriteSetPanel spritePanel;

	/** Frame constructor. */
	public SpriteTools() {

		super("Sprite tools");

		spriteSetManager = new SpriteSetManager();

		spritePanel = new SpriteSetPanel(spriteSetManager);
		setContentPane(spritePanel);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu spriteSetMenu = new JMenu("Sprite set");
		menuBar.add(spriteSetMenu);

		SwingUtils.addMenuItem(openSpriteSetAction, spriteSetMenu);

		SwingUtils.addMenuItem(chooseSpriteDefColor, spriteSetMenu);

		JMenu imageMenu = new JMenu("Image");
		menuBar.add(imageMenu);

		SwingUtils.addMenuItem(openImageAction, imageMenu);

		pack();
	}

	/** Choose the color of sprite bounds. */
	protected void chooseSpriteBoundsColor() {

		JColorChooser colorChooser = new JColorChooser(Color.MAGENTA);
		JDialog colorPickerDialog = JColorChooser.createDialog(this, "Pick a color for sprite bounds", true,
				colorChooser, event -> {
					// Change color of sprite bounds as requested
					spritePanel.setSpriteDefinitionColor(colorChooser.getColor());
				}, event -> {
					// Nothing to cancel
				});

		colorPickerDialog.setVisible(true);
		
		repaint();
	}

	/** Open an image file. */
	protected void openImage() {

		fileChooser.setDialogTitle("Choose an image to open");
		int dialogResult = fileChooser.showOpenDialog(this);

		if (dialogResult == JFileChooser.APPROVE_OPTION) {
			File imageFileToOpen = fileChooser.getSelectedFile();

			try {
				spriteSetManager.createSpriteSetForImage(imageFileToOpen.getAbsolutePath());
				spritePanel.ajustToSpriteSetImage();

				pack();

				repaint();
			} catch (PurpleException e) {
				LOG.error("Error encountered while loading the sprite set image.", e);
			}

		}

	}

	/** Opens a sprite set definition file. */
	protected void openSpriteSet() {

		fileChooser.setDialogTitle("Choose a sprite set to open");
		int userChoice = fileChooser.showOpenDialog(this);

		if (userChoice == JFileChooser.APPROVE_OPTION) {
			File tileSetFileToOpen = fileChooser.getSelectedFile();

			try {
				spriteSetManager.loadsSpriteSetDefinition(tileSetFileToOpen.getAbsolutePath());
				repaint();
			} catch (PurpleException e) {
				LOG.error("Error encountered while loading the sprite set definition.", e);
			}

		}

	}

	/**
	 * Entry point.
	 * 
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpriteTools spriteTool = new SpriteTools();
		spriteTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		spriteTool.setVisible(true);
	}

}
