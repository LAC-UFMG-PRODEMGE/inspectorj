package br.ufmg.harmonia.inspectorj.classesSoot;

import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

public class IMDB {

	static final double atanp10 = Math.atan(10);
	static final double atanm10 = Math.atan(-10);

	public static double getArctanVariation(double min, double x, double max) {
		double d = -10 + 20 * (x - min) / (max - min);
		d = Math.atan(d);
		d = (d - atanm10) / (atanp10 - atanm10);
		return min + d * (max - min);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String css = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:rgba(34,34,34,150);} "
				+ "edge .tracked {size:5px;fill-color:#89a9e3;} "
				+ "node {text-padding:3px;text-background-mode:rounded-box;text-background-color:rgba(220,220,220,100);text-font:Ubuntu;text-alignment:at-right;text-offset:2px,0px;text-visibility-mode: zoom-range;text-visibility: 0,0.3;fill-color:#d9bfa4;size-mode:dyn-size;stroke-mode:plain;stroke-width:2px;stroke-color:#222222;} "
				+ "node .tracked {size:40px;fill-color:#89a9e3;}";

		FileSourceDGS dgs = new FileSourceDGS();

		FileSinkImages images = new FileSinkImages(
				"1.0-video/tracker/imdb/frame_", OutputType.PNG,
				Resolutions.HD720, OutputPolicy.BY_STEP);

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);

		DefaultGraph g = new DefaultGraph("g");
		g.addSink(images);

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		dgs.addSink(g);
		dgs.readAll("imdb-spanning_tree.dgs");
		dgs.removeSink(g);

		AStar astar = new AStar();
		astar.init(g);
		astar.compute("David Duchovny", "Richard Dean Anderson");

		// Viewer v = g.display(false);
		double step = 1;

		Node n = g.getNode("David Duchovny");

		double[] xy = { (Double) (n.getArray("xyz")[0]),
				(Double) (n.getArray("xyz")[1]) };
		double zoom = 1;

		// View view = v.getDefaultView();
		images.setViewCenter(xy[0], xy[1]);

		for (int i = 0; i < 50; i++) {
			g.stepBegins(step++);
			// Thread.sleep(25);
		}

		while (zoom > 0.03) {
			zoom -= 0.005;
			images.setViewPercent(getArctanVariation(0.03, zoom, 1));

			g.stepBegins(step++);
			// Thread.sleep(25);
		}
		
		n.addAttribute("ui.class", "tracked");

		for (int i = 0; i < 50; i++) {
			g.stepBegins(step++);
			// Thread.sleep(25);
		}
		// Thread.sleep(1000);

		while (zoom < 0.1) {
			zoom += 0.005;
			images.setViewPercent(getArctanVariation(0.03, zoom, 0.1));

			g.stepBegins(step++);
			// Thread.sleep(25);
		}

		for (Node p : astar.getShortestPath().getNodePath()) {
			if (p == n)
				continue;

			p.addAttribute("ui.class", "tracked");

			double[] xyTo = { (Double) p.getArray("xyz")[0],
					(Double) p.getArray("xyz")[1] };

			double d = 0;

			n.getEdgeBetween(p.getId()).addAttribute("ui.class", "tracked");

			while (d < 1) {
				double t = getArctanVariation(0, d, 1);
				images.setViewCenter(xy[0] + (xyTo[0] - xy[0]) * t, xy[1]
						+ (xyTo[1] - xy[1]) * t);
				d += 0.01;
				g.stepBegins(step++);
				// Thread.sleep(25);
			}

			n.getEdgeBetween(p.getId()).removeAttribute("ui.class");

			n.removeAttribute("ui.class");

			n = p;
			xy = xyTo;
		}

		while (zoom > 0.03) {
			zoom -= 0.005;
			images.setViewPercent(getArctanVariation(0.03, zoom, 0.1));

			g.stepBegins(step++);
			// Thread.sleep(25);
		}

		for (int i = 0; i < 100; i++) {
			g.stepBegins(step++);
			// Thread.sleep(25);
		}
	}

}
