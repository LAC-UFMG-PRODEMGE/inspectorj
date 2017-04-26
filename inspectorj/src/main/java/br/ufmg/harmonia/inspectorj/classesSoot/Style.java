package br.ufmg.harmonia.inspectorj.classesSoot;
import java.util.Iterator;
import java.util.Random;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

public class Style {

	static final String SIZE_CSS = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
			+ "edge {fill-color:rgba(34,34,34,150);size-mode:dyn-size;} "
			+ "node {fill-color:#5f84c9;size-mode:dyn-size;stroke-mode:plain;stroke-width:2px;stroke-color:#222222;}";

	static final String COLOR_CSS = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
			+ "edge {fill-color:#222222,#a50c06,#f87d15,#fad15f,#157778,#71d5d5,#83fcf7,#3d845a,#111111;fill-mode:dyn-plain;} "
			+ "node {size:15px;fill-color:#222222,#a50c06,#f87d15,#fad15f,#157778,#71d5d5,#83fcf7,#3d845a,#111111;fill-mode:dyn-plain;"
			+ "stroke-mode:plain;stroke-width:2px;stroke-color:#333333;}";

	static final String SHAPE_CSS = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
			+ "edge {fill-color:#222222,#a50c06,#f87d15,#fad15f,#157778,#71d5d5,#83fcf7,#3d845a,#111111;fill-mode:dyn-plain;size-mode:dyn-size;} "
			+ "node {fill-mode:dyn-plain;fill-color:#222222,#a50c06,#f87d15,#fad15f,#157778,#71d5d5,#83fcf7,#3d845a,#111111;"
			+ "size-mode:dyn-size;size:15px;"
			+ "stroke-mode:plain;stroke-width:2px;stroke-color:#333333;}";

	static enum Type {
		SIZE(SHAPE_CSS, "1.0-video/stylesheet/size/frame_"), COLOR(COLOR_CSS,
				"1.0-video/stylesheet/color/frame_"), SHAPE(SHAPE_CSS,
				"1.0-video/stylesheet/shape/frame_");

		String css;
		String prefix;

		Type(String css, String prefix) {
			this.css = css;
			this.prefix = prefix;
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
		System.out.printf("* making %s\n", type.name());

		BaseGenerator gen = new BarabasiAlbertGenerator();

		FileSinkImages images = new FileSinkImages(type.prefix, OutputType.PNG,
				Resolutions.HD720, OutputPolicy.BY_STEP);

		DefaultGraph g = new DefaultGraph("g");

		images.setStyleSheet(type.css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);
		images.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
		images.setLayoutStepAfterStabilization(1);

		g.addAttribute("ui.stylesheet", type.css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		g.addSink(images);

		gen.addSink(g);
		gen.begin();
		for (int i = 0; i < 100; i++)
			gen.nextEvents();
		gen.end();
		gen.removeSink(g);

		images.stabilizeLayout(0.9);

		Random random = new Random();

		double max = -1;
		Node maxNode = null;
		for (Node n : g.getEachNode()) {
			if (n.getDegree() > max) {
				max = n.getDegree();
				maxNode = n;
			}
		}

		switch (type) {
		case SIZE:
			size(g, maxNode, random);
			break;
		case COLOR:
			color(g, maxNode, random);
			break;
		case SHAPE:
			shape(g, maxNode, random);
			break;
		}

		for (int i = 0; i < 50; i++)
			g.stepBegins(g.getStep() + 1);

		images.end();
	}

	protected static void size(Graph g, Node start, Random random)
			throws InterruptedException {
		Iterator<Node> it;
		Iterator<Edge> it2;

		it = start.getDepthFirstIterator();
		while (it.hasNext()) {
			Node n = it.next();
			n.addAttribute("ui.color", random.nextDouble());
		}

		it2 = g.getEdgeIterator();
		while (it2.hasNext()) {
			Edge e = it2.next();
			e.addAttribute("ui.color", random.nextDouble());
		}
		
		for (int i = 0; i < 50; i++)
			g.stepBegins(g.getStep() + 1);

		it = start.getDepthFirstIterator();
		while (it.hasNext()) {
			Node n = it.next();
			n.addAttribute("ui.size", random.nextDouble() * 25 + 5);
			g.stepBegins(g.getStep() + 1);
		}

		it2 = g.getEdgeIterator();
		while (it2.hasNext()) {
			Edge e = it2.next();
			e.addAttribute("ui.size", random.nextDouble() * 5 + 1);
			g.stepBegins(g.getStep() + 1);
		}
	}

	protected static void color(Graph g, Node start, Random random)
			throws InterruptedException {
		Iterator<Node> it = start.getDepthFirstIterator();

		for (int i = 0; i < 50; i++)
			g.stepBegins(g.getStep() + 1);

		while (it.hasNext()) {
			Node n = it.next();
			n.addAttribute("ui.color", random.nextDouble());
			g.stepBegins(g.getStep() + 1);
		}

		Iterator<Edge> it2 = g.getEdgeIterator();
		while (it2.hasNext()) {
			Edge e = it2.next();
			e.addAttribute("ui.color", random.nextDouble());
			g.stepBegins(g.getStep() + 1);
		}
	}

	protected static void shape(Graph g, Node start, Random random)
			throws InterruptedException {
		Iterator<Node> it;
		Iterator<Edge> it2;

		it = start.getDepthFirstIterator();
		while (it.hasNext()) {
			Node n = it.next();
			n.addAttribute("ui.color", random.nextDouble());
			n.addAttribute("ui.size", random.nextDouble() * 25 + 5);
		}

		it2 = g.getEdgeIterator();
		while (it2.hasNext()) {
			Edge e = it2.next();
			e.addAttribute("ui.color", random.nextDouble());
			e.addAttribute("ui.size", random.nextDouble() * 5 + 1);
		}

		for (int i = 0; i < 50; i++)
			g.stepBegins(g.getStep() + 1);

		String[] shapes = { "circle", "box", "rounded-box", "diamond", "cross" };
		String[] shapes2 = { "line", "angle", "cubic-curve", "blob" };

		it = start.getDepthFirstIterator();
		while (it.hasNext()) {
			Node n = it.next();
			n.addAttribute("ui.style",
					"shape: " + shapes[random.nextInt(shapes.length)] + ";");
			g.stepBegins(g.getStep() + 1);
		}

		it2 = g.getEdgeIterator();
		while (it2.hasNext()) {
			Edge e = it2.next();
			e.addAttribute("ui.style",
					"shape: " + shapes2[random.nextInt(shapes2.length)] + ";");
			g.stepBegins(g.getStep() + 1);
		}
	}
}
