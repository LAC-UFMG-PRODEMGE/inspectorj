package br.ufmg.harmonia.inspectorj.classesSoot;

import java.util.HashSet;

import org.graphstream.algorithm.randomWalk.Entity;
import org.graphstream.algorithm.randomWalk.RandomWalk;
import org.graphstream.algorithm.randomWalk.TabuEntity;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.stream.file.FileSinkImages.OutputPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Quality;
import org.graphstream.stream.file.FileSinkImages.RendererType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class LeHavre {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// System.setErr(new java.io.PrintStream("err"));
		System.setProperty("gs.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		String css = "graph {fill-mode:gradient-radial;fill-color:#FFFFFF,#EEEEEE;} "
				+ "edge {fill-color:green,yellow,orange,red;fill-mode:dyn-plain;shape:polyline;text-padding:1px;text-background-mode:rounded-box;text-background-color:rgba(220,220,220,100);text-size:10;text-font:Ubuntu;text-alignment:at-right;text-offset:2px,0px;text-visibility-mode:zoom-range;text-visibility: 0,0.2;} "
				+ "edge .tracked {fill-color:#5f84c9;size:3px;z-index:10;} "
				+ "node {fill-color:green,yellow,orange,red;fill-mode:dyn-plain;"
				+ "text-padding:3px;text-background-mode:rounded-box;text-background-color:rgba(220,220,220,100);text-font:Ubuntu;text-alignment:at-right;text-offset:2px,0px;text-visibility-mode:hidden;text-visibility: 0,0.3;size:4px;stroke-mode:plain;stroke-width:1px;stroke-color:#222222;} "
				+ "sprite {size:3px;fill-color:rgba(22,22,22,120);}";
		FileSourceDGS dgs = new FileSourceDGS();

		FileSinkImages images = new FileSinkImages(
				"1.0-video/gis/le_havre/frame_", OutputType.PNG,
				Resolutions.HD720, OutputPolicy.NONE);

		images.setStyleSheet(css);
		images.setQuality(Quality.HIGH);
		images.setRenderer(RendererType.SCALA);

		DefaultGraph g = new DefaultGraph("g");
		SpriteManager manager = new SpriteManager(g);
		RandomWalk rwalk = new LHRandomWalk(manager);

		g.addSink(images);

		double step = 0;

		g.addAttribute("ui.stylesheet", css);
		g.addAttribute("ui.quality");
		g.addAttribute("ui.antialias");

		dgs.addSink(g);
		dgs.readAll("1.0-video/dgs/LeHavre.dgs");
		dgs.removeSink(g);

		HashSet<String> labels = new HashSet<String>();

		for (Edge e : g.getEachEdge()) {
			if (e.hasAttribute("name")) {
				String label = e.getAttribute("name").toString().toLowerCase();

				if (!labels.contains(label)) {
					e.setAttribute("label", label);
					labels.add(label);
				}
			}
		}

		labels.clear();

		double xmin, xmax, ymin, ymax;
		xmin = ymin = Double.MAX_VALUE;
		xmax = ymax = Double.MIN_VALUE;

		for (Node n : g.getEachNode()) {

			double[] xy = { (Double) (n.getArray("xyz")[0]),
					(Double) (n.getArray("xyz")[1]) };

			xmin = Math.min(xmin, xy[0]);
			ymin = Math.min(ymin, xy[1]);
			xmax = Math.max(xmax, xy[0]);
			ymax = Math.max(ymax, xy[1]);
		}

		// AStar astar = new AStar();
		// astar.init(g);
		// astar.compute("LH.9921_0", "LH.12055_0");

		// LH.9921_0
		// LH.5953_0
		// LH.15292_0
		// LH.9533_0

		Node[] path = { g.getNode("LH.9921_0"), g.getNode("LH.5953_0"),
				g.getNode("LH.15292_0"), g.getNode("LH.9533_0"),
				g.getNode("LH.9623__1"), g.getNode("LH.170_0") };
		double[][] coords = new double[path.length][2];
		double[] lengths = new double[path.length - 1];

		double fullLength = 0;

		for (int i = 0; i < path.length - 1; i++) {

			double[] xy1 = { (Double) (path[i].getArray("xyz")[0]),
					(Double) (path[i].getArray("xyz")[1]) };
			double[] xy2 = { (Double) (path[i + 1].getArray("xyz")[0]),
					(Double) (path[i + 1].getArray("xyz")[1]) };
			coords[i][0] = xy1[0];
			coords[i][1] = xy1[1];
			coords[i + 1][0] = xy2[0];
			coords[i + 1][1] = xy2[1];

			lengths[i] = Math.sqrt(Math.pow(xy1[0] - xy2[0], 2)
					+ Math.pow(xy1[1] - xy2[1], 2));

			fullLength += lengths[i];
		}

		images.setViewCenter((xmax + xmin) / 2.0, (ymax + ymin) / 2.0);

		MoveCenterView move = new MoveCenterView(images,
				g.getNode("LH.9921_0"), 75, true);
		DynamicCenterViewZoom zoom = new DynamicCenterViewZoom(images, 0.1, 75,
				true);

		rwalk.setEntityCount(g.getNodeCount() * 4);
		rwalk.setEvaporation(0.7);
		rwalk.setEntityMemory(40);
		rwalk.init(g);

		for (int i = 0; i < 20; i++)
			rwalk.compute();

		images.setOutputPolicy(OutputPolicy.BY_STEP);

		for (int i = 0; i < 75; i++) {
			rwalk.compute();
			updateGraph(g, rwalk);
			g.stepBegins(step++);
		}

		for (int i = 0; i < 75; i++) {
			// for (int j = 0; j < 2; j++)
			rwalk.compute();
			updateGraph(g, rwalk);
			move.next();
			zoom.nextValue();
			g.stepBegins(step++);
			// Thread.sleep(100);
		}
		/*
		 * for (int i = 0; i < 50; i++) { rwalk.compute(); updateGraph(g,
		 * rwalk); g.stepBegins(step++); }
		 */
		double byStepLength = fullLength / 925.0;
		double currentLength = 0;

		while (currentLength < fullLength) {
			// Edge e = null;
			double d = getArctanVariation(0, currentLength, fullLength)
					* fullLength;
			int index = 0;
			double s = 0;

			for (int i = 1; i < path.length; i++) {
				double l = lengths[i - 1];

				if (s + l >= d) {
					index = i - 1;
					break;
				}

				s += l;
			}

			d = (d - s) / lengths[index];

			double x = coords[index][0] + d
					* (coords[index + 1][0] - coords[index][0]);
			double y = coords[index][1] + d
					* (coords[index + 1][1] - coords[index][1]);

			// System.out.printf(
			// "[%d] (%.1f;%.1f) -- (%.1f;%.1f) -- (%.1f;%.1f)\n", index,
			// coords[index][0], coords[index][1], x, y,
			// coords[index + 1][0], coords[index + 1][1]);

			images.setViewCenter(x, y);

			currentLength += byStepLength;
			rwalk.compute();
			updateGraph(g, rwalk);
			g.stepBegins(step++);
		}

		move = new MoveCenterView(images, (xmax + xmin) / 2.0,
				(ymax + ymin) / 2.0, 25, true);
		zoom = new DynamicCenterViewZoom(images, 1, 25, true);

		for (int i = 0; i < 25; i++) {
			move.next();
			zoom.nextValue();
			rwalk.compute();
			updateGraph(g, rwalk);
			g.stepBegins(step++);
		}

		for (int i = 0; i < 100; i++) {
			rwalk.compute();
			updateGraph(g, rwalk);
			g.stepBegins(step++);
		}

		rwalk.terminate();
	}

	public static class MoveCenterView {
		DynamicAttribute x;
		DynamicAttribute y;
		FileSinkImages view;

		public MoveCenterView(FileSinkImages view, Node n, int length,
				boolean arctan) {
			double[] xy = { (Double) (n.getArray("xyz")[0]),
					(Double) (n.getArray("xyz")[1]) };

			this.x = new DynamicAttribute(view.getViewCenter().x, xy[0],
					length, arctan);
			this.y = new DynamicAttribute(view.getViewCenter().y, xy[1],
					length, arctan);

			this.view = view;

		}

		public MoveCenterView(FileSinkImages view, double x, double y,
				int length, boolean arctan) {
			this.x = new DynamicAttribute(view.getViewCenter().x, x, length,
					arctan);
			this.y = new DynamicAttribute(view.getViewCenter().y, y, length,
					arctan);

			this.view = view;
		}

		public void next() {
			x.nextValue();
			y.nextValue();

			view.setViewCenter(x.value, y.value);
		}
	}

	public static class DynamicCenterViewZoom extends DynamicAttribute {
		FileSinkImages view;

		public DynamicCenterViewZoom(FileSinkImages view, double to,
				double length, boolean arctan) {
			super(view.getViewPercent(), to, length, arctan);
			this.view = view;
		}

		public void setValue(double value) {
			view.setViewPercent(value);
		}
	}

	public static class DynamicAttribute {
		double from;
		double to;
		double step;
		double length;
		boolean arctan;
		double value;

		public DynamicAttribute(double from, double to, double length,
				boolean arctan) {
			this.from = from;
			this.to = to;
			this.length = length;
			this.arctan = arctan;
		}

		public void nextValue() {
			double d;

			if (step > length)
				return;

			if (arctan)
				d = getArctanVariation(0, step, length);
			else
				d = step / length;

			step++;

			d = d * (to - from) + from;
			setValue(d);
		}

		public void setValue(double value) {
			this.value = value;
		}
	}

	static final double atanp10 = Math.atan(10);
	static final double atanm10 = Math.atan(-10);

	public static double getArctanVariation(double min, double x, double max) {
		double d = -10 + 20 * (x - min) / (max - min);
		d = Math.atan(d);
		d = (d - atanm10) / (atanp10 - atanm10);
		return d;
	}

	public static void updateGraph(Graph graph, RandomWalk rwalk) {
		double mine = Double.MAX_VALUE;
		double maxe = Double.MIN_VALUE;

		// Obtain the maximum and minimum passes values.
		for (Edge edge : graph.getEachEdge()) {
			double passes = Math.min(27, rwalk.getPasses(edge));
			if (passes > maxe)
				maxe = passes;
			if (passes < mine)
				mine = passes;
		}

		// Set the colors.
		for (Edge edge : graph.getEachEdge()) {
			double passes = rwalk.getPasses(edge);
			double color = ((passes - mine) / (maxe - mine));
			edge.setAttribute("ui.color", color);
		}

		for (Node node : graph.getEachNode()) {
			double s = 0;

			for (Edge e : node.getEachEdge())
				s += e.getNumber("ui.color");

			node.setAttribute("ui.color", s / Math.max(1, node.getDegree()));
		}
	}

	public static class LHRandomWalk extends RandomWalk {
		SpriteManager manager;
		int eid = 0;

		public LHRandomWalk(SpriteManager manager) {
			super();
			this.manager = manager;
			setEntityClass(VehicleEntity.class.getName());
		}

		public Entity createEntity() {
			Entity e = super.createEntity();

			if (e != null && e instanceof VehicleEntity) {
				((VehicleEntity) e).setSprite(manager.addSprite(String.format(
						"entity-%08x", eid++)));
			}

			return e;
		}
	}

	public static class VehicleEntity extends TabuEntity {
		Edge crossing = null;
		double position = 1;
		double crossedLength = 0;
		Sprite sprite;

		public void setSprite(Sprite s) {
			sprite = s;
		}

		public void step() {
			if (position >= 1 || crossing == null)
				super.step();

			if (crossing != null) {
				double speed = crossing.getNumber("speedMax") / 90.0;
				double trafic = 1 - 0.3 * Math.min(50,
						crossing.getNumber(context.getPassesAttribute())) / 50.0;

				crossedLength += speed * trafic * 25;
				crossedLength = Math.min(crossedLength,
						crossing.getNumber("length"));
				position = crossedLength / crossing.getNumber("length");
				addPass(crossing, current);

				// System.out.printf("%s on %s at %f (%f ; %f ; %f / %f)\n",
				// sprite.getId(), crossing.getId(), position, speed, trafic,
				// crossedLength, crossing.getNumber("length"));

				// /System.out.printf("%.5f, %.5f, %.5f, %.5f, %.5f\n", speed,
				// trafic, crossedLength, crossing.getNumber("length"),
				// position);

				sprite.setPosition(getSpriteLocation());
			} else
				System.out.printf("%s nowhere\n", sprite.getId());
		}

		public double getSpriteLocation() {
			if (crossing == null)
				return 0;

			if (current == crossing.getNode0())
				return position;
			else
				return 1 - position;
		}

		protected void cross(Edge e) {
			if (crossing != null)
				sprite.detach();
			crossing = e;
			position = 0;
			crossedLength = 0;
			sprite.attachToEdge(e.getId());
			super.cross(crossing);
			sprite.setPosition(getSpriteLocation());
		}
	}
}
