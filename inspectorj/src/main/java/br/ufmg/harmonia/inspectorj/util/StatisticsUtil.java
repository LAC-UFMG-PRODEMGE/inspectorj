package br.ufmg.harmonia.inspectorj.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StatisticsUtil {
	
	protected Date timeStart;
	protected Date timeEnd;
	
	protected Set<String> methodsAndClasses = new HashSet<String>();
	protected Set<String> onlyClasses = new HashSet<String>();
	protected Map<String, Date> methodTimeStart = new HashMap<String, Date>();  
	protected Map<String, Date> methodTimeEnd = new HashMap<String, Date>();
	protected Map<String, Integer> numberOfNode = new HashMap<String, Integer>();
	protected Map<String, Integer> numberOfTaintedNode = new HashMap<String, Integer>();
	protected Map<String, Integer> numberOfCommandNode = new HashMap<String, Integer>();
	protected Map<String, Integer> numberOfPrintoutNode = new HashMap<String, Integer>();
	protected Map<String, Integer> numberOfConditionalNode = new HashMap<String, Integer>();
	protected int numberOfMethod = 0;
	
	private static StatisticsUtil  myInstance;
	
	
	private StatisticsUtil() {	
	}
	
	
	public synchronized static StatisticsUtil getInstance() {

		if(myInstance == null){
			myInstance  = new StatisticsUtil();
		}

		return myInstance;
	}


	public Date getTimeStart() {
		return timeStart;
	}


	public void setTimeStart(Date timeStart) {
		this.timeStart = timeStart;
	}


	public Date getTimeEnd() {
		return timeEnd;
	}


	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}


	public Map<String, Date> getMethodTimeStart() {
		return methodTimeStart;
	}


	public void setMethodTimeStart(Map<String, Date> methodTimeStart) {
		this.methodTimeStart = methodTimeStart;
	}


	public Map<String, Date> getMethodTimeEnd() {
		return methodTimeEnd;
	}


	public void setMethodTimeEnd(Map<String, Date> methodTimeEnd) {
		this.methodTimeEnd = methodTimeEnd;
	}


	public Map<String, Integer> getNumberOfNode() {
		return numberOfNode;
	}


	public void setNumberOfNode(Map<String, Integer> numberOfNode) {
		this.numberOfNode = numberOfNode;
	}


	public Map<String, Integer> getNumberOfTaintedNode() {
		return numberOfTaintedNode;
	}


	public void setNumberOfTaintedNode(Map<String, Integer> numberOfTaintedNode) {
		this.numberOfTaintedNode = numberOfTaintedNode;
	}


	public int getNumberOfMethod() {
		return numberOfMethod;
	}


	public void setNumberOfMethod(int numberOfMethod) {
		this.numberOfMethod = numberOfMethod;
	}


	public Set<String> getMethodsAndClasses() {
		return methodsAndClasses;
	}


	public void setMethodsAndClasses(Set<String> classes) {
		this.methodsAndClasses = classes;
	}


	public Set<String> getOnlyClasses() {
		return onlyClasses;
	}


	public void setOnlyClasses(Set<String> onlyClasses) {
		this.onlyClasses = onlyClasses;
	}


	public Map<String, Integer> getNumberOfCommandNode() {
		return numberOfCommandNode;
	}


	public void setNumberOfCommandNode(Map<String, Integer> numberOfCommandNode) {
		this.numberOfCommandNode = numberOfCommandNode;
	}


	public Map<String, Integer> getNumberOfPrintoutNode() {
		return numberOfPrintoutNode;
	}


	public void setNumberOfPrintoutNode(Map<String, Integer> numberOfPrintoutNode) {
		this.numberOfPrintoutNode = numberOfPrintoutNode;
	}


	public Map<String, Integer> getNumberOfConditionalNode() {
		return numberOfConditionalNode;
	}


	public void setNumberOfConditionalNode(
			Map<String, Integer> numberOfConditionalNode) {
		this.numberOfConditionalNode = numberOfConditionalNode;
	}
	
	
	
	
}
