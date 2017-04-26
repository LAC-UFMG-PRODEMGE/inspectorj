package br.ufmg.harmonia.inspectorj.classesSoot;

import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

public class Boids {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String css = "graph {fill-mode:none;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,100);} "
				+ "node {fill-color:#88a8e2;size-mode:dyn-size;stroke-mode:plain;stroke-width:2px;stroke-color:#333333;}";

		FileSourceDGS dgs = new FileSourceDGS();

		FileSinkImages images = new FileSinkImages(
				"1.0-video/intro/graph/frame_", OutputType.PNG,
				Resolutions.HD720, OutputPolicy.NONE);

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);
		images.setClearImageBeforeOutputEnabled(true);

		DefaultGraph g = new DefaultGraph("g");
		g.addSink(images);

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		dgs.addSink(g);
		// Viewer v = g.display(false);

		dgs.begin("1.0-video/BoidsMovie.dgs");

		for (int i = 0; i < 150; i++)
			dgs.nextStep();

		images.setOutputPolicy(OutputPolicy.BY_STEP);

		while (dgs.nextStep())
			;
		dgs.end();
	}

}
