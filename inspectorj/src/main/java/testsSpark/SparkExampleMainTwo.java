package testsSpark;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.EntryPoints;
import soot.G;
import soot.Local;
import soot.PointsToSet;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.ValueBox;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.spark.SparkTransformer;
import soot.options.Options;
import soot.tagkit.SourceLnNamePosTag;
import soot.tagkit.Tag;
import soot.util.Chain;
import br.ufmg.harmonia.inspectorj.resource.test.case04.Test4;
import soot.jimple.toolkits.pointer.LocalMayAliasAnalysis;
import soot.jimple.toolkits.pointer.LocalMustAliasAnalysis;

public class SparkExampleMainTwo {
	private static SootClass loadClass(String name, boolean main) {
	    SootClass c = Scene.v().loadClassAndSupport(name);
	    c.setApplicationClass();
	    if (main) Scene.v().setMainClass(c);
	        return c;
	}
	
	public static void main(String[] args) {

		String classpath = //"/C:/inspectorj/src/test/java;/C:/Prodemge/maven/.m2/repository/org/graphstream/gs-algo/1.3/gs-algo-1.3.jar;/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/resources.jar;/C:/Prodemge/maven/.m2/repository/org/graphstream/pherd/1.0/pherd-1.0.jar;/C:/Prodemge/maven/.m2/repository/soot/soot-trunk/2.5.0/soot-trunk-2.5.0.jar;/C:/Prodemge/maven/.m2/repository/org/graphstream/gs-ui/1.3/gs-ui-1.3.jar;/C:/Prodemge/git2/InspectorJ/inspectorj/target/methodsAndClasses/;/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/jce.jar;/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/ext/sunjce_provider.jar;/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/ext/sunmscapi.jar;/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/ext/zipfs.jar;/C:/Prodemge/maven/.m2/repository/org/graphstream/gs-core/1.3/gs-core-1.3.jar;/C:/Prodemge/maven/.m2/repository/org/scala-lang/scala-library/2.10.1/scala-library-2.10.1.jar;/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/ext/access-bridge-64.jar;"
				"/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/rt.jar;"
				+ "/C:/Prodemge/workspaces/workspace-kepler/inspectorj/src/main/java";
				//+ "/C:/Prodemge/workspaces/workspace-kepler/inspectorj/src/main/resources";

		
		Options.v().set_soot_classpath(classpath);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_keep_line_number(true);
		Options.v().set_via_shimple(true);
		
		//enable whole program mode
		Options.v().set_whole_program(true);
//	    sootArgs.add("-p");
//	    sootArgs.add("wjop");
//	    sootArgs.add("enabled:true");
		
//	    //enable points-to analysis
		Options.v().setPhaseOption("cg","verbose:false");
		Options.v().setPhaseOption("cg","enabled:true");

//	    //enable Spark
		Options.v().setPhaseOption("cg.spark","enabled:true");
			
		
//		loadClass(Item.class.getName(), false);
//		loadClass(Container.class.getName(), false);
//		SootClass c = loadClass(Use.class.getName(), true);
//		
		//
		SootClass c = loadClass(Test4.class.getName(), true);
	
		
		
		soot.Scene.v().loadNecessaryClasses();
		soot.Scene.v().setEntryPoints(EntryPoints.v().all());
		
		HashMap opt = new HashMap();
		opt.put("enabled","true");                                   // enabled, necessary
		opt.put("verbose","false");                                   //
		opt.put("simple-edges-bidirectional","false");               //
		opt.put("on-fly-cg","true");                                 //
		opt.put("propagator","worklist");                            //
		//opt.put("double-set-old","hybrid");                        //so faz sentido se "set-impl" for double
		//opt.put("double-set-new","hybrid");                        //so faz sentido se "set-impl" for double
		opt.put("set-impl", "hybrid");                               //tem varias opcoes

	//	opt.put("vta","false");                   
    //	Setting VTA to true has the effect of setting field-based, types-for-sites, and simplify-sccs to true, 
    //  and on-fly-cg to false, to simulate Variable Type Analysis, described in our OOPSLA 2000 paper. Note that the 
	//	algorithm differs from the original VTA in that it handles array elements more precisely.
 	//	opt.put("rta","false");          
	//  Setting RTA to true sets types-for-sites to true, and causes Spark to use a single points-to set for all variables, giving Rapid Type Analysis.	

	//   When this option is set to true, a browseable HTML representation of the pointer assignment graph is output to a file called pag.jar after the analysis completes.	
		opt.put("dump-html","true");
	
	//  When this option is set to true, the results of the analysis are encoded within tags and printed with the resulting Jimple code.
		opt.put("add-tags","true");    
		
		SparkTransformer.v().transform("",opt);
		
		
		
		//PAG pag = (PAG) Scene.v().getPointsToAnalysis();
		//PAG2HTML teste = new PAG2HTML(pag, "src/test/java");
		//teste.dump();
		
//		//Objetos de Container 
//		Map/*<Local>*/ ls = getLocals(c,"go","br.ufmg.harmonia.inspectorj.Container");
//		printLocalIntersects(ls);
//		
		//Objetos de Item
//		Map/*<Local>*/ ls2 = getLocals(c,"go","br.ufmg.harmonia.inspectorj.Item");
//		printLocalIntersects(ls2);
		
		//Objetos de Item
		//Map/*<Local>*/ ls2 = getLocals(c,"main","int[]");
		//printLocalIntersects(ls2);
		
		printLocalIntersects2(c);
		
//		Map/*<Local>*/ ls = getLocals(c,"go","br.ufmg.harmonia.inspectorj.Container.item");
//		printLocalIntersects(ls);
	}
	
	
	private static void printLocalIntersects2(SootClass sootClass){
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		
		Chain<Local> locals = null;
		List<SootMethod> methods = sootClass.getMethods();
		for (SootMethod sootMethod : methods) {
			if(sootMethod.isConcrete() && sootMethod.hasActiveBody()){
				if(locals == null)
					locals = sootMethod.getActiveBody().getLocals();
				else
					locals.addAll(sootMethod.getActiveBody().getLocals());
			}
		}
		
		for (Local localAux1 : locals) {
			PointsToSet pointsAux1 = pta.reachingObjects(localAux1);
			if(localAux1.getType() instanceof soot.ArrayType){
				PointsToSet pointsArrayAux2 = pta.reachingObjectsOfArrayElement(pointsAux1);
				System.out.println(pointsArrayAux2);
			}
			for(Local localAux2 : locals){
				if(!localAux1.equals(localAux2)){
					PointsToSet pointsAux2 = pta.reachingObjects(localAux2);
					
					
					System.out.println("["+localAux1+","+localAux2+"]\t Container intersect? "+pointsAux1.hasNonEmptyIntersection(pointsAux2));
				}
			}
		}
		
	}
	
	
	
	
	
	//TA OK
	private static void printLocalIntersects(Map/*<Integer,Local>*/ ls) {
		soot.PointsToAnalysis pta = Scene.v().getPointsToAnalysis();
		
		Iterator i1 = ls.entrySet().iterator();
		while (i1.hasNext()) {
			Map.Entry e1 = (Map.Entry)i1.next();
			int p1 = ((Integer)e1.getKey()).intValue();
			Local l1 = (Local)e1.getValue();
			PointsToSet r1 = pta.reachingObjects(l1);
//			PointsToSet r3= pta.reachingObjectsOfArrayElement(r1);
			System.out.println("r1:"+r1);
//			System.out.println("r3:"+r3);
			Iterator i2 = ls.entrySet().iterator();
			while (i2.hasNext()) {
				Map.Entry e2 = (Map.Entry)i2.next();
				int p2 = ((Integer)e2.getKey()).intValue();
		        Local l2 = (Local)e2.getValue();
		        PointsToSet r2 = pta.reachingObjects(l2);
		        if (p1<=p2)
		        	System.out.println("["+p1+","+p2+"]\t Container intersect? "+r1.hasNonEmptyIntersection(r2));
		    }
		}
	}
	
	private static Map/*<Integer,Local>*/ getLocals(SootClass sc, String methodname, String typename) {
		Map res = new HashMap();
		Iterator mi = sc.getMethods().iterator();
		while (mi.hasNext()) {
			SootMethod sm = (SootMethod)mi.next();
			
	        System.err.println(sm.getName());
	        if (sm.getName().equals(methodname) && sm.isConcrete()) {
				JimpleBody jb = (JimpleBody)sm.retrieveActiveBody();
				//jb.getLocals().getFirst();
				PrintStream out = G.v().out;
				out.println(jb);
				Iterator ui = jb.getUnits().iterator();
				while (ui.hasNext()) {
					Stmt s = (Stmt)ui.next();
	                int line = getLineNumber(s);
	                System.out.println("Line: "+line);
	                // find definitions
					Iterator bi = s.getDefBoxes().iterator();
					while (bi.hasNext()) {
						Object o = bi.next();
						if (o instanceof ValueBox) {
							Value v = ((ValueBox)o).getValue();
							String typeNameClass = v.getType().toString();
							if(v instanceof Local) {
								if (typeNameClass.equals(typename))
									res.put(new Integer(line),v);
							}
							
						}
                    }
                }
	        }
		}
		return res;
	}
		 
	//TA OK	 
//	private static Map/*<Integer,Local>*/ getLocals(SootClass sc, String methodname, String typename) {
//		Map res = new HashMap();
//		Iterator mi = sc.getMethods().iterator();
//		while (mi.hasNext()) {
//			SootMethod sm = (SootMethod)mi.next();
//	        System.err.println(sm.getName());
//	        if (sm.getName().equals(methodname) && sm.isConcrete()) {
//				JimpleBody jb = (JimpleBody)sm.retrieveActiveBody();
//				//PrintStream out = G.v().out;
//				//out.println(jb);
//				Iterator ui = jb.getUnits().iterator();
//				while (ui.hasNext()) {
//					Stmt s = (Stmt)ui.next();
//	                int line = getLineNumber(s);
//	                // find definitions
//					Iterator bi = s.getDefBoxes().iterator();
//					while (bi.hasNext()) {
//						Object o = bi.next();
//						if (o instanceof ValueBox) {
//							Value v = ((ValueBox)o).getValue();
//							String typeNameClass = v.getType().toString();
//							if(v instanceof Local) {
//								if (typeNameClass.equals(typename))
//									res.put(new Integer(line),v);
//							}
//							
//						}
//                    }
//                }
//	        }
//		}
//		return res;
//	}
	 
	//TA OK
	private static int getLineNumber(Stmt s) {

		List<Tag> tagsList = s.getTags();
		Tag tag = tagsList.get(0);
		SourceLnNamePosTag posTag = (SourceLnNamePosTag) tag;
		Integer line = posTag.startLn();
		
		if(line != 0) {
//			System.out.println(line);
			return Integer.parseInt(line.toString());
		}
		    
		else {
			System.out.println("not define line number tag");
			return -1;
		}
	}
}
