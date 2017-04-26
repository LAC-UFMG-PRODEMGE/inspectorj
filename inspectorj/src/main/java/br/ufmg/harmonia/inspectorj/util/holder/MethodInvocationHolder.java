package br.ufmg.harmonia.inspectorj.util.holder;

import java.util.List;

import org.graphstream.graph.Node;

import soot.Unit;
import soot.tagkit.Tag;

public class MethodInvocationHolder {
	Unit unit;
	Node nodeMethod;
	Node nodeArgOutside;
	Node nodeParamInside;
	boolean visited;
	String key;
	
	List<Tag> tags;
	
	public List<Tag> getTags() {
		return tags;
	}
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public Node getNodeMethod() {
		return nodeMethod;
	}
	public void setNodeMethod(Node nodeMethod) {
		this.nodeMethod = nodeMethod;
	}
	public Node getNodeArgOutside() {
		return nodeArgOutside;
	}
	public void setNodeArgOutside(Node nodeArgOutside) {
		this.nodeArgOutside = nodeArgOutside;
	}
	public Node getNodeParamInside() {
		return nodeParamInside;
	}
	public void setNodeParamInside(Node nodeParamInside) {
		this.nodeParamInside = nodeParamInside;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}	

	
	
}
