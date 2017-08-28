package br.ufmg.harmonia.inspectorj.stp;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.graphstream.graph.Node;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Hierarchy;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.BinopExpr;
import soot.jimple.Constant;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JTableSwitchStmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.shimple.internal.SPhiExpr;
import soot.tagkit.Tag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import br.ufmg.harmonia.inspectorj.util.ConfigProperties;
import br.ufmg.harmonia.inspectorj.util.GraphSingleton;
import br.ufmg.harmonia.inspectorj.util.InterproceduralResolver;
import br.ufmg.harmonia.inspectorj.util.StatisticsUtil;
import br.ufmg.harmonia.inspectorj.util.holder.ArrayHolder;
import br.ufmg.harmonia.inspectorj.util.holder.ConditionalHolder;
import br.ufmg.harmonia.inspectorj.util.holder.LocalTypeHolder;
import br.ufmg.harmonia.inspectorj.util.holder.MethodHolder;
import br.ufmg.harmonia.inspectorj.util.holder.MethodInvocationHolder;
import br.ufmg.harmonia.inspectorj.util.holder.SwitchHolder;

import com.google.common.collect.Lists;

public class ShimpleBodyTransformer extends BodyTransformer {
	
	protected ConditionalHolder previousIf = null;
	
//	LinLog layout;
//	
//	public ShimpleBodyTransformer(LinLog layout) {
//		this.layout = layout;
//	}
	
	
	@SuppressWarnings("unused")
	private List<String> classesToAnalyse = Arrays.asList(new String[]{
			"br.gov.prodemge.ssc.admin.negocio.usuario.UsuarioRN"
		, "br.gov.prodemge.ssc.admin.negocio.AdminBaseRN"
		, "br.gov.prodemge.ssc.admin.persistencia.usuario.UsuarioDAO"
		, "br.gov.prodemge.ssc.admin.persistencia.AdminBaseDAO"
		, "org.springframework.ldap.core.LdapTemplate"
		, "org.springframework.ldap.core.LdapTemplate"
		, "org.springframework.ldap.core.ContextSource"
		, "org.springframework.ldap.core.support.AbstractContextSource"
		, "org.springframework.ldap.core.support.DirContextAuthenticationStrategy"
		, "org.springframework.ldap.core.support.SimpleDirContextAuthenticationStrategy"
		, "org.springframework.ldap.core.LdapTemplate"
		, "org.springframework.ldap.core.support.DirContextAuthenticationStrategy"
		, "org.springframework.ldap.core.support.LdapContextSource"
		, "javax.naming.ldap.InitialLdapContext"
		, "com.sun.naming.internal.ResourceManager"
		, "org.springframework.ldap.core.support.LookupAttemptingCallback"
		, "javax.naming.InitialContext"
		, "com.sun.jndi.ldap.LdapCtx"
		, "com.sun.jndi.ldap.LdapCtxFactory"
		, "javax.naming.spi.NamingManager"
		, "com.sun.jndi.ldap.LdapClient"
	});
	
	/*
	 * 	at com.sun.jndi.ldap.LdapCtx.mapErrorCode(LdapCtx.java:3087) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtx.processReturnCode(LdapCtx.java:3033) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtx.processReturnCode(LdapCtx.java:2835) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtx.connect(LdapCtx.java:2749) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtx.<init>(LdapCtx.java:316) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtxFactory.getUsingURL(LdapCtxFactory.java:193) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtxFactory.getUsingURLs(LdapCtxFactory.java:211) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtxFactory.getLdapCtxInstance(LdapCtxFactory.java:154) [rt.jar:1.7.0_55]
		at com.sun.jndi.ldap.LdapCtxFactory.getInitialContext(LdapCtxFactory.java:84) [rt.jar:1.7.0_55]
		at javax.naming.spi.NamingManager.getInitialContext(NamingManager.java:684) [rt.jar:1.7.0_55]
		at javax.naming.InitialContext.getDefaultInitCtx(InitialContext.java:307) [rt.jar:1.7.0_55]
		at javax.naming.InitialContext.init(InitialContext.java:242) [rt.jar:1.7.0_55]
		at javax.naming.ldap.InitialLdapContext.<init>(InitialLdapContext.java:153) [rt.jar:1.7.0_55]
		at org.springframework.ldap.core.support.LdapContextSource.getDirContextInstance(LdapContextSource.java:43) [spring-ldap-core-1.3.1.RELEASE.jar:1.3.1.RELEASE]
		at org.springframework.ldap.core.support.AbstractContextSource.createContext(AbstractContextSource.java:254)
	 */
		
	private List<String> methodsToAnalyse = Arrays.asList(new String[]{
		"autenticar",
		"validarUsuarioNoLDAP",
		"getLdapTemplate",
		"authenticate",
		"search",
		"getContext",
		"processContextAfterCreation",
		"executeWithContext",
		"getAuthenticatedEnv",
		"setupEnvironment",
		"getDirContextInstance",
		"InitialLdapContext",
		"getInitialEnvironment",
		"lookup",
		"getURLOrDefaultInitCtx",
		"processReturnCode",
		"<init>",
		"connect",
		"getUsingURL",
		"getUsingURLs",
		"getLdapCtxInstance",
		"getDefaultInitCtx",
		"init",
		"createContext",
		"authenticate"
	});

	protected boolean methodIsContained(String name){ 
		boolean result = false;
		for(String method: methodsToAnalyse){
			if(name.contains(method)){
				result = true;
				break;
			}
		}
		return result;
	}
		
	
	protected Set<Unit> allArrays = new LinkedHashSet<Unit>();
	protected Map<JimpleLocal, LinkedHashSet<JimpleLocal>> mapArrayEqual = new HashMap<JimpleLocal, LinkedHashSet<JimpleLocal>>();
	protected Map<JimpleLocal, LinkedHashSet<ArrayHolder>> mapArrayIndexKnown = new HashMap<JimpleLocal, LinkedHashSet<ArrayHolder>>();
	
	
	protected Map<Value, LocalTypeHolder> mapLocalType = new HashMap<Value, LocalTypeHolder>(); 
	
	//Primeiro método	
	protected void internalTransform(Body body, String phase, Map<String, String> options) {
	//s	SootMethod method = body.getMethod();
		//String nameClass = method.getDeclaringClass().getName();
//		if(classesToAnalyse.contains(nameClass) && methodIsContained(method.getName())){		
		
	//		ConfigProperties prop = ConfigProperties.getInstance();
	//		String methodPropName = prop.getString("fonte.metodo");
	//		String classPropName = prop.getString("fonte.classe");
			
	//		if(name.equals(classPropName)) {
	//			if(method.getName().equals(methodPropName)) {				
		
		
					StatisticsUtil instance = StatisticsUtil.getInstance();
					
					SootClass declaringClass = body.getMethod().getDeclaringClass();
					String name = declaringClass.getName();
					String prefixFull = name+"-"+body.getMethod().getName();
					instance.getMethodTimeStart().put(prefixFull, new Date());//tempo inicial
					instance.getMethodsAndClasses().add(prefixFull);//assinatura do metodo
					instance.getOnlyClasses().add(name);
					
					instance.setNumberOfMethod(instance.getNumberOfMethod()+1);//conta o numero de metodo
					
					PrintStream out = G.v().out;
					out.println(body);
					//				out.println(name+ ": "+method.getName());
					GraphSingleton graph = GraphSingleton.getInstance();
									
					createDependecyGraph(body, graph);						
					
					instance.getMethodTimeEnd().put(prefixFull, new Date());//tempo final
					
	//				CallGraph callGraph = Scene.v().getCallGraph();
	//				Iterator<Edge> edgesOutOfAutenticar = callGraph.edgesOutOf(body.getMethod());
	//				List<Edge> visiteds = new ArrayList<Edge>();
	//				nextCallMethodAnalysis(graph, edgesOutOfAutenticar, visiteds, callGraph);
	//			}
	//		}
//		}

				
//		Set<Entry<JimpleLocal, LinkedHashSet<JimpleLocal>>> entrySet = mapArrayEqual.entrySet();
//		for (Entry<JimpleLocal, LinkedHashSet<JimpleLocal>> entry : entrySet) {
//			System.out.print(entry.getKey()+ " é igual ");
//			LinkedHashSet<JimpleLocal> value = entry.getValue();
//			for (JimpleLocal jimpleLocal : value) {
//				System.out.print(" "+jimpleLocal+",");
//			}
//			System.out.println();
//		}
	}



	//Cria o grafo de dependencias para um método
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private synchronized void createDependecyGraph(Body body, GraphSingleton graph) {
		PatchingChain<Unit> units = body.getUnits();
	
		SootMethod method = body.getMethod();
		String methodName = method.getName();
		SootClass declaringClass = method.getDeclaringClass();
		String name = declaringClass.getName();
		
		
		String prefixFull = name+"-"+method.getName();
		String prefixShort = declaringClass.getShortName()+"-"+method.getName();
		String currentColor = graph.getColor();
		
		int indexOf = currentColor.indexOf('(')+1;
		int lastIndex = currentColor.lastIndexOf(')');
		String[] split = currentColor.substring(indexOf, lastIndex).split(",");
		
		graph.getMethodsOnGraph().add(new MethodHolder(prefixFull, split[0], split[1], split[2]));
		
		ConfigProperties prop = ConfigProperties.getInstance();
		String methodPropName = prop.getString("fonte.metodo");
		String classPropName = prop.getString("fonte.classe");
		
		if(name.equals(classPropName)) {
			if(methodName.equals(methodPropName)) {
				
				String taintedNodeName = prop.getString("fonte");
				
				Node taintedNode = graph.createNode(taintedNodeName, prefixFull, prefixShort, currentColor, null);
				taintedNode.setAttribute("secret", true);
				taintedNode.setAttribute("ui.class", "secret");
				//taintedNode.addAttribute("tags", "TESTE AGORA VAI");
				//includeNumberOfLines(tags, taintedNode);
				
				InterproceduralResolver.getInstance().getMapUnitTaintedNode().put(taintedNode.getId(), taintedNode);			
			}
		}
		

		ExceptionalUnitGraph eUnitGraph = new ExceptionalUnitGraph(body);
		MHGPostDominatorsFinder finderPost = new MHGPostDominatorsFinder(eUnitGraph);
		MHGDominatorsFinder finder = new MHGDominatorsFinder(eUnitGraph);
		Stack<ConditionalHolder> ifStack = new Stack<ConditionalHolder>();
		
		String propertySink = prop.getString("sorvedouro");
		String[] splitSink = propertySink.split(",");
		
//		
		
//			eUnitGraph.getSuccsOf(unit);
//		}
		
		List<ConditionalHolder> listOfswitchDominator = new ArrayList<ConditionalHolder>();
		
		//Verifica se existe um switch
		for(Unit unit : units){
			if(unit instanceof JTableSwitchStmt) {
				Value rightOp = ((JTableSwitchStmt)unit).getKeyBox().getValue();
				if(!(rightOp instanceof Constant)) {
					String leftOp = "switch("+rightOp+")";
					Node localNode = graph.createNode(leftOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
					localNode.setAttribute("condicional", true);
					localNode.setAttribute("ui.class", "condicional");
					ConditionalHolder conditionalSwitch = new ConditionalHolder((JTableSwitchStmt)unit, localNode);
					listOfswitchDominator.add(conditionalSwitch);
				}
			}
			
		}
		SwitchHolder switchHolder = null;
		if(!listOfswitchDominator.isEmpty()){
			switchHolder = new SwitchHolder(finder, finderPost, listOfswitchDominator);
		}
		
		//body.
		
		//JIfStmt ifStmt = null;
		List<Unit> heads = eUnitGraph.getHeads();
		List<Unit> visiteds = new ArrayList<Unit>();
		previousIf = null;
		
		
		//
//		SimpleLocalDefs sld = new SimpleLocalDefs(eUnitGraph);
//		Chain<Local> locals = body.getLocals();
//		for(Local l: locals){
//			List<Unit> defsOf = sld.getDefsOf(l);
//			for(Unit unit: defsOf){
//				System.out.println(unit);
//			}
//		}
		//
		
		
		doAnalysisOnGraph(heads, visiteds, eUnitGraph, graph, method, prefixFull, prefixShort,
				currentColor, finderPost, finder, ifStack, splitSink,
				switchHolder);
		
	}



	@SuppressWarnings("rawtypes")
	protected synchronized void doAnalysisOnGraph(List<Unit> heads, List<Unit> visiteds, ExceptionalUnitGraph eUnitGraph,
			GraphSingleton graph, SootMethod method, String prefixFull, String prefixShort, String currentColor, 
			MHGPostDominatorsFinder finderPost, MHGDominatorsFinder finder, Stack<ConditionalHolder> ifStack,
			String[] splitSink, SwitchHolder switchHolder) {
		
		for(Unit unit: heads){
			
 			if(visiteds.contains(unit)){
				//faz o topo da pilha de if ser o novo previous
				if(!ifStack.isEmpty()){
					//if(!(unit instanceof JGotoStmt)){
						ifStack.peek().incrementCountNop();
						previousIf = ifStack.peek();
						ifStack.pop();																								
					//}
				}
				else{
					previousIf = null;
				}
				continue;
			}else{
				visiteds.add(unit);
			}	
					
			//Quando for JIdentityStmt somente cria o Node
			//Essa é uma estrategia do Soot para criar variveis representando os argumentos do metodo
			if(unit instanceof JIdentityStmt){
				
				JIdentityStmt jIdStmt = (JIdentityStmt)unit;
				Value leftOp = jIdStmt.getLeftOp();
				Node leftNode = graph.createNode(leftOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
				
				//leftNode.get
				//Guarda quais as variaveis são argumentos de métodos. 
				//Será utilizado depois para vincular um metodo ao outro
				if(jIdStmt.getRightOp() instanceof ParameterRef){					
					ParameterRef parameterRef = (ParameterRef)jIdStmt.getRightOp();
					
					if(parameterRef.getType() instanceof ArrayType){
						System.out.println(leftOp + " e' um array");
						allArrays.add(unit);
					}
					
					int index = parameterRef.getIndex();					
					InterproceduralResolver.getInstance().getMapUnitParameterInvocation().put(method.toString()+"@parameter"+index, leftNode);
				}
				else {
					String nameNode = prefixShort +": "+ leftOp;
					InterproceduralResolver.getInstance().getMapUnitObjectName().put(leftNode, nameNode);
					
				}
				
				//Cria aresta de controle se houver condicional
				checkControlEdge(graph, leftNode, ifStack,previousIf);
				
				//Cria aresta de controle se houver switch
				if(switchHolder!=null){					
					switchHolder.setUnit(unit);
					checkControlSwitch( graph, switchHolder, leftNode);
				}
				
			}
			
			//O PROBLEMA ESTA AQUI
			if(unit instanceof JInvokeStmt){
				JInvokeStmt jInvokeStmt = (JInvokeStmt) unit;
				InvokeExpr invokeExpr = jInvokeStmt.getInvokeExpr();				
				List<Value> args = invokeExpr.getArgs();
				if(args!=null && args.size()>0){
					
					
					
					
					
					 
					List<ValueBox> useBoxes = invokeExpr.getUseBoxes();
					Value leftOp = null;
					for(ValueBox valueBox: useBoxes){
						if(!args.contains(valueBox.getValue())){
							leftOp = valueBox.getValue();
							break;
						}
					}
					if(leftOp!=null){
						
						Node localNode = graph.createNode(leftOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));				
						
						
						
						if(invokeExpr instanceof JInterfaceInvokeExpr){
							JInterfaceInvokeExpr jInterfaceExpr = (JInterfaceInvokeExpr)invokeExpr;
							Type type = jInterfaceExpr.getBase().getType();
							Scene v = Scene.v();
							Hierarchy hierarchy = v.getActiveHierarchy();
							SootClass sootInterface = v.getSootClass(type.getEscapedName());
							if(sootInterface.isInterface()){								
								List<SootClass> implementersOf = hierarchy.getImplementersOf(sootInterface);
								for (SootClass sootClass : implementersOf) {
									
									System.out.println(sootClass);
									
									LocalTypeHolder holder =  mapLocalType.get(leftOp);
									if(holder==null){
										holder = new LocalTypeHolder();
										holder.setUnit(jInvokeStmt);
										holder.setNodeRefered(localNode);
										List<Type> types = holder.getTypes();
										if(types == null){
											holder.setTypes(new ArrayList<Type>());
										}
									}
									holder.getTypes().add(sootClass.getType());
									mapLocalType.put(leftOp, holder);
								}
							}
							
						}
						
						
						
						//Cria aresta de controle se houver condicional
						checkControlEdge(graph, localNode, ifStack,previousIf);
						
						//Cria aresta de controle se houver switch
						if(switchHolder!=null){							
							switchHolder.setUnit(unit);
							checkControlSwitch(graph, switchHolder, localNode);
							
						}
						
						//Cria um mapa que vai guardar todos os parametros e todos os metodos que contem esses parametros
						Map<Node, String> mapParametersToKeys = new HashMap<Node, String>();
						//
						for(int i = 0; i< args.size(); i++){
							if(!(args.get(i) instanceof Constant)) {
								Value rightOp = args.get(i);									
								createDependencesEdges(unit, leftOp, localNode, rightOp, prefixFull, prefixShort, currentColor, switchHolder, method);			
								
								
								Node nodeRight = graph.createNode(rightOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));	
								//getUnitAttributes(unit.getTags(), nodeRight);
								
								
								String key = invokeExpr.getMethodRef().toString()+"@parameter"+i;
								LocalTypeHolder holder =  mapLocalType.get(leftOp);
								if(holder==null){
									holder = new LocalTypeHolder();
									holder.setNodeRefered(nodeRight);
								}
						
								
								
								List<Type> list = holder.getTypes();//mapLocalType.get(leftOp); 
								if(list!=null){
									for(Type type: list){
										key = "<"+type+key.substring(key.indexOf(":"));//Troca o tipo de 
										
										Node nodeArg = InterproceduralResolver.getInstance().getMapUnitParameterInvocation().get(key);
										if(nodeArg!=null){
											GraphSingleton.getInstance().createEdge(nodeArg, nodeRight, false);
											getUnitAttributes(unit.getTags(), nodeArg);
											
											
										}else{
											List<String> list2 = InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().get(nodeRight);
											if(list2!=null){
												list2.add(key);
												InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(nodeRight, list2);
											}
											else{
												list2 = new ArrayList<String>();
												list2.add(key);
												InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(nodeRight, list2);
											}
										}
										MethodInvocationHolder miHolder = new MethodInvocationHolder();
										miHolder.setNodeArgOutside(nodeRight);
										miHolder.setNodeParamInside(nodeArg);
										miHolder.setNodeMethod(localNode);
										miHolder.setUnit(unit);
										miHolder.setTags(unit.getTags());
										miHolder.setKey(key);
										
										List<MethodInvocationHolder> attribute = nodeRight.getAttribute("tagsInvocation");
										if(attribute == null){
											attribute = new ArrayList<MethodInvocationHolder>();
										}
										attribute.add(miHolder);
										nodeRight.addAttribute("tagsInvocation", attribute);
									}
								}								
								else{
									Node nodeArg = InterproceduralResolver.getInstance().getMapUnitParameterInvocation().get(key);
									if(nodeArg!=null){
										GraphSingleton.getInstance().createEdge(nodeArg, nodeRight, false);
										//createEdgeInvocation.addAttribute("tagsInvocation", unit.getTags());
										getUnitAttributes(unit.getTags(), nodeArg);
										
										
									}else{
										//nodeRight.addAttribute("tagsInvocation", unit.getTags());
										List<String> list2 = InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().get(nodeRight);
										if(list2!=null){
											list2.add(key);
											InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(nodeRight, list2);
										}
										else{
											list2 = new ArrayList<String>();
											list2.add(key);
											InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(nodeRight, list2);
										}
									}	
									MethodInvocationHolder miHolder = new MethodInvocationHolder();
									miHolder.setNodeArgOutside(nodeRight);
									miHolder.setNodeParamInside(nodeArg);
									miHolder.setNodeMethod(localNode);
									miHolder.setUnit(unit);
									miHolder.setTags(unit.getTags());
									miHolder.setKey(key);
									
									List<MethodInvocationHolder> attribute = nodeRight.getAttribute("tagsInvocation");
									if(attribute == null){
										attribute = new ArrayList<MethodInvocationHolder>();
									}
									attribute.add(miHolder);
									nodeRight.addAttribute("tagsInvocation", attribute);
								}								
								String key2 = key.substring(key.indexOf(":"));
								mapParametersToKeys.put(nodeRight, key2);
							}						
						}
						//
						InterproceduralResolver.getInstance().getMapSubtypes().put(localNode, mapParametersToKeys);
						//
						
					}
				}
			}
			
			//Quando for JAssignStmt criar uma aresta de dependencia
			if (unit instanceof JAssignStmt) {
				JAssignStmt stmt = (JAssignStmt) unit;
				Value leftOp = stmt.getLeftOp();
				
				Node localNode = null;
				//Cria aresta de dependencia do array para a posicao do Array				
				if(leftOp instanceof JArrayRef){
					JArrayRef jArrayRef = (JArrayRef) leftOp;										
					
					//Cria node para o objeto da posicao do array
					localNode = GraphSingleton.getInstance().createNode(jArrayRef, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
					
					//Cria aresta de controle se houver condicional
					checkControlEdge(graph, localNode, ifStack, previousIf);
					
					//Cria aresta de controle se houver switch
					if(switchHolder!=null){						
						switchHolder.setUnit(unit);
						checkControlSwitch(graph, switchHolder, localNode);						
					}
					//Cria aresta do objeto base para o objeto da posicao do array
					ValueBox baseBox = jArrayRef.getBaseBox();
					Node nodeBaseArray = graph.createNode(baseBox.getValue(), prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
					
					graph.createEdge(localNode, nodeBaseArray, false);
					
					//Cria aresta do indice para o objeto da posicao do array					
					ValueBox indexBox = jArrayRef.getIndexBox();
					Value value = indexBox.getValue();
					Node nodeIndex = null;
					if(!(value instanceof Constant)){							
						nodeIndex = graph.createNode(indexBox.getValue(), prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
						graph.createEdge(localNode, nodeIndex, false);
				//		//getUnitAttributes(unit.getTags(), nodeIndex);
					}else{
						System.out.println("Descobrir o que fazer nesse caso");
					}
					
					
					
//					codigo novo daqui pra baixo
					
					
//					Guarda as informacoes dos arrays para achar as posicoes iguais
					ArrayHolder arrayHolder = new ArrayHolder();
					arrayHolder.setUnit(unit);
					arrayHolder.setLocal((JimpleLocal)baseBox.getValue());
					arrayHolder.setNodeBase(nodeBaseArray);
					arrayHolder.setNodeIndex(nodeIndex);
					arrayHolder.setNodeJimplePosition(localNode);
					
									
					
					PatchingChain<Unit> units = method.getActiveBody().getUnits();
					IntConstant rightOp = extractConstantDefinition(indexBox.getValue(), units);
					arrayHolder.setConstant(rightOp);
					
					
					JimpleLocal jimpleLocal = (JimpleLocal)baseBox.getValue();
					LinkedHashSet<ArrayHolder> linkedHashSet = mapArrayIndexKnown.get(jimpleLocal);
					if(linkedHashSet==null){
						linkedHashSet = new LinkedHashSet<ArrayHolder>();
					}
					linkedHashSet.add(arrayHolder);
					mapArrayIndexKnown.put(jimpleLocal, linkedHashSet);
					

					
					
				}else
				//Cria aresta para referencias de campos de classe
				if(leftOp instanceof JInstanceFieldRef){
					JInstanceFieldRef jInsFieldRef = (JInstanceFieldRef)leftOp;
					Value baseValue = jInsFieldRef.getBase();
					SootFieldRef fieldRef = jInsFieldRef.getFieldRef();
					String nodeName  = baseValue.toString()+"."+fieldRef.resolve().getName();
					SootClass declaringInstanceClass = fieldRef.declaringClass();
					
					localNode = graph.createNode(nodeName, declaringInstanceClass.getName(), declaringInstanceClass.getShortName(), currentColor, getUnitAttributes(unit.getTags(), null));
					
					//Cria aresta de controle se houver condicional
					checkControlEdge(graph, localNode, ifStack, previousIf);
					
					//Cria aresta de controle se houver switch
					if(switchHolder!=null){
						switchHolder.setUnit(unit);
						checkControlSwitch(graph, switchHolder, localNode);
					}
					
					//Cria uma aresta do campo para a classe base
					Node nodeBase = graph.createNode(baseValue, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
					graph.createEdge(nodeBase, localNode, false);	
					
				}else{
					
					//Se for array deve encontrar quais são iguais.
					if(leftOp.getType() instanceof ArrayType && leftOp instanceof JimpleLocal){
						System.out.println(leftOp + " e' um array");
						allArrays.add(unit);
						
						LinkedHashSet<JimpleLocal> linkedHashSet = mapArrayEqual.get(leftOp);
						if(linkedHashSet == null){
							linkedHashSet = new LinkedHashSet<JimpleLocal>();						
						}
												
						Value jimpleRightOp = stmt.getRightOp();
						if(jimpleRightOp instanceof JimpleLocal){
							linkedHashSet.add((JimpleLocal)jimpleRightOp);
							//Se o operador da direita for uma variavel do tipo array pega seu valor e atualiza todos as outras referencias.
							LinkedHashSet<JimpleLocal> linkedHashSet2 = mapArrayEqual.get(jimpleRightOp);
							if(linkedHashSet2!=null){								
								for (JimpleLocal jimpleLocal : linkedHashSet2) {
									mapArrayEqual.get(jimpleLocal).add((JimpleLocal)leftOp);
									linkedHashSet.add(jimpleLocal);
								}
								linkedHashSet2.add((JimpleLocal)leftOp);
							}
						}
						
						mapArrayEqual.put((JimpleLocal)leftOp, linkedHashSet);
					}
					
					
					
					localNode = graph.createNode(leftOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
				
					//Cria aresta de controle se houver condicional
					checkControlEdge(graph,localNode, ifStack, previousIf);
					//Cria aresta de controle se houver switch
					if(switchHolder!=null){
						switchHolder.setUnit(unit);
						checkControlSwitch(graph, switchHolder, localNode);
					}
				}


				Value rightOp = stmt.getRightOp();
				
				//COLOCA AQUI
				//FALTA COLOCAR QUANDO O OBJETO RECEBER UMA FUNÇÃO PHI
				if(!leftOp.getType().equals(rightOp.getType())){
					System.out.println("Entrei");					
					
					LocalTypeHolder holder = mapLocalType.get(leftOp);
					if(holder==null){
						holder = new LocalTypeHolder();
						holder.setNodeRefered(localNode);
						holder.setUnit(unit);
					}
					List<Type> list = holder.getTypes();//mapLocalType.get(leftOp);
					if(list == null){
						list = new ArrayList<Type>();
						//list.add(rightOp.getType());
						//mapLocalType.put(leftOp, list);
					}
					//list = new ArrayList<Type>();
					list.add(rightOp.getType());
					holder.setTypes(list);
					mapLocalType.put(leftOp, holder);
					
					
					//se o localNode(leftOp) ja existir e ja tiver aresta saindo dele, entao temos que colcoar ele
					//em um mapa para resolver depois, em que eu vou mudar o tipo do no pra onde minha aresta vai e
					//assim por diante. se eu chegar em um no que fez uma invocacao, entao tenho que criar aresta pro novo
					//tipo que eu achei
					
					Iterable<org.graphstream.graph.Edge> eachLeavingEdge = localNode.getEachLeavingEdge();
					ArrayList<org.graphstream.graph.Edge> newArrayList = Lists.newArrayList(eachLeavingEdge);
					if(newArrayList != null){
						List<org.graphstream.graph.Edge> synchronizedList = Collections.synchronizedList(newArrayList);
						for(org.graphstream.graph.Edge edge: synchronizedList) {
							Node targetNode = edge.getTargetNode();
							LocalTypeHolder holder2 = mapLocalType.get(leftOp);
							if(holder2 == null) {
								holder2 = new LocalTypeHolder();
								holder2.setNodeRefered(targetNode);
								holder2.setUnit(unit);
							}
							list = holder2.getTypes();//mapLocalType.get(leftOp);
							holder2.setTypes(list);
							InterproceduralResolver.getInstance().getMapLocalType().put(targetNode, holder2);
						}
					}
				}
				
				
				//Se é um PrintOut
				Type type = rightOp.getType();
				if((type.toString()).equals("java.io.PrintStream")) {
					
					for(String typeSink : splitSink) {
						if(typeSink.equals("printout")) {
							localNode.setAttribute("sink", true);
							localNode.setAttribute("printout", true);
							localNode.setAttribute("ui.class", "printout");
							break;
						}
					}				
				}
				
				if(switchHolder!=null){					
					switchHolder.setUnit(unit);
				}
				createDependencesEdges(unit, leftOp, localNode, rightOp, prefixFull, prefixShort, currentColor, switchHolder, method);
				
				if(rightOp instanceof JNewExpr){
					System.out.println(unit);
				}
				
				
				//createDependencesEdges(unit, localNode, rightOp, prefixFull, prefixShort, currentColor, conditional, switchHolder, method);
				
			}
			
			
			
			//Se é um condicional "switch"
			if(unit instanceof JTableSwitchStmt) {
				JTableSwitchStmt jSwitch = (JTableSwitchStmt)unit;
				Value rightOp = jSwitch.getKeyBox().getValue();
				
				
				
				if(!(rightOp instanceof Constant)) {
					String leftOp = "switch("+rightOp+")";
					Node localNode = graph.createNode(leftOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
					localNode.setAttribute("condicional", true);
					localNode.setAttribute("ui.class", "condicional");
					

					
					//Cria aresta de controle se houver condicional
					checkControlEdge(graph, localNode, ifStack, previousIf);
					
					for(String typeSink : splitSink) {
						if(typeSink.equals("condicional")) {
							localNode.setAttribute("sink", true);
							break;
						}
					}
					if(switchHolder!=null){						
						switchHolder.setUnit(unit);
					}
					createDependencesEdges(unit, null, localNode, rightOp, prefixFull, prefixShort, currentColor, switchHolder, method);	
				}
			}
			
			
			if(unit instanceof JReturnStmt){				
				JReturnStmt last = (JReturnStmt)unit;			
				Node returnNode = graph.createNode(last.getOp(), prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
				
				//Cria aresta de controle se houver condicional
				checkControlEdge(graph, returnNode, ifStack,previousIf);
				
				//Cria aresta de controle se houver switch
				if(switchHolder!=null){
					switchHolder.setUnit(unit);
					checkControlSwitch(graph, switchHolder, returnNode);
				}
				
				List<Node> list = InterproceduralResolver.getInstance().getMapUnitReturnMethod().get(method.toString());
				if(list!=null){
					list.add(returnNode);
					InterproceduralResolver.getInstance().getMapUnitReturnMethod().put(method.toString(), list);
				}
				else{
					List<Node> newlist = new ArrayList<Node>();
					newlist.add(returnNode);
					InterproceduralResolver.getInstance().getMapUnitReturnMethod().put(method.toString(), newlist);
				}			
				
				if(!ifStack.isEmpty()){
					ifStack.peek().incrementCountNop();
					if(ifStack.peek().getCountNop() == 2){
						previousIf = ifStack.peek();
						ifStack.pop();
					}					
				}
			}
						
			//Se é um condicional "if"
			if(unit instanceof JIfStmt) {
				JIfStmt jIfStmt = (JIfStmt)unit;
				
				Value leftOp = jIfStmt.getCondition();				
				Node localNode = graph.createNode(leftOp, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));
				getUnitAttributes(unit.getTags(), localNode);
				
				//Cria aresta de controle se houver condicional
				checkControlEdge(graph, localNode, ifStack, previousIf);
				
//				if(ifStack.size()>0){
//					previousIf = ifStack.peek();
//				}
				
				//Guarda o IF para criar as arestas de controle
				ifStack.push(new ConditionalHolder(jIfStmt, localNode));
				
				
				localNode.setAttribute("condicional", true);
				localNode.setAttribute("ui.class", "condicional");
				
				//Cria aresta de controle se houver switch
				if(switchHolder!=null){
					switchHolder.setUnit(unit);
					checkControlSwitch(graph, switchHolder, localNode);
				}
				
				for(String typeSink : splitSink) {
					if(typeSink.equals("condicional")) {
						localNode.setAttribute("sink", true);
						break;
					}
				}
						
				List<ValueBox> useBoxes = jIfStmt.getConditionBox().getValue().getUseBoxes();
				
				
				for(int i=0; i<useBoxes.size(); i++){
					Value rightOp = useBoxes.get(i).getValue();
					if(!(rightOp instanceof Constant)){
//						createDependencesEdges(localNode, rightOp, prefixFull, prefixShort, currentColor, switchHolder);			
						createDependencesEdges(unit, leftOp, localNode, rightOp, prefixFull, prefixShort, currentColor, switchHolder, method);			
					}
				}				

			}
			
				
			//
			if(unit instanceof JGotoStmt){
				if(!ifStack.isEmpty()){
					ifStack.peek().incrementCountNop();
					if(ifStack.peek().getCountNop() == 2){
						previousIf = ifStack.peek();
					}
				}
			}
			
			//
			if(unit instanceof JReturnVoidStmt){
				if(!ifStack.isEmpty()){
					ifStack.peek().incrementCountNop();
					if(ifStack.peek().getCountNop() == 2){
						previousIf = ifStack.peek();
						ifStack.pop();
					}					
				}
			}
			
			List<Unit> succsOf = eUnitGraph.getSuccsOf(unit);
			if(succsOf!=null && !succsOf.isEmpty()){
				doAnalysisOnGraph(succsOf, visiteds, eUnitGraph, graph, method, 
						prefixFull, prefixShort, currentColor, finderPost, finder, 
						ifStack, splitSink, switchHolder);
			}
		}
	}



	protected IntConstant extractConstantDefinition(Value indexBox, PatchingChain<Unit> units) {
		IntConstant rightOp = null;
		for (Unit valueBox : units) {
			if(valueBox instanceof JAssignStmt){ 
				JAssignStmt jAssignStmt = (JAssignStmt) valueBox;
				if(jAssignStmt.getLeftOp().equals(indexBox)){
					Value intRightOp = jAssignStmt.getRightOp();
					if(!(intRightOp instanceof IntConstant)){
						rightOp = extractConstantDefinition(intRightOp, units);						
					}else{						
						rightOp = (IntConstant)intRightOp;						
					}
					break;					
				}
			}						
		}
		return rightOp;
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void checkControlSwitch(GraphSingleton graph,SwitchHolder switchHolder, Node rightNode) {
		
		
		if(switchHolder!=null && switchHolder.getListOfSwitch()!=null){
			List<ConditionalHolder>  listOfswitchDominator = switchHolder.getListOfSwitch();
			MHGDominatorsFinder finder = switchHolder.getFinder();
			MHGPostDominatorsFinder finderPost = switchHolder.getFinderPost();
			Unit target = switchHolder.getUnit();
			for(ConditionalHolder conditional: listOfswitchDominator){
				Object immediateDominator = finderPost.getImmediateDominator(conditional.getjSwitchStmt());
				if(finder.isDominatedBy(target, conditional.getjSwitchStmt()) && immediateDominator!=null &&
						!immediateDominator.equals(target)){
					//createDependencesEdges(conditional.getSwitchNode(), rightOp, prefixFull, prefixShort, currentColor, conditional, listOfswitchDominator);
					graph.createControlEdge(rightNode,conditional.getSwitchNode());
				}
			}
		}
	}


	protected Map<String, Object> getUnitAttributes(List<Tag> tags, Node leftNode) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		if(tags!=null && !tags.isEmpty()){
			map.put("tags", tags);
		}
		return map;
	}

	protected synchronized void checkControlEdge(GraphSingleton graph, Node leftNode, Stack<ConditionalHolder> ifStack, ConditionalHolder previousIf) {
		if(previousIf != null){
			graph.createControlEdge(leftNode, previousIf.getIfNode());
		}
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected synchronized ConditionalHolder getConditionalDominator(MHGDominatorsFinder finder, MHGPostDominatorsFinder finderPost, 
																				Stack<ConditionalHolder> ifStack, Unit unit) {
		ConditionalHolder conditional = null;
		if(ifStack.size()>0){
			conditional = ifStack.peek();			
			JIfStmt ifStmt = conditional.getjIfStmt();
			Object immediateDominator = finderPost.getImmediateDominator(ifStmt);
			boolean dominatedBy = finderPost.isDominatedBy(unit, ifStmt);
			boolean b = immediateDominator!=null;
			boolean equals = immediateDominator.equals(unit);
			if(dominatedBy && b && !equals){
				//G.v().out.println(ifStmt + " ---> dominate --> " + unit);
				return conditional;
			}else{
				ifStack.pop();
				conditional = getConditionalDominator(finder, finderPost, ifStack, unit);
			}
		}
		return conditional;
	}


	
	
	@SuppressWarnings("unchecked")
	private synchronized void createDependencesEdges(Unit unit, Value leftOp, Node localNode, Value rightOp, String prefixFull, 
			String prefixShort, String currentColor, SwitchHolder switchHolder, SootMethod method) {
		GraphSingleton graph = GraphSingleton.getInstance();
		List<Tag> tags = (List<Tag>) graph.getMap().get(localNode);
		Map<String, Object> map = new HashMap<String, Object>();
		if(tags!=null){			
			map.put("tags", tags);
		}
		//Cria aresta para campos de classe
		if(rightOp instanceof JInstanceFieldRef){
			JInstanceFieldRef jInsFieldRef = (JInstanceFieldRef)rightOp;
			Value baseValue = jInsFieldRef.getBase();
			SootFieldRef fieldRef = jInsFieldRef.getFieldRef();
			String rightNodeName  = baseValue.toString()+"."+fieldRef.resolve().getName();
			SootClass declaringInstanceClass = fieldRef.declaringClass();
			Node nodeField = graph.createNode(rightNodeName, declaringInstanceClass.getName(), declaringInstanceClass.getShortName(), currentColor, map);
			
			
			//cria uma aresta do campo para a classe base
			Node nodeBase = graph.createNode(baseValue, prefixFull, prefixShort, currentColor, map);
			graph.createEdge(nodeBase, nodeField, false);
			
			
			graph.createEdge(localNode, nodeField, false);
			
			//Cria aresta de controle se houver switch			
			//checkControlSwitch(graph, switchHolder, nodeBase);
			
		}//else		
		//Cria aresta para invocacao de metodos de classe	
		if(rightOp instanceof JVirtualInvokeExpr){
			JVirtualInvokeExpr jVirtualInvokeExpr = (JVirtualInvokeExpr)rightOp;
			Value base = jVirtualInvokeExpr.getBase();
			//se tiver base
			if(base!=null){
								
				Node nodeBase = graph.createNode(base, prefixFull, prefixShort, currentColor, map);	
				if(jVirtualInvokeExpr.getMethodRef().getSignature().equals("<java.lang.Runtime: java.lang.Process exec(java.lang.String)>")){
					ConfigProperties prop = ConfigProperties.getInstance();
					String propertySink = prop.getString("sorvedouro");
					String[] splitSink = propertySink.split(",");
					//commandinjection
					for(String typeSink : splitSink) {
						if(typeSink.equals("commandinjection")) {
							localNode.setAttribute("sink", true);
							localNode.setAttribute("commandinjection", true);
							localNode.setAttribute("ui.class", "commandinjection");
							break;
						}
					}	
					
				}
				
				List<Value> args = jVirtualInvokeExpr.getArgs();
				if(args!=null){
					for(Value arg: args){
						if(!(arg instanceof Constant)){
							createDependencesEdges(unit, leftOp, nodeBase, arg, prefixFull, prefixShort, currentColor, switchHolder, method);	
						}
					}					
				}
				graph.createEdge(localNode, nodeBase, false);
				
				
			}
			
			else {
				List<Value> args = jVirtualInvokeExpr.getArgs();	
				if(args!=null){
					graph.createEdges(leftOp, localNode, args, prefixFull, prefixShort, currentColor, map, mapLocalType);										
				}
			}
				
			SootMethodRef methodRef = jVirtualInvokeExpr.getMethodRef();
			includeReturnNode(localNode, methodRef);
			//Cria aresta de controle se houver switch			
			//checkControlSwitch(graph, switchHolder, nodeBase);
		}//else
		
		
		
		
		//cria aresta de dependencia a partir de uma variavel utilizada em uma invocacao estatica  				
		if(rightOp instanceof JStaticInvokeExpr){
			JStaticInvokeExpr jStaticExpr = (JStaticInvokeExpr)rightOp;
			List<Value> args = jStaticExpr.getArgs();
			
			//considerar tambem se eu tiver algo tipo temp = aux.staticInvoke(a,b)
			
			includeReturnNode(localNode, jStaticExpr.getMethodRef());
			
			//
			for(int i = 0; i< args.size(); i++){
				if(!(args.get(i) instanceof Constant)) {
					Value nameNode = args.get(i);									
					
					Node node = graph.createNode(nameNode, prefixFull, prefixShort, currentColor, getUnitAttributes(unit.getTags(), null));	
					
					String key = jStaticExpr.getMethodRef().toString()+"@parameter"+i;
					
					Node nodeArg = InterproceduralResolver.getInstance().getMapUnitParameterInvocation().get(key);
					if(nodeArg!=null){
						GraphSingleton.getInstance().createEdge(nodeArg, node, false);
						getUnitAttributes(unit.getTags(), nodeArg);
					}else{
						List<String> list2 = InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().get(node);
						if(list2!=null){
							list2.add(key);
							InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(node, list2);
						}
						else{
							list2 = new ArrayList<String>();
							list2.add(key);
							InterproceduralResolver.getInstance().getMapResolveParameterInvocationLater().put(node, list2);
						}
					}	
				}						
			}
			//
			graph.createEdges(leftOp, localNode, args, prefixFull, prefixShort, currentColor, map, mapLocalType);		
			
			
		}//else
		//cria aresta de dependencia para o Array e a posicao do Array				
		if(rightOp instanceof JArrayRef){
			JArrayRef jArrayRef = (JArrayRef) rightOp;										
			
			//Cria aresta para o objeto da posicao do array
			Node nodeArray = graph.createNode(jArrayRef, prefixFull, prefixShort, currentColor, map);
			graph.createEdge(localNode, nodeArray, false);
			
			//Cria aresta do objeto base para o objeto da posicao array
			ValueBox baseBox = jArrayRef.getBaseBox();
			Node nodeBaseArray = graph.createNode(baseBox.getValue(), prefixFull, prefixShort, currentColor, map);
						
			graph.createEdge(nodeArray, nodeBaseArray, false);
			//Cria aresta de controle se houver switch			
			//checkControlSwitch(graph, switchHolder, nodeBaseArray);
			
			//Cria aresta do indice para o objeto da posicao do array					
			ValueBox indexBox = jArrayRef.getIndexBox();
			Value value = indexBox.getValue();
			Node nodeIndex = null;
			if(!(value instanceof Constant)){				
				nodeIndex = graph.createNode(value, prefixFull, prefixShort, currentColor, map);
				graph.createEdge(nodeArray, nodeIndex, false);	
				//Cria aresta de controle se houver switch			
				//checkControlSwitch(graph, switchHolder, nodeIndex);
			}else{
				//não sei o que fazer aqui
			}
			
			
//			Guarda as informacoes dos arrays para achar as posicoes iguais
			ArrayHolder arrayHolder = new ArrayHolder();
			arrayHolder.setUnit(unit);
			arrayHolder.setLocal((JimpleLocal)baseBox.getValue());
			arrayHolder.setNodeBase(nodeBaseArray);
			arrayHolder.setNodeIndex(nodeIndex);
			arrayHolder.setNodeJimplePosition(nodeArray);
			
			
			PatchingChain<Unit> units = method.getActiveBody().getUnits();
			IntConstant rightConstOp = extractConstantDefinition(indexBox.getValue(), units);
			arrayHolder.setConstant(rightConstOp);
			
			
			
			JimpleLocal baseJimpleLocal = (JimpleLocal)baseBox.getValue();
			LinkedHashSet<ArrayHolder> linkedHashSet = mapArrayIndexKnown.get(baseJimpleLocal);
			if(linkedHashSet==null){
				linkedHashSet = new LinkedHashSet<ArrayHolder>();
			}
			linkedHashSet.add(arrayHolder);
			mapArrayIndexKnown.put(baseJimpleLocal, linkedHashSet);
			
			
			
			//Verifica se vai criar uma aresta para cada posicao
			LinkedHashSet<JimpleLocal> listArrayEquivalent = mapArrayEqual.get(baseJimpleLocal);
			if(listArrayEquivalent!=null){				
				for (JimpleLocal jimpleLocal : listArrayEquivalent) {
					LinkedHashSet<ArrayHolder> listaPositionEquivalent = mapArrayIndexKnown.get(jimpleLocal);
					if(listaPositionEquivalent!=null && !listaPositionEquivalent.isEmpty()){
						for (ArrayHolder arrayHolderPair : listaPositionEquivalent) {
							IntConstant constantIndex = arrayHolderPair.getConstant();
							if(constantIndex!= null && constantIndex.equals(arrayHolder.getConstant())){
								Node nodeJimplePosition = arrayHolderPair.getNodeJimplePosition();
								graph.createEdge(nodeArray, nodeJimplePosition, false);
								graph.createEdge(nodeJimplePosition, nodeArray, false);
							}
						}
					}
				}
			}
			
		
			
		}//else
		//Cria aresta de dependencia para uma variavel local				
		if (rightOp instanceof JimpleLocal) {
			JimpleLocal jLocal = (JimpleLocal) rightOp;
			
			//Conferir se o unit é um Invoce e se o método é do tipo Exec.
			//se sim incluir que está contaminado
			if(unit instanceof JInvokeStmt) {
				if(((JInvokeStmt)unit).getInvokeExpr().getMethodRef().getSignature().equals("<java.lang.Runtime: java.lang.Process exec(java.lang.String)>")){
					ConfigProperties prop = ConfigProperties.getInstance();
					String propertySink = prop.getString("sorvedouro");
					String[] splitSink = propertySink.split(",");
					//commandinjection
					for(String typeSink : splitSink) {
						if(typeSink.equals("commandinjection")) {
							localNode.setAttribute("sink", true);
							localNode.setAttribute("commandinjection", true);
							localNode.setAttribute("ui.class", "commandinjection");
							break;
						}
					}	
					
				}				
			}
			
			
			
			
			if(jLocal.getType() instanceof ArrayType){
				
//				LinkedHashSet<JimpleLocal> linkedHashSet = mapArrayEqual.get(jLocal);
//				if(linkedHashSet==null){
//					linkedHashSet = new LinkedHashSet<JimpleLocal>();
//				}
//				
//				JAssignStmt arrayStmt = (JAssignStmt)unit;
//				JimpleLocal leftOp = (JimpleLocal)arrayStmt.getLeftOp();
//				
//				linkedHashSet.add(jLocal);
//				
//				
//			
//				
//				mapArrayEqual.put(jLocal, linkedHashSet);
//				
				
			}
			
			Node dependencyNode = graph.createNode(jLocal, prefixFull, prefixShort, currentColor, map);
			graph.createEdge(localNode, dependencyNode, false);
			
			//Se rightOp pode ter outro tipo
			LocalTypeHolder holder = mapLocalType.get(rightOp);
			
			List<Type> list = holder!=null?holder.getTypes():null;//mapLocalType.get(rightOp);
			if(list!=null){
				LocalTypeHolder holderNew = new LocalTypeHolder(localNode, list, unit);
				mapLocalType.put(leftOp, holder);
			}
			else{
				Iterable<org.graphstream.graph.Edge> eachLeavingEdge = localNode.getEachLeavingEdge();
				ArrayList<org.graphstream.graph.Edge> newArrayList = Lists.newArrayList(eachLeavingEdge);
				if(newArrayList != null){
					List<org.graphstream.graph.Edge> synchronizedList = Collections.synchronizedList(newArrayList);
					for(org.graphstream.graph.Edge edge: synchronizedList) {
						Node targetNode = edge.getTargetNode();
						LocalTypeHolder holder2 = mapLocalType.get(leftOp);
						//list = mapLocalType.get(leftOp);
						if(holder2!=null){
							InterproceduralResolver.getInstance().getMapLocalType().put(targetNode, new LocalTypeHolder(targetNode,holder2.getTypes(),unit));							
						}else{
							List<Type> newList = new ArrayList<Type>();
							newList.add(leftOp.getType());
							holder2 = new LocalTypeHolder(targetNode, newList, unit);
							
							InterproceduralResolver.getInstance().getMapLocalType().put(targetNode, holder2);	
						}
					}
				}
			}
			
						
		}//else
		//Cria arestas de dependencias para variaveis da função phi
		if (rightOp instanceof SPhiExpr) {
			//Verifica se o leftOp é um objeto de uma das methodsAndClasses analisadas			
			SPhiExpr phiExpr = (SPhiExpr) rightOp;
			List<Value> values = phiExpr.getValues();
			graph.createEdges(leftOp, localNode, values, prefixFull, prefixShort, currentColor, map, mapLocalType);				
		}//else
		//Cria arestas de dependencias para operacoes binarias
		if(rightOp instanceof BinopExpr){	
			BinopExpr expr = (BinopExpr) rightOp;
			Value op1 = expr.getOp1();
			if (op1 instanceof JimpleLocal) {
				Node dependencyNode = graph.createNode(op1, prefixFull, prefixShort, currentColor, map);
				graph.createEdge(localNode, dependencyNode, false);
			}
			Value op2 = expr.getOp2();
			if (op2 instanceof JimpleLocal) {
				Node dependencyNode = graph.createNode(op2, prefixFull, prefixShort, currentColor, map);
				graph.createEdge(localNode, dependencyNode, false);
			}
		}
		//Se é um objeto de uma classe
		if(rightOp instanceof JNewExpr){
			String className = ((JNewExpr) rightOp).getBaseType().getClassName();
			System.out.println(className);
			
			//Pega o nome do pacote que esta sendo analisado
			ConfigProperties prop = ConfigProperties.getInstance();
			String classPropName = prop.getString("fonte.classe");
			
			String[] packageList = classPropName.split("\\."); 
			int lenghtP = packageList.length;
			String packageName = "";
			for(int i=0; i<lenghtP-1; i++){
				packageName = packageName+""+packageList[i] +".";
			}
		
			
			//Verificar se o className é uma das methodsAndClasses transformadas pelo Soot
			if(className.contains(packageName)){
				
				String[] list = className.split("\\."); 
				int lenght = list.length;
				String methodNameClass = list[lenght-1];
				
				String shortName = methodNameClass+"-<init>: this";
				
				InterproceduralResolver.getInstance().getMapUnitRefToObject().put(localNode,shortName);	
			}
		}
		if(rightOp instanceof JCastExpr){
			JCastExpr jcast = (JCastExpr) rightOp;
			Value value = jcast.getOpBox().getValue();
						
			Node node = graph.createNode(value, prefixFull, prefixShort, currentColor, map);
			graph.createEdge(localNode, node, false);
		}
		if(rightOp instanceof JLengthExpr){
			JLengthExpr jlength = (JLengthExpr) rightOp;
			Value value = jlength.getOpBox().getValue();
						
			Node node = graph.createNode(value, prefixFull, prefixShort, currentColor, map);
			graph.createEdge(localNode, node, false);
		}
	}

	private void includeReturnNode(Node localNode,SootMethodRef methodRef) {
		List<Node> list = InterproceduralResolver.getInstance().getMapUnitReturnMethod().get(methodRef.toString());
		if(list!=null){
			for(Node returnNode: list){
				GraphSingleton.getInstance().createEdge(localNode, returnNode, false);				
			}
		}
		else{
			InterproceduralResolver.getInstance().getMapResolveReturnMethodLater().put(localNode, methodRef.toString());
		}				
	}
}

//http://www-labs.iro.umontreal.ca/~dufour/cours/ift6315/docs/soot-tutorial.pdf
//ThrowStmt
//body.getTraps()
