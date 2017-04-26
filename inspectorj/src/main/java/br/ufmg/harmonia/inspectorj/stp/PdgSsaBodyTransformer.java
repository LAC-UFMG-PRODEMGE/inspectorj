package br.ufmg.harmonia.inspectorj.stp;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages.OutputType;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PatchingChain;
import soot.Unit;
import soot.Value;
import soot.jimple.AddExpr;
import soot.jimple.BinopExpr;
import soot.jimple.DivExpr;
import soot.jimple.MulExpr;
import soot.jimple.SubExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JimpleLocal;
import soot.shimple.internal.SPhiExpr;

public class PdgSsaBodyTransformer extends BodyTransformer {// jtp.myTransform

	//private Map<String, Unit> visited = new HashMap<String, Unit>();

	protected void internalTransform(Body body, String phase, Map<String, String> options) {

		//if (body.getMethod().getName().contains("calculate")) {

			PrintStream out = G.v().out;
			out.println(body);
			Graph graph = createGraph();

			createDependecyGraph(body, graph);

			

			graph.display();
			
			// FileSinkImages pic = new FileSinkImages(OutputType.PNG,
			// Resolutions.VGA);
			// pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);
			//
			// try {
			// pic.writeAll(graph, "sample2.png");
			// } catch (IOException e) {
			// e.printStackTrace();
			// }

		//}

	}

	private void createDependecyGraph(Body body, Graph graph) {
		PatchingChain<Unit> units = body.getUnits();

		for (Unit unit : units) {

			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt) unit;
				Value leftOp = stmt.getLeftOp();

				Node localNode = createNode(graph, leftOp);

				Value rightOp = stmt.getRightOp();
				if (rightOp instanceof JimpleLocal) {
					JimpleLocal jLocal = (JimpleLocal) rightOp;
					Node dependencyNode = createNode(graph, jLocal);
					createEdge(graph, localNode, dependencyNode);
				}

				if (rightOp instanceof SPhiExpr) {
					SPhiExpr phiExpr = (SPhiExpr) rightOp;
					List<Value> values = phiExpr.getValues();
					if (values != null) {
						for (Value phiValue : values) {
							Node phiValueNode = createNode(graph, phiValue);
							createEdge(graph, localNode, phiValueNode);
						}
					}
					// out.print(phiExpr);
				}

				if (rightOp instanceof AddExpr || rightOp instanceof MulExpr
						|| rightOp instanceof DivExpr
						|| rightOp instanceof SubExpr) {
					BinopExpr expr = (BinopExpr) rightOp;
					Value op1 = expr.getOp1();
					if (op1 instanceof JimpleLocal) {
						Node dependencyNode = createNode(graph, op1);
						createEdge(graph, localNode, dependencyNode);

					}
					Value op2 = expr.getOp2();
					if (op2 instanceof JimpleLocal) {
						Node dependencyNode = createNode(graph, op2);
						createEdge(graph, localNode, dependencyNode);
					}
				}

			}

			// out.println(unit);
		}
	}

	private void createEdge(Graph graph, Node localNode, Node dependencyNode) {
		String idEdge = localNode.getId() + "_" + dependencyNode.getId();
		Edge edge = graph.getEdge(idEdge);
		if (edge == null) {
			graph.addEdge(idEdge, dependencyNode, localNode, true);
		}
	}

	private Node createNode(Graph graph, Value leftOp) {
		Node localNode = graph.getNode(leftOp.toString());
		if (localNode == null) {
			localNode = graph.addNode(leftOp.toString());
			localNode.addAttribute("label", localNode.getId());
		}
		return localNode;
	}
	

	private Graph createGraph() {
		System.setProperty("gs.ui.renderer",
				"org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		Graph graph = new SingleGraph("PDG!");
		String styleSheet = "node { "
				+ "stroke-mode: plain; fill-color: white;shape: rounded-box;"
				+ "size-mode: fit;text-size:13; padding: 4px, 4px;"
				+ "}edge {arrow-shape: arrow; arrow-size: 10px, 10px;}";

		graph.addAttribute("ui.stylesheet", styleSheet);

		return graph;
	}

}
