package br.ufmg.harmonia.inspectorj;

import soot.Pack;
import soot.PackManager;
import soot.Transform;
import br.ufmg.harmonia.inspectorj.jtp.JimpleBodyTransformer;


public class InspectorJTPMain {
	private static String JIMPLE_TRANSFORMER_PACK = "jtp";
	public static void main(String[] args) {
		
		Pack stp = PackManager.v().getPack(JIMPLE_TRANSFORMER_PACK);
		JimpleBodyTransformer bodyTransformer = new JimpleBodyTransformer();
		stp.add(new Transform(JIMPLE_TRANSFORMER_PACK+".mytransformer", bodyTransformer));
		
		
		soot.Main.main(args);
	}
}
