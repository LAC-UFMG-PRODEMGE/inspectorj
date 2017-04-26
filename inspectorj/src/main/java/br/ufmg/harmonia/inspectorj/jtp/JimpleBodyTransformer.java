package br.ufmg.harmonia.inspectorj.jtp;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Unit;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIfStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;

public class JimpleBodyTransformer extends BodyTransformer {

	// jtp.myTransform

	private Map<String, Unit> visited = new HashMap<String, Unit>();

	protected void internalTransform(Body body, String phase, Map<String, String> options) {

		//if (body.getMethod().getName().contains("calculate")) {

			PrintStream out = G.v().out;
			out.println(body);
			Graph graph = createGraph();

			createCFG(body, graph);

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

	private void createCFG(Body body, Graph graph) {
		ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(body);

		List<Unit> heads = unitGraph.getHeads();
		for (Unit head : heads) {
			visited.put(head.toString(), head);
			Node previousNode = graph.addNode(head.toString());
			previousNode.addAttribute("label", previousNode.getId());
			addSuccsOfNode(unitGraph, graph, head, previousNode);
		}
	}

	private Unit getJumpUnit(JGotoStmt jgoto) {
		Unit target = jgoto.getTarget();
		if (target instanceof JGotoStmt) {
			return getJumpUnit((JGotoStmt) target);
		}
		return target;
	}

	private void addSuccsOfNode(ExceptionalUnitGraph unitGraph, Graph graph,
			Unit head, Node previousNode) {
		List<Unit> succsOf = unitGraph.getSuccsOf(head);
		if (succsOf != null) {
			for (Unit next : succsOf) {
				// JAssignStmt check the left Operator
				// JGotoStmt
				if (next instanceof JGotoStmt) {
					next = getJumpUnit((JGotoStmt) next);
				}

				String nextId = next.toString();
				if (next instanceof JIfStmt) {
					nextId = "if " + ((JIfStmt) next).getCondition().toString();
				}

				Node addNode = graph.getNode(nextId);
				if (addNode == null) {
					addNode = graph.addNode(nextId);

					addNode.addAttribute("label", addNode.getId());
				}
				String idEdge = head.toString() + "_" + nextId;
				Edge addEdge = graph.getEdge(idEdge);
				if (addEdge == null) {
					graph.addEdge(idEdge, previousNode, addNode, true);
				}
				if (visited.containsKey(nextId)) {
					continue;
				} else {
					visited.put(nextId, next);
				}

				List<Unit> succsOf2 = unitGraph.getSuccsOf(next);
				if (succsOf2 != null && succsOf2.size() > 0) {
					addSuccsOfNode(unitGraph, graph, next, addNode);
				}
			}
		}
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
