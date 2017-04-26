package br.ufmg.harmonia.inspectorj.classesSoot;

import org.graphstream.algorithm.generator.BananaTreeGenerator;
import org.graphstream.algorithm.generator.FlowerSnarkGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.IncompleteGridGenerator;
import org.graphstream.algorithm.generator.LobsterGenerator;
import org.graphstream.algorithm.generator.RandomEuclideanGenerator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;

public class OtherGenerators {

	static enum Type {
		Lobster(new LobsterGenerator(1),
				"1.0-video/generators/others/lobster/frame_", true, 125), FLOWER(
				new FlowerSnarkGenerator(),
				"1.0-video/generators/others/flower_snark/frame_", false, 5), IGRID(
				new IncompleteGridGenerator(), "1.0-video/generators/others/incomplete_grid/frame_", true, 15),
				BANANA(new BananaTreeGenerator(6),"1.0-video/generators/others/banana_tree/frame_", false, 15),
				RANDOM_EUCLIDEAN(new RandomEuclideanGenerator(),"1.0-video/generators/others/random_euclidean/frame_", false, 125),
				RANDOM(new RandomGenerator(),"1.0-video/generators/others/random/frame_", true, 150);
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
		//for (Type type : Type.values())
		//	make(type);
		make(Type.RANDOM_EUCLIDEAN);
	}

	public static void make(Type type) throws Exception {
		System.out.printf("\n* make %s\n", type.name());
		
		String css = "graph {fill-mode:none;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,150);size:1px;} "
				+ "node {size:8px;fill-color:#5f84c9;"
				+ "stroke-mode:plain;stroke-width:1px;stroke-color:#222222;} ";

		Generator gen = type.gen;

		FileSinkImages images = new FileSinkImages(type.prefix, OutputType.PNG,
				new FileSinkImages.CustomResolution(320,270), OutputPolicy.BY_STEP);

		DefaultGraph g = new DefaultGraph("g");

		images.setClearImageBeforeOutputEnabled(true);
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
