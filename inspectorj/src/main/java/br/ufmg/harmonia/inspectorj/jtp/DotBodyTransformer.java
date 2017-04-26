package br.ufmg.harmonia.inspectorj.jtp;

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.pdg.HashMutablePDG;
import soot.util.cfgcmd.CFGToDotGraph;
import soot.util.dot.DotGraph;

public class DotBodyTransformer extends BodyTransformer{

	@Override
	protected void internalTransform(Body body, String phaseName,
			Map<String, String> options) {
		ExceptionalUnitGraph unitGraph = new ExceptionalUnitGraph(body);
		
		HashMutablePDG pDependencyGraph = new HashMutablePDG(unitGraph);
		
		
		CFGToDotGraph dotGenerator = new CFGToDotGraph();
		
		DotGraph drawCFG = dotGenerator.drawCFG(unitGraph);		
		drawCFG.plot(body.getMethod().getName());
		//drawCFG.
	}

}
