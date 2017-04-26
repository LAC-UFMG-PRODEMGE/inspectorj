package br.ufmg.harmonia.inspectorj.classesSoot;

import java.util.HashSet;
import java.util.Iterator;

import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.BetweennessCentrality;
import org.graphstream.algorithm.Kruskal;
import org.graphstream.algorithm.Toolkit;
import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.DorogovtsevMendesGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

public class Algorithms {

	public static void main(String... args) throws Exception {
		makeBetweenness();
	}

	public static void makeAStar() throws Exception {
		String css = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,150);} "
				+ "node {size:15px;fill-color:#5f84c9;stroke-mode:plain;stroke-width:2px;stroke-color:#333333;} "
				+ "node .inpath {size:20px;fill-color:#ef9200;} "
				+ "node .extpath {size:30px;fill-color:#ef3e00;} "
				+ "node .notinpath {size:10px;fill-color:gray;} "
				+ "edge .notintree {size:1px;fill-color:gray;} "
				+ "edge .intree {size:3px;fill-color:#ef9200;}";

		BaseGenerator gen = new DorogovtsevMendesGenerator();
		gen.addEdgeAttribute("weight");
		gen.setEdgeAttributesRange(1, 100);

		FileSinkImages images = new FileSinkImages(
				"1.0-video/algorithms/a_star/frame_", OutputType.PNG,
				Resolutions.HD720, OutputPolicy.BY_STEP);

		DefaultGraph g = new DefaultGraph("g");

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);
		images.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
		images.setLayoutStepAfterStabilization(1);

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		g.addSink(images);

		gen.addSink(g);
		gen.begin();
		for (int i = 0; i < 150; i++)
			gen.nextEvents();
		gen.end();
		gen.removeSink(g);

		images.stabilizeLayout(0.9);

		AStar astar = new AStar();
		String n1, n2;

		do {
			n1 = Toolkit.randomNode(g).getId();

			do {
				n2 = Toolkit.randomNode(g).getId();
			} while (n2.equals(n1));

			astar.init(g);
			astar.compute(n1, n2);
		} while (astar.getShortestPath().size() < 11);

		double step = 0;

		for (int i = 0; i < 50; i++)
			g.stepBegins(step++);

		for (Edge e : g.getEachEdge())
			e.addAttribute("ui.class", "notintree");

		for (Node n : g.getEachNode())
			n.addAttribute("ui.class", "notinpath");

		g.getNode(n1).setAttribute("ui.class", "extpath");
		g.getNode(n2).setAttribute("ui.class", "extpath");

		for (Edge e : astar.getShortestPath().getEdgePath()) {
			if (!e.getNode0().getId().matches(n1 + "|" + n2))
				e.getNode0().setAttribute("ui.class", "inpath");
			g.stepBegins(step++);
			e.setAttribute("ui.class", "intree");
			g.stepBegins(step++);
			if (!e.getNode1().getId().matches(n1 + "|" + n2))
				e.getNode1().setAttribute("ui.class", "inpath");
			g.stepBegins(step++);
		}

		while (step < 125)
			g.stepBegins(step++);
	}

	public static void makeKruskal() throws Exception {
		String css = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,150);} "
				+ "node {size:15px;fill-color:#ef9200;stroke-mode:plain;stroke-width:2px;stroke-color:#333333;} "
				+ "edge .notintree {size:1px;fill-color:gray;} "
				+ "edge .intree {size:3px;fill-color:#ef9200;}";

		BaseGenerator gen = new DorogovtsevMendesGenerator();
		gen.addEdgeAttribute("weight");
		gen.setEdgeAttributesRange(1, 100);

		FileSinkImages images = new FileSinkImages(
				"1.0-video/algorithms/kruskal/frame_", OutputType.PNG,
				Resolutions.HD720, OutputPolicy.BY_STEP);

		DefaultGraph g = new DefaultGraph("g");

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);
		images.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
		images.setLayoutStepAfterStabilization(1);

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		g.addSink(images);

		gen.addSink(g);
		gen.begin();
		for (int i = 0; i < 150; i++)
			gen.nextEvents();
		gen.end();
		gen.removeSink(g);

		images.stabilizeLayout(0.9);

		double step = 0;

		Kruskal kruskal = new Kruskal("kruskal", "intree", "notintree");
		kruskal.init(g);
		kruskal.compute();

		Node maxNode = null;

		for (Node n : g.getEachNode()) {
			if (maxNode == null || n.getDegree() > maxNode.getDegree())
				maxNode = n;
		}

		for (int i = 0; i < 50; i++)
			g.stepBegins(step++);

		for (Edge e : g.getEachEdge())
			e.addAttribute("ui.class", "notintree");

		Iterator<Node> it = maxNode.getDepthFirstIterator();
		HashSet<Edge> done = new HashSet<Edge>();
		int write = 0;

		while (it.hasNext()) {
			Node n = it.next();
			for (Edge e : n.getEachEdge()) {
				if (done.contains(e))
					continue;

				e.setAttribute("ui.class", e.getAttribute("kruskal"));
				if ((write % 4) == 0
						&& e.getAttribute("kruskal").equals("intree")) {
					g.stepBegins(step++);
				}
				write++;
				done.add(e);
			}
		}

		done.clear();

		while (step < 125)
			g.stepBegins(step++);
	}

	public static void makeBetweenness() throws Exception {
		String css = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,150);} "
				+ "node {text-padding:3px;text-background-mode:rounded-box;text-background-color:rgba(220,220,220,100);text-font:Ubuntu;text-alignment:at-right;text-offset:2px,0px;"
				+ "size:20px;fill-color:#5f84c9,#ffea00;fill-mode:dyn-plain;size-mode:dyn-size;stroke-mode:plain;stroke-width:2px;stroke-color:#222222;} ";

		BaseGenerator gen = new BarabasiAlbertGenerator();
		// gen.addNodeAttribute("ui.size");
		// gen.setNodeAttributesRange(10, 30);
		gen.addEdgeAttribute("weight");
		gen.setEdgeAttributesRange(1, 100);

		FileSinkImages images = new FileSinkImages(
				"1.0-video/algorithms/betweenness_centrality/frame_",
				OutputType.PNG, Resolutions.HD720, OutputPolicy.BY_STEP);

		DefaultGraph g = new DefaultGraph("g");

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);
		images.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
		images.setLayoutStepAfterStabilization(1);

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		g.addSink(images);

		gen.addSink(g);
		gen.begin();
		for (int i = 0; i < 50; i++) {
			gen.nextEvents();
		}

		images.stabilizeLayout(0.9);

		double step = 0;
		
		for (int i = 0; i < 50; i++)
			g.stepBegins(step++);
		
		BetweennessCentrality bc = new BetweennessCentrality("c");
		bc.init(g);
		bc.compute();

		double max = -1;
		double min = Double.MAX_VALUE;

		for (Node n : g.getEachNode()) {
			max = Math.max(max, n.getNumber("c"));
			min = Math.min(min, n.getNumber("c"));
		}

		min = Math.log(min);
		min = Math.max(0, min);
		max = Math.log(max);

		for (Node n : g.getEachNode()) {
			double c = Math.max(0,Math.log(n.getNumber("c")));
			n.addAttribute("ui.color", (c - min) / (max - min));
			n.addAttribute("ui.size", (c - min) / (max - min) * 25 + 10);
			n.addAttribute("label", String.format("  %.1f", n.getNumber("c")));
			g.stepBegins(step++);
		}

		while (step < 150)
			g.stepBegins(step++);
	}
}
