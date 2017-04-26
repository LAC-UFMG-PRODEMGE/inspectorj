package br.ufmg.harmonia.inspectorj.classesSoot;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

public class Generators {

	static enum Type {
		BARABASI(new BarabasiAlbertGenerator(),
				"1.0-video/generators/barabasi_albert/frame_", true, 125), DOROGOVTSEV(
				new DorogovtsevMendesGenerator(),
				"1.0-video/generators/dorogovtsev_mendes/frame_", true, 125), GRID(
				new GridGenerator(), "1.0-video/generators/grid/frame_", false, 15);
		Generator gen;
		String prefix;
		boolean layout;
		int size;

		Type(Generator gen, String prefix, boolean layout, int size) {
			this.gen = gen;
			this.prefix = prefix;
			this.layout = layout;
			this.size = size;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		for (Type type : Type.values())
			make(type);
	}

	public static void make(Type type) throws Exception {
		System.out.printf("\n* make %s\n", type.name());
		
		String css = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,150);size:2px;} "
				+ "node {size:15px;fill-color:#5f84c9;"
				+ "stroke-mode:plain;stroke-width:2px;stroke-color:#222222;} ";

		Generator gen = type.gen;

		FileSinkImages images = new FileSinkImages(type.prefix, OutputType.PNG,
				Resolutions.HD720, OutputPolicy.BY_STEP);

		DefaultGraph g = new DefaultGraph("g");

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);
		if (type.layout)
			images.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
		images.setLayoutStepAfterStabilization(1);

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		g.addSink(images);

		double step = 0;

		gen.addSink(g);
		gen.begin();
		for (int i = 0; i < type.size; i++) {
			gen.nextEvents();
			g.stepBegins(step++);
			for (int j = 0; j < 250 / type.size - 1; j++)
				g.stepBegins(step++);
		}

		while (step < 300)
			g.stepBegins(step++);

		images.end();
	}

}
