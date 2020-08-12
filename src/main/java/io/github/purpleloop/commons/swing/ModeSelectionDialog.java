package io.github.purpleloop.commons.swing;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** A dialog box to configure the display mode. */
public class ModeSelectionDialog extends JDialog implements ActionListener {

    /** Serial tag. */
    private static final long serialVersionUID = 4768841489856279548L;

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(ModeSelectionDialog.class);

    /** Selection validation command. */
    private static final String CMD_OK = "CMD_OK";

    /** Windowed mode selection command. */
    private static final String CMD_WINDOWED = "CMD_WINDOWED";

    /** Full screen mode selection command. */
    private static final String CMD_FULLSCREEN = "CMD_FULLSCREEN";

    /** Windowed mode radio button. */
    private JRadioButton rbWindowed;

    /** Full screen mode radio button. */
    private JRadioButton rbFullScreen;

    /** Display modes supported by the graphical device to configure. */
    private DisplayMode[] displayModes;

    /** A combo-box to select the display mode to use for full screen. */
    private JComboBox<String> displayModeComboBox;

    /** The currently selected mode for full screen. */
    private DisplayMode selectedModeForFullScreen;

    /**
     * Creates a dialog box for configuring the display mode of the graphic
     * device.
     * 
     * @param owner owner frame
     * @param graphicDevice the graphic device to configure
     */
    public ModeSelectionDialog(JFrame owner, GraphicsDevice graphicDevice) {
        super(owner, true);

        setTitle("Screen resoultion selection");

        selectedModeForFullScreen = null;

        JPanel mainPanel = new JPanel();
        setContentPane(mainPanel);

        mainPanel.add(createFullScreenOrWindowedPanel(graphicDevice));

        mainPanel.add(createFullScreenDisplayModeSelectionPanel(graphicDevice));

        SwingUtils.createButton("Ok", CMD_OK, mainPanel, this, true);

        pack();
    }

    /**
     * Creates a panel allowing to select the display mode for full screen.
     * 
     * @param graphicDevice the graphic device
     * @return selection panel
     */
    private JPanel createFullScreenDisplayModeSelectionPanel(GraphicsDevice graphicDevice) {
        JPanel displayModePanel = new JPanel();

        displayModes = graphicDevice.getDisplayModes();
        String[] modesDescriptions = new String[displayModes.length];

        int displayModeIndex = 0;

        for (DisplayMode testedMode : displayModes) {
            LOG.debug(GraphicDeviceManager.describeDisplayMode(testedMode));

            modesDescriptions[displayModeIndex] = GraphicDeviceManager
                    .describeDisplayMode(testedMode);

            displayModeIndex++;
        }

        displayModeComboBox = new JComboBox<String>(modesDescriptions);

        displayModePanel.add(displayModeComboBox);

        displayModeComboBox.setEnabled(false);

        return displayModePanel;

    }

    /**
     * Creates a panel allowing to choose between windowed and full screen mode.
     * 
     * @param graphicDevice the graphic device
     * @return the panel to choose between full screen and windowed mode
     */
    private JPanel createFullScreenOrWindowedPanel(GraphicsDevice graphicDevice) {

        boolean supportsFullScreen = graphicDevice.isFullScreenSupported();

        String infoFullScreen;

        if (supportsFullScreen) {
            infoFullScreen = "The graphical device allows the full screen display.";

        } else {
            infoFullScreen = "The graphical device does not support the full screen display.";
            LOG.error("The graphical device " + graphicDevice.getIDstring()
                    + " does not support full screen display.");

        }

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

        radioPanel.add(new JLabel(infoFullScreen));

        radioPanel.add(new JLabel("Display mode :"));

        ButtonGroup bg = new ButtonGroup();
        rbWindowed = SwingUtils.createRadioButton("Windowed", CMD_WINDOWED, bg, this, radioPanel,
                true);
        rbFullScreen = SwingUtils.createRadioButton("Full screen", CMD_FULLSCREEN, bg, this,
                radioPanel, false);
        bg.setSelected(rbWindowed.getModel(), true);

        if (!supportsFullScreen) {
            rbFullScreen.setEnabled(false);
        }

        return radioPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String cmd = e.getActionCommand();

        if (cmd.equals(CMD_OK)) {

            int selectedModeIndex = displayModeComboBox.getSelectedIndex();

            selectedModeForFullScreen = displayModes[selectedModeIndex];

            setVisible(false);
        } else if (cmd.equals(CMD_FULLSCREEN)) {
            displayModeComboBox.setEnabled(true);

        } else if (cmd.equals(CMD_WINDOWED)) {
            displayModeComboBox.setEnabled(false);

        }
    }

    /** @return the selected display mode for full screen */
    public DisplayMode getSelectedMode() {
        return selectedModeForFullScreen;
    }

    /** @return true if full screen display is requested, false elsewhere */
    public boolean isFullScreenRequested() {
        return rbFullScreen.isSelected();
    }

}
