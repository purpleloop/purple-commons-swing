package io.github.purpleloop.commons.swing.image;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.exception.PurpleException;

/** Utilities for images. */
public final class ImageUtils {

	/** Supported image file formats. */
	public enum FileFormat {

		/** BMP file format. */
		BMP,

		/** GIF file format. */
		GIF,

		/** Jpeg file format. */
		JPG,

		/** PNG file format. */
		PNG;
	}

	/** Class logger. */
	private static final Log LOG = LogFactory.getLog(ImageUtils.class);

	/** Private constructor. */
	private ImageUtils() {
	}

	/**
	 * Loads an image from a local file given by it's path.
	 * 
	 * @param imageFilePath path of the local image file to load
	 * @return image (BufferedImage instance)
	 * @throws PurpleException in case of problem
	 */
	public static BufferedImage loadImageFromFile(Path imageFilePath) throws PurpleException {
		return loadImageFromFile(imageFilePath.toFile());
	}

	/**
	 * Loads an image from a local file.
	 * 
	 * @param imageFileName name of the local image file to load
	 * @return image (BufferedImage instance)
	 * @throws PurpleException in case of problem
	 */
	public static BufferedImage loadImageFromFile(String imageFileName) throws PurpleException {
		return loadImageFromFile(new File(imageFileName));
	}

	/**
	 * Loads an image from a local file.
	 * 
	 * @param imageFile the local image file to load
	 * @return image (BufferedImage instance)
	 * @throws PurpleException in case of problem
	 */
	public static BufferedImage loadImageFromFile(File imageFile) throws PurpleException {

		BufferedImage bufferedImageResult = null;
		PurpleException purpleException = null;
		FileImageInputStream fileImageInputStream = null;

		String absolutePath = imageFile.getAbsolutePath();

		if (!imageFile.canRead()) {
			throw new PurpleException("Image file " + absolutePath + " is not readable.");
		}

		LOG.info("Loading image file from " + absolutePath);

		// We can'y use the auto-closable feature on the try/catch.
		// The 'read' method can closes the stream and this leads auto-close to
		// rise an IOException (closed).
		try {

			fileImageInputStream = new FileImageInputStream(imageFile);

			// See the warning in the java-doc about closing behavior
			bufferedImageResult = ImageIO.read(fileImageInputStream);

			// Tests if a close operation is required (loading has failed)
			if (bufferedImageResult != null) {
				fileImageInputStream = null;
			}

		} catch (IOException ex) {
			LOG.error("Error while reading image file " + absolutePath, ex);
			purpleException = new PurpleException("Error while reading image file :" + absolutePath, ex);
		} finally {

			// Explicit close
			if (fileImageInputStream != null) {
				try {
					fileImageInputStream.close();
				} catch (IOException e) {
					if (purpleException == null) {
						purpleException = new PurpleException("Unable to close the image file", e);
					} else {
						LOG.error("Subsequent close error on image file while throwing another exception", e);
					}
				}
				fileImageInputStream = null;
			}

			if (purpleException != null) {
				throw purpleException;
			}
		}

		return bufferedImageResult;

	}

	/**
	 * Saves a rendered image to a local file.
	 * 
	 * @param imageToSave     the rendered image to save (typically a BufferedImage)
	 * @param imageFilePath   path of the destination file where to save the image
	 * @param imageFileFormat file format used to save the image
	 * @throws PurpleException in case of problem
	 */
	public static void saveImageToFile(RenderedImage imageToSave, Path imageFilePath, FileFormat imageFileFormat)
			throws PurpleException {
		saveImageToFile(imageToSave, imageFilePath.toFile(), imageFileFormat);
	}

	/**
	 * Saves a rendered image to a local file.
	 * 
	 * @param imageToSave     the rendered image to save (typically a BufferedImage)
	 * @param imageFileName name of the destination file where to save the image
	 * @param imageFileFormat file format used to save the image
	 * @throws PurpleException in case of problem
	 */
	public static void saveImageToFile(RenderedImage imageToSave, String imageFileName, FileFormat imageFileFormat)
			throws PurpleException {
		saveImageToFile(imageToSave, new File(imageFileName), imageFileFormat);
	}

	/**
	 * Saves a rendered image to a local file.
	 * 
	 * @param imageToSave     the rendered image to save (typically a BufferedImage)
	 * @param fileToWrite     the destination file where to save the image
	 * @param imageFileFormat file format used to save the image
	 * @throws PurpleException in case of problem
	 */
	public static void saveImageToFile(RenderedImage imageToSave, File fileToWrite, FileFormat imageFileFormat)
			throws PurpleException {

		String absolutePath = fileToWrite.getAbsolutePath();

		try (FileImageOutputStream fileImageOutputStream = new FileImageOutputStream(fileToWrite);) {

			LOG.info("Saving image to " + absolutePath);

			ImageIO.write(imageToSave, imageFileFormat.name(), fileImageOutputStream);
		} catch (IOException e) {
			throw new PurpleException("Error writing file " + absolutePath, e);
		}
	}

}
