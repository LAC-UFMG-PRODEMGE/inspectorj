package br.ufmg.harmonia.inspectorj.util.movie;

/*
 * @(#)JpegImagesToMovie.java   1.3 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.ConfigureCompleteEvent;
import javax.media.Controller;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.EndOfMediaEvent;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.Time;
import javax.media.control.TrackControl;
import javax.media.datasink.DataSinkErrorEvent;
import javax.media.datasink.DataSinkEvent;
import javax.media.datasink.DataSinkListener;
import javax.media.datasink.EndOfStreamEvent;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PullBufferDataSource;
import javax.media.protocol.PullBufferStream;

/**
 * This program takes a list of JPEG image files and convert them into a
 * QuickTime movie.
 */
public class JpegImagesToMovie2 implements ControllerListener, DataSinkListener {

	static private Vector<String> getImageFilesPathsVector(
			String imagesFolderPath) {
		File imagesFolder = new File(imagesFolderPath);
		String[] imageFilesArray = imagesFolder.list();
		Vector<String> imageFilesPathsVector = new Vector<String>();
		for (String imageFileName : imageFilesArray) {
			if (!imageFileName.toLowerCase().endsWith("png"))
				continue;
			imageFilesPathsVector.add(imagesFolder.getAbsolutePath()
					+ File.separator + imageFileName);
		}
		return imageFilesPathsVector;
	}
	
	/**
     * Method to write out an AVI movie
     * @param width the width of the resulting movie
     * @param height the height of the resulting movie
     * @param frameRate the number of frames per second
     * @param inFiles string full path names of the frames
     * @param outML the Media Locator
     */
    public boolean doItAVI(int width, int height,
                           int frameRate,
                           Vector<String> inFiles,
                           MediaLocator outML) {
    	return doItAVI(width, height, frameRate, inFiles, outML,
                       FileTypeDescriptor.MSVIDEO);
    }
	
	 /**
     * Method to write out the movie for an AVI movie
     * @param width the width of the resulting movie
     * @param height the height of the resulting movie
     * @param frameRate the number of frames per second
     * @param inFiles string full path names of the frames
     * @param outML the Media Locator
     * @param type the VideoFormat type
     */
    public boolean doItAVI(int width, int height,
                           int frameRate, Vector<String> inFiles,
                           MediaLocator outML,
                           String type) {
        ImageDataSource ids = new ImageDataSource(width, height,
                frameRate, inFiles);

        Processor p;

        try {
            System.err.println("- create processor for the image datasource ...");
            p = Manager.createProcessor(ids);
        } catch (Exception e) {
            System.err.println("Yikes!  Cannot create a processor from the data source.");
            return false;
        }

        p.addControllerListener(this);

        // Put the Processor into configured state so we can set
        // some processing options on the processor.
        p.configure();
        if (!waitForState(p, p.Configured)) {
            System.err.println("Failed to configure the processor.");
            return false;
        }

        // Set the output content descriptor to QuickTime.
        p.setContentDescriptor(new ContentDescriptor(type));

        // Query for the processor for supported formats.
        // Then set it on the processor.
        TrackControl tcs[] = p.getTrackControls();
        Format format;
        TrackControl [] arrTrackControls = p.getTrackControls();
        for (int i = 0;  i < arrTrackControls.length;  i++) {
            format = arrTrackControls[i].getFormat();
            if (format  instanceof VideoFormat) {
                arrTrackControls[i].setFormat(new VideoFormat(VideoFormat.CINEPAK));
            }
            //else if ( format instanceof AudioFormat ){
            //arrTrackControls[i].setFormat(new AudioFormat(AudioFormat.GSM_MS));
            //}
        }

        // We are done with programming the processor.  Let's just
        // realize it.
        p.realize();
        if (!waitForState(p, p.Realized)) {
            System.err.println("Failed to realize the processor.");
            return false;
        }

        // Now, we'll need to create a DataSink.
        DataSink dsink;
        if ((dsink = createDataSink(p, outML)) == null) {
            System.err.println("Failed to create a DataSink for the given output MediaLocator: " + outML);
            return false;
        }

        dsink.addDataSinkListener(this);
        fileDone = false;

        System.err.println("start processing...");

        // OK, we can now start the actual transcoding.
        try {
            p.start();
            dsink.start();
        } catch (IOException e) {
            System.err.println("IO error during processing");
            return false;
        }

        // Wait for EndOfStream event.
        waitForFileDone();

        // Cleanup.
        try {
            dsink.close();
        } catch (Exception e) {}
        p.removeControllerListener(this);

        System.err.println("...done processing.");

        return true;
    }
	
	
	

	public boolean doIt(int width, int height, int frameRate,
			Vector<String> inFiles, MediaLocator outML) {
		ImageDataSource ids = new ImageDataSource(width, height, frameRate,
				inFiles);

		Processor p;

		try {
			System.err
					.println("- create processor for the image datasource ...");
			p = Manager.createProcessor(ids);
		} catch (Exception e) {
			System.err
					.println("Yikes!  Cannot create a processor from the data source.");
			return false;
		}

		p.addControllerListener(this);

		// Put the Processor into configured state so we can set
		// some processing options on the processor.
		p.configure();
		if (!waitForState(p, Processor.Configured)) {
			System.err.println("Failed to configure the processor.");
			return false;
		}

		// Set the output content descriptor to QuickTime.
		p.setContentDescriptor(new ContentDescriptor(
				FileTypeDescriptor.QUICKTIME));// FileTypeDescriptor.MSVIDEO

		// Query for the processor for supported formats.
		// Then set it on the processor.
		TrackControl tcs[] = p.getTrackControls();
		Format f[] = tcs[0].getSupportedFormats();
		if (f == null || f.length <= 0) {
			System.err.println("The mux does not support the input format: "
					+ tcs[0].getFormat());
			return false;
		}

		tcs[0].setFormat(f[0]);

		System.err.println("Setting the track format to: " + f[0]);

		// We are done with programming the processor. Let's just
		// realize it.
		p.realize();
		if (!waitForState(p, Controller.Realized)) {
			System.err.println("Failed to realize the processor.");
			return false;
		}

		// Now, we'll need to create a DataSink.
		DataSink dsink;
		if ((dsink = createDataSink(p, outML)) == null) {
			System.err
					.println("Failed to create a DataSink for the given output MediaLocator: "
							+ outML);
			return false;
		}

		dsink.addDataSinkListener(this);
		fileDone = false;

		System.err.println("start processing...");

		// OK, we can now start the actual transcoding.
		try {
			p.start();
			dsink.start();
		} catch (IOException e) {
			System.err.println("IO error during processing");
			return false;
		}

		// Wait for EndOfStream event.
		waitForFileDone();

		// Cleanup.
		try {
			dsink.close();
		} catch (Exception e) {
		}
		p.removeControllerListener(this);

		System.err.println("...done processing.");

		return true;
	}

	/**
	 * Create the DataSink.
	 */
	DataSink createDataSink(Processor p, MediaLocator outML) {

		DataSource ds;

		if ((ds = p.getDataOutput()) == null) {
			System.err
					.println("Something is really wrong: the processor does not have an output DataSource");
			return null;
		}

		DataSink dsink;

		try {
			System.err.println("- create DataSink for: " + outML);
			dsink = Manager.createDataSink(ds, outML);
			dsink.open();
		} catch (Exception e) {
			System.err.println("Cannot create the DataSink: " + e);
			return null;
		}

		return dsink;
	}

	Object waitSync = new Object();
	boolean stateTransitionOK = true;

	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 */
	boolean waitForState(Processor p, int state) {
		synchronized (waitSync) {
			try {
				while (p.getState() < state && stateTransitionOK)
					waitSync.wait();
			} catch (Exception e) {
			}
		}
		return stateTransitionOK;
	}

	/**
	 * Controller Listener.
	 */
	public void controllerUpdate(ControllerEvent evt) {

		if (evt instanceof ConfigureCompleteEvent
				|| evt instanceof RealizeCompleteEvent
				|| evt instanceof PrefetchCompleteEvent) {
			synchronized (waitSync) {
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		} else if (evt instanceof ResourceUnavailableEvent) {
			synchronized (waitSync) {
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		} else if (evt instanceof EndOfMediaEvent) {
			evt.getSourceController().stop();
			evt.getSourceController().close();
		}
	}

	Object waitFileSync = new Object();
	boolean fileDone = false;
	boolean fileSuccess = true;

	/**
	 * Block until file writing is done.
	 */
	boolean waitForFileDone() {
		synchronized (waitFileSync) {
			try {
				while (!fileDone)
					waitFileSync.wait();
			} catch (Exception e) {
			}
		}
		return fileSuccess;
	}

	/**
	 * Event handler for the file writer.
	 */
	public void dataSinkUpdate(DataSinkEvent evt) {

		if (evt instanceof EndOfStreamEvent) {
			synchronized (waitFileSync) {
				fileDone = true;
				waitFileSync.notifyAll();
			}
		} else if (evt instanceof DataSinkErrorEvent) {
			synchronized (waitFileSync) {
				fileDone = true;
				fileSuccess = false;
				waitFileSync.notifyAll();
			}
		}
	}

	public static void main(String args[]) {
		// changed this method a bit

		if (args.length == 0)
			prUsage();

		// Parse the arguments.
		int i = 0;
		int width = -1, height = -1, frameRate = -1;
		Vector<String> inputFiles = new Vector<String>();
		String rootDir = null;
		String outputURL = null;

		while (i < args.length) {

			if (args[i].equals("-w")) {
				i++;
				if (i >= args.length)
					prUsage();
				width = new Integer(args[i]).intValue();
			} else if (args[i].equals("-h")) {
				i++;
				if (i >= args.length)
					prUsage();
				height = new Integer(args[i]).intValue();
			} else if (args[i].equals("-f")) {
				i++;
				if (i >= args.length)
					prUsage();
				// new Integer(args[i]).intValue();
				frameRate = Integer.parseInt(args[i]);

			} else if (args[i].equals("-o")) {
				i++;
				if (i >= args.length)
					prUsage();
				outputURL = args[i];
			} else if (args[i].equals("-i")) {
				i++;
				if (i >= args.length)
					prUsage();
				rootDir = args[i];

			} else {
				System.out.println(".");
				prUsage();
			}
			i++;
		}

		if (rootDir == null) {
			System.out
					.println("Since no input (-i) forder provided, assuming this JAR is inside JPEGs folder.");
			rootDir = (new File(".")).getAbsolutePath();
		}
		inputFiles = getImageFilesPathsVector(rootDir);

		if (inputFiles.size() == 0)
			prUsage();
		if (outputURL == null) {
			outputURL = (new File(rootDir)).getAbsolutePath() + File.separator
					+ "pngs2movie.mov";
		}
		if (!outputURL.toLowerCase().startsWith("file:///")) {
			outputURL = "file:///" + outputURL;
		}

		// Check for output file extension.
		if (!outputURL.toLowerCase().endsWith(".mov")) {
			prUsage();
			outputURL += ".mov";
			System.out
					.println("outputURL should be ending with mov. Making this happen.\nNow outputURL is: "
							+ outputURL);
		}

		if (width < 0 || height < 0) {
			prUsage();
			System.out.println("Trying to guess movie size from first image");
			BufferedImage firstImageInFolder = getFirstImageInFolder(rootDir);
			width = firstImageInFolder.getWidth();
			height = firstImageInFolder.getHeight();
			System.out.println("width = " + width);
			System.out.println("height = " + height);
		}

		// Check the frame rate.
		if (frameRate < 1)
			frameRate = 30;

		// Generate the output media locators.
		MediaLocator oml;

		if ((oml = createMediaLocator(outputURL)) == null) {
			System.err.println("Cannot build media locator from: " + outputURL);
			System.exit(0);
		}

		JpegImagesToMovie2 imageToMovie = new JpegImagesToMovie2();
		imageToMovie.doIt(width, height, frameRate, inputFiles, oml);

		System.exit(0);
	}

	public static BufferedImage getFirstImageInFolder(String rootDir) {
		File rootFile = new File(rootDir);
		String[] list = (rootFile).list();
		BufferedImage bufferedImage = null;
		for (String filePath : list) {
			if (!filePath.toLowerCase().endsWith(".png")
					&& !filePath.toLowerCase().endsWith(".png")) {
				continue;
			}
			try {
				bufferedImage = ImageIO.read(new File(rootFile
						.getAbsoluteFile() + File.separator + filePath));
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return bufferedImage;
	}

	static void prUsage() {
		System.err
				.println("Usage: java JpegImagesToMovie [-w <width>] [-h <height>] [-f <frame rate>] [-o <output URL>] -i <input JPEG files dir Path>");
		// System.exit(-1);
	}

	/**
	 * Create a media locator from the given string.
	 */
	@SuppressWarnings("unused")
	public static MediaLocator createMediaLocator(String url) {

		MediaLocator ml;

		if (url.indexOf(":") > 0 && (ml = new MediaLocator(url)) != null)
			return ml;

		if (url.startsWith(File.separator)) {
			if ((ml = new MediaLocator("file:" + url)) != null)
				return ml;
		} else {
			String file = "file:" + System.getProperty("user.dir")
					+ File.separator + url;
			if ((ml = new MediaLocator(file)) != null)
				return ml;
		}
		return null;
	}

	// /////////////////////////////////////////////
	//
	// Inner methodsAndClasses.
	// /////////////////////////////////////////////

	/**
	 * A DataSource to read from a list of JPEG image files and turn that into a
	 * stream of JMF buffers. The DataSource is not seekable or positionable.
	 */
	class ImageDataSource extends PullBufferDataSource {

		ImageSourceStream streams[];

		ImageDataSource(int width, int height, int frameRate,
				Vector<String> images) {
			streams = new ImageSourceStream[1];
			streams[0] = new PngImageSourceStream(width, height, frameRate,
					images);
		}

		public void setLocator(MediaLocator source) {
		}

		public MediaLocator getLocator() {
			return null;
		}

		/**
		 * Content type is of RAW since we are sending buffers of video frames
		 * without a container format.
		 */
		public String getContentType() {
			return ContentDescriptor.RAW;
		}

		public void connect() {
		}

		public void disconnect() {
		}

		public void start() {
		}

		public void stop() {
		}

		/**
		 * Return the ImageSourceStreams.
		 */
		public PullBufferStream[] getStreams() {
			return streams;
		}

		/**
		 * We could have derived the duration from the number of frames and
		 * frame rate. But for the purpose of this program, it's not necessary.
		 */
		public Time getDuration() {
			return DURATION_UNKNOWN;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}

	/**
	 * The source stream to go along with ImageDataSource.
	 */
	class ImageSourceStream implements PullBufferStream {

		Vector<String> images;
		int width, height;
		VideoFormat format;

		int nextImage = 0; // index of the next image to be read.
		boolean ended = false;

		public ImageSourceStream(int width, int height, int frameRate,
				Vector<String> images) {
			this.width = width;
			this.height = height;
			this.images = images;

			format = new VideoFormat(VideoFormat.JPEG, new Dimension(width,
					height), Format.NOT_SPECIFIED, Format.byteArray,
					(float) frameRate);
		}

		/**
		 * We should never need to block assuming data are read from files.
		 */
		public boolean willReadBlock() {
			return false;
		}

		/**
		 * This is called from the Processor to read a frame worth of video
		 * data.
		 */
		public void read(Buffer buf) throws IOException {

			// Check if we've finished all the frames.
			if (nextImage >= images.size()) {
				// We are done. Set EndOfMedia.
				System.err.println("Done reading all images.");
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				ended = true;
				return;
			}

			String imageFile = (String) images.elementAt(nextImage);
			nextImage++;

			System.err.println("  - reading image file: " + imageFile);

			// Open a random access file for the next image.
			RandomAccessFile raFile;
			raFile = new RandomAccessFile(imageFile, "r");

			byte data[] = null;

			// Check the input buffer type & size.

			if (buf.getData() instanceof byte[])
				data = (byte[]) buf.getData();

			// Check to see the given buffer is big enough for the frame.
			if (data == null || data.length < raFile.length()) {
				data = new byte[(int) raFile.length()];
				buf.setData(data);
			}

			// Read the entire JPEG image from the file.
			raFile.readFully(data, 0, (int) raFile.length());

			System.err.println("    read " + raFile.length() + " bytes.");

			buf.setOffset(0);
			buf.setLength((int) raFile.length());
			buf.setFormat(format);
			buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);

			// Close the random access file.
			raFile.close();
		}

		/**
		 * Return the format of each video frame. That will be JPEG.
		 */
		public Format getFormat() {
			return format;
		}

		public ContentDescriptor getContentDescriptor() {
			return new ContentDescriptor(ContentDescriptor.RAW);
		}

		public long getContentLength() {
			return 0;
		}

		public boolean endOfStream() {
			return ended;
		}

		public Object[] getControls() {
			return new Object[0];
		}

		public Object getControl(String type) {
			return null;
		}
	}

	class PngImageSourceStream extends ImageSourceStream {

		public PngImageSourceStream(int width, int height, int frameRate,
				Vector<String> images) {
			super(width, height, frameRate, images);

			// configure the new format as RGB format
			format = new RGBFormat(new Dimension(width, height),
					Format.NOT_SPECIFIED, Format.byteArray, frameRate, 24, // 24
																			// bits
																			// per
																			// pixel
					1, 2, 3); // red, green and blue masks when data are in the
								// form of byte[]
		}

		public void read(Buffer buf) throws IOException {

			// Check if we've finished all the frames.
			if (nextImage >= images.size()) {
				// We are done. Set EndOfMedia.
				System.err.println("Done reading all images.");
				buf.setEOM(true);
				buf.setOffset(0);
				buf.setLength(0);
				ended = true;
				return;
			}

			String imageFile = (String) images.elementAt(nextImage);
			nextImage++;

			System.err.println("  - reading image file: " + imageFile);

			// read the PNG image
			BufferedImage image = ImageIO.read(new File(imageFile));
			Dimension size = format.getSize();

			// convert 32-bit RGBA to 24-bit RGB
			byte[] imageData = convertTo24Bit(image.getRaster().getPixels(0, 0,
					size.width, size.height, (int[]) null));
			buf.setData(imageData);

			System.err.println("    read " + imageData.length + " bytes.");

			buf.setOffset(0);
			buf.setLength(imageData.length);
			buf.setFormat(format);
			buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
		}

		private void convertIntByteToByte(int[] src, int srcIndex, byte[] out,
				int outIndex) {
			// Note: the int[] returned by bufferedImage.getRaster().getPixels()
			// is an int[]
			// where each int is the value for one color i.e. the first 4 ints
			// contain the RGBA values for the first pixel
			int r = src[srcIndex];
			int g = src[srcIndex + 1];
			int b = src[srcIndex + 2];

			out[outIndex] = (byte) (r & 0xFF);
			out[outIndex + 1] = (byte) (g & 0xFF);
			out[outIndex + 2] = (byte) (b & 0xFF);
		}

		private byte[] convertTo24Bit(int[] input) {
			int dataLength = input.length;
			byte[] convertedData = new byte[dataLength * 3 / 4];

			// for every 4 int values of the original array (RGBA) write 3
			// bytes (RGB) to the output array
			for (int i = 0, j = 0; i < dataLength; i += 4, j += 3) {
				convertIntByteToByte(input, i, convertedData, j);
			}
			return convertedData;
		}

	}
}