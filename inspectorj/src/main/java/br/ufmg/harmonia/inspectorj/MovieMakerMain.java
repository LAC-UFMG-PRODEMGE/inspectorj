package br.ufmg.harmonia.inspectorj;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.media.MediaLocator;

import br.ufmg.harmonia.inspectorj.util.movie.JpegImagesToMovie;
import br.ufmg.harmonia.inspectorj.util.movie.JpegImagesToMovie2;

public class MovieMakerMain {

	public static void main(String[] args) {
		JpegImagesToMovie2 imageToMovie = new JpegImagesToMovie2();
		
		
		File directory = new File("C:/Prodemge/workspaces/workspace-kepler/inspectorj/tracking/images");
		File[] listFiles = directory.listFiles();
		Vector<String> inFiles = new Vector<String>();
		String firstFile = null;
		for (File file : listFiles) {
			if(file.isFile() && file.getName().toUpperCase().endsWith("PNG")){
				inFiles.add(file.getAbsolutePath());
				if(firstFile == null){
					firstFile = file.getAbsolutePath();
				}
				
			}
		}
		
		try {
			BufferedImage bufferedImage = ImageIO.read(new File(firstFile));
			
			int width = bufferedImage.getWidth();;
			int height = bufferedImage.getHeight();
			int frameRate = 30;
			
			
			MediaLocator oml;
			
			if ((oml = JpegImagesToMovie.createMediaLocator("movieGlauco.mov")) == null) {
				System.err.println("Cannot build media locator ");
				System.exit(0);
			}
			imageToMovie.doIt(width, height, frameRate, inFiles, oml);
			
			
			//Runtime.getRuntime().exec("C:/Program Files (x86)/QuickTime/QuickTimePlayer.exe C:/Prodemge/Federal/workspace/inspectorj/movieGlauco.mov");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		System.exit(0);
	}
}
