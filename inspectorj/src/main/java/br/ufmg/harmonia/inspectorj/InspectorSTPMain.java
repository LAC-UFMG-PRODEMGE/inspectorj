package br.ufmg.harmonia.inspectorj;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import soot.Kind;
import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import br.ufmg.harmonia.inspectorj.stp.ShimpleBodyTransformer;
import br.ufmg.harmonia.inspectorj.util.ConfigProperties;
import br.ufmg.harmonia.inspectorj.util.ForeLayoutRender;
import br.ufmg.harmonia.inspectorj.util.GraphSingleton;
import br.ufmg.harmonia.inspectorj.util.InspectorJWin;
import br.ufmg.harmonia.inspectorj.util.InterproceduralResolver;
import br.ufmg.harmonia.inspectorj.util.ViewerMouseEvents;


public class InspectorSTPMain {
	private static String SHIMPLE_TRANSFORMER_PACK = "stp";
	
	private static InspectorJWin inspectorJWindow = new InspectorJWin();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		String classpath = "/C:/Prodemge/jdks/jdk1.7.0_55/jre/lib/rt.jar;"
						 + "/C:/Prodemge/workspaces/workspace-kepler/inspectorj/src/main/java;"
						 + "/C:/Prodemge/workspaces/workspace-kepler/inspectorj/sootOutput;"
					     + "/C:/Prodemge/workspaces/workspace-kepler/inspectorj/src/main/resources";
		
		//System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
		
		
		Pack stp = PackManager.v().getPack(SHIMPLE_TRANSFORMER_PACK);
		ShimpleBodyTransformer bodyTransformer = new ShimpleBodyTransformer();
		stp.add(new Transform(SHIMPLE_TRANSFORMER_PACK+".mytransformer", bodyTransformer));

		
		
		Options.v().set_verbose(false);
		Options.v().set_validate(true);
		Options.v().set_whole_program(false);
		Options.v().set_app(true);
		Options.v().set_via_shimple(true);
		Options.v().set_src_prec(Options.src_prec_java); 
		Options.v().set_output_format(Options.output_format_S);
		
		
		Options.v().set_allow_phantom_refs(true);		
		Options.v().set_xml_attributes(false);
		Options.v().set_force_overwrite(true);
		Options.v().set_whole_shimple(false);
		Options.v().set_write_local_annotations(true);
		
		Options.v().setPhaseOption("jb","use-original-names:true");
		Options.v().setPhaseOption("jb","preserve-source-annotations:true");
		
		Options.v().setPhaseOption("jj","use-original-names:true");

		Options.v().set_keep_line_number(true);

		
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setCurrentDirectory(new File("src/main/resources"));
		
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter() {			
			@Override
			public String getDescription() {				
				return "*.properties";
			}
			
			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				
				
				if(f!=null && f.getName().endsWith(".properties")){
					return true;
				}								
				return false;
			}
		});
		
		
		int result = fileChooser.showDialog(null, "Selecionar o arquivo .properties");
		String filename = null;
		if (result == JFileChooser.APPROVE_OPTION){
            filename = fileChooser.getSelectedFile().getPath();
            JOptionPane.showMessageDialog(null, "Aguarde enquanto a análise é processada ");
        }
        else{
        	if (result == JFileChooser.CANCEL_OPTION){   
        		JOptionPane.showMessageDialog(null, "Você não selecionou nada.");
        	}
        	else if (result == JFileChooser.ERROR_OPTION){
        		JOptionPane.showMessageDialog(null, "Ocorreu um erro inexperado.");
        	} 
        	return;
        }
		
		ConfigProperties configProperties = ConfigProperties.getInstance(new File(filename));
		
		Options.v().set_output_dir(configProperties.getString("diretorio.saida"));
		
		
		//Options.v().set_soot_classpath(classpath);
		String classpath2 = configProperties.getString("classpath");
		Options.v().set_soot_classpath(classpath2);
		
//		SootClass c = Scene.v().forceResolve(MinhaClasse.class.getName(), SootClass.BODIES);
		String nomeDaClasse = configProperties.getString("inicial.classe");		
		if(nomeDaClasse == null || nomeDaClasse.equals(""))
			nomeDaClasse = configProperties.getString("fonte.classe");
		
		
		SootClass c = Scene.v().forceResolve(nomeDaClasse, SootClass.BODIES);
		/*Options.v().set_src_prec(Options.src_prec_J); 
		Options.v().set_output_dir("C:Prodemge/workspaces/workspace-kepler/inspectorj/sootOutput");
		Options.v().set_output_format(Options.output_format_shimple);
		
		Options.v().set_allow_phantom_refs(true);		
		Options.v().set_xml_attributes(false);
		Options.v().set_force_overwrite(true);
		Options.v().set_whole_shimple(false);		*/

		
		c.setApplicationClass();
		Scene.v().loadNecessaryClasses();
		
		String nomeDoMetodo = configProperties.getString("inicial.metodo");
		if(nomeDoMetodo == null || nomeDoMetodo.equals(""))
			nomeDoMetodo = configProperties.getString("fonte.metodo");
		
		SootMethod method = c.getMethodByName(nomeDoMetodo);
		//SootMethod method = c.getMethodByName("main");//main
		List<SootMethod> entryPoints = new ArrayList<SootMethod>();
		entryPoints.add(method);
		Scene.v().setEntryPoints(entryPoints);				
	
		//Instancia o grafo, para começar a criar as imagens
		final GraphSingleton graph = GraphSingleton.getInstance();
		//final Viewer viewer = graph.display(true);
		
		PackManager.v().runPacks();
		PackManager.v().writeOutput();
					
		//percorreCallGraph(method);
		
		InterproceduralResolver interResolver = InterproceduralResolver.getInstance();
		interResolver.resolveLaters();
		interResolver.resolveTaintedNode();
		interResolver.selectTaintedNodes();
		interResolver.colorGraph();			
		
		//Termina a criaçao das imagens
		graph.endOfCreatingOfImages();
		buildWindow();
		System.out.println("---------Finished");
	}


	protected static void percorreCallGraph(SootMethod method) {
		CallGraph callGraph = Scene.v().getCallGraph();
		Iterator<Edge> edgesOutOfAutenticar = callGraph.edgesOutOf(method);
		List<SootMethod> visiteds = new ArrayList<SootMethod>();
		System.out.println(method.getName());
		while(edgesOutOfAutenticar.hasNext()){
			Edge edge = edgesOutOfAutenticar.next();
			
			if(edge.kind().equals(Kind.CLINIT)){
				continue;
			}
			
			SootMethod tgt = edge.tgt();
			if(tgt!=null){
				System.out.println(tgt.getName());
				nextCallMethodAnalysis(tgt, visiteds, callGraph, "\t");	
			}		
		}
	}
	
	
	protected static void nextCallMethodAnalysis(SootMethod tgt, List<SootMethod> visiteds, 
			CallGraph callGraph, String tab) {
		Iterator<Edge> edgesOutOfAutenticar = callGraph.edgesOutOf(tgt);
		while(edgesOutOfAutenticar.hasNext()){
			Edge  edge = edgesOutOfAutenticar.next();
			if(edge.kind().equals(Kind.CLINIT)){
				continue;
			}
			SootMethod  nextTgt = edge.tgt();
			if(nextTgt!=null){										
				if(visiteds.contains(nextTgt)){
					continue;
				}else{
					visiteds.add(nextTgt);
				}
				System.out.println(tab+"|->"+nextTgt.getName());					
				nextCallMethodAnalysis(nextTgt, visiteds, callGraph, tab +"\t");
		buildWindow();
				
			}
		}
	}
	
	public static void buildWindow() {
		ConfigProperties propriedades = ConfigProperties.getInstance();
				
		int janelaLargura = propriedades.getInteger("janela.largura", 1280);
		int janelaAltura = propriedades.getInteger("janela.altura", 720);
		
		String tituloJanela = "InspectorJ";
		
		inspectorJWindow.build(tituloJanela, janelaLargura, janelaAltura);

		GraphSingleton graph = GraphSingleton.getInstance();
		
	
//		Viewer display = graph.display(true);
		Viewer viewer = new Viewer(graph,
				Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
		
	
		SpringBox layout = new SpringBox(false, new Random(new Date().getTime()));

		viewer.enableAutoLayout(layout);

		DefaultView view = (DefaultView) viewer.addDefaultView(false);
//		DefaultView view = (DefaultView) display.getDefaultView();

		
//		view.resizeFrame(janelaLargura, janelaAltura - 200);
//		view.setPreferredSize(new Dimension(janelaLargura, janelaAltura));
		view.getCamera().setViewPercent(1.5);
		
		final ViewerPipe pipe = viewer.newViewerPipe();
		
		pipe.addAttributeSink(graph);
				
		ViewerMouseEvents mouseEffects = new ViewerMouseEvents(view);
		pipe.addViewerListener(mouseEffects);
		view.addMouseListener(mouseEffects);
		view.addMouseMotionListener(mouseEffects);
		view.addMouseWheelListener(mouseEffects);
		ForeLayoutRender renderer = new ForeLayoutRender(mouseEffects);
		view.setForeLayoutRenderer(renderer);
		
//		view.setMouseManager(new InternalMousManager());
		
		inspectorJWindow.appendView(viewer, view);
//		inspectorJWindow.appendView(display, view);
		
		(new Thread() {
			
			public void run() {
				while (true) {
				try {
					Thread.sleep(500);
					Thread.sleep(500);
					pipe.blockingPump();
				} catch (InterruptedException e) {
					System.exit(0);
				}
			}				
			}
		}).start();
		
		
	}	
}
