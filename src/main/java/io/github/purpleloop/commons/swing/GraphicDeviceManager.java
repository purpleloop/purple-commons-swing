package io.github.purpleloop.commons.swing;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A manager for the graphic devices and the screen mode selection.
 * 
 * <p>
 * Example of usage, for instance before main frame creation :
 * </p>
 * 
 * <pre>
 * GraphicDeviceManager graphicDeviceManager = GraphicDeviceManager.getInstance();
 * graphicDeviceManager.configureScreenMode();
 * </pre>
 * 
 * <p>
 * Then once the main frame is created :
 * </p>
 * 
 * <pre>
 * graphicDeviceManager.initDisplayUsingFrame(frame, true);
 * </pre>
 * 
 * <p>
 * and when finished, for instance in a WindowClosing event :
 * </p>
 * 
 * <pre>
 * graphicDeviceManager.restoreDisplay();
 * </pre>
 */
public final class GraphicDeviceManager {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(GraphicDeviceManager.class);

    /** Initial mode. */
    private static DisplayMode initialDisplayMode;

    /** Full screen display mode. */
    private static DisplayMode fullScreenDisplayMode;

    /** Singleton object. */
    private static GraphicDeviceManager singleton;

    /** The graphic device. */
    private GraphicsDevice graphicDevice;

    /** Is full screen active ? */
    private boolean fullScreenActive = false;

    /** Private constructor. */
    private GraphicDeviceManager() {
    }

    /** @return provides an instance of graphic device manager. */
    public static GraphicDeviceManager getInstance() {

        if (singleton == null) {
            singleton = new GraphicDeviceManager();
        }

        return singleton;
    }

    /** Configures the screen mode of the local graphic device interactively. */
    public void configureScreenMode() {

        LOG.debug("Acquiring the local graphic device");

        GraphicsEnvironment graphEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        graphicDevice = graphEnv.getDefaultScreenDevice();

        logDescribeGraphicDevice();

        initialDisplayMode = graphicDevice.getDisplayMode();
        LOG.debug("Initial display mode : " + describeDisplayMode(initialDisplayMode));

        ModeSelectionDialog modeSelectionDialog = new ModeSelectionDialog(null, graphicDevice);

        modeSelectionDialog.setVisible(true);

        if (modeSelectionDialog.isFullScreenRequested()) {
            DisplayMode selectedMode = modeSelectionDialog.getSelectedMode();

            if (selectedMode != null) {

                fullScreenDisplayMode = selectedMode;

                LOG.debug("The following display mode will be used for full screen display : "
                        + describeDisplayMode(fullScreenDisplayMode));
            }
        }

    }

    /** Describes the graphic device on logs. */
    public void logDescribeGraphicDevice() {
        if (LOG.isDebugEnabled()) {

            LOG.debug("The default graphic device is " + graphicDevice.getIDstring());
            LOG.debug("Is full screen supported ? " + graphicDevice.isFullScreenSupported());

            switch (graphicDevice.getType()) {
            case GraphicsDevice.TYPE_RASTER_SCREEN:
                LOG.debug("This is a display screen with raster (pixels) rendering");
                break;
            case GraphicsDevice.TYPE_PRINTER:
                LOG.debug("This is a printer");
                break;
            case GraphicsDevice.TYPE_IMAGE_BUFFER:
                LOG.debug("This is an image buffer");
                break;
            default:
                LOG.debug("This is of an unknown type");
                break;
            }

        }
    }

    /**
     * Describes the given graphical display mode.
     * 
     * @param displayMode the display mode to describe
     * @return description of the display mode
     */
    public static String describeDisplayMode(DisplayMode displayMode) {

        StringBuffer displayModeDescriptionStringBuffer = new StringBuffer();
        displayModeDescriptionStringBuffer.append(displayMode.getWidth());
        displayModeDescriptionStringBuffer.append(" x ");
        displayModeDescriptionStringBuffer.append(displayMode.getHeight());
        displayModeDescriptionStringBuffer.append(",");
        displayModeDescriptionStringBuffer.append(displayMode.getBitDepth());
        displayModeDescriptionStringBuffer.append("bpp");
        displayModeDescriptionStringBuffer.append(",");
        displayModeDescriptionStringBuffer.append(displayMode.getRefreshRate());
        displayModeDescriptionStringBuffer.append("Hz ");
        return displayModeDescriptionStringBuffer.toString();
    }

    /**
     * Initializes the display adapting the given JFrame
     * 
     * @param frame the JFrame to adapt
     * @param stopIfFailed Should we do a low level JVM stop in case of error ?
     */
    public void initDisplayUsingFrame(JFrame frame, boolean stopIfFailed) {
        if (isFullScreenConfigured()) {
            initFullScreen(frame, stopIfFailed);
        } else {
            frame.pack();
            frame.setResizable(false);
        }
    }

    /**
     * Switch to full screen, adapting the given frame.
     * 
     * If full screen activation fails, this
     * 
     * @param frame the JFrame to adapt
     * @param stopIfFailed Should we do a low level JVM stop in case of error ?
     * @return full screen activity
     */
    private boolean initFullScreen(JFrame frame, boolean stopIfFailed) {

        try {
            LOG.debug("Activating full screen mode.");
            frame.setUndecorated(true);
            graphicDevice.setFullScreenWindow(frame);
            graphicDevice.setDisplayMode(fullScreenDisplayMode);

            this.fullScreenActive = true;

        } catch (Exception e) {
            LOG.error("Full screen activation failed with an exception - Return to normal mode", e);
            graphicDevice.setDisplayMode(initialDisplayMode);
            graphicDevice.setFullScreenWindow(null);

            if (stopIfFailed) {
                LOG.info("Safety stop", e);
                System.exit(-1);
            }

            this.fullScreenActive = false;
        }

        return this.fullScreenActive;
    }

    /**
     * Terminates the full screen mode and returns in normal mode.
     */
    private void quitFullScreen() {

        try {
            LOG.debug("Exiting full screen - Back to initial display mode "
                    + describeDisplayMode(initialDisplayMode));
            graphicDevice.setDisplayMode(initialDisplayMode);
            graphicDevice.setFullScreenWindow(null);

        } catch (Exception e) {
            LOG.error(
                    "An exception occured while exiting from full screen - exiting the JVM by safety",
                    e);
            System.exit(-1);
        }
    }

    /**
     * @return true if the graphic device manager is configured to run running
     *         in full screen, false elsewhere
     */
    protected boolean isFullScreenConfigured() {
        return fullScreenDisplayMode != null;
    }

    /** Exits full screen, if necessary. */
    public void restoreDisplay() {
        if (this.fullScreenActive) {
            quitFullScreen();
        }
    }

}
