package br.ufmg.harmonia.inspectorj.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import soot.Type;
import soot.tagkit.LineNumberTag;
import soot.tagkit.SourceLnNamePosTag;
import soot.tagkit.Tag;
import br.ufmg.harmonia.inspectorj.util.holder.ClassModificationInfo;
import br.ufmg.harmonia.inspectorj.util.holder.LocalTypeHolder;
import br.ufmg.harmonia.inspectorj.util.holder.MethodInvocationHolder;

import com.google.common.collect.Lists;

public class InterproceduralResolver {

	
	private static InterproceduralResolver myInstance;
	
	protected Map<String, List<Node>> mapUnitReturnMethod;
	protected Map<Node, String> mapResolveReturnMethodLater;
		
	protected Map<String, Node> mapUnitParameterInvocation;
	protected Map<Node, List<String>> mapResolveParameterInvocationLater;
	
	protected Map<String, Node> mapUnitTaintedNode;
	protected Map<String, Edge> mapUnitTaintedEdge;
	
	protected Map<String, Node> mapSinkNode;
	
	protected Map<Node, String> mapUnitRefToObject;
	protected Map<Node, String> mapUnitObjectName;
	
	protected Map<Node, LocalTypeHolder> mapLocalType;
	
	protected Map<Node, Map<Node, String>> mapSubtypes;
	
	protected Map<String, ClassModificationInfo> mapClassModification;
	
	protected List<Node> nodeTypesVisited = new ArrayList<Node>();
	
	protected Set<Node> nodeWriteClass = new HashSet<Node>();
	
		
	private InterproceduralResolver() {
		mapUnitReturnMethod = new ConcurrentHashMap<String, List<Node>>();
		mapResolveReturnMethodLater = new ConcurrentHashMap<Node, String>();
		
		mapUnitParameterInvocation = new ConcurrentHashMap<String, Node>();
		mapResolveParameterInvocationLater = new ConcurrentHashMap<Node, List<String>>();
		
		mapUnitTaintedNode = new ConcurrentHashMap<String, Node>();
		mapUnitTaintedEdge = new ConcurrentHashMap<String, Edge>();
		
		mapSinkNode = new ConcurrentHashMap<String, Node>();
		
		mapUnitRefToObject = new ConcurrentHashMap<Node, String>();
		mapUnitObjectName =  new ConcurrentHashMap<Node, String>();
		
		mapLocalType = new HashMap<Node, LocalTypeHolder>();
		
		mapSubtypes = new HashMap<Node, Map<Node, String>>();
		
		mapClassModification = new HashMap<String, ClassModificationInfo>();
		
	}
	
	public synchronized static InterproceduralResolver getInstance(){
		if(myInstance == null){
			myInstance = new InterproceduralResolver();
		}
		return myInstance;
	}
	
	//ReturnMethod
	public synchronized Map<String, List<Node>> getMapUnitReturnMethod() {
		return mapUnitReturnMethod;
	}

	public synchronized Map<Node, String> getMapResolveReturnMethodLater() {
		return mapResolveReturnMethodLater;
	}

	//Parameter Invocation
	public synchronized Map<String, Node> getMapUnitParameterInvocation() {
		return mapUnitParameterInvocation;
	}

	public synchronized Map<Node, List<String>> getMapResolveParameterInvocationLater() {
		return mapResolveParameterInvocationLater;
	}
	
	//Tainted Nodes
	public synchronized Map<String, Node> getMapUnitTaintedNode() {
		return mapUnitTaintedNode;
	}
	
	
	//Taindted Edges
	public synchronized Map<String, Edge> getMapUnitTaintedEdge() {
		return mapUnitTaintedEdge;
	}
	
	//Sink Nodes
	public synchronized Map<String, Node> getMapSinkNode(){
		return mapSinkNode;
	}
	
	//Object
	public synchronized Map<Node, String> getMapUnitRefToObject() {
		return mapUnitRefToObject;
	}

	public synchronized Map<Node, String> getMapUnitObjectName() {
		return mapUnitObjectName;
	}
	
	//MapLocalType
	public synchronized Map<Node, LocalTypeHolder> getMapLocalType() {
		return mapLocalType;
	}
	
	//
	public synchronized Map<Node, Map<Node, String>> getMapSubtypes() {
		return mapSubtypes;
	}
	
	//
	public synchronized void resolveTaintedNodes(Node node) {
		
		Iterable<Edge> eachLeavingEdge = node.getEachLeavingEdge();
		ArrayList<Edge> newArrayList = Lists.newArrayList(eachLeavingEdge);
		List<Edge> synchronizedList = Collections.synchronizedList(newArrayList);
		for(Edge edge : synchronizedList) {
			
			Boolean flag = edge.getAttribute("isControlEdge"); 
			if(flag == null || flag == false){
				
				Edge taintedEdge = mapUnitTaintedEdge.get(edge.getId());
				if(taintedEdge == null) {
					mapUnitTaintedEdge.put(edge.getId(), edge);
					Node targetNode = edge.getTargetNode();
					
					if(targetNode.getAttribute("sink")) {
						GraphSingleton.getInstance().setAttribute("tainted", true);
						InterproceduralResolver.getInstance().getMapSinkNode().put(targetNode.getId(), targetNode);
					}
					
					mapUnitTaintedNode.put(targetNode.getId(), targetNode);
					resolveTaintedNodes(targetNode);
				}				
			}			
		}
	}
	
	
	
	//colocar algo aqui pra tratar a escrita no programa
	public synchronized void resolveTypes(Node node){
		
		if(nodeTypesVisited.contains(node)){
			return;
		}else{
			nodeTypesVisited.add(node);
		}
		
		Node targetNode = null;
		//verifica se node e um node de invocacao
		Map<Node, String> map = mapSubtypes.get(node);
		if(map!=null){
			Set<Entry<Node, String>> entryParameters = Collections.synchronizedSet(map.entrySet());
		//	GraphSingleton graphInstance = GraphSingleton.getInstance();
			for (Entry<Node, String> entry : entryParameters) {			
				synchronized (this) {
					Node parameter = entry.getKey();
					LocalTypeHolder holder = mapLocalType.get(node);
					List<Type> listType = holder.getTypes();
					for(Type type: listType){
						String key = "<"+type+entry.getValue(); 
						
						Node nodeArg = InterproceduralResolver.getInstance().getMapUnitParameterInvocation().get(key);
						if(nodeArg!=null){
							GraphSingleton.getInstance().createEdge(nodeArg, parameter, false);
							
							MethodInvocationHolder miHolder = new MethodInvocationHolder();
							miHolder.setNodeArgOutside(parameter);
							miHolder.setNodeParamInside(nodeArg);
							miHolder.setNodeMethod(node);
							miHolder.setUnit(holder.getUnit());
							miHolder.setTags(holder.getUnit().getTags());
							miHolder.setKey(key);
							
							
						}else{
							List<String> list = InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().get(parameter);
							if(list!=null){
								list.add(key);
								InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(parameter, list);
							}
							else{
								list = new ArrayList<String>();
								list.add(key);
								InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(parameter, list);
							}							
						}
					}
				}
			}				
			return;
		}
		//se nao, coloca todos os filhos do node no map passando seu tipo real
		else{
			Iterable<org.graphstream.graph.Edge> eachLeavingEdge = node.getEachLeavingEdge();
			ArrayList<org.graphstream.graph.Edge> newArrayList = Lists.newArrayList(eachLeavingEdge);
			if(newArrayList != null){
				List<org.graphstream.graph.Edge> synchronizedList = Collections.synchronizedList(newArrayList);
				for(org.graphstream.graph.Edge edge: synchronizedList) {
					targetNode = edge.getTargetNode();
					LocalTypeHolder holder = mapLocalType.get(node);
					List<Type >list = holder.getTypes();
					InterproceduralResolver.getInstance().getMapLocalType().put(targetNode, new LocalTypeHolder(targetNode,list,holder.getUnit()));
					resolveTypes(targetNode);
				}
				InterproceduralResolver.getInstance().getMapLocalType().remove(node);
			}
		}
		
	}
	
	
	//Resolve Laters
	@SuppressWarnings("unchecked")
	public synchronized void resolveLaters() {
		//Tratando a possibilidade de mais de um tipo do polimorfismo
//		Set<Entry<Node, List<Type>>> entryLocalType = Collections.synchronizedSet(mapLocalType.entrySet());
//		for (Entry<Node, List<Type>> entry : entryLocalType) {			
//			synchronized (this) {
//				resolveTypes(entry.getKey());
//			}
//		}
//		
		//ja ta chegando aqui vazio
		
		ArrayList<Node> listLocal = new ArrayList<Node>(mapLocalType.keySet());
		for(Node entry: listLocal){
			synchronized (this) {
				resolveTypes(entry);
			}
		}
		

		//
		
		Set<Entry<Node, List<String>>> entryParameterSet = Collections.synchronizedSet(mapResolveParameterInvocationLater.entrySet());
		GraphSingleton graphInstance = GraphSingleton.getInstance();
		for (Entry<Node, List<String>> entry : entryParameterSet) {			
			synchronized (this) {			
				Node node = entry.getKey();
				List<String> list = entry.getValue();
				if(list!=null){
					for(String parameter: list){
						Node parameterNode = mapUnitParameterInvocation.get(parameter);
						if(parameterNode!=null){
							graphInstance.createEdge(parameterNode, node, false);		
														
							List<MethodInvocationHolder> miInvocation = (List<MethodInvocationHolder>)node.getAttribute("tagsInvocation");
							if(miInvocation!=null){
								for (MethodInvocationHolder methodInvocationHolder : miInvocation) {
									if(methodInvocationHolder.getKey().equals(parameter)){
										methodInvocationHolder.setNodeParamInside(parameterNode);
									}
								}
							}
							
						}					
					}	
				}
			}
		}
		
		Set<Entry<Node, String>> unitReturnSet = Collections.synchronizedSet(mapResolveReturnMethodLater.entrySet());
		for (Entry<Node, String> entry : unitReturnSet) {
			synchronized (this) {
				Node node = entry.getKey();
				List<Node> list = mapUnitReturnMethod.get(entry.getValue());
				if(list!=null){
					for(Node returnNode: list){
						if(returnNode!=null){
							graphInstance.createEdge(node, returnNode, false);											
						}
					}
					
				}
			}
		}	
		
		Set<Entry<Node, String>> setOfObjects = Collections.synchronizedSet(mapUnitObjectName.entrySet());
		for (Entry<Node, String> object : setOfObjects) {			
			synchronized (this) {			
				Node node = object.getKey();
				String value = object.getValue();
				if(mapUnitRefToObject.containsValue(value)) {
					Set<Node> keySet = mapUnitRefToObject.keySet();
					for(Node n: keySet){
						String objectname = mapUnitRefToObject.get(n);
						if(value.equals(objectname)){
							graphInstance.createEdge(n, node, false);	
						}
					}
				}
			}
		}
		
	}

	public synchronized void resolveTaintedNode() {
		Set<Entry<String, Node>> unitTaintedSet = Collections.synchronizedSet(mapUnitTaintedNode.entrySet());
		for (Entry<String, Node> entry : unitTaintedSet) {
			synchronized (this) {
				resolveTaintedNodes(entry.getValue());
			}
		}
	}
	
	
	List<Node> visitedTainted = new ArrayList<Node>();
	
	public synchronized void selectTaintedNode(Node node){
		//CODIGO NOVO
		Iterable<Edge> eachEnteringEdge = node.getEachEnteringEdge();
		ArrayList<Edge> newArrayList = Lists.newArrayList(eachEnteringEdge);
		List<Edge> synchronizedList = Collections.synchronizedList(newArrayList);
		for(Edge edge : synchronizedList) {
			
			Boolean flag = edge.getAttribute("isControlEdge"); 
			if(flag == null || flag == false){
				
				Edge taintedEdge = mapUnitTaintedEdge.get(edge.getId());
				if(taintedEdge != null) {
					taintedEdge.setAttribute("tainted", true);
					Node targetNode = edge.getSourceNode();
					targetNode.setAttribute("tainted", true);
				
					if(!visitedTainted.contains(targetNode)){
						visitedTainted.add(targetNode);
						selectTaintedNode(targetNode);
					}
				}				
			}			
		}
	}
	
	public synchronized void selectTaintedNodes(){
		//CODIGO NOVO
		Set<Entry<String, Node>> unitSinkNode = Collections.synchronizedSet(mapSinkNode.entrySet());
		for (Entry<String, Node> entry : unitSinkNode) {
			synchronized (this) {
				entry.getValue().setAttribute("tainted", true);
				selectTaintedNode(entry.getValue());
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public synchronized void colorGraph() {
		GraphSingleton graphInstance = GraphSingleton.getInstance();
		Boolean incluirTrava = ConfigProperties.getInstance().getBoolean("incluir.trava");
		incluirTrava = (incluirTrava == null)? false: incluirTrava;
		StatisticsUtil statistics = StatisticsUtil.getInstance();
		Map<String, Integer> numberOfTaintedNode = statistics.getNumberOfTaintedNode();
		Map<String, Integer> numberOfCommandNode = statistics.getNumberOfCommandNode();
		Map<String, Integer> numberOfPrintoutNode = statistics.getNumberOfPrintoutNode();
		Map<String, Integer> numberOfConditionalNode = statistics.getNumberOfConditionalNode();
		
		if(graphInstance.getAttribute("tainted")) {
			
			Set<Entry<String, Node>> unitTaintedNode = Collections.synchronizedSet(mapUnitTaintedNode.entrySet());
			for (Entry<String, Node> entry : unitTaintedNode) {
				synchronized (this) {
					Node taintedNode = entry.getValue();
					if(taintedNode.getAttribute("tainted")){
						
						
						String prefixFull = taintedNode.toString().split(":")[0];
						
						
						if(taintedNode.getAttribute("condicional")){
							taintedNode.setAttribute("ui.class", "taintedCondicional");
							graphInstance.stepBegins(graphInstance.getStep());
							
							//Contabiliza a quantidade nodos contaminados
							Integer qtde = numberOfConditionalNode.get(prefixFull);
							numberOfConditionalNode.put(prefixFull, (qtde==null)?1:qtde+1);
							
						}
						else if(taintedNode.getAttribute("secret")) {
							taintedNode.setAttribute("ui.class", "taintedSecret");
							graphInstance.stepBegins(graphInstance.getStep());														
						}
						else if(taintedNode.getAttribute("printout")) {
							taintedNode.setAttribute("ui.class", "taintedPrintout");
							graphInstance.stepBegins(graphInstance.getStep());
							
							//Contabiliza a quantidade nodos contaminados
							Integer qtde = numberOfPrintoutNode.get(prefixFull);
							numberOfPrintoutNode.put(prefixFull, (qtde==null)?1:qtde+1);
						}
						else if(taintedNode.getAttribute("commandinjection")) {
							taintedNode.setAttribute("ui.class", "taintedCommandinjection");
							graphInstance.stepBegins(graphInstance.getStep());
							
							//Contabiliza a quantidade nodos contaminados
							Integer qtde = numberOfCommandNode.get(prefixFull);
							numberOfCommandNode.put(prefixFull, (qtde==null)?1:qtde+1);
							
						}
						else {
							taintedNode.setAttribute("ui.class", "tainted");
							graphInstance.stepBegins(graphInstance.getStep());
						}
					
						//Contabiliza a quantidade nodos contaminados
						Integer qtde = numberOfTaintedNode.get(prefixFull);
						numberOfTaintedNode.put(prefixFull, (qtde==null)?1:qtde+1);
						
						
						if(incluirTrava){							
							List<MethodInvocationHolder> miInvocation = (List<MethodInvocationHolder>)taintedNode.getAttribute("tagsInvocation");
							if(miInvocation!=null){
								//Guarda para escrever nas methodsAndClasses depois de colorir o grafo
								nodeWriteClass.add(taintedNode);
							}												
						}
						
					}
					
				}
			}
			
			Set<Entry<String, Edge>> unitTaintedEdge = Collections.synchronizedSet(mapUnitTaintedEdge.entrySet());
			for (Entry<String, Edge> entry : unitTaintedEdge) {
				synchronized (this) {
					Edge taintedEdge = entry.getValue();
					if(taintedEdge.getAttribute("tainted")){
						Boolean isControl = taintedEdge.getAttribute("isControlEdge");
						if(isControl!=null && isControl){
							taintedEdge.setAttribute("ui.class", "taintedControl");
						}else{ 						
							taintedEdge.setAttribute("ui.class", "tainted");
						}
						
						graphInstance.stepBegins(graphInstance.getStep());
					}
				}
			}
			
			
			if(incluirTrava){							
				//codigo pra escrever no programa Java
				for (Node node : nodeWriteClass) {
					List<MethodInvocationHolder> miInvocation = (List<MethodInvocationHolder>)node.getAttribute("tagsInvocation");
					writeOnOriginalClass(miInvocation, node);
				}
				//fim do codigo pra escrever no programa Java
			}
			
		}
	}

	protected void writeOnOriginalClass(
			List<MethodInvocationHolder> miInvocation, Node taintedNode) {
		if(miInvocation!=null){
			
			
			//linha e classe 
			Map<String, Integer> classeMarked = new HashMap<String, Integer>();
			for (MethodInvocationHolder methodInvocationHolder : miInvocation) {
				
				List<Tag> tags = methodInvocationHolder.getTags();
				if (tags!=null && !methodInvocationHolder.isVisited()) {
					
					
					
					Tag tag = tags.get(0);
					int linePosition = 0;
					if(tag instanceof SourceLnNamePosTag){
						SourceLnNamePosTag posTag = (SourceLnNamePosTag) tag;
						linePosition = posTag.startLn();
						
					}else if(tag instanceof LineNumberTag){
						LineNumberTag posTag = (LineNumberTag)tag;
						linePosition = posTag.getLineNumber();
					}else{
						System.out.println("erro ao pegar posicao da invocacao : "+tag.getClass());
						continue;
					}
					String classeEMetodo = methodInvocationHolder.getNodeMethod().toString()
							.split(":")[0];
					String nomeDaClasse = classeEMetodo.split("-")[0];
					nomeDaClasse = nomeDaClasse.replaceAll("\\.",
							"/");
					System.out.println("Alterar a classe "
							+ nomeDaClasse + " na linha " + linePosition);
					ConfigProperties properties = ConfigProperties
							.getInstance();
					String[] dirs = properties.getString(
							"classpath").split(";");
					File fileJava = null;
					boolean founded = false;
					for (String directory : dirs) {
						if (!directory.endsWith(".jar")) {
							fileJava = new File(directory + "/"
									+ nomeDaClasse + ".java");
							if (fileJava.exists()) {
								founded = true;
								break;
							}
						}
					}
					if (founded) {
						
						//Pega informacoes das alteracoes ja feitas no arquivo da classe
						ClassModificationInfo classModificationInfo = mapClassModification.get(nomeDaClasse);
						if(classModificationInfo == null){
							classModificationInfo = new ClassModificationInfo();
						}
						
						//Se a linha mudada foi depois de alguma linha já alterada, inclui o offSet no valor
						int offSet = classModificationInfo.getOffSet();
						if(linePosition<classModificationInfo.getMinorLine()){
							classModificationInfo.setMinorLine(linePosition);
							linePosition += offSet;
						}
																																	
						BufferedReader reader = null;
						BufferedWriter writer = null;
						ArrayList<String> list = new ArrayList<String>();
//						List<String> imports = new ArrayList<String>();
//						List<String> importsToInclude = new ArrayList<String>();
//						int endOfImports = -1;
						try {
							reader = new BufferedReader(
									new FileReader(fileJava));
							String tmp;
							while ((tmp = reader.readLine()) != null) {								
								list.add(tmp);
//								if(tmp.contains("import")){
//									imports.add(tmp);
//								}else{
//									if(endOfImports == -1 && imports.size()>0){
//										endOfImports = list.size();
//									}
//								}
							}
							reader.close();		
							
							Node nodeParamInside = methodInvocationHolder.getNodeParamInside();
							Node nodeMethod = methodInvocationHolder.getNodeMethod();
							if(nodeMethod!=null && nodeParamInside!=null){											
								
								
								Node nodeArgOutside = methodInvocationHolder.getNodeArgOutside();
								Iterable<Edge> eachLeavingEdge = nodeArgOutside.getEachLeavingEdge();
								for(Edge next: eachLeavingEdge){
									Object tainted = next.getAttribute("tainted");
									if(Boolean.TRUE.equals(tainted)){
										String classNode = next.toString().split("-")[0];
										Integer linePositionSaved = classeMarked.get(classNode);//Verifica se já foi incluido essa marcação para não repetir a classe bloqueada
										if(linePositionSaved == null || linePosition != linePositionSaved.intValue() ){											
											String[] array = nodeMethod.toString().split(":");
											String variable = array[1];
											list.add(linePosition - 1 + offSet,
												//	"/*"+ 
														" if("+variable+ " instanceof  " + classNode+ "){ "
															+ "throw new java.lang.Exception(\"A flaw Detected in ["+nodeParamInside.toString().split(":")[0]+"] \");"
													 +  " }"
												//					+ "*/"
															);
										}
										
										classeMarked.put(classNode, linePosition);
//										boolean importFounded = false;
//										for(String imported: imports){
//											if(imported.contains(classNode)){
//												importFounded = true;
//												break;
//											}
//										}
//										
//										if(!importFounded){
//											importsToInclude.add(classNode);
//										}
									}									
								}
							
							}
							
							
							writer = new BufferedWriter(
									new FileWriter(fileJava));
							
							
							for (int i = 0; i < list.size(); i++) {
								String line = list.get(i);																								
								writer.write(line + "\r\n");
//								if(i == endOfImports){
//									for(String toImport: importsToInclude){
//										writer.write("import "+toImport+";\r\n");		
//									}
//								}
							}
							writer.close();
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							if (writer != null) {
								try {
									writer.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
						//Após terminar a alteração acrescer 1 no offSet pois mais uma linha foi inserida no arquivo
						classModificationInfo.setOffSet(++offSet);
						mapClassModification.put(nomeDaClasse, classModificationInfo);
						
					}
				}
				methodInvocationHolder.setVisited(true);
			}

		}
	}			
}